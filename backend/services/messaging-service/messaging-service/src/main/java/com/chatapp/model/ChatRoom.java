package com.chatapp.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Document(collection = "chat_rooms")
public class ChatRoom {
    @Id
    private String id;
    private String name;
    private boolean isPrivate;
    private Set<String> members = new HashSet<>();
    private String ownerId;
    private String type = "group"; // direct, group
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ChatRoom() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}