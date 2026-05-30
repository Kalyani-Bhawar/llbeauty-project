package com.llbeauty.repository;

import com.llbeauty.entity.MembershipPurchase;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MembershipPurchaseRepository extends JpaRepository<MembershipPurchase, Long> {
    MembershipPurchase findByRazorpayOrderId(String razorpayOrderId);
}
