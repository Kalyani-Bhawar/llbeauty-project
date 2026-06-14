package com.llbeauty.controller;

import com.llbeauty.entity.MerchantApplication;
import com.llbeauty.service.MerchantApplicationService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/merchants")
@PreAuthorize("hasRole('ADMIN')")
public class AdminMerchantController {

    private final MerchantApplicationService applicationService;

    public AdminMerchantController(MerchantApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @GetMapping("/applications")
    public String viewApplications(Model model) {
        List<MerchantApplication> applications = applicationService.getAllApplications();
        model.addAttribute("applications", applications);
        model.addAttribute("activeTab", "merchant-applications");
        return "admin/merchant_applications";
    }

    @PostMapping("/applications/{id}/approve")
    public String approveApplication(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            applicationService.approveApplication(id);
            redirectAttributes.addFlashAttribute("successMessage", "Merchant application approved successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error approving application: " + e.getMessage());
        }
        return "redirect:/admin/merchants/applications";
    }

    @PostMapping("/applications/{id}/reject")
    public String rejectApplication(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            applicationService.rejectApplication(id);
            redirectAttributes.addFlashAttribute("successMessage", "Merchant application rejected.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error rejecting application: " + e.getMessage());
        }
        return "redirect:/admin/merchants/applications";
    }
}
