package com.llbeauty.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "franchise_leads")
public class FranchiseLead {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String mobile;
    private String email;
    private String city;
    private String state;
    private String budget;
    private String preferredLocation;
    private String franchiseType; // Kiosk, Studio, Lounge
    private String businessType;
    private String experience;
    private String message;
    private String status; // NEW, CONTACTED, INTERESTED, CONVERTED, REJECTED
    private String remarks;
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = "NEW";
        }
    }


    @java.lang.SuppressWarnings("all")
    
    public static class FranchiseLeadBuilder {
        @java.lang.SuppressWarnings("all")
        
        private Long id;
        @java.lang.SuppressWarnings("all")
        
        private String name;
        @java.lang.SuppressWarnings("all")
        
        private String mobile;
        @java.lang.SuppressWarnings("all")
        
        private String email;
        @java.lang.SuppressWarnings("all")
        
        private String city;
        @java.lang.SuppressWarnings("all")
        
        private String budget;
        @java.lang.SuppressWarnings("all")
        
        private String preferredLocation;
        @java.lang.SuppressWarnings("all")
        
        private String franchiseType;
        @java.lang.SuppressWarnings("all")
        
        private LocalDateTime createdAt;

        @java.lang.SuppressWarnings("all")
        
        FranchiseLeadBuilder() {
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        
        public FranchiseLead.FranchiseLeadBuilder id(final Long id) {
            this.id = id;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        
        public FranchiseLead.FranchiseLeadBuilder name(final String name) {
            this.name = name;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        
        public FranchiseLead.FranchiseLeadBuilder mobile(final String mobile) {
            this.mobile = mobile;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        
        public FranchiseLead.FranchiseLeadBuilder email(final String email) {
            this.email = email;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        
        public FranchiseLead.FranchiseLeadBuilder city(final String city) {
            this.city = city;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        
        public FranchiseLead.FranchiseLeadBuilder budget(final String budget) {
            this.budget = budget;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        
        public FranchiseLead.FranchiseLeadBuilder preferredLocation(final String preferredLocation) {
            this.preferredLocation = preferredLocation;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        
        public FranchiseLead.FranchiseLeadBuilder franchiseType(final String franchiseType) {
            this.franchiseType = franchiseType;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        
        public FranchiseLead.FranchiseLeadBuilder createdAt(final LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        @java.lang.SuppressWarnings("all")
        
        public FranchiseLead build() {
            return new FranchiseLead(this.id, this.name, this.mobile, this.email, this.city, this.budget, this.preferredLocation, this.franchiseType, this.createdAt);
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        
        public java.lang.String toString() {
            return "FranchiseLead.FranchiseLeadBuilder(id=" + this.id + ", name=" + this.name + ", mobile=" + this.mobile + ", email=" + this.email + ", city=" + this.city + ", budget=" + this.budget + ", preferredLocation=" + this.preferredLocation + ", franchiseType=" + this.franchiseType + ", createdAt=" + this.createdAt + ")";
        }
    }

    @java.lang.SuppressWarnings("all")
    
    public static FranchiseLead.FranchiseLeadBuilder builder() {
        return new FranchiseLead.FranchiseLeadBuilder();
    }

    @java.lang.SuppressWarnings("all")
    
    public Long getId() {
        return this.id;
    }

    @java.lang.SuppressWarnings("all")
    
    public String getName() {
        return this.name;
    }

    @java.lang.SuppressWarnings("all")
    
    public String getMobile() {
        return this.mobile;
    }

    @java.lang.SuppressWarnings("all")
    
    public String getEmail() {
        return this.email;
    }

    @java.lang.SuppressWarnings("all")
    
    public String getCity() {
        return this.city;
    }

    @java.lang.SuppressWarnings("all")
    
    public String getBudget() {
        return this.budget;
    }

    @java.lang.SuppressWarnings("all")
    
    public String getPreferredLocation() {
        return this.preferredLocation;
    }

    @java.lang.SuppressWarnings("all")
    
    public String getFranchiseType() {
        return this.franchiseType;
    }

    @java.lang.SuppressWarnings("all")
    
    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    @java.lang.SuppressWarnings("all")
    
    public void setId(final Long id) {
        this.id = id;
    }

    @java.lang.SuppressWarnings("all")
    
    public void setName(final String name) {
        this.name = name;
    }

    @java.lang.SuppressWarnings("all")
    
    public void setMobile(final String mobile) {
        this.mobile = mobile;
    }

    @java.lang.SuppressWarnings("all")
    
    public void setEmail(final String email) {
        this.email = email;
    }

    @java.lang.SuppressWarnings("all")
    
    public void setCity(final String city) {
        this.city = city;
    }

    @java.lang.SuppressWarnings("all")
    
    public void setBudget(final String budget) {
        this.budget = budget;
    }

    @java.lang.SuppressWarnings("all")
    
    public void setPreferredLocation(final String preferredLocation) {
        this.preferredLocation = preferredLocation;
    }

    @java.lang.SuppressWarnings("all")
    
    public void setFranchiseType(final String franchiseType) {
        this.franchiseType = franchiseType;
    }

    public String getState() { return this.state; }
    public void setState(String state) { this.state = state; }

    public String getBusinessType() { return this.businessType; }
    public void setBusinessType(String businessType) { this.businessType = businessType; }

    public String getExperience() { return this.experience; }
    public void setExperience(String experience) { this.experience = experience; }

    public String getMessage() { return this.message; }
    public void setMessage(String message) { this.message = message; }

    public String getStatus() { return this.status; }
    public void setStatus(String status) { this.status = status; }

    public String getRemarks() { return this.remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public void setCreatedAt(final LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    
    public boolean equals(final java.lang.Object o) {
        if (o == this) return true;
        if (!(o instanceof FranchiseLead)) return false;
        final FranchiseLead other = (FranchiseLead) o;
        if (!other.canEqual((java.lang.Object) this)) return false;
        final java.lang.Object this$id = this.getId();
        final java.lang.Object other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
        final java.lang.Object this$name = this.getName();
        final java.lang.Object other$name = other.getName();
        if (this$name == null ? other$name != null : !this$name.equals(other$name)) return false;
        final java.lang.Object this$mobile = this.getMobile();
        final java.lang.Object other$mobile = other.getMobile();
        if (this$mobile == null ? other$mobile != null : !this$mobile.equals(other$mobile)) return false;
        final java.lang.Object this$email = this.getEmail();
        final java.lang.Object other$email = other.getEmail();
        if (this$email == null ? other$email != null : !this$email.equals(other$email)) return false;
        final java.lang.Object this$city = this.getCity();
        final java.lang.Object other$city = other.getCity();
        if (this$city == null ? other$city != null : !this$city.equals(other$city)) return false;
        final java.lang.Object this$budget = this.getBudget();
        final java.lang.Object other$budget = other.getBudget();
        if (this$budget == null ? other$budget != null : !this$budget.equals(other$budget)) return false;
        final java.lang.Object this$preferredLocation = this.getPreferredLocation();
        final java.lang.Object other$preferredLocation = other.getPreferredLocation();
        if (this$preferredLocation == null ? other$preferredLocation != null : !this$preferredLocation.equals(other$preferredLocation)) return false;
        final java.lang.Object this$franchiseType = this.getFranchiseType();
        final java.lang.Object other$franchiseType = other.getFranchiseType();
        if (this$franchiseType == null ? other$franchiseType != null : !this$franchiseType.equals(other$franchiseType)) return false;
        final java.lang.Object this$createdAt = this.getCreatedAt();
        final java.lang.Object other$createdAt = other.getCreatedAt();
        if (this$createdAt == null ? other$createdAt != null : !this$createdAt.equals(other$createdAt)) return false;
        return true;
    }

    @java.lang.SuppressWarnings("all")
    
    protected boolean canEqual(final java.lang.Object other) {
        return other instanceof FranchiseLead;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final java.lang.Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        final java.lang.Object $name = this.getName();
        result = result * PRIME + ($name == null ? 43 : $name.hashCode());
        final java.lang.Object $mobile = this.getMobile();
        result = result * PRIME + ($mobile == null ? 43 : $mobile.hashCode());
        final java.lang.Object $email = this.getEmail();
        result = result * PRIME + ($email == null ? 43 : $email.hashCode());
        final java.lang.Object $city = this.getCity();
        result = result * PRIME + ($city == null ? 43 : $city.hashCode());
        final java.lang.Object $budget = this.getBudget();
        result = result * PRIME + ($budget == null ? 43 : $budget.hashCode());
        final java.lang.Object $preferredLocation = this.getPreferredLocation();
        result = result * PRIME + ($preferredLocation == null ? 43 : $preferredLocation.hashCode());
        final java.lang.Object $franchiseType = this.getFranchiseType();
        result = result * PRIME + ($franchiseType == null ? 43 : $franchiseType.hashCode());
        final java.lang.Object $createdAt = this.getCreatedAt();
        result = result * PRIME + ($createdAt == null ? 43 : $createdAt.hashCode());
        return result;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    
    public java.lang.String toString() {
        return "FranchiseLead(id=" + this.getId() + ", name=" + this.getName() + ", mobile=" + this.getMobile() + ", email=" + this.getEmail() + ", city=" + this.getCity() + ", budget=" + this.getBudget() + ", preferredLocation=" + this.getPreferredLocation() + ", franchiseType=" + this.getFranchiseType() + ", createdAt=" + this.getCreatedAt() + ")";
    }

    @java.lang.SuppressWarnings("all")
    
    public FranchiseLead() {
    }

    @java.lang.SuppressWarnings("all")
    
    public FranchiseLead(final Long id, final String name, final String mobile, final String email, final String city, final String budget, final String preferredLocation, final String franchiseType, final LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.mobile = mobile;
        this.email = email;
        this.city = city;
        this.budget = budget;
        this.preferredLocation = preferredLocation;
        this.franchiseType = franchiseType;
        this.createdAt = createdAt;
    }
}
