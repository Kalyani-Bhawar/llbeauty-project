package com.llbeauty.controller;

import com.llbeauty.entity.*;
import com.llbeauty.service.AdminStoreService;
import com.llbeauty.service.StoreApplicationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.math.BigDecimal;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminStoreController {

    private final AdminStoreService adminStoreService;
    private final StoreApplicationService storeApplicationService;

    public AdminStoreController(AdminStoreService adminStoreService,
                                StoreApplicationService storeApplicationService) {
        this.adminStoreService = adminStoreService;
        this.storeApplicationService = storeApplicationService;
    }

    @GetMapping("/store-management")
    public String storeManagement(@RequestParam(value = "tab", defaultValue = "applications") String tab,
                                  @RequestParam(value = "search", required = false) String search,
                                  @RequestParam(value = "statusFilter", required = false) String statusFilter,
                                  @RequestParam(value = "typeFilter", required = false) String typeFilter,
                                  Model model) {
        model.addAttribute("activeTab", "store-management");
        model.addAttribute("currentSubTab", tab);
        model.addAttribute("statusFilter", statusFilter);
        model.addAttribute("typeFilter", typeFilter);
        model.addAttribute("search", search);

        Map<String, Object> data = adminStoreService.getStoreManagementDashboardData(search, statusFilter, typeFilter);
        
        model.addAttribute("applications", data.get("applications"));
        model.addAttribute("agents", data.get("agents"));
        model.addAttribute("merchants", data.get("merchants"));
        model.addAttribute("wallets", data.get("wallets"));
        model.addAttribute("storeCredits", data.get("storeCredits"));
        model.addAttribute("commissions", data.get("commissions"));
        model.addAttribute("agentPendingCommissions", data.get("agentPendingCommissions"));
        model.addAttribute("totalPayableCommissions", data.get("totalPayableCommissions"));
        model.addAttribute("payoutHistory", data.get("payoutHistory"));
        model.addAttribute("totalApps", data.get("totalApps"));
        model.addAttribute("pendingApps", data.get("pendingApps"));
        model.addAttribute("approvedApps", data.get("approvedApps"));
        model.addAttribute("rejectedApps", data.get("rejectedApps"));
        model.addAttribute("totalAgents", data.get("totalAgents"));
        model.addAttribute("totalMerchants", data.get("totalMerchants"));
        model.addAttribute("totalWalletBalance", data.get("totalWalletBalance"));
        model.addAttribute("totalStoreCredits", data.get("totalStoreCredits"));
        model.addAttribute("totalCommissions", data.get("totalCommissions"));

        return "admin/store_management";
    }

    @GetMapping("/store/application/{id}")
    public String viewApplicationDetails(@PathVariable("id") Long id, Model model) {
        model.addAttribute("activeTab", "store-management");
        StoreApplication app = adminStoreService.getApplicationDetails(id);
        model.addAttribute("app", app);
        return "admin/application_details";
    }

    @PostMapping("/store/application/{id}/approve")
    public String approveApplication(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            storeApplicationService.approveApplication(id);
            redirectAttributes.addFlashAttribute("successMessage", "Application approved successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error approving application: " + e.getMessage());
        }
        return "redirect:/admin/store-management";
    }

    @PostMapping("/store/application/{id}/reject")
    public String rejectApplication(@PathVariable("id") Long id,
                                    @RequestParam("remarks") String remarks,
                                    RedirectAttributes redirectAttributes) {
        try {
            storeApplicationService.rejectApplication(id, remarks);
            redirectAttributes.addFlashAttribute("successMessage", "Application rejected successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error rejecting application: " + e.getMessage());
        }
        return "redirect:/admin/store-management";
    }

    @PostMapping("/store/application/{id}/delete")
    public String deleteApplication(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            adminStoreService.deleteApplication(id);
            redirectAttributes.addFlashAttribute("successMessage", "Application soft-deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/store-management?tab=applications";
    }

    @PostMapping("/store/agent/{id}/delete")
    public String deleteAgent(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            adminStoreService.deleteAgent(id);
            redirectAttributes.addFlashAttribute("successMessage", "Agent deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/store-management?tab=agents";
    }

    @PostMapping("/store/merchant/{id}/delete")
    public String deleteMerchant(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            adminStoreService.deleteMerchant(id);
            redirectAttributes.addFlashAttribute("successMessage", "Merchant deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/store-management?tab=merchants";
    }

    @PostMapping("/store/credit/add")
    public String addStoreCredit(@RequestParam("userId") Long userId,
                                 @RequestParam("amount") BigDecimal amount,
                                 RedirectAttributes redirectAttributes) {
        try {
            adminStoreService.addStoreCredit(userId, amount);
            redirectAttributes.addFlashAttribute("successMessage", "Successfully added ₹" + amount + " store credits!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error adding store credit: " + e.getMessage());
        }
        return "redirect:/admin/store-management?tab=credits";
    }

    @PostMapping("/store/commissions/pay")
    public String payCommissions(@RequestParam("agentId") Long agentId,
                                 @RequestParam("utrNumber") String utrNumber,
                                 @RequestParam(value = "remarks", required = false) String remarks,
                                 RedirectAttributes redirectAttributes) {
        try {
            BigDecimal totalAmount = adminStoreService.payCommissions(agentId, utrNumber, remarks);
            redirectAttributes.addFlashAttribute("successMessage", "Payout of ₹" + totalAmount + " for agent marked completed successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error processing commission payout: " + e.getMessage());
        }
        return "redirect:/admin/store-management?tab=commissions";
    }
}