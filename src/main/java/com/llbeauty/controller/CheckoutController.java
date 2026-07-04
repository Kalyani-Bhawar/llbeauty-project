package com.llbeauty.controller;

import com.llbeauty.entity.Order;
import com.llbeauty.entity.OrderStatusHistory;
import com.llbeauty.entity.Product;
import com.llbeauty.entity.User;
import com.llbeauty.repository.OrderRepository;
import com.llbeauty.repository.OrderStatusHistoryRepository;
import com.llbeauty.repository.ProductRepository;
import com.llbeauty.repository.UserRepository;
import com.llbeauty.service.CheckoutService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/checkout")
public class CheckoutController {

    private final CheckoutService checkoutService;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderStatusHistoryRepository orderStatusHistoryRepository;

    @Value("${razorpay.key.id:rzp_test_dummy}")
    private String razorpayKeyId;

    public CheckoutController(CheckoutService checkoutService, UserRepository userRepository,
                              ProductRepository productRepository, OrderRepository orderRepository,
                              OrderStatusHistoryRepository orderStatusHistoryRepository) {
        this.checkoutService = checkoutService;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.orderStatusHistoryRepository = orderStatusHistoryRepository;
    }

    private User getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            return userRepository.findByEmail(auth.getName()).orElse(null);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private Map<Long, Integer> getCartFromSession(HttpSession session) {
        Map<Long, Integer> cart = (Map<Long, Integer>) session.getAttribute("LLB_CART");
        if (cart == null) {
            cart = new HashMap<>();
            session.setAttribute("LLB_CART", cart);
        }
        return cart;
    }

    @PostMapping("/cart/add")
    @ResponseBody
    public ResponseEntity<?> addToCart(@RequestParam("productId") Long productId,
                                       @RequestParam(value = "quantity", defaultValue = "1") Integer quantity,
                                       HttpSession session) {
        User user = getAuthenticatedUser();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "auth"));
        }

        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "not_found"));
        }

        Map<Long, Integer> cart = getCartFromSession(session);
        cart.put(productId, cart.getOrDefault(productId, 0) + quantity);

        return ResponseEntity.ok(Map.of(
            "status", "success",
            "cartSize", cart.values().stream().mapToInt(Integer::intValue).sum()
        ));
    }

    @GetMapping("/cart/items")
    @ResponseBody
    public ResponseEntity<?> getCartItems(HttpSession session) {
        return ResponseEntity.ok(checkoutService.getCartItemsDetails(getCartFromSession(session)));
    }

    @PostMapping("/cart/update")
    @ResponseBody
    public ResponseEntity<?> updateCartItem(@RequestParam("productId") Long productId,
                                            @RequestParam("quantity") Integer quantity,
                                            HttpSession session) {
        Map<Long, Integer> cart = getCartFromSession(session);
        if (quantity <= 0) {
            cart.remove(productId);
        } else {
            cart.put(productId, quantity);
        }
        return getCartItems(session);
    }

    @PostMapping("/cart/clear")
    @ResponseBody
    public ResponseEntity<?> clearCart(HttpSession session) {
        session.removeAttribute("LLB_CART");
        return ResponseEntity.ok(Map.of(
            "status", "success",
            "cartSize", 0,
            "subtotal", 0.0,
            "items", new ArrayList<>()
        ));
    }

    @GetMapping
    public String checkoutPage(Model model, HttpSession session,
                               @RequestParam(value = "productId", required = false) Long productId,
                               RedirectAttributes redirectAttributes) {
        User user = getAuthenticatedUser();
        if (user == null) {
            return "redirect:/auth/login?redirect=/checkout";
        }

        if (productId == null) {
            Map<Long, Integer> cart = getCartFromSession(session);
            if (cart.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Your cart is empty. Add products to cart first.");
                return "redirect:/shop";
            }
        }

        Map<String, Object> details = checkoutService.getCheckoutPageDetails(user, productId, getCartFromSession(session));
        
        if (productId != null) {
            model.addAttribute("directProductId", productId);
        }

        model.addAttribute("items", details.get("items"));
        model.addAttribute("subtotal", details.get("subtotal"));
        model.addAttribute("discountPercent", details.get("discountPercent"));
        model.addAttribute("discountAmount", details.get("discountAmount"));
        model.addAttribute("gstAmount", details.get("gstAmount"));
        model.addAttribute("finalAmount", details.get("finalAmount"));
        model.addAttribute("passName", details.get("passName"));
        model.addAttribute("walletBalance", details.get("walletBalance"));
        model.addAttribute("razorpayKeyId", razorpayKeyId);

        return "checkout";
    }

    @PostMapping("/place-order")
    @ResponseBody
    public ResponseEntity<?> placeOrder(@RequestParam(value = "directProductId", required = false) Long directProductId,
                                        @RequestParam("useWallet") boolean useWallet,
                                        @RequestParam(value = "useNxl", defaultValue = "false") boolean useNxl,
                                        @RequestParam(value = "referralCode", required = false) String referralCode,
                                        @RequestParam(value = "shippingAddress", required = false) String shippingAddress,
                                        @RequestParam(value = "billingName", required = false) String billingName,
                                        @RequestParam(value = "billingMobile", required = false) String billingMobile,
                                        HttpSession session) {
        User user = getAuthenticatedUser();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Login required"));
        }

        try {
            Map<String, Object> response = checkoutService.placeOrder(user, directProductId, useWallet, useNxl,
                    referralCode, getCartFromSession(session), razorpayKeyId,
                    shippingAddress, billingName, billingMobile);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/orders/{id}/track")
    public String trackOrder(@PathVariable("id") Long orderId, Model model) {
        User user = getAuthenticatedUser();
        if (user == null) {
            return "redirect:/auth/login?redirect=/checkout/orders/" + orderId + "/track";
        }
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null || !order.getUser().getId().equals(user.getId())) {
            return "redirect:/dashboard";
        }
        java.util.List<OrderStatusHistory> statusHistory = orderStatusHistoryRepository.findByOrderIdOrderByUpdatedAtAsc(orderId);
        model.addAttribute("order", order);
        model.addAttribute("statusHistory", statusHistory);
        return "track_order";
    }

    @PostMapping("/confirm-order")
    @ResponseBody
    public ResponseEntity<?> confirmOrderPayment(
            @RequestParam("orderId") Long orderId,
            @RequestParam("paymentId") String paymentId,
            @RequestParam(value = "razorpayOrderId", required = false) String razorpayOrderId,
            @RequestParam(value = "razorpaySignature", required = false) String razorpaySignature,
            @RequestParam(value = "useWallet", defaultValue = "false") boolean useWallet,
            @RequestParam(value = "walletRedeemed", defaultValue = "0") double walletRedeemed,
            @RequestParam(value = "directProductId", required = false) Long directProductId,
            HttpSession session) {

        User user = getAuthenticatedUser();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Login required"));
        }

        try {
            Map<String, Object> response = checkoutService.confirmOrderPayment(
                    orderId, paymentId, razorpayOrderId, razorpaySignature, useWallet, walletRedeemed, directProductId != null, user, razorpayKeyId, getCartFromSession(session)
            );
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/success")
    public String orderSuccessPage(@RequestParam("orderId") Long orderId, Model model) {
        User user = getAuthenticatedUser();
        if (user == null) {
            return "redirect:/auth/login";
        }

        Order order = checkoutService.getOrderSuccessDetails(orderId, user);
        if (order == null) {
            return "redirect:/";
        }

        model.addAttribute("order", order);
        model.addAttribute("cashbackEarned", checkoutService.getCashbackEarned(order, user));
        return "checkout_success";
    }
}
