package com.llbeauty.repository;

import com.llbeauty.entity.Lead;
import com.llbeauty.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LeadRepository extends JpaRepository<Lead, Long> {
    List<Lead> findByAgentOrderByCreatedAtDesc(User agent);
}
