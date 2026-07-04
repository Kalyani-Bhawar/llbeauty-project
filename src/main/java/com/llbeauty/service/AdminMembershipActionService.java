package com.llbeauty.service;

import com.llbeauty.entity.MembershipHistory;
import com.llbeauty.entity.UserMembership;
import com.llbeauty.repository.MemberProfileRepository;
import com.llbeauty.repository.MembershipHistoryRepository;
import com.llbeauty.repository.UserMembershipRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AdminMembershipActionService {

    private final UserMembershipRepository userMembershipRepository;
    private final MemberProfileRepository memberProfileRepository;
    private final MembershipHistoryRepository membershipHistoryRepository;

    public AdminMembershipActionService(UserMembershipRepository userMembershipRepository,
                                        MemberProfileRepository memberProfileRepository,
                                        MembershipHistoryRepository membershipHistoryRepository) {
        this.userMembershipRepository = userMembershipRepository;
        this.memberProfileRepository = memberProfileRepository;
        this.membershipHistoryRepository = membershipHistoryRepository;
    }

    @Transactional
    public void deactivateMembership(Long id) {
        Optional<UserMembership> umOpt = userMembershipRepository.findById(id);
        if (umOpt.isPresent()) {
            UserMembership um = umOpt.get();
            um.setStatus("EXPIRED");
            userMembershipRepository.save(um);

            memberProfileRepository.findByUser(um.getUser()).ifPresent(p -> {
                p.setMembershipType("EXPIRED");
                memberProfileRepository.save(p);
            });

            List<MembershipHistory> histories = membershipHistoryRepository.findByUserOrderByStartDateDesc(um.getUser());
            for (MembershipHistory h : histories) {
                if (h.getPlanName().equalsIgnoreCase(um.getMembership().getName()) && "ACTIVE".equals(h.getStatus())) {
                    h.setStatus("EXPIRED");
                    membershipHistoryRepository.save(h);
                }
            }
        } else {
            throw new IllegalArgumentException("Membership not found");
        }
    }

    @Transactional
    public void activateMembership(Long id) {
        Optional<UserMembership> umOpt = userMembershipRepository.findById(id);
        if (umOpt.isPresent()) {
            UserMembership um = umOpt.get();
            um.setStatus("ACTIVE");
            um.setExpiryDate(LocalDateTime.now().plusDays(um.getMembership().getDurationDays()));
            userMembershipRepository.save(um);

            memberProfileRepository.findByUser(um.getUser()).ifPresent(p -> {
                p.setMembershipType(um.getMembership().getName());
                memberProfileRepository.save(p);
            });

            MembershipHistory history = new MembershipHistory();
            history.setUser(um.getUser());
            history.setPlanName(um.getMembership().getName());
            history.setPrice(BigDecimal.valueOf(um.getMembership().getPrice()));
            history.setStartDate(LocalDateTime.now());
            history.setExpiryDate(um.getExpiryDate());
            history.setStatus("ACTIVE");
            history.setPaymentId(um.getRazorpayPaymentId() != null ? um.getRazorpayPaymentId() : "ADMIN_MANUAL");
            membershipHistoryRepository.save(history);
        } else {
            throw new IllegalArgumentException("Membership not found");
        }
    }

    @Transactional
    public void extendMembership(Long id, int months) {
        Optional<UserMembership> umOpt = userMembershipRepository.findById(id);
        if (umOpt.isPresent()) {
            UserMembership um = umOpt.get();
            um.setExpiryDate(um.getExpiryDate().plusMonths(months));
            userMembershipRepository.save(um);
        } else {
            throw new IllegalArgumentException("Membership not found");
        }
    }
}
