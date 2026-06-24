package com.llbeauty.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payouts")
public class Payout {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_profile_id", nullable = false)
    private AgentProfile agent;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "payment_method", nullable = false)
    private String paymentMethod; // UPI, Bank Transfer, Cash

    @Column(name = "utr_number", nullable = false)
    private String utrNumber;

    @Column(nullable = false)
    private String status = "PAID";

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "remarks")
    private String remarks;

    public Payout() {}

    public Payout(AgentProfile agent, BigDecimal amount, String paymentMethod, String utrNumber, String status) {
        this.agent = agent;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.utrNumber = utrNumber;
        this.status = status;
        this.createdAt = LocalDateTime.now();
    }

    public Payout(AgentProfile agent, BigDecimal amount, String paymentMethod, String utrNumber, String status, String remarks) {
        this.agent = agent;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.utrNumber = utrNumber;
        this.status = status;
        this.remarks = remarks;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public AgentProfile getAgent() { return agent; }
    public void setAgent(AgentProfile agent) { this.agent = agent; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getUtrNumber() { return utrNumber; }
    public void setUtrNumber(String utrNumber) { this.utrNumber = utrNumber; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
}
