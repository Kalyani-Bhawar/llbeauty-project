package com.llbeauty.repository;

import com.llbeauty.entity.RewardTransaction;
import com.llbeauty.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RewardTransactionRepository extends JpaRepository<RewardTransaction, Long> {
    List<RewardTransaction> findByUserOrderByCreatedAtDesc(User user);
}
