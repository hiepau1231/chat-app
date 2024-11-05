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
    private Status status;

    public enum Status {
        ONLINE,
        OFFLINE,
        AWAY,
        DO_NOT_DISTURB
    }
    
    public UserStatus(String userId) {
        this.userId = userId;
        this.lastSeen = LocalDateTime.now();
        this.status = Status.ONLINE;
    }

    public void updateStatus(Status newStatus) {
        this.status = newStatus;
        this.lastSeen = LocalDateTime.now();
    }

    public void updateLastSeen() {
        this.lastSeen = LocalDateTime.now();
    }
}