package com.llbeauty.controller;

import com.llbeauty.entity.Appointment;
import com.llbeauty.entity.Commission;
import com.llbeauty.entity.User;
import com.llbeauty.entity.Payment;
import com.llbeauty.repository.AgentProfileRepository;
import com.llbeauty.repository.AppointmentRepository;
import com.llbeauty.repository.CommissionRepository;
import com.llbeauty.repository.UserRepository;
import com.llbeauty.repository.PaymentRepository;
import com.llbeauty.service.WalletService;
import com.llbeauty.service.PaymentService;
import com.llbeauty.config.RazorpayConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@Controller
public class SalonPaymentController {

    private static final Logger log = LoggerFactory.getLogger(SalonPaymentController.class);

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final WalletService walletService;
    private final PaymentService paymentService;
    private final RazorpayConfig razorpayConfig;
    private final com.llbeauty.service.RewardService rewardService;
    private final AgentProfileRepository agentProfileRepository;
    private final CommissionRepository commissionRepository;
    private String referralCode;

    public SalonPaymentController(AppointmentRepository appointmentRepository,
                                  UserRepository userRepository,
                                  WalletService walletService,
                                  PaymentService paymentService,
                                  RazorpayConfig razorpayConfig,
                                  com.llbeauty.service.RewardService rewardService,AgentProfileRepository agentProfileRepository,
                                  CommissionRepository commissionRepository) {
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
        this.walletService = walletService;
        this.paymentService = paymentService;
        this.razorpayConfig = razorpayConfig;
        this.rewardService = rewardService;
        this.agentProfileRepository = agentProfileRepository;
        this.commissionRepository = commissionRepository;
    }

