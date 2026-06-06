package com.llbeauty.service;

import com.llbeauty.entity.Payment;
import com.llbeauty.entity.User;
import com.llbeauty.repository.PaymentRepository;
import com.razorpay.RazorpayException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentService {

    private final RazorpayService razorpayService;
    private final PaymentRepository paymentRepository;
    private final WalletService walletService;

    public PaymentService(RazorpayService razorpayService, PaymentRepository paymentRepository, WalletService walletService) {
        this.razorpayService = razorpayService;
        this.paymentRepository = paymentRepository;
        this.walletService = walletService;
    }

    /**
     * Legacy method retained for backward compatibility.
     */
    @Transactional
    public Payment initiatePayment(User user, Double amount, String paymentFor, String referenceId, String paymentMethod) throws RazorpayException {
        // Create Razorpay Order
        String receipt = paymentFor.toLowerCase() + "_" + referenceId + "_" + System.currentTimeMillis();
        com.razorpay.Order rzpOrder = razorpayService.createOrder(amount, receipt);

        // Create Payment Record
        Payment payment = new Payment();
        payment.setUser(user);
        payment.setAmount(amount);
        payment.setCurrency("INR");
        payment.setPaymentFor(paymentFor);
        payment.setReferenceId(referenceId);
        payment.setPaymentMethod(paymentMethod);
        payment.setStatus("CREATED");
        payment.setRazorpayOrderId(rzpOrder.get("id"));
        // No wallet involvement
        payment.setWalletDeductionAmount(0.0);
        payment.setTotalAmountPaid(null);
        return paymentRepository.save(payment);
    }

    /**
     * Unified payment flow used by checkout for product, membership, wallet top‑up and salon booking.
     * It optionally deducts the requested amount from the user's wallet first, then creates a Razorpay order
     * for any remaining balance. The resulting Payment entity records the wallet deduction and the final
     * amount paid (wallet + Razorpay).
     */
    @Transactional
    public Payment initiateUnifiedPayment(User user, Double amount, String paymentFor, String referenceId, boolean useWallet) throws RazorpayException {
        double walletDeduction = 0.0;
        double amountToCharge = amount;

        if (useWallet) {
            double walletBalance = walletService.getBalance(user).doubleValue();
            walletDeduction = Math.min(walletBalance, amount);
            if (walletDeduction > 0) {
                // Debit wallet immediately
                walletService.debit(user, walletDeduction, "Payment for " + paymentFor + " ID " + referenceId);
                amountToCharge = amount - walletDeduction;
            }
        }

        // Create Razorpay order for the remaining amount (if any). If amountToCharge is zero, we still create a
        // dummy order to keep the flow consistent; the caller can treat the payment as fully wallet‑paid.
        String receipt = paymentFor.toLowerCase() + "_" + referenceId + "_" + System.currentTimeMillis();
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
            return payment; // Already processed
        }

        payment.setStatus("SUCCESS");
        payment.setRazorpayPaymentId(razorpayPaymentId);
        payment.setRazorpaySignature(signature);
        // totalAmountPaid already includes wallet deduction; no further changes needed.
        return paymentRepository.save(payment);
    }

    @Transactional
    public Payment markPaymentFailed(String razorpayOrderId) {
        Payment payment = paymentRepository.findByRazorpayOrderId(razorpayOrderId);
        if (payment != null && !"SUCCESS".equals(payment.getStatus())) {
            payment.setStatus("FAILED");
            return paymentRepository.save(payment);
        }
        return payment;
    }

    @Transactional
    public boolean processRefund(String razorpayOrderId, String refundMethod, Double overrideAmount) {
        Payment payment = paymentRepository.findByRazorpayOrderId(razorpayOrderId);
        if (payment == null || !"SUCCESS".equals(payment.getStatus())) {
            return false;
        }

        Double refundAmount = overrideAmount != null ? overrideAmount : payment.getAmount();

        if ("WALLET".equalsIgnoreCase(refundMethod)) {
            // Mark as refunded; wallet credit will be handled by caller.
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
}
