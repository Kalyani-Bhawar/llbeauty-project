package com.llbeauty.service;

import com.llbeauty.entity.Otp;
import com.llbeauty.repository.OtpRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * OTP Service — DUMMY MODE for MVP Testing.
 * OTP is always "1234". No real SMS sent.
 * To switch to real SMS (MSG91/Twilio), replace sendOtp() logic only.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OtpService {

    private static final String DUMMY_OTP = "1234";
    private static final int OTP_VALIDITY_MINUTES = 10;

    private final OtpRepository otpRepository;

    // ---- STEP 1: Generate & "Send" OTP ----
    @Transactional
    public void sendOtp(String mobile) {
        // Delete any existing OTP for this mobile
        otpRepository.deleteByMobile(mobile);

        // Save new dummy OTP
        Otp otp = Otp.builder()
                .mobile(mobile)
                .code(DUMMY_OTP)
                .expiresAt(LocalDateTime.now().plusMinutes(OTP_VALIDITY_MINUTES))
                .build();
        otpRepository.save(otp);

        // In real production: call MSG91 / Twilio API here
        // For MVP: just log it
        log.info("==============================");
        log.info("📱 OTP for mobile {}: {}", mobile, DUMMY_OTP);
        log.info("==============================");
    }

    // ---- STEP 2: Verify OTP ----
    @Transactional
    public boolean verifyOtp(String mobile, String enteredOtp) {
        Optional<Otp> latestOtp = otpRepository.findTopByMobileOrderByIdDesc(mobile);

        if (latestOtp.isEmpty()) {
            log.warn("No OTP found for mobile: {}", mobile);
            return false;
        }

        Otp otp = latestOtp.get();

        // Check if already used
        if (otp.isUsed()) {
            log.warn("OTP already used for mobile: {}", mobile);
            return false;
        }

        // Check expiry
        if (LocalDateTime.now().isAfter(otp.getExpiresAt())) {
            log.warn("OTP expired for mobile: {}", mobile);
            return false;
        }

        // Check code match
        if (!otp.getCode().equals(enteredOtp.trim())) {
            log.warn("Wrong OTP entered for mobile: {}", mobile);
            return false;
        }

        // Mark OTP as used
        otp.setUsed(true);
        otpRepository.save(otp);
        log.info("✅ OTP verified for mobile: {}", mobile);
        return true;
    }
}
