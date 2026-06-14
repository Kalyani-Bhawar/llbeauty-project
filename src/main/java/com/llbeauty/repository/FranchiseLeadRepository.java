package com.llbeauty.repository;

import com.llbeauty.entity.FranchiseLead;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FranchiseLeadRepository extends JpaRepository<FranchiseLead, Long> {
    long countByStatus(String status);

    @Query("SELECT f FROM FranchiseLead f WHERE " +
           "(:search IS NULL OR :search = '' OR LOWER(f.name) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(f.email) LIKE LOWER(CONCAT('%', :search, '%')) OR f.mobile LIKE %:search% OR LOWER(f.city) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:status IS NULL OR :status = '' OR f.status = :status)")
    Page<FranchiseLead> searchLeads(@Param("search") String search, @Param("status") String status, Pageable pageable);

    @Query("SELECT f FROM FranchiseLead f WHERE " +
           "(:search IS NULL OR :search = '' OR LOWER(f.name) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(f.email) LIKE LOWER(CONCAT('%', :search, '%')) OR f.mobile LIKE %:search% OR LOWER(f.city) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:status IS NULL OR :status = '' OR f.status = :status) ORDER BY f.createdAt DESC")
    List<FranchiseLead> searchLeadsList(@Param("search") String search, @Param("status") String status);
}
