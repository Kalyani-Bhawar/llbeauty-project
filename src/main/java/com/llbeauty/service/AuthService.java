package com.llbeauty.service;

import com.llbeauty.dto.RegisterRequest;
import com.llbeauty.entity.Admin;
import com.llbeauty.entity.User;
import com.llbeauty.repository.AdminRepository;
import com.llbeauty.repository.UserRepository;
import com.llbeauty.security.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * AuthService — handles User registration, User login (OTP-based) and Admin login (password).
 */
@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final OtpService otpService;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository,
                       AdminRepository adminRepository,
                       OtpService otpService,
                       JwtUtil jwtUtil,
                       BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.adminRepository = adminRepository;
        this.otpService = otpService;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    // =========================================================
    //  USER — Register (Step 1: save user, send OTP)
    // =========================================================
    public boolean registerUser(RegisterRequest request) {
        if (userRepository.existsByMobile(request.getMobile())) {
            log.warn("Mobile already registered: {}", request.getMobile());
            return false;
        }
        User user = new User();
        user.setName(request.getName());
        user.setMobile(request.getMobile());
        user.setEmail(request.getEmail());
        userRepository.save(user);
        // Send dummy OTP
        otpService.sendOtp(request.getMobile());
        log.info("New user registered: {}", request.getMobile());
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
        String token = jwtUtil.generateToken(mobile, "USER");
        log.info("JWT issued for USER: {}", mobile);
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
        String token = jwtUtil.generateToken(email, "ADMIN");
        log.info("JWT issued for ADMIN: {}", email);
        return token;
    }

    // =========================================================
    //  ADMIN — Reset and create default admin on every startup
    //  This DELETES all existing admin records and inserts one
    //  fresh admin with the correct BCrypt-hashed password.
    //  Email:    admin@llbeauty.com
    //  Password: admin123
    // =========================================================
    public void resetAndCreateDefaultAdmin() {
        // Step 1: Delete ALL existing admin records
        long count = adminRepository.count();
        if (count > 0) {
            adminRepository.deleteAll();
            log.info("Deleted {} existing admin record(s) from the database.", count);
        }

        // Step 2: Create fresh admin with BCrypt-hashed password
        Admin admin = new Admin();
        admin.setName("L.L. Beauty Admin");
        admin.setEmail("admin@llbeauty.com");
        admin.setPassword(passwordEncoder.encode("admin123"));
        adminRepository.save(admin);

        log.info("Default admin created -> Email: admin@llbeauty.com | Password: admin123");
    }
}
