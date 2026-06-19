package com.llbeauty.controller;

import com.llbeauty.entity.MerchantOrder;
import com.llbeauty.entity.Product;
import com.llbeauty.entity.User;
import com.llbeauty.entity.Payment;
import com.llbeauty.repository.ProductRepository;
import com.llbeauty.service.MerchantOrderService;
import com.llbeauty.service.PaymentService;
import com.llbeauty.service.WalletService;
import com.llbeauty.repository.UserRepository;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/merchant")
public class MerchantCartController {

    private final ProductRepository productRepository;
    private final MerchantOrderService merchantOrderService;
    private final WalletService walletService;
    private final PaymentService paymentService;
    private final UserRepository userRepository;

    @Value("${razorpay.key.id:rzp_test_dummy}")
    private String razorpayKeyId;

    public MerchantCartController(ProductRepository productRepository,
                                  MerchantOrderService merchantOrderService,
                                  WalletService walletService,
                                  PaymentService paymentService,
                                  UserRepository userRepository) {
        this.productRepository = productRepository;
        this.merchantOrderService = merchantOrderService;
        this.walletService = walletService;
        this.paymentService = paymentService;
        this.userRepository = userRepository;
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
        Map<Long, Integer> cart = (Map<Long, Integer>) session.getAttribute("EVA_MERCHANT_CART");
        if (cart == null) {
            cart = new HashMap<>();
            session.setAttribute("EVA_MERCHANT_CART", cart);
        }
        return cart;
    }

    @PostMapping("/cart/add-bulk")
    public String addBulkToCart(@RequestParam Map<String, String> allParams, HttpSession session) {
        User user = getAuthenticatedUser();
        if (user == null || !"ROLE_MERCHANT".equals(user.getRole())) {
            return "redirect:/auth/login";
        }

        Map<Long, Integer> cart = getCartFromSession(session);
        cart.clear(); // Clear old items for merchant bulk purchase flow

        for (Map.Entry<String, String> entry : allParams.entrySet()) {
            if (entry.getKey().startsWith("product_")) {
                try {
                    Long productId = Long.parseLong(entry.getKey().replace("product_", ""));
                    int quantity = Integer.parseInt(entry.getValue());
                    if (quantity > 0) {
                        cart.put(productId, quantity);
                    }
                } catch (NumberFormatException ignored) {}
            }
        }

        return "redirect:/merchant/checkout";
    }

    @GetMapping("/checkout")
    public String checkoutPage(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        User user = getAuthenticatedUser();
        if (user == null || !"ROLE_MERCHANT".equals(user.getRole())) {
            return "redirect:/auth/login?redirect=/merchant/checkout";
        }

        Map<Long, Integer> cart = getCartFromSession(session);
        if (cart.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Your cart is empty. Please select products first.");
            return "redirect:/merchant/wholesale";
        }

        List<Map<String, Object>> checkoutItems = new ArrayList<>();
        double subtotal = 0.0;
        double productDiscounts = 0.0;
        double bulkDiscounts = 0.0;
        double totalSavings = 0.0;
        double finalAmount = 0.0;

        for (Map.Entry<Long, Integer> entry : cart.entrySet()) {
            Product product = productRepository.findById(entry.getKey()).orElse(null);
            if (product != null) {
                int qty = entry.getValue();
                double mrp = product.getPrice();
                double merchantDiscPercent = product.getMerchantDiscount() != null ? product.getMerchantDiscount() : 0.0;
                double merchantDiscAmount = mrp * (merchantDiscPercent / 100.0);
                double merchantPrice = mrp - merchantDiscAmount;

                double bulkDiscPercent = 0.0;
                if (qty >= 11 && qty <= 50) {
                    bulkDiscPercent = 5.0;
                } else if (qty >= 51) {
                    bulkDiscPercent = 10.0;
                }

                double bulkDiscAmount = mrp * (bulkDiscPercent / 100.0);
                double finalPrice = mrp - merchantDiscAmount - bulkDiscAmount;

                double itemSubtotal = mrp * qty;
                double itemProductDisc = merchantDiscAmount * qty;
                double itemBulkDisc = bulkDiscAmount * qty;
                double itemSavings = itemProductDisc + itemBulkDisc;
                double itemFinal = finalPrice * qty;

                subtotal += itemSubtotal;
                productDiscounts += itemProductDisc;
                bulkDiscounts += itemBulkDisc;
                totalSavings += itemSavings;
                finalAmount += itemFinal;

                Map<String, Object> item = new HashMap<>();
                item.put("product", product);
                item.put("quantity", qty);
                item.put("mrp", mrp);
                item.put("merchantDiscountPercent", merchantDiscPercent);
                item.put("merchantPrice", merchantPrice);
                item.put("bulkDiscountPercent", bulkDiscPercent);
                item.put("bulkPrice", finalPrice);
                item.put("savings", itemSavings);
                item.put("total", itemFinal);

                checkoutItems.add(item);
            }
        }

        BigDecimal walletBalance = walletService.getBalance(user);
        double walletUsed = Math.min(walletBalance.doubleValue(), finalAmount);
        double remainingAmount = finalAmount - walletUsed;

        model.addAttribute("items", checkoutItems);
        model.addAttribute("subtotal", subtotal);
        model.addAttribute("productDiscounts", productDiscounts);
        model.addAttribute("bulkDiscounts", bulkDiscounts);
        model.addAttribute("totalSavings", totalSavings);
        model.addAttribute("finalAmount", finalAmount);
        model.addAttribute("walletBalance", walletBalance);
        model.addAttribute("walletUsed", walletUsed);
        model.addAttribute("remainingAmount", remainingAmount);
        model.addAttribute("razorpayKeyId", razorpayKeyId);

        return "merchant/checkout";
    }

