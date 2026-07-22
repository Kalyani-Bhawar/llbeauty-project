package com.llbeauty.service;

import com.llbeauty.entity.AdminNotification;
import com.llbeauty.entity.Notification;
import com.llbeauty.entity.User;
import com.llbeauty.repository.UserRepository;
import com.llbeauty.repository.AdminNotificationRepository;
import com.llbeauty.repository.NotificationRepository;
import com.llbeauty.service.EmailService;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * NotificationServiceImpl – production-ready implementation of {@link NotificationService}.
 *
 * <ul>
 *   <li>User notifications → {@code notifications} table via {@link NotificationRepository}</li>
 *   <li>Admin notifications → {@code admin_notifications} table via {@link AdminNotificationRepository}</li>
 * </ul>
 */
@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final EmailService emailService;
    private final AdminNotificationRepository adminNotificationRepository;
    private final UserRepository userRepository;

    @Value("${app.base-url}")
    private String baseUrl;

    public NotificationServiceImpl(NotificationRepository notificationRepository,
                                   AdminNotificationRepository adminNotificationRepository,
                                   UserRepository userRepository,
                                   EmailService emailService) {
        this.notificationRepository = notificationRepository;
        this.adminNotificationRepository = adminNotificationRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    // ─── User Notifications ───────────────────────────────────────────────────

    @Override
    @Transactional
    public void createNotification(User user, String title, String message) {
        createNotification(user, title, message, Notification.NotificationType.INFO);
    }

    @Override
    @Transactional
    public void createNotification(User user, String title, String message, Notification.NotificationType type) {
        if (user == null || title == null || message == null) return;
        Notification notification = new Notification(user, title, message, type);
        notificationRepository.save(notification);
    }

    @Override
    public List<Notification> getUserNotifications(User user) {
        if (user == null) return List.of();
        return notificationRepository.findByUserOrderByCreatedAtDesc(user);
    }

    @Override
    public List<Notification> getUnreadUserNotifications(User user) {
        if (user == null) return List.of();
        return notificationRepository.findByUserAndIsReadFalseOrderByCreatedAtDesc(user);
    }

    @Override
    public long getUserUnreadCount(User user) {
        if (user == null) return 0;
        return notificationRepository.countByUserAndIsReadFalse(user);
    }

    @Override
    @Transactional
    public void markAsRead(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(n -> {
            n.setRead(true);
            n.setReadAt(LocalDateTime.now());
            notificationRepository.save(n);
        });
    }

    @Override
    @Transactional
    public void markAllAsRead(User user) {
        if (user == null) return;
        notificationRepository.markAllReadByUser(user);
    }

    @Override
    @Transactional
    public void deleteNotification(Long notificationId) {
        if (notificationRepository.existsById(notificationId)) {
            notificationRepository.deleteById(notificationId);
        }
    }

    // ─── Event specific notifications ───────────────────────────────────────
    @Override
    public void notifyProfileApproved(Long userId) {
        userRepository.findById(userId).ifPresent(user -> {
            createNotification(user, "Profile Approved", "Your profile has been approved.", Notification.NotificationType.INFO);
            // Send HTML email
            Map<String, Object> model = new HashMap<>();
            model.put("user", user);
            model.put("baseUrl", baseUrl);
            emailService.sendHtmlMail(user.getEmail(), "Your EVA Matrimony Profile Has Been Approved ❤️", "email/matrimony/profile-approved", model);
        });
    }

    @Override
    public void notifyInterestReceived(Long receiverId, Long senderId) {
        User receiver = userRepository.findById(receiverId).orElse(null);
        User sender = userRepository.findById(senderId).orElse(null);
        if (receiver != null && sender != null) {
            createNotification(receiver, "Interest Received",
                    "You have received a new interest from " + sender.getName() + ".",
                    Notification.NotificationType.INFO);
            // Send HTML email
            Map<String, Object> model = new HashMap<>();
            model.put("receiver", receiver);
            model.put("sender", sender);
            model.put("baseUrl", baseUrl);
            emailService.sendHtmlMail(receiver.getEmail(), "You Have Received a New EVA Matrimony Interest", "email/matrimony/interest-received", model);
        }
    }

    @Override
    public void notifyInterestAccepted(Long senderId, Long receiverId) {
        User sender = userRepository.findById(senderId).orElse(null);
        User receiver = userRepository.findById(receiverId).orElse(null);
        if (sender != null && receiver != null) {
            createNotification(sender, "Interest Accepted",
                    receiver.getName() + " accepted your interest.",
                    Notification.NotificationType.INFO);
            // Send HTML email
            Map<String, Object> model = new HashMap<>();
            model.put("sender", sender);
            model.put("receiver", receiver);
            model.put("baseUrl", baseUrl);
            emailService.sendHtmlMail(sender.getEmail(), "Your Interest Has Been Accepted", "email/matrimony/interest-accepted", model);
        }
    }

    @Override
    public void notifyMatchCreated(Long userId, Long partnerId) {
        User user = userRepository.findById(userId).orElse(null);
        User partner = userRepository.findById(partnerId).orElse(null);
        if (user != null && partner != null) {
            createNotification(user, "New Match Created",
                    "You are now matched with " + partner.getName() + ".",
                    Notification.NotificationType.INFO);
            createNotification(partner, "New Match Created",
                    "You are now matched with " + user.getName() + ".",
                    Notification.NotificationType.INFO);
            // Send HTML emails to both parties
            Map<String, Object> modelUser = new HashMap<>();
            modelUser.put("user", user);
            modelUser.put("partner", partner);
            modelUser.put("baseUrl", baseUrl);
            emailService.sendHtmlMail(user.getEmail(), "Congratulations! You Have a New EVA Matrimony Match ❤️", "email/matrimony/match-created", modelUser);
            Map<String, Object> modelPartner = new HashMap<>();
            modelPartner.put("user", partner);
            modelPartner.put("partner", user);
            modelPartner.put("baseUrl", baseUrl);
            emailService.sendHtmlMail(partner.getEmail(), "New Match Created", "email/matrimony/match-created", modelPartner);
        }
    }

    // ─── Admin Notifications ──────────────────────────────────────────────────

    @Override
    @Transactional
    public void createNotificationToAdmins(String title, String message) {
        if (title == null || message == null) return;
        AdminNotification notification = new AdminNotification();
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType("INFO");
        adminNotificationRepository.save(notification);
    }

    @Override
    public List<AdminNotification> getRecentNotifications() {
        return adminNotificationRepository.findTop10ByOrderByCreatedAtDesc();
    }

    @Override
    public long getUnreadCount() {
        return adminNotificationRepository.countByIsReadFalse();
    }

    @Override
    @Transactional
    public void markAdminNotificationRead(Long id) {
        adminNotificationRepository.findById(id).ifPresent(n -> {
            n.setRead(true);
            adminNotificationRepository.save(n);
        });
    }

    @Override
    @Transactional
    public void markAllAdminNotificationsRead() {
        List<AdminNotification> unread = adminNotificationRepository.findByIsReadFalseOrderByCreatedAtDesc();
        for (AdminNotification n : unread) {
            n.setRead(true);
        }
        adminNotificationRepository.saveAll(unread);
    }

    @Override
    @Transactional
    public void deleteAdminNotification(Long id) {
        if (adminNotificationRepository.existsById(id)) {
            adminNotificationRepository.deleteById(id);
        }
    }

    @Override
    @Transactional
    public void sendNotification(String title, String message, String type, String linkUrl) {
        AdminNotification notification = new AdminNotification();
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type);
        notification.setLinkUrl(linkUrl);
        notification.setRead(false);
        adminNotificationRepository.save(notification);
    }

    @Override
    public List<AdminNotification> getUnreadNotifications() {
        return adminNotificationRepository.findByIsReadFalseOrderByCreatedAtDesc();
    }

    @Override
    @Transactional
    public void markAllAsRead() {
        List<AdminNotification> unread = adminNotificationRepository.findByIsReadFalseOrderByCreatedAtDesc();
        for (AdminNotification n : unread) {
            n.setRead(true);
        }
        adminNotificationRepository.saveAll(unread);
    }
}