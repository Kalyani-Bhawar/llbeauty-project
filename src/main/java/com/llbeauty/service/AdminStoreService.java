package com.llbeauty.service;

import com.llbeauty.entity.*;
import com.llbeauty.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AdminStoreService {

    private final StoreApplicationRepository storeApplicationRepository;
    private final UserRepository userRepository;
    private final AgentProfileRepository agentProfileRepository;
    private final MerchantProfileRepository merchantProfileRepository;
    private final StoreCreditRepository storeCreditRepository;
    private final CommissionRepository commissionRepository;
    private final WalletRepository walletRepository;
    private final PayoutRepository payoutRepository;

    public AdminStoreService(StoreApplicationRepository storeApplicationRepository,
                             UserRepository userRepository,
                             AgentProfileRepository agentProfileRepository,
                             MerchantProfileRepository merchantProfileRepository,
                             StoreCreditRepository storeCreditRepository,
                             CommissionRepository commissionRepository,
                             WalletRepository walletRepository,
                             PayoutRepository payoutRepository) {
        this.storeApplicationRepository = storeApplicationRepository;
        this.userRepository = userRepository;
        this.agentProfileRepository = agentProfileRepository;
        this.merchantProfileRepository = merchantProfileRepository;
        this.storeCreditRepository = storeCreditRepository;
        this.commissionRepository = commissionRepository;
        this.walletRepository = walletRepository;
        this.payoutRepository = payoutRepository;
    }

    public Map<String, Object> getStoreManagementDashboardData(String search, String statusFilter, String typeFilter) {
        Map<String, Object> data = new HashMap<>();

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
        data.put("applications", applications);

        data.put("agents", agentProfileRepository.findAll());
        data.put("merchants", merchantProfileRepository.findAll());
        data.put("wallets", walletRepository.findAll());
        data.put("storeCredits", storeCreditRepository.findAll());
        data.put("commissions", commissionRepository.findAll());

        List<AgentProfile> agentsList = agentProfileRepository.findAll();
        List<Map<String, Object>> agentPendingCommissions = new ArrayList<>();
        BigDecimal totalPayableCommissions = BigDecimal.ZERO;

        List<Commission> allCommissions = commissionRepository.findAll();
        for (AgentProfile agent : agentsList) {
            BigDecimal pending = allCommissions.stream()
                .filter(c -> c.getAgent().getId().equals(agent.getId()) &&
                             ("PENDING".equalsIgnoreCase(c.getStatus()) || "APPROVED".equalsIgnoreCase(c.getStatus())))
                .map(Commission::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

            if (pending.compareTo(BigDecimal.ZERO) > 0) {
                Map<String, Object> map = new HashMap<>();
                map.put("agent", agent);
                map.put("pendingAmount", pending);
                agentPendingCommissions.add(map);
                totalPayableCommissions = totalPayableCommissions.add(pending);
            }
        }
        data.put("agentPendingCommissions", agentPendingCommissions);
        data.put("totalPayableCommissions", totalPayableCommissions);

        List<Payout> payoutHistory = payoutRepository.findAll().stream()
            .sorted((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt()))
            .collect(Collectors.toList());
        data.put("payoutHistory", payoutHistory);

        data.put("totalApps", storeApplicationRepository.count());
        data.put("pendingApps", storeApplicationRepository.findAllByStatus(ApplicationStatus.PENDING).size());
        data.put("approvedApps", storeApplicationRepository.findAllByStatus(ApplicationStatus.APPROVED).size());
        data.put("rejectedApps", storeApplicationRepository.findAllByStatus(ApplicationStatus.REJECTED).size());
        data.put("totalAgents", agentProfileRepository.count());
        data.put("totalMerchants", merchantProfileRepository.count());

        BigDecimal totalWalletBalance = walletRepository.findAll().stream()
            .map(Wallet::getBalance)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalStoreCredits = storeCreditRepository.findAll().stream()
            .map(StoreCredit::getBalance)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCommissions = commissionRepository.findAll().stream()
            .map(Commission::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        data.put("totalWalletBalance", totalWalletBalance);
        data.put("totalStoreCredits", totalStoreCredits);
        data.put("totalCommissions", totalCommissions);

        return data;
    }

    public StoreApplication getApplicationDetails(Long id) {
        return storeApplicationRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Application not found: " + id));
    }

    @Transactional
    public void deleteApplication(Long id) {
        StoreApplication app = storeApplicationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Application not found."));
        User user = app.getUser();
        if (app.getType() == ApplicationType.AGENT) {
            user.setAgentStatus("NOT_APPLIED");
        } else if (app.getType() == ApplicationType.MERCHANT) {
            user.setMerchantStatus("NOT_APPLIED");
        }
        userRepository.save(user);
        storeApplicationRepository.deleteById(id);
    }

    @Transactional
    public void deleteAgent(Long id) {
        AgentProfile exe = agentProfileRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Agent not found."));
        User user = exe.getUser();
        user.setAgentStatus("NOT_APPLIED");
        userRepository.save(user);
        agentProfileRepository.deleteById(id);
    }

    @Transactional
    public void deleteMerchant(Long id) {
        MerchantProfile mer = merchantProfileRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Merchant not found."));
        User user = mer.getUser();
        user.setMerchantStatus("NOT_APPLIED");
        userRepository.save(user);
        merchantProfileRepository.deleteById(id);
    }

    @Transactional
    public void addStoreCredit(Long userId, BigDecimal amount) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        StoreCredit sc = storeCreditRepository.findByUser(user)
            .orElseThrow(() -> new IllegalArgumentException("Store credit account not found for user: " + userId));

        sc.setBalance(sc.getBalance().add(amount));
        sc.setUpdatedAt(LocalDateTime.now());
        storeCreditRepository.save(sc);
    }

    @Transactional
    public BigDecimal payCommissions(Long agentId, String utrNumber, String remarks) {
        AgentProfile agent = agentProfileRepository.findById(agentId)
            .orElseThrow(() -> new IllegalArgumentException("Agent not found: " + agentId));
        
        List<Commission> pendingCommissions = commissionRepository.findByAgentOrderByCreatedAtDesc(agent).stream()
            .filter(c -> "PENDING".equalsIgnoreCase(c.getStatus()) || "APPROVED".equalsIgnoreCase(c.getStatus()))
            .collect(Collectors.toList());
        
        if (pendingCommissions.isEmpty()) {
            throw new IllegalStateException("No pending commissions to pay for this agent.");
        }
        
        BigDecimal totalAmount = pendingCommissions.stream()
            .map(Commission::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        for (Commission commission : pendingCommissions) {
            commission.setStatus("PAID");
            commissionRepository.save(commission);
        }
        
        Payout payout = new Payout();
        payout.setAgent(agent);
        payout.setAmount(totalAmount);
        payout.setPaymentMethod("Bank Transfer");
        payout.setUtrNumber(utrNumber);
        payout.setStatus("PAID");
        payout.setRemarks(remarks);
        payout.setCreatedAt(LocalDateTime.now());
        payoutRepository.save(payout);

        return totalAmount;
    }
}
