package com.llbeauty.repository;

import com.llbeauty.entity.AgentProfile;

import com.llbeauty.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface AgentProfileRepository extends JpaRepository<AgentProfile, Long> {
    Optional<AgentProfile> findByUser(User user);
    Optional<AgentProfile> findByAgentId(String agentId);
    Optional<AgentProfile> findByReferralCode(String referralCode);
    boolean existsByUser(User user);
    List<AgentProfile> findAllByStatus(String status);
}
