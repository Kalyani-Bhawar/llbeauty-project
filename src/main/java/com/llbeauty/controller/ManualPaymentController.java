package com.llbeauty.controller;

import com.llbeauty.entity.User;
import com.llbeauty.repository.UserRepository;
import com.llbeauty.service.ManualPaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/manual-payment")
public class ManualPaymentController {

    private final ManualPaymentService manualPaymentService;
    private final UserRepository userRepository;

    public ManualPaymentController(ManualPaymentService manualPaymentService, UserRepository userRepository) {
        this.manualPaymentService = manualPaymentService;
        this.userRepository = userRepository;
    }

    private User getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            return userRepository.findByEmail(auth.getName()).orElse(null);
        }
        return null;
    }

    @PostMapping("/submit")
    @ResponseBody
    public ResponseEntity<?> submitManualPayment(
            @RequestParam("paymentPurpose") String paymentPurpose,
            @RequestParam("amount") Double amount,
            @RequestParam("utrNumber") String utrNumber,
            @RequestParam(value = "referenceId", required = false) String referenceId,
            @RequestParam(value = "screenshot", required = false) MultipartFile screenshot) {

        User user = getAuthenticatedUser();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Login required"));
        }

        try {
            manualPaymentService.submitManualPayment(user, paymentPurpose, amount, utrNumber, referenceId, screenshot);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Payment request submitted. Admin will verify your UTR shortly.");
            
            String redirectUrl = "/dashboard";
            if ("CHECKOUT".equals(paymentPurpose)) {
                redirectUrl = "/checkout/success?orderId=" + referenceId;
            } else if ("SALON_PAYMENT".equals(paymentPurpose)) {
                redirectUrl = "/salon/success?appointmentId=" + referenceId;
            } else if ("WALLET_TOPUP".equals(paymentPurpose)) {
                redirectUrl = "/dashboard?topup=pending";
            }
            response.put("redirectUrl", redirectUrl);

            return ResponseEntity.ok(response);
            
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to upload screenshot."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "An error occurred while submitting your request."));
        }
    }
}
