package com.chatapp.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class RoomSettings {
    private String id;
    private List<String> mutedUsers = new ArrayList<>();
    private List<String> blockedUsers = new ArrayList<>();
    private List<String> admins = new ArrayList<>();
    private boolean muteNotifications;

    public RoomSettings(String adminId) {
        this.id = adminId; // Sử dụng adminId làm id
        this.admins = new ArrayList<>();
        this.admins.add(adminId);
        this.muteNotifications = false;
        this.mutedUsers = new ArrayList<>();
        this.blockedUsers = new ArrayList<>();
    }

    public List<String> getMutedUsers() {
        return mutedUsers;
    }

    public List<String> getBlockedUsers() {
        return blockedUsers;
    }

    public List<String> getAdmins() {
        return admins;
    }

    public void addAdmin(String userId) {
        if (!this.admins.contains(userId)) {
            this.admins.add(userId);
        }
    }

    public void removeAdmin(String userId) {
        this.admins.remove(userId);
    }

    public void muteUser(String userId) {
        if (!this.mutedUsers.contains(userId)) {
            this.mutedUsers.add(userId);
        }
    }

    public void unmuteUser(String userId) {
        this.mutedUsers.remove(userId);
    }

    public void blockUser(String userId) {
        if (!this.blockedUsers.contains(userId)) {
            this.blockedUsers.add(userId);
        }
    }

    public void unblockUser(String userId) {
        this.blockedUsers.remove(userId);
    }

    public void setMuteNotifications(boolean muteNotifications) {
        this.muteNotifications = muteNotifications;
    }

    public boolean isMuteNotifications() {
        return muteNotifications;
    }
}