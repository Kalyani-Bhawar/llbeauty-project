package com.llbeauty.controller;

import com.llbeauty.entity.MerchantApplication;
import com.llbeauty.entity.User;
import com.llbeauty.service.MerchantApplicationService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class MerchantApplicationController {

    private final MerchantApplicationService applicationService;

    public MerchantApplicationController(MerchantApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @GetMapping("/apply/merchant")
    public String showApplicationForm(@AuthenticationPrincipal User user, Model model) {
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("merchantApplication", new MerchantApplication());
        return "application/merchant_form";
    }

    @PostMapping("/apply/merchant")
    public String submitApplication(@AuthenticationPrincipal User user,
                                    @ModelAttribute MerchantApplication application,
                                    RedirectAttributes redirectAttributes) {
        if (user == null) {
            return "redirect:/login";
        }
        application.setUser(user);
        applicationService.submitApplication(application);
        redirectAttributes.addFlashAttribute("successMessage", "Your merchant application has been submitted successfully and is pending approval.");
        return "redirect:/";
    }
}
