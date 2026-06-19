package com.llbeauty.service;

import com.llbeauty.dto.AgentApplicationRequest;
import com.llbeauty.dto.MerchantApplicationRequest;
import com.llbeauty.dto.StoreApplicationResponse;
import com.llbeauty.dto.ApplicationStatusResponse;
import com.llbeauty.entity.*;
import com.llbeauty.exception.ResourceNotFoundException;
import com.llbeauty.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.llbeauty.service.WalletService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StoreApplicationServiceImpl implements StoreApplicationService {

    private final StoreApplicationRepository storeApplicationRepository;
    private final UserRepository userRepository;
    private final AgentProfileRepository agentProfileRepository;
    private final MerchantRepository merchantRepository;
    private final QrCodeRepository qrCodeRepository;
    private final MerchantProfileRepository merchantProfileRepository;
    private final StoreCreditRepository storeCreditRepository;
    private final WalletService walletService;

    public StoreApplicationServiceImpl(StoreApplicationRepository storeApplicationRepository,
                                       UserRepository userRepository,
                                       AgentProfileRepository agentProfileRepository,
                                       MerchantRepository merchantRepository,
                                       QrCodeRepository qrCodeRepository,
                                       MerchantProfileRepository merchantProfileRepository,
                                       StoreCreditRepository storeCreditRepository,
                                       WalletService walletService) {
        this.storeApplicationRepository = storeApplicationRepository;
        this.userRepository = userRepository;
        this.agentProfileRepository = agentProfileRepository;
        this.merchantRepository = merchantRepository;
        this.qrCodeRepository = qrCodeRepository;
        this.merchantProfileRepository = merchantProfileRepository;
        this.storeCreditRepository = storeCreditRepository;
        this.walletService = walletService;
    }

    private StoreApplicationResponse mapToResponse(StoreApplication app) {
        return new StoreApplicationResponse(
            app.getId(),
            app.getUser().getId(),
            app.getUser().getName(),
            app.getType(),
            app.getBusinessName(),
            app.getContactEmail(),
            app.getContactPhone(),
            app.getStatus(),
            app.getCreatedAt(),
            app.getDetails()
        );
    }

    private String parseDetail(String details, String key) {
        if (details == null) return "";
        for (String line : details.split("\n")) {
            if (line.startsWith(key + ":")) {
                return line.substring(key.length() + 1).trim();
            }
        }
        return "";
    }

    @Override
    @Transactional
    public StoreApplicationResponse applyAgent(Long userId, AgentApplicationRequest request) throws ResourceNotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        StoreApplication app = new StoreApplication();
        app.setUser(user);
        app.setType(ApplicationType.AGENT);
        app.setBusinessName(request.getFullName());
        app.setContactEmail(request.getEmail());
        app.setContactPhone(request.getMobile());
        app.setStatus(ApplicationStatus.PENDING);
        app.setCreatedAt(LocalDateTime.now());
        
        StringBuilder sb = new StringBuilder();
        sb.append("Address: ").append(request.getAddress()).append("\n")
          .append("City: ").append(request.getCity()).append("\n")
          .append("State: ").append(request.getState()).append("\n")
          .append("Occupation: ").append(request.getOccupation()).append("\n")
          .append("Experience: ").append(request.getExperience()).append("\n")
          .append("Referral Code: ").append(request.getReferralCode() != null ? request.getReferralCode() : "").append("\n")
          .append("Registration Type: ").append(request.getRegistrationType() != null ? request.getRegistrationType() : "FREE");
        app.setDetails(sb.toString());

        StoreApplication saved = storeApplicationRepository.save(app);

        user.setAgentStatus("PENDING");
        userRepository.save(user);

        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public StoreApplicationResponse applyMerchant(Long userId, MerchantApplicationRequest request) throws ResourceNotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        StoreApplication app = new StoreApplication();
        app.setUser(user);
        app.setType(ApplicationType.MERCHANT);
        app.setBusinessName(request.getOwnerName());
        app.setContactEmail(request.getEmail());
        app.setContactPhone(request.getMobile());
        app.setStatus(ApplicationStatus.PENDING);
        app.setCreatedAt(LocalDateTime.now());

        // Set direct columns
        app.setOwnerName(request.getOwnerName());
        app.setAddress(request.getAddress());
        app.setCity(request.getCity());
        app.setState(request.getState());
        app.setGstNumber(request.getGstNumber());
        app.setPanNumber(request.getPanNumber());
        app.setAadharNumber(request.getAadharNumber());
        app.setBusinessType(request.getBusinessType());
        app.setBankAccountHolderName(request.getBankAccountHolderName());
        app.setBankAccountNumber(request.getBankAccountNumber());
        app.setIfscCode(request.getIfscCode());
        app.setPanDocumentUrl(request.getPanDocumentUrl());
        app.setAadharDocumentUrl(request.getAadharDocumentUrl());
        app.setGstDocumentUrl(request.getGstDocumentUrl());

        StringBuilder sb = new StringBuilder();
        sb.append("Owner Name: ").append(request.getOwnerName()).append("\n")
          .append("Address: ").append(request.getAddress()).append("\n")
          .append("City: ").append(request.getCity()).append("\n")
          .append("State: ").append(request.getState()).append("\n")
          .append("GST: ").append(request.getGstNumber()).append("\n")
          .append("PAN: ").append(request.getPanNumber()).append("\n")
          .append("Aadhar: ").append(request.getAadharNumber()).append("\n")
          .append("Business Type: ").append(request.getBusinessType()).append("\n")
          .append("Bank Holder: ").append(request.getBankAccountHolderName()).append("\n")
          .append("Online Selling: ").append(request.getOnlineSelling() != null && request.getOnlineSelling() ? "Yes" : "No").append("\n")
          .append("Offline Selling: ").append(request.getOfflineSelling() != null && request.getOfflineSelling() ? "Yes" : "No").append("\n")
          .append("Registration Type: MERCHANT_DEPOSIT\n")
          .append("Security Deposit: ₹50,000");
        app.setDetails(sb.toString());

        StoreApplication saved = storeApplicationRepository.save(app);

        user.setMerchantStatus("PENDING");
        userRepository.save(user);

        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public StoreApplicationResponse approveApplication(Long applicationId) throws ResourceNotFoundException {
        StoreApplication app = storeApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Store application not found with ID: " + applicationId));

        if (app.getStatus() != ApplicationStatus.PENDING) {
            throw new IllegalStateException("Application is already processed. Current status: " + app.getStatus());
        }

        app.setStatus(ApplicationStatus.APPROVED);
        storeApplicationRepository.save(app);

        User user = app.getUser();
        if (app.getType() == ApplicationType.AGENT) {
            user.setAgentStatus("ACTIVE");
            userRepository.save(user);

            // Create Agent Profile if not exists
            if (agentProfileRepository.findByUser(user).isEmpty()) {
                String agentId = "LLB-EXE-" + user.getId();
                String referralCode;
                do {
                    referralCode = "REF" + user.getId() + (int) (Math.random() * 900 + 100);
                } while (agentProfileRepository.findByReferralCode(referralCode).isPresent());

                AgentProfile profile = new AgentProfile(user, agentId, referralCode);
                profile.setStatus("ACTIVE");
                agentProfileRepository.save(profile);
            }
        } else if (app.getType() == ApplicationType.MERCHANT) {
            user.setMerchantStatus("ACTIVE");
            userRepository.save(user);

            // Create MerchantProfile if not exists
            if (merchantProfileRepository.findByUser(user).isEmpty()) {
                String merchantId = "LLB-MER-" + user.getId();
                String ownerName = app.getOwnerName();
                if (ownerName == null || ownerName.isEmpty()) ownerName = user.getName();

                MerchantProfile mp = new MerchantProfile(
                    user, merchantId, ownerName, app.getContactPhone(), app.getContactEmail(),
                    app.getAddress(), app.getCity(), app.getState(), app.getGstNumber(), app.getPanNumber(),
                    app.getAadharNumber(), app.getBusinessType()
                );
                mp.setPanDocumentUrl(app.getPanDocumentUrl());
                mp.setAadharDocumentUrl(app.getAadharDocumentUrl());
                mp.setGstDocumentUrl(app.getGstDocumentUrl());
                mp.setBankAccountHolderName(app.getBankAccountHolderName());
                mp.setBankAccountNumber(app.getBankAccountNumber());
                mp.setIfscCode(app.getIfscCode());
                mp.setStatus("ACTIVE");
                merchantProfileRepository.save(mp);
            }

            // Create or update StoreCredit with 55,000 balance
            java.math.BigDecimal creditAmount = java.math.BigDecimal.valueOf(55000);
            StoreCredit sc = storeCreditRepository.findByUser(user).orElse(null);
            if (sc == null) {
                sc = new StoreCredit(user, creditAmount);
            } else {
                sc.setBalance(sc.getBalance().add(creditAmount));
                sc.setUpdatedAt(java.time.LocalDateTime.now());
            }
            storeCreditRepository.save(sc);

            // Initialize general wallet balance (creates entry if missing, starts at zero)
            walletService.getBalance(user);

            // Create Merchant entity (for local scanning QR flow)
            if (merchantRepository.findByName(app.getBusinessName()) == null) {
                Merchant merchant = new Merchant();
                merchant.setName(app.getBusinessName());
                merchant.setContact(app.getContactPhone());
                merchant.setLocation("Pending Setup");
                merchant.setStatus("ACTIVE");
                merchant = merchantRepository.save(merchant);

                // Create QR code for merchant
                QrCode qr = new QrCode(null, merchant, "/wallet/redeem?merchantId=" + merchant.getId(), "ACTIVE", null);
                qrCodeRepository.save(qr);
            }
        }

        return mapToResponse(app);
    }

    @Override
    @Transactional
    public StoreApplicationResponse rejectApplication(Long applicationId, String adminRemarks) throws ResourceNotFoundException {
        StoreApplication app = storeApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Store application not found with ID: " + applicationId));

        if (app.getStatus() != ApplicationStatus.PENDING) {
            throw new IllegalStateException("Application is already processed. Current status: " + app.getStatus());
        }

        app.setStatus(ApplicationStatus.REJECTED);
        String currentDetails = app.getDetails() != null ? app.getDetails() : "";
        app.setDetails(currentDetails + "\nAdmin Rejection Remarks: " + adminRemarks);
        storeApplicationRepository.save(app);

        User user = app.getUser();
        if (app.getType() == ApplicationType.AGENT) {
            user.setAgentStatus("REJECTED");
        } else if (app.getType() == ApplicationType.MERCHANT) {
            user.setMerchantStatus("REJECTED");
        }
        userRepository.save(user);

        return mapToResponse(app);
    }

    @Override
    public ApplicationStatusResponse getApplicationStatus(Long applicationId) throws ResourceNotFoundException {
        StoreApplication app = storeApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Store application not found with ID: " + applicationId));
        return new ApplicationStatusResponse(app.getId(), app.getStatus(), app.getDetails());
    }

    @Override
    public List<StoreApplicationResponse> listPendingApplications() {
        return storeApplicationRepository.findAllByStatus(ApplicationStatus.PENDING)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
}
