package com.llbeauty.service;

import com.llbeauty.entity.ApplicationStatus;
import com.llbeauty.entity.MerchantApplication;
import com.llbeauty.entity.User;
import com.llbeauty.repository.MerchantApplicationRepository;
import com.llbeauty.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MerchantApplicationService {

    private final MerchantApplicationRepository applicationRepository;
    private final UserRepository userRepository;

    public MerchantApplicationService(MerchantApplicationRepository applicationRepository, UserRepository userRepository) {
        this.applicationRepository = applicationRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public MerchantApplication submitApplication(MerchantApplication application) {
        return applicationRepository.save(application);
    }

    public List<MerchantApplication> getApplicationsByUser(User user) {
        return applicationRepository.findByUserId(user.getId());
    }

    public List<MerchantApplication> getAllApplications() {
        return applicationRepository.findAll();
    }

    @Transactional
    public void approveApplication(Long applicationId) {
        MerchantApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        application.setStatus(ApplicationStatus.APPROVED);
        applicationRepository.save(application);

        User user = application.getUser();
        if (!"ROLE_MERCHANT".equals(user.getRole())) {
            user.setRole("ROLE_MERCHANT");
            userRepository.save(user);
        }
    }

    @Transactional
    public void rejectApplication(Long applicationId) {
        MerchantApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        application.setStatus(ApplicationStatus.REJECTED);
        applicationRepository.save(application);
    }
}
