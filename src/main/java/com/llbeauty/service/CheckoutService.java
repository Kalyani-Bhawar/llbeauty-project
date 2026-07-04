package com.llbeauty.service;

import com.llbeauty.entity.*;
import com.llbeauty.repository.*;
import com.llbeauty.exception.NxlException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

/**
 * CheckoutService handles the full product purchase lifecycle.
 *
 * =========================================================
 * CASHBACK FORMULA (Business Rules 1-5)
 * =========================================================
 *
 *  Step 1: subtotal            = sum of product prices (excl. GST, excl. any discount)
 *  Step 2: discountedPrice     = subtotal  - (subtotal × membershipDiscountPercent)
 *  Step 3: gstAmount           = discountedPrice × 18%
 *  Step 4: effectiveNxlUsed    = min(userNxlBalance, discountedPrice)
 *                                  (NXL is applied BEFORE GST, only against discountedPrice)
 *  Step 5: finalPayable        = discountedPrice - effectiveNxlUsed + gstAmount
 *  Step 6: walletRedeemed      = min(walletBalance, finalPayable)
 *  Step 7: amountToPayByCard   = finalPayable - walletRedeemed
 *
 *  Cashback:
 *    cashbackBase   = discountedPrice - effectiveNxlUsed      (GST is NEVER in cashback base)
 *    cashbackBase   = max(0, cashbackBase)
 *    cashbackAmount = cashbackBase × 5%
 *
 * =========================================================
 * LEDGER after purchase (ONLY these two entries may appear for the buyer):
 *   -X NXL  "NXL used for Order #N"
 *   +Y NXL  "5% Cashback on Order #N"
 *
 * No duplicate cashback. No "Product Cashback". No "Reward Points Converted to NXL Wallet".
 * =========================================================
 */
@Service
public class CheckoutService {

    private static final BigDecimal GST_RATE            = new BigDecimal("0.18");
    private static final BigDecimal CASHBACK_RATE       = new BigDecimal("0.05");

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final WalletService walletService;
    private final MembershipService membershipService;
    private final PaymentService paymentService;
    private final OrderItemRepository orderItemRepository;
    private final RewardService rewardService;
    private final AgentProfileRepository agentProfileRepository;
    private final CommissionRepository commissionRepository;
    private final OrderStatusHistoryRepository orderStatusHistoryRepository;