    @PostMapping("/checkout/place-order")
    @ResponseBody
    public ResponseEntity<?> placeOrder(@RequestParam("useWallet") boolean useWallet, HttpSession session) {
        User user = getAuthenticatedUser();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Login required"));
        }

        Map<Long, Integer> cart = getCartFromSession(session);
        if (cart.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Cart is empty"));
        }

        try {
            MerchantOrder order = merchantOrderService.initiateOrder(user, cart, useWallet);

            Map<String, Object> response = new HashMap<>();
            response.put("orderId", order.getId());
            response.put("finalAmount", order.getFinalAmount());
            response.put("walletAmountUsed", order.getWalletAmountUsed());

            double remaining = order.getFinalAmount() - order.getWalletAmountUsed();
            response.put("amountToPay", remaining);

            if (remaining <= 0) {
                // Fully paid via wallet credits
                session.removeAttribute("EVA_MERCHANT_CART");
                response.put("status", "success");
                response.put("redirectUrl", "/merchant/orders?success=true");
                return ResponseEntity.ok(response);
            }

            // Create Razorpay Order
            if (!isDummyCredentials()) {
                try {
                    Payment payment = paymentService.initiatePayment(user, remaining, "MERCHANT_PRODUCT", 
                        String.valueOf(order.getId()), "RAZORPAY" + (order.getWalletAmountUsed() > 0 ? "+WALLET" : ""));
                    
                    order.setRazorpayOrderId(payment.getRazorpayOrderId());
                    merchantOrderService.initiateOrder(user, cart, useWallet); // Save updated details

                    response.put("razorpayOrderId", payment.getRazorpayOrderId());
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

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/checkout/confirm")
    @ResponseBody
    public ResponseEntity<?> confirmPayment(@RequestParam("orderId") Long orderId,
                                            @RequestParam("paymentId") String paymentId,
                                            @RequestParam(value = "razorpayOrderId", required = false) String razorpayOrderId,
                                            @RequestParam(value = "razorpaySignature", required = false) String razorpaySignature,
                                            HttpSession session) {
        User user = getAuthenticatedUser();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Login required"));
        }

        try {
            // Verify signature for real payments
            if (!isDummyCredentials() && razorpayOrderId != null && razorpaySignature != null) {
                try {
                    paymentService.verifyAndProcessPayment(razorpayOrderId, paymentId, razorpaySignature);
                } catch (Exception e) {
                    merchantOrderService.rollbackOrder(orderId);
                    return ResponseEntity.badRequest().body(Map.of("message", "Payment signature verification failed. Wallet rolled back."));
                }
            }

            merchantOrderService.confirmOrder(orderId, paymentId, razorpaySignature, razorpayOrderId);
            session.removeAttribute("EVA_MERCHANT_CART");

            return ResponseEntity.ok(Map.of("status", "success", "redirectUrl", "/merchant/orders?success=true"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/checkout/rollback")
    @ResponseBody
    public ResponseEntity<?> rollbackOrder(@RequestParam("orderId") Long orderId) {
        User user = getAuthenticatedUser();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Login required"));
        }

        try {
            merchantOrderService.rollbackOrder(orderId);
            return ResponseEntity.ok(Map.of("status", "success"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    private boolean isDummyCredentials() {
        return "rzp_test_dummy".equals(razorpayKeyId) || razorpayKeyId == null || razorpayKeyId.trim().isEmpty();
    }
}
