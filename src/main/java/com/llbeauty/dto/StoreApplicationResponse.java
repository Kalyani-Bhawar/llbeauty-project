package com.llbeauty.dto;

import com.llbeauty.entity.ApplicationStatus;
import com.llbeauty.entity.ApplicationType;
import java.time.LocalDateTime;

public class StoreApplicationResponse {
    private Long id;
    private Long userId;
    private String userName;
    private ApplicationType type;
    private String businessName;
    private String contactEmail;
    private String contactPhone;
    private ApplicationStatus status;
    private LocalDateTime createdAt;
    private String details;
    private String referralCode;
    private String paymentId;
    private Double paymentAmount;
    private String gstNumber;

    public StoreApplicationResponse() {}

    public StoreApplicationResponse(Long id, Long userId, String userName, ApplicationType type,
                                    String businessName, String contactEmail, String contactPhone,
                                    ApplicationStatus status, LocalDateTime createdAt, String details, String referralCode) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.type = type;
        this.businessName = businessName;
        this.contactEmail = contactEmail;
        this.contactPhone = contactPhone;
        this.status = status;
        this.createdAt = createdAt;
        this.details = details;
        this.referralCode = referralCode;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public ApplicationType getType() { return type; }
    public void setType(ApplicationType type) { this.type = type; }

    public String getBusinessName() { return businessName; }
    public void setBusinessName(String businessName) { this.businessName = businessName; }

    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }

    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }

    public ApplicationStatus getStatus() { return status; }
    public void setStatus(ApplicationStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public String getReferralCode() { return referralCode; }
    public void setReferralCode(String referralCode) { this.referralCode = referralCode; }

    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }

    public Double getPaymentAmount() { return paymentAmount; }
    public void setPaymentAmount(Double paymentAmount) { this.paymentAmount = paymentAmount; }

    public String getGstNumber() { return gstNumber; }
    public void setGstNumber(String gstNumber) { this.gstNumber = gstNumber; }
}
