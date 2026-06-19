package com.llbeauty.repository;

import com.llbeauty.entity.AgentProfile;
import com.llbeauty.entity.Commission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface CommissionRepository extends JpaRepository<Commission, Long> {

    List<Commission> findByAgentOrderByCreatedAtDesc(AgentProfile agent);

    @Query("""
            SELECT COALESCE(SUM(c.amount), 0)
            FROM Commission c
            WHERE c.agent = :agent
            """)
    BigDecimal getTotalCommission(@Param("agent") AgentProfile agent);
}