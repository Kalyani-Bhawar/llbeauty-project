package com.llbeauty.service;

import com.llbeauty.entity.*;
import com.llbeauty.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    @Transactional
    public MerchantOrder initiateOrder(User user, Map<Long, Integer> cart, boolean useWallet) {
        if (cart == null || cart.isEmpty()) {
            throw new IllegalArgumentException("Cart is empty");
        }

        double subtotal = 0.0;
        double productDiscounts = 0.0;
        double bulkDiscounts = 0.0;
        double totalSavings = 0.0;
        double finalAmount = 0.0;

        MerchantOrder order = new MerchantOrder();
        order.setUser(user);
        order.setOrderStatus("PENDING");
        order = merchantOrderRepository.save(order);

        List<MerchantOrderItem> items = new ArrayList<>();

        for (Map.Entry<Long, Integer> entry : cart.entrySet()) {
            Product product = productRepository.findById(entry.getKey())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found: " + entry.getKey()));
            int qty = entry.getValue();
            if (qty <= 0) continue;

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

        order.setItems(items);
        order.setSubtotal(subtotal);
        order.setProductDiscounts(productDiscounts);
        order.setBulkDiscounts(bulkDiscounts);
        order.setTotalSavings(totalSavings);
        order.setFinalAmount(finalAmount);

        double walletAmountUsed = 0.0;
        if (useWallet) {
            double walletBalance = walletService.getBalance(user).doubleValue();
            walletAmountUsed = Math.min(walletBalance, finalAmount);
            if (walletAmountUsed > 0) {
                boolean debitSuccess = walletService.debit(user, BigDecimal.valueOf(walletAmountUsed), 
                    "Reserved credits for Merchant Order #" + order.getId(), "MERCHANT_ORDER");
                if (!debitSuccess) {
                    walletAmountUsed = 0.0;
                }
            }
        }

        order.setWalletAmountUsed(walletAmountUsed);
        double remainingAmount = finalAmount - walletAmountUsed;
        order.setRazorpayAmountPaid(0.0); // Will be updated on success

        if (remainingAmount <= 0) {
            // Fully paid via NXL Credits
            order.setOrderStatus("SUCCESS");
            merchantOrderRepository.save(order);
            
            // Deduct stock, generate invoice
            updateStockAndGenerateInvoice(order);
            
            // Log action
            auditLogRepository.save(new AuditLog("MERCHANT_ORDER_COMPLETED", 
                "Order #" + order.getId() + " fully paid via NXL Credits.", user.getEmail()));
        } else {
            merchantOrderRepository.save(order);
        }

        return order;
    }

    @Transactional
    public void confirmOrder(Long orderId, String paymentId, String signature, String razorpayOrderId) {
        MerchantOrder order = merchantOrderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

        if ("SUCCESS".equals(order.getOrderStatus())) {
            return; // Already completed
        }

        order.setOrderStatus("SUCCESS");
        order.setRazorpayPaymentId(paymentId);
        order.setRazorpaySignature(signature);
        if (razorpayOrderId != null) {
            order.setRazorpayOrderId(razorpayOrderId);
        }
        double remaining = order.getFinalAmount() - order.getWalletAmountUsed();
        order.setRazorpayAmountPaid(remaining);
        merchantOrderRepository.save(order);

        // Deduct stock, generate invoice
        updateStockAndGenerateInvoice(order);

        // Log action
        auditLogRepository.save(new AuditLog("MERCHANT_ORDER_COMPLETED", 
            "Order #" + order.getId() + " completed. Paid via NXL Credits + Razorpay.", order.getUser().getEmail()));
    }

    @Transactional
    public void rollbackOrder(Long orderId) {
        MerchantOrder order = merchantOrderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

        if (!"PENDING".equals(order.getOrderStatus())) {
            return; // Only pending orders can be rolled back
        }

        order.setOrderStatus("PAYMENT_FAILED");
        merchantOrderRepository.save(order);

        if (order.getWalletAmountUsed() > 0) {
            walletService.credit(order.getUser(), BigDecimal.valueOf(order.getWalletAmountUsed()), 
                "Rollback reserved credits for failed Merchant Order #" + order.getId(), "MERCHANT_ORDER_ROLLBACK");
        }

        // Log action
        auditLogRepository.save(new AuditLog("MERCHANT_ORDER_FAILED", 
            "Order #" + order.getId() + " payment failed. NXL Credits restored.", order.getUser().getEmail()));
    }

    private void updateStockAndGenerateInvoice(MerchantOrder order) {
        // Update product inventory
        for (MerchantOrderItem item : order.getItems()) {
            Product product = item.getProduct();
            int newStock = Math.max(0, product.getStock() - item.getQuantity());
            product.setStock(newStock);
            productRepository.save(product);
        }

        // Generate Invoice
        Invoice invoice = new Invoice();
        invoice.setMerchantOrder(order);
        invoice.setInvoiceNumber("INV-MERCH-" + System.currentTimeMillis() + "-" + order.getId());
        invoice.setOrderNumber("ORD-MERCH-" + order.getId());
        invoice.setSubtotal(order.getSubtotal());
        invoice.setProductDiscounts(order.getProductDiscounts());
        invoice.setBulkDiscounts(order.getBulkDiscounts());
        invoice.setTotalSavings(order.getTotalSavings());
        invoice.setWalletAmountUsed(order.getWalletAmountUsed());
        invoice.setRazorpayAmountPaid(order.getRazorpayAmountPaid());
        invoice.setFinalPayableAmount(order.getFinalAmount());
        
        invoiceRepository.save(invoice);
    }
}
