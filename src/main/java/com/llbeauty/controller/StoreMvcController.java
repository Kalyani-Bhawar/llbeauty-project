package com.llbeauty.controller;

import com.llbeauty.dto.AgentApplicationRequest;

import com.llbeauty.dto.MerchantApplicationRequest;
import com.llbeauty.dto.StoreApplicationResponse;
import com.llbeauty.entity.*;
import com.llbeauty.repository.*;
import com.llbeauty.service.StoreApplicationService;
import com.llbeauty.service.WalletService;
import com.llbeauty.service.PaymentService;
import com.llbeauty.service.RazorpayService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Controller
@RequestMapping("/store")
public class StoreMvcController {

    private final UserRepository userRepository;
    private final StoreApplicationService storeApplicationService;
    private final StoreApplicationRepository storeApplicationRepository;
    private final AgentProfileRepository agentProfileRepository;
    private final MerchantProfileRepository merchantProfileRepository;
    private final StoreCreditRepository storeCreditRepository;
    private final CommissionRepository commissionRepository;
    private final UserMembershipRepository userMembershipRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final WalletService walletService;
    private final PaymentService paymentService;
    private final RazorpayService razorpayService;

    @org.springframework.beans.factory.annotation.Value("${razorpay.key.id}")
    private String razorpayKeyId;

    public StoreMvcController(UserRepository userRepository,
                              StoreApplicationService storeApplicationService,
                              StoreApplicationRepository storeApplicationRepository,
                              AgentProfileRepository agentProfileRepository,
                              MerchantProfileRepository merchantProfileRepository,
                              StoreCreditRepository storeCreditRepository,
                              CommissionRepository commissionRepository,
                              UserMembershipRepository userMembershipRepository,
                              ProductRepository productRepository,
                              OrderRepository orderRepository,
                              WalletService walletService,
                              PaymentService paymentService,
                              RazorpayService razorpayService) {
        this.userRepository = userRepository;
        this.storeApplicationService = storeApplicationService;
        this.storeApplicationRepository = storeApplicationRepository;
        this.agentProfileRepository = agentProfileRepository;
        this.merchantProfileRepository = merchantProfileRepository;
        this.storeCreditRepository = storeCreditRepository;
        this.commissionRepository = commissionRepository;
        this.userMembershipRepository = userMembershipRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.walletService = walletService;
        this.paymentService = paymentService;
        this.razorpayService = razorpayService;
    }

