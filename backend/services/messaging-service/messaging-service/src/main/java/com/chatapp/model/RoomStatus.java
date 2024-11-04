package com.chatapp.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Document(collection = "room_status")
@NoArgsConstructor
@AllArgsConstructor
public class RoomStatus {
    private String roomId;
    private List<String> onlineUsers;
    private List<String> typingUsers;
    private RoomSettings settings;
    private LocalDateTime lastActivity;
} 