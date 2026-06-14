package com.llbeauty.repository;

import com.llbeauty.entity.MerchantApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MerchantApplicationRepository extends JpaRepository<MerchantApplication, Long> {
    List<MerchantApplication> findByUserId(Long userId);
}
