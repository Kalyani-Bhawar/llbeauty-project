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
import org.springframework.http.ResponseEntity;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import com.llbeauty.repository.PaymentRepository;
import com.llbeauty.entity.Payment;
import com.llbeauty.service.RazorpayService;
import java.math.BigDecimal;
import com.llbeauty.entity.MembershipHistory;
import com.llbeauty.repository.MembershipHistoryRepository;

@Controller
public class WalletController {

    private final UserRepository userRepository;
    private final WalletService walletService;
    private final MembershipService membershipService;
    private final MerchantRepository merchantRepository;
    private final RazorpayService razorpayService;
    private final PaymentRepository paymentRepository;
    private final MembershipHistoryRepository membershipHistoryRepository;
    private final com.llbeauty.service.RewardService rewardService;
    private final com.llbeauty.service.PaymentService paymentService;

    @org.springframework.beans.factory.annotation.Value("${razorpay.key.id}")
    private String razorpayKeyId;

    public WalletController(UserRepository userRepository,
                            WalletService walletService,
                            MembershipService membershipService,
                            MerchantRepository merchantRepository,
                            RazorpayService razorpayService,
                            PaymentRepository paymentRepository,
                            MembershipHistoryRepository membershipHistoryRepository,
                            com.llbeauty.service.RewardService rewardService,
                            com.llbeauty.service.PaymentService paymentService) {
        this.userRepository = userRepository;
        this.walletService = walletService;
        this.membershipService = membershipService;
        this.merchantRepository = merchantRepository;
        this.razorpayService = razorpayService;
        this.paymentRepository = paymentRepository;
        this.membershipHistoryRepository = membershipHistoryRepository;
        this.rewardService = rewardService;
        this.paymentService = paymentService;
    }

    private User getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            return userRepository.findByEmail(auth.getName()).orElse(null);
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
        BigDecimal balance = walletService.getBalance(user);
        List<WalletTransaction> transactions = walletService.getTransactionHistory(user);

        // Fetch active membership
        Optional<UserMembership> activeOpt = membershipService.getActiveMembership(user);

        // Fetch rewards points
        com.llbeauty.entity.RewardPoint rp = rewardService.getPoints(user);
        List<MembershipHistory> historyList = membershipHistoryRepository.findByUserOrderByStartDateDesc(user);

        model.addAttribute("user", user);
        model.addAttribute("walletBalance", balance);
        model.addAttribute("transactions", transactions);
        model.addAttribute("rewardPoint", rp);
        model.addAttribute("membershipHistory", historyList);

        if (activeOpt.isPresent()) {
            UserMembership active = activeOpt.get();
            model.addAttribute("activeMembership", active);
            // Membership details
            String mId = active.getMemberId() != null ? active.getMemberId() : ("LLB-MEMBER-" + String.format("%04d", active.getId()));
            model.addAttribute("membershipId", mId);
            model.addAttribute("plan", active.getMembership());
            model.addAttribute("expiryDate", active.getExpiryDate());
        }

        // Award Progress Tracker variables (Gold Pass is mid-level, Black Pass is top-level)
        int tierProgress = 0;
        if (activeOpt.isPresent()) {
            String name = activeOpt.get().getMembership().getName();
            if (name.contains("Pink")) tierProgress = 33;
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
                                 @RequestParam("amount") BigDecimal amount,
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

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            redirectAttributes.addFlashAttribute("errorMessage", "Enter a valid redemption amount.");
            return "redirect:/wallet/redeem?merchantId=" + merchantId;
        }

        boolean success = walletService.debit(user, amount, "Redeemed at " + merchant.getName() + " via QR", "QR_REDEEM");
        if (success) {
            redirectAttributes.addFlashAttribute("successMessage", "Successfully redeemed ₹" + amount + " at " + merchant.getName() + "!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Insufficient wallet balance to redeem this amount.");
            return "redirect:/wallet/redeem?merchantId=" + merchantId;
        }

        return "redirect:/dashboard";
    }

    // ==========================================
    //  WALLET TOP-UP VIA RAZORPAY
    // ==========================================
    @GetMapping("/wallet/topup")
    public String topupPage(Model model) {
        User user = getAuthenticatedUser();
        if (user == null) {
            return "redirect:/auth/login?redirect=/wallet/topup";
        }
        model.addAttribute("user", user);
        return "wallet_topup";
    }

    @PostMapping("/wallet/topup/initiate")
    @ResponseBody
    public ResponseEntity<?> initiateTopup(@RequestBody Map<String, Object> data) {
        try {
            BigDecimal amount = new BigDecimal(data.get("amount").toString());
            if (amount.compareTo(BigDecimal.valueOf(10)) < 0 || amount.compareTo(BigDecimal.valueOf(10000)) > 0) {
                return ResponseEntity.badRequest().body(Map.of("error", "Amount must be between ₹10 and ₹10,000"));
            }

            User user = getAuthenticatedUser();
            if (user == null) return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));

            String dummyRefId = "topup_" + System.currentTimeMillis();
            Payment payment = paymentService.initiatePayment(user, amount.doubleValue(), "WALLET_TOPUP", dummyRefId, "RAZORPAY");

            Map<String, Object> response = new HashMap<>();
            response.put("razorpayOrderId", payment.getRazorpayOrderId());
            response.put("razorpayKeyId", razorpayKeyId);
            response.put("amount", amount);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/wallet/topup/verify")
    @ResponseBody
    public ResponseEntity<?> verifyTopup(@RequestBody Map<String, String> data) {
        try {
            User user = getAuthenticatedUser();
            if (user == null) return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));

            String orderId = data.get("razorpayOrderId");
            String paymentId = data.get("razorpayPaymentId");
            String signature = data.get("razorpaySignature");

            Payment payment = null;
            try {
                payment = paymentService.verifyAndProcessPayment(orderId, paymentId, signature);
            } catch (Exception e) {
                return ResponseEntity.status(400).body(Map.of("error", "Payment verification failed."));
            }

            BigDecimal creditAmount = BigDecimal.ZERO;
            if (payment != null) {
                creditAmount = BigDecimal.valueOf(payment.getAmount());
            }

            walletService.credit(user, creditAmount, "Wallet Top-up via Razorpay", "RAZORPAY_TOPUP");

            return ResponseEntity.ok(Map.of("success", true, "newBalance", walletService.getBalance(user)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/dashboard/membership")
    public String membershipCardPage(Model model) {
        User user = getAuthenticatedUser();
        if (user == null) {
            return "redirect:/auth/login?redirect=/dashboard/membership";
        }

        Optional<UserMembership> activeOpt = membershipService.getActiveMembership(user);
        List<MembershipHistory> history = membershipHistoryRepository.findByUserOrderByStartDateDesc(user);

        model.addAttribute("user", user);
        model.addAttribute("membershipHistory", history);

        if (activeOpt.isPresent()) {
            UserMembership active = activeOpt.get();
            model.addAttribute("activeMembership", active);
            model.addAttribute("membershipId", "LLB-MEMBER-" + String.format("%04d", active.getId()));
            model.addAttribute("plan", active.getMembership());
            model.addAttribute("expiryDate", active.getExpiryDate());
            // Award Progress Tracker variables
            int tierProgress = 0;
            String name = active.getMembership().getName();
            if (name.contains("Pink")) tierProgress = 33;
            else if (name.contains("Gold")) tierProgress = 66;
            else if (name.contains("Black")) tierProgress = 100;
            model.addAttribute("tierProgress", tierProgress);
        } else {
            model.addAttribute("activeMembership", null);
        }

        return "membership_card";
    }
}
