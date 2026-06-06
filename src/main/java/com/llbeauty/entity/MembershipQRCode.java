package com.llbeauty.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "membership_qrcodes")
public class MembershipQRCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_membership_id", nullable = false)
    private UserMembership userMembership;

    @Column(name = "qr_url", nullable = false)
    private String qrUrl;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public MembershipQRCode() {
    }

    public MembershipQRCode(UserMembership userMembership, String qrUrl) {
        this.userMembership = userMembership;
        this.qrUrl = qrUrl;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserMembership getUserMembership() {
        return userMembership;
    }

    public void setUserMembership(UserMembership userMembership) {
        this.userMembership = userMembership;
    }

    public String getQrUrl() {
        return qrUrl;
    }

    public void setQrUrl(String qrUrl) {
        this.qrUrl = qrUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
