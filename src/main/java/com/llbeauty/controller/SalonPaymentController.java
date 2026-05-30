package com.llbeauty.controller;

import com.llbeauty.entity.Appointment;
import com.llbeauty.entity.User;
import com.llbeauty.entity.Payment;
import com.llbeauty.repository.AppointmentRepository;
import com.llbeauty.repository.UserRepository;
import com.llbeauty.repository.PaymentRepository;
import com.llbeauty.service.WalletService;
import com.llbeauty.service.RazorpayService;
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
    private final RazorpayService razorpayService;
    private final PaymentRepository paymentRepository;
    private final RazorpayConfig razorpayConfig;

    public SalonPaymentController(AppointmentRepository appointmentRepository,
                                  UserRepository userRepository,
                                  WalletService walletService,
                                  RazorpayService razorpayService,
                                  PaymentRepository paymentRepository,
                                  RazorpayConfig razorpayConfig) {
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
        this.walletService = walletService;
        this.razorpayService = razorpayService;
        this.paymentRepository = paymentRepository;
        this.razorpayConfig = razorpayConfig;
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

        // Pre-create Razorpay Order for ₹100
        String razorpayOrderId = "mock_order_" + System.currentTimeMillis();
        if (!isDummyCredentials()) {
            try {
                com.razorpay.Order order = razorpayService.createOrder(100.0, "salon_init_" + appointmentId + "_" + System.currentTimeMillis());
                razorpayOrderId = order.get("id");
                
                // Track Payment
                Payment payment = new Payment();
                payment.setUser(user);
                payment.setRazorpayOrderId(razorpayOrderId);
                payment.setAmount(100.0);
                payment.setStatus("PENDING");
                payment.setPaymentMethod("RAZORPAY");
                payment.setPurpose("SALON_DEPOSIT");
                paymentRepository.save(payment);
            } catch (Exception e) {
                log.error("Failed to pre-create Razorpay order for salon booking", e);
            }
        }

        model.addAttribute("appointment", app);
        model.addAttribute("walletBalance", walletService.getBalance(user));
        model.addAttribute("razorpayKeyId", razorpayConfig.getKeyId());
        model.addAttribute("razorpayOrderId", razorpayOrderId);

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
                com.razorpay.Order order = razorpayService.createOrder(amountToPay, "salon_" + appointmentId + "_" + System.currentTimeMillis());
                razorpayOrderId = order.get("id");
                
                Payment payment = new Payment();
                payment.setUser(user);
                payment.setRazorpayOrderId(razorpayOrderId);
                payment.setAmount(amountToPay);
                payment.setStatus("PENDING");
                payment.setPaymentMethod("RAZORPAY" + (useWallet ? "+WALLET" : ""));
                payment.setPurpose("SALON_DEPOSIT");
                paymentRepository.save(payment);
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
            boolean isValid = razorpayService.verifySignature(razorpayOrderId, paymentId, razorpaySignature);
            if (!isValid) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Invalid signature"));
            }
            
            Payment payment = paymentRepository.findByRazorpayOrderId(razorpayOrderId);
            if (payment != null) {
                payment.setStatus("SUCCESS");
                payment.setRazorpayPaymentId(paymentId);
                payment.setRazorpaySignature(razorpaySignature);
                paymentRepository.save(payment);
            }
        }

        // Generate dynamic booking slot token e.g., LL-SLOT-7294
        int tokenNum = 1000 + new Random().nextInt(9000);
        String bookingToken = "LL-SLOT-" + tokenNum;

        app.setStatus("CONFIRMED");
        app.setPaymentStatus("PAID");
        app.setToken(bookingToken);
        appointmentRepository.save(app);

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
