package com.llbeauty.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "invoices")
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "invoice_number", nullable = false, unique = true, length = 50)
    private String invoiceNumber;

    @Column(name = "order_number", nullable = false, length = 50)
    private String orderNumber;

    @Column(name = "date", nullable = false)
    private LocalDateTime date;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_order_id", nullable = false)
    private MerchantOrder merchantOrder;

    @Column(name = "subtotal", nullable = false)
    private Double subtotal = 0.0;

    @Column(name = "product_discounts", nullable = false)
    private Double productDiscounts = 0.0;

    @Column(name = "bulk_discounts", nullable = false)
    private Double bulkDiscounts = 0.0;

    @Column(name = "total_savings", nullable = false)
    private Double totalSavings = 0.0;

    @Column(name = "wallet_amount_used", nullable = false)
    private Double walletAmountUsed = 0.0;

    @Column(name = "razorpay_amount_paid", nullable = false)
    private Double razorpayAmountPaid = 0.0;

    @Column(name = "final_payable_amount", nullable = false)
    private Double finalPayableAmount = 0.0;

    @PrePersist
    public void prePersist() {
        if (this.date == null) {
            this.date = LocalDateTime.now();
        }
    }

    public Invoice() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public MerchantOrder getMerchantOrder() {
        return merchantOrder;
    }

    public void setMerchantOrder(MerchantOrder merchantOrder) {
        this.merchantOrder = merchantOrder;
    }

    public Double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Double subtotal) {
        this.subtotal = subtotal;
    }

    public Double getProductDiscounts() {
        return productDiscounts;
    }

    public void setProductDiscounts(Double productDiscounts) {
        this.productDiscounts = productDiscounts;
    }

    public Double getBulkDiscounts() {
        return bulkDiscounts;
    }

    public void setBulkDiscounts(Double bulkDiscounts) {
        this.bulkDiscounts = bulkDiscounts;
    }

    public Double getTotalSavings() {
        return totalSavings;
    }

    public void setTotalSavings(Double totalSavings) {
        this.totalSavings = totalSavings;
    }

    public Double getWalletAmountUsed() {
        return walletAmountUsed;
    }

    public void setWalletAmountUsed(Double walletAmountUsed) {
        this.walletAmountUsed = walletAmountUsed;
    }

    public Double getRazorpayAmountPaid() {
        return razorpayAmountPaid;
    }

    public void setRazorpayAmountPaid(Double razorpayAmountPaid) {
        this.razorpayAmountPaid = razorpayAmountPaid;
    }

    public Double getFinalPayableAmount() {
        return finalPayableAmount;
    }

    public void setFinalPayableAmount(Double finalPayableAmount) {
        this.finalPayableAmount = finalPayableAmount;
    }
}
