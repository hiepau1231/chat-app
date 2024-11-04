package com.chatapp.dto;

import com.chatapp.model.Message;
import com.chatapp.model.RoomSettings;
import com.chatapp.model.ChatRoom;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomDTO {
    private String id;
    private String name;
    private List<String> members;
    private Message lastMessage;
    private RoomSettings settings;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ChatRoomDTO fromChatRoom(ChatRoom room) {
        ChatRoomDTO dto = new ChatRoomDTO();
        dto.setId(room.getId());
        dto.setName(room.getName());
        dto.setMembers(room.getMembers());
        dto.setLastMessage(room.getLastMessage());
        dto.setSettings(room.getSettings());
        dto.setCreatedAt(room.getCreatedAt());
        dto.setUpdatedAt(room.getUpdatedAt());
        return dto;
    }
} 