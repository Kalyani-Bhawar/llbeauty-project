package com.llbeauty.repository;

import com.llbeauty.entity.MembershipQRCode;
import com.llbeauty.entity.UserMembership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface MembershipQRCodeRepository extends JpaRepository<MembershipQRCode, Long> {
    Optional<MembershipQRCode> findByUserMembership(UserMembership userMembership);
}
