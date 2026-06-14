package com.llbeauty.repository;

import com.llbeauty.entity.StoreCredit;
import com.llbeauty.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface StoreCreditRepository extends JpaRepository<StoreCredit, Long> {
    Optional<StoreCredit> findByUser(User user);
}