    private User getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            return userRepository.findByEmail(auth.getName()).orElse(null);
        }
        return null;
    }

    @GetMapping
    public String storeOpportunity(Model model) {
        User user = getAuthenticatedUser();
        if (user != null) {
            model.addAttribute("agentStatus", user.getAgentStatus() != null ? user.getAgentStatus() : "NOT_APPLIED");
            model.addAttribute("merchantStatus", user.getMerchantStatus() != null ? user.getMerchantStatus() : "NOT_APPLIED");
        } else {
            model.addAttribute("agentStatus", "NOT_APPLIED");
            model.addAttribute("merchantStatus", "NOT_APPLIED");
        }
        return "store_opportunity";
    }

    @GetMapping("/status")
    public String viewLatestStatus(Model model) {
        User user = getAuthenticatedUser();
        if (user == null) {
            return "redirect:/auth/login?redirect=/store/status";
        }

        List<StoreApplication> apps = storeApplicationRepository.findByUser(user);
        if (apps.isEmpty()) {
            return "redirect:/store";
        }

        // Sort by ID descending to get the latest one
        StoreApplication latestApp = apps.stream()
            .sorted((a, b) -> b.getId().compareTo(a.getId()))
            .findFirst()
            .get();

        model.addAttribute("application", latestApp);
        model.addAttribute("appType", latestApp.getType() == ApplicationType.AGENT ? "Agent" : "Merchant");
        model.addAttribute("status", latestApp.getStatus().name());

        return "store_status";
    }

    @GetMapping("/agent/application-status")
    public String agentApplicationStatus(Model model) {
        User user = getAuthenticatedUser();
        if (user == null) {
            return "redirect:/auth/login?redirect=/store/agent/application-status";
        }
        String status = user.getAgentStatus();
        if ("ACTIVE".equals(status)) return "redirect:/store/agent/dashboard";
        if ("NOT_APPLIED".equals(status) || status == null) return "redirect:/store/agent/apply";

        StoreApplication latestApp = storeApplicationRepository.findByUser(user).stream()
            .filter(a -> a.getType() == ApplicationType.AGENT)
            .max((a, b) -> a.getId().compareTo(b.getId()))
            .orElse(null);

        if (latestApp == null) {
            return "redirect:/store/agent/apply";
        }

        model.addAttribute("application", latestApp);
        model.addAttribute("appType", "Agent");
        model.addAttribute("status", latestApp.getStatus().name());

        return "store_status";
    }

    @GetMapping("/merchant/application-status")
    public String merchantApplicationStatus(Model model) {
        User user = getAuthenticatedUser();
        if (user == null) {
            return "redirect:/auth/login?redirect=/store/merchant/application-status";
        }
        String status = user.getMerchantStatus();
        if ("ACTIVE".equals(status)) return "redirect:/store/merchant/dashboard";
        if ("NOT_APPLIED".equals(status) || status == null) return "redirect:/store/merchant/apply";

        StoreApplication latestApp = storeApplicationRepository.findByUser(user).stream()
            .filter(a -> a.getType() == ApplicationType.MERCHANT)
            .max((a, b) -> a.getId().compareTo(b.getId()))
            .orElse(null);

        if (latestApp == null) {
            return "redirect:/store/merchant/apply";
        }

        model.addAttribute("application", latestApp);
        model.addAttribute("appType", "Merchant");
        model.addAttribute("status", latestApp.getStatus().name());

        return "store_status";
    }

    @GetMapping("/agent/apply")
    public String agentApplyForm(Model model) {
        User user = getAuthenticatedUser();
        if (user == null) {
            return "redirect:/auth/login?redirect=/store/agent/apply";
        }

        if ("ACTIVE".equals(user.getAgentStatus())) {
            return "redirect:/store/agent/dashboard";
        }

        List<StoreApplication> pending = storeApplicationRepository.findByUserAndTypeAndStatus(user, ApplicationType.AGENT, ApplicationStatus.PENDING);
        if (!pending.isEmpty()) {
            return "redirect:/store/status";
        }

        model.addAttribute("agentRequest", new AgentApplicationRequest());
        return "agent_apply";
    }

    @PostMapping("/agent/apply")
    public String submitAgentApply(@Valid @ModelAttribute("agentRequest") AgentApplicationRequest request,
                                      BindingResult bindingResult,
                                      RedirectAttributes redirectAttributes) {
        User user = getAuthenticatedUser();
        if (user == null) {
            return "redirect:/auth/login";
        }

        if ("STARTER_KIT".equalsIgnoreCase(request.getRegistrationType())) {
            bindingResult.rejectValue("registrationType", "error.request", "Starter Kit requires payment first");
        }

        if (bindingResult.hasErrors()) {
            return "agent_apply";
        }

        try {
            storeApplicationService.applyAgent(user.getId(), request);
            return "redirect:/store/status";
        } catch (Exception e) {
            bindingResult.rejectValue("fullName", "error.request", e.getMessage());
            return "agent_apply";
        }
    }

    @PostMapping("/agent/pay-initiate")
    @ResponseBody
    public ResponseEntity<?> initiateAgentPayment(@Valid @RequestBody AgentApplicationRequest request,
                                                       BindingResult bindingResult) {
        User user = getAuthenticatedUser();
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }

        if ("ACTIVE".equals(user.getAgentStatus())) {
            return ResponseEntity.badRequest().body(Map.of("error", "You are already registered as an Agent."));
        }

        List<StoreApplication> pending =
        	    storeApplicationRepository.findByUserAndTypeAndStatus(
        	        user,
        	        ApplicationType.AGENT,
        	        ApplicationStatus.PENDING
        	    );
        if (!pending.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "You already have a pending Agent application."));
        }

        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(Map.of("validationErrors", errors));
        }

        try {
            String dummyRefId = "exe_start_" + System.currentTimeMillis();
            Payment payment = paymentService.initiatePayment(user, 10000.0, "AGENT_STARTER", dummyRefId, "RAZORPAY");

            Map<String, Object> response = new HashMap<>();
            response.put("razorpayOrderId", payment.getRazorpayOrderId());
            response.put("razorpayKeyId", razorpayKeyId);
            response.put("amount", 10000.0);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/agent/apply-confirm")
    @ResponseBody
    public ResponseEntity<?> confirmAgentApply(@Valid @RequestBody AgentApplicationRequest request,
                                                   BindingResult bindingResult,
                                                   @RequestParam("paymentId") String paymentId,
                                                   @RequestParam("orderId") String orderId,
                                                   @RequestParam("signature") String signature) {
        User user = getAuthenticatedUser();
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }

        if ("ACTIVE".equals(user.getAgentStatus())) {
            return ResponseEntity.badRequest().body(Map.of("error", "You are already registered as an Agent."));
        }

        List<StoreApplication> pending = storeApplicationRepository.findByUserAndTypeAndStatus(user, ApplicationType.AGENT, ApplicationStatus.PENDING);
        if (!pending.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "You already have a pending AGENT application."));
        }

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Validation failed"));
        }

        try {
            paymentService.verifyAndProcessPayment(orderId, paymentId, signature);
            StoreApplicationResponse appResponse = storeApplicationService.applyAgent(user.getId(), request);
            return ResponseEntity.ok(Map.of("success", true, "applicationId", appResponse.getId()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/merchant/apply")
    public String merchantApplyForm(Model model) {
        User user = getAuthenticatedUser();
        if (user == null) {
            return "redirect:/auth/login?redirect=/store/merchant/apply";
        }

        if ("ACTIVE".equals(user.getMerchantStatus())) {
            return "redirect:/store/merchant/dashboard";
        }

        List<StoreApplication> pending = storeApplicationRepository.findByUserAndTypeAndStatus(user, ApplicationType.MERCHANT, ApplicationStatus.PENDING);
        if (!pending.isEmpty()) {
            return "redirect:/store/status";
        }

        model.addAttribute("merchantRequest", new MerchantApplicationRequest());
        return "merchant_apply";
    }

    @PostMapping("/merchant/pay-initiate")
    @ResponseBody
    public ResponseEntity<?> initiateMerchantPayment(@Valid @RequestBody MerchantApplicationRequest request,
                                                     BindingResult bindingResult) {
        User user = getAuthenticatedUser();
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }

        if ("ACTIVE".equals(user.getMerchantStatus())) {
            return ResponseEntity.badRequest().body(Map.of("error", "You are already registered as a Merchant."));
        }

        List<StoreApplication> pending = storeApplicationRepository.findByUserAndTypeAndStatus(user, ApplicationType.MERCHANT, ApplicationStatus.PENDING);
        if (!pending.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "You already have a pending Merchant application."));
        }

        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(Map.of("validationErrors", errors));
        }

        try {
            String dummyRefId = "merch_dep_" + System.currentTimeMillis();
            Payment payment = paymentService.initiatePayment(user, 50000.0, "MERCHANT_DEPOSIT", dummyRefId, "RAZORPAY");

            Map<String, Object> response = new HashMap<>();
            response.put("razorpayOrderId", payment.getRazorpayOrderId());
            response.put("razorpayKeyId", razorpayKeyId);
            response.put("amount", 50000.0);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/merchant/apply-confirm")
    @ResponseBody
    public ResponseEntity<?> confirmMerchantApply(@Valid @RequestBody MerchantApplicationRequest request,
                                                  BindingResult bindingResult,
                                                  @RequestParam("paymentId") String paymentId,
                                                  @RequestParam("orderId") String orderId,
                                                  @RequestParam("signature") String signature) {
        User user = getAuthenticatedUser();
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }

        if ("ACTIVE".equals(user.getMerchantStatus())) {
            return ResponseEntity.badRequest().body(Map.of("error", "You are already registered as a Merchant."));
        }

        List<StoreApplication> pending = storeApplicationRepository.findByUserAndTypeAndStatus(user, ApplicationType.MERCHANT, ApplicationStatus.PENDING);
        if (!pending.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "You already have a pending Merchant application."));
        }

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Validation failed"));
        }

        try {
            paymentService.verifyAndProcessPayment(orderId, paymentId, signature);
            StoreApplicationResponse appResponse = storeApplicationService.applyMerchant(user.getId(), request);
            return ResponseEntity.ok(Map.of("success", true, "applicationId", appResponse.getId()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/agent/dashboard")
    public String agentDashboard(Model model) {
        User user = getAuthenticatedUser();
        if (user == null) {
            return "redirect:/auth/login?redirect=/store/agent/dashboard";
        }

        if (!"ACTIVE".equals(user.getAgentStatus())) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.FORBIDDEN, "Access Denied: Requires ACTIVE Agent status");
        }

        AgentProfile profile = agentProfileRepository.findByUser(user)
            .orElseThrow(() -> new IllegalStateException("Agent profile not found"));

        List<Commission> commissions = commissionRepository.findByAgentOrderByCreatedAtDesc(profile);
        List<UserMembership> referrals = userMembershipRepository.findByReferralCode(profile.getReferralCode());
        java.math.BigDecimal totalCommission = commissions.stream()
            .map(Commission::getAmount)
            .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);

        model.addAttribute("user", user);
        model.addAttribute("profile", profile);
        model.addAttribute("commissions", commissions);
        model.addAttribute("referrals", referrals);
        model.addAttribute("totalCommission", totalCommission);
        model.addAttribute("walletBalance", walletService.getBalance(user));
        model.addAttribute("transactions", walletService.getTransactionHistory(user));

        return "agent_dashboard";
    }

    @GetMapping("/merchant/dashboard")
    public String merchantDashboard(Model model) {
        User user = getAuthenticatedUser();
        if (user == null) {
            return "redirect:/auth/login?redirect=/store/merchant/dashboard";
        }

        if (!"ACTIVE".equals(user.getMerchantStatus())) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.FORBIDDEN, "Access Denied: Requires ACTIVE Merchant status");
        }

        MerchantProfile profile = merchantProfileRepository.findByUser(user)
            .orElseThrow(() -> new IllegalStateException("Merchant profile not found"));

        StoreCredit storeCredit = storeCreditRepository.findByUser(user)
            .orElseGet(() -> storeCreditRepository.save(new StoreCredit(user, java.math.BigDecimal.ZERO)));

        List<Product> products = productRepository.findAll();
        List<Order> orders = orderRepository.findByUserOrderByCreatedAtDesc(user);

        model.addAttribute("user", user);
        model.addAttribute("profile", profile);
        model.addAttribute("storeCredit", storeCredit);
        model.addAttribute("products", products);
        model.addAttribute("orders", orders);
        model.addAttribute("walletBalance", walletService.getBalance(user));
        model.addAttribute("transactions", walletService.getTransactionHistory(user));

        return "merchant_dashboard";
    }
}
