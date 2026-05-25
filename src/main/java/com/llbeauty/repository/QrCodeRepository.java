package com.llbeauty.repository;

import com.llbeauty.entity.QrCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface QrCodeRepository extends JpaRepository<QrCode, Long> {
    Optional<QrCode> findByQrData(String qrData);
}
