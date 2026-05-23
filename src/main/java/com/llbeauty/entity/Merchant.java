package com.llbeauty.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "merchants")
public class Merchant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String contact;
    private String location;
    private String status;
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }


    @java.lang.SuppressWarnings("all")
    
    public static class MerchantBuilder {
        @java.lang.SuppressWarnings("all")
        
        private Long id;
        @java.lang.SuppressWarnings("all")
        
        private String name;
        @java.lang.SuppressWarnings("all")
        
        private String contact;
        @java.lang.SuppressWarnings("all")
        
        private String location;
        @java.lang.SuppressWarnings("all")
        
        private String status;
        @java.lang.SuppressWarnings("all")
        
        private LocalDateTime createdAt;

        @java.lang.SuppressWarnings("all")
        
        MerchantBuilder() {
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        
        public Merchant.MerchantBuilder id(final Long id) {
            this.id = id;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        
        public Merchant.MerchantBuilder name(final String name) {
            this.name = name;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        
        public Merchant.MerchantBuilder contact(final String contact) {
            this.contact = contact;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        
        public Merchant.MerchantBuilder location(final String location) {
            this.location = location;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        
        public Merchant.MerchantBuilder status(final String status) {
            this.status = status;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        
        public Merchant.MerchantBuilder createdAt(final LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        @java.lang.SuppressWarnings("all")
        
        public Merchant build() {
            return new Merchant(this.id, this.name, this.contact, this.location, this.status, this.createdAt);
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        
        public java.lang.String toString() {
            return "Merchant.MerchantBuilder(id=" + this.id + ", name=" + this.name + ", contact=" + this.contact + ", location=" + this.location + ", status=" + this.status + ", createdAt=" + this.createdAt + ")";
        }
    }

    @java.lang.SuppressWarnings("all")
    
    public static Merchant.MerchantBuilder builder() {
        return new Merchant.MerchantBuilder();
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
    
    public String getContact() {
        return this.contact;
    }

    @java.lang.SuppressWarnings("all")
    
    public String getLocation() {
        return this.location;
    }

    @java.lang.SuppressWarnings("all")
    
    public String getStatus() {
        return this.status;
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
    
    public void setContact(final String contact) {
        this.contact = contact;
    }

    @java.lang.SuppressWarnings("all")
    
    public void setLocation(final String location) {
        this.location = location;
    }

    @java.lang.SuppressWarnings("all")
    
    public void setStatus(final String status) {
        this.status = status;
    }

    @java.lang.SuppressWarnings("all")
    
    public void setCreatedAt(final LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    
    public boolean equals(final java.lang.Object o) {
        if (o == this) return true;
        if (!(o instanceof Merchant)) return false;
        final Merchant other = (Merchant) o;
        if (!other.canEqual((java.lang.Object) this)) return false;
        final java.lang.Object this$id = this.getId();
        final java.lang.Object other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
        final java.lang.Object this$name = this.getName();
        final java.lang.Object other$name = other.getName();
        if (this$name == null ? other$name != null : !this$name.equals(other$name)) return false;
        final java.lang.Object this$contact = this.getContact();
        final java.lang.Object other$contact = other.getContact();
        if (this$contact == null ? other$contact != null : !this$contact.equals(other$contact)) return false;
        final java.lang.Object this$location = this.getLocation();
        final java.lang.Object other$location = other.getLocation();
        if (this$location == null ? other$location != null : !this$location.equals(other$location)) return false;
        final java.lang.Object this$status = this.getStatus();
        final java.lang.Object other$status = other.getStatus();
        if (this$status == null ? other$status != null : !this$status.equals(other$status)) return false;
        final java.lang.Object this$createdAt = this.getCreatedAt();
        final java.lang.Object other$createdAt = other.getCreatedAt();
        if (this$createdAt == null ? other$createdAt != null : !this$createdAt.equals(other$createdAt)) return false;
        return true;
    }

    @java.lang.SuppressWarnings("all")
    
    protected boolean canEqual(final java.lang.Object other) {
        return other instanceof Merchant;
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
        final java.lang.Object $contact = this.getContact();
        result = result * PRIME + ($contact == null ? 43 : $contact.hashCode());
        final java.lang.Object $location = this.getLocation();
        result = result * PRIME + ($location == null ? 43 : $location.hashCode());
        final java.lang.Object $status = this.getStatus();
        result = result * PRIME + ($status == null ? 43 : $status.hashCode());
        final java.lang.Object $createdAt = this.getCreatedAt();
        result = result * PRIME + ($createdAt == null ? 43 : $createdAt.hashCode());
        return result;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    
    public java.lang.String toString() {
        return "Merchant(id=" + this.getId() + ", name=" + this.getName() + ", contact=" + this.getContact() + ", location=" + this.getLocation() + ", status=" + this.getStatus() + ", createdAt=" + this.getCreatedAt() + ")";
    }

    @java.lang.SuppressWarnings("all")
    
    public Merchant() {
    }

    @java.lang.SuppressWarnings("all")
    
    public Merchant(final Long id, final String name, final String contact, final String location, final String status, final LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.contact = contact;
        this.location = location;
        this.status = status;
        this.createdAt = createdAt;
    }
}
