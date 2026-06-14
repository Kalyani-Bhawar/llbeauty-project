package com.llbeauty.service;

import com.llbeauty.dto.ExecutiveApplicationRequest;
import com.llbeauty.dto.MerchantApplicationRequest;
import com.llbeauty.dto.StoreApplicationResponse;
import com.llbeauty.dto.ApplicationStatusResponse;
import com.llbeauty.entity.ApplicationStatus;
import com.llbeauty.entity.StoreApplication;
import com.llbeauty.exception.ResourceNotFoundException;
import java.util.List;

public interface StoreApplicationService {

    /**
     * User applies for Executive program.
     */
    StoreApplicationResponse applyExecutive(Long userId, ExecutiveApplicationRequest request) throws ResourceNotFoundException;

    /**
     * User applies for Merchant program.
     */
    StoreApplicationResponse applyMerchant(Long userId, MerchantApplicationRequest request) throws ResourceNotFoundException;

    /**
     * Admin approves an application (executive or merchant).
     */
    StoreApplicationResponse approveApplication(Long applicationId) throws ResourceNotFoundException;

    /**
     * Admin rejects an application.
     */
    StoreApplicationResponse rejectApplication(Long applicationId, String adminRemarks) throws ResourceNotFoundException;

    /**
     * Get status of a specific application.
     */
    ApplicationStatusResponse getApplicationStatus(Long applicationId) throws ResourceNotFoundException;

    /**
     * List all pending applications.
     */
    List<StoreApplicationResponse> listPendingApplications();
}
