package com.chatapp.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Data
@Document(collection = "user_status")
@NoArgsConstructor
@AllArgsConstructor
public class UserStatus {
    @Id
    private String id;
    private String userId;
    private LocalDateTime lastSeen;
    
    public UserStatus(String userId) {
        this.userId = userId;
        this.lastSeen = LocalDateTime.now();
    }
} 