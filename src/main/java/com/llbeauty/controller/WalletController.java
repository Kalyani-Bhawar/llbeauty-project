package com.llbeauty.controller;

import com.llbeauty.entity.Merchant;
import com.llbeauty.constants.NxlConstants;
import com.llbeauty.entity.User;
import com.llbeauty.entity.UserMembership;
import com.llbeauty.entity.WalletTransaction;
import com.llbeauty.exception.NxlException;
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

        // ⬇️ NXL wallet balance + history (ata Nxl tables var shift kela)
        BigDecimal balance = walletService.getNxlBalance(user);
        List<com.llbeauty.entity.NxlWalletTransaction> transactions = walletService.getNxlHistory(user);

        // Fetch active membership
        Optional<UserMembership> activeOpt = membershipService.getActiveMembership(user);

        // Fetch rewards points
        com.llbeauty.entity.RewardPoint rp = rewardService.getPoints(user);
        List<MembershipHistory> historyList = membershipHistoryRepository.findByUserOrderByStartDateDesc(user);

        model.addAttribute("user", user);
        model.addAttribute("walletBalance", balance);
        model.addAttribute("nxlBalance", balance);
        model.addAttribute("transactions", transactions);
        model.addAttribute("rewardPoint", rp);
        model.addAttribute("membershipHistory", historyList);

        if (activeOpt.isPresent()) {
            UserMembership active = activeOpt.get();
            model.addAttribute("activeMembership", active);
            String mId = active.getMemberId() != null ? active.getMemberId() : ("LLB-MEMBER-" + String.format("%04d", active.getId()));
            model.addAttribute("membershipId", mId);
            model.addAttribute("plan", active.getMembership());
            model.addAttribute("expiryDate", active.getExpiryDate());
        }

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
        model.addAttribute("walletBalance", walletService.getNxlBalance(user));
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

        // ⬇️ FIXED: boolean nahi, ata try-catch (NxlException) vaprla.
        // ⬇️ FIXED: SOURCE_BOOKING ("BOOKING") NxlConstants.ALLOWED_SOURCES madhe nahiye, mhanun "LL_BEAUTY" vaprla.
        try {
            walletService.debitNxl(
                    user,
                    amount,
                    NxlConstants.SOURCE_LLBEAUTY,
                    merchantId.toString(),
                    "QR_REDEEM at " + merchant.getName()
            );
            redirectAttributes.addFlashAttribute("successMessage", "Successfully redeemed ₹" + amount + " at " + merchant.getName() + "!");
        } catch (NxlException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
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
        model.addAttribute("walletBalance", walletService.getNxlBalance(user)); // ⬅️ NAVIN LINE
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

            boolean isDummy = razorpayKeyId == null
                || razorpayKeyId.trim().isEmpty()
                || "rzp_test_dummy".equals(razorpayKeyId);

            Map<String, Object> response = new HashMap<>();
            response.put("amount", amount);

            if (isDummy) {
                // ✅ Mock flow — Razorpay call नाही
                response.put("razorpayOrderId", "mock_topup_" + System.currentTimeMillis());
                response.put("razorpayKeyId", "mock_key");
                response.put("useMock", true);
            } else {
                // Real Razorpay
                String refId = "topup_" + System.currentTimeMillis();
                Payment payment = paymentService.initiatePayment(user, amount.doubleValue(), "WALLET_TOPUP", refId, "RAZORPAY");
                response.put("razorpayOrderId", payment.getRazorpayOrderId());
                response.put("razorpayKeyId", razorpayKeyId);
                response.put("useMock", false);
            }
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

            String orderId   = data.get("razorpayOrderId");
            String amountStr = data.get("amount");

            boolean isDummy = razorpayKeyId == null
                || razorpayKeyId.trim().isEmpty()
                || "rzp_test_dummy".equals(razorpayKeyId)
                || (orderId != null && orderId.startsWith("mock_"));

            BigDecimal creditAmount = BigDecimal.ZERO;

            if (isDummy) {
                // ✅ Mock — amount directly from request
                if (amountStr != null && !amountStr.isEmpty()) {
                    creditAmount = new BigDecimal(amountStr);
                }
            } else {
                // Real Razorpay verify
                try {
                    Payment payment = paymentService.verifyAndProcessPayment(
                        orderId,
                        data.get("razorpayPaymentId"),
                        data.get("razorpaySignature")
                    );
                    if (payment != null) creditAmount = BigDecimal.valueOf(payment.getAmount());
                } catch (Exception e) {
                    return ResponseEntity.status(400).body(Map.of("error", "Payment verification failed."));
                }
            }

            // ✅ 5% BONUS: ₹100 pay → 105 NXL
            BigDecimal bonus       = creditAmount.multiply(new BigDecimal("0.05"))
                                                 .setScale(2, java.math.RoundingMode.HALF_UP);
            BigDecimal totalCredit = creditAmount.add(bonus);

            try {
                walletService.creditNxl(
                    user,
                    totalCredit,
                    NxlConstants.SOURCE_LLBEAUTY,
                    orderId != null ? orderId : "topup_" + System.currentTimeMillis(),
                    "Wallet Top-up ₹" + creditAmount + " + 5% bonus"
                );
            } catch (NxlException e) {
                return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
            }

            return ResponseEntity.ok(Map.of(
                "success",    true,
                "newBalance", walletService.getNxlBalance(user),
                "credited",   totalCredit,
                "bonus",      bonus
            ));
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