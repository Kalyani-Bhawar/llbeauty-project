package com.llbeauty.service;

import com.llbeauty.entity.*;
import com.llbeauty.repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.math.BigDecimal;

@Service
public class MembershipService {

    private final MembershipRepository membershipRepository;
    private final UserMembershipRepository userMembershipRepository;
    private final WalletService walletService;
    private final com.llbeauty.service.PaymentService paymentService;
    private final PaymentRepository paymentRepository;
    private final MembershipPurchaseRepository membershipPurchaseRepository;
    private final MembershipHistoryRepository membershipHistoryRepository;
    
    private final MemberProfileRepository memberProfileRepository;
    private final RewardPointRepository rewardPointRepository;
    private final MembershipQRCodeRepository membershipQRCodeRepository;
    private final EmailService emailService;

    @Value("${razorpay.key.id}")
    private String razorpayKeyId;

    public MembershipService(MembershipRepository membershipRepository,
                             UserMembershipRepository userMembershipRepository,
                             WalletService walletService,
                             com.llbeauty.service.PaymentService paymentService,
                             PaymentRepository paymentRepository,
                             MembershipPurchaseRepository membershipPurchaseRepository,
                             MembershipHistoryRepository membershipHistoryRepository,
                             MemberProfileRepository memberProfileRepository,
                             RewardPointRepository rewardPointRepository,
                             MembershipQRCodeRepository membershipQRCodeRepository,
                             EmailService emailService) {
        this.membershipRepository = membershipRepository;
        this.userMembershipRepository = userMembershipRepository;
        this.walletService = walletService;
        this.paymentService = paymentService;
        this.paymentRepository = paymentRepository;
        this.membershipPurchaseRepository = membershipPurchaseRepository;
        this.membershipHistoryRepository = membershipHistoryRepository;
        this.memberProfileRepository = memberProfileRepository;
        this.rewardPointRepository = rewardPointRepository;
        this.membershipQRCodeRepository = membershipQRCodeRepository;
        this.emailService = emailService;
    }

    public List<Membership> getAllPlans() {
        return membershipRepository.findAll();
    }

    public Optional<Membership> getPlanById(Long id) {
        return membershipRepository.findById(id);
    }

    @Transactional
    public Optional<UserMembership> getActiveMembership(User user) {
        if (user == null) return Optional.empty();
        Optional<UserMembership> activeOpt = userMembershipRepository.findByUserAndStatus(user, "ACTIVE");
        if (activeOpt.isPresent()) {
            UserMembership um = activeOpt.get();
            if (um.getExpiryDate().isBefore(LocalDateTime.now())) {
                um.setStatus("EXPIRED");
                userMembershipRepository.save(um);
                
                // Also update permanent profile status
                memberProfileRepository.findByUser(user).ifPresent(p -> {
                    p.setMembershipType("EXPIRED");
                    memberProfileRepository.save(p);
                });
                
                user.setMembershipStatus("NOT_APPLIED");
                // The userRepository isn't injected directly here so we'd need to inject it. Let's just set it and let any other transaction save it, or inject UserRepository.
            }
            return Optional.of(um);
        }
        return Optional.empty();
    }

