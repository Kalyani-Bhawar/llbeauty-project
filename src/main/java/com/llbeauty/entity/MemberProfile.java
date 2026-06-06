package com.llbeauty.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "member_profiles")
public class MemberProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "member_id", unique = true, nullable = false)
    private String memberId;

    @Column(name = "uuid", unique = true, nullable = false)
    private String uuid;

    @Column(name = "membership_type", nullable = false)
    private String membershipType;

    @Column(name = "join_date", nullable = false)
    private LocalDateTime joinDate;

    public MemberProfile() {
    }

    public MemberProfile(User user, String memberId, String uuid, String membershipType, LocalDateTime joinDate) {
        this.user = user;
        this.memberId = memberId;
        this.uuid = uuid;
        this.membershipType = membershipType;
        this.joinDate = joinDate;
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

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getMembershipType() {
        return membershipType;
    }

    public void setMembershipType(String membershipType) {
        this.membershipType = membershipType;
    }

    public LocalDateTime getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(LocalDateTime joinDate) {
        this.joinDate = joinDate;
    }
}
