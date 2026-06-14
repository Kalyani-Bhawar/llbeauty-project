package com.llbeauty.repository;

import com.llbeauty.entity.User;
import com.llbeauty.entity.WalletTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, Long> {
    List<WalletTransaction> findByUserOrderByCreatedAtDesc(User user);
    List<WalletTransaction> findTop10ByUserOrderByCreatedAtDesc(User user);
    List<WalletTransaction> findAllByOrderByCreatedAtDesc();
    List<WalletTransaction> findByTypeAndStatus(String type, String status);
    List<WalletTransaction> findByType(String type);

    @Query("SELECT wt FROM WalletTransaction wt WHERE " +
           "(:search IS NULL OR :search = '' OR LOWER(wt.user.name) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(wt.user.email) LIKE LOWER(CONCAT('%', :search, '%')) OR wt.user.mobile LIKE %:search% OR CAST(wt.id AS string) LIKE %:search% OR wt.description LIKE %:search%) AND " +
           "(:type IS NULL OR :type = '' OR wt.type = :type)")
    Page<WalletTransaction> searchTransactions(@Param("search") String search, @Param("type") String type, Pageable pageable);

    @Query("SELECT wt FROM WalletTransaction wt WHERE " +
           "(:search IS NULL OR :search = '' OR LOWER(wt.user.name) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(wt.user.email) LIKE LOWER(CONCAT('%', :search, '%')) OR wt.user.mobile LIKE %:search% OR CAST(wt.id AS string) LIKE %:search% OR wt.description LIKE %:search%) AND " +
           "(:type IS NULL OR :type = '' OR wt.type = :type) ORDER BY wt.createdAt DESC")
    List<WalletTransaction> searchTransactionsList(@Param("search") String search, @Param("type") String type);
}