    private User getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            return userRepository.findByEmail(auth.getName()).orElse(null);
        }
        return null;
    }

    private boolean isDummyCredentials() {
        String key = razorpayConfig.getKeyId();
        return "rzp_test_dummy".equals(key) || key == null || key.trim().isEmpty();
    }

    // Secure advanced payment page for ₹100
    @GetMapping("/salon/payment")
    public String salonPaymentPage(@RequestParam("appointmentId") Long appointmentId, Model model) {
        User user = getAuthenticatedUser();
        if (user == null) {
            return "redirect:/auth/login?redirect=/salon/payment?appointmentId=" + appointmentId;
        }

        Appointment app = appointmentRepository.findById(appointmentId).orElse(null);

        if (app == null || !app.getUserId().equals(user.getId())) {
            return "redirect:/salon";
        }

        if ("CONFIRMED".equalsIgnoreCase(app.getStatus())) {
            return "redirect:/salon/success?appointmentId=" + app.getId();
        }

        // No longer pre-creating the Razorpay Order here.

        model.addAttribute("appointment", app);
        model.addAttribute("walletBalance", walletService.getBalance(user));
        model.addAttribute("razorpayKeyId", razorpayConfig.getKeyId());
        return "salon_payment";
    }

    // Endpoint: POST /salon/create-order → returns { razorpayOrderId, amount }
    @PostMapping("/salon/create-order")
    @ResponseBody
    public ResponseEntity<?> createOrder(@RequestParam("appointmentId") Long appointmentId,
                                         @RequestParam("useWallet") boolean useWallet) {
        User user = getAuthenticatedUser();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Login required"));
        }

        Appointment app = appointmentRepository.findById(appointmentId).orElse(null);
        if (app == null || !app.getUserId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Appointment not found"));
        }

        double total = 100.0;
        double walletApplied = 0.0;
        if (useWallet) {
            BigDecimal walletBal = walletService.getBalance(user);
            walletApplied = Math.min(walletBal.doubleValue(), total);
        }
        double amountToPay = total - walletApplied;

        String razorpayOrderId = "mock_order_" + System.currentTimeMillis();
        if (amountToPay > 0 && !isDummyCredentials()) {
            try {
                Payment payment = paymentService.initiatePayment(user, amountToPay, "SALON_DEPOSIT", String.valueOf(appointmentId), "RAZORPAY" + (useWallet ? "+WALLET" : ""));
                razorpayOrderId = payment.getRazorpayOrderId();
            } catch (Exception e) {
                log.error("Failed to create Razorpay order for salon booking", e);
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("razorpayOrderId", razorpayOrderId);
        response.put("amount", amountToPay);
        return ResponseEntity.ok(response);
    }

    // Process AJAX pay-and-confirm for appointment
    @PostMapping("/salon/confirm-payment")
    @ResponseBody
    public ResponseEntity<?> confirmSalonPayment(@RequestParam("appointmentId") Long appointmentId,
                                                 @RequestParam("useWallet") boolean useWallet,
                                                 @RequestParam(value = "paymentId", required = false) String paymentId,
                                                 @RequestParam(value = "razorpayOrderId", required = false) String razorpayOrderId,
                                                 @RequestParam(value = "razorpaySignature", required = false) String razorpaySignature) {
        User user = getAuthenticatedUser();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Authentication required."));
        }

        Appointment app = appointmentRepository.findById(appointmentId).orElse(null);
        if (app == null || !app.getUserId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Appointment not found."));
        }

        double total = 100.0;
        double walletApplied = 0.0;
        if (useWallet) {
            BigDecimal walletBal = walletService.getBalance(user);
            walletApplied = Math.min(walletBal.doubleValue(), total);
            if (walletApplied > 0) {
                boolean success = walletService.debit(user, BigDecimal.valueOf(walletApplied), "Secured advanced booking payment for Appointment #" + appointmentId, "SALON_DEPOSIT");
                if (!success) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Insufficient Wallet Credits."));
                }
            }
        }

        // Verify Razorpay signature if payment is through Razorpay
        if (razorpayOrderId != null && razorpaySignature != null && !isDummyCredentials()) {
            try {
                paymentService.verifyAndProcessPayment(razorpayOrderId, paymentId, razorpaySignature);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Invalid signature"));
            }
        }

        // Generate dynamic booking slot token e.g., LL-SLOT-7294
        int tokenNum = 1000 + new Random().nextInt(9000);
        String bookingToken = "LL-SLOT-" + tokenNum;

        app.setStatus("CONFIRMED");
        app.setPaymentStatus("PAID");
        app.setToken(bookingToken);
        appointmentRepository.save(app);
        String referralCode = app.getReferralCode();

        System.out.println(
            "SALON REF = " + referralCode
        );

        if (referralCode != null && !referralCode.isBlank()) {

            agentProfileRepository
                .findByReferralCode(referralCode)
                .ifPresent(agent -> {

                    Commission commission =
                        new Commission();

                    commission.setAgent(agent);

                    commission.setAmount(
                        new BigDecimal("100"));

                    commission.setDescription(
                        "Salon Booking Referral");

                    commission.setStatus(
                        "APPROVED");

                    commissionRepository.save(
                        commission);

                    System.out.println(
                        "SALON COMMISSION SAVED"
                    );
                });
        }

        // Award Reward Points for Salon Deposit!
        rewardService.awardPoints(user, BigDecimal.valueOf(total));

        log.info("Salon Appointment secured successfully. ID: {}, Token: {}", app.getId(), bookingToken);

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "redirectUrl", "/salon/success?appointmentId=" + app.getId()
        ));
    }

    // Confirmation Success Receipt
    @GetMapping("/salon/success")
    public String salonSuccessPage(@RequestParam("appointmentId") Long appointmentId, Model model) {
        User user = getAuthenticatedUser();
        if (user == null) {
            return "redirect:/auth/login";
        }

        Appointment app = appointmentRepository.findById(appointmentId).orElse(null);
        if (app == null || !app.getUserId().equals(user.getId())) {
            return "redirect:/salon";
        }

        model.addAttribute("appointment", app);
        return "salon_success";
    }
}
