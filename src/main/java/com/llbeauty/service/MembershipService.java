package com.llbeauty.service;

import com.llbeauty.entity.Membership;
import com.llbeauty.entity.User;
import com.llbeauty.entity.UserMembership;
import com.llbeauty.repository.MembershipRepository;
import com.llbeauty.repository.UserMembershipRepository;
import com.razorpay.RazorpayClient;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import com.llbeauty.repository.PaymentRepository;
import com.llbeauty.repository.MembershipPurchaseRepository;
import com.llbeauty.entity.Payment;
import com.llbeauty.entity.MembershipPurchase;
import com.llbeauty.entity.MembershipHistory;
import com.llbeauty.repository.MembershipHistoryRepository;
import java.math.BigDecimal;

@Service
public class MembershipService {

    private final MembershipRepository membershipRepository;
    private final UserMembershipRepository userMembershipRepository;
    private final WalletService walletService;
    private final RazorpayService razorpayService;
    private final PaymentRepository paymentRepository;
    private final MembershipPurchaseRepository membershipPurchaseRepository;
    private final MembershipHistoryRepository membershipHistoryRepository;

    @Value("${razorpay.key.id}")
    private String razorpayKeyId;

    public MembershipService(MembershipRepository membershipRepository,
                             UserMembershipRepository userMembershipRepository,
                             WalletService walletService,
                             RazorpayService razorpayService,
                             PaymentRepository paymentRepository,
                             MembershipPurchaseRepository membershipPurchaseRepository,
                             MembershipHistoryRepository membershipHistoryRepository) {
        this.membershipRepository = membershipRepository;
        this.userMembershipRepository = userMembershipRepository;
        this.walletService = walletService;
        this.razorpayService = razorpayService;
        this.paymentRepository = paymentRepository;
        this.membershipPurchaseRepository = membershipPurchaseRepository;
        this.membershipHistoryRepository = membershipHistoryRepository;
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
                return Optional.empty();
            }
            return Optional.of(um);
        }
        return Optional.empty();
    }

    /**
     * Prepares purchase or upgrade payment options
     */
    @Transactional
    public Map<String, Object> preparePurchase(User user, Long planId) throws Exception {
        Membership newPlan = membershipRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid membership plan ID"));

        Optional<UserMembership> activeOpt = getActiveMembership(user);
        double finalPrice = newPlan.getPrice();
        boolean isUpgrade = false;
        Long currentMembershipId = null;

        if (activeOpt.isPresent()) {
            UserMembership current = activeOpt.get();
            Membership currentPlan = current.getMembership();

            if (currentPlan.getId().equals(newPlan.getId())) {
                throw new IllegalStateException("You already have an active " + newPlan.getName() + " membership");
            }

            if (newPlan.getPrice() < currentPlan.getPrice()) {
                throw new IllegalStateException("Downgrades are not allowed. You can only upgrade to a higher tier plan.");
            }

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
        
        double gstAmount = finalPrice * 0.18;
        double finalPriceWithGst = finalPrice + gstAmount;

        Map<String, Object> response = new HashMap<>();
        response.put("planId", planId);
        response.put("planName", newPlan.getName());
        response.put("price", finalPrice);
        response.put("gstAmount", gstAmount);
        response.put("finalPriceWithGst", finalPriceWithGst);
        response.put("isUpgrade", isUpgrade);
        response.put("currentMembershipId", currentMembershipId);

        // Generate Razorpay Order
        if (finalPriceWithGst > 0) {
            try {
                com.razorpay.Order order = razorpayService.createOrder(finalPriceWithGst, "member_" + user.getId() + "_" + System.currentTimeMillis());
                
                // Track Payment
                Payment payment = new Payment();
                payment.setUser(user);
                payment.setRazorpayOrderId(order.get("id"));
                payment.setAmount(finalPriceWithGst);
                payment.setStatus("PENDING");
                payment.setPaymentMethod("RAZORPAY");
                payment.setPurpose("MEMBERSHIP");
                paymentRepository.save(payment);

                // Track Membership Purchase
                MembershipPurchase purchase = new MembershipPurchase();
                purchase.setUser(user);
                purchase.setMembership(newPlan);
                purchase.setAmountPaid(finalPriceWithGst);
                purchase.setStatus("PENDING");
                purchase.setRazorpayOrderId(order.get("id"));
                membershipPurchaseRepository.save(purchase);

                response.put("razorpayOrderId", order.get("id"));
                response.put("key", razorpayKeyId);
                response.put("useMock", false);
            } catch (Exception e) {
                e.printStackTrace();
                throw new IllegalStateException("Failed to initialize payment gateway.");
            }
        } else {
            response.put("useMock", true);
            response.put("razorpayOrderId", "mock_order_" + System.currentTimeMillis());
            response.put("key", "mock_key");
        }

        return response;
    }

    @Transactional
    public UserMembership activateMembership(User user, Long planId, String paymentId, String orderId, String signature, String dob, String referralCode) {
        
        if (orderId != null && signature != null) {
            boolean isValid = razorpayService.verifySignature(orderId, paymentId, signature);
            if (!isValid) {
                throw new IllegalStateException("Invalid payment signature.");
            }

            Payment payment = paymentRepository.findByRazorpayOrderId(orderId);
            if (payment != null) {
                payment.setStatus("SUCCESS");
                payment.setRazorpayPaymentId(paymentId);
                payment.setRazorpaySignature(signature);
                paymentRepository.save(payment);
            }

            MembershipPurchase purchase = membershipPurchaseRepository.findByRazorpayOrderId(orderId);
            if (purchase != null) {
                purchase.setStatus("SUCCESS");
                purchase.setRazorpayPaymentId(paymentId);
                membershipPurchaseRepository.save(purchase);
            }
        }

        Membership plan = membershipRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("Plan not found"));

        Optional<UserMembership> activeOpt = getActiveMembership(user);

        if (activeOpt.isPresent()) {
            UserMembership current = activeOpt.get();
            // Deactivate old active membership
            current.setStatus("UPGRADED");
            userMembershipRepository.save(current);
        }

        UserMembership newMembership = new UserMembership();
        newMembership.setUser(user);
        newMembership.setMembership(plan);
        newMembership.setStartDate(LocalDateTime.now());
        newMembership.setExpiryDate(LocalDateTime.now().plusDays(plan.getDurationDays()));
        newMembership.setStatus("ACTIVE");
        newMembership.setRazorpayPaymentId(paymentId);
        newMembership.setDob(dob);
        newMembership.setReferralCode(referralCode);

        UserMembership saved = userMembershipRepository.save(newMembership);

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

        return saved;
    }
}
