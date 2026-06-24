package com.llbeauty.repository;

import com.llbeauty.entity.MerchantOrder;
import com.llbeauty.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MerchantOrderRepository extends JpaRepository<MerchantOrder, Long> {
    List<MerchantOrder> findByUserOrderByCreatedAtDesc(User user);
    MerchantOrder findByRazorpayOrderId(String razorpayOrderId);
    long countByUserAndOrderStatus(User user, String orderStatus);

    @Query("SELECT COALESCE(SUM(o.totalSavings), 0.0) FROM MerchantOrder o WHERE o.user = :user AND o.orderStatus = 'SUCCESS'")
    Double sumTotalSavingsByUser(@Param("user") User user);

    @Query("SELECT COALESCE(SUM(o.finalAmount), 0.0) FROM MerchantOrder o WHERE o.user = :user AND o.orderStatus = 'SUCCESS'")
    Double sumTotalAmountByUser(@Param("user") User user);
}
