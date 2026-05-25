package com.llbeauty.repository;

import com.llbeauty.entity.User;
import com.llbeauty.entity.WalletTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, Long> {
    List<WalletTransaction> findByUserOrderByCreatedAtDesc(User user);
    List<WalletTransaction> findTop10ByUserOrderByCreatedAtDesc(User user);
}
