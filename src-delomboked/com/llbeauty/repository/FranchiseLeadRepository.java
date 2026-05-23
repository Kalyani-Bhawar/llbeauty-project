package com.llbeauty.repository;

import com.llbeauty.entity.FranchiseLead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FranchiseLeadRepository extends JpaRepository<FranchiseLead, Long> {
}
