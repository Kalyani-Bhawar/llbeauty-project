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
import com.llbeauty.entity.AgentProfile;
import com.llbeauty.entity.Commission;
import com.llbeauty.repository.AgentProfileRepository;
import com.llbeauty.repository.CommissionRepository;
import java.math.BigDecimal;
import java.util.Optional;

/**
 * AuthService — handles User registration, User login (Email OTP-based) and Admin login (password).
 */
@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final OtpService otpService;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AgentProfileRepository agentProfileRepository;
    private final CommissionRepository commissionRepository;

    @org.springframework.beans.factory.annotation.Value("${admin.default.password:admin123}")
    private String defaultAdminPassword;

    public AuthService(UserRepository userRepository,
                       AdminRepository adminRepository,
                       OtpService otpService,
                       JwtUtil jwtUtil,
                       BCryptPasswordEncoder passwordEncoder,AgentProfileRepository agentProfileRepository,
                       CommissionRepository commissionRepository) {
        this.userRepository = userRepository;
        this.adminRepository = adminRepository;
        this.otpService = otpService;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.agentProfileRepository = agentProfileRepository;
        this.commissionRepository = commissionRepository;
    }

    // =========================================================
    //  USER — Register (Step 1: save user, send OTP to email)
    // =========================================================
    public boolean registerUser(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Email already registered: {}", request.getEmail());
            return false;
        }
        User user = new User();
        user.setName(request.getName());
        user.setMobile(request.getMobile());
        user.setEmail(request.getEmail());
        user.setReferralCode(request.getReferralCode());
        userRepository.save(user);
        String referralCode = request.getReferralCode();

        if (referralCode != null) {
            referralCode = referralCode.trim();
        }

        if (referralCode != null && !referralCode.isBlank()) {

            System.out.println("Referral Code = [" + referralCode + "]");

            agentProfileRepository.findByReferralCode(referralCode)
                .ifPresent(agent -> {

                    System.out.println("FOUND AGENT = " + agent.getId());

                    Commission commission = new Commission();

                    commission.setAgent(agent);
                    commission.setAmount(new BigDecimal("50"));
                    commission.setDescription("User Registration Referral");
                    commission.setStatus("APPROVED");

                    commissionRepository.save(commission);

                    System.out.println("COMMISSION SAVED");
                });
        }
        
        // Send OTP to registered email
        otpService.sendOtp(request.getEmail());
        log.info("New user registered with email: {}", request.getEmail());
        return true;
    }

    // =========================================================
    //  USER — Send OTP for Login (existing user)
    // =========================================================
    public boolean sendLoginOtp(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            log.warn("Email not registered: {}", email);
            return false;
        }
        User user = userOpt.get();
        if (user.getIsBlocked() != null && user.getIsBlocked()) {
            log.warn("Blocked user tried to send login OTP: {}", email);
            return false;
        }
        otpService.sendOtp(email);
        return true;
    }

    // =========================================================
    //  USER — Verify OTP and Issue JWT
    // =========================================================
    public String verifyOtpAndLogin(String email, String enteredOtp) {
        boolean valid = otpService.verifyOtp(email, enteredOtp);
        if (!valid) return null;

        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent() && userOpt.get().getIsBlocked() != null && userOpt.get().getIsBlocked()) {
            log.warn("Blocked user tried to login: {}", email);
            return null;
        }

        String role = userOpt.map(User::getRole).orElse("USER");
        String token = jwtUtil.generateToken(email, role);
        log.info("JWT issued for USER (Email): {} with role: {}", email, role);
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
        admin.setPassword(passwordEncoder.encode(defaultAdminPassword));
        adminRepository.save(admin);

        log.info("Default admin created -> Email: admin@llbeauty.com | Password: [CONFIGURED]");
    }
}
