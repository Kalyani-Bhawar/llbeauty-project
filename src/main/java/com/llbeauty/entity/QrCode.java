package com.llbeauty.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "qr_codes")
public class QrCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "merchant_id")
    private Merchant merchant;
    private String qrData;
    private String status; // ACTIVE, USED
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }


    @java.lang.SuppressWarnings("all")
    
    public static class QrCodeBuilder {
        @java.lang.SuppressWarnings("all")
        
        private Long id;
        @java.lang.SuppressWarnings("all")
        
        private Merchant merchant;
        @java.lang.SuppressWarnings("all")
        
        private String qrData;
        @java.lang.SuppressWarnings("all")
        
        private String status;
        @java.lang.SuppressWarnings("all")
        
        private LocalDateTime createdAt;

        @java.lang.SuppressWarnings("all")
        
        QrCodeBuilder() {
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        
        public QrCode.QrCodeBuilder id(final Long id) {
            this.id = id;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        
        public QrCode.QrCodeBuilder merchant(final Merchant merchant) {
            this.merchant = merchant;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        
        public QrCode.QrCodeBuilder qrData(final String qrData) {
            this.qrData = qrData;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        
        public QrCode.QrCodeBuilder status(final String status) {
            this.status = status;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        
        public QrCode.QrCodeBuilder createdAt(final LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        @java.lang.SuppressWarnings("all")
        
        public QrCode build() {
            return new QrCode(this.id, this.merchant, this.qrData, this.status, this.createdAt);
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        
        public java.lang.String toString() {
            return "QrCode.QrCodeBuilder(id=" + this.id + ", merchant=" + this.merchant + ", qrData=" + this.qrData + ", status=" + this.status + ", createdAt=" + this.createdAt + ")";
        }
    }

    @java.lang.SuppressWarnings("all")
    
    public static QrCode.QrCodeBuilder builder() {
        return new QrCode.QrCodeBuilder();
    }

    @java.lang.SuppressWarnings("all")
    
    public Long getId() {
        return this.id;
    }

    @java.lang.SuppressWarnings("all")
    
    public Merchant getMerchant() {
        return this.merchant;
    }

    @java.lang.SuppressWarnings("all")
    
    public String getQrData() {
        return this.qrData;
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
    
    public void setMerchant(final Merchant merchant) {
        this.merchant = merchant;
    }

    @java.lang.SuppressWarnings("all")
    
    public void setQrData(final String qrData) {
        this.qrData = qrData;
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
        if (!(o instanceof QrCode)) return false;
        final QrCode other = (QrCode) o;
        if (!other.canEqual((java.lang.Object) this)) return false;
        final java.lang.Object this$id = this.getId();
        final java.lang.Object other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
        final java.lang.Object this$merchant = this.getMerchant();
        final java.lang.Object other$merchant = other.getMerchant();
        if (this$merchant == null ? other$merchant != null : !this$merchant.equals(other$merchant)) return false;
        final java.lang.Object this$qrData = this.getQrData();
        final java.lang.Object other$qrData = other.getQrData();
        if (this$qrData == null ? other$qrData != null : !this$qrData.equals(other$qrData)) return false;
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
        return other instanceof QrCode;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final java.lang.Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        final java.lang.Object $merchant = this.getMerchant();
        result = result * PRIME + ($merchant == null ? 43 : $merchant.hashCode());
        final java.lang.Object $qrData = this.getQrData();
        result = result * PRIME + ($qrData == null ? 43 : $qrData.hashCode());
        final java.lang.Object $status = this.getStatus();
        result = result * PRIME + ($status == null ? 43 : $status.hashCode());
        final java.lang.Object $createdAt = this.getCreatedAt();
        result = result * PRIME + ($createdAt == null ? 43 : $createdAt.hashCode());
        return result;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    
    public java.lang.String toString() {
        return "QrCode(id=" + this.getId() + ", merchant=" + this.getMerchant() + ", qrData=" + this.getQrData() + ", status=" + this.getStatus() + ", createdAt=" + this.getCreatedAt() + ")";
    }

    @java.lang.SuppressWarnings("all")
    
    public QrCode() {
    }

    @java.lang.SuppressWarnings("all")
    
    public QrCode(final Long id, final Merchant merchant, final String qrData, final String status, final LocalDateTime createdAt) {
        this.id = id;
        this.merchant = merchant;
        this.qrData = qrData;
        this.status = status;
        this.createdAt = createdAt;
    }
}