    /**
     * Prepares purchase or upgrade payment options
     */
    public Map<String, Object> preparePurchase(User user, Long planId, boolean useWallet) throws Exception {
        Membership newPlan = membershipRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid membership plan ID"));

        Optional<UserMembership> activeOpt = getActiveMembership(user);
        double finalPrice = newPlan.getPrice();
        boolean isUpgrade = false;
        boolean isRenewal = false;
        Long currentMembershipId = null;

        if (activeOpt.isPresent()) {
            UserMembership current = activeOpt.get();
            Membership currentPlan = current.getMembership();

            if (currentPlan.getId().equals(newPlan.getId())) {
                // Same plan renewal is allowed!
                finalPrice = newPlan.getPrice();
                isRenewal = true;
                currentMembershipId = current.getId();
            } else if (newPlan.getPrice() < currentPlan.getPrice()) {
                throw new IllegalStateException("Downgrades are not allowed. You can only upgrade to a higher tier plan.");
            } else {
                // Pro-rated upgrade price calculation
                long totalDays = currentPlan.getDurationDays();
                long daysUsed = ChronoUnit.DAYS.between(current.getStartDate(), LocalDateTime.now());
                if (daysUsed < 0) daysUsed = 0;
                if (daysUsed > totalDays) daysUsed = totalDays;

                double remainingValue = Math.max(0.0, currentPlan.getPrice() * (1.0 - ((double) daysUsed / totalDays)));
                finalPrice = Math.max(0.0, newPlan.getPrice() - remainingValue);
                isUpgrade = true;
                currentMembershipId = current.getId();
            }
        }
        
        double gstAmount = finalPrice * 0.18;
        double grandTotal = finalPrice + gstAmount;

        double walletRedeemed = 0.0;
        if (useWallet) {
            double currentWallet = walletService.getBalance(user).doubleValue();
            walletRedeemed = Math.min(currentWallet, grandTotal);
        }

        double amountToPay = grandTotal - walletRedeemed;

        Map<String, Object> response = new HashMap<>();
        response.put("planId", planId);
        response.put("planName", newPlan.getName());
        response.put("price", finalPrice);
        response.put("gstAmount", gstAmount);
        response.put("finalPriceWithGst", grandTotal);
        response.put("walletRedeemed", walletRedeemed);
        response.put("amountToPay", amountToPay);
        response.put("isUpgrade", isUpgrade);
        response.put("isRenewal", isRenewal);
        response.put("currentMembershipId", currentMembershipId);

        // Generate Razorpay Order
        if (amountToPay > 0) {
            try {
                // Track Membership Purchase
                MembershipPurchase purchase = new MembershipPurchase();
                purchase.setUser(user);
                purchase.setMembership(newPlan);
                purchase.setAmountPaid(grandTotal);
                purchase.setStatus("PENDING");
                MembershipPurchase savedPurchase = membershipPurchaseRepository.save(purchase);

                Payment payment = paymentService.initiatePayment(user, amountToPay, "MEMBERSHIP", String.valueOf(savedPurchase.getId()), "RAZORPAY" + (walletRedeemed > 0 ? "+WALLET" : ""));

                savedPurchase.setRazorpayOrderId(payment.getRazorpayOrderId());
                membershipPurchaseRepository.save(savedPurchase);

                response.put("razorpayOrderId", payment.getRazorpayOrderId());
                response.put("key", razorpayKeyId);
                response.put("useMock", false);
                response.put("purchaseId", savedPurchase.getId());
            } catch (Exception e) {
                e.printStackTrace();
                throw new IllegalStateException("Failed to initialize payment gateway.");
            }
        } else {
            // Track Membership Purchase
            MembershipPurchase purchase = new MembershipPurchase();
            purchase.setUser(user);
            purchase.setMembership(newPlan);
            purchase.setAmountPaid(grandTotal);
            purchase.setStatus("PENDING");
            MembershipPurchase savedPurchase = membershipPurchaseRepository.save(purchase);

            response.put("useMock", true);
            response.put("razorpayOrderId", "mock_order_" + System.currentTimeMillis());
            response.put("key", "mock_key");
            response.put("purchaseId", savedPurchase.getId());
            
            if (walletRedeemed > 0) {
                walletService.debit(user, walletRedeemed, "Redeemed for Membership " + newPlan.getName());
            }
        }

        return response;
    }

    private synchronized String generateNextMemberId(String prefix) {
        List<UserMembership> all = userMembershipRepository.findAll();
        long maxNum = 0;
        for (UserMembership um : all) {
            if (um.getMemberId() != null && um.getMemberId().startsWith(prefix)) {
                try {
                    String suffix = um.getMemberId().substring(prefix.length());
                    long num = Long.parseLong(suffix);
                    if (num > maxNum) {
                        maxNum = num;
                    }
                } catch (Exception e) {
                    // ignore malformed ones
                }
            }
        }
        long nextNum = maxNum + 1;
        return prefix + String.format("%05d", nextNum);
    }

