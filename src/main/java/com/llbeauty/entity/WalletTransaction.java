package com.llbeauty.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "wallet_transactions")
public class WalletTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(precision = 10, scale = 2)
    private BigDecimal amount;

    private String type; // CREDIT, DEBIT, REFUND, TOPUP, PURCHASE
    private String source; // RAZORPAY_TOPUP, QR_REDEEM, etc.
    private String description;
    private Long paymentId;
    private Long orderId;
    private String status; // PENDING, SUCCESS, FAILED
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    private String razorpayOrderId;
    private String razorpayPaymentId;

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    public WalletTransaction() {
    }

    public WalletTransaction(Long id, User user, BigDecimal amount, String type, String source, String description, LocalDateTime createdAt, String razorpayOrderId, String razorpayPaymentId, Long paymentId, Long orderId, String status) {
        this.id = id;
        this.user = user;
        this.amount = amount;
        this.type = type;
        this.source = source;
        this.description = description;
        this.createdAt = createdAt;
        this.razorpayOrderId = razorpayOrderId;
        this.razorpayPaymentId = razorpayPaymentId;
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.status = status;
    }

    // Getters and Setters
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

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getRazorpayOrderId() {
        return razorpayOrderId;
    }

    public void setRazorpayOrderId(String razorpayOrderId) {
        this.razorpayOrderId = razorpayOrderId;
    }

    public Long getPaymentId() { return paymentId; }
    public void setPaymentId(Long paymentId) { this.paymentId = paymentId; }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getRazorpayPaymentId() {
        return razorpayPaymentId;
    }

    public void setRazorpayPaymentId(String razorpayPaymentId) {
        this.razorpayPaymentId = razorpayPaymentId;
    }

    // Simple Builder Pattern matching the old code style
    public static class WalletTransactionBuilder {
        private Long id;
        private User user;
        private BigDecimal amount;
        private String type;
        private String source;
        private String description;
        private LocalDateTime createdAt;
        private String razorpayOrderId;
        private String razorpayPaymentId;
        private Long paymentId;
        private Long orderId;
        private String status;

        WalletTransactionBuilder() {}

        public WalletTransactionBuilder id(Long id) { this.id = id; return this; }
        public WalletTransactionBuilder user(User user) { this.user = user; return this; }
        public WalletTransactionBuilder amount(BigDecimal amount) { this.amount = amount; return this; }
        public WalletTransactionBuilder type(String type) { this.type = type; return this; }
        public WalletTransactionBuilder source(String source) { this.source = source; return this; }
        public WalletTransactionBuilder description(String description) { this.description = description; return this; }
        public WalletTransactionBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public WalletTransactionBuilder razorpayOrderId(String razorpayOrderId) { this.razorpayOrderId = razorpayOrderId; return this; }
        public WalletTransactionBuilder razorpayPaymentId(String razorpayPaymentId) { this.razorpayPaymentId = razorpayPaymentId; return this; }
        public WalletTransactionBuilder paymentId(Long paymentId) { this.paymentId = paymentId; return this; }
        public WalletTransactionBuilder orderId(Long orderId) { this.orderId = orderId; return this; }
        public WalletTransactionBuilder status(String status) { this.status = status; return this; }

        public WalletTransaction build() {
            return new WalletTransaction(id, user, amount, type, source, description, createdAt, razorpayOrderId, razorpayPaymentId, paymentId, orderId, status);
        }
    }

    public static WalletTransactionBuilder builder() {
        return new WalletTransactionBuilder();
    }
}
