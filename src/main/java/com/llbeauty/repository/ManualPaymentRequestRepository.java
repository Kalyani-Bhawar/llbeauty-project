package com.llbeauty.repository;

import com.llbeauty.entity.ManualPaymentRequest;
import com.llbeauty.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ManualPaymentRequestRepository extends JpaRepository<ManualPaymentRequest, Long> {
    List<ManualPaymentRequest> findByStatus(String status);
    List<ManualPaymentRequest> findByUserOrderByCreatedAtDesc(User user);
}
