package com.llbeauty.service;

import com.llbeauty.entity.AuditLog;
import com.llbeauty.entity.User;
import com.llbeauty.repository.AuditLogRepository;
import com.llbeauty.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class AdminUserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuditLogRepository auditLogRepository;

    public AdminUserService(UserRepository userRepository,
                            BCryptPasswordEncoder passwordEncoder,
                            AuditLogRepository auditLogRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditLogRepository = auditLogRepository;
    }

    @Transactional
    public void blockUser(Long id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setIsBlocked(true);
            userRepository.save(user);
        } else {
            throw new IllegalArgumentException("User not found");
        }
    }

    @Transactional
    public void unblockUser(Long id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setIsBlocked(false);
            userRepository.save(user);
        } else {
            throw new IllegalArgumentException("User not found");
        }
    }

    @Transactional
    public void resetUserPassword(Long id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setPassword(passwordEncoder.encode("123456"));
            userRepository.save(user);
        } else {
            throw new IllegalArgumentException("User not found");
        }
    }

    @Transactional
    public void updateUserWallet(Long id, Double balance) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setWalletBalance(BigDecimal.valueOf(balance));
            userRepository.save(user);
        } else {
            throw new IllegalArgumentException("User not found");
        }
    }

    @Transactional
    public void deleteUser(Long id, String currentEmail) {
        Optional<User> userToDelete = userRepository.findById(id);
        if (userToDelete.isPresent()) {
            if (currentEmail.equals(userToDelete.get().getEmail())) {
                throw new IllegalArgumentException("You cannot delete your own account.");
            }
            User user = userToDelete.get();
            user.setActive(false);
            userRepository.save(user);

            AuditLog log = new AuditLog("USER_DELETED", "User " + user.getEmail() + " deleted", currentEmail);
            auditLogRepository.save(log);
        } else {
            throw new IllegalArgumentException("User not found");
        }
    }

    @Transactional
    public void activateUser(Long id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setActive(true);
            userRepository.save(user);
        } else {
            throw new IllegalArgumentException("User not found");
        }
    }
}
