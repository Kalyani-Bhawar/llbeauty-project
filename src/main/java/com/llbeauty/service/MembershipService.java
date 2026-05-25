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

@Service
public class MembershipService {

    private final MembershipRepository membershipRepository;
    private final UserMembershipRepository userMembershipRepository;
    private final WalletService walletService;

    @Value("${razorpay.key.id:rzp_test_dummy}")
    private String razorpayKeyId;

    @Value("${razorpay.key.secret:dummysecret}")
    private String razorpayKeySecret;

    public MembershipService(MembershipRepository membershipRepository,
                             UserMembershipRepository userMembershipRepository,
                             WalletService walletService) {
        this.membershipRepository = membershipRepository;
        this.userMembershipRepository = userMembershipRepository;
        this.walletService = walletService;
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

        Map<String, Object> response = new HashMap<>();
        response.put("planId", planId);
        response.put("planName", newPlan.getName());
        response.put("price", finalPrice);
        response.put("isUpgrade", isUpgrade);
        response.put("currentMembershipId", currentMembershipId);

        // Generate Razorpay Order
        if (finalPrice > 0 && !isDummyCredentials()) {
            try {
                RazorpayClient client = new RazorpayClient(razorpayKeyId, razorpayKeySecret);
                JSONObject orderRequest = new JSONObject();
                orderRequest.put("amount", (int) Math.round(finalPrice * 100)); // in paise
                orderRequest.put("currency", "INR");
                orderRequest.put("receipt", "member_" + user.getId() + "_" + System.currentTimeMillis());

                com.razorpay.Order order = client.orders.create(orderRequest);
                response.put("razorpayOrderId", order.get("id"));
                response.put("key", razorpayKeyId);
                response.put("useMock", false);
            } catch (Exception e) {
                // Log exception and fallback to mock
                response.put("useMock", true);
                response.put("razorpayOrderId", "mock_order_" + System.currentTimeMillis());
                response.put("key", "mock_key");
            }
        } else {
            response.put("useMock", true);
            response.put("razorpayOrderId", "mock_order_" + System.currentTimeMillis());
            response.put("key", "mock_key");
        }

        return response;
    }

    /**
     * Activates or upgrades the membership after verified payment
     */
    @Transactional
    public UserMembership activateMembership(User user, Long planId, String paymentId, String orderId) {
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

        UserMembership saved = userMembershipRepository.save(newMembership);

        // Grant Welcome Credits
        walletService.credit(user, plan.getWelcomeCredits(), "Welcome credit for " + plan.getName() + " activation");

        return saved;
    }

    private boolean isDummyCredentials() {
        return "rzp_test_dummy".equals(razorpayKeyId) || razorpayKeyId == null || razorpayKeyId.trim().isEmpty();
    }
}
