package com.chatapp.service;

import com.chatapp.model.RoomStatus;
import com.chatapp.model.RoomSettings;
import com.chatapp.repository.RoomStatusRepository;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoomStatusService {
    private final RoomStatusRepository roomStatusRepository;

    public RoomStatus getRoomStatus(String roomId) {
        return roomStatusRepository.findByRoomId(roomId)
            .orElseGet(() -> createNewRoomStatus(roomId));
    }

    public void updateOnlineStatus(String roomId, String userId, boolean isOnline) {
        RoomStatus status = getRoomStatus(roomId);
        List<String> onlineUsers = status.getOnlineUsers();
        
        if (isOnline && !onlineUsers.contains(userId)) {
            onlineUsers.add(userId);
        } else if (!isOnline) {
            onlineUsers.remove(userId);
        }
        
        status.setLastActivity(LocalDateTime.now());
        roomStatusRepository.save(status);
    }

    public void updateTypingStatus(String roomId, String userId, boolean isTyping) {
        RoomStatus status = getRoomStatus(roomId);
        List<String> typingUsers = status.getTypingUsers();
        
        if (isTyping && !typingUsers.contains(userId)) {
            typingUsers.add(userId);
        } else if (!isTyping) {
            typingUsers.remove(userId);
        }
        
        status.setLastActivity(LocalDateTime.now());
        roomStatusRepository.save(status);
    }

    private RoomStatus createNewRoomStatus(String roomId) {
        RoomStatus status = new RoomStatus();
        status.setRoomId(roomId);
        status.setOnlineUsers(new ArrayList<>());
        status.setTypingUsers(new ArrayList<>());
        status.setSettings(new RoomSettings());
        status.setLastActivity(LocalDateTime.now());
        return roomStatusRepository.save(status);
    }

    public List<String> getOnlineUsers(String roomId) {
        return getRoomStatus(roomId).getOnlineUsers();
    }

    public List<String> getTypingUsers(String roomId) {
        return getRoomStatus(roomId).getTypingUsers();
    }

    public RoomSettings getSettings(String roomId) {
        return getRoomStatus(roomId).getSettings();
    }
} 