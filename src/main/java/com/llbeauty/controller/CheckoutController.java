package com.llbeauty.controller;

import com.llbeauty.entity.AgentProfile;
import com.llbeauty.entity.Commission;
import com.llbeauty.repository.AgentProfileRepository;
import com.llbeauty.repository.CommissionRepository;
import com.llbeauty.entity.*;
import com.llbeauty.repository.*;
import com.llbeauty.service.MembershipService;
import com.llbeauty.service.WalletService;
import com.razorpay.RazorpayClient;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

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
import java.math.BigDecimal;
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
    private final com.llbeauty.service.PaymentService paymentService;
    private final OrderItemRepository orderItemRepository;
    private final com.llbeauty.service.RewardService rewardService;
    private final AgentProfileRepository agentProfileRepository;
    private final CommissionRepository commissionRepository;

    @Value("${razorpay.key.id:rzp_test_dummy}")
    private String razorpayKeyId;

    public CheckoutController(ProductRepository productRepository,
                              OrderRepository orderRepository,
                              UserRepository userRepository,
                              WalletService walletService,
                              MembershipService membershipService,
                              UserMembershipRepository userMembershipRepository,
                              com.llbeauty.service.PaymentService paymentService,
                              OrderItemRepository orderItemRepository,
                              com.llbeauty.service.RewardService rewardService,AgentProfileRepository agentProfileRepository,
                              CommissionRepository commissionRepository) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.walletService = walletService;
        this.membershipService = membershipService;
        this.userMembershipRepository = userMembershipRepository;
        this.paymentService = paymentService;
        this.orderItemRepository = orderItemRepository;
        this.rewardService = rewardService;
        this.agentProfileRepository = agentProfileRepository;
        this.commissionRepository = commissionRepository;
    }

    private User getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            return userRepository.findByEmail(auth.getName()).orElse(null);
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

    // Get cart items (AJAX)
    @GetMapping("/cart/items")
    @ResponseBody
    public ResponseEntity<?> getCartItems(HttpSession session) {
        Map<Long, Integer> cart = getCartFromSession(session);
        List<Map<String, Object>> items = new ArrayList<>();
        double subtotal = 0.0;
        for (Map.Entry<Long, Integer> entry : cart.entrySet()) {
            Product product = productRepository.findById(entry.getKey()).orElse(null);
            if (product != null) {
                Map<String, Object> item = new HashMap<>();
                item.put("productId", product.getId());
                item.put("name", product.getName());
                item.put("price", product.getPrice());
                item.put("imageUrl", product.getImageUrl());
                item.put("quantity", entry.getValue());
                double total = product.getPrice() * entry.getValue();
                item.put("total", total);
                items.add(item);
                subtotal += total;
            }
        }
        Map<String, Object> response = new HashMap<>();
        response.put("items", items);
        response.put("subtotal", subtotal);
        response.put("cartSize", cart.values().stream().mapToInt(Integer::intValue).sum());
        return ResponseEntity.ok(response);
    }

    // Update cart item quantity (AJAX)
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
        return getCartItems(session); // Return updated list
    }

    // Clear cart (AJAX)
    @PostMapping("/cart/clear")
    @ResponseBody
    public ResponseEntity<?> clearCart(HttpSession session) {
        session.removeAttribute("LLB_CART");
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("cartSize", 0);
        response.put("subtotal", 0.0);
        response.put("items", new ArrayList<>());
        return ResponseEntity.ok(response);
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

        BigDecimal subtotalBD = BigDecimal.valueOf(subtotal).setScale(2, java.math.RoundingMode.HALF_UP);
        BigDecimal discountAmountBD = subtotalBD.multiply(BigDecimal.valueOf(discountPercent)).setScale(2, java.math.RoundingMode.HALF_UP);
        BigDecimal discountedTotalBD = subtotalBD.subtract(discountAmountBD).setScale(2, java.math.RoundingMode.HALF_UP);
        BigDecimal gstAmountBD = discountedTotalBD.multiply(new BigDecimal("0.18")).setScale(2, java.math.RoundingMode.HALF_UP);
        BigDecimal finalAmountBD = discountedTotalBD.add(gstAmountBD).setScale(2, java.math.RoundingMode.HALF_UP);

        model.addAttribute("items", checkoutItems);
        model.addAttribute("subtotal", subtotalBD.doubleValue());
        model.addAttribute("discountPercent", discountPercent * 100);
        model.addAttribute("discountAmount", discountAmountBD.doubleValue());
        model.addAttribute("gstAmount", gstAmountBD.doubleValue());
        model.addAttribute("finalAmount", finalAmountBD.doubleValue());
        model.addAttribute("passName", passName);
        model.addAttribute("walletBalance", walletService.getBalance(user));

        // Not generating pre-created order ID here anymore. Handled via AJAX.
        model.addAttribute("razorpayKeyId", razorpayKeyId);

        return "checkout";
    }

    // Place Order API - handles wallet debit and initializes Razorpay for balance
    @PostMapping("/place-order")
    @ResponseBody
    public ResponseEntity<?> placeOrder(@RequestParam(value = "directProductId", required = false) Long directProductId,
                                        @RequestParam("useWallet") boolean useWallet,
                                        @RequestParam(value = "useNxl", defaultValue = "false") boolean useNxl, // ← हे ADD करा
                                        @RequestParam(value = "referralCode", required = false)
                                        String referralCode,
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

        // Pricing logic calculations with BigDecimal and scale 2
        BigDecimal discountedPrice = BigDecimal.valueOf(subtotal - discountAmount).setScale(2, java.math.RoundingMode.HALF_UP);
        BigDecimal gstAmount = discountedPrice.multiply(new BigDecimal("0.18")).setScale(2, java.math.RoundingMode.HALF_UP);

        BigDecimal nxlUsed = BigDecimal.ZERO;
        if (useNxl) {
            nxlUsed = walletService.getNxlBalance(user);
        }
        nxlUsed = nxlUsed.setScale(2, java.math.RoundingMode.HALF_UP);
        BigDecimal effectiveNxlUsed = nxlUsed.min(discountedPrice).setScale(2, java.math.RoundingMode.HALF_UP);
        BigDecimal remainingPrice = discountedPrice.subtract(effectiveNxlUsed).setScale(2, java.math.RoundingMode.HALF_UP);
        if (remainingPrice.compareTo(BigDecimal.ZERO) < 0) {
            remainingPrice = BigDecimal.ZERO;
        }

        BigDecimal finalPayable = remainingPrice.add(gstAmount).setScale(2, java.math.RoundingMode.HALF_UP);

        // 2. Handle Wallet Redemption
        double walletRedeemed = 0.0;
        if (useWallet) {
            double currentWallet = walletService.getBalance(user).doubleValue();
            walletRedeemed = Math.min(currentWallet, finalPayable.doubleValue());
            walletRedeemed = BigDecimal.valueOf(walletRedeemed).setScale(2, java.math.RoundingMode.HALF_UP).doubleValue();
        }

        double amountToPay = finalPayable.subtract(BigDecimal.valueOf(walletRedeemed)).setScale(2, java.math.RoundingMode.HALF_UP).doubleValue();
        if (amountToPay < 0) {
            amountToPay = 0.0;
        }

        // 3. Create Pending Order
        Order order = new Order();
        order.setUser(user);
        order.setTotalAmount(finalPayable.doubleValue());
        order.setStatus("PENDING");
        order.setPaymentId(null);
        if (referralCode != null) {
            referralCode = referralCode.trim();
        }
        order.setReferralCode(
            (referralCode != null && !referralCode.isBlank()) ? referralCode : null
        );
        Order savedOrder = orderRepository.save(order);

        // Save Order Items
        for (Product p : productsToDeductStock) {
            OrderItem oi = new OrderItem();
            oi.setOrder(savedOrder);
            oi.setProduct(p);
            int qty = 1;
            if (directProductId == null) {
                qty = getCartFromSession(session).getOrDefault(p.getId(), 1);
            }
            oi.setQuantity(qty);
            oi.setPriceAtPurchase(p.getPrice());
            orderItemRepository.save(oi);
        }

        // ✅ NXL Token Deduction — savedOrder.getId() now available
        if (effectiveNxlUsed.compareTo(BigDecimal.ZERO) > 0) {
            try {
                walletService.debitNxl(
                    user,
                    effectiveNxlUsed,
                    "ONLINE_SHOPPING",
                    "NXL_USE_ORDER_" + savedOrder.getId(),
                    "NXL used for Order #" + savedOrder.getId()
                );
            } catch (com.llbeauty.exception.NxlException e) {
                // insufficient tokens — ignore
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("orderId", savedOrder.getId());
        response.put("amountToPay", amountToPay);
        response.put("walletRedeemed", walletRedeemed);
        response.put("nxlRedeemed", effectiveNxlUsed);
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
                Payment payment = paymentService.initiatePayment(user, amountToPay, "PRODUCT", String.valueOf(savedOrder.getId()), "RAZORPAY" + (walletRedeemed > 0 ? "+WALLET" : ""));

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
    }

    // Verify payment from Razorpay
    @PostMapping("/confirm-order")
    @ResponseBody
    @Transactional
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
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                Map.of("message", "Login required"));
        }

        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                Map.of("message", "Order not found"));
        }

        // IMPORTANT: Check if already processed
        if ("SUCCESS".equals(order.getStatus())) {
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Order already completed",
                "redirectUrl", "/checkout/success?orderId=" + orderId
            ));
        }

        // Verify payment signature for real payments
        if (!isDummyCredentials()) {
            if (razorpayOrderId == null || razorpaySignature == null) {
                order.setStatus("FAILED");
                orderRepository.save(order);
                
                return ResponseEntity.badRequest().body(
                    Map.of("message", "Payment verification failed - missing signature"));
            }

            try {
                paymentService.verifyAndProcessPayment(razorpayOrderId, paymentId, razorpaySignature);
            } catch (Exception e) {
                order.setStatus("FAILED");
                orderRepository.save(order);
                
                return ResponseEntity.badRequest().body(
                    Map.of("message", "Payment verification failed"));
            }
        }

        // NOW deduct wallet (AFTER payment verified)
        if (useWallet && walletRedeemed > 0) {
            try {
                walletService.debit(user, walletRedeemed, 
                    "Wallet redemption for Order #" + orderId);
            } catch (Exception e) {
                order.setStatus("FAILED");
                orderRepository.save(order);
                
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    Map.of("message", "Failed to process wallet deduction"));
            }
        }

        completeOrder(order, user, paymentId, directProductId == null, session);

        return ResponseEntity.ok(Map.of(
            "status", "success",
            "redirectUrl", "/checkout/success?orderId=" + orderId
        ));
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
        String referralCode = order.getReferralCode();

        if (referralCode == null || referralCode.isBlank()) {
            referralCode = user.getReferralCode();
        }

        if (referralCode != null && !referralCode.isBlank()) {

            agentProfileRepository
                .findByReferralCode(referralCode)
                .ifPresent(agent -> {

                    double commissionAmount =
                            order.getTotalAmount() * 0.05; // 5%

                    Commission commission = new Commission();

                    commission.setAgent(agent);

                    commission.setAmount(
                            BigDecimal.valueOf(commissionAmount));

                    commission.setDescription(
                            "Product Order #" + order.getId());

                    commission.setStatus("PENDING");
                    commission.setCommissionType("PRODUCT");

                    commissionRepository.save(commission);
                    walletService.creditNxl(
                    	    agent.getUser(),
                    	    BigDecimal.valueOf(commissionAmount),
                    	    WalletService.SOURCE_REFERRAL,
                    	    "PRODUCT_" + order.getId(),
                    	    "Product Order Referral Commission"
                    	);
                });
        }

        // Award membership cashback
        Optional<UserMembership> activeOpt = membershipService.getActiveMembership(user);
        if (activeOpt.isPresent()) {
            double cashbackPercent = activeOpt.get().getMembership().getCashbackPercent();
            double cashbackAmount = order.getTotalAmount() * cashbackPercent;
            if (cashbackAmount > 0) {
            	walletService.creditNxl(
            		    user,
            		    BigDecimal.valueOf(cashbackAmount),
            		    WalletService.SOURCE_MEMBERSHIP,
            		    "CASHBACK_" + order.getId(),
            		    "Product Cashback"
            		);
            }
            
            // Award Reward Points!
            rewardService.awardPoints(user, BigDecimal.valueOf(order.getTotalAmount()));
        }

        // Award default purchase credits (e.g. 5% cashback or 1 credit per 20 rupees spent even for non-VIP if desired, but VIP gets the plan specific percentage)
        // Here we adhere to VIP-only automatic plan discounts and cashback logic.

     // ✅ 5% NXL CASHBACK — remainingPrice * 5%
        try {
            BigDecimal gstRate = new BigDecimal("0.18");
            BigDecimal onePlusGstRate = new BigDecimal("1.18");
            BigDecimal finalPayable = BigDecimal.valueOf(order.getTotalAmount());
            BigDecimal effectiveNxlUsed = BigDecimal.ZERO;
            List<com.llbeauty.entity.NxlWalletTransaction> history = walletService.getNxlHistory(user);
            for (com.llbeauty.entity.NxlWalletTransaction tx : history) {
                if (("NXL_USE_ORDER_" + order.getId()).equals(tx.getTransactionId())) {
                    effectiveNxlUsed = tx.getAmount();
                    break;
                }
            }
            BigDecimal numerator = finalPayable.subtract(effectiveNxlUsed.multiply(gstRate));
            BigDecimal remainingPrice = numerator.divide(onePlusGstRate, 2, java.math.RoundingMode.HALF_UP);

            BigDecimal cashbackTokens = remainingPrice
                    .multiply(new BigDecimal("0.05"))
                    .setScale(2, java.math.RoundingMode.HALF_UP);
            if (cashbackTokens.compareTo(BigDecimal.ZERO) > 0) {
                walletService.creditNxl(
                    user,
                    cashbackTokens,
                    "ONLINE_SHOPPING",
                    "CASHBACK_ORDER_" + order.getId(),
                    "5% cashback on Order #" + order.getId()
                );
            }
        } catch (com.llbeauty.exception.NxlException e) {
            // duplicate or validation error
        }

        if (clearCart) {
            session.removeAttribute("LLB_CART");
        }
        if (clearCart) {
            session.removeAttribute("LLB_CART");
        }
    }

    private boolean isDummyCredentials() {
        return "rzp_test_dummy".equals(razorpayKeyId) || razorpayKeyId == null || razorpayKeyId.trim().isEmpty();
    }
}
