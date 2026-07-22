package com.llbeauty.service;

import com.llbeauty.entity.MatrimonyProfile;
import com.llbeauty.entity.Notification;
import com.llbeauty.enums.ProfileStatus;
import com.llbeauty.exception.ResourceNotFoundException;
import com.llbeauty.repository.MatrimonyProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class AdminMatrimonyService {

    private final MatrimonyProfileRepository matrimonyProfileRepository;
    private final NotificationService notificationService;

    public AdminMatrimonyService(MatrimonyProfileRepository matrimonyProfileRepository,
                                 NotificationService notificationService) {
        this.matrimonyProfileRepository = matrimonyProfileRepository;
        this.notificationService = notificationService;
    }

    @Transactional(readOnly = true)
    public List<MatrimonyProfile> getPendingProfiles() {
        return matrimonyProfileRepository.findByStatus(ProfileStatus.PENDING);
    }

    public MatrimonyProfile approveProfile(Long profileId) {
        MatrimonyProfile profile = matrimonyProfileRepository.findById(profileId)
                .orElseThrow(() -> new ResourceNotFoundException("Matrimony profile not found with id: " + profileId));
        profile.setStatus(ProfileStatus.APPROVED);
        MatrimonyProfile savedProfile = matrimonyProfileRepository.save(profile);

        // Notify user about profile approval
        notificationService.createNotification(savedProfile.getUser(), "Profile Approved", "Your matrimony profile has been approved.", Notification.NotificationType.SUCCESS);
        notificationService.notifyProfileApproved(savedProfile.getUser().getId());

        return savedProfile;
    }

    public MatrimonyProfile rejectProfile(Long profileId) {
        MatrimonyProfile profile = matrimonyProfileRepository.findById(profileId)
                .orElseThrow(() -> new ResourceNotFoundException("Matrimony profile not found with id: " + profileId));
        profile.setStatus(ProfileStatus.REJECTED);
        return matrimonyProfileRepository.save(profile);
    }

    public MatrimonyProfile blockProfile(Long profileId) {
        MatrimonyProfile profile = matrimonyProfileRepository.findById(profileId)
                .orElseThrow(() -> new ResourceNotFoundException("Matrimony profile not found with id: " + profileId));
        profile.setStatus(ProfileStatus.BLOCKED);
        return matrimonyProfileRepository.save(profile);
    }
    
    @Transactional(readOnly = true)
    public List<MatrimonyProfile> getRejectedProfiles() {
        return matrimonyProfileRepository.findByStatus(ProfileStatus.REJECTED);
    }

    @Transactional(readOnly = true)
    public List<MatrimonyProfile> getBlockedProfiles() {
        return matrimonyProfileRepository.findByStatus(ProfileStatus.BLOCKED);
    }
}

