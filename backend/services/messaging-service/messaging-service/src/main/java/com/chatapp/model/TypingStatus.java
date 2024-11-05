package com.chatapp.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class TypingStatus {
    private String userId;
    private String roomId;
    private boolean isTyping;
    private LocalDateTime timestamp;

    public TypingStatus(String userId, String roomId, boolean isTyping) {
        this.userId = userId;
        this.roomId = roomId;
        this.isTyping = isTyping;
        this.timestamp = LocalDateTime.now();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public boolean isTyping() {
        return isTyping;
    }

    public void setTyping(boolean typing) {
        isTyping = typing;
        this.timestamp = LocalDateTime.now();
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public void startTyping() {
        this.isTyping = true;
        this.timestamp = LocalDateTime.now();
    }

    public void stopTyping() {
        this.isTyping = false;
        this.timestamp = LocalDateTime.now();
    }
}