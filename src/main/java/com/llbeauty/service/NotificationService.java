package com.llbeauty.service;

import com.llbeauty.entity.AdminNotification;
import com.llbeauty.repository.AdminNotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class NotificationService {

    private final AdminNotificationRepository notificationRepository;

    public NotificationService(AdminNotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Transactional
    public void sendNotification(String title, String message, String type, String linkUrl) {
        AdminNotification notification = new AdminNotification();
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type);
        notification.setLinkUrl(linkUrl);
        notification.setRead(false);
        notificationRepository.save(notification);
    }

    public List<AdminNotification> getUnreadNotifications() {
        return notificationRepository.findByIsReadFalseOrderByCreatedAtDesc();
    }

    public List<AdminNotification> getRecentNotifications() {
        return notificationRepository.findTop10ByOrderByCreatedAtDesc();
    }

    public long getUnreadCount() {
        return notificationRepository.countByIsReadFalse();
    }

    @Transactional
    public void markAsRead(Long id) {
        notificationRepository.findById(id).ifPresent(notification -> {
            notification.setRead(true);
            notificationRepository.save(notification);
        });
    }

    @Transactional
    public void markAllAsRead() {
        List<AdminNotification> unread = notificationRepository.findByIsReadFalseOrderByCreatedAtDesc();
        for (AdminNotification n : unread) {
            n.setRead(true);
        }
        notificationRepository.saveAll(unread);
    }
}
