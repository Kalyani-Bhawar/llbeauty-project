package com.llbeauty.repository;

import com.llbeauty.entity.NxlWalletTransaction;
import com.llbeauty.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;

@Repository
public interface NxlWalletTransactionRepository extends JpaRepository<NxlWalletTransaction, Long> {
    List<NxlWalletTransaction> findByUserOrderByDateTimeDesc(User user);
    List<NxlWalletTransaction> findTop10ByUserOrderByDateTimeDesc(User user);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM NxlWalletTransaction t WHERE t.user = :user AND t.type = 'CREDIT'")
    BigDecimal sumTotalCreditsEarned(@Param("user") User user);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM NxlWalletTransaction t WHERE t.user = :user AND t.type = 'DEBIT'")
    BigDecimal sumTotalCreditsUsed(@Param("user") User user);
}
