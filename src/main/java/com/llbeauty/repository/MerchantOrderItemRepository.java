package com.llbeauty.repository;

import com.llbeauty.entity.MerchantOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MerchantOrderItemRepository extends JpaRepository<MerchantOrderItem, Long> {
}
