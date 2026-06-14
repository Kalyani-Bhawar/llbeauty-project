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

    public StoreApplicationResponse() {}

    public StoreApplicationResponse(Long id, Long userId, String userName, ApplicationType type,
                                    String businessName, String contactEmail, String contactPhone,
                                    ApplicationStatus status, LocalDateTime createdAt, String details) {
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
}
