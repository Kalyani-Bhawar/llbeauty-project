package com.llbeauty.repository;

import com.llbeauty.entity.StoreApplication;
import com.llbeauty.entity.ApplicationStatus;
import com.llbeauty.entity.ApplicationType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface StoreApplicationRepository extends JpaRepository<StoreApplication, Long> {
    Optional<StoreApplication> findByUserId(Long userId);
    List<StoreApplication> findAllByStatus(ApplicationStatus status);
    List<StoreApplication> findAllByType(ApplicationType type);
    List<StoreApplication> findByUserAndTypeAndStatus(com.llbeauty.entity.User user, ApplicationType type, ApplicationStatus status);
    List<StoreApplication> findByUser(com.llbeauty.entity.User user);
}
