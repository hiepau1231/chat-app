package com.chatapp.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Document(collection = "room_statuses")
public class RoomStatus {
    @Id
    private String id;
    private String roomId;
    private List<String> onlineUsers = new ArrayList<>();
    private List<String> typingUsers = new ArrayList<>();
    private RoomSettings settings;
    private LocalDateTime lastActivity;

    public RoomStatus(String roomId, String creatorId) {
        this.roomId = roomId;
        this.onlineUsers = new ArrayList<>();
        this.typingUsers = new ArrayList<>();
        this.settings = new RoomSettings(creatorId);
        this.lastActivity = LocalDateTime.now();
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

    public List<String> getOnlineUsers() {
        return onlineUsers;
    }

    public void setOnlineUsers(List<String> onlineUsers) {
        this.onlineUsers = onlineUsers;
    }

    public List<String> getTypingUsers() {
        return typingUsers;
    }

    public void setTypingUsers(List<String> typingUsers) {
        this.typingUsers = typingUsers;
    }

    public RoomSettings getSettings() {
        return settings;
    }

    public void setSettings(RoomSettings settings) {
        this.settings = settings;
    }

    public LocalDateTime getLastActivity() {
        return lastActivity;
    }

    public void setLastActivity(LocalDateTime lastActivity) {
        this.lastActivity = lastActivity;
    }

    public void addOnlineUser(String userId) {
        if (!this.onlineUsers.contains(userId)) {
            this.onlineUsers.add(userId);
            this.lastActivity = LocalDateTime.now();
        }
    }

    public void removeOnlineUser(String userId) {
        this.onlineUsers.remove(userId);
        this.lastActivity = LocalDateTime.now();
    }

    public void addTypingUser(String userId) {
        if (!this.typingUsers.contains(userId)) {
            this.typingUsers.add(userId);
            this.lastActivity = LocalDateTime.now();
        }
    }

    public void removeTypingUser(String userId) {
        this.typingUsers.remove(userId);
        this.lastActivity = LocalDateTime.now();
    }

    public void updateLastActivity() {
        this.lastActivity = LocalDateTime.now();
    }
}