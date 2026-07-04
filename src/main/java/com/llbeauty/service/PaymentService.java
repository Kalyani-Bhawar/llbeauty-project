package com.llbeauty.service;

import com.llbeauty.entity.Payment;
import com.llbeauty.entity.User;
import com.llbeauty.repository.PaymentRepository;
import com.razorpay.RazorpayException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Random;

/**
 * PaymentService is responsible ONLY for:
 *  - Creating payment records (initiatePayment, initiateUnifiedPayment)
 *  - Verifying Razorpay signatures (verifyAndProcessPayment)
 *  - Updating payment status (markPaymentFailed)
 *  - Issuing refunds via Razorpay or wallet flag (processRefund)
 *  - Fulfilling post-payment side-effects for PRODUCT, SALON_DEPOSIT, and WALLET_TOPUP (processPaymentCaptured)
 *
 * PaymentService must NEVER depend on MembershipService to avoid a circular dependency.
 * Membership activation after a successful payment is handled externally by the caller
 * (e.g. RazorpayWebhookController, MembershipService.activateMembership).
 */
@Service
public class PaymentService {

    private final RazorpayService razorpayService;
    private final PaymentRepository paymentRepository;
    private final WalletService walletService;
    private final com.llbeauty.repository.OrderRepository orderRepository;
    private final com.llbeauty.repository.AppointmentRepository appointmentRepository;
    private final com.llbeauty.repository.MembershipPurchaseRepository membershipPurchaseRepository;
    private final RewardService rewardService;

    public PaymentService(RazorpayService razorpayService,
                          PaymentRepository paymentRepository,
                          WalletService walletService,
                          com.llbeauty.repository.OrderRepository orderRepository,
                          com.llbeauty.repository.AppointmentRepository appointmentRepository,
                          com.llbeauty.repository.MembershipPurchaseRepository membershipPurchaseRepository,
                          RewardService rewardService) {
        this.razorpayService = razorpayService;
        this.paymentRepository = paymentRepository;
        this.walletService = walletService;
        this.orderRepository = orderRepository;
        this.appointmentRepository = appointmentRepository;
        this.membershipPurchaseRepository = membershipPurchaseRepository;
        this.rewardService = rewardService;
    }

    /**
     * Legacy method retained for backward compatibility.
     * Creates a Razorpay order and persists a Payment record in CREATED status.
     */
    @Transactional
    public Payment initiatePayment(User user, Double amount, String paymentFor,
                                   String referenceId, String paymentMethod) throws RazorpayException {
        String cleanRef = referenceId.replaceAll("[^a-zA-Z0-9]", "");
        String shortRef = cleanRef.length() > 15 ? cleanRef.substring(cleanRef.length() - 15) : cleanRef;
        String receipt = "r_" + shortRef + "_" + (System.currentTimeMillis() % 100000000L);
        com.razorpay.Order rzpOrder = razorpayService.createOrder(amount, receipt);

        Payment payment = new Payment();
        payment.setUser(user);
        payment.setAmount(amount);
        payment.setCurrency("INR");
        payment.setPaymentFor(paymentFor);
        payment.setReferenceId(referenceId);
        payment.setPaymentMethod(paymentMethod);
        payment.setStatus("CREATED");
        payment.setRazorpayOrderId(rzpOrder.get("id"));
        payment.setWalletDeductionAmount(0.0);
        payment.setTotalAmountPaid(null);
        return paymentRepository.save(payment);
    }

    /**
     * Unified payment flow: optionally deducts wallet balance first, then creates a Razorpay
     * order for the remaining amount. Used by checkout for product, membership, wallet top-up
     * and salon booking.
     */
    @Transactional
    public Payment initiateUnifiedPayment(User user, Double amount, String paymentFor,
                                          String referenceId, boolean useWallet) throws RazorpayException {
        double walletDeduction = 0.0;
        double amountToCharge = amount;

        if (useWallet) {
            double walletBalance = walletService.getBalance(user).doubleValue();
            walletDeduction = Math.min(walletBalance, amount);
            if (walletDeduction > 0) {
                walletService.debit(user, walletDeduction, "Payment for " + paymentFor + " ID " + referenceId);
                amountToCharge = amount - walletDeduction;
            }
        }

        String cleanRef = referenceId.replaceAll("[^a-zA-Z0-9]", "");
        String shortRef = cleanRef.length() > 15 ? cleanRef.substring(cleanRef.length() - 15) : cleanRef;
        String receipt = "r_" + shortRef + "_" + (System.currentTimeMillis() % 100000000L);
        com.razorpay.Order rzpOrder = null;
        if (amountToCharge > 0) {
            rzpOrder = razorpayService.createOrder(amountToCharge, receipt);
        }

        Payment payment = new Payment();
        payment.setUser(user);
        payment.setAmount(amount);
        payment.setCurrency("INR");
        payment.setPaymentFor(paymentFor);
        payment.setReferenceId(referenceId);
        payment.setPaymentMethod(useWallet ? (amountToCharge > 0 ? "RAZORPAY+WALLET" : "WALLET") : "RAZORPAY");
        payment.setStatus("CREATED");
        payment.setWalletDeductionAmount(walletDeduction);
        payment.setTotalAmountPaid(amount - walletDeduction + (rzpOrder != null ? amountToCharge : 0.0));
        if (rzpOrder != null) {
            payment.setRazorpayOrderId(rzpOrder.get("id"));
        }
        return paymentRepository.save(payment);
    }

