package com.llbeauty.repository;

import com.llbeauty.entity.MerchantProfile;
import com.llbeauty.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MerchantProfileRepository extends JpaRepository<MerchantProfile, Long> {
    Optional<MerchantProfile> findByUser(User user);
    Optional<MerchantProfile> findByMerchantId(String merchantId);
    boolean existsByUser(User user);
}
