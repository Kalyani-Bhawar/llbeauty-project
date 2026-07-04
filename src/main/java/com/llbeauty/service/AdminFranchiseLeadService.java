package com.llbeauty.service;

import com.llbeauty.entity.Commission;
import com.llbeauty.entity.FranchiseLead;
import com.llbeauty.repository.AgentProfileRepository;
import com.llbeauty.repository.CommissionRepository;
import com.llbeauty.repository.FranchiseLeadRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class AdminFranchiseLeadService {

    private final FranchiseLeadRepository franchiseLeadRepository;
    private final AgentProfileRepository agentProfileRepository;
    private final CommissionRepository commissionRepository;

    public AdminFranchiseLeadService(FranchiseLeadRepository franchiseLeadRepository,
                                     AgentProfileRepository agentProfileRepository,
                                     CommissionRepository commissionRepository) {
        this.franchiseLeadRepository = franchiseLeadRepository;
        this.agentProfileRepository = agentProfileRepository;
        this.commissionRepository = commissionRepository;
    }

    @Transactional
    public void updateFranchiseLeadStatus(Long leadId, String status, String remarks, BigDecimal finalFranchiseAmount) {
        Optional<FranchiseLead> leadOpt = franchiseLeadRepository.findById(leadId);
        if (leadOpt.isPresent()) {
            FranchiseLead lead = leadOpt.get();
            if ("APPROVED".equalsIgnoreCase(status) && (finalFranchiseAmount == null || finalFranchiseAmount.compareTo(BigDecimal.ZERO) <= 0)) {
                throw new IllegalArgumentException("Final Franchise Amount is required for approval.");
            }
            
            lead.setStatus(status);
            if (remarks != null) {
                lead.setRemarks(remarks);
            }

            if ("APPROVED".equalsIgnoreCase(status)) {
                if (finalFranchiseAmount != null) {
                    lead.setFinalFranchiseAmount(finalFranchiseAmount);
                }

                if (!Boolean.TRUE.equals(lead.getCommissionGenerated())) {
                    String refCode = lead.getReferralCode();
                    if (refCode != null && !refCode.trim().isEmpty()) {
                        agentProfileRepository.findByReferralCode(refCode.trim()).ifPresent(agent -> {
                            BigDecimal finalAmount = lead.getFinalFranchiseAmount() != null ? lead.getFinalFranchiseAmount() : BigDecimal.ZERO;
                            if (finalAmount.compareTo(BigDecimal.ZERO) > 0) {
                                BigDecimal commissionAmount = finalAmount.multiply(new BigDecimal("0.10"));

                                Commission commission = new Commission();
                                commission.setAgent(agent);
                                commission.setAmount(commissionAmount);
                                commission.setDescription("Franchise Referral Commission - Lead #" + lead.getId());
                                commission.setStatus("PENDING");
                                commission.setCommissionType("FRANCHISE");
                                commissionRepository.save(commission);

                                lead.setCommissionGenerated(true);
                            }
                        });
                    }
                }
            }

            franchiseLeadRepository.save(lead);
        } else {
            throw new IllegalArgumentException("Franchise lead not found.");
        }
    }
}
