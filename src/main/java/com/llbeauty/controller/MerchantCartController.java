package com.llbeauty.controller;

import com.llbeauty.dto.MerchantCartItemDTO;
import com.llbeauty.entity.MerchantOrder;
import com.llbeauty.entity.Payment;
import com.llbeauty.entity.User;
import com.llbeauty.repository.UserRepository;
import com.llbeauty.repository.ProductRepository;
import com.llbeauty.service.MerchantCartService;
import com.llbeauty.service.MerchantOrderService;
import com.llbeauty.service.PaymentService;
import com.llbeauty.service.WalletService;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MerchantCartController — Thin controller layer for the merchant wholesale cart & checkout flow.
 *
 * Architecture rules enforced:
 *  1. NO Map<String,Object> for cart items — always uses MerchantCartItemDTO
 *  2. NO price calculations — all delegated to MerchantCartService
 *  3. NO order creation logic — all delegated to MerchantOrderService
 *  4. SESSION only stores: Map<Long (productId), Integer (quantity)> under key "EVA_MERCHANT_CART"
 *  5. Controller = routing + model population only
 */
@Controller
@RequestMapping("/merchant")
public class MerchantCartController {

    private final ProductRepository productRepository;
    private final MerchantCartService merchantCartService;
    private final MerchantOrderService merchantOrderService;
    private final WalletService walletService;
    private final PaymentService paymentService;
    private final UserRepository userRepository;

    @Value("${razorpay.key.id:rzp_test_dummy}")
    private String razorpayKeyId;

    public MerchantCartController(ProductRepository productRepository,
                                  MerchantCartService merchantCartService,
                                  MerchantOrderService merchantOrderService,
                                  WalletService walletService,
                                  PaymentService paymentService,
                                  UserRepository userRepository) {
        this.productRepository = productRepository;
        this.merchantCartService = merchantCartService;
        this.merchantOrderService = merchantOrderService;
        this.walletService = walletService;
        this.paymentService = paymentService;
        this.userRepository = userRepository;
    }

    // ──────────────────────────────────────────────────────────────────
    // Auth helper
    // ──────────────────────────────────────────────────────────────────

