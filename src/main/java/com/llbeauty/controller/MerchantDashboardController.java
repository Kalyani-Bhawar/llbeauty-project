package com.llbeauty.controller;

import com.llbeauty.entity.MerchantApplication;
import com.llbeauty.entity.Product;
import com.llbeauty.entity.User;
import com.llbeauty.repository.ProductRepository;
import com.llbeauty.service.MerchantApplicationService;
import com.llbeauty.service.WalletService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/merchant")
public class MerchantDashboardController {

    private final WalletService walletService;
    private final ProductRepository productRepository;
    private final MerchantApplicationService applicationService;

    public MerchantDashboardController(WalletService walletService, ProductRepository productRepository, MerchantApplicationService applicationService) {
        this.walletService = walletService;
        this.productRepository = productRepository;
        this.applicationService = applicationService;
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal User user, Model model) {
        if (user == null) {
            return "redirect:/auth/login?redirect=/merchant/dashboard";
        }
        if (!"ACTIVE".equals(user.getMerchantStatus())) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.FORBIDDEN, "Access Denied: Requires ACTIVE Merchant status");
        }
        model.addAttribute("walletBalance", walletService.getBalance(user));
        // Add more attributes as needed for dashboard
        return "merchant/dashboard";
    }

    @GetMapping("/wallet")
    public String wallet(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("walletBalance", walletService.getBalance(user));
        model.addAttribute("transactions", walletService.getTransactionHistory(user));
        return "merchant/wallet";
    }

    @GetMapping("/wholesale")
    public String wholesaleCatalog(Model model) {
        List<Product> products = productRepository.findAll();
        model.addAttribute("products", products);
        return "merchant/wholesale";
    }

    @GetMapping("/orders")
    public String orderHistory(Model model) {
        return "merchant/orders";
    }
}
