package com.llbeauty.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "otps")
public class Otp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private String code;
    @Column(nullable = false)
    private LocalDateTime expiresAt;
    private boolean used;

    @PrePersist
    public void prePersist() {
        this.used = false;
    }


    @java.lang.SuppressWarnings("all")
    public static class OtpBuilder {
        @java.lang.SuppressWarnings("all")
        private Long id;
        @java.lang.SuppressWarnings("all")
        private String email;
        @java.lang.SuppressWarnings("all")
        private String code;
        @java.lang.SuppressWarnings("all")
        private LocalDateTime expiresAt;
        @java.lang.SuppressWarnings("all")
        private boolean used;

        @java.lang.SuppressWarnings("all")
        OtpBuilder() {
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public Otp.OtpBuilder id(final Long id) {
            this.id = id;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public Otp.OtpBuilder email(final String email) {
            this.email = email;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public Otp.OtpBuilder code(final String code) {
            this.code = code;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public Otp.OtpBuilder expiresAt(final LocalDateTime expiresAt) {
            this.expiresAt = expiresAt;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public Otp.OtpBuilder used(final boolean used) {
            this.used = used;
            return this;
        }

        @java.lang.SuppressWarnings("all")
        public Otp build() {
            return new Otp(this.id, this.email, this.code, this.expiresAt, this.used);
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        public java.lang.String toString() {
            return "Otp.OtpBuilder(id=" + this.id + ", email=" + this.email + ", code=" + this.code + ", expiresAt=" + this.expiresAt + ", used=" + this.used + ")";
        }
    }

    @java.lang.SuppressWarnings("all")
    public static Otp.OtpBuilder builder() {
        return new Otp.OtpBuilder();
    }

    @java.lang.SuppressWarnings("all")
    public Long getId() {
        return this.id;
    }

    @java.lang.SuppressWarnings("all")
    public String getEmail() {
        return this.email;
    }

    @java.lang.SuppressWarnings("all")
    public String getCode() {
        return this.code;
    }

    @java.lang.SuppressWarnings("all")
    public LocalDateTime getExpiresAt() {
        return this.expiresAt;
    }

    @java.lang.SuppressWarnings("all")
    public boolean isUsed() {
        return this.used;
    }

    @java.lang.SuppressWarnings("all")
    public void setId(final Long id) {
        this.id = id;
    }

    @java.lang.SuppressWarnings("all")
    public void setEmail(final String email) {
        this.email = email;
    }

    @java.lang.SuppressWarnings("all")
    public void setCode(final String code) {
        this.code = code;
    }

    @java.lang.SuppressWarnings("all")
    public void setExpiresAt(final LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    @java.lang.SuppressWarnings("all")
    public void setUsed(final boolean used) {
        this.used = used;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public boolean equals(final java.lang.Object o) {
        if (o == this) return true;
        if (!(o instanceof Otp)) return false;
        final Otp other = (Otp) o;
        if (!other.canEqual((java.lang.Object) this)) return false;
        if (this.isUsed() != other.isUsed()) return false;
        final java.lang.Object this$id = this.getId();
        final java.lang.Object other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
        final java.lang.Object this$email = this.getEmail();
        final java.lang.Object other$email = other.getEmail();
        if (this$email == null ? other$email != null : !this$email.equals(other$email)) return false;
        final java.lang.Object this$code = this.getCode();
        final java.lang.Object other$code = other.getCode();
        if (this$code == null ? other$code != null : !this$code.equals(other$code)) return false;
        final java.lang.Object this$expiresAt = this.getExpiresAt();
        final java.lang.Object other$expiresAt = other.getExpiresAt();
        if (this$expiresAt == null ? other$expiresAt != null : !this$expiresAt.equals(other$expiresAt)) return false;
        return true;
    }

    @java.lang.SuppressWarnings("all")
    protected boolean canEqual(final java.lang.Object other) {
        return other instanceof Otp;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * PRIME + (this.isUsed() ? 79 : 97);
        final java.lang.Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        final java.lang.Object $email = this.getEmail();
        result = result * PRIME + ($email == null ? 43 : $email.hashCode());
        final java.lang.Object $code = this.getCode();
        result = result * PRIME + ($code == null ? 43 : $code.hashCode());
        final java.lang.Object $expiresAt = this.getExpiresAt();
        result = result * PRIME + ($expiresAt == null ? 43 : $expiresAt.hashCode());
        return result;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public java.lang.String toString() {
        return "Otp(id=" + this.getId() + ", email=" + this.getEmail() + ", code=" + this.getCode() + ", expiresAt=" + this.getExpiresAt() + ", used=" + this.isUsed() + ")";
    }

    @java.lang.SuppressWarnings("all")
    public Otp() {
    }

    @java.lang.SuppressWarnings("all")
    public Otp(final Long id, final String email, final String code, final LocalDateTime expiresAt, final boolean used) {
        this.id = id;
        this.email = email;
        this.code = code;
        this.expiresAt = expiresAt;
        this.used = used;
    }
}