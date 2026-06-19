package com.llbeauty.service;

import com.llbeauty.entity.AdminNotification;
import com.llbeauty.entity.Notification;
import com.llbeauty.entity.User;
import com.llbeauty.repository.AdminNotificationRepository;
import com.llbeauty.repository.NotificationRepository;
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
    private final AdminNotificationRepository adminNotificationRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository,
                                   AdminNotificationRepository adminNotificationRepository) {
        this.notificationRepository = notificationRepository;
        this.adminNotificationRepository = adminNotificationRepository;
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
