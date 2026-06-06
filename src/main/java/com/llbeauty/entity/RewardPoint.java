package com.llbeauty.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "reward_points")
public class RewardPoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "available_points", nullable = false)
    private Integer availablePoints = 0;

    @Column(name = "total_points", nullable = false)
    private Integer totalPoints = 0;

    @Column(name = "redeemed_points", nullable = false)
    private Integer redeemedPoints = 0;

    public RewardPoint() {
    }

    public RewardPoint(User user, Integer availablePoints, Integer totalPoints, Integer redeemedPoints) {
        this.user = user;
        this.availablePoints = availablePoints;
        this.totalPoints = totalPoints;
        this.redeemedPoints = redeemedPoints;
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

    public Integer getAvailablePoints() {
        return availablePoints;
    }

    public void setAvailablePoints(Integer availablePoints) {
        this.availablePoints = availablePoints;
    }

    public Integer getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(Integer totalPoints) {
        this.totalPoints = totalPoints;
    }

    public Integer getRedeemedPoints() {
        return redeemedPoints;
    }

    public void setRedeemedPoints(Integer redeemedPoints) {
        this.redeemedPoints = redeemedPoints;
    }
}
