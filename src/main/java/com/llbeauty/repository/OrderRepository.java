package com.llbeauty.repository;

import com.llbeauty.entity.Order;
import com.llbeauty.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserOrderByCreatedAtDesc(User user);
    long countByStatus(String status);

    @Query("SELECT o FROM Order o WHERE " +
           "(:search IS NULL OR :search = '' OR LOWER(o.user.name) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(o.user.email) LIKE LOWER(CONCAT('%', :search, '%')) OR CAST(o.id AS string) LIKE %:search% OR o.paymentId LIKE %:search%) AND " +
           "(:status IS NULL OR :status = '' OR o.status = :status)")
    Page<Order> searchOrders(@Param("search") String search, @Param("status") String status, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE " +
           "(:search IS NULL OR :search = '' OR LOWER(o.user.name) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(o.user.email) LIKE LOWER(CONCAT('%', :search, '%')) OR CAST(o.id AS string) LIKE %:search% OR o.paymentId LIKE %:search%) AND " +
           "(:status IS NULL OR :status = '' OR o.status = :status) ORDER BY o.createdAt DESC")
    List<Order> searchOrdersList(@Param("search") String search, @Param("status") String status);
}
