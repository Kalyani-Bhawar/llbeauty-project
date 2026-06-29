package com.llbeauty.repository;

import com.llbeauty.entity.AdminNxlTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AdminNxlTransactionRepository extends JpaRepository<AdminNxlTransaction, Long> {
    List<AdminNxlTransaction> findAllByOrderByCreatedAtDesc();
}