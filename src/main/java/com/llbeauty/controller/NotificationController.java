package com.llbeauty.controller;

import com.llbeauty.entity.AdminNotification;
import com.llbeauty.entity.User;
import com.llbeauty.repository.AdminNotificationRepository;
import com.llbeauty.repository.UserRepository;
import com.llbeauty.service.NotificationService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * NotificationController – Handles all notification-related HTTP endpoints.
 *
 * Admin routes: /admin/notifications/**
 * User/API routes: /api/notifications/**
 */
@Controller
public class NotificationController {

    private final NotificationService notificationService;
    private final AdminNotificationRepository adminNotificationRepository;
    private final UserRepository userRepository;

    public NotificationController(NotificationService notificationService,
                                  AdminNotificationRepository adminNotificationRepository,
                                  UserRepository userRepository) {
        this.notificationService = notificationService;
        this.adminNotificationRepository = adminNotificationRepository;
        this.userRepository = userRepository;
    }

    private User getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            return userRepository.findByEmail(auth.getName()).orElse(null);
        }
        return null;
    }

    // ─── Admin Notification Pages ─────────────────────────────────────────────

    /**
     * Full admin notifications list page.
     * GET /admin/notifications
     */
    @GetMapping("/admin/notifications")
    public String viewAllAdminNotifications(Model model) {
        model.addAttribute("activeTab", "notifications");
        List<AdminNotification> allNotifications = adminNotificationRepository.findAll()
                .stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .toList();
        model.addAttribute("allNotifications", allNotifications);
        model.addAttribute("unreadCount", notificationService.getUnreadCount());
        return "admin/notifications";
    }

    /**
     * Mark a single admin notification as read.
     * POST /admin/notifications/{id}/read
     */
    @PostMapping("/admin/notifications/{id}/read")
    public String markAdminNotificationRead(@PathVariable("id") Long id,
                                             RedirectAttributes redirectAttributes) {
        notificationService.markAdminNotificationRead(id);
        redirectAttributes.addFlashAttribute("successMessage", "Notification marked as read.");
        return "redirect:/admin/notifications";
    }

    /**
     * Mark all admin notifications as read.
     * POST /admin/notifications/read-all
     */
    @PostMapping("/admin/notifications/read-all")
    public String markAllAdminNotificationsRead(RedirectAttributes redirectAttributes) {
        notificationService.markAllAdminNotificationsRead();
        redirectAttributes.addFlashAttribute("successMessage", "All notifications marked as read.");
        return "redirect:/admin/notifications";
    }

    /**
     * Delete a single admin notification.
     * POST /admin/notifications/{id}/delete
     */
    @PostMapping("/admin/notifications/{id}/delete")
    public String deleteAdminNotification(@PathVariable("id") Long id,
                                           RedirectAttributes redirectAttributes) {
        notificationService.deleteAdminNotification(id);
        redirectAttributes.addFlashAttribute("successMessage", "Notification deleted.");
        return "redirect:/admin/notifications";
    }

    /**
     * Mark admin notification as read from dropdown (AJAX-friendly redirect).
     * POST /admin/notifications/{id}/read-inline
     */
    @PostMapping("/admin/notifications/{id}/read-inline")
    public String markReadInline(@PathVariable("id") Long id,
                                  @RequestHeader(value = "Referer", defaultValue = "/admin/dashboard") String referer) {
        notificationService.markAdminNotificationRead(id);
        return "redirect:" + referer;
    }

    // ─── User Notification API ────────────────────────────────────────────────

    /**
     * Returns the current user's notifications as JSON.
     * GET /api/notifications/user
     */
    @GetMapping("/api/notifications/user")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getUserNotificationsJson() {
        User user = getAuthenticatedUser();
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        Map<String, Object> response = new HashMap<>();
        response.put("notifications", notificationService.getUserNotifications(user));
        response.put("unreadCount", notificationService.getUserUnreadCount(user));
        return ResponseEntity.ok(response);
    }

    /**
     * Mark a user notification as read.
     * POST /api/notifications/{id}/read
     */
    @PostMapping("/api/notifications/{id}/read")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> markUserNotificationRead(@PathVariable("id") Long id) {
        User user = getAuthenticatedUser();
        if (user == null) return ResponseEntity.status(401).build();
        notificationService.markAsRead(id);
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("unreadCount", notificationService.getUserUnreadCount(user));
        return ResponseEntity.ok(response);
    }

    /**
     * Mark all user notifications as read.
     * POST /api/notifications/read-all
     */
    @PostMapping("/api/notifications/read-all")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> markAllUserNotificationsRead() {
        User user = getAuthenticatedUser();
        if (user == null) return ResponseEntity.status(401).build();
        notificationService.markAllAsRead(user);
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("unreadCount", 0);
        return ResponseEntity.ok(response);
    }

    /**
     * Delete a user notification.
     * POST /api/notifications/{id}/delete
     */
    @PostMapping("/api/notifications/{id}/delete")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteUserNotification(@PathVariable("id") Long id) {
        User user = getAuthenticatedUser();
        if (user == null) return ResponseEntity.status(401).build();
        notificationService.deleteNotification(id);
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        return ResponseEntity.ok(response);
    }
}
