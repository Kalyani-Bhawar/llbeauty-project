package com.llbeauty.service;

import com.llbeauty.entity.*;
import com.llbeauty.repository.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AgentService {

    private final AgentProfileRepository agentProfileRepository;
    private final CommissionRepository commissionRepository;
    private final FranchiseLeadRepository franchiseLeadRepository;
    private final PayoutRepository payoutRepository;
    private final StoreApplicationRepository storeApplicationRepository;
    private final AppointmentRepository appointmentRepository;
    private final OrderRepository orderRepository;

    public AgentService(AgentProfileRepository agentProfileRepository,
                        CommissionRepository commissionRepository,
                        FranchiseLeadRepository franchiseLeadRepository,
                        PayoutRepository payoutRepository,
                        StoreApplicationRepository storeApplicationRepository,
                        AppointmentRepository appointmentRepository,
                        OrderRepository orderRepository) {
        this.agentProfileRepository = agentProfileRepository;
        this.commissionRepository = commissionRepository;
        this.franchiseLeadRepository = franchiseLeadRepository;
        this.payoutRepository = payoutRepository;
        this.storeApplicationRepository = storeApplicationRepository;
        this.appointmentRepository = appointmentRepository;
        this.orderRepository = orderRepository;
    }

    public AgentProfile getAgentProfileByEmail(String email) {
        return agentProfileRepository.findAll()
                .stream()
                .filter(a -> a.getUser().getEmail().equals(email))
                .findFirst()
                .orElse(null);
    }

    public Map<String, Object> getDashboardData(AgentProfile profile) {
        List<Commission> commissions = commissionRepository.findByAgentOrderByCreatedAtDesc(profile);

        BigDecimal pendingCommission = commissions.stream()
                .filter(c -> "PENDING".equalsIgnoreCase(c.getStatus()) || "APPROVED".equalsIgnoreCase(c.getStatus()))
                .map(Commission::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal lifetimeEarnings = commissions.stream()
                .map(Commission::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<Payout> payoutHistory = payoutRepository.findByAgentOrderByCreatedAtDesc(profile);
        BigDecimal lastPayoutAmount = BigDecimal.ZERO;
        LocalDateTime lastPayoutDate = null;
        String lastPayoutRemarks = "";
        String lastPayoutUtr = "";
        if (!payoutHistory.isEmpty()) {
            Payout lastPayout = payoutHistory.get(0);
            lastPayoutAmount = lastPayout.getAmount();
            lastPayoutDate = lastPayout.getCreatedAt();
            lastPayoutRemarks = lastPayout.getRemarks();
            lastPayoutUtr = lastPayout.getUtrNumber();
        }

        List<Map<String, Object>> activityList = new ArrayList<>();

        for (Commission c : commissions) {
            Map<String, Object> activity = new HashMap<>();
            activity.put("date", c.getCreatedAt());
            activity.put("commissionAmount", c.getAmount());
            activity.put("status", "PAID".equalsIgnoreCase(c.getStatus()) ? "PAID" : "PENDING");

            String type = "User Registration";
            String userName = "-";
            String reference = "-";
            BigDecimal txAmount = BigDecimal.ZERO;
            String pct = "-";

            String commType = c.getCommissionType();
            String desc = c.getDescription() != null ? c.getDescription() : "";

            if ("MEMBERSHIP".equalsIgnoreCase(commType) || desc.contains("Membership Referral")) {
                type = "Membership Purchase";
                pct = "5%";
                if (desc.contains(" - User: ")) {
                    int userIdx = desc.indexOf(" - User: ");
                    int startIdx = desc.indexOf("Membership Referral - ");
                    if (startIdx >= 0) {
                        reference = desc.substring(startIdx + "Membership Referral - ".length(), userIdx);
                    } else {
                        reference = "Membership Plan";
                    }
                    userName = desc.substring(userIdx + " - User: ".length());
                } else {
                    reference = "Membership";
                    userName = "Referred User";
                }
                txAmount = c.getAmount().multiply(new BigDecimal("20"));
            } else if ("PRODUCT".equalsIgnoreCase(commType) || desc.startsWith("Product Order #")) {
                type = "Product Purchase";
                pct = "5%";
                String orderIdStr = desc.replace("Product Order #", "").trim();
                try {
                    Long orderId = Long.parseLong(orderIdStr);
                    Optional<Order> orderOpt = orderRepository.findById(orderId);
                    if (orderOpt.isPresent()) {
                        Order order = orderOpt.get();
                        userName = order.getUser() != null ? order.getUser().getName() : "Customer";
                        reference = "Order #" + orderId;
                        txAmount = BigDecimal.valueOf(order.getTotalAmount() != null ? order.getTotalAmount() : 0.0);
                    } else {
                        reference = "Order #" + orderIdStr;
                        userName = "Customer";
                        txAmount = c.getAmount().multiply(new BigDecimal("20"));
                    }
                } catch (Exception e) {
                    reference = desc;
                    userName = "Customer";
                    txAmount = c.getAmount().multiply(new BigDecimal("20"));
                }
            } else if ("SALON_BOOKING".equalsIgnoreCase(commType) || desc.contains("Appointment #")) {
                type = "Salon Booking";
                pct = "10%";
                String appIdStr = desc.replaceAll("[^0-9]", "").trim();
                try {
                    Long appId = Long.parseLong(appIdStr);
                    Optional<Appointment> appOpt = appointmentRepository.findById(appId);
                    if (appOpt.isPresent()) {
                        Appointment app = appOpt.get();
                        userName = app.getUserName();
                        reference = app.getServiceName() != null ? app.getServiceName() : app.getServices();
                        if (reference == null || reference.isEmpty()) {
                            reference = "Salon Appointment";
                        }
                        txAmount = BigDecimal.valueOf(app.getTotalAmount() != null ? app.getTotalAmount() : 0.0);
                    } else {
                        reference = "Appointment #" + appIdStr;
                        userName = "Customer";
                        txAmount = c.getAmount().multiply(BigDecimal.TEN);
                    }
                } catch (Exception e) {
                    reference = desc;
                    userName = "Customer";
                    txAmount = c.getAmount().multiply(BigDecimal.TEN);
                }
            } else if ("FRANCHISE".equalsIgnoreCase(commType) || desc.contains("Franchise Referral")) {
                type = "Franchise Lead";
                pct = "10%";
                String leadIdStr = desc.replaceAll("[^0-9]", "").trim();
                try {
                    Long leadId = Long.parseLong(leadIdStr);
                    Optional<FranchiseLead> leadOpt = franchiseLeadRepository.findById(leadId);
                    if (leadOpt.isPresent()) {
                        FranchiseLead lead = leadOpt.get();
                        userName = lead.getName();
                        reference = lead.getFranchiseType() + " (" + lead.getCity() + ")";
                        txAmount = lead.getFinalFranchiseAmount() != null ? lead.getFinalFranchiseAmount() : BigDecimal.ZERO;
                    } else {
                        reference = "Lead #" + leadIdStr;
                        userName = "Franchise Partner";
                        txAmount = c.getAmount().multiply(BigDecimal.TEN);
                    }
                } catch (Exception e) {
                    reference = desc;
                    userName = "Franchise Partner";
                    txAmount = c.getAmount().multiply(BigDecimal.TEN);
                }
            } else if ("AGENT".equalsIgnoreCase(commType) || desc.contains("Agent Referral")) {
                type = "Agent Registration";
                pct = "Fixed";
                reference = "Agent Registration";
                if (desc.contains(" - ")) {
                    userName = desc.substring(desc.indexOf(" - ") + 3);
                } else {
                    userName = "Agent Partner";
                }
                txAmount = c.getAmount();
            } else if ("MERCHANT".equalsIgnoreCase(commType) || desc.contains("Merchant Referral")) {
                type = "Merchant Registration";
                pct = "Fixed";
                reference = "Merchant Registration";
                if (desc.contains(" - ")) {
                    userName = desc.substring(desc.indexOf(" - ") + 3);
                } else {
                    userName = "Merchant Partner";
                }
                txAmount = c.getAmount();
            } else if ("USER_REGISTRATION".equalsIgnoreCase(commType) || desc.contains("User Registration")) {
                type = "User Registration";
                pct = "Fixed";
                reference = "New User Signup";
                userName = "Referred User";
                txAmount = BigDecimal.ZERO;
            }

            activity.put("type", type);
            activity.put("userName", userName);
            activity.put("reference", reference);
            activity.put("transactionAmount", txAmount);
            activity.put("commissionPercent", pct);

            activityList.add(activity);
        }

        if (profile.getReferralCode() != null && !profile.getReferralCode().isBlank()) {
            List<Appointment> pendingApps = appointmentRepository.findByReferralCode(profile.getReferralCode()).stream()
                    .filter(a -> !"COMPLETED".equalsIgnoreCase(a.getStatus()) && !"CANCELLED".equalsIgnoreCase(a.getStatus()))
                    .collect(Collectors.toList());
            for (Appointment app : pendingApps) {
                Map<String, Object> activity = new HashMap<>();
                activity.put("date", app.getCreatedAt() != null ? app.getCreatedAt() : LocalDateTime.now());
                activity.put("type", "Salon Booking");
                activity.put("userName", app.getUserName());
                String ref = app.getServiceName() != null ? app.getServiceName() : app.getServices();
                activity.put("reference", ref != null && !ref.isEmpty() ? ref : "Booking Pending");
                double amt = app.getTotalAmount() != null ? app.getTotalAmount() : 0.0;
                activity.put("transactionAmount", BigDecimal.valueOf(amt));
                activity.put("commissionPercent", "10%");
                activity.put("commissionAmount", BigDecimal.valueOf(amt * 0.10));
                activity.put("status", "PENDING");
                activityList.add(activity);
            }

            List<FranchiseLead> pendingLeads = franchiseLeadRepository.findByReferralCodeOrderByCreatedAtDesc(profile.getReferralCode()).stream()
                    .filter(f -> !Boolean.TRUE.equals(f.getCommissionGenerated()) && !"REJECTED".equalsIgnoreCase(f.getStatus()))
                    .collect(Collectors.toList());
            for (FranchiseLead lead : pendingLeads) {
                Map<String, Object> activity = new HashMap<>();
                activity.put("date", lead.getCreatedAt() != null ? lead.getCreatedAt() : LocalDateTime.now());
                activity.put("type", "Franchise Lead");
                activity.put("userName", lead.getName());
                activity.put("reference", lead.getFranchiseType() + " (" + lead.getCity() + ")");
                BigDecimal amt = lead.getFinalFranchiseAmount() != null ? lead.getFinalFranchiseAmount() : BigDecimal.ZERO;
                activity.put("transactionAmount", amt);
                activity.put("commissionPercent", "10%");
                activity.put("commissionAmount", amt.multiply(new BigDecimal("0.10")));
                activity.put("status", "PENDING");
                activityList.add(activity);
            }

            List<StoreApplication> pendingStoreApps = storeApplicationRepository.findByReferralCode(profile.getReferralCode()).stream()
                    .filter(sa -> sa.getStatus() != ApplicationStatus.APPROVED && sa.getStatus() != ApplicationStatus.REJECTED)
                    .collect(Collectors.toList());
            for (StoreApplication sa : pendingStoreApps) {
                Map<String, Object> activity = new HashMap<>();
                activity.put("date", sa.getCreatedAt() != null ? sa.getCreatedAt() : LocalDateTime.now());
                String t = sa.getType() == ApplicationType.AGENT ? "Agent Registration" : "Merchant Registration";
                activity.put("type", t);
                activity.put("userName", sa.getUser() != null ? sa.getUser().getName() : "Partner");
                activity.put("reference", t);
                double amt = sa.getPaymentAmount() != null ? sa.getPaymentAmount() : 0.0;
                activity.put("transactionAmount", BigDecimal.valueOf(amt));
                activity.put("commissionPercent", "Fixed");
                BigDecimal commAmt = sa.getType() == ApplicationType.AGENT ?
                        ("STARTER_KIT".equalsIgnoreCase(sa.getRegistrationType()) ? new BigDecimal("1000.00") : new BigDecimal("100.00")) :
                        new BigDecimal("1000.00");
                activity.put("commissionAmount", commAmt);
                activity.put("status", "PENDING");
                activityList.add(activity);
            }
        }

        activityList.sort((a1, a2) -> {
            LocalDateTime d1 = (LocalDateTime) a1.get("date");
            LocalDateTime d2 = (LocalDateTime) a2.get("date");
            if (d1 == null && d2 == null) return 0;
            if (d1 == null) return 1;
            if (d2 == null) return -1;
            return d2.compareTo(d1);
        });

        int totalReferralsCount = activityList.size();

        Map<String, Object> data = new HashMap<>();
        data.put("pendingCommission", pendingCommission);
        data.put("lifetimeEarnings", lifetimeEarnings);
        data.put("totalReferralsCount", totalReferralsCount);
        data.put("lastPayoutAmount", lastPayoutAmount);
        data.put("lastPayoutDate", lastPayoutDate);
        data.put("lastPayoutRemarks", lastPayoutRemarks);
        data.put("lastPayoutUtr", lastPayoutUtr);
        data.put("activityList", activityList);
        data.put("payoutHistory", payoutHistory);

        return data;
    }
}
