package com.llbeauty.service;

import com.llbeauty.entity.AdminNotification;
import com.llbeauty.entity.Notification;
import com.llbeauty.entity.User;

import java.util.List;

/**
 * NotificationService – contract for user and admin notification operations.
 */
public interface NotificationService {

    // ─── User Notifications ───────────────────────────────────────────────────

    void createNotification(User user, String title, String message);

    void createNotification(User user, String title, String message, Notification.NotificationType type);

    List<Notification> getUserNotifications(User user);

    List<Notification> getUnreadUserNotifications(User user);

    long getUserUnreadCount(User user);

    void markAsRead(Long notificationId);

    void markAllAsRead(User user);

    void deleteNotification(Long notificationId);

    // ─── Admin Notifications ──────────────────────────────────────────────────

    void sendNotification(String title, String message, String type, String linkUrl);

    void createNotificationToAdmins(String title, String message);

    List<AdminNotification> getRecentNotifications();

    List<AdminNotification> getUnreadNotifications();

    long getUnreadCount();

    void markAdminNotificationRead(Long id);

    void markAllAdminNotificationsRead();

    void deleteAdminNotification(Long id);

    void markAllAsRead();
}
