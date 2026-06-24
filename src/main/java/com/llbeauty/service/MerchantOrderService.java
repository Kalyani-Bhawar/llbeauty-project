package com.llbeauty.service;

import com.llbeauty.entity.*;
import com.llbeauty.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * MerchantOrderService — All merchant order business logic lives here.
 *
 * Responsibilities:
 *  - Create orders from cart (initiateOrder)
 *  - Deduct NXL wallet credits at order initiation (reserved immediately)
 *  - Confirm orders after Razorpay payment (confirmOrder)
 *  - Roll back PENDING orders on payment failure (rollbackOrder)
 *  - Update product stock and generate invoice on completion
 *
 * Architecture rule: Controllers call initiateOrder() / confirmOrder() / rollbackOrder() only.
 * No business logic should exist in the controller.
 */
@Service
public class MerchantOrderService {

    private final ProductRepository productRepository;
    private final MerchantOrderRepository merchantOrderRepository;
    private final MerchantOrderItemRepository merchantOrderItemRepository;
    private final InvoiceRepository invoiceRepository;
    private final WalletService walletService;
    private final AuditLogRepository auditLogRepository;

    public MerchantOrderService(ProductRepository productRepository,
                                MerchantOrderRepository merchantOrderRepository,
                                MerchantOrderItemRepository merchantOrderItemRepository,
                                InvoiceRepository invoiceRepository,
                                WalletService walletService,
                                AuditLogRepository auditLogRepository) {
        this.productRepository = productRepository;
        this.merchantOrderRepository = merchantOrderRepository;
        this.merchantOrderItemRepository = merchantOrderItemRepository;
        this.invoiceRepository = invoiceRepository;
        this.walletService = walletService;
        this.auditLogRepository = auditLogRepository;
    }

    // ──────────────────────────────────────────────────────────────────
    // Initiate Order
    // ──────────────────────────────────────────────────────────────────

