package com.llbeauty.controller;

import com.llbeauty.entity.AgentProfile;
import com.llbeauty.entity.Commission;
import com.llbeauty.entity.FranchiseLead;
import com.llbeauty.entity.UserMembership;
import com.llbeauty.entity.User;
import com.llbeauty.repository.AgentProfileRepository;
import com.llbeauty.repository.CommissionRepository;
import com.llbeauty.repository.FranchiseLeadRepository;
import com.llbeauty.repository.UserMembershipRepository;
import com.llbeauty.service.WalletService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class AgentDashboardController {

    private final AgentProfileRepository agentProfileRepository;
    private final CommissionRepository commissionRepository;
    private final FranchiseLeadRepository franchiseLeadRepository;
    private final UserMembershipRepository userMembershipRepository;
    private final WalletService walletService;

    public AgentDashboardController(AgentProfileRepository agentProfileRepository,
                                    CommissionRepository commissionRepository,
                                    FranchiseLeadRepository franchiseLeadRepository,
                                    UserMembershipRepository userMembershipRepository,
                                    WalletService walletService) {
        this.agentProfileRepository = agentProfileRepository;
        this.commissionRepository = commissionRepository;
        this.franchiseLeadRepository = franchiseLeadRepository;
        this.userMembershipRepository = userMembershipRepository;
        this.walletService = walletService;
    }

    @GetMapping("/agent/dashboard")
    public String dashboard(Model model, Principal principal) {

        String email = principal.getName();

        AgentProfile profile = agentProfileRepository.findAll()
                .stream()
                .filter(a -> a.getUser().getEmail().equals(email))
                .findFirst()
                .orElse(null);

        if (profile == null) {
            return "redirect:/";
        }

        User user = profile.getUser();

        List<Commission> commissions = commissionRepository.findByAgentOrderByCreatedAtDesc(profile);
        List<UserMembership> referrals = userMembershipRepository.findByReferralCode(profile.getReferralCode());
        BigDecimal totalCommission = commissions.stream()
                .map(Commission::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Fetch franchise referral leads and build details map
        List<Map<String, Object>> franchiseCommissions = new ArrayList<>();
        if (profile.getReferralCode() != null && !profile.getReferralCode().isBlank()) {
            List<FranchiseLead> leads = franchiseLeadRepository.findByReferralCodeOrderByCreatedAtDesc(profile.getReferralCode());
            for (FranchiseLead lead : leads) {
                Map<String, Object> map = new HashMap<>();
                map.put("applicantName", lead.getName());
                map.put("franchiseType", lead.getFranchiseType());
                map.put("budgetRange", lead.getBudget());
                map.put("finalFranchiseAmount", lead.getFinalFranchiseAmount() != null ? lead.getFinalFranchiseAmount() : BigDecimal.ZERO);
                map.put("referralCode", lead.getReferralCode());
                map.put("status", lead.getStatus());
                
                BigDecimal commAmount = BigDecimal.ZERO;
                java.time.LocalDateTime approvalDate = null;
                if ("APPROVED".equalsIgnoreCase(lead.getStatus())) {
                    Commission matchingComm = commissions.stream()
                        .filter(c -> "FRANCHISE".equalsIgnoreCase(c.getCommissionType()) && c.getDescription().contains("Lead #" + lead.getId()))
                        .findFirst()
                        .orElse(null);
                    if (matchingComm != null) {
                        commAmount = matchingComm.getAmount();
                        approvalDate = matchingComm.getCreatedAt();
                    } else {
                        if (lead.getFinalFranchiseAmount() != null) {
                            commAmount = lead.getFinalFranchiseAmount().multiply(new BigDecimal("0.10"));
                        }
                        approvalDate = lead.getCreatedAt();
                    }
                }
                map.put("commissionAmount", commAmount);
                map.put("approvalDate", approvalDate);
                franchiseCommissions.add(map);
            }
        }

        model.addAttribute("user", user);
        model.addAttribute("profile", profile);
        model.addAttribute("commissions", commissions);
        model.addAttribute("referrals", referrals);
        model.addAttribute("totalCommission", totalCommission);
        model.addAttribute("franchiseCommissions", franchiseCommissions);
        model.addAttribute("walletBalance", walletService.getBalance(user));
        model.addAttribute("transactions", walletService.getTransactionHistory(user));

        return "agent_dashboard";
    }
}