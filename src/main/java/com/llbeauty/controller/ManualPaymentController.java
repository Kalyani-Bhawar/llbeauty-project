package com.llbeauty.controller;

import com.llbeauty.entity.ManualPaymentRequest;
import com.llbeauty.entity.User;
import com.llbeauty.repository.ManualPaymentRequestRepository;
import com.llbeauty.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/manual-payment")
public class ManualPaymentController {

    private final ManualPaymentRequestRepository manualPaymentRequestRepository;
    private final UserRepository userRepository;
    
    // Using static folder for simplicity
    private final String UPLOAD_DIR = "src/main/resources/static/uploads/";

    public ManualPaymentController(ManualPaymentRequestRepository manualPaymentRequestRepository, UserRepository userRepository) {
        this.manualPaymentRequestRepository = manualPaymentRequestRepository;
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
            ManualPaymentRequest request = new ManualPaymentRequest();
            request.setUser(user);
            request.setPaymentPurpose(paymentPurpose);
            request.setAmount(amount);
            request.setUtrNumber(utrNumber);
            request.setReferenceId(referenceId);
            request.setStatus("PENDING");

            // Handle optional screenshot upload
            if (screenshot != null && !screenshot.isEmpty()) {
                File uploadDir = new File(UPLOAD_DIR);
                if (!uploadDir.exists()) {
                    uploadDir.mkdirs();
                }
                
                String filename = UUID.randomUUID().toString() + "_" + screenshot.getOriginalFilename();
                Path filePath = Paths.get(UPLOAD_DIR, filename);
                Files.write(filePath, screenshot.getBytes());
                
                request.setScreenshotPath("/uploads/" + filename);
            }

            manualPaymentRequestRepository.save(request);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Payment request submitted. Admin will verify your UTR shortly.");
            
            // Generate redirect URL based on purpose
            String redirectUrl = "/dashboard";
            if ("CHECKOUT".equals(paymentPurpose)) {
                redirectUrl = "/checkout/success?orderId=" + referenceId; // order is pending, but we show success page
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