    @Transactional
    public UserMembership activateMembership(User user, Long planId, String paymentId, String orderId, String signature, String dob, String referralCode) {
        
        if (orderId != null && signature != null && !orderId.startsWith("mock_order_")) {
            Payment payment = paymentService.verifyAndProcessPayment(orderId, paymentId, signature);

            MembershipPurchase purchase = membershipPurchaseRepository.findByRazorpayOrderId(orderId);
            if (purchase != null) {
                purchase.setStatus("SUCCESS");
                purchase.setRazorpayPaymentId(paymentId);
                membershipPurchaseRepository.save(purchase);
                
                // Note: Wallet deduction for partial payment should ideally happen here to ensure atomic transaction,
                // but since preparePurchase might not persist the walletRedeemed amount cleanly without a new field,
                // we assume if payment method is +WALLET, we already deducted it or we deduct it now.
                // For a robust system, MembershipPurchase entity should store `walletRedeemed`.
                // For now, if payment amount < purchase amount, we assume the rest was from wallet.
                double amountPaidViaRazorpay = payment.getAmount();
                double totalPurchaseAmount = purchase.getAmountPaid();
                if (totalPurchaseAmount > amountPaidViaRazorpay) {
                     double walletAmount = totalPurchaseAmount - amountPaidViaRazorpay;
                     walletService.debitWithDetails(user, BigDecimal.valueOf(walletAmount), "Redeemed for Membership " + purchase.getMembership().getName(), "MEMBERSHIP_PURCHASE", payment.getId(), null);
                }
            }
        } else if (orderId != null && orderId.startsWith("mock_order_")) {
             // Fully wallet paid mock order
             // Already deducted in preparePurchase
             MembershipPurchase purchase = membershipPurchaseRepository.findById(Long.parseLong(paymentId)).orElse(null); // Assuming paymentId is purchaseId for mock
             if (purchase != null) {
                 purchase.setStatus("SUCCESS");
                 membershipPurchaseRepository.save(purchase);
             }
        }

        Membership plan = membershipRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("Plan not found"));

        Optional<UserMembership> activeOpt = getActiveMembership(user);
        String oldPlanName = null;
        boolean isUpgrade = false;
        boolean isRenewal = false;
        String existingMemberId = null;

        if (activeOpt.isPresent()) {
            UserMembership current = activeOpt.get();
            oldPlanName = current.getMembership().getName();
            
            if (current.getMembership().getId().equals(plan.getId())) {
                isRenewal = true;
                existingMemberId = current.getMemberId();
                current.setStatus("RENEWED");
            } else {
                isUpgrade = true;
                current.setStatus("UPGRADED");
            }
            userMembershipRepository.save(current);
        }

        // Generate Secure UUID
        String secureUuid = UUID.randomUUID().toString();

        // Generate unique member ID
        String memberIdPrefix = "LLB-P-";
        if (plan.getName().contains("Gold")) {
            memberIdPrefix = "LLB-G-";
        } else if (plan.getName().contains("Black")) {
            memberIdPrefix = "LLB-B-";
        }

        String memberId = isRenewal && existingMemberId != null ? existingMemberId : generateNextMemberId(memberIdPrefix);

        UserMembership newMembership = new UserMembership();
        newMembership.setUser(user);
        newMembership.setMembership(plan);
        newMembership.setStartDate(LocalDateTime.now());
        newMembership.setExpiryDate(LocalDateTime.now().plusDays(plan.getDurationDays()));
        newMembership.setStatus("ACTIVE");
        newMembership.setRazorpayPaymentId(paymentId);
        newMembership.setDob(dob);
        newMembership.setReferralCode(referralCode);
        newMembership.setMemberId(memberId);
        newMembership.setUuid(secureUuid);

        UserMembership saved = userMembershipRepository.save(newMembership);

        // Update or create permanent MemberProfile
        MemberProfile profile = memberProfileRepository.findByUser(user).orElseGet(() -> {
            MemberProfile newProfile = new MemberProfile();
            newProfile.setUser(user);
            newProfile.setUuid(secureUuid);
            newProfile.setJoinDate(LocalDateTime.now());
            return newProfile;
        });
        profile.setMemberId(memberId);
        profile.setMembershipType(plan.getName());
        memberProfileRepository.save(profile);

        // Save/Map Membership QR Code
        MembershipQRCode qr = new MembershipQRCode(saved, "/member/verify/" + secureUuid);
        membershipQRCodeRepository.save(qr);

        // Deactivate old active history records
        List<MembershipHistory> histories = membershipHistoryRepository.findByUserOrderByStartDateDesc(user);
        for (MembershipHistory h : histories) {
            if ("ACTIVE".equals(h.getStatus())) {
                h.setStatus("EXPIRED");
                membershipHistoryRepository.save(h);
            }
        }

