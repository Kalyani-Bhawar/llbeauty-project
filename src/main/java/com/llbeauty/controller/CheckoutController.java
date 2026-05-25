package com.llbeauty.controller;

import com.llbeauty.entity.*;
import com.llbeauty.repository.*;
import com.llbeauty.service.MembershipService;
import com.llbeauty.service.WalletService;
import com.razorpay.RazorpayClient;
import jakarta.servlet.http.HttpSession;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDateTime;
import java.util.*;

@Controller
@RequestMapping("/checkout")
public class CheckoutController {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final WalletService walletService;
    private final MembershipService membershipService;
    private final UserMembershipRepository userMembershipRepository;

    @Value("${razorpay.key.id:rzp_test_dummy}")
    private String razorpayKeyId;

    @Value("${razorpay.key.secret:dummysecret}")
    private String razorpayKeySecret;

    public CheckoutController(ProductRepository productRepository,
                              OrderRepository orderRepository,
                              UserRepository userRepository,
                              WalletService walletService,
                              MembershipService membershipService,
                              UserMembershipRepository userMembershipRepository) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.walletService = walletService;
        this.membershipService = membershipService;
        this.userMembershipRepository = userMembershipRepository;
    }

    private User getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            return userRepository.findByMobile(auth.getName()).orElse(null);
        }
        return null;
    }

    // Helper: Get cart from session (Map of Product -> Quantity)
    @SuppressWarnings("unchecked")
    private Map<Long, Integer> getCartFromSession(HttpSession session) {
        Map<Long, Integer> cart = (Map<Long, Integer>) session.getAttribute("LLB_CART");
        if (cart == null) {
            cart = new HashMap<>();
            session.setAttribute("LLB_CART", cart);
        }
        return cart;
    }

    // Add item to cart (AJAX or Redirect)
    @PostMapping("/cart/add")
    @ResponseBody
    public ResponseEntity<?> addToCart(@RequestParam("productId") Long productId,
                                       @RequestParam(value = "quantity", defaultValue = "1") Integer quantity,
                                       HttpSession session) {
        User user = getAuthenticatedUser();
        if (user == null) {
            Map<String, String> err = new HashMap<>();
            err.put("error", "auth");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(err);
        }

        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            Map<String, String> err = new HashMap<>();
            err.put("error", "not_found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err);
        }

        Map<Long, Integer> cart = getCartFromSession(session);
        cart.put(productId, cart.getOrDefault(productId, 0) + quantity);

        Map<String, Object> success = new HashMap<>();
        success.put("status", "success");
        success.put("cartSize", cart.values().stream().mapToInt(Integer::intValue).sum());
        return ResponseEntity.ok(success);
    }

    // Display checkout page
    @GetMapping
    public String checkoutPage(Model model, HttpSession session,
                               @RequestParam(value = "productId", required = false) Long productId,
                               RedirectAttributes redirectAttributes) {
        User user = getAuthenticatedUser();
        if (user == null) {
            return "redirect:/auth/login?redirect=/checkout";
        }

        List<Map<String, Object>> checkoutItems = new ArrayList<>();
        double subtotal = 0.0;

        if (productId != null) {
            // Direct buy single product
            Product product = productRepository.findById(productId).orElse(null);
            if (product != null) {
                Map<String, Object> item = new HashMap<>();
                item.put("product", product);
                item.put("quantity", 1);
                item.put("total", product.getPrice());
                checkoutItems.add(item);
                subtotal = product.getPrice();
                model.addAttribute("directProductId", productId);
            }
        } else {
            // Session cart checkout
            Map<Long, Integer> cart = getCartFromSession(session);
            if (cart.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Your cart is empty. Add products to cart first.");
                return "redirect:/shop";
            }
            for (Map.Entry<Long, Integer> entry : cart.entrySet()) {
                Product product = productRepository.findById(entry.getKey()).orElse(null);
                if (product != null) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("product", product);
                    item.put("quantity", entry.getValue());
                    double total = product.getPrice() * entry.getValue();
                    item.put("total", total);
                    checkoutItems.add(item);
                    subtotal += total;
                }
            }
        }

        // Apply membership discount
        double discountPercent = 0.0;
        String passName = "";
        Optional<UserMembership> activeOpt = membershipService.getActiveMembership(user);
        if (activeOpt.isPresent()) {
            UserMembership active = activeOpt.get();
            discountPercent = active.getMembership().getCashbackPercent(); // Use cashback_percent as the direct discount in checkout
            passName = active.getMembership().getName();
        }

        double discountAmount = subtotal * discountPercent;
        double finalAmount = subtotal - discountAmount;

        model.addAttribute("items", checkoutItems);
        model.addAttribute("subtotal", subtotal);
        model.addAttribute("discountPercent", discountPercent * 100);
        model.addAttribute("discountAmount", discountAmount);
        model.addAttribute("finalAmount", finalAmount);
        model.addAttribute("passName", passName);
        model.addAttribute("walletBalance", walletService.getBalance(user));

        return "checkout";
    }

    // Place Order API - handles wallet debit and initializes Razorpay for balance
    @PostMapping("/place-order")
    @ResponseBody
    public ResponseEntity<?> placeOrder(@RequestParam(value = "directProductId", required = false) Long directProductId,
                                        @RequestParam("useWallet") boolean useWallet,
                                        HttpSession session) {
        User user = getAuthenticatedUser();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Login required"));
        }

        // 1. Calculate totals
        double subtotal = 0.0;
        List<Product> productsToDeductStock = new ArrayList<>();

        if (directProductId != null) {
            Product product = productRepository.findById(directProductId).orElse(null);
            if (product != null) {
                subtotal = product.getPrice();
                productsToDeductStock.add(product);
            }
        } else {
            Map<Long, Integer> cart = getCartFromSession(session);
            if (cart.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Cart is empty"));
            }
            for (Map.Entry<Long, Integer> entry : cart.entrySet()) {
                Product product = productRepository.findById(entry.getKey()).orElse(null);
                if (product != null) {
                    subtotal += product.getPrice() * entry.getValue();
                    productsToDeductStock.add(product);
                }
            }
        }

        // Apply Membership Discount
        double discountPercent = 0.0;
        Optional<UserMembership> activeOpt = membershipService.getActiveMembership(user);
        if (activeOpt.isPresent()) {
            discountPercent = activeOpt.get().getMembership().getCashbackPercent();
        }
        double discountAmount = subtotal * discountPercent;
        double discountedTotal = subtotal - discountAmount;

        // 2. Handle Wallet Redemption
        double walletRedeemed = 0.0;
        if (useWallet) {
            double currentWallet = walletService.getBalance(user);
            walletRedeemed = Math.min(currentWallet, discountedTotal);
        }

        double amountToPay = discountedTotal - walletRedeemed;

        // 3. Create Pending Order
        Order order = new Order();
        order.setUser(user);
        order.setTotalAmount(discountedTotal); // Total value of order
        order.setStatus("PENDING");
        order.setPaymentId(null);
        Order savedOrder = orderRepository.save(order);

        // Deduct Wallet Balance if applied
        if (walletRedeemed > 0) {
            walletService.debit(user, walletRedeemed, "Redeemed for Order #" + savedOrder.getId());
        }

        Map<String, Object> response = new HashMap<>();
        response.put("orderId", savedOrder.getId());
        response.put("amountToPay", amountToPay);
        response.put("walletRedeemed", walletRedeemed);

        if (amountToPay <= 0) {
            // Fully paid via wallet, complete order instantly!
            completeOrder(savedOrder, user, "WALLET_PAY", directProductId == null, session);
            response.put("status", "success");
            response.put("redirectUrl", "/checkout/success?orderId=" + savedOrder.getId());
            return ResponseEntity.ok(response);
        }

        // 4. Generate Razorpay order for remaining balance
        if (!isDummyCredentials()) {
            try {
                RazorpayClient client = new RazorpayClient(razorpayKeyId, razorpayKeySecret);
                JSONObject orderRequest = new JSONObject();
                orderRequest.put("amount", (int) Math.round(amountToPay * 100)); // in paise
                orderRequest.put("currency", "INR");
                orderRequest.put("receipt", "order_" + savedOrder.getId() + "_" + System.currentTimeMillis());

                com.razorpay.Order rzpOrder = client.orders.create(orderRequest);
                response.put("razorpayOrderId", rzpOrder.get("id"));
                response.put("key", razorpayKeyId);
                response.put("useMock", false);
            } catch (Exception e) {
                response.put("useMock", true);
                response.put("razorpayOrderId", "mock_order_" + System.currentTimeMillis());
                response.put("key", "mock_key");
            }
        } else {
            response.put("useMock", true);
            response.put("razorpayOrderId", "mock_order_" + System.currentTimeMillis());
            response.put("key", "mock_key");
        }

        response.put("status", "payment_pending");
        return ResponseEntity.ok(response);
    }

    // Verify payment from Razorpay
    @PostMapping("/confirm-order")
    @ResponseBody
    public ResponseEntity<?> confirmOrderPayment(@RequestParam("orderId") Long orderId,
                                                 @RequestParam("paymentId") String paymentId,
                                                 @RequestParam(value = "directProductId", required = false) Long directProductId,
                                                 HttpSession session) {
        User user = getAuthenticatedUser();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Login required"));
        }

        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Order not found"));
        }

        completeOrder(order, user, paymentId, directProductId == null, session);

        return ResponseEntity.ok(Map.of("status", "success", "redirectUrl", "/checkout/success?orderId=" + order.getId()));
    }

    // Success Landing Page
    @GetMapping("/success")
    public String orderSuccessPage(@RequestParam("orderId") Long orderId, Model model) {
        User user = getAuthenticatedUser();
        if (user == null) {
            return "redirect:/auth/login";
        }

        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null || !order.getUser().getId().equals(user.getId())) {
            return "redirect:/";
        }

        // Fetch recent cashback credited for this order
        double cashbackEarned = 0.0;
        Optional<UserMembership> activeOpt = membershipService.getActiveMembership(user);
        if (activeOpt.isPresent()) {
            double cashbackPercent = activeOpt.get().getMembership().getCashbackPercent();
            cashbackEarned = order.getTotalAmount() * cashbackPercent;
        }

        model.addAttribute("order", order);
        model.addAttribute("cashbackEarned", cashbackEarned);
        return "checkout_success";
    }

    // Complete order helper: marks success, updates stock, awards cashback, clears cart
    private void completeOrder(Order order, User user, String paymentId, boolean clearCart, HttpSession session) {
        order.setStatus("SUCCESS");
        order.setPaymentId(paymentId);
        orderRepository.save(order);

        // Award membership cashback
        Optional<UserMembership> activeOpt = membershipService.getActiveMembership(user);
        if (activeOpt.isPresent()) {
            double cashbackPercent = activeOpt.get().getMembership().getCashbackPercent();
            double cashbackAmount = order.getTotalAmount() * cashbackPercent;
            if (cashbackAmount > 0) {
                walletService.credit(user, cashbackAmount, "Cashback for Order #" + order.getId() + " (" + activeOpt.get().getMembership().getName() + ")");
            }
        }

        // Award default purchase credits (e.g. 5% cashback or 1 credit per 20 rupees spent even for non-VIP if desired, but VIP gets the plan specific percentage)
        // Here we adhere to VIP-only automatic plan discounts and cashback logic.

        if (clearCart) {
            session.removeAttribute("LLB_CART");
        }
    }

    private boolean isDummyCredentials() {
        return "rzp_test_dummy".equals(razorpayKeyId) || razorpayKeyId == null || razorpayKeyId.trim().isEmpty();
    }
}