    /**
     * Create a MerchantOrder from the session cart.
     *
     * Pricing logic (single source of truth — mirrors MerchantCartService):
     *   merchantDiscount%  → from Product.getMerchantDiscount()
     *   bulkDiscount%      → qty 11-50 = 5%, qty 51+ = 10%, else 0
     *   finalPrice         = mrp * (1 - merchantDisc/100) * (1 - bulkDisc/100)
     *
     * Wallet flow:
     *   - If useWallet=true, deducts min(walletBalance, finalAmount) immediately.
     *   - If wallet covers full amount, order is marked SUCCESS immediately.
     *   - If partial, order stays PENDING until Razorpay payment confirmed.
     *
     * @param user      the authenticated merchant user
     * @param cart      session cart: Map<productId, quantity>
     * @param useWallet whether the user elected to use NXL Wallet credits
     * @return the persisted MerchantOrder (PENDING or SUCCESS)
     */
    @Transactional
    public MerchantOrder initiateOrder(User user, Map<Long, Integer> cart, boolean useWallet) {
        if (cart == null || cart.isEmpty()) {
            throw new IllegalArgumentException("Cart is empty");
        }

        // ── Step 1: Create the order skeleton ──
        MerchantOrder order = new MerchantOrder();
        order.setUser(user);
        order.setOrderStatus("PENDING");
        order = merchantOrderRepository.save(order); // save to get ID for FK references

        // ── Step 2: Process cart items & accumulate totals ──
        double subtotal = 0.0;
        double productDiscounts = 0.0;
        double bulkDiscounts = 0.0;
        double totalSavings = 0.0;
        double finalAmount = 0.0;

        List<MerchantOrderItem> items = new ArrayList<>();

        for (Map.Entry<Long, Integer> entry : cart.entrySet()) {
            Product product = productRepository.findById(entry.getKey())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found: " + entry.getKey()));

            int qty = entry.getValue();
            if (qty <= 0) continue;

            Double mrp = product.getPrice();
            if (mrp == null) {
                mrp = 0.0;
            }

            // Merchant discount
            double merchantDiscPercent = product.getMerchantDiscount() != null
                    ? product.getMerchantDiscount() : 0.0;
            double merchantDiscAmount = mrp * (merchantDiscPercent / 100.0);
            double merchantPrice = mrp - merchantDiscAmount;

            // Bulk discount (applied on top of merchant price)
            double bulkDiscPercent = resolveBulkDiscountPercent(qty);
            double bulkDiscAmount = merchantPrice * (bulkDiscPercent / 100.0);
            double finalPrice = merchantPrice - bulkDiscAmount;

            // Line totals
            double itemSubtotal = mrp * qty;
            double itemProductDisc = merchantDiscAmount * qty;
            double itemBulkDisc = bulkDiscAmount * qty;
            double itemFinal = finalPrice * qty;

            subtotal += itemSubtotal;
            productDiscounts += itemProductDisc;
            bulkDiscounts += itemBulkDisc;
            totalSavings += (itemProductDisc + itemBulkDisc);
            finalAmount += itemFinal;

            // Build order item entity
            MerchantOrderItem item = new MerchantOrderItem();
            item.setMerchantOrder(order);
            item.setProduct(product);
            item.setQuantity(qty);
            item.setMrp(mrp);
            item.setMerchantDiscountPercent(merchantDiscPercent);
            item.setMerchantPrice(merchantPrice);
            item.setBulkDiscountPercent(bulkDiscPercent);
            item.setFinalPrice(finalPrice);
            items.add(item);
            merchantOrderItemRepository.save(item);
        }

        // ── Step 3: Save computed totals onto order ──
        order.setItems(items);
        order.setSubtotal(subtotal);
        order.setProductDiscounts(productDiscounts);
         
        order.setTotalSavings(totalSavings);
        order.setFinalAmount(finalAmount);

        // ── Step 4: Wallet deduction (reserve credits up-front) ──
        double walletAmountUsed = 0.0;
        if (useWallet) {
            double walletBalance = walletService.getBalance(user).doubleValue();
            double toDeduct = Math.min(walletBalance, finalAmount);
            if (toDeduct > 0) {
                boolean debited = walletService.debit(
                        user,
                        BigDecimal.valueOf(toDeduct),
                        "Reserved NXL Credits for Merchant Order #" + order.getId(),
                        "MERCHANT_ORDER");
                if (debited) {
                    walletAmountUsed = toDeduct;
                }
            }
        }

        order.setWalletAmountUsed(walletAmountUsed);
        order.setRazorpayAmountPaid(0.0); // Updated after Razorpay confirmation

        // ── Step 5: If wallet covers full amount → complete immediately ──
        double remainingAmount = finalAmount - walletAmountUsed;
        if (remainingAmount <= 0) {
            order.setOrderStatus("SUCCESS");
            merchantOrderRepository.save(order);
            updateStockAndGenerateInvoice(order);
            auditLogRepository.save(new AuditLog(
                    "MERCHANT_ORDER_COMPLETED",
                    "Order #" + order.getId() + " fully paid via NXL Credits. Amount: ₹" + finalAmount,
                    user.getEmail()));
        } else {
            merchantOrderRepository.save(order);
        }

        return order;
    }

    // ──────────────────────────────────────────────────────────────────
    // Confirm Order (after Razorpay payment)
    // ──────────────────────────────────────────────────────────────────

