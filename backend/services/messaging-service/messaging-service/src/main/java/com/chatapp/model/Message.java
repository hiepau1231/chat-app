package com.chatapp.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Data
@Document(collection = "messages")
public class Message {
    @Id
    private String id;
    private String content;
    private String type = "text"; // text, image, file
    private String fileUrl;
    private String fileName;
    private String senderId;
    private String receiverId;
    private String roomId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Message() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}