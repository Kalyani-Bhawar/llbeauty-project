package com.llbeauty.service;

import com.llbeauty.entity.ContactMessage;
import com.llbeauty.repository.ContactMessageRepository;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class ContactService {

    private final ContactMessageRepository contactMessageRepository;
    private final JavaMailSender mailSender;

    public ContactService(ContactMessageRepository contactMessageRepository, JavaMailSender mailSender) {
        this.contactMessageRepository = contactMessageRepository;
        this.mailSender = mailSender;
    }

    /**
     * Saves the contact message in the database and sends an email notification.
     *
     * @param name    User Name
     * @param email   User Email
     * @param phone   User Phone Number
     * @param message User Message
     * @return The saved ContactMessage entity
     */
    public ContactMessage saveAndSendEmail(String name, String email, String phone, String message) {
        ContactMessage contactMessage = new ContactMessage();
        contactMessage.setName(name);
        contactMessage.setEmail(email);
        contactMessage.setPhone(phone);
        contactMessage.setMessage(message);
        contactMessage.setCreatedAt(LocalDateTime.now());
        
        // Save to database
        ContactMessage savedMessage = contactMessageRepository.save(contactMessage);

        // Send email
        sendContactEmail(savedMessage);

        return savedMessage;
    }

    private void sendContactEmail(ContactMessage message) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo("kalyanibhawar3@gmail.com");
            mailMessage.setReplyTo(message.getEmail());
            mailMessage.setSubject("L.L. Beauty - New Contact Us Inquiry");

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDate = message.getCreatedAt().format(formatter);

            String text = "Dear Admin,\n\n" +
                    "You have received a new inquiry from the Contact Us form:\n\n" +
                    "User Name: " + message.getName() + "\n" +
                    "User Email: " + message.getEmail() + "\n" +
                    "User Phone Number: " + message.getPhone() + "\n" +
                    "Submission Date & Time: " + formattedDate + "\n\n" +
                    "User Message:\n" + message.getMessage() + "\n\n" +
                    "Regards,\n" +
                    "L.L. Beauty Portal";

            mailMessage.setText(text);
            mailSender.send(mailMessage);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email notification: " + e.getMessage(), e);
        }
    }
}
