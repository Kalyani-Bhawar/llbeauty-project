package com.llbeauty.service;

import com.llbeauty.entity.Otp;
import com.llbeauty.repository.OtpRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

/**
 * OTP Service — generates, saves, sends via EmailService, and verifies real email OTPs.
 */
@Service
public class OtpService {
    
    private static final Logger log = LoggerFactory.getLogger(OtpService.class);
    private static final int OTP_VALIDITY_MINUTES = 5;
    
    private final OtpRepository otpRepository;
    private final EmailService emailService;

    public OtpService(OtpRepository otpRepository, EmailService emailService) {
        this.otpRepository = otpRepository;
        this.emailService = emailService;
    }

    // ---- STEP 1: Generate & Send OTP ----
    @Transactional
    public void sendOtp(String email) {
        // Delete any existing OTP for this email
        otpRepository.deleteByEmail(email);
        
        // Generate a random 6-digit OTP
        int otpValue = 100000 + new Random().nextInt(900000);
        String code = String.valueOf(otpValue);
        
        // Save new OTP
        Otp otp = Otp.builder()
                .email(email)
                .code(code)
                .expiresAt(LocalDateTime.now().plusMinutes(OTP_VALIDITY_MINUTES))
                .used(false)
                .build();
        otpRepository.save(otp);
        
        // Send OTP via EmailService
        emailService.sendOtp(email, code);
        
        log.info("==============================");
        log.info("📧 OTP sent to email {}: {}", email, code);
        log.info("==============================");
    }

    // ---- STEP 2: Verify OTP ----
    @Transactional
    public boolean verifyOtp(String email, String enteredOtp) {
        Optional<Otp> latestOtp = otpRepository.findTopByEmailOrderByIdDesc(email);
        if (latestOtp.isEmpty()) {
            log.warn("No OTP found for email: {}", email);
            return false;
        }
        Otp otp = latestOtp.get();
        
        // Check if already used
        if (otp.isUsed()) {
            log.warn("OTP already used for email: {}", email);
            return false;
        }
        
        // Check expiry
        if (LocalDateTime.now().isAfter(otp.getExpiresAt())) {
            log.warn("OTP expired for email: {}", email);
            return false;
        }
        
        // Check code match
        if (!otp.getCode().equals(enteredOtp.trim())) {
            log.warn("Wrong OTP entered for email: {}", email);
            return false;
        }
        
        // Mark OTP as used
        otp.setUsed(true);
        otpRepository.save(otp);
        log.info("✅ OTP verified for email: {}", email);
        return true;
    }
}