    /**
     * Confirm a PENDING order after successful Razorpay payment.
     * Idempotent — safe to call if order is already SUCCESS (webhook + frontend double-fire).
     *
     * @param orderId         internal order ID
     * @param paymentId       Razorpay payment ID (razorpay_payment_id)
     * @param signature       Razorpay signature (razorpay_signature)
     * @param razorpayOrderId Razorpay order ID (razorpay_order_id) — may be null for mock flow
     */
    @Transactional
    public void confirmOrder(Long orderId, String paymentId, String signature, String razorpayOrderId) {
        MerchantOrder order = merchantOrderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

        // Idempotency guard
        if ("SUCCESS".equals(order.getOrderStatus())) {
            return;
        }

        order.setOrderStatus("SUCCESS");
        order.setRazorpayPaymentId(paymentId);
        order.setRazorpaySignature(signature);
        if (razorpayOrderId != null) {
            order.setRazorpayOrderId(razorpayOrderId);
        }

        double razorpayPaid = order.getFinalAmount() - order.getWalletAmountUsed();
        order.setRazorpayAmountPaid(Math.max(0, razorpayPaid));

        merchantOrderRepository.save(order);

        // Deduct stock and create invoice
        updateStockAndGenerateInvoice(order);

        auditLogRepository.save(new AuditLog(
                "MERCHANT_ORDER_COMPLETED",
                "Order #" + order.getId() + " confirmed. Wallet: ₹" + order.getWalletAmountUsed()
                        + " + Razorpay: ₹" + order.getRazorpayAmountPaid(),
                order.getUser().getEmail()));
    }

    // ──────────────────────────────────────────────────────────────────
    // Rollback Order (Razorpay dismissed / failed)
    // ──────────────────────────────────────────────────────────────────

    /**
     * Roll back a PENDING order. Restores any NXL wallet credits that were debited at initiation.
     * Only acts on PENDING orders — SUCCESS orders are not touched.
     *
     * @param orderId internal order ID
     */
    @Transactional
    public void rollbackOrder(Long orderId) {
        MerchantOrder order = merchantOrderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

        if (!"PENDING".equals(order.getOrderStatus())) {
            return; // Cannot roll back a completed or already-failed order
        }

        order.setOrderStatus("PAYMENT_FAILED");
        merchantOrderRepository.save(order);

        // Restore wallet credits if any were deducted
        if (order.getWalletAmountUsed() != null && order.getWalletAmountUsed() > 0) {
            walletService.credit(
                    order.getUser(),
                    BigDecimal.valueOf(order.getWalletAmountUsed()),
                    "Restored NXL Credits — Merchant Order #" + order.getId() + " payment failed",
                    "MERCHANT_ORDER_ROLLBACK");
        }

        auditLogRepository.save(new AuditLog(
                "MERCHANT_ORDER_FAILED",
                "Order #" + order.getId() + " payment failed. NXL Credits restored: ₹" + order.getWalletAmountUsed(),
                order.getUser().getEmail()));
    }

    // ──────────────────────────────────────────────────────────────────
    // Internal helpers
    // ──────────────────────────────────────────────────────────────────

    /**
     * Deduct product stock and generate an invoice PDF record.
     * Called on order completion — both wallet-only and Razorpay paths.
     */
    private void updateStockAndGenerateInvoice(MerchantOrder order) {
        // Deduct stock from each product
        for (MerchantOrderItem item : order.getItems()) {
            Product product = item.getProduct();
            int newStock = Math.max(0, (product.getStock() != null ? product.getStock() : 0) - item.getQuantity());
            product.setStock(newStock);
            productRepository.save(product);
        }

        // Create invoice record
        Invoice invoice = new Invoice();
        invoice.setMerchantOrder(order);
        invoice.setInvoiceNumber("INV-MERCH-" + System.currentTimeMillis() + "-" + order.getId());
        invoice.setOrderNumber("ORD-MERCH-" + order.getId());
        invoice.setSubtotal(order.getSubtotal());
        invoice.setProductDiscounts(order.getProductDiscounts());
        invoice.setTotalSavings(order.getTotalSavings());
        invoice.setWalletAmountUsed(order.getWalletAmountUsed());
        invoice.setRazorpayAmountPaid(order.getRazorpayAmountPaid());
        invoice.setFinalPayableAmount(order.getFinalAmount());
        invoiceRepository.save(invoice);
    }

    /**
     * Resolve bulk discount percentage by quantity tier.
     * Must stay in sync with MerchantCartService.resolveBulkDiscountPercent()
     */
    private double resolveBulkDiscountPercent(int qty) {
        if (qty >= 51) return 10.0;
        if (qty >= 11) return 5.0;
        return 0.0;
    }
}
