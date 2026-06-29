package com.llbeauty.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "appointments")
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private String userName;
    private String userMobile;
    private String serviceName; // Kept for backwards compatibility
    private LocalDate appointmentDate;
    private String timeSlot;
    private String status; // PENDING, CONFIRMED, CANCELLED, PAYMENT_PENDING
    private LocalDateTime createdAt;
    
    @Column(length = 1000)
    private String services; // Comma-separated list of selected services
    private Double advancePaid; // Fixed ₹100.0 advanced payment amount
    private String paymentStatus; // PENDING, PAID
    private String token; // Unique queue/booking token e.g., "LL-TKN-4902"
    
    private Long beauticianId;
    private String beauticianName;
    @Column(name = "referral_code")
    private String referralCode;

    @Column(name = "total_amount")
    private Double totalAmount = 0.0;

    @Column(name = "razorpay_order_id")
    private String razorpayOrderId;

    @Column(name = "razorpay_payment_id")
    private String razorpayPaymentId;

    @Column(name = "nxl_used")
    private Double nxlUsed = 0.0;

    @Column(name = "earned_nxl")
    private Double earnedNxl = 0.0;

    @Column(name = "nxl_rewarded")
    private Boolean nxlRewarded = false;

    @Column(name = "final_paid_amount")
    private Double finalPaidAmount = 0.0;

    public Double getNxlUsed() {
        return nxlUsed;
    }

    public void setNxlUsed(Double nxlUsed) {
        this.nxlUsed = nxlUsed;
    }

    public Double getEarnedNxl() {
        return earnedNxl;
    }

    public void setEarnedNxl(Double earnedNxl) {
        this.earnedNxl = earnedNxl;
    }

    public Boolean getNxlRewarded() {
        return nxlRewarded;
    }

    public void setNxlRewarded(Boolean nxlRewarded) {
        this.nxlRewarded = nxlRewarded;
    }

    public Double getFinalPaidAmount() {
        return finalPaidAmount;
    }

    public void setFinalPaidAmount(Double finalPaidAmount) {
        this.finalPaidAmount = finalPaidAmount;
    }

    public String getReferralCode() {
        return referralCode;
    }

    public void setReferralCode(String referralCode) {
        this.referralCode = referralCode;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getRazorpayOrderId() {
        return razorpayOrderId;
    }

    public void setRazorpayOrderId(String razorpayOrderId) {
        this.razorpayOrderId = razorpayOrderId;
    }

    public String getRazorpayPaymentId() {
        return razorpayPaymentId;
    }

    public void setRazorpayPaymentId(String razorpayPaymentId) {
        this.razorpayPaymentId = razorpayPaymentId;
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = "PENDING";
        }
        if (this.paymentStatus == null) {
            this.paymentStatus = "PENDING";
        }
        if (this.advancePaid == null) {
            this.advancePaid = 100.0;
        }
        if (this.nxlUsed == null) {
            this.nxlUsed = 0.0;
        }
        if (this.earnedNxl == null) {
            this.earnedNxl = 0.0;
        }
        if (this.nxlRewarded == null) {
            this.nxlRewarded = false;
        }
        if (this.finalPaidAmount == null) {
            this.finalPaidAmount = 0.0;
        }
    }

    // Default Constructor
    public Appointment() {
    }

    // All Arguments Constructor
    public Appointment(Long id, Long userId, String userName, String userMobile, String serviceName, 
                       LocalDate appointmentDate, String timeSlot, String status, LocalDateTime createdAt,
                       String services, Double advancePaid, String paymentStatus, String token) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.userMobile = userMobile;
        this.serviceName = serviceName;
        this.appointmentDate = appointmentDate;
        this.timeSlot = timeSlot;
        this.status = status;
        this.createdAt = createdAt;
        this.services = services;
        this.advancePaid = advancePaid;
        this.paymentStatus = paymentStatus;
        this.token = token;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserMobile() {
        return userMobile;
    }

    public void setUserMobile(String userMobile) {
        this.userMobile = userMobile;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(LocalDate appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(String timeSlot) {
        this.timeSlot = timeSlot;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getServices() {
        return services;
    }

    public void setServices(String services) {
        this.services = services;
    }

    public Double getAdvancePaid() {
        return advancePaid;
    }

    public void setAdvancePaid(Double advancePaid) {
        this.advancePaid = advancePaid;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    // Builder Class
    public static AppointmentBuilder builder() {
        return new AppointmentBuilder();
    }

    public static class AppointmentBuilder {
        private Long id;
        private Long userId;
        private String userName;
        private String userMobile;
        private String serviceName;
        private LocalDate appointmentDate;
        private String timeSlot;
        private String status;
        private LocalDateTime createdAt;
        private String services;
        private Double advancePaid;
        private String paymentStatus;
        private String token;
        private Double totalAmount;
        private String razorpayOrderId;
        private String razorpayPaymentId;
        private String referralCode;
        private Double nxlUsed;
        private Double earnedNxl;
        private Boolean nxlRewarded;
        private Double finalPaidAmount;

        AppointmentBuilder() {
        }

        public AppointmentBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public AppointmentBuilder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public AppointmentBuilder userName(String userName) {
            this.userName = userName;
            return this;
        }

        public AppointmentBuilder userMobile(String userMobile) {
            this.userMobile = userMobile;
            return this;
        }

        public AppointmentBuilder serviceName(String serviceName) {
            this.serviceName = serviceName;
            return this;
        }

        public AppointmentBuilder appointmentDate(LocalDate appointmentDate) {
            this.appointmentDate = appointmentDate;
            return this;
        }

        public AppointmentBuilder timeSlot(String timeSlot) {
            this.timeSlot = timeSlot;
            return this;
        }

        public AppointmentBuilder status(String status) {
            this.status = status;
            return this;
        }

        public AppointmentBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public AppointmentBuilder services(String services) {
            this.services = services;
            return this;
        }

        public AppointmentBuilder advancePaid(Double advancePaid) {
            this.advancePaid = advancePaid;
            return this;
        }

        public AppointmentBuilder paymentStatus(String paymentStatus) {
            this.paymentStatus = paymentStatus;
            return this;
        }

        public AppointmentBuilder token(String token) {
            this.token = token;
            return this;
        }

        public AppointmentBuilder totalAmount(Double totalAmount) {
            this.totalAmount = totalAmount;
            return this;
        }

        public AppointmentBuilder razorpayOrderId(String razorpayOrderId) {
            this.razorpayOrderId = razorpayOrderId;
            return this;
        }

        public AppointmentBuilder razorpayPaymentId(String razorpayPaymentId) {
            this.razorpayPaymentId = razorpayPaymentId;
            return this;
        }

        public AppointmentBuilder referralCode(String referralCode) {
            this.referralCode = referralCode;
            return this;
        }

        public AppointmentBuilder nxlUsed(Double nxlUsed) {
            this.nxlUsed = nxlUsed;
            return this;
        }

        public AppointmentBuilder earnedNxl(Double earnedNxl) {
            this.earnedNxl = earnedNxl;
            return this;
        }

        public AppointmentBuilder nxlRewarded(Boolean nxlRewarded) {
            this.nxlRewarded = nxlRewarded;
            return this;
        }

        public AppointmentBuilder finalPaidAmount(Double finalPaidAmount) {
            this.finalPaidAmount = finalPaidAmount;
            return this;
        }

        public Appointment build() {
            Appointment app = new Appointment(id, userId, userName, userMobile, serviceName, appointmentDate, timeSlot, status, createdAt, services, advancePaid, paymentStatus, token);
            app.setTotalAmount(this.totalAmount);
            app.setRazorpayOrderId(this.razorpayOrderId);
            app.setRazorpayPaymentId(this.razorpayPaymentId);
            app.setReferralCode(this.referralCode);
            if (this.nxlUsed != null) app.setNxlUsed(this.nxlUsed);
            if (this.earnedNxl != null) app.setEarnedNxl(this.earnedNxl);
            if (this.nxlRewarded != null) app.setNxlRewarded(this.nxlRewarded);
            if (this.finalPaidAmount != null) app.setFinalPaidAmount(this.finalPaidAmount);
            return app;
        }
    }

    public Long getBeauticianId() {
        return beauticianId;
    }

    public void setBeauticianId(Long beauticianId) {
        this.beauticianId = beauticianId;
    }

    public String getBeauticianName() {
        return beauticianName;
    }

    public void setBeauticianName(String beauticianName) {
        this.beauticianName = beauticianName;
    }
}
