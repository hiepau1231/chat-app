package com.chatapp.dto;

import com.chatapp.model.User;
import com.chatapp.model.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private String id;
    private String username;
    private String email;
    private UserStatus status;
    private LocalDateTime lastLogin;
    private User.UserProfile profile;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserProfileResponse {
        private String avatar;
        private String bio;
        private UserSettingsResponse settings;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserSettingsResponse {
        private boolean emailNotifications;
        private boolean pushNotifications;
        private String theme;
        private String language;
    }
}