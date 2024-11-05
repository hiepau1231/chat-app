package com.chatapp.controller;

import com.chatapp.model.User;
import com.chatapp.model.UserProfile;
import com.chatapp.service.UserService;
import com.chatapp.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserProfileService userProfileService;
    private final Logger log = LoggerFactory.getLogger(UserController.class);

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal User user) {
        try {
            log.debug("Getting current user info");
            
            if (user == null) {
                log.error("User is null in getCurrentUser");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "User not authenticated"));
            }
            
            log.debug("User found: {}", user.getEmail());
            Map<String, Object> userProfile = userService.getUserProfile(user);
            
            // Добавляем дополнительную информацию из UserProfile
            userProfileService.getUserProfileByEmail(user.getEmail()).ifPresent(profile -> {
                userProfile.put("displayName", profile.getDisplayName());
                userProfile.put("bio", profile.getBio());
                userProfile.put("avatarUrl", profile.getAvatarUrl());
            });
            
            log.debug("User profile: {}", userProfile);
            
            return ResponseEntity.ok(userProfile);
        } catch (Exception e) {
            log.error("Error in getCurrentUser: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Internal server error"));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable UUID id) {
        return userService.getUserById(id)
                .map(user -> ResponseEntity.ok(userService.getUserProfile(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<?> getUserByEmail(@PathVariable String email) {
        return userService.getUserByEmail(email)
                .map(user -> ResponseEntity.ok(userService.getUserProfile(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/profile")
    public ResponseEntity<?> updateUserProfile(
            @AuthenticationPrincipal User user, 
            @RequestBody UserProfile profileDetails) {
        try {
            UserProfile updatedProfile = userProfileService.createOrUpdateProfile(user, profileDetails);
            return ResponseEntity.ok(updatedProfile);
        } catch (Exception e) {
            log.error("Error updating user profile: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Could not update profile"));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<Map<String, Object>>> searchUsers(
            @RequestParam String query) {
        List<Map<String, Object>> results = userProfileService.searchUsers(query);
        return ResponseEntity.ok(results);
    }

    @PostMapping("/{friendId}/add")
    public ResponseEntity<?> addFriend(
            @AuthenticationPrincipal User user,
            @PathVariable UUID friendId) {
        try {
            userProfileService.addFriend(user.getId(), friendId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error adding friend: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Could not add friend"));
        }
    }

    @DeleteMapping("/{friendId}/remove")
    public ResponseEntity<?> removeFriend(
            @AuthenticationPrincipal User user,
            @PathVariable UUID friendId) {
        try {
            userProfileService.removeFriend(user.getId(), friendId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error removing friend: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Could not remove friend"));
        }
    }

    @GetMapping("/friends")
    public ResponseEntity<List<Map<String, Object>>> getUserFriends(
            @AuthenticationPrincipal User user) {
        try {
            List<Map<String, Object>> friends = userProfileService.getUserFriends(user.getId());
            return ResponseEntity.ok(friends);
        } catch (Exception e) {
            log.error("Error getting friends: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
        }
    }
}