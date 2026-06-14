package com.llbeauty.dto;

import com.llbeauty.entity.ApplicationStatus;

public class ApplicationStatusResponse {
    private Long applicationId;
    private ApplicationStatus status;
    private String details;

    public ApplicationStatusResponse() {}

    public ApplicationStatusResponse(Long applicationId, ApplicationStatus status, String details) {
        this.applicationId = applicationId;
        this.status = status;
        this.details = details;
    }

    public Long getApplicationId() { return applicationId; }
    public void setApplicationId(Long applicationId) { this.applicationId = applicationId; }

    public ApplicationStatus getStatus() { return status; }
    public void setStatus(ApplicationStatus status) { this.status = status; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
}
