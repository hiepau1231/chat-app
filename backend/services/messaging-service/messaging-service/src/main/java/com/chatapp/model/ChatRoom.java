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
@Document(collection = "chat_rooms")
public class ChatRoom {
    @Id
    private String id;
    private String name;
    private List<String> members = new ArrayList<>();
    private Message lastMessage;
    private RoomSettings settings;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ChatRoom(String name, List<String> members) {
        this.name = name;
        this.members = members;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (!members.isEmpty()) {
            this.settings = new RoomSettings(members.get(0));
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }

    public Message getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(Message lastMessage) {
        this.lastMessage = lastMessage;
        this.updatedAt = LocalDateTime.now();
    }

    public RoomSettings getSettings() {
        return settings;
    }

    public void setSettings(RoomSettings settings) {
        this.settings = settings;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void addMember(String userId) {
        if (!this.members.contains(userId)) {
            this.members.add(userId);
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void removeMember(String userId) {
        this.members.remove(userId);
        this.updatedAt = LocalDateTime.now();
    }
}