package com.chatapp.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Document(collection = "chatrooms")
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoom {
    @Id
    private String id;
    private String name;
    private List<String> members;
    private Message lastMessage;
    private RoomSettings settings;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}