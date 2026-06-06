package com.llbeauty.controller;

import com.llbeauty.entity.User;
import com.llbeauty.entity.UserMembership;
import com.llbeauty.entity.RewardPoint;
import com.llbeauty.repository.UserMembershipRepository;
import com.llbeauty.service.RewardService;
import com.llbeauty.service.WalletService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.time.LocalDateTime;
import java.util.Optional;

@Controller
public class MemberVerifyController {

    private final UserMembershipRepository userMembershipRepository;
    private final WalletService walletService;
    private final RewardService rewardService;

    public MemberVerifyController(UserMembershipRepository userMembershipRepository,
                                  WalletService walletService,
                                  RewardService rewardService) {
        this.userMembershipRepository = userMembershipRepository;
        this.walletService = walletService;
        this.rewardService = rewardService;
    }

    @GetMapping("/member/verify/{id}")
    public String verifyMembership(@PathVariable("id") String id, Model model) {
        Optional<UserMembership> umOpt = userMembershipRepository.findByUuid(id);
        if (umOpt.isEmpty()) {
            umOpt = userMembershipRepository.findByMemberId(id);
        }
        
        if (umOpt.isEmpty()) {
            model.addAttribute("error", "VERIFICATION FAILED");
            model.addAttribute("notFound", true);
            return "member_verify";
        }

        UserMembership um = umOpt.get();
        User user = um.getUser();

        // Check if active has actually expired in real time
        if ("ACTIVE".equals(um.getStatus()) && um.getExpiryDate().isBefore(LocalDateTime.now())) {
            um.setStatus("EXPIRED");
            userMembershipRepository.save(um);
        }

        model.addAttribute("um", um);
        model.addAttribute("user", user);
        
        if ("ACTIVE".equals(um.getStatus())) {
            model.addAttribute("statusMessage", "VERIFIED MEMBER");
        } else {
            model.addAttribute("statusMessage", "MEMBERSHIP EXPIRED");
        }

        return "member_verify";
    }
}
