package com.llbeauty.controller;

import com.llbeauty.entity.Product;
import com.llbeauty.entity.User;
import com.llbeauty.entity.MerchantProfile;
import com.llbeauty.entity.MerchantOrder;
import com.llbeauty.repository.ProductRepository;
import com.llbeauty.repository.MerchantProfileRepository;
import com.llbeauty.repository.MerchantOrderRepository;
import com.llbeauty.repository.UserRepository;
import com.llbeauty.service.WalletService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/merchant")
public class MerchantDashboardController {

    private static final Logger log = LoggerFactory.getLogger(MerchantDashboardController.class);

    private final WalletService walletService;
    private final ProductRepository productRepository;
    private final MerchantProfileRepository merchantProfileRepository;
    private final MerchantOrderRepository merchantOrderRepository;
    private final UserRepository userRepository;

    public MerchantDashboardController(WalletService walletService, 
                                       ProductRepository productRepository, 
                                       MerchantProfileRepository merchantProfileRepository,
                                       MerchantOrderRepository merchantOrderRepository,
                                       UserRepository userRepository) {
        this.walletService = walletService;
        this.productRepository = productRepository;
        this.merchantProfileRepository = merchantProfileRepository;
        this.merchantOrderRepository = merchantOrderRepository;
        this.userRepository = userRepository;
    }

    private User getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            return userRepository.findByEmail(auth.getName()).orElse(null);
        }
        return null;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        User user = getAuthenticatedUser();
        if (user == null) {
            return "redirect:/auth/login?redirect=/merchant/dashboard";
        }
        if (!"ACTIVE".equals(user.getMerchantStatus())) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.FORBIDDEN, "Access Denied: Requires ACTIVE Merchant status");
        }
        model.addAttribute("walletBalance", walletService.getNxlBalance(user));
        
        MerchantProfile profile = merchantProfileRepository.findByUser(user).orElse(null);
        model.addAttribute("profile", profile);
        
        long totalOrders = merchantOrderRepository.countByUserAndOrderStatus(user, "SUCCESS");
        Double totalPurchaseValue = merchantOrderRepository.sumTotalAmountByUser(user);
        Double totalSavings = merchantOrderRepository.sumTotalSavingsByUser(user);
        
        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("totalPurchaseValue", totalPurchaseValue != null ? totalPurchaseValue : 0.0);
        model.addAttribute("totalSavings", totalSavings != null ? totalSavings : 0.0);
        
        List<Product> products = productRepository.findAll();
        model.addAttribute("products", products);
        
        List<MerchantOrder> orders = merchantOrderRepository.findByUserOrderByCreatedAtDesc(user);
        model.addAttribute("orders", orders);
        
        return "merchant_dashboard";
    }

    @GetMapping("/wallet")
    public String wallet(Model model) {
        User user = getAuthenticatedUser();
        if (user == null) {
            return "redirect:/auth/login?redirect=/merchant/wallet";
        }
        model.addAttribute("walletBalance", walletService.getNxlBalance(user));
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
        User user = getAuthenticatedUser();
        if (user == null || !"ACTIVE".equals(user.getMerchantStatus())) {
            return "redirect:/auth/login?redirect=/merchant/orders";
        }
        List<MerchantOrder> orders = merchantOrderRepository.findByUserOrderByCreatedAtDesc(user);
        // Null-safety: default status for any order missing a status
        orders.forEach(o -> {
            if (o.getOrderStatus() == null) o.setOrderStatus("PENDING");
        });
        model.addAttribute("orders", orders);
        return "merchant/orders";
    }

    // ──────────────────────────────────────────────────────────────────
    // Order Detail Page — GET /merchant/orders/{id}
    // ──────────────────────────────────────────────────────────────────

    @GetMapping("/orders/{id}")
    public String orderDetails(@PathVariable Long id,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        User user = getAuthenticatedUser();
        if (user == null || !"ACTIVE".equals(user.getMerchantStatus())) {
            return "redirect:/auth/login?redirect=/merchant/orders/" + id;
        }

        MerchantOrder order = merchantOrderRepository.findById(id).orElse(null);
        if (order == null) {
            log.warn("Order not found: id={}", id);
            redirectAttributes.addFlashAttribute("errorMessage", "Order #" + id + " not found.");
            return "redirect:/merchant/orders";
        }

        // Security: merchant can only view their own orders
        if (!order.getUser().getId().equals(user.getId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Access denied.");
            return "redirect:/merchant/orders";
        }

        // Null-safety defaults
        if (order.getOrderStatus() == null) order.setOrderStatus("PENDING");
        if (order.getItems() == null) order.setItems(new ArrayList<>());

        model.addAttribute("order", order);
        model.addAttribute("items", order.getItems());
        return "merchant/order-details";
    }
}
