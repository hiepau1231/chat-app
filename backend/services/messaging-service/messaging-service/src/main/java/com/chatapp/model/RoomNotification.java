package com.chatapp.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Document(collection = "room_notifications")
public class RoomNotification {
    @Id
    private String id;
    private String roomId;
    private String userId;
    private NotificationType type;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime readAt;
    private boolean read;

    public RoomNotification(String roomId, String userId, NotificationType type, String content) {
        this.roomId = roomId;
        this.userId = userId;
        this.type = type;
        this.content = content;
        this.createdAt = LocalDateTime.now();
        this.read = false;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getReadAt() {
        return readAt;
    }

    public void setReadAt(LocalDateTime readAt) {
        this.readAt = readAt;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public void markAsRead() {
        this.read = true;
        this.readAt = LocalDateTime.now();
    }

    public enum NotificationType {
        MESSAGE,
        MEMBER_JOIN,
        MEMBER_LEAVE,
        ROOM_UPDATE,
        MENTION,
        SYSTEM_ALERT
    }
}