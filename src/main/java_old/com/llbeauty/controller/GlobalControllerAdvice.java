package com.llbeauty.controller;

import com.llbeauty.entity.User;
import com.llbeauty.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Optional;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalControllerAdvice {

    private final UserRepository userRepository;

    @ModelAttribute("currentUser")
    public User currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            String mobileOrEmail = auth.getName();
            Optional<User> userOpt = userRepository.findByMobile(mobileOrEmail);
            return userOpt.orElse(null);
        }
        return null;
    }
}
