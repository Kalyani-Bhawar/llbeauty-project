package com.llbeauty.controller;

import com.llbeauty.entity.AgentProfile;
import com.llbeauty.entity.User;
import com.llbeauty.service.AgentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.Map;

@Controller
public class AgentDashboardController {

    private final AgentService agentService;

    public AgentDashboardController(AgentService agentService) {
        this.agentService = agentService;
    }

    @GetMapping("/agent/dashboard")
    public String dashboard(Model model, Principal principal) {

        String email = principal.getName();
        AgentProfile profile = agentService.getAgentProfileByEmail(email);

        if (profile == null) {
            return "redirect:/";
        }

        User user = profile.getUser();
        Map<String, Object> dashboardData = agentService.getDashboardData(profile);

        model.addAttribute("user", user);
        model.addAttribute("profile", profile);
        model.addAttribute("pendingCommission", dashboardData.get("pendingCommission"));
        model.addAttribute("lifetimeEarnings", dashboardData.get("lifetimeEarnings"));
        model.addAttribute("totalReferralsCount", dashboardData.get("totalReferralsCount"));
        model.addAttribute("lastPayoutAmount", dashboardData.get("lastPayoutAmount"));
        model.addAttribute("lastPayoutDate", dashboardData.get("lastPayoutDate"));
        model.addAttribute("lastPayoutRemarks", dashboardData.get("lastPayoutRemarks"));
        model.addAttribute("lastPayoutUtr", dashboardData.get("lastPayoutUtr"));
        model.addAttribute("activityList", dashboardData.get("activityList"));
        model.addAttribute("payoutHistory", dashboardData.get("payoutHistory"));

        return "agent_dashboard";
    }
}