    /**
     * Verifies a Razorpay payment signature and marks the Payment as SUCCESS.
     */
    @Transactional
    public Payment verifyAndProcessPayment(String razorpayOrderId, String razorpayPaymentId, String signature) {
        boolean isValid = razorpayService.verifySignature(razorpayOrderId, razorpayPaymentId, signature);
        if (!isValid) {
            throw new IllegalArgumentException("Invalid payment signature");
        }

        Payment payment = paymentRepository.findByRazorpayOrderId(razorpayOrderId);
        if (payment == null) {
            throw new IllegalArgumentException("Payment record not found");
        }

        if ("SUCCESS".equals(payment.getStatus())) {
            return payment; // Idempotent: already processed
        }

        payment.setStatus("SUCCESS");
        payment.setRazorpayPaymentId(razorpayPaymentId);
        payment.setRazorpaySignature(signature);
        return paymentRepository.save(payment);
    }

    /**
     * Marks a payment as FAILED.
     */
    @Transactional
    public Payment markPaymentFailed(String razorpayOrderId) {
        Payment payment = paymentRepository.findByRazorpayOrderId(razorpayOrderId);
        if (payment != null && !"SUCCESS".equals(payment.getStatus())) {
            payment.setStatus("FAILED");
            return paymentRepository.save(payment);
        }
        return payment;
    }

    /**
     * Issues a refund. The 'WALLET' method flags the payment and assumes the caller credits
     * the user's wallet. The 'RAZORPAY' method calls the Razorpay refund API directly.
     */
    @Transactional
    public boolean processRefund(String razorpayOrderId, String refundMethod, Double overrideAmount) {
        Payment payment = paymentRepository.findByRazorpayOrderId(razorpayOrderId);
        if (payment == null || !"SUCCESS".equals(payment.getStatus())) {
            return false;
        }

        Double refundAmount = overrideAmount != null ? overrideAmount : payment.getAmount();

        if ("WALLET".equalsIgnoreCase(refundMethod)) {
            payment.setStatus("REFUNDED_WALLET");
            paymentRepository.save(payment);
            return true;
        } else if ("RAZORPAY".equalsIgnoreCase(refundMethod)) {
            try {
                if (payment.getRazorpayPaymentId() != null) {
                    razorpayService.refundPayment(payment.getRazorpayPaymentId(), refundAmount);
                    payment.setStatus("REFUNDED_ORIGINAL");
                    paymentRepository.save(payment);
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * Handles post-capture side-effects for PRODUCT, SALON_DEPOSIT, and WALLET_TOPUP payments.
     *
     * NOTE: MEMBERSHIP payments are intentionally NOT handled here to avoid a circular dependency
     * between PaymentService and MembershipService. Membership activation after a Razorpay
     * webhook is handled directly in RazorpayWebhookController, which calls MembershipService
     * independently after this method returns.
     */
    @Transactional
    public void processPaymentCaptured(Payment payment) {
        String purpose = payment.getPaymentFor();
        User user = payment.getUser();
        String refId = payment.getReferenceId();

        if ("PRODUCT".equals(purpose) && refId != null) {
            Long orderId = Long.parseLong(refId);
            com.llbeauty.entity.Order order = orderRepository.findById(orderId).orElse(null);
            if (order != null && "PENDING".equals(order.getStatus())) {
                order.setStatus("SUCCESS");
                order.setPaymentId(payment.getRazorpayPaymentId());
                orderRepository.save(order);
                // Cashback and reward points are handled by CheckoutService.completeOrder
                // which is the authoritative post-order success handler.
                rewardService.awardPoints(user, BigDecimal.valueOf(order.getTotalAmount()));
            }

        } else if ("SALON_DEPOSIT".equals(purpose) && refId != null) {
            Long appointmentId = Long.parseLong(refId);
            com.llbeauty.entity.Appointment app = appointmentRepository.findById(appointmentId).orElse(null);
            if (app != null && !"CONFIRMED".equalsIgnoreCase(app.getStatus())) {
                app.setStatus("CONFIRMED");
                app.setPaymentStatus("PAID");
                app.setToken("LL-SLOT-" + (1000 + new Random().nextInt(9000)));
                if (payment.getRazorpayOrderId() != null) {
                    app.setRazorpayOrderId(payment.getRazorpayOrderId());
                }
                if (payment.getRazorpayPaymentId() != null) {
                    app.setRazorpayPaymentId(payment.getRazorpayPaymentId());
                }
                appointmentRepository.save(app);
                rewardService.awardPoints(user, BigDecimal.valueOf(payment.getAmount()));
            }

        } else if ("WALLET_TOPUP".equals(purpose)) {
            walletService.creditNxl(
                user,
                BigDecimal.valueOf(payment.getAmount()),
                WalletService.SOURCE_PAYMENT,
                "TOPUP_" + payment.getRazorpayOrderId(),
                "Wallet Top-up via Razorpay"
            );
        }
        // MEMBERSHIP case is intentionally omitted — handled by the webhook controller.
    }
}