    private User getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            return userRepository.findByEmail(auth.getName()).orElse(null);
        }
        return null;
    }

    // ──────────────────────────────────────────────────────────────────
    // Session cart helper — stores ONLY Map<productId, quantity>
    // ──────────────────────────────────────────────────────────────────

    @SuppressWarnings("unchecked")
    private Map<Long, Integer> getCartFromSession(HttpSession session) {
        Map<Long, Integer> cart = (Map<Long, Integer>) session.getAttribute("EVA_MERCHANT_CART");
        if (cart == null) {
            cart = new HashMap<>();
            session.setAttribute("EVA_MERCHANT_CART", cart);
        }
        return cart;
    }

    // ──────────────────────────────────────────────────────────────────
    // Add bulk from wholesale catalog (form POST)
    // ──────────────────────────────────────────────────────────────────

    /**
     * Accepts a form submission from wholesale.html with fields named "product_{id}".
     * Replaces the entire cart (bulk purchase flow — merchant picks a new batch each time).
     */
    @PostMapping("/cart/add-bulk")
    public String addBulkToCart(@RequestParam Map<String, String> allParams,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        User user = getAuthenticatedUser();
        if (user == null) return "redirect:/auth/login";
        if (!"MERCHANT".equalsIgnoreCase(user.getRole())) return "redirect:/merchant/dashboard";

        Map<Long, Integer> cart = getCartFromSession(session);
        cart.clear(); // Replace cart with new selection

        int itemsAdded = 0;
        for (Map.Entry<String, String> entry : allParams.entrySet()) {
            if (entry.getKey().startsWith("product_")) {
                try {
                    Long productId = Long.parseLong(entry.getKey().replace("product_", ""));
                    int quantity = Integer.parseInt(entry.getValue());
                    if (quantity > 0) {
                        cart.put(productId, quantity);
                        itemsAdded++;
                    }
                } catch (NumberFormatException ignored) {
                    // Skip malformed params silently
                }
            }
        }

        if (itemsAdded == 0) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please select at least one product with a quantity greater than 0.");
            return "redirect:/merchant/wholesale";
        }

        return "redirect:/merchant/cart";
    }

    // ──────────────────────────────────────────────────────────────────
    // Cart page
    // ──────────────────────────────────────────────────────────────────

    /**
     * Display the merchant cart. Uses MerchantCartItemDTO via MerchantCartService — no Map<String,Object>.
     */
    @GetMapping("/cart")
    public String cartPage(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        User user = getAuthenticatedUser();
        if (user == null) return "redirect:/auth/login?redirect=/merchant/cart";
        if (!"MERCHANT".equalsIgnoreCase(user.getRole())) return "redirect:/merchant/dashboard";

        Map<Long, Integer> cart = getCartFromSession(session);
        if (cart.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Your cart is empty. Please select products first.");
            return "redirect:/merchant/wholesale";
        }

        // Delegate ALL calculation to service — no price logic in controller
        List<MerchantCartItemDTO> cartItems = merchantCartService.buildCartItems(cart);
        double grandTotal = merchantCartService.calculateGrandTotal(cartItems);

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("grandTotal", grandTotal);
        return "merchant/cart";
    }

    // ──────────────────────────────────────────────────────────────────
    // Checkout page
    // ──────────────────────────────────────────────────────────────────

    /**
     * Display the merchant checkout page. All model attributes exactly match merchant_checkout.html fields.
     *
     * Template expects:
     *  ${items}             — List<MerchantCartItemDTO>
     *  item.product.name    — Product name
     *  item.mrp             — MRP
     *  item.merchantDiscountPercent — Discount %
     *  item.bulkPrice       — Final unit price (alias for finalPrice via getBulkPrice())
     *  item.quantity        — Qty
     *  item.total           — Line total
     *  ${subtotal}          — MRP subtotal
     *  ${productDiscounts}  — Total merchant + bulk discounts
     *  ${finalAmount}       — Amount after all discounts
     *  ${walletBalance}     — NXL wallet balance
     *  ${walletUsed}        — Pre-calculated wallet deduction (initial display)
     *  ${remainingAmount}   — finalAmount - walletUsed (initial display)
     *  ${razorpayKeyId}     — Razorpay key for JS
     */
    @GetMapping("/checkout")
    public String checkoutPage(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        User user = getAuthenticatedUser();
        if (user == null) return "redirect:/auth/login?redirect=/merchant/checkout";
        if (!"MERCHANT".equalsIgnoreCase(user.getRole())) return "redirect:/merchant/dashboard";

        Map<Long, Integer> cart = getCartFromSession(session);
        if (cart.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Cart is empty.");
            return "redirect:/merchant/wholesale";
        }

        // Delegate ALL calculations to service
        List<MerchantCartItemDTO> items = merchantCartService.buildCartItems(cart);
        double subtotal = merchantCartService.calculateSubtotal(items);
        double productDiscounts = merchantCartService.calculateProductDiscounts(items)
                + merchantCartService.calculateBulkDiscounts(items);
        double finalAmount = merchantCartService.calculateGrandTotal(items);

        // Wallet balance for initial display
        BigDecimal walletBal = walletService.getBalance(user);
        double walletUsed = Math.min(walletBal.doubleValue(), finalAmount);
        double remainingAmount = finalAmount - walletUsed;

        model.addAttribute("items", items);
        model.addAttribute("subtotal", subtotal);
        model.addAttribute("productDiscounts", productDiscounts);
        model.addAttribute("finalAmount", finalAmount);
        model.addAttribute("walletBalance", walletBal);
        model.addAttribute("walletUsed", walletUsed);
        model.addAttribute("remainingAmount", remainingAmount);
        model.addAttribute("razorpayKeyId", razorpayKeyId);

        return "merchant_checkout";
    }

    // ──────────────────────────────────────────────────────────────────
    // Place Order (AJAX)
    // ──────────────────────────────────────────────────────────────────

    /**
     * Initiates a merchant order. Delegates ALL business logic to MerchantOrderService.
     * Returns JSON for the checkout page JS to handle wallet-only vs Razorpay flow.
     *
     * Response schema:
     *  success (wallet-only):   { status:"success", redirectUrl:"/merchant/orders?success=true" }
     *  success (razorpay):      { status:"payment_pending", razorpayOrderId, key, amount, orderId, useMock }
     *  error:                   { message:"..." }
     */
    @PostMapping("/checkout/place-order")
    @ResponseBody
    public ResponseEntity<?> placeOrder(@RequestParam("useWallet") boolean useWallet,
                                         HttpSession session) {
        User user = getAuthenticatedUser();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Login required"));
        }

        Map<Long, Integer> cart = getCartFromSession(session);
        if (cart.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Cart is empty"));
        }

        try {
            // Service handles all: order creation, wallet deduction, partial payment logic
            MerchantOrder order = merchantOrderService.initiateOrder(user, cart, useWallet);

            Map<String, Object> response = new HashMap<>();
            response.put("orderId", order.getId());
            response.put("finalAmount", order.getFinalAmount());
            response.put("walletAmountUsed", order.getWalletAmountUsed());

            double remaining = order.getFinalAmount() - order.getWalletAmountUsed();
            response.put("amountToPay", remaining);

            if (remaining <= 0) {
                // Fully paid via NXL Wallet credits — clear cart and redirect
                session.removeAttribute("EVA_MERCHANT_CART");
                response.put("status", "success");
                response.put("redirectUrl", "/merchant/orders?success=true");
                return ResponseEntity.ok(response);
            }

            // Needs Razorpay for remaining amount
            if (!isDummyCredentials()) {
                try {
                    Payment payment = paymentService.initiatePayment(
                            user, remaining, "MERCHANT_PRODUCT",
                            String.valueOf(order.getId()),
                            "RAZORPAY" + (order.getWalletAmountUsed() > 0 ? "+WALLET" : ""));

                    response.put("razorpayOrderId", payment.getRazorpayOrderId());
                    response.put("key", razorpayKeyId);
                    response.put("useMock", false);
                } catch (Exception e) {
                    // Razorpay unavailable — fall through to mock flow
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

    // ──────────────────────────────────────────────────────────────────
    // Confirm Payment (AJAX — called after Razorpay handler fires)
    // ──────────────────────────────────────────────────────────────────

    /**
     * Confirms payment after Razorpay callback. Verifies signature, confirms order, clears cart.
     *
     * Response schema:
     *  success: { status:"success", redirectUrl:"/merchant/orders?success=true" }
     *  error:   { message:"..." }
     */
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
            // Verify Razorpay signature for real (non-mock) payments
            if (!isDummyCredentials() && razorpayOrderId != null && razorpaySignature != null) {
                try {
                    paymentService.verifyAndProcessPayment(razorpayOrderId, paymentId, razorpaySignature);
                } catch (Exception e) {
                    // Signature invalid — rollback wallet reservation
                    merchantOrderService.rollbackOrder(orderId);
                    return ResponseEntity.badRequest().body(
                            Map.of("message", "Payment signature verification failed. Wallet credits restored."));
                }
            }

            // Service handles: order status → SUCCESS, razorpay fields saved, stock updated, invoice generated
            merchantOrderService.confirmOrder(orderId, paymentId, razorpaySignature, razorpayOrderId);
            session.removeAttribute("EVA_MERCHANT_CART");

            return ResponseEntity.ok(Map.of("status", "success", "redirectUrl", "/merchant/orders?success=true"));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // ──────────────────────────────────────────────────────────────────
    // Rollback — called when Razorpay modal is dismissed
    // ──────────────────────────────────────────────────────────────────

    /**
     * Rolls back a PENDING order (restores wallet credits if debited).
     * Called from the Razorpay modal's ondismiss handler.
     */
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

    // ──────────────────────────────────────────────────────────────────
    // Helpers
    // ──────────────────────────────────────────────────────────────────

    /** Returns true when running in test/local mode without real Razorpay credentials. */
    private boolean isDummyCredentials() {
        return "rzp_test_dummy".equals(razorpayKeyId)
                || razorpayKeyId == null
                || razorpayKeyId.trim().isEmpty();
    }
}
