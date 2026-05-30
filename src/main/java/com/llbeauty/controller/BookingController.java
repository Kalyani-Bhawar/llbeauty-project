package com.llbeauty.controller;

import com.llbeauty.entity.Appointment;

import com.llbeauty.entity.FranchiseLead;
import com.llbeauty.entity.User;
import com.llbeauty.repository.AppointmentRepository;
import com.llbeauty.repository.FranchiseLeadRepository;
import com.llbeauty.repository.UserRepository;
import com.llbeauty.service.WalletService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDate;
import com.llbeauty.config.RazorpayConfig;

/**
 * BookingController - Handles appointment booking and franchise applications
 * 
 * ✅ FIXED: Removed duplicate methods that conflicted with SalonPaymentController
 *    - Removed: @GetMapping("/salon/payment") salonPaymentPage()
 *    - Removed: @PostMapping("/salon/confirm-payment") confirmSalonPayment()
 *    - Removed: @GetMapping("/salon/success") salonSuccessPage()
 * 
 * These payment-related methods are now exclusively handled by SalonPaymentController
 * which provides better Razorpay integration and payment tracking.
 */
@Controller
public class BookingController {
    
    private static final Logger log = LoggerFactory.getLogger(BookingController.class);
    
    private final AppointmentRepository appointmentRepository;
    private final FranchiseLeadRepository franchiseLeadRepository;
    private final UserRepository userRepository;
    private final WalletService walletService;
    private final RazorpayConfig razorpayConfig;

    public BookingController(AppointmentRepository appointmentRepository,
            FranchiseLeadRepository franchiseLeadRepository,
            UserRepository userRepository,
            WalletService walletService,
            RazorpayConfig razorpayConfig) {
        this.appointmentRepository = appointmentRepository;
        this.franchiseLeadRepository = franchiseLeadRepository;
        this.userRepository = userRepository;
        this.walletService = walletService;
        this.razorpayConfig = razorpayConfig;
    }

    private User getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            return userRepository.findByEmail(auth.getName()).orElse(null);
        }
        return null;
    }

    /**
     * Creates a new appointment and redirects to payment page
     * 
     * @param services Comma-separated list of services
     * @param appointmentDateStr Date in format YYYY-MM-DD
     * @param timeSlot Selected time slot
     * @param redirectAttributes For flash messages
     * @return Redirect to /salon/payment with appointmentId
     */
    @PostMapping("/salon/book")
    public String bookAppointment(@RequestParam("services") String services,
                                  @RequestParam("appointmentDate") String appointmentDateStr,
                                  @RequestParam("timeSlot") String timeSlot,
                                  RedirectAttributes redirectAttributes) {
        User user = getAuthenticatedUser();
        if (user == null) {
            return "redirect:/auth/login?redirect=/salon";
        }

        // Keep serviceName as first element in comma separated list for backwards compatibility
        String firstService = services.split(",")[0].trim();

        Appointment appointment = Appointment.builder()
                .userId(user.getId())
                .userName(user.getName())
                .userMobile(user.getMobile())
                .serviceName(firstService)
                .services(services)
                .appointmentDate(LocalDate.parse(appointmentDateStr))
                .timeSlot(timeSlot)
                .status("PAYMENT_PENDING")
                .paymentStatus("PENDING")
                .advancePaid(100.0)
                .build();

        Appointment saved = appointmentRepository.save(appointment);
        log.info("Appointment created, pending ₹100 payment. ID: {}", saved.getId());
        
        // Redirect to SalonPaymentController's payment page
        return "redirect:/salon/payment?appointmentId=" + saved.getId();
    }

    /**
     * Handles franchise application submissions
     * 
     * @param name Applicant name
     * @param mobile Applicant mobile number
     * @param email Applicant email
     * @param city Preferred city
     * @param budget Budget range
     * @param preferredLocation Preferred location details
     * @param franchiseType Type of franchise
     * @param redirectAttributes For success messages
     * @return Redirect to /franchise with success message
     */
    @PostMapping("/franchise/apply")
    public String applyFranchise(@RequestParam("name") String name,
                                 @RequestParam("mobile") String mobile,
                                 @RequestParam("email") String email,
                                 @RequestParam("city") String city,
                                 @RequestParam("budget") String budget,
                                 @RequestParam("preferredLocation") String preferredLocation,
                                 @RequestParam("franchiseType") String franchiseType,
                                 RedirectAttributes redirectAttributes) {
        FranchiseLead lead = FranchiseLead.builder()
                .name(name)
                .mobile(mobile)
                .email(email)
                .city(city)
                .budget(budget)
                .preferredLocation(preferredLocation)
                .franchiseType(franchiseType)
                .build();
        franchiseLeadRepository.save(lead);
        log.info("Franchise lead submitted by: {}", name);
        redirectAttributes.addFlashAttribute("successMessage", "Your Franchise inquiry has been submitted! Our team will contact you soon.");
        return "redirect:/franchise";
    }

    public RazorpayConfig getRazorpayConfig() {
        return razorpayConfig;
    }
}