package com.llbeauty.service;

import com.llbeauty.entity.MatrimonyProfile;
import com.llbeauty.enums.ProfileStatus;
import com.llbeauty.repository.MatrimonyProfileRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;

@Service
@Transactional(readOnly = true)
public class MatrimonySearchService {

    private final MatrimonyProfileRepository matrimonyProfileRepository;

    public MatrimonySearchService(MatrimonyProfileRepository matrimonyProfileRepository) {
        this.matrimonyProfileRepository = matrimonyProfileRepository;
    }

    public Page<MatrimonyProfile> searchProfiles(
            String city,
            String gender,
            Integer minAge,
            Integer maxAge,
            String religion,
            String education,
            String occupation,
            Pageable pageable
    ) {
        ProfileStatus searchStatus = ProfileStatus.APPROVED;

        LocalDate youngestDob = minAge != null ? LocalDate.now().minusYears(minAge) : null;
        LocalDate oldestDob = maxAge != null ? LocalDate.now().minusYears(maxAge + 1) : null;

        // Clean up inputs (blank strings as null for JPQL query ease)
        String genderParam = (gender != null && !gender.trim().isEmpty()) ? gender.trim() : null;
        String cityParam = (city != null && !city.trim().isEmpty()) ? city.trim() : null;
        String religionParam = (religion != null && !religion.trim().isEmpty()) ? religion.trim() : null;
        String educationParam = (education != null && !education.trim().isEmpty()) ? education.trim() : null;
        String occupationParam = (occupation != null && !occupation.trim().isEmpty()) ? occupation.trim() : null;

        return matrimonyProfileRepository.searchProfilesWithFilters(
                searchStatus,
                genderParam,
                cityParam,
                youngestDob,
                oldestDob,
                religionParam,
                educationParam,
                occupationParam,
                pageable
        );
    }
}

