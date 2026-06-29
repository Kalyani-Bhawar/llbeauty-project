package com.llbeauty.config;

import com.llbeauty.entity.User;
import com.llbeauty.repository.UserRepository;
import com.llbeauty.service.WalletService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import java.math.BigDecimal;

@ControllerAdvice
public class GlobalModelAdvice {

    private final WalletService walletService;
    private final UserRepository userRepository;

    public GlobalModelAdvice(WalletService walletService, UserRepository userRepository) {
        this.walletService = walletService;
        this.userRepository = userRepository;
    }

    @ModelAttribute
    public void addNxlBalance(Model model) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() 
                && !"anonymousUser".equals(auth.getName())) {
                User user = userRepository.findByEmail(auth.getName()).orElse(null);
                if (user != null) {
                    model.addAttribute("nxlBalance", walletService.getNxlBalance(user));
                    return;
                }
            }
        } catch (Exception ignored) {}
        model.addAttribute("nxlBalance", BigDecimal.ZERO);
    }
}