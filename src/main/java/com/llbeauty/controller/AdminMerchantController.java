package com.llbeauty.controller;

import com.llbeauty.entity.StoreApplication;
import com.llbeauty.entity.ApplicationType;
import com.llbeauty.service.StoreApplicationService;
import com.llbeauty.repository.StoreApplicationRepository;
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

    private final StoreApplicationService applicationService;
    private final StoreApplicationRepository applicationRepository;

    public AdminMerchantController(StoreApplicationService applicationService, StoreApplicationRepository applicationRepository) {
        this.applicationService = applicationService;
        this.applicationRepository = applicationRepository;
    }

    @GetMapping("/applications")
    public String viewApplications(Model model) {
        List<StoreApplication> applications = applicationRepository.findAllByType(ApplicationType.MERCHANT);
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
    public String rejectApplication(@PathVariable Long id, @RequestParam(required = false, defaultValue = "Rejected by admin") String remarks, RedirectAttributes redirectAttributes) {
        try {
            applicationService.rejectApplication(id, remarks);
            redirectAttributes.addFlashAttribute("successMessage", "Merchant application rejected.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error rejecting application: " + e.getMessage());
        }
        return "redirect:/admin/merchants/applications";
    }
}
