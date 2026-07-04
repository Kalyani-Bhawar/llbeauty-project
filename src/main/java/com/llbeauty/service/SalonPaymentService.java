package com.llbeauty.service;

import com.llbeauty.constants.NxlConstants;
import com.llbeauty.entity.Appointment;
import com.llbeauty.entity.Payment;
import com.llbeauty.entity.User;
import com.llbeauty.repository.AppointmentRepository;
import com.llbeauty.exception.NxlException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Random;

@Service
public class SalonPaymentService {

    private final AppointmentRepository appointmentRepository;
    private final WalletService walletService;
    private final PaymentService paymentService;
    private final RewardService rewardService;

    public SalonPaymentService(AppointmentRepository appointmentRepository,
                               WalletService walletService,
                               PaymentService paymentService,
                               RewardService rewardService) {
        this.appointmentRepository = appointmentRepository;
        this.walletService = walletService;
        this.paymentService = paymentService;
        this.rewardService = rewardService;
    }

    @Transactional
    public Appointment confirmSalonPayment(User user, Long appointmentId, boolean useNxl, String paymentId, String razorpayOrderId, String razorpaySignature, boolean isDummyCredentials) throws Exception {
        Appointment app = appointmentRepository.findById(appointmentId).orElse(null);
        if (app == null || !app.getUserId().equals(user.getId())) {
            throw new IllegalArgumentException("Appointment not found.");
        }

        double total = 100.0;
        double walletApplied = 0.0;
        if (useNxl) {
            BigDecimal walletBal = walletService.getNxlBalance(user);
            walletApplied = Math.min(walletBal.doubleValue(), total);
            if (walletApplied > 0) {
                walletService.debitNxl(
                        user,
                        BigDecimal.valueOf(walletApplied),
                        NxlConstants.SOURCE_LLBEAUTY,
                        "SALON_" + appointmentId,
                        "Salon Booking Payment"
                );
            }
        }

        if (razorpayOrderId != null && razorpaySignature != null && !isDummyCredentials) {
            paymentService.verifyAndProcessPayment(razorpayOrderId, paymentId, razorpaySignature);
        }

        int tokenNum = 1000 + new Random().nextInt(9000);
        String bookingToken = "LL-SLOT-" + tokenNum;

        app.setStatus("CONFIRMED");
        app.setPaymentStatus("PAID");
        app.setToken(bookingToken);
        app.setNxlUsed(walletApplied);
        app.setAdvancePaid(total);
        app.setFinalPaidAmount(total);
        if (razorpayOrderId != null && !razorpayOrderId.isEmpty()) {
            app.setRazorpayOrderId(razorpayOrderId);
        }
        if (paymentId != null && !paymentId.isEmpty()) {
            app.setRazorpayPaymentId(paymentId);
        }
        appointmentRepository.save(app);

        rewardService.awardPoints(user, BigDecimal.valueOf(total));

        return app;
    }
}
