package com.llbeauty.controller;

import com.llbeauty.dto.ApplicationStatusResponse;

import com.llbeauty.dto.AgentApplicationRequest;
import com.llbeauty.dto.MerchantApplicationRequest;
import com.llbeauty.dto.StoreApplicationResponse;
import com.llbeauty.entity.User;
import com.llbeauty.repository.UserRepository;
import com.llbeauty.service.StoreApplicationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/store")
public class StoreController {

    private final StoreApplicationService storeService;
    private final UserRepository userRepository;

    public StoreController(StoreApplicationService storeService, UserRepository userRepository) {
        this.storeService = storeService;
        this.userRepository = userRepository;
    }

    private User getCurrentUser(@AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Authenticated user not found"));
    }

    @PostMapping("/agent/apply")
    public ResponseEntity<StoreApplicationResponse> applyAgent(
            @AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails userDetails,
            @Valid @RequestBody AgentApplicationRequest request) {
        User user = getCurrentUser(userDetails);
        StoreApplicationResponse response = storeService.applyAgent(user.getId(), request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/merchant/apply")
    public ResponseEntity<StoreApplicationResponse> applyMerchant(
            @AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails userDetails,
            @Valid @RequestBody MerchantApplicationRequest request) {
        User user = getCurrentUser(userDetails);
        StoreApplicationResponse response = storeService.applyMerchant(user.getId(), request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status/{applicationId}")
    public ResponseEntity<ApplicationStatusResponse> getStatus(@PathVariable Long applicationId) {
        ApplicationStatusResponse status = storeService.getApplicationStatus(applicationId);
        return ResponseEntity.ok(status);
    }
}
