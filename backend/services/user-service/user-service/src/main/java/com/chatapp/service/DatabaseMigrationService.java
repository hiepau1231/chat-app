package com.chatapp.service;

import com.chatapp.model.User;
import com.chatapp.model.Role;
import com.chatapp.model.UserStatus;
import com.chatapp.model.User.UserProfile;
import com.chatapp.model.User.UserSettings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class DatabaseMigrationService {
    
    private final MongoTemplate mongoTemplate;
    
    public void initializeSampleData() {
        log.info("Initializing sample data in MongoDB...");
        
        try {
            List<User> users = new ArrayList<>();
            
            // Admin user
            UserSettings adminSettings = UserSettings.builder()
                .emailNotifications(true)
                .pushNotifications(true)
                .theme("dark")
                .language("en")
                .build();
            
            UserProfile adminProfile = new UserProfile(
                "https://ui-avatars.com/api/?name=Admin&background=random",
                "System Administrator",
                new ArrayList<>(),
                adminSettings
            );
            
            User admin = User.builder()
                .username("admin")
                .email("admin@chatapp.com")
                .password("$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG") // password: 123456
                .role(Role.ADMIN)
                .createdAt(LocalDateTime.now())
                .lastLogin(LocalDateTime.now())
                .status(UserStatus.ONLINE)
                .profile(adminProfile)
                .build();
            users.add(admin);
            
            // Regular users
            for (int i = 1; i <= 5; i++) {
                UserSettings userSettings = UserSettings.builder()
                    .emailNotifications(true)
                    .pushNotifications(true)
                    .theme("light")
                    .language("en")
                    .build();
                
                UserProfile userProfile = new UserProfile(
                    "https://ui-avatars.com/api/?name=User" + i + "&background=random",
                    "Regular User " + i,
                    new ArrayList<>(),
                    userSettings
                );
                
                User user = User.builder()
                    .username("user" + i)
                    .email("user" + i + "@chatapp.com")
                    .password("$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG") // password: 123456
                    .role(Role.USER)
                    .createdAt(LocalDateTime.now())
                    .lastLogin(LocalDateTime.now())
                    .status(UserStatus.OFFLINE)
                    .profile(userProfile)
                    .build();
                users.add(user);
            }
            
            mongoTemplate.insertAll(users);
            
            log.info("Successfully initialized {} sample users", users.size());
            
        } catch (Exception e) {
            log.error("Error initializing sample data", e);
            throw new RuntimeException("Failed to initialize sample data", e);
        }
    }
}