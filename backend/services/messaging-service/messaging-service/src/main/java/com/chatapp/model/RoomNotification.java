package com.chatapp.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Data
@Document(collection = "room_notifications")
@NoArgsConstructor
@AllArgsConstructor
public class RoomNotification {
    @Id
    private String id;
    private String roomId;
    private String userId;
    private String content;
    private NotificationType type;
    private boolean read;
    private LocalDateTime createdAt;
    private LocalDateTime readAt;

    public enum NotificationType {
        MESSAGE,
        MENTION,
        SYSTEM
    }
} 