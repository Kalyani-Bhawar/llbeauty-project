package com.llbeauty.controller;

import com.llbeauty.entity.Appointment;
import com.llbeauty.entity.User;
import com.llbeauty.repository.AppointmentRepository;
import com.llbeauty.repository.UserRepository;
import com.llbeauty.service.PdfReceiptService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@Controller
public class PdfController {

    private final AppointmentRepository appointmentRepository;
    private final PdfReceiptService pdfReceiptService;
    private final UserRepository userRepository;

    public PdfController(AppointmentRepository appointmentRepository, PdfReceiptService pdfReceiptService, UserRepository userRepository) {
        this.appointmentRepository = appointmentRepository;
        this.pdfReceiptService = pdfReceiptService;
        this.userRepository = userRepository;
    }

    private User getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            return null;
        }
        return userRepository.findByEmail(auth.getName()).orElse(null);
    }

    @GetMapping("/salon/receipt/{id}")
    public ResponseEntity<byte[]> downloadUserReceipt(@PathVariable("id") Long id) {
        Optional<Appointment> appointmentOpt = appointmentRepository.findById(id);
        if (appointmentOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Appointment appointment = appointmentOpt.get();

        // Verify if the current user owns this appointment or is an admin
        User user = getAuthenticatedUser();
        if (user == null) {
            return ResponseEntity.status(401).build();
        }

        boolean isAdmin = "ADMIN".equals(user.getRole());
        if (!isAdmin && !appointment.getUserId().equals(user.getId())) {
            return ResponseEntity.status(403).build();
        }

        byte[] pdfBytes = pdfReceiptService.generateSalonReceipt(appointment);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "Salon_Receipt_" + appointment.getId() + ".pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }

    @GetMapping("/admin/salon/receipt/{id}")
    public ResponseEntity<byte[]> downloadAdminReceipt(@PathVariable("id") Long id) {
        Optional<Appointment> appointmentOpt = appointmentRepository.findById(id);
        if (appointmentOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        byte[] pdfBytes = pdfReceiptService.generateSalonReceipt(appointmentOpt.get());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "Salon_Receipt_" + id + ".pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }
}
