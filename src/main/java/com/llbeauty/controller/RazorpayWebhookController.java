package com.llbeauty.controller;

import com.llbeauty.entity.Appointment;
import com.llbeauty.entity.MembershipPurchase;
import com.llbeauty.entity.Order;
import com.llbeauty.entity.Payment;
import com.llbeauty.entity.User;
import com.llbeauty.repository.AppointmentRepository;
import com.llbeauty.repository.MembershipPurchaseRepository;
import com.llbeauty.repository.OrderRepository;
import com.llbeauty.repository.PaymentRepository;
import com.llbeauty.service.MembershipService;
import com.llbeauty.service.WalletService;
import com.llbeauty.service.RewardService;
import com.razorpay.Utils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Random;

@RestController
@RequestMapping("/razorpay")
public class RazorpayWebhookController {

    private static final Logger log = LoggerFactory.getLogger(RazorpayWebhookController.class);

    @Value("${razorpay.webhook.secret:dummysecret}")
    private String webhookSecret;

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final AppointmentRepository appointmentRepository;
    private final MembershipPurchaseRepository membershipPurchaseRepository;
    private final WalletService walletService;
    private final MembershipService membershipService;
    private final RewardService rewardService;

    public RazorpayWebhookController(PaymentRepository paymentRepository,
                                     OrderRepository orderRepository,
                                     AppointmentRepository appointmentRepository,
                                     MembershipPurchaseRepository membershipPurchaseRepository,
                                     WalletService walletService,
                                     MembershipService membershipService,
                                     RewardService rewardService) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.appointmentRepository = appointmentRepository;
        this.membershipPurchaseRepository = membershipPurchaseRepository;
        this.walletService = walletService;
        this.membershipService = membershipService;
        this.rewardService = rewardService;
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(@RequestBody String payload,
                                                @RequestHeader("X-Razorpay-Signature") String signature) {
        try {
            boolean isValid = Utils.verifyWebhookSignature(payload, signature, webhookSecret);
            if (!isValid) {
                log.error("Invalid Razorpay Webhook Signature");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature");
            }

            JSONObject json = new JSONObject(payload);
            String event = json.getString("event");
            JSONObject paymentEntity = json.getJSONObject("payload").getJSONObject("payment").getJSONObject("entity");
            
            String razorpayOrderId = paymentEntity.getString("order_id");
            String razorpayPaymentId = paymentEntity.getString("id");

            Payment payment = paymentRepository.findByRazorpayOrderId(razorpayOrderId);
            if (payment == null) {
                log.warn("Payment not found for orderId: {}", razorpayOrderId);
                return ResponseEntity.ok("OK");
            }

            if ("payment.captured".equals(event)) {
                if ("SUCCESS".equals(payment.getStatus())) {
                    return ResponseEntity.ok("Already processed");
                }
                payment.setStatus("SUCCESS");
                payment.setRazorpayPaymentId(razorpayPaymentId);
                paymentRepository.save(payment);

                processPaymentCaptured(payment);

            } else if ("payment.failed".equals(event)) {
                if (!"SUCCESS".equals(payment.getStatus())) {
                    payment.setStatus("FAILED");
                    payment.setRazorpayPaymentId(razorpayPaymentId);
                    paymentRepository.save(payment);
                }
            }

            return ResponseEntity.ok("OK");
        } catch (Exception e) {
            log.error("Error processing Razorpay Webhook", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing webhook");
        }
    }

    private void processPaymentCaptured(Payment payment) {
        String purpose = payment.getPaymentFor();
        User user = payment.getUser();
        String refId = payment.getReferenceId();

        try {
            if ("PRODUCT".equals(purpose) && refId != null) {
                Long orderId = Long.parseLong(refId);
                Order order = orderRepository.findById(orderId).orElse(null);
                if (order != null && "PENDING".equals(order.getStatus())) {
                    order.setStatus("SUCCESS");
                    order.setPaymentId(payment.getRazorpayPaymentId());
                    orderRepository.save(order);
                    
                    // Award Cashback & Points
                    Optional<com.llbeauty.entity.UserMembership> activeOpt = membershipService.getActiveMembership(user);
                    if (activeOpt.isPresent()) {
                        double cashbackAmount = order.getTotalAmount() * activeOpt.get().getMembership().getCashbackPercent();
                        if (cashbackAmount > 0) {
                            walletService.credit(user, BigDecimal.valueOf(cashbackAmount), "Cashback for Order #" + order.getId() + " (" + activeOpt.get().getMembership().getName() + ")", "CASHBACK");
                        }
                        rewardService.awardPoints(user, BigDecimal.valueOf(order.getTotalAmount()));
                    }
                }
            } else if ("SALON_DEPOSIT".equals(purpose) && refId != null) {
                Long appointmentId = Long.parseLong(refId);
                Appointment app = appointmentRepository.findById(appointmentId).orElse(null);
                if (app != null && !"CONFIRMED".equalsIgnoreCase(app.getStatus())) {
                    app.setStatus("CONFIRMED");
                    app.setPaymentStatus("PAID");
                    app.setToken("LL-SLOT-" + (1000 + new Random().nextInt(9000)));
                    appointmentRepository.save(app);
                    rewardService.awardPoints(user, BigDecimal.valueOf(payment.getAmount()));
                }
            } else if ("WALLET_TOPUP".equals(purpose)) {
                walletService.credit(user, BigDecimal.valueOf(payment.getAmount()), "Wallet Top-up via Razorpay", "RAZORPAY_TOPUP");
            } else if ("MEMBERSHIP".equals(purpose)) {
                MembershipPurchase purchase = membershipPurchaseRepository.findByRazorpayOrderId(payment.getRazorpayOrderId());
                if (purchase != null && "PENDING".equals(purchase.getStatus())) {
                    // Activate membership via service
                    membershipService.activateMembership(user, purchase.getMembership().getId(), payment.getRazorpayPaymentId(), payment.getRazorpayOrderId(), payment.getRazorpaySignature(), null, null);
                }
            }
        } catch (Exception e) {
            log.error("Failed to process payment capture for Payment ID: {}", payment.getId(), e);
        }
    }
}
