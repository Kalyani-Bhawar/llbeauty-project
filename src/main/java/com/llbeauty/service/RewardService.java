package com.llbeauty.service;

import com.llbeauty.entity.*;
import com.llbeauty.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class RewardService {

    private final RewardPointRepository rewardPointRepository;
    private final RewardTransactionRepository rewardTransactionRepository;
    private final UserMembershipRepository userMembershipRepository;

    public RewardService(RewardPointRepository rewardPointRepository,
                         RewardTransactionRepository rewardTransactionRepository,
                         UserMembershipRepository userMembershipRepository) {
        this.rewardPointRepository = rewardPointRepository;
        this.rewardTransactionRepository = rewardTransactionRepository;
        this.userMembershipRepository = userMembershipRepository;
    }

    public RewardPoint getPoints(User user) {
        if (user == null) return null;
        return rewardPointRepository.findByUser(user).orElseGet(() -> {
            RewardPoint rp = new RewardPoint(user, 0, 0, 0);
            return rewardPointRepository.save(rp);
        });
    }

    @Transactional
    public void awardPoints(User user, BigDecimal amountSpent) {
        if (user == null || amountSpent == null || amountSpent.compareTo(BigDecimal.ZERO) <= 0) return;
        
        Optional<UserMembership> activeOpt = userMembershipRepository.findByUserAndStatus(user, "ACTIVE");
        if (activeOpt.isEmpty()) return; // Non-members do not earn reward points
        
        UserMembership active = activeOpt.get();
        String planName = active.getMembership().getName();
        int multiplier = 0;
        if (planName.contains("Pink")) {
            multiplier = 1;
        } else if (planName.contains("Gold")) {
            multiplier = 2;
        } else if (planName.contains("Black")) {
            multiplier = 3;
        }
        
        if (multiplier == 0) return;
        
        int pointsEarned = amountSpent.divide(BigDecimal.valueOf(100)).intValue() * multiplier;
        if (pointsEarned <= 0) return;
        
        RewardPoint rp = getPoints(user);
        rp.setAvailablePoints(rp.getAvailablePoints() + pointsEarned);
        rp.setTotalPoints(rp.getTotalPoints() + pointsEarned);
        rewardPointRepository.save(rp);
        
        RewardTransaction rt = new RewardTransaction(user, pointsEarned, "CREDIT", "Earned reward points on purchase of value ₹" + amountSpent.setScale(2, BigDecimal.ROUND_HALF_UP));
        rewardTransactionRepository.save(rt);
    }

    @Transactional
    public boolean redeemPoints(User user, int pointsToRedeem) {
        if (user == null || pointsToRedeem <= 0) return false;
        
        RewardPoint rp = getPoints(user);
        if (rp.getAvailablePoints() < pointsToRedeem) {
            return false;
        }
        
        rp.setAvailablePoints(rp.getAvailablePoints() - pointsToRedeem);
        rp.setRedeemedPoints(rp.getRedeemedPoints() + pointsToRedeem);
        rewardPointRepository.save(rp);
        
        RewardTransaction rt = new RewardTransaction(user, pointsToRedeem, "DEBIT", "Redeemed reward points at checkout");
        rewardTransactionRepository.save(rt);
        return true;
    }

    public List<RewardTransaction> getTransactionHistory(User user) {
        return rewardTransactionRepository.findByUserOrderByCreatedAtDesc(user);
    }
}
