package com.llbeauty.service;

import com.llbeauty.entity.MatrimonyProfile;
import com.llbeauty.entity.User;
import com.llbeauty.enums.ProfileStatus;
import com.llbeauty.exception.ResourceNotFoundException;
import com.llbeauty.repository.MatrimonyProfileRepository;
import com.llbeauty.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MatrimonyProfileService {

    private final MatrimonyProfileRepository matrimonyProfileRepository;
    private final UserRepository userRepository;

    public MatrimonyProfileService(MatrimonyProfileRepository matrimonyProfileRepository, UserRepository userRepository) {
        this.matrimonyProfileRepository = matrimonyProfileRepository;
        this.userRepository = userRepository;
    }

    public MatrimonyProfile createProfile(MatrimonyProfile profile) {
        if (profile.getUser() == null || profile.getUser().getId() == null) {
            throw new IllegalArgumentException("User details must not be null");
        }
        
        // Check user exists
        User user = userRepository.findById(profile.getUser().getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + profile.getUser().getId()));
        
        // Check user does not already have matrimony profile
        if (matrimonyProfileRepository.existsByUserId(user.getId())) {
            throw new IllegalArgumentException("Matrimony profile already exists for user: " + user.getId());
        }

        profile.setUser(user);
        profile.setStatus(ProfileStatus.PENDING);
        return matrimonyProfileRepository.save(profile);
    }

    public MatrimonyProfile updateProfile(Long profileId, MatrimonyProfile updatedProfile) {
        MatrimonyProfile existing = matrimonyProfileRepository.findById(profileId)
                .orElseThrow(() -> new ResourceNotFoundException("Matrimony profile not found with id: " + profileId));

        // Update allowed fields
        existing.setFullName(updatedProfile.getFullName());
        existing.setDateOfBirth(updatedProfile.getDateOfBirth());
        existing.setGender(updatedProfile.getGender());
        existing.setHeight(updatedProfile.getHeight());
        existing.setMotherTongue(updatedProfile.getMotherTongue());
        existing.setCity(updatedProfile.getCity());
        existing.setState(updatedProfile.getState());
        existing.setCountry(updatedProfile.getCountry());
        existing.setReligion(updatedProfile.getReligion());
        existing.setCaste(updatedProfile.getCaste());
        existing.setSubCaste(updatedProfile.getSubCaste());
        existing.setEducation(updatedProfile.getEducation());
        existing.setOccupation(updatedProfile.getOccupation());
        existing.setCompanyName(updatedProfile.getCompanyName());
        existing.setAnnualIncome(updatedProfile.getAnnualIncome());
        existing.setFatherOccupation(updatedProfile.getFatherOccupation());
        existing.setMotherOccupation(updatedProfile.getMotherOccupation());
        existing.setBrotherCount(updatedProfile.getBrotherCount());
        existing.setSisterCount(updatedProfile.getSisterCount());
        existing.setFamilyType(updatedProfile.getFamilyType());
        existing.setAboutMe(updatedProfile.getAboutMe());
        existing.setProfilePhoto(updatedProfile.getProfilePhoto());

        return matrimonyProfileRepository.save(existing);
    }

    @Transactional(readOnly = true)
    public Optional<MatrimonyProfile> getProfileByUserId(Long userId) {
        return matrimonyProfileRepository.findByUserId(userId);
    }

    public MatrimonyProfile approveProfile(Long profileId) {
        MatrimonyProfile profile = matrimonyProfileRepository.findById(profileId)
                .orElseThrow(() -> new ResourceNotFoundException("Matrimony profile not found with id: " + profileId));
        profile.setStatus(ProfileStatus.APPROVED);
        return matrimonyProfileRepository.save(profile);
    }

    public MatrimonyProfile rejectProfile(Long profileId) {
        MatrimonyProfile profile = matrimonyProfileRepository.findById(profileId)
                .orElseThrow(() -> new ResourceNotFoundException("Matrimony profile not found with id: " + profileId));
        profile.setStatus(ProfileStatus.REJECTED);
        return matrimonyProfileRepository.save(profile);
    }

    @Transactional(readOnly = true)
    public List<MatrimonyProfile> getApprovedProfiles() {
        return matrimonyProfileRepository.findByStatus(ProfileStatus.APPROVED);
    }

    public Integer calculateProfileCompletion(MatrimonyProfile profile) {
        if (profile == null) {
            return 0;
        }
        int filledCount = 0;
        int totalFields = 10;

        // 1. Basic Information: Name, DOB, Gender, City
        if (profile.getFullName() != null && !profile.getFullName().trim().isEmpty()) filledCount++;
        if (profile.getDateOfBirth() != null) filledCount++;
        if (profile.getGender() != null && !profile.getGender().trim().isEmpty()) filledCount++;
        if (profile.getCity() != null && !profile.getCity().trim().isEmpty()) filledCount++;

        // 2. Religion Information: Religion
        if (profile.getReligion() != null && !profile.getReligion().trim().isEmpty()) filledCount++;

        // 3. Career: Education, Occupation
        if (profile.getEducation() != null && !profile.getEducation().trim().isEmpty()) filledCount++;
        if (profile.getOccupation() != null && !profile.getOccupation().trim().isEmpty()) filledCount++;

        // 4. Family: Family details (familyType)
        if (profile.getFamilyType() != null && !profile.getFamilyType().trim().isEmpty()) filledCount++;

        // 5. About Me: aboutMe
        if (profile.getAboutMe() != null && !profile.getAboutMe().trim().isEmpty()) filledCount++;

        // 6. Photo: profilePhoto
        if (profile.getProfilePhoto() != null && !profile.getProfilePhoto().trim().isEmpty()) filledCount++;

        return (filledCount * 100) / totalFields;
    }
}
