package com.llbeauty.controller;

import com.llbeauty.entity.Appointment;
import com.llbeauty.entity.Payment;
import com.llbeauty.entity.User;
import com.llbeauty.repository.AppointmentRepository;
import com.llbeauty.repository.UserRepository;
import com.llbeauty.service.PaymentService;
import com.llbeauty.service.SalonPaymentService;
import com.llbeauty.service.WalletService;
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

@Controller
public class SalonPaymentController {

    private static final Logger log = LoggerFactory.getLogger(SalonPaymentController.class);

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final WalletService walletService;
    private final PaymentService paymentService;
    private final RazorpayConfig razorpayConfig;
    private final SalonPaymentService salonPaymentService;

    public SalonPaymentController(AppointmentRepository appointmentRepository,
                                  UserRepository userRepository,
                                  WalletService walletService,
                                  PaymentService paymentService,
                                  RazorpayConfig razorpayConfig,
                                  SalonPaymentService salonPaymentService) {
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
        this.walletService = walletService;
        this.paymentService = paymentService;
        this.razorpayConfig = razorpayConfig;
        this.salonPaymentService = salonPaymentService;
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

        model.addAttribute("appointment", app);
        model.addAttribute("nxlBalance", walletService.getNxlBalance(user));
        model.addAttribute("razorpayKeyId", razorpayConfig.getKeyId());
        model.addAttribute("currentUser", user);
        return "salon_payment";
    }

    @PostMapping("/salon/create-order")
    @ResponseBody
    public ResponseEntity<?> createOrder(@RequestParam("appointmentId") Long appointmentId,
                                         @RequestParam("useNxl") boolean useNxl) {
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
        if (useNxl) {
            BigDecimal walletBal = walletService.getNxlBalance(user);
            walletApplied = Math.min(walletBal.doubleValue(), total);
        }
        double amountToPay = total - walletApplied;
        String razorpayOrderId = null;
        if (amountToPay > 0 && !isDummyCredentials()) {
            try {
                Payment payment = paymentService.initiatePayment(user, amountToPay, "SALON_DEPOSIT", String.valueOf(appointmentId), "RAZORPAY" + (useNxl ? "+NXL" : ""));
                razorpayOrderId = payment.getRazorpayOrderId();
            } catch (Exception e) {
                log.error("Failed to create Razorpay order for salon booking", e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("message", "Could not connect to Razorpay: " + e.getMessage()));
            }
        } else if (amountToPay > 0) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Razorpay is not configured (dummy credentials)."));
        } else {
            razorpayOrderId = "mock_order_" + System.currentTimeMillis();
        }

        Map<String, Object> response = new HashMap<>();
        response.put("razorpayOrderId", razorpayOrderId);
        response.put("amount", amountToPay);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/salon/confirm-payment")
    @ResponseBody
    public ResponseEntity<?> confirmSalonPayment(@RequestParam("appointmentId") Long appointmentId,
                                                 @RequestParam("useNxl") boolean useNxl,
                                                 @RequestParam(value = "paymentId", required = false) String paymentId,
                                                 @RequestParam(value = "razorpayOrderId", required = false) String razorpayOrderId,
                                                 @RequestParam(value = "razorpaySignature", required = false) String razorpaySignature) {
        User user = getAuthenticatedUser();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Authentication required."));
        }

        try {
            Appointment app = salonPaymentService.confirmSalonPayment(user, appointmentId, useNxl, paymentId, razorpayOrderId, razorpaySignature, isDummyCredentials());

            log.info("Salon Appointment secured successfully. ID: {}, Token: {}", app.getId(), app.getToken());

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "redirectUrl", "/salon/success?appointmentId=" + app.getId()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        }
    }

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
