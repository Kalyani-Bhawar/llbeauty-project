package com.llbeauty.repository;

import com.llbeauty.entity.SalonInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SalonInfoRepository extends JpaRepository<SalonInfo, Long> {
}
