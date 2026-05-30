package com.llbeauty.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    /**
     * Send OTP Email
     */
    public void sendOtp(String email, String otp) {

        try {

            SimpleMailMessage message = new SimpleMailMessage();

            message.setTo(email);
            message.setSubject("L.L. Beauty - Login Verification OTP");

            message.setText(
                    "Dear Customer,\n\n" +
                    "Your One Time Password (OTP) for login is:\n\n" +
                    otp +
                    "\n\n" +
                    "This OTP is valid for 5 minutes.\n\n" +
                    "If you did not request this OTP, please ignore this email.\n\n" +
                    "Regards,\n" +
                    "L.L. Beauty Team"
            );

            mailSender.send(message);

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to send OTP email: " + e.getMessage()
            );
        }
    }

    /**
     * Send Welcome Email After Registration
     */
    public void sendWelcomeEmail(String email, String name) {

        try {

            SimpleMailMessage message = new SimpleMailMessage();

            message.setTo(email);
            message.setSubject("Welcome to L.L. Beauty");

            message.setText(
                    "Dear " + name + ",\n\n" +
                    "Welcome to L.L. Beauty.\n\n" +
                    "Your account has been created successfully.\n\n" +
                    "You can now login, purchase memberships, earn rewards and enjoy exclusive discounts.\n\n" +
                    "Thank you for choosing L.L. Beauty.\n\n" +
                    "Regards,\n" +
                    "L.L. Beauty Team"
            );

            mailSender.send(message);

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to send welcome email: " + e.getMessage()
            );
        }
    }
}