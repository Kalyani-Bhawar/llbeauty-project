package com.llbeauty.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "memberships")
public class Membership {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Double price;

    @Column(name = "cashback_percent", nullable = false)
    private Double cashbackPercent;

    @Column(columnDefinition = "TEXT")
    private String benefits;

    @Column(name = "duration_days", nullable = false)
    private Integer durationDays;

    @Column(name = "duration_months", nullable = false, columnDefinition = "INT DEFAULT 1")
    private Integer durationMonths = 1;

    @Column(name = "welcome_credits", nullable = false)
    private Double welcomeCredits;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean active = true;

    public Membership() {
    }

    public Membership(Long id, String name, Double price, Double cashbackPercent, String benefits, Integer durationDays, Integer durationMonths, Double welcomeCredits, Boolean active) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.cashbackPercent = cashbackPercent;
        this.benefits = benefits;
        this.durationDays = durationDays;
        this.durationMonths = durationMonths;
        this.welcomeCredits = welcomeCredits;
        this.active = active;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getCashbackPercent() {
        return cashbackPercent;
    }

    public void setCashbackPercent(Double cashbackPercent) {
        this.cashbackPercent = cashbackPercent;
    }

    public String getBenefits() {
        return benefits;
    }

    public void setBenefits(String benefits) {
        this.benefits = benefits;
    }

    public Integer getDurationDays() {
        return durationDays;
    }

    public void setDurationDays(Integer durationDays) {
        this.durationDays = durationDays;
    }

    public Double getWelcomeCredits() {
        return welcomeCredits;
    }

    public void setWelcomeCredits(Double welcomeCredits) {
        this.welcomeCredits = welcomeCredits;
    }

    public Integer getDurationMonths() {
        return durationMonths;
    }

    public void setDurationMonths(Integer durationMonths) {
        this.durationMonths = durationMonths;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
