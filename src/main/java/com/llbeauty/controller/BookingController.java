package com.llbeauty.controller;

import com.llbeauty.entity.Appointment;


import com.llbeauty.entity.FranchiseLead;
import com.llbeauty.entity.User;
import com.llbeauty.repository.AppointmentRepository;
import com.llbeauty.repository.FranchiseLeadRepository;
import com.llbeauty.repository.UserRepository;
import com.llbeauty.repository.SalonServiceRepository;
import com.llbeauty.repository.AgentProfileRepository;
import com.llbeauty.entity.AgentProfile;
import com.llbeauty.service.WalletService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
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
    private final SalonServiceRepository salonServiceRepository;
    private final AgentProfileRepository agentProfileRepository;

    public BookingController(AppointmentRepository appointmentRepository,
            FranchiseLeadRepository franchiseLeadRepository,
            UserRepository userRepository,
            WalletService walletService,
            RazorpayConfig razorpayConfig,
            SalonServiceRepository salonServiceRepository,
            AgentProfileRepository agentProfileRepository) {
        this.appointmentRepository = appointmentRepository;
        this.franchiseLeadRepository = franchiseLeadRepository;
        this.userRepository = userRepository;
        this.walletService = walletService;
        this.razorpayConfig = razorpayConfig;
        this.salonServiceRepository = salonServiceRepository;
        this.agentProfileRepository = agentProfileRepository;
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
                                  RedirectAttributes redirectAttributes,@RequestParam(value = "referralCode", required = false)
    String referralCode) {
        User user = getAuthenticatedUser();
        if (user == null) {
            return "redirect:/auth/login?redirect=/salon";
        }

        // 1. Validate Duplicate Slot
        boolean slotTaken = appointmentRepository.existsByAppointmentDateAndTimeSlotAndStatusIn(
            LocalDate.parse(appointmentDateStr), timeSlot, Arrays.asList("CONFIRMED", "PAYMENT_PENDING"));
        if (slotTaken) {
            redirectAttributes.addFlashAttribute("errorMessage", "The selected time slot is already booked. Please choose another.");
            return "redirect:/salon";
        }

        // 2. Compute Total Amount
        double totalAmount = 0.0;
        if (services != null && !services.trim().isEmpty()) {
            List<String> serviceNames = Arrays.asList(services.split(","));
            for (int i = 0; i < serviceNames.size(); i++) {
                serviceNames.set(i, serviceNames.get(i).trim());
            }
            List<com.llbeauty.entity.SalonService> dbServices = salonServiceRepository.findByNameIn(serviceNames);
            for (com.llbeauty.entity.SalonService s : dbServices) {
                if (s.getPrice() != null) {
                    totalAmount += s.getPrice();
                }
            }
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
                .totalAmount(totalAmount)
                .build();

        appointment.setReferralCode(referralCode);
        System.out.println("SALON REF RECEIVED = " + referralCode);
        Appointment saved = appointmentRepository.save(appointment);
        if (referralCode != null && !referralCode.trim().isEmpty()) {

            Optional<AgentProfile> agentOpt =
                agentProfileRepository.findByReferralCode(referralCode.trim());

            if (agentOpt.isPresent()) {

                User agentUser = agentOpt.get().getUser();

                walletService.creditNxl(
                    agentUser,
                    BigDecimal.valueOf(50),
                    WalletService.SOURCE_REFERRAL,
                    "BOOKING_" + saved.getId(),
                    "Salon Booking Referral Reward"
                );
            }
        }
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
                                 @RequestParam(value = "referralCode", required = false) String referralCode,
                                 RedirectAttributes redirectAttributes) {
        User user = getAuthenticatedUser();
        if (user == null) {
            return "redirect:/auth/login?redirect=/franchise";
        }

        if (referralCode != null && !referralCode.trim().isEmpty()) {
            Optional<AgentProfile> agentOpt = agentProfileRepository.findByReferralCode(referralCode.trim());
            if (agentOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Invalid Referral Code");
                redirectAttributes.addFlashAttribute("name", name);
                redirectAttributes.addFlashAttribute("mobile", mobile);
                redirectAttributes.addFlashAttribute("email", email);
                redirectAttributes.addFlashAttribute("city", city);
                redirectAttributes.addFlashAttribute("budget", budget);
                redirectAttributes.addFlashAttribute("preferredLocation", preferredLocation);
                redirectAttributes.addFlashAttribute("franchiseType", franchiseType);
                redirectAttributes.addFlashAttribute("referralCode", referralCode);
                redirectAttributes.addFlashAttribute("highlightReferral", true);
                return "redirect:/franchise";
            }
        }

        FranchiseLead lead = FranchiseLead.builder()
                .name(name)
                .mobile(mobile)
                .email(email)
                .city(city)
                .budget(budget)
                .preferredLocation(preferredLocation)
                .franchiseType(franchiseType)
                .build();
        if (referralCode != null && !referralCode.trim().isEmpty()) {
            lead.setReferralCode(referralCode.trim());
        }
        lead.setStatus("NEW");
        lead.setCommissionGenerated(false);

        franchiseLeadRepository.save(lead);
        log.info("Franchise lead submitted by: {}", name);
        redirectAttributes.addFlashAttribute("successMessage", "Your Franchise inquiry has been submitted! Our team will contact you soon.");
        return "redirect:/franchise";
    }

    public RazorpayConfig getRazorpayConfig() {
        return razorpayConfig;
    }
}