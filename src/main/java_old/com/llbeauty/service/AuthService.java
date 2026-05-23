package com.llbeauty.service;

import com.llbeauty.dto.RegisterRequest;
import com.llbeauty.entity.Admin;
import com.llbeauty.entity.User;
import com.llbeauty.repository.AdminRepository;
import com.llbeauty.repository.UserRepository;
import com.llbeauty.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * AuthService — handles User registration, User login (OTP-based) and Admin login (password).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final OtpService otpService;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder;

    // =========================================================
    //  USER — Register (Step 1: save user, send OTP)
    // =========================================================
    public boolean registerUser(RegisterRequest request) {
        if (userRepository.existsByMobile(request.getMobile())) {
            log.warn("Mobile already registered: {}", request.getMobile());
            return false; // Already registered
        }

        User user = User.builder()
                .name(request.getName())
                .mobile(request.getMobile())
                .email(request.getEmail())
                .build();
        userRepository.save(user);

        // Send dummy OTP
        otpService.sendOtp(request.getMobile());
        log.info("✅ New user registered: {}", request.getMobile());
        return true;
    }

    // =========================================================
    //  USER — Send OTP for Login (existing user)
    // =========================================================
    public boolean sendLoginOtp(String mobile) {
        if (!userRepository.existsByMobile(mobile)) {
            log.warn("Mobile not registered: {}", mobile);
            return false;
        }
        otpService.sendOtp(mobile);
        return true;
    }

    // =========================================================
    //  USER — Verify OTP and Issue JWT
    // =========================================================
    public String verifyOtpAndLogin(String mobile, String enteredOtp) {
        boolean valid = otpService.verifyOtp(mobile, enteredOtp);
        if (!valid) return null;

        // Generate JWT token for USER role
        String token = jwtUtil.generateToken(mobile, "USER");
        log.info("🔐 JWT issued for USER: {}", mobile);
        return token;
    }

    // =========================================================
    //  ADMIN — Login with Email + Password
    // =========================================================
    public String adminLogin(String email, String password) {
        Optional<Admin> adminOpt = adminRepository.findByEmail(email);
        if (adminOpt.isEmpty()) {
            log.warn("Admin not found: {}", email);
            return null;
        }

        Admin admin = adminOpt.get();
        if (!passwordEncoder.matches(password, admin.getPassword())) {
            log.warn("Wrong password for admin: {}", email);
            return null;
        }

        // Generate JWT token for ADMIN role
        String token = jwtUtil.generateToken(email, "ADMIN");
        log.info("🔐 JWT issued for ADMIN: {}", email);
        return token;
    }

    // =========================================================
    //  ADMIN — Create default admin (run once on startup)
    // =========================================================
    public void createDefaultAdminIfNotExists() {
        String adminEmail = "admin@llbeauty.com";
        if (!adminRepository.existsByEmail(adminEmail)) {
            Admin admin = Admin.builder()
                    .name("L.L. Beauty Admin")
                    .email(adminEmail)
                    .password(passwordEncoder.encode("admin@123"))
                    .build();
            adminRepository.save(admin);
            log.info("✅ Default admin created → Email: {} | Password: admin@123", adminEmail);
        }
    }
}
