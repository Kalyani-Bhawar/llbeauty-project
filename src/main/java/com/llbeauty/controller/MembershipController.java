package com.llbeauty.controller;

import com.llbeauty.entity.User;
import com.llbeauty.entity.UserMembership;
import com.llbeauty.repository.UserRepository;
import com.llbeauty.service.MembershipService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/membership")
public class MembershipController {

    private final MembershipService membershipService;
    private final UserRepository userRepository;
    private final com.llbeauty.service.WalletService walletService;

    public MembershipController(MembershipService membershipService, UserRepository userRepository, com.llbeauty.service.WalletService walletService) {
        this.membershipService = membershipService;
        this.userRepository = userRepository;
        this.walletService = walletService;
    }

    private User getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            return userRepository.findByEmail(auth.getName()).orElse(null);
        }
        return null;
    }

    // Showcase page showing plans and comparisons
    @GetMapping
    public String viewPlans(Model model) {
        model.addAttribute("plans", membershipService.getAllPlans());
        User user = getAuthenticatedUser();
        if (user != null) {
            Optional<UserMembership> activeOpt = membershipService.getActiveMembership(user);
            activeOpt.ifPresent(userMembership -> model.addAttribute("activeMembership", userMembership));
            model.addAttribute("walletBalance", walletService.getBalance(user));
        } else {
            model.addAttribute("walletBalance", 0);
        }
        return "membership";
    }

    // Initiate purchase - returns Razorpay order options in JSON
    @PostMapping("/buy")
    @ResponseBody
    public ResponseEntity<?> initiateBuy(@RequestParam("planId") Long planId,
                                         @RequestParam(value = "useWallet", defaultValue = "false") boolean useWallet) {
        User user = getAuthenticatedUser();
        if (user == null) {
            Map<String, Object> err = new HashMap<>();
            err.put("error", "authentication_required");
            err.put("message", "Please login to purchase a membership.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(err);
        }

        try {
            Map<String, Object> paymentOptions = membershipService.preparePurchase(user, planId, useWallet);
            paymentOptions.put("userEmail", user.getEmail());
            paymentOptions.put("userName", user.getName());
            paymentOptions.put("userMobile", user.getMobile());
            return ResponseEntity.ok(paymentOptions);
        } catch (IllegalStateException e) {
            Map<String, Object> err = new HashMap<>();
            err.put("error", "bad_request");
            err.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
        } catch (Exception e) {
            Map<String, Object> err = new HashMap<>();
            err.put("error", "server_error");
            err.put("message", "Something went wrong: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
        }
    }

    // Confirm/Verify payment from frontend
    @PostMapping("/confirm")
    @ResponseBody
    public ResponseEntity<?> confirmPayment(@RequestParam("planId") Long planId,
                                            @RequestParam("paymentId") String paymentId,
                                            @RequestParam("orderId") String orderId,
                                            @RequestParam(value = "razorpaySignature", required = false) String razorpaySignature,
                                            @RequestParam(value = "dob", required = false) String dob,
                                            @RequestParam(value = "referralCode", required = false) String referralCode) {
        User user = getAuthenticatedUser();
        if (user == null) {
            Map<String, Object> err = new HashMap<>();
            err.put("error", "authentication_required");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(err);
        }

        try {
            UserMembership um = membershipService.activateMembership(user, planId, paymentId, orderId, razorpaySignature, dob, referralCode);
            Map<String, Object> success = new HashMap<>();
            success.put("status", "success");
            success.put("message", "Membership " + um.getMembership().getName() + " activated successfully!");
            success.put("redirectUrl", "/dashboard");
            return ResponseEntity.ok(success);
        } catch (Exception e) {
            Map<String, Object> err = new HashMap<>();
            err.put("error", "activation_failed");
            err.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
        }
    }
}
