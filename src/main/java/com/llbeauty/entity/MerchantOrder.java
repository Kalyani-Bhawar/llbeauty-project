package com.llbeauty.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "merchant_orders")
public class MerchantOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "subtotal", nullable = false)
    private Double subtotal = 0.0;

    @Column(name = "product_discounts", nullable = false)
    private Double productDiscounts = 0.0;

    @Column(name = "bulk_discounts", nullable = false)
    private Double bulkDiscounts = 0.0;

    @Column(name = "total_savings", nullable = false)
    private Double totalSavings = 0.0;

    @Column(name = "final_amount", nullable = false)
    private Double finalAmount = 0.0;

    @Column(name = "wallet_amount_used", nullable = false)
    private Double walletAmountUsed = 0.0;

    @Column(name = "razorpay_amount_paid", nullable = false)
    private Double razorpayAmountPaid = 0.0;

    @Column(name = "razorpay_order_id")
    private String razorpayOrderId;

    @Column(name = "razorpay_payment_id")
    private String razorpayPaymentId;

    @Column(name = "razorpay_signature")
    private String razorpaySignature;

    @Column(name = "order_status", nullable = false, length = 20)
    private String orderStatus = "PENDING"; // PENDING, SUCCESS, PAYMENT_FAILED

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "merchantOrder", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<MerchantOrderItem> items = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.orderStatus == null) {
            this.orderStatus = "PENDING";
        }
    }

    public MerchantOrder() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public Double getFinalAmount() {
        return finalAmount;
    }

    public void setFinalAmount(Double finalAmount) {
        this.finalAmount = finalAmount;
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

    public String getRazorpayOrderId() {
        return razorpayOrderId;
    }

    public void setRazorpayOrderId(String razorpayOrderId) {
        this.razorpayOrderId = razorpayOrderId;
    }

    public String getRazorpayPaymentId() {
        return razorpayPaymentId;
    }

    public void setRazorpayPaymentId(String razorpayPaymentId) {
        this.razorpayPaymentId = razorpayPaymentId;
    }

    public String getRazorpaySignature() {
        return razorpaySignature;
    }

    public void setRazorpaySignature(String razorpaySignature) {
        this.razorpaySignature = razorpaySignature;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<MerchantOrderItem> getItems() {
        return items;
    }

    public void setItems(List<MerchantOrderItem> items) {
        this.items = items;
    }
}
