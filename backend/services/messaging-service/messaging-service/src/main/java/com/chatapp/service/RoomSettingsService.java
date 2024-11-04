package com.chatapp.service;

import com.chatapp.model.RoomSettings;
import com.chatapp.model.RoomStatus;
import com.chatapp.repository.RoomStatusRepository;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoomSettingsService {
    private final RoomStatusRepository roomStatusRepository;

    public RoomSettings getRoomSettings(String roomId) {
        RoomStatus status = roomStatusRepository.findByRoomId(roomId)
            .orElseThrow(() -> new RuntimeException("Room status not found"));
        return status.getSettings();
    }

    public void updateRoomSettings(String roomId, RoomSettings settings) {
        RoomStatus status = roomStatusRepository.findByRoomId(roomId)
            .orElseThrow(() -> new RuntimeException("Room status not found"));
        status.setSettings(settings);
        roomStatusRepository.save(status);
    }

    public void muteRoom(String roomId, String userId) {
        RoomStatus status = roomStatusRepository.findByRoomId(roomId)
            .orElseThrow(() -> new RuntimeException("Room status not found"));
        status.getSettings().getMutedUsers().add(userId);
        roomStatusRepository.save(status);
    }

    public void unmuteRoom(String roomId, String userId) {
        RoomStatus status = roomStatusRepository.findByRoomId(roomId)
            .orElseThrow(() -> new RuntimeException("Room status not found"));
        status.getSettings().getMutedUsers().remove(userId);
        roomStatusRepository.save(status);
    }

    public void blockUser(String roomId, String userId, String targetUserId) {
        RoomStatus status = roomStatusRepository.findByRoomId(roomId)
            .orElseThrow(() -> new RuntimeException("Room status not found"));
        
        // Verify user is admin or blocking in private chat
        if (!isAdmin(status, userId) && !isPrivateChat(status)) {
            throw new RuntimeException("User is not authorized to block");
        }

        status.getSettings().getBlockedUsers().add(targetUserId);
        roomStatusRepository.save(status);
    }

    public void unblockUser(String roomId, String userId, String targetUserId) {
        RoomStatus status = roomStatusRepository.findByRoomId(roomId)
            .orElseThrow(() -> new RuntimeException("Room status not found"));
        
        // Verify user is admin or unblocking in private chat
        if (!isAdmin(status, userId) && !isPrivateChat(status)) {
            throw new RuntimeException("User is not authorized to unblock");
        }

        status.getSettings().getBlockedUsers().remove(targetUserId);
        roomStatusRepository.save(status);
    }

    public void addAdmin(String roomId, String userId, String newAdminId) {
        RoomStatus status = roomStatusRepository.findByRoomId(roomId)
            .orElseThrow(() -> new RuntimeException("Room status not found"));
        
        // Verify user is admin
        if (!isAdmin(status, userId)) {
            throw new RuntimeException("User is not admin");
        }

        status.getSettings().getAdmins().add(newAdminId);
        roomStatusRepository.save(status);
    }

    public void removeAdmin(String roomId, String userId, String adminId) {
        RoomStatus status = roomStatusRepository.findByRoomId(roomId)
            .orElseThrow(() -> new RuntimeException("Room status not found"));
        
        // Verify user is admin
        if (!isAdmin(status, userId)) {
            throw new RuntimeException("User is not admin");
        }

        status.getSettings().getAdmins().remove(adminId);
        roomStatusRepository.save(status);
    }

    private boolean isAdmin(RoomStatus status, String userId) {
        return status.getSettings().getAdmins().contains(userId);
    }

    private boolean isPrivateChat(RoomStatus status) {
        // Implement based on your chat room model
        return true; // Temporary
    }
} 