package com.llbeauty.controller;

import com.llbeauty.entity.Payment;
import com.llbeauty.entity.MembershipPurchase;
import com.llbeauty.repository.PaymentRepository;
import com.llbeauty.repository.MembershipPurchaseRepository;
import com.llbeauty.service.MembershipService;
import com.llbeauty.service.PaymentService;
import com.razorpay.Utils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Handles inbound Razorpay webhook events.
 *
 * Dependency direction (no cycles):
 *   Controller → PaymentService → (repositories, WalletService, RewardService, RazorpayService)
 *   Controller → MembershipService → PaymentService  ← PaymentService does NOT call back MembershipService
 *
 * MEMBERSHIP activation is deliberately performed here in the controller rather than inside
 * PaymentService, so that PaymentService remains free of any MembershipService dependency and
 * the circular dependency MembershipService ↔ PaymentService is fully eliminated.
 */
@RestController
@RequestMapping("/razorpay")
public class RazorpayWebhookController {

    private static final Logger log = LoggerFactory.getLogger(RazorpayWebhookController.class);

    @Value("${razorpay.webhook.secret:dummysecret}")
    private String webhookSecret;

    private final PaymentRepository paymentRepository;
    private final PaymentService paymentService;
    private final MembershipService membershipService;
    private final MembershipPurchaseRepository membershipPurchaseRepository;

    public RazorpayWebhookController(PaymentRepository paymentRepository,
                                     PaymentService paymentService,
                                     MembershipService membershipService,
                                     MembershipPurchaseRepository membershipPurchaseRepository) {
        this.paymentRepository = paymentRepository;
        this.paymentService = paymentService;
        this.membershipService = membershipService;
        this.membershipPurchaseRepository = membershipPurchaseRepository;
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
            JSONObject paymentEntity = json
                    .getJSONObject("payload")
                    .getJSONObject("payment")
                    .getJSONObject("entity");

            String razorpayOrderId = paymentEntity.getString("order_id");
            String razorpayPaymentId = paymentEntity.getString("id");

            Payment payment = paymentRepository.findByRazorpayOrderId(razorpayOrderId);
            if (payment == null) {
                log.warn("No payment record found for Razorpay orderId: {}", razorpayOrderId);
                return ResponseEntity.ok("OK");
            }

            if ("payment.captured".equals(event)) {
                if ("SUCCESS".equals(payment.getStatus())) {
                    log.info("Payment {} already processed, skipping.", razorpayOrderId);
                    return ResponseEntity.ok("Already processed");
                }

                // 1. Mark payment SUCCESS
                payment.setStatus("SUCCESS");
                payment.setRazorpayPaymentId(razorpayPaymentId);
                paymentRepository.save(payment);

                // 2. Handle side-effects for PRODUCT, SALON_DEPOSIT, WALLET_TOPUP
                paymentService.processPaymentCaptured(payment);

                // 3. Handle MEMBERSHIP activation separately to avoid circular dependency
                if ("MEMBERSHIP".equals(payment.getPaymentFor())) {
                    MembershipPurchase purchase = membershipPurchaseRepository
                            .findByRazorpayOrderId(razorpayOrderId);
                    if (purchase != null && "PENDING".equals(purchase.getStatus())) {
                        membershipService.activateMembership(
                                payment.getUser(),
                                purchase.getMembership().getId(),
                                razorpayPaymentId,
                                razorpayOrderId,
                                payment.getRazorpaySignature(),
                                null,
                                null
                        );
                    }
                }

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
}
