package com.llbeauty.repository;

import com.llbeauty.entity.AgentProfile;
import com.llbeauty.entity.Payout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PayoutRepository extends JpaRepository<Payout, Long> {
    List<Payout> findByAgentOrderByCreatedAtDesc(AgentProfile agent);
}