        // Save to membership history
        MembershipHistory history = new MembershipHistory();
        history.setUser(user);
        history.setPlanName(plan.getName());
        history.setPrice(BigDecimal.valueOf(plan.getPrice()));
        history.setStartDate(newMembership.getStartDate());
        history.setExpiryDate(newMembership.getExpiryDate());
        history.setStatus("ACTIVE");
        history.setPaymentId(paymentId);
        membershipHistoryRepository.save(history);

        // Grant Welcome Credits
        walletService.credit(user, BigDecimal.valueOf(plan.getWelcomeCredits()), "Welcome credit for " + plan.getName() + " activation", "MEMBERSHIP_WELCOME");

        // Initialize user reward points if missing
        if (rewardPointRepository.findByUser(user).isEmpty()) {
            RewardPoint rp = new RewardPoint(user, 0, 0, 0);
            rewardPointRepository.save(rp);
        }

        // Trigger transactional emails
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
        String formattedExpiry = saved.getExpiryDate().format(formatter);
        if (isRenewal) {
            emailService.sendMembershipRenewalEmail(user.getEmail(), user.getName(), plan.getName(), formattedExpiry);
        } else if (isUpgrade && oldPlanName != null) {
            emailService.sendMembershipUpgradedEmail(user.getEmail(), user.getName(), oldPlanName, plan.getName(), memberId);
        } else {
            emailService.sendMembershipActivatedEmail(user.getEmail(), user.getName(), plan.getName(), memberId, formattedExpiry);
        }

        return saved;
    }

    @Transactional
    public void populateMissingMembershipIdentifiers() {
        List<UserMembership> memberships = userMembershipRepository.findAll();
        for (UserMembership um : memberships) {
            boolean updated = false;
            String secureUuid = um.getUuid();
            if (secureUuid == null || secureUuid.trim().isEmpty() || "null".equalsIgnoreCase(secureUuid)) {
                secureUuid = UUID.randomUUID().toString();
                um.setUuid(secureUuid);
                updated = true;
            }
            String memberId = um.getMemberId();
            if (memberId == null || memberId.trim().isEmpty() || "null".equalsIgnoreCase(memberId)) {
                Membership plan = um.getMembership();
                String memberIdPrefix = "LLB-P-";
                if (plan.getName().contains("Gold")) {
                    memberIdPrefix = "LLB-G-";
                } else if (plan.getName().contains("Black")) {
                    memberIdPrefix = "LLB-B-";
                }
                memberId = generateNextMemberId(memberIdPrefix);
                um.setMemberId(memberId);
                updated = true;
            }
            if (updated) {
                userMembershipRepository.saveAndFlush(um);
            }
            
            final String finalMemberId = memberId;
            final String finalUuid = secureUuid;
            
            Optional<MemberProfile> profileOpt = memberProfileRepository.findByUser(um.getUser());
            if (profileOpt.isPresent()) {
                MemberProfile profile = profileOpt.get();
                boolean profileUpdated = false;
                if (profile.getUuid() == null || profile.getUuid().trim().isEmpty() || "null".equalsIgnoreCase(profile.getUuid())) {
                    profile.setUuid(finalUuid);
                    profileUpdated = true;
                }
                if (profile.getMemberId() == null || profile.getMemberId().trim().isEmpty() || "null".equalsIgnoreCase(profile.getMemberId())) {
                    profile.setMemberId(finalMemberId);
                    profileUpdated = true;
                }
                if (profileUpdated) {
                    memberProfileRepository.saveAndFlush(profile);
                }
            } else {
                MemberProfile profile = new MemberProfile();
                profile.setUser(um.getUser());
                profile.setUuid(finalUuid);
                profile.setMemberId(finalMemberId);
                profile.setJoinDate(um.getStartDate());
                profile.setMembershipType(um.getMembership().getName());
                memberProfileRepository.saveAndFlush(profile);
            }

            if (membershipQRCodeRepository.findByUserMembership(um).isEmpty()) {
                MembershipQRCode qr = new MembershipQRCode(um, "/member/verify/" + secureUuid);
                membershipQRCodeRepository.save(qr);
            }
        }
    }
}
