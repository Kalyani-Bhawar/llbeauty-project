package com.llbeauty.repository;

import com.llbeauty.entity.Commission;
import com.llbeauty.entity.ExecutiveProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommissionRepository extends JpaRepository<Commission, Long> {
    List<Commission> findByExecutiveOrderByCreatedAtDesc(ExecutiveProfile executive);
}
