package com.llbeauty.repository;

import com.llbeauty.entity.NxlWalletTransaction;
import com.llbeauty.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Query("SELECT t FROM NxlWalletTransaction t WHERE " +
           "(:search IS NULL OR :search = '' OR LOWER(t.user.name) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(t.user.email) LIKE LOWER(CONCAT('%', :search, '%')) OR t.user.mobile LIKE %:search% OR CAST(t.id AS string) LIKE %:search% OR t.description LIKE %:search%) AND " +
           "(:type IS NULL OR :type = '' OR t.type = :type) ORDER BY t.dateTime DESC")
    Page<NxlWalletTransaction> searchTransactions(@Param("search") String search, @Param("type") String type, Pageable pageable);

    @Query("SELECT t FROM NxlWalletTransaction t WHERE " +
           "(:search IS NULL OR :search = '' OR LOWER(t.user.name) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(t.user.email) LIKE LOWER(CONCAT('%', :search, '%')) OR t.user.mobile LIKE %:search% OR CAST(t.id AS string) LIKE %:search% OR t.description LIKE %:search%) AND " +
           "(:type IS NULL OR :type = '' OR t.type = :type) ORDER BY t.dateTime DESC")
    List<NxlWalletTransaction> searchTransactionsList(@Param("search") String search, @Param("type") String type);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM NxlWalletTransaction t WHERE t.user = :user AND t.type = 'CREDIT'")
    BigDecimal sumTotalCreditsEarned(@Param("user") User user);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM NxlWalletTransaction t WHERE t.user = :user AND t.type = 'DEBIT'")
    BigDecimal sumTotalCreditsUsed(@Param("user") User user);

    boolean existsBySourceAndTransactionId(String source, String transactionId);

    List<NxlWalletTransaction> findAllByOrderByDateTimeDesc(org.springframework.data.domain.Pageable pageable);
}