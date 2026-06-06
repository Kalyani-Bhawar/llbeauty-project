package com.llbeauty.controller;

import com.llbeauty.service.ContactService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ContactController {

    private static final Logger log = LoggerFactory.getLogger(ContactController.class);
    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @PostMapping("/contact/submit")
    public String submitContact(
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("phone") String phone,
            @RequestParam("message") String message,
            RedirectAttributes redirectAttributes) {

        try {
            contactService.saveAndSendEmail(name, email, phone, message);
            redirectAttributes.addFlashAttribute("successMessage", 
                    "Your message has been sent successfully. Our team will contact you soon.");
            log.info("Contact form successfully processed for user: {}", email);
        } catch (Exception e) {
            log.error("Error processing contact form submission from {}: {}", email, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", 
                    "Failed to send message: " + e.getMessage() + ". Please try again later.");
        }

        return "redirect:/contact";
    }
}
