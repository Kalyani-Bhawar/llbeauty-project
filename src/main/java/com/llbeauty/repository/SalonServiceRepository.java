package com.llbeauty.repository;

import com.llbeauty.entity.SalonService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SalonServiceRepository extends JpaRepository<SalonService, Long> {
}
