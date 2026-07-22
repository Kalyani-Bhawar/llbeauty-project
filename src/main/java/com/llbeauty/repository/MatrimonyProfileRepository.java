package com.llbeauty.repository;

import com.llbeauty.entity.MatrimonyProfile;
import com.llbeauty.enums.ProfileStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MatrimonyProfileRepository extends JpaRepository<MatrimonyProfile, Long> {


    Optional<MatrimonyProfile> findByUserId(Long userId);

    boolean existsByUserId(Long userId);

    List<MatrimonyProfile> findByStatus(ProfileStatus status);

    Page<MatrimonyProfile> findByStatus(ProfileStatus status, Pageable pageable);

    Page<MatrimonyProfile> findByCityIgnoreCase(String city, Pageable pageable);

    Page<MatrimonyProfile> findByGenderIgnoreCase(String gender, Pageable pageable);

    Page<MatrimonyProfile> findByStatusAndCityIgnoreCaseAndGenderIgnoreCase(
            ProfileStatus status, String city, String gender, Pageable pageable
    );

    Page<MatrimonyProfile> findByStatusAndCityIgnoreCase(
            ProfileStatus status, String city, Pageable pageable
    );

    Page<MatrimonyProfile> findByStatusAndGenderIgnoreCase(
            ProfileStatus status, String gender, Pageable pageable
    );

    @Query("SELECT p FROM MatrimonyProfile p WHERE p.status = :status " +
           "AND (:gender IS NULL OR :gender = '' OR LOWER(p.gender) = LOWER(:gender)) " +
           "AND (:city IS NULL OR :city = '' OR LOWER(p.city) = LOWER(:city)) " +
           "AND (:youngestDob IS NULL OR p.dateOfBirth <= :youngestDob) " +
           "AND (:oldestDob IS NULL OR p.dateOfBirth >= :oldestDob) " +
           "AND (:religion IS NULL OR :religion = '' OR LOWER(p.religion) = LOWER(:religion)) " +
           "AND (:education IS NULL OR :education = '' OR LOWER(p.education) = LOWER(:education)) " +
           "AND (:occupation IS NULL OR :occupation = '' OR LOWER(p.occupation) = LOWER(:occupation))")
    Page<MatrimonyProfile> searchProfilesWithFilters(
            @Param("status") ProfileStatus status,
            @Param("gender") String gender,
            @Param("city") String city,
            @Param("youngestDob") LocalDate youngestDob,
            @Param("oldestDob") LocalDate oldestDob,
            @Param("religion") String religion,
            @Param("education") String education,
            @Param("occupation") String occupation,
            Pageable pageable
    );
}

