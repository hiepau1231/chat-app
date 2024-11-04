package com.chatapp.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
@Document(collection = "messages")
public class Message {
    @Id
    private String id;
    private String content;
    private String senderId;
    private String receiverId;
    private String roomId;
    private LocalDateTime createdAt;
    private Map<String, MessageStatus.Status> statusMap = new HashMap<>();

    public void updateStatus(String userId, MessageStatus.Status status) {
        statusMap.put(userId, status);
    }
}
