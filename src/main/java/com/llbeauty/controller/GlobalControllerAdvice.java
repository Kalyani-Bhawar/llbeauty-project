package com.llbeauty.controller;

import com.llbeauty.entity.User;
import com.llbeauty.entity.SalonInfo;
import com.llbeauty.entity.UserMembership;
import com.llbeauty.repository.UserRepository;
import com.llbeauty.repository.SalonInfoRepository;
import com.llbeauty.repository.UserMembershipRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import java.time.LocalDateTime;
import java.util.Optional;

@ControllerAdvice
public class GlobalControllerAdvice {
    private final UserRepository userRepository;
    private final SalonInfoRepository salonInfoRepository;
    private final UserMembershipRepository userMembershipRepository;

    @ModelAttribute("currentUser")
    public User currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            String email = auth.getName();
            Optional<User> userOpt = userRepository.findByEmail(email);
            return userOpt.orElse(null);
        }
        return null;
    }

    @ModelAttribute("activeMembership")
    public UserMembership activeMembership() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            String email = auth.getName();
            Optional<User> userOpt = userRepository.findByEmail(email);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                Optional<UserMembership> activeOpt = userMembershipRepository.findByUserAndStatus(user, "ACTIVE");
                if (activeOpt.isPresent()) {
                    UserMembership um = activeOpt.get();
                    if (um.getExpiryDate().isAfter(LocalDateTime.now())) {
                        return um;
                    } else {
                        um.setStatus("EXPIRED");
                        userMembershipRepository.save(um);
                    }
                }
            }
        }
        return null;
    }

    @ModelAttribute("salonInfo")
    public SalonInfo salonInfo() {
        return salonInfoRepository.findById(1L).orElse(null);
    }

    public GlobalControllerAdvice(final UserRepository userRepository,
                                  final SalonInfoRepository salonInfoRepository,
                                  final UserMembershipRepository userMembershipRepository) {
        this.userRepository = userRepository;
        this.salonInfoRepository = salonInfoRepository;
        this.userMembershipRepository = userMembershipRepository;
    }
}
