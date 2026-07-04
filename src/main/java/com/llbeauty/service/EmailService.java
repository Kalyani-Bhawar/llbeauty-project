package com.llbeauty.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    /**
     * Send OTP Email
     */
    @Async
    public void sendOtp(String email, String otp) {

        try {

            SimpleMailMessage message = new SimpleMailMessage();

            message.setTo(email);
            message.setSubject("EVA Beauty - Login Verification OTP");

            message.setText(
                    "Dear Customer,\n\n" +
                    "Your One Time Password (OTP) for login is:\n\n" +
                    otp +
                    "\n\n" +
                    "This OTP is valid for 5 minutes.\n\n" +
                    "If you did not request this OTP, please ignore this email.\n\n" +
                    "Regards,\n" +
                    "EVA Beauty Team"
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
                    "Welcome to EVA Beauty.\n\n" +
                    "Your account has been created successfully.\n\n" +
                    "You can now login, purchase memberships, earn rewards and enjoy exclusive discounts.\n\n" +
                    "Thank you for choosing EVA Beauty.\n\n" +
                    "Regards,\n" +
                    "EVA Beauty Team"
            );

            mailSender.send(message);

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to send welcome email: " + e.getMessage()
            );
        }
    }

    public void sendMembershipActivatedEmail(String email, String name, String planName, String memberId, String expiryDate) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("EVA Beauty VIP Membership Activated!");
            message.setText(
                "Dear " + name + ",\n\n" +
                "Congratulations! Your " + planName + " membership has been successfully activated.\n\n" +
                "Membership Details:\n" +
                "- Plan Type: " + planName + "\n" +
                "- Permanent Member ID: " + memberId + "\n" +
                "- Expiry Date: " + expiryDate + "\n\n" +
                "Log in to your account dashboard to view your new VIP Digital Membership Card, check your wallet balance, and explore your premium rewards and benefits.\n\n" +
                "Thank you for being part of the EVA Beauty family!\n\n" +
                "Regards,\n" +
                "EVA Beauty Team"
            );
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send activation email: " + e.getMessage());
        }
    }

    public void sendMembershipUpgradedEmail(String email, String name, String oldPlan, String newPlan, String memberId) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("EVA Beauty VIP Membership Upgraded!");
            message.setText(
                "Dear " + name + ",\n\n" +
                "Wonderful news! Your VIP membership has been successfully upgraded from " + oldPlan + " to " + newPlan + ".\n\n" +
                "Details:\n" +
                "- Upgraded To: " + newPlan + "\n" +
                "- Member ID: " + memberId + "\n\n" +
                "Your higher tier multiplier is now active, so you will earn more reward points and receive premium benefits instantly!\n\n" +
                "Regards,\n" +
                "EVA Beauty Team"
            );
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send upgrade email: " + e.getMessage());
        }
    }

    public void sendMembershipRenewalEmail(String email, String name, String planName, String newExpiryDate) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("EVA Beauty VIP Membership Renewed!");
            message.setText(
                "Dear " + name + ",\n\n" +
                "Thank you for renewing your " + planName + " membership with EVA Beauty!\n\n" +
                "Your membership validity has been extended.\n" +
                "- Plan Type: " + planName + "\n" +
                "- New Expiry Date: " + newExpiryDate + "\n\n" +
                "We are thrilled to continue serving you with our premium beauty offerings.\n\n" +
                "Regards,\n" +
                "EVA Beauty Team"
            );
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send renewal email: " + e.getMessage());
        }
    }

    public void sendMembershipExpiryReminderEmail(String email, String name, String planName, String expiryDate) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("EVA Beauty VIP Membership Expiry Reminder");
            message.setText(
                "Dear " + name + ",\n\n" +
                "This is a friendly reminder that your " + planName + " membership is expiring soon on " + expiryDate + ".\n\n" +
                "Renew your membership today to keep enjoying your cashbacks, premium rewards, and VIP benefits without interruption.\n\n" +
                "Regards,\n" +
                "EVA Beauty Team"
            );
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send expiry reminder email: " + e.getMessage());
        }
    }
    
    public void sendFranchiseStatusEmail(String email, String name, String status, String remarks) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);

            String subject;
            String statusLine;

            switch (status.toUpperCase()) {
                case "APPROVED":
                    subject = "🎉 Your EVA Franchise Application is Approved!";
                    statusLine = "Congratulations! Your franchise application has been APPROVED.";
                    break;
                case "REJECTED":
                    subject = "EVA Franchise Application Update";
                    statusLine = "We regret to inform you that your franchise application has been REJECTED.";
                    break;
                case "CONTACTED":
                    subject = "EVA Franchise - Our Team Will Contact You";
                    statusLine = "Our franchise team has reviewed your application and will contact you shortly.";
                    break;
                case "INTERESTED":
                    subject = "EVA Franchise - Application In Progress";
                    statusLine = "Great news! We are interested in taking your franchise application forward.";
                    break;
                default:
                    subject = "EVA Franchise Application Status Update";
                    statusLine = "Your franchise application status has been updated to: " + status;
            }

            message.setSubject(subject);
            message.setText(
                "Dear " + name + ",\n\n" +
                statusLine + "\n\n" +
                (remarks != null && !remarks.trim().isEmpty() ? "Remarks from our team: " + remarks + "\n\n" : "") +
                "For any queries, feel free to reach out to our franchise support team.\n\n" +
                "Regards,\n" +
                "EVA Beauty Franchise Team"
            );

            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send franchise status email: " + e.getMessage());
        }
    }
}