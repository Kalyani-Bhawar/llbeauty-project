package com.llbeauty.controller;

import com.llbeauty.entity.AgentProfile;
import com.llbeauty.entity.Commission;
import com.llbeauty.repository.AgentProfileRepository;
import com.llbeauty.repository.CommissionRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

@Controller
public class AgentDashboardController {

    private final AgentProfileRepository agentProfileRepository;
    private final CommissionRepository commissionRepository;

    public AgentDashboardController(AgentProfileRepository agentProfileRepository,
                                    CommissionRepository commissionRepository) {
        this.agentProfileRepository = agentProfileRepository;
        this.commissionRepository = commissionRepository;
    }

    @GetMapping("/agent/dashboard")
    public String dashboard(Model model, Principal principal) {

        String email = principal.getName();

        AgentProfile agent = agentProfileRepository.findAll()
                .stream()
                .filter(a -> a.getUser().getEmail().equals(email))
                .findFirst()
                .orElse(null);

        if (agent == null) {
            return "redirect:/";
        }

        List<Commission> commissions =
                commissionRepository.findByAgentOrderByCreatedAtDesc(agent);

        BigDecimal totalCommission =
                commissions.stream()
                        .map(Commission::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("agent", agent);
        model.addAttribute("commissions", commissions);
        model.addAttribute("totalCommission", totalCommission);
        model.addAttribute("totalReferrals", commissions.size());

        return "agent_dashboard";
    }
}