package com.llbeauty.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private Double totalAmount;
    private String status; // PENDING, SUCCESS, FAILED
    private String orderStatus; // Pending, Confirmed, Packed, Shipped, Delivered, Cancelled, Refunded
    private String orderType; // CUSTOMER, WHOLESALE
    private String paymentId;
    private String razorpayOrderId;
    private LocalDateTime createdAt;
    @Column(name = "referral_code")
    private String referralCode;

    public String getReferralCode() {
		return referralCode;
	}

	public void setReferralCode(String referralCode) {
		this.referralCode = referralCode;
	}

	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> items = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    public static class OrderBuilder {
        private Long id;
        private User user;
        private Double totalAmount;
        private String status;
        private String orderStatus;
        private String paymentId;
        private LocalDateTime createdAt;

        OrderBuilder() {}

        public OrderBuilder id(Long id) { this.id = id; return this; }
        public OrderBuilder user(User user) { this.user = user; return this; }
        public OrderBuilder totalAmount(Double totalAmount) { this.totalAmount = totalAmount; return this; }
        public OrderBuilder status(String status) { this.status = status; return this; }
        public OrderBuilder orderStatus(String orderStatus) { this.orderStatus = orderStatus; return this; }
        public OrderBuilder paymentId(String paymentId) { this.paymentId = paymentId; return this; }
        public OrderBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public Order build() { return new Order(id, user, totalAmount, status, orderStatus, paymentId, createdAt); }

        @Override
        public String toString() {
            return "Order.OrderBuilder(id=" + id + ", user=" + user + ", totalAmount=" + totalAmount + ", status=" + status + ", orderStatus=" + orderStatus + ", paymentId=" + paymentId + ", createdAt=" + createdAt + ")";
        }
    }

    public static OrderBuilder builder() { return new OrderBuilder(); }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getOrderStatus() { return orderStatus; }
    public void setOrderStatus(String orderStatus) { this.orderStatus = orderStatus; }
    public String getOrderType() { return orderType; }
    public void setOrderType(String orderType) { this.orderType = orderType; }
    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }
    public String getRazorpayOrderId() { return razorpayOrderId; }
    public void setRazorpayOrderId(String razorpayOrderId) { this.razorpayOrderId = razorpayOrderId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }

    public Order() {}

    public Order(Long id, User user, Double totalAmount, String status, String orderStatus, String paymentId, LocalDateTime createdAt) {
        this.id = id;
        this.user = user;
        this.totalAmount = totalAmount;
        this.status = status;
        this.orderStatus = orderStatus;
        this.paymentId = paymentId;
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Order)) return false;
        Order other = (Order) o;
        if (!other.canEqual(this)) return false;
        return (id != null ? id.equals(other.id) : other.id == null) &&
               (totalAmount != null ? totalAmount.equals(other.totalAmount) : other.totalAmount == null) &&
               (user != null ? user.equals(other.user) : other.user == null) &&
               (status != null ? status.equals(other.status) : other.status == null) &&
               (orderStatus != null ? orderStatus.equals(other.orderStatus) : other.orderStatus == null) &&
               (paymentId != null ? paymentId.equals(other.paymentId) : other.paymentId == null) &&
               (createdAt != null ? createdAt.equals(other.createdAt) : other.createdAt == null);
    }

    protected boolean canEqual(Object other) { return other instanceof Order; }

    @Override
    public int hashCode() {
        int result = 1;
        final int PRIME = 59;
        result = result * PRIME + (id == null ? 43 : id.hashCode());
        result = result * PRIME + (totalAmount == null ? 43 : totalAmount.hashCode());
        result = result * PRIME + (user == null ? 43 : user.hashCode());
        result = result * PRIME + (status == null ? 43 : status.hashCode());
        result = result * PRIME + (orderStatus == null ? 43 : orderStatus.hashCode());
        result = result * PRIME + (paymentId == null ? 43 : paymentId.hashCode());
        result = result * PRIME + (createdAt == null ? 43 : createdAt.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "Order(id=" + id + ", user=" + user + ", totalAmount=" + totalAmount + ", status=" + status + ", orderStatus=" + orderStatus + ", paymentId=" + paymentId + ", createdAt=" + createdAt + ")";
    }
}
