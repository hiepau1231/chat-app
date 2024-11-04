package com.chatapp.service;

import com.chatapp.model.RoomNotification;
import com.chatapp.model.RoomSettings;
import com.chatapp.repository.RoomNotificationRepository;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final RoomNotificationRepository notificationRepository;
    private final RoomStatusService roomStatusService;

    public void createNotification(String roomId, String userId, String content, RoomNotification.NotificationType type) {
        // Check if user has muted notifications
        RoomSettings settings = roomStatusService.getSettings(roomId);
        if (settings.getMutedUsers().contains(userId)) {
            return;
        }

        RoomNotification notification = new RoomNotification();
        notification.setRoomId(roomId);
        notification.setUserId(userId);
        notification.setType(type);
        notification.setContent(content);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setRead(false);

        notificationRepository.save(notification);
    }

    public List<RoomNotification> getUnreadNotifications(String userId) {
        return notificationRepository.findByUserIdAndReadFalseOrderByCreatedAtDesc(userId);
    }

    public void markAsRead(String notificationId) {
        RoomNotification notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new RuntimeException("Notification not found"));

        notification.setRead(true);
        notification.setReadAt(LocalDateTime.now());
        notificationRepository.save(notification);
    }

    public void markAllAsRead(String userId) {
        List<RoomNotification> notifications = notificationRepository.findByUserIdAndReadFalse(userId);
        for (RoomNotification notification : notifications) {
            notification.setRead(true);
            notification.setReadAt(LocalDateTime.now());
        }
        notificationRepository.saveAll(notifications);
    }
} 