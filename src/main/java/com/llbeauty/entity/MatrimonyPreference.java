package com.llbeauty.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "matrimony_preferences", indexes = {
    @Index(name = "idx_mat_pref_user", columnList = "user_id")
})
public class MatrimonyPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "minimum_age")
    private Integer minimumAge;

    @Column(name = "maximum_age")
    private Integer maximumAge;

    @Column(name = "preferred_city")
    private String preferredCity;

    @Column(name = "preferred_education")
    private String preferredEducation;

    @Column(name = "preferred_religion")
    private String preferredReligion;

    @Column(name = "preferred_occupation")
    private String preferredOccupation;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    public MatrimonyPreference() {
    }

    public MatrimonyPreference(User user, Integer minimumAge, Integer maximumAge, String preferredCity,
                               String preferredEducation, String preferredReligion, String preferredOccupation) {
        this.user = user;
        this.minimumAge = minimumAge;
        this.maximumAge = maximumAge;
        this.preferredCity = preferredCity;
        this.preferredEducation = preferredEducation;
        this.preferredReligion = preferredReligion;
        this.preferredOccupation = preferredOccupation;
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

    public Integer getMinimumAge() {
        return minimumAge;
    }

    public void setMinimumAge(Integer minimumAge) {
        this.minimumAge = minimumAge;
    }

    public Integer getMaximumAge() {
        return maximumAge;
    }

    public void setMaximumAge(Integer maximumAge) {
        this.maximumAge = maximumAge;
    }

    public String getPreferredCity() {
        return preferredCity;
    }

    public void setPreferredCity(String preferredCity) {
        this.preferredCity = preferredCity;
    }

    public String getPreferredEducation() {
        return preferredEducation;
    }

    public void setPreferredEducation(String preferredEducation) {
        this.preferredEducation = preferredEducation;
    }

    public String getPreferredReligion() {
        return preferredReligion;
    }

    public void setPreferredReligion(String preferredReligion) {
        this.preferredReligion = preferredReligion;
    }

    public String getPreferredOccupation() {
        return preferredOccupation;
    }

    public void setPreferredOccupation(String preferredOccupation) {
        this.preferredOccupation = preferredOccupation;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
