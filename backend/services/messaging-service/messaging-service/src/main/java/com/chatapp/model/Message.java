package com.chatapp.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Document(collection = "messages")
public class Message {
    @Id
    private String id;
    private String content;
    private String senderId;
    private String roomId;
    private LocalDateTime timestamp;
    private LocalDateTime createdAt;
    private MessageStatus status;

    public Message(String content, String senderId, String roomId) {
        this.content = content;
        this.senderId = senderId;
        this.roomId = roomId;
        this.timestamp = LocalDateTime.now();
        this.createdAt = LocalDateTime.now();
        this.status = MessageStatus.SENT;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public MessageStatus getStatus() {
        return status;
    }

    public void setStatus(MessageStatus status) {
        this.status = status;
    }

    public void updateStatus(MessageStatus newStatus) {
        this.status = newStatus;
    }

    public void updateStatus(String userId, MessageStatus.Status status) {
        // Implement status update logic for specific user
    }
}