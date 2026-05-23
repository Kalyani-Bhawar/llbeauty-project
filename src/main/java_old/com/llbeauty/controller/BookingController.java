package com.llbeauty.controller;

import com.llbeauty.entity.Appointment;
import com.llbeauty.entity.FranchiseLead;
import com.llbeauty.entity.User;
import com.llbeauty.repository.AppointmentRepository;
import com.llbeauty.repository.FranchiseLeadRepository;
import com.llbeauty.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@Slf4j
public class BookingController {

    private final AppointmentRepository appointmentRepository;
    private final FranchiseLeadRepository franchiseLeadRepository;
    private final UserRepository userRepository;

    @PostMapping("/salon/book")
    public String bookAppointment(@RequestParam("serviceName") String serviceName,
                                  @RequestParam("appointmentDate") String appointmentDateStr,
                                  @RequestParam("timeSlot") String timeSlot,
                                  RedirectAttributes redirectAttributes) {
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
            return "redirect:/auth/login?redirect=/salon";
        }

        String mobileOrEmail = auth.getName();
        Optional<User> userOpt = userRepository.findByMobile(mobileOrEmail);
        if (userOpt.isEmpty()) {
            return "redirect:/auth/login?redirect=/salon";
        }

        User user = userOpt.get();

        Appointment appointment = Appointment.builder()
                .userId(user.getId())
                .userName(user.getName())
                .userMobile(user.getMobile())
                .serviceName(serviceName)
                .appointmentDate(LocalDate.parse(appointmentDateStr))
                .timeSlot(timeSlot)
                .status("PENDING")
                .build();

        appointmentRepository.save(appointment);
        log.info("Appointment booked for user: {} on {}", user.getMobile(), appointmentDateStr);

        redirectAttributes.addFlashAttribute("successMessage", "Your appointment for " + serviceName + " has been booked successfully! Pay at Salon on arrival.");
        return "redirect:/salon";
    }

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
}
