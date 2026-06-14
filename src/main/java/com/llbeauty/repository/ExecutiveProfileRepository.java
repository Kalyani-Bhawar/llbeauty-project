package com.llbeauty.repository;

import com.llbeauty.entity.ExecutiveProfile;
import com.llbeauty.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ExecutiveProfileRepository extends JpaRepository<ExecutiveProfile, Long> {
    Optional<ExecutiveProfile> findByUser(User user);
    Optional<ExecutiveProfile> findByExecutiveId(String executiveId);
    Optional<ExecutiveProfile> findByReferralCode(String referralCode);
    boolean existsByUser(User user);
    List<ExecutiveProfile> findAllByStatus(String status);
}
