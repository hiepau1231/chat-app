package com.chatapp.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.List;
import java.util.ArrayList;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "user_profiles")
public class UserProfile {
    @Id
    private String id;
    
    private String avatar;
    private String bio;
    
    @Builder.Default
    private String theme = "light";
    
    @Builder.Default
    private String language = "en";
    
    @Builder.Default
    private boolean emailNotifications = true;
    
    @Builder.Default
    private boolean pushNotifications = true;
    
    @Builder.Default
    private List<String> interests = new ArrayList<>();
    
    @Builder.Default
    private List<String> skills = new ArrayList<>();
    
    @Builder.Default
    private UserStatus status = UserStatus.ACTIVE;
    
    public enum UserStatus {
        ACTIVE, INACTIVE, BANNED
    }
}