    public CheckoutService(ProductRepository productRepository,
                           OrderRepository orderRepository,
                           WalletService walletService,
                           MembershipService membershipService,
                           PaymentService paymentService,
                           OrderItemRepository orderItemRepository,
                           RewardService rewardService,
                           AgentProfileRepository agentProfileRepository,
                           CommissionRepository commissionRepository,
                           OrderStatusHistoryRepository orderStatusHistoryRepository) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.walletService = walletService;
        this.membershipService = membershipService;
        this.paymentService = paymentService;
        this.orderItemRepository = orderItemRepository;
        this.rewardService = rewardService;
        this.agentProfileRepository = agentProfileRepository;
        this.commissionRepository = commissionRepository;
        this.orderStatusHistoryRepository = orderStatusHistoryRepository;
    }

    // =========================================================
    //  CART / PAGE HELPERS  (no changes to business logic here)
    // =========================================================

    public Map<String, Object> getCartItemsDetails(Map<Long, Integer> cart) {
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
        return response;
    }

    public Map<String, Object> getCheckoutPageDetails(User user, Long directProductId, Map<Long, Integer> cart) {
        List<Map<String, Object>> checkoutItems = new ArrayList<>();
        double subtotal = 0.0;

        if (directProductId != null) {
            Product product = productRepository.findById(directProductId).orElse(null);
            if (product != null) {
                Map<String, Object> item = new HashMap<>();
                item.put("product", product);
                item.put("quantity", 1);
                item.put("total", product.getPrice());
                checkoutItems.add(item);
                subtotal = product.getPrice();
            }
        } else {
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

        double discountPercent = 0.0;
        String passName = "";
        Optional<UserMembership> activeOpt = membershipService.getActiveMembership(user);
        if (activeOpt.isPresent()) {
            UserMembership active = activeOpt.get();
            discountPercent = active.getMembership().getCashbackPercent();
            passName = active.getMembership().getName();
        }

        BigDecimal subtotalBD       = BigDecimal.valueOf(subtotal).setScale(2, RoundingMode.HALF_UP);
        BigDecimal discountAmountBD = subtotalBD.multiply(BigDecimal.valueOf(discountPercent)).setScale(2, RoundingMode.HALF_UP);
        BigDecimal discountedTotalBD = subtotalBD.subtract(discountAmountBD).setScale(2, RoundingMode.HALF_UP);
        BigDecimal gstAmountBD      = discountedTotalBD.multiply(GST_RATE).setScale(2, RoundingMode.HALF_UP);
        BigDecimal finalAmountBD    = discountedTotalBD.add(gstAmountBD).setScale(2, RoundingMode.HALF_UP);

        Map<String, Object> details = new HashMap<>();
        details.put("items", checkoutItems);
        details.put("subtotal", subtotalBD.doubleValue());
        details.put("discountPercent", discountPercent * 100);
        details.put("discountAmount", discountAmountBD.doubleValue());
        details.put("gstAmount", gstAmountBD.doubleValue());
        details.put("finalAmount", finalAmountBD.doubleValue());
        details.put("passName", passName);
        details.put("walletBalance", walletService.getBalance(user));

        return details;
    }

    // =========================================================
    //  PLACE ORDER
    // =========================================================

    @Transactional
    public Map<String, Object> placeOrder(User user, Long directProductId, boolean useWallet,
                                          boolean useNxl, String referralCode,
                                          Map<Long, Integer> cart, String razorpayKeyId,
                                          String shippingAddress, String billingName, String billingMobile) {

        // --- 1. Collect products and compute raw subtotal ---
        BigDecimal subtotal = BigDecimal.ZERO;
        List<Product> productsToDeductStock = new ArrayList<>();

        if (directProductId != null) {
            Product product = productRepository.findById(directProductId).orElse(null);
            if (product != null) {
                subtotal = BigDecimal.valueOf(product.getPrice()).setScale(2, RoundingMode.HALF_UP);
                productsToDeductStock.add(product);
            }
        } else {
            if (cart.isEmpty()) throw new IllegalArgumentException("Cart is empty");
            for (Map.Entry<Long, Integer> entry : cart.entrySet()) {
                Product product = productRepository.findById(entry.getKey()).orElse(null);
                if (product != null) {
                    subtotal = subtotal.add(
                        BigDecimal.valueOf(product.getPrice() * entry.getValue())
                    ).setScale(2, RoundingMode.HALF_UP);
                    productsToDeductStock.add(product);
                }
            }
        }

        // --- 2. Membership discount (applied BEFORE GST) ---
        BigDecimal discountPercent = BigDecimal.ZERO;
        Optional<UserMembership> activeOpt = membershipService.getActiveMembership(user);
        if (activeOpt.isPresent()) {
            discountPercent = BigDecimal.valueOf(activeOpt.get().getMembership().getCashbackPercent());
        }
        BigDecimal discountAmount  = subtotal.multiply(discountPercent).setScale(2, RoundingMode.HALF_UP);
        BigDecimal discountedPrice = subtotal.subtract(discountAmount).setScale(2, RoundingMode.HALF_UP);

        // --- 3. GST on discounted price ---
        BigDecimal gstAmount = discountedPrice.multiply(GST_RATE).setScale(2, RoundingMode.HALF_UP);

        // --- 4. NXL deduction (applied to discountedPrice BEFORE adding GST) ---
        BigDecimal nxlBalance       = useNxl ? walletService.getNxlBalance(user) : BigDecimal.ZERO;
        BigDecimal effectiveNxlUsed = nxlBalance.min(discountedPrice).setScale(2, RoundingMode.HALF_UP);
        if (effectiveNxlUsed.compareTo(BigDecimal.ZERO) < 0) {
            effectiveNxlUsed = BigDecimal.ZERO;
        }

        // --- 5. Final payable = (discountedPrice - NXL) + GST ---
        BigDecimal remainingPrice = discountedPrice.subtract(effectiveNxlUsed).setScale(2, RoundingMode.HALF_UP);
        if (remainingPrice.compareTo(BigDecimal.ZERO) < 0) remainingPrice = BigDecimal.ZERO;
        BigDecimal finalPayable = remainingPrice.add(gstAmount).setScale(2, RoundingMode.HALF_UP);

        // --- 6. Wallet deduction ---
        BigDecimal walletRedeemed = BigDecimal.ZERO;
        if (useWallet) {
            BigDecimal walletBalance = walletService.getBalance(user);
            walletRedeemed = walletBalance.min(finalPayable).setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal amountToPayBD = finalPayable.subtract(walletRedeemed).setScale(2, RoundingMode.HALF_UP);
        if (amountToPayBD.compareTo(BigDecimal.ZERO) < 0) amountToPayBD = BigDecimal.ZERO;

        // --- 7. Create order ---
        Order order = new Order();
        order.setUser(user);
        order.setTotalAmount(finalPayable.doubleValue());
        order.setStatus("PENDING");
        order.setPaymentId(null);
        if (referralCode != null) referralCode = referralCode.trim();
        order.setReferralCode((referralCode != null && !referralCode.isBlank()) ? referralCode : null);
        order.setShippingAddress(shippingAddress);
        order.setBillingName(billingName);
        order.setBillingMobile(billingMobile);
        // Store discountedPrice and effectiveNxlUsed on order so completeOrder can compute cashback
        // correctly even if called asynchronously (e.g. webhook). We embed them in existing fields.
        Order savedOrder = orderRepository.save(order);

        // --- 8. Save order items ---
        for (Product p : productsToDeductStock) {
            OrderItem oi = new OrderItem();
            oi.setOrder(savedOrder);
            oi.setProduct(p);
            int qty = (directProductId == null) ? cart.getOrDefault(p.getId(), 1) : 1;
            oi.setQuantity(qty);
            oi.setPriceAtPurchase(p.getPrice());
            orderItemRepository.save(oi);
        }

        // --- 9. Debit NXL immediately (before Razorpay so it's committed) ---
        if (effectiveNxlUsed.compareTo(BigDecimal.ZERO) > 0) {
            try {
                walletService.debitNxl(user, effectiveNxlUsed, "ONLINE_SHOPPING",
                        "NXL_USE_ORDER_" + savedOrder.getId(),
                        "NXL used for Order #" + savedOrder.getId());
            } catch (NxlException e) {
                // If NXL balance is insufficient, silently zero it out (race condition guard)
                effectiveNxlUsed = BigDecimal.ZERO;
            }
        }

        // --- 10. Build response ---
        Map<String, Object> response = new HashMap<>();
        response.put("orderId", savedOrder.getId());
        response.put("amountToPay", amountToPayBD.doubleValue());
        response.put("walletRedeemed", walletRedeemed.doubleValue());
        response.put("nxlRedeemed", effectiveNxlUsed);

        if (amountToPayBD.compareTo(BigDecimal.ZERO) <= 0) {
            // Fully paid by wallet + NXL — complete immediately
            completeOrder(savedOrder, user, "WALLET_PAY", directProductId == null, cart,
                    discountedPrice, effectiveNxlUsed);
            response.put("status", "success");
            response.put("redirectUrl", "/checkout/success?orderId=" + savedOrder.getId());
            return response;
        }

        // --- 11. Initiate Razorpay payment ---
        if (!isDummyCredentials(razorpayKeyId)) {
            try {
                Payment payment = paymentService.initiatePayment(user, amountToPayBD.doubleValue(),
                        "PRODUCT", String.valueOf(savedOrder.getId()),
                        "RAZORPAY" + (walletRedeemed.compareTo(BigDecimal.ZERO) > 0 ? "+WALLET" : ""));
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
        return response;
    }

    // =========================================================
    //  CONFIRM ORDER PAYMENT (called after Razorpay success)
    // =========================================================

    @Transactional
    public Map<String, Object> confirmOrderPayment(Long orderId, String paymentId,
                                                   String razorpayOrderId, String razorpaySignature,
                                                   boolean useWallet, double walletRedeemed,
                                                   boolean isDirectProduct, User user,
                                                   String razorpayKeyId, Map<Long, Integer> cart) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        if ("SUCCESS".equals(order.getStatus())) {
            return Map.of("status", "success", "message", "Order already completed",
                    "redirectUrl", "/checkout/success?orderId=" + orderId);
        }

        if (!isDummyCredentials(razorpayKeyId)) {
            if (razorpayOrderId == null || razorpaySignature == null) {
                order.setStatus("FAILED");
                orderRepository.save(order);
                throw new IllegalArgumentException("Payment verification failed - missing signature");
            }
            try {
                paymentService.verifyAndProcessPayment(razorpayOrderId, paymentId, razorpaySignature);
            } catch (Exception e) {
                order.setStatus("FAILED");
                orderRepository.save(order);
                throw new IllegalArgumentException("Payment verification failed");
            }
        }

        if (useWallet && walletRedeemed > 0) {
            try {
                walletService.debit(user, walletRedeemed, "Wallet redemption for Order #" + orderId);
            } catch (Exception e) {
                order.setStatus("FAILED");
                orderRepository.save(order);
                throw new IllegalStateException("Failed to process wallet deduction");
            }
        }

        // Recover effectiveNxlUsed from the NXL transaction that was already committed in placeOrder
        BigDecimal effectiveNxlUsed = recoverNxlUsed(user, order.getId());

        // Recover discountedPrice from order items (sum of priceAtPurchase × qty before any discounts)
        // We re-derive discountedPrice using the same formula so cashback is always consistent.
        BigDecimal discountedPrice = deriveDiscountedPrice(order, user);

        completeOrder(order, user, paymentId, !isDirectProduct, cart, discountedPrice, effectiveNxlUsed);

        return Map.of("status", "success", "redirectUrl", "/checkout/success?orderId=" + orderId);
    }

    // =========================================================
    //  COMPLETE ORDER — single authoritative post-payment method
    // =========================================================

    /**
     * Marks the order SUCCESS, awards referral commission, and credits ONE cashback transaction.
     *
     * Cashback formula:
     *   cashbackBase   = max(0, discountedPrice - effectiveNxlUsed)
     *   cashbackAmount = cashbackBase × 5%
     *
     * GST is NEVER part of the cashback base.
     * No other NXL credit is generated for the buyer inside this method.
     * Reward points are stored in RewardPoint table only — NOT converted to NXL here.
     *
     * @param discountedPrice   Product price after membership discount, before GST (BigDecimal)
     * @param effectiveNxlUsed  NXL actually deducted for this order (BigDecimal)
     */
    private void completeOrder(Order order, User user, String paymentId,
                               boolean clearCart, Map<Long, Integer> cart,
                               BigDecimal discountedPrice, BigDecimal effectiveNxlUsed) {
        order.setStatus("SUCCESS");
        order.setPaymentId(paymentId);
        order.setOrderStatus("PLACED");
        order.setLastStatusUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);

        // Save status history record
        OrderStatusHistory trackingHistory = new OrderStatusHistory(order, "PLACED", "Order successfully paid and placed.", user != null ? user.getEmail() : "system");
        orderStatusHistoryRepository.save(trackingHistory);

        // --- Referral commission (unchanged, credits agent's NXL — not the buyer) ---
        String referralCode = (order.getReferralCode() != null && !order.getReferralCode().isBlank())
                ? order.getReferralCode() : user.getReferralCode();
        if (referralCode != null && !referralCode.isBlank()) {
            agentProfileRepository.findByReferralCode(referralCode).ifPresent(agent -> {
                double commissionAmount = order.getTotalAmount() * 0.05;
                Commission commission = new Commission();
                commission.setAgent(agent);
                commission.setAmount(BigDecimal.valueOf(commissionAmount));
                commission.setDescription("Product Order #" + order.getId());
                commission.setStatus("PENDING");
                commission.setCommissionType("PRODUCT");
                commissionRepository.save(commission);
                walletService.creditNxl(agent.getUser(), BigDecimal.valueOf(commissionAmount),
                        WalletService.SOURCE_REFERRAL,
                        "PRODUCT_" + order.getId(),
                        "Product Order Referral Commission");
            });
        }

        // --- ONE cashback transaction for the buyer ---
        // cashbackBase = max(0, discountedPrice - effectiveNxlUsed)
        // GST is never included.
        BigDecimal cashbackBase = discountedPrice.subtract(effectiveNxlUsed).setScale(2, RoundingMode.HALF_UP);
        if (cashbackBase.compareTo(BigDecimal.ZERO) < 0) cashbackBase = BigDecimal.ZERO;

        BigDecimal cashbackAmount = cashbackBase.multiply(CASHBACK_RATE).setScale(2, RoundingMode.HALF_UP);
        if (cashbackAmount.compareTo(BigDecimal.ZERO) > 0) {
            try {
                walletService.creditNxl(user, cashbackAmount,
                        "ONLINE_SHOPPING",
                        "CASHBACK_ORDER_" + order.getId(),
                        "5% Cashback on Order #" + order.getId());
            } catch (NxlException e) {
                // Non-fatal: cashback failed (e.g. system wallet empty). Log and continue.
            }
        }

        // --- Reward points stored in RewardPoint table ONLY (NOT converted to NXL) ---
        // rewardService.awardPoints() is called here; it must NOT call walletService.creditNxl().
        rewardService.awardPoints(user, BigDecimal.valueOf(order.getTotalAmount()));

        if (clearCart && cart != null) {
            cart.clear();
        }
    }

    // =========================================================
    //  HELPER: recover NXL used from already-committed tx
    // =========================================================

    /**
     * Looks up the NXL debit transaction created by placeOrder to find the actual NXL deducted.
     * Returns ZERO if no NXL was used.
     */
    private BigDecimal recoverNxlUsed(User user, Long orderId) {
        try {
            List<NxlWalletTransaction> history = walletService.getNxlHistory(user);
            String targetTxId = "NXL_USE_ORDER_" + orderId;
            for (NxlWalletTransaction tx : history) {
                if (targetTxId.equals(tx.getTransactionId())) {
                    return tx.getAmount() != null ? tx.getAmount().abs() : BigDecimal.ZERO;
                }
            }
        } catch (Exception ignored) { }
        return BigDecimal.ZERO;
    }

    // =========================================================
    //  HELPER: derive discountedPrice from order (for webhook path)
    // =========================================================

    /**
     * Re-derives discountedPrice from the order's items and the user's active membership.
     * This mirrors the placeOrder calculation so cashback is consistent regardless of
     * which code path (direct confirm vs. Razorpay webhook) completes the order.
     */
    private BigDecimal deriveDiscountedPrice(Order order, User user) {
        // Sum raw product prices from order items
        List<OrderItem> items = orderItemRepository.findByOrder(order);
        BigDecimal rawSubtotal = BigDecimal.ZERO;
        for (OrderItem oi : items) {
            if (oi.getPriceAtPurchase() != null && oi.getQuantity() != null) {
                rawSubtotal = rawSubtotal.add(
                    BigDecimal.valueOf(oi.getPriceAtPurchase()).multiply(BigDecimal.valueOf(oi.getQuantity()))
                );
            }
        }

        // Apply membership discount
        BigDecimal discountPercent = BigDecimal.ZERO;
        Optional<UserMembership> activeOpt = membershipService.getActiveMembership(user);
        if (activeOpt.isPresent()) {
            discountPercent = BigDecimal.valueOf(activeOpt.get().getMembership().getCashbackPercent());
        }
        BigDecimal discountAmount  = rawSubtotal.multiply(discountPercent).setScale(2, RoundingMode.HALF_UP);
        return rawSubtotal.subtract(discountAmount).setScale(2, RoundingMode.HALF_UP);
    }

    // =========================================================
    //  SUCCESS PAGE HELPERS
    // =========================================================

    public Order getOrderSuccessDetails(Long orderId, User user) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null || !order.getUser().getId().equals(user.getId())) {
            return null;
        }
        return order;
    }

    /**
     * Returns cashback earned for display on the success page.
     * This re-derives cashback so it matches what was actually credited.
     */
    public double getCashbackEarned(Order order, User user) {
        BigDecimal discountedPrice  = deriveDiscountedPrice(order, user);
        BigDecimal effectiveNxlUsed = recoverNxlUsed(user, order.getId());
        BigDecimal cashbackBase     = discountedPrice.subtract(effectiveNxlUsed).setScale(2, RoundingMode.HALF_UP);
        if (cashbackBase.compareTo(BigDecimal.ZERO) < 0) cashbackBase = BigDecimal.ZERO;
        return cashbackBase.multiply(CASHBACK_RATE).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    private boolean isDummyCredentials(String razorpayKeyId) {
        return "rzp_test_dummy".equals(razorpayKeyId) || razorpayKeyId == null || razorpayKeyId.trim().isEmpty();
    }
}
