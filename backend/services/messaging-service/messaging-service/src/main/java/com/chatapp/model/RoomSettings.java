package com.chatapp.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Data
@Document(collection = "room_settings")
@NoArgsConstructor
@AllArgsConstructor
public class RoomSettings {
    private boolean muteNotifications;
    private List<String> blockedUsers;
    private List<String> admins;
    private List<String> mutedUsers;
} 