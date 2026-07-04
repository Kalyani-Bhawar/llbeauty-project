package com.llbeauty.service;

import com.llbeauty.constants.NxlConstants;
import com.llbeauty.entity.Appointment;
import com.llbeauty.entity.Commission;
import com.llbeauty.entity.User;
import com.llbeauty.repository.AppointmentRepository;
import com.llbeauty.repository.AgentProfileRepository;
import com.llbeauty.repository.CommissionRepository;
import com.llbeauty.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class AdminAppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final CommissionRepository commissionRepository;
    private final AgentProfileRepository agentProfileRepository;
    private final UserRepository userRepository;
    private final WalletService walletService;

    public AdminAppointmentService(AppointmentRepository appointmentRepository,
                                   CommissionRepository commissionRepository,
                                   AgentProfileRepository agentProfileRepository,
                                   UserRepository userRepository,
                                   WalletService walletService) {
        this.appointmentRepository = appointmentRepository;
        this.commissionRepository = commissionRepository;
        this.agentProfileRepository = agentProfileRepository;
        this.userRepository = userRepository;
        this.walletService = walletService;
    }

    @Transactional
    public void updateAppointmentStatus(Long appointmentId, String status) {
        Optional<Appointment> appointmentOpt = appointmentRepository.findById(appointmentId);
        if (appointmentOpt.isPresent()) {
            Appointment app = appointmentOpt.get();
            app.setStatus(status);

            // Commission generation logic ONLY when COMPLETED
            if ("COMPLETED".equalsIgnoreCase(status) && app.getReferralCode() != null && !app.getReferralCode().isBlank()) {
                boolean commissionExists = commissionRepository.findAll().stream()
                        .anyMatch(c -> c.getAppointment() != null && c.getAppointment().getId().equals(app.getId()));

                if (!commissionExists) {
                    agentProfileRepository.findByReferralCode(app.getReferralCode()).ifPresent(agent -> {
                        Commission commission = new Commission();
                        commission.setAgent(agent);
                        commission.setAppointment(app);

                        double amount = (app.getTotalAmount() != null ? app.getTotalAmount() : 0.0) * 0.10;
                        commission.setAmount(BigDecimal.valueOf(amount));
                        commission.setDescription("Commission for Appointment #" + app.getId());
                        commission.setStatus("PENDING");
                        commission.setCommissionType("SALON_BOOKING");
                        commissionRepository.save(commission);
                    });
                }
            }

            // NXL Reward Logic when COMPLETED
            if ("COMPLETED".equalsIgnoreCase(status)) {
                if (!Boolean.TRUE.equals(app.getNxlRewarded())) {
                    User user = userRepository.findById(app.getUserId()).orElse(null);
                    if (user != null) {
                        double serviceAmount = app.getTotalAmount() != null ? app.getTotalAmount() : 0.0;
                        double effectiveNxlUsed = app.getNxlUsed() != null ? app.getNxlUsed() : 0.0;
                        if (effectiveNxlUsed > 100.0) {
                            effectiveNxlUsed = 100.0;
                        }
                        double baseValueForReward = serviceAmount - effectiveNxlUsed;
                        double earned = Math.round(baseValueForReward * 0.05 * 100.0) / 100.0;
                        if (earned > 0) {
                            try {
                                walletService.creditNxl(
                                        user,
                                        BigDecimal.valueOf(earned),
                                        NxlConstants.SOURCE_LLBEAUTY,
                                        "CASHBACK_SALON_" + app.getId(),
                                        "5% NXL Cashback on Completed Salon Appointment #" + app.getId()
                                );
                                app.setEarnedNxl(earned);
                                app.setNxlRewarded(true);
                            } catch (Exception e) {
                                // log.error handled in controller usually, or just let service handle it silently here
                            }
                        } else {
                            app.setNxlRewarded(true);
                        }
                    }
                }
            }

            // NXL Refund Logic when CANCELLED
            if ("CANCELLED".equalsIgnoreCase(status)) {
                double nxlUsed = app.getNxlUsed() != null ? app.getNxlUsed() : 0.0;
                if (nxlUsed > 0 && !Boolean.TRUE.equals(app.getNxlRewarded())) {
                    User user = userRepository.findById(app.getUserId()).orElse(null);
                    if (user != null) {
                        try {
                            walletService.creditNxl(
                                    user,
                                    BigDecimal.valueOf(nxlUsed),
                                    NxlConstants.SOURCE_LLBEAUTY,
                                    "REFUND_SALON_" + app.getId(),
                                    "Refund NXL used for Cancelled Appointment #" + app.getId()
                            );
                        } catch (Exception e) {
                            // handle
                        }
                    }
                    app.setNxlUsed(0.0);
                }
            }

            appointmentRepository.save(app);
        } else {
            throw new IllegalArgumentException("Appointment not found");
        }
    }
}
