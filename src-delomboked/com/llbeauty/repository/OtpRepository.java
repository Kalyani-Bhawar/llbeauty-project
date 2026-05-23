package com.llbeauty.repository;

import com.llbeauty.entity.Otp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<Otp, Long> {

    Optional<Otp> findTopByMobileOrderByIdDesc(String mobile);

    @Modifying
    @Transactional
    @Query("DELETE FROM Otp o WHERE o.mobile = :mobile")
    void deleteByMobile(String mobile);
}
