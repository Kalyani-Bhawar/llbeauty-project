package com.llbeauty.repository;

import com.llbeauty.entity.MerchantCustomer;
import com.llbeauty.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MerchantCustomerRepository extends JpaRepository<MerchantCustomer, Long> {
    List<MerchantCustomer> findByMerchantOrderByCreatedAtDesc(User merchant);
}
