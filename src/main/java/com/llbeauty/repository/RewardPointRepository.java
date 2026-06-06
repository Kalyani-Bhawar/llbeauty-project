package com.llbeauty.repository;

import com.llbeauty.entity.RewardPoint;
import com.llbeauty.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RewardPointRepository extends JpaRepository<RewardPoint, Long> {
    Optional<RewardPoint> findByUser(User user);
}
