package com.llbeauty.controller;

import com.llbeauty.entity.Merchant;
import com.llbeauty.entity.User;
import com.llbeauty.entity.UserMembership;
import com.llbeauty.entity.WalletTransaction;
import com.llbeauty.repository.MerchantRepository;
import com.llbeauty.repository.UserRepository;
import com.llbeauty.service.MembershipService;
import com.llbeauty.service.WalletService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;
import java.util.Optional;

@Controller
public class WalletController {

    private final UserRepository userRepository;
    private final WalletService walletService;
    private final MembershipService membershipService;
    private final MerchantRepository merchantRepository;

    public WalletController(UserRepository userRepository,
                            WalletService walletService,
                            MembershipService membershipService,
                            MerchantRepository merchantRepository) {
        this.userRepository = userRepository;
        this.walletService = walletService;
        this.membershipService = membershipService;
        this.merchantRepository = merchantRepository;
    }

    private User getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            return userRepository.findByMobile(auth.getName()).orElse(null);
        }
        return null;
    }

    // ==========================================
    //  USER DASHBOARD (My Membership & Wallet)
    // ==========================================
    @GetMapping("/dashboard")
    public String userDashboard(Model model) {
        User user = getAuthenticatedUser();
        if (user == null) {
            return "redirect:/auth/login?redirect=/dashboard";
        }

        // Fetch wallet details
        double balance = walletService.getBalance(user);
        List<WalletTransaction> transactions = walletService.getTransactionHistory(user);

        // Fetch active membership
        Optional<UserMembership> activeOpt = membershipService.getActiveMembership(user);

        model.addAttribute("user", user);
        model.addAttribute("walletBalance", balance);
        model.addAttribute("transactions", transactions);

        if (activeOpt.isPresent()) {
            UserMembership active = activeOpt.get();
            model.addAttribute("activeMembership", active);
            // Membership details
            model.addAttribute("membershipId", "LLB-MEMBER-" + String.format("%04d", active.getId()));
            model.addAttribute("plan", active.getMembership());
            model.addAttribute("expiryDate", active.getExpiryDate());
        }

        // Award Progress Tracker variables (Gold Pass is mid-level, Black Pass is top-level)
        int tierProgress = 0;
        if (activeOpt.isPresent()) {
            String name = activeOpt.get().getMembership().getName();
            if (name.contains("Silver")) tierProgress = 33;
            else if (name.contains("Gold")) tierProgress = 66;
            else if (name.contains("Black")) tierProgress = 100;
        }
        model.addAttribute("tierProgress", tierProgress);

        return "dashboard";
    }

    // ==========================================
    //  QR REDEMPTION FLOW
    // ==========================================
    @GetMapping("/wallet/redeem")
    public String scanRedeemPage(@RequestParam("merchantId") Long merchantId, Model model, RedirectAttributes redirectAttributes) {
        User user = getAuthenticatedUser();
        if (user == null) {
            return "redirect:/auth/login?redirect=/wallet/redeem?merchantId=" + merchantId;
        }

        Merchant merchant = merchantRepository.findById(merchantId).orElse(null);
        if (merchant == null || !"ACTIVE".equals(merchant.getStatus())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid or inactive merchant QR code.");
            return "redirect:/dashboard";
        }

        model.addAttribute("merchant", merchant);
        model.addAttribute("walletBalance", walletService.getBalance(user));
        return "wallet_redeem";
    }

    @PostMapping("/wallet/redeem/confirm")
    public String confirmRedeem(@RequestParam("merchantId") Long merchantId,
                                 @RequestParam("amount") Double amount,
                                 RedirectAttributes redirectAttributes) {
        User user = getAuthenticatedUser();
        if (user == null) {
            return "redirect:/auth/login";
        }

        Merchant merchant = merchantRepository.findById(merchantId).orElse(null);
        if (merchant == null || !"ACTIVE".equals(merchant.getStatus())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid merchant.");
            return "redirect:/dashboard";
        }

        if (amount <= 0) {
            redirectAttributes.addFlashAttribute("errorMessage", "Enter a valid redemption amount.");
            return "redirect:/wallet/redeem?merchantId=" + merchantId;
        }

        boolean success = walletService.debit(user, amount, "Redeemed at " + merchant.getName() + " via QR");
        if (success) {
            redirectAttributes.addFlashAttribute("successMessage", "Successfully redeemed ₹" + String.format("%.2f", amount) + " at " + merchant.getName() + "!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Insufficient wallet balance to redeem this amount.");
            return "redirect:/wallet/redeem?merchantId=" + merchantId;
        }

        return "redirect:/dashboard";
    }
}
