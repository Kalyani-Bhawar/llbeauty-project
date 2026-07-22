package com.llbeauty.controller;

import com.llbeauty.entity.MatrimonyProfile;
import com.llbeauty.exception.ResourceNotFoundException;
import com.llbeauty.service.AdminMatrimonyService;
import com.llbeauty.service.MatrimonyProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/matrimony")
public class AdminMatrimonyController {

    private static final Logger log = LoggerFactory.getLogger(AdminMatrimonyController.class);

    private final AdminMatrimonyService adminMatrimonyService;
    private final MatrimonyProfileService matrimonyProfileService;

    public AdminMatrimonyController(AdminMatrimonyService adminMatrimonyService,
                                    MatrimonyProfileService matrimonyProfileService) {
        this.adminMatrimonyService = adminMatrimonyService;
        this.matrimonyProfileService = matrimonyProfileService;
    }

    // ------------------------------------------------------------
    // List pending profiles
    // ------------------------------------------------------------
    @GetMapping("/pending")
    public String listPending(Model model) {
        List<MatrimonyProfile> pending = adminMatrimonyService.getPendingProfiles();
        model.addAttribute("pendingProfiles", pending);
        model.addAttribute("activeTab", "matrimony");
        return "admin/matrimony_pending";
    }

    // ------------------------------------------------------------
    // List approved profiles
    // ------------------------------------------------------------
    @GetMapping("/approved")
    public String listApproved(Model model) {
        List<MatrimonyProfile> approved = matrimonyProfileService.getApprovedProfiles();
        model.addAttribute("approvedProfiles", approved);
        model.addAttribute("activeTab", "matrimony");
        return "admin/matrimony_approved";
    }
    
    @GetMapping("/rejected")
    public String listRejected(Model model) {
        model.addAttribute("rejectedProfiles", adminMatrimonyService.getRejectedProfiles());
        return "admin/matrimony_rejected";
    }

    @GetMapping("/blocked")
    public String listBlocked(Model model) {
        model.addAttribute("blockedProfiles", adminMatrimonyService.getBlockedProfiles());
        return "admin/matrimony_blocked";
    }

    // ------------------------------------------------------------
    // Detailed profile view for inspection
    // ------------------------------------------------------------
    @GetMapping("/profile-view/{id}")
    public String viewProfile(@PathVariable("id") Long id, Model model) {
        MatrimonyProfile profile = matrimonyProfileService.getProfileByUserId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Matrimony profile not found with user id: " + id));
        model.addAttribute("profile", profile);
        model.addAttribute("activeTab", "matrimony");
        return "admin/matrimony_profile_view";
    }

    // ------------------------------------------------------------
    // Approve profile
    // ------------------------------------------------------------
    @PostMapping("/approve/{id}")
    public String approve(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            adminMatrimonyService.approveProfile(id);
            redirectAttributes.addFlashAttribute("successMessage", "Profile approved successfully!");
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/matrimony/pending";
    }

    // ------------------------------------------------------------
    // Reject profile
    // ------------------------------------------------------------
    @PostMapping("/reject/{id}")
    public String reject(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            adminMatrimonyService.rejectProfile(id);
            redirectAttributes.addFlashAttribute("successMessage", "Profile rejected successfully!");
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/matrimony/pending";
    }

    // ------------------------------------------------------------
    // Block profile
    // ------------------------------------------------------------
    @PostMapping("/block/{id}")
    public String block(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            adminMatrimonyService.blockProfile(id);
            redirectAttributes.addFlashAttribute("successMessage", "Profile blocked successfully!");
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/matrimony/pending";
    }
}