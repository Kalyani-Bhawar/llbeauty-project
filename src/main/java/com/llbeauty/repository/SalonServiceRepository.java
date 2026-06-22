package com.llbeauty.repository;

import com.llbeauty.entity.SalonService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SalonServiceRepository extends JpaRepository<SalonService, Long> {
    List<SalonService> findByActiveTrue();
    List<SalonService> findByNameIn(List<String> names);
}
