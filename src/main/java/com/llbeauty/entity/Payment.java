package com.llbeauty.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String razorpayOrderId;
    private String razorpayPaymentId;
    private String razorpaySignature;

    private Double amount;
    private String status; // PENDING, SUCCESS, FAILED
    private String paymentMethod; // RAZORPAY, WALLET, RAZORPAY+WALLET
    private Double walletDeductionAmount = 0.0; // amount deducted from wallet before Razorpay
    private Double totalAmountPaid; // total amount finally paid (wallet + Razorpay)
    public Double getWalletDeductionAmount() {
        return walletDeductionAmount;
    }

    public void setWalletDeductionAmount(Double walletDeductionAmount) {
        this.walletDeductionAmount = walletDeductionAmount;
    }

    public Double getTotalAmountPaid() {
        return totalAmountPaid;
    }

    public void setTotalAmountPaid(Double totalAmountPaid) {
        this.totalAmountPaid = totalAmountPaid;
    }

    private String currency = "INR";

    private String paymentFor; // PRODUCT, MEMBERSHIP, WALLET_TOPUP, SALON_BOOKING

    private String referenceId; // To store Order ID, Appointment ID, etc.

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getRazorpayOrderId() { return razorpayOrderId; }
    public void setRazorpayOrderId(String razorpayOrderId) { this.razorpayOrderId = razorpayOrderId; }
    public String getRazorpayPaymentId() { return razorpayPaymentId; }
    public void setRazorpayPaymentId(String razorpayPaymentId) { this.razorpayPaymentId = razorpayPaymentId; }
    public String getRazorpaySignature() { return razorpaySignature; }
    public void setRazorpaySignature(String razorpaySignature) { this.razorpaySignature = razorpaySignature; }
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public String getPaymentFor() { return paymentFor; }
    public void setPaymentFor(String paymentFor) { this.paymentFor = paymentFor; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public String getReferenceId() { return referenceId; }
    public void setReferenceId(String referenceId) { this.referenceId = referenceId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
