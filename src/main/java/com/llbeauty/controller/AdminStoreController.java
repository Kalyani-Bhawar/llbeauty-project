package com.llbeauty.controller;

import com.llbeauty.entity.*;
import com.llbeauty.repository.*;
import com.llbeauty.service.StoreApplicationService;
import com.llbeauty.service.WalletService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminStoreController {

    private final StoreApplicationRepository storeApplicationRepository;
    private final StoreApplicationService storeApplicationService;
    private final UserRepository userRepository;
    private final ExecutiveProfileRepository executiveProfileRepository;
    private final MerchantProfileRepository merchantProfileRepository;
    private final StoreCreditRepository storeCreditRepository;
    private final CommissionRepository commissionRepository;
    private final WalletRepository walletRepository;
    private final WalletService walletService;
    private final WalletTransactionRepository walletTransactionRepository;

    public AdminStoreController(StoreApplicationRepository storeApplicationRepository,
                                StoreApplicationService storeApplicationService,
                                UserRepository userRepository,
                                ExecutiveProfileRepository executiveProfileRepository,
                                MerchantProfileRepository merchantProfileRepository,
                                StoreCreditRepository storeCreditRepository,
                                CommissionRepository commissionRepository,
                                WalletRepository walletRepository,
                                WalletService walletService,
                                WalletTransactionRepository walletTransactionRepository) {
        this.storeApplicationRepository = storeApplicationRepository;
        this.storeApplicationService = storeApplicationService;
        this.userRepository = userRepository;
        this.executiveProfileRepository = executiveProfileRepository;
        this.merchantProfileRepository = merchantProfileRepository;
        this.storeCreditRepository = storeCreditRepository;
        this.commissionRepository = commissionRepository;
        this.walletRepository = walletRepository;
        this.walletService = walletService;
        this.walletTransactionRepository = walletTransactionRepository;
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

        List<StoreApplication> applications = storeApplicationRepository.findAll().stream()
                .filter(app -> app.getDeleted() == null || !app.getDeleted())
                .collect(Collectors.toList());
        if (search != null && !search.isEmpty()) {
            applications = applications.stream()
                .filter(app -> app.getUser().getName().toLowerCase().contains(search.toLowerCase()) ||
                               app.getBusinessName().toLowerCase().contains(search.toLowerCase()) ||
                               app.getContactEmail().toLowerCase().contains(search.toLowerCase()) ||
                               app.getContactPhone().contains(search))
                .collect(Collectors.toList());
        }
        if (statusFilter != null && !statusFilter.isEmpty()) {
            applications = applications.stream()
                .filter(app -> app.getStatus().name().equalsIgnoreCase(statusFilter))
                .collect(Collectors.toList());
        }
        if (typeFilter != null && !typeFilter.isEmpty()) {
            applications = applications.stream()
                .filter(app -> app.getType().name().equalsIgnoreCase(typeFilter))
                .collect(Collectors.toList());
        }
        model.addAttribute("applications", applications);

        model.addAttribute("executives", executiveProfileRepository.findAll());
        model.addAttribute("merchants", merchantProfileRepository.findAll());
        model.addAttribute("wallets", walletRepository.findAll());
        model.addAttribute("storeCredits", storeCreditRepository.findAll());
        model.addAttribute("commissions", commissionRepository.findAll());

        long totalApps = storeApplicationRepository.count();
        long pendingApps = storeApplicationRepository.findAllByStatus(ApplicationStatus.PENDING).size();
        long approvedApps = storeApplicationRepository.findAllByStatus(ApplicationStatus.APPROVED).size();
        long rejectedApps = storeApplicationRepository.findAllByStatus(ApplicationStatus.REJECTED).size();
        long totalExecutives = executiveProfileRepository.count();
        long totalMerchants = merchantProfileRepository.count();

        BigDecimal totalWalletBalance = walletRepository.findAll().stream()
            .map(Wallet::getBalance)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalStoreCredits = storeCreditRepository.findAll().stream()
            .map(StoreCredit::getBalance)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCommissions = commissionRepository.findAll().stream()
            .map(Commission::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("totalApps", totalApps);
        model.addAttribute("pendingApps", pendingApps);
        model.addAttribute("approvedApps", approvedApps);
        model.addAttribute("rejectedApps", rejectedApps);
        model.addAttribute("totalExecutives", totalExecutives);
        model.addAttribute("totalMerchants", totalMerchants);
        model.addAttribute("totalWalletBalance", totalWalletBalance);
        model.addAttribute("totalStoreCredits", totalStoreCredits);
        model.addAttribute("totalCommissions", totalCommissions);

        return "admin/store_management";
    }

    @GetMapping("/store/application/{id}")
    public String viewApplicationDetails(@PathVariable("id") Long id, Model model) {
        model.addAttribute("activeTab", "store-management");
        StoreApplication app = storeApplicationRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Application not found: " + id));
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
        if (storeApplicationRepository.existsById(id)) {
            StoreApplication app = storeApplicationRepository.findById(id).get();
            User user = app.getUser();
            if (app.getType() == ApplicationType.EXECUTIVE) {
                user.setExecutiveStatus("NOT_APPLIED");
            } else if (app.getType() == ApplicationType.MERCHANT) {
                user.setMerchantStatus("NOT_APPLIED");
            }
            userRepository.save(user);
            storeApplicationRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Application soft-deleted successfully.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Application not found.");
        }
        return "redirect:/admin/store-management?tab=applications";
    }

    @PostMapping("/store/executive/{id}/delete")
    public String deleteExecutive(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        if (executiveProfileRepository.existsById(id)) {
            ExecutiveProfile exe = executiveProfileRepository.findById(id).get();
            // Reset user executive status
            User user = exe.getUser();
            user.setExecutiveStatus("NOT_APPLIED");
            userRepository.save(user);
            executiveProfileRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Executive " + exe.getExecutiveId() + " deleted successfully.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Executive not found.");
        }
        return "redirect:/admin/store-management?tab=executives";
    }

    @PostMapping("/store/merchant/{id}/delete")
    public String deleteMerchant(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        if (merchantProfileRepository.existsById(id)) {
            MerchantProfile mer = merchantProfileRepository.findById(id).get();
            // Reset user merchant status
            User user = mer.getUser();
            user.setMerchantStatus("NOT_APPLIED");
            userRepository.save(user);
            merchantProfileRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Merchant " + mer.getMerchantId() + " deleted successfully.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Merchant not found.");
        }
        return "redirect:/admin/store-management?tab=merchants";
    }

    @PostMapping("/store/credit/add")
    public String addStoreCredit(@RequestParam("userId") Long userId,
                                 @RequestParam("amount") BigDecimal amount,
                                 RedirectAttributes redirectAttributes) {
        try {
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
            StoreCredit sc = storeCreditRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("Store credit account not found for user: " + userId));

            sc.setBalance(sc.getBalance().add(amount));
            sc.setUpdatedAt(LocalDateTime.now());
            storeCreditRepository.save(sc);

            redirectAttributes.addFlashAttribute("successMessage", "Successfully added ₹" + amount + " store credits!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error adding store credit: " + e.getMessage());
        }
        return "redirect:/admin/store-management?tab=credits";
    }
}