package com.chatapp.controller;

import com.chatapp.model.User;
import com.chatapp.model.User.UserProfile;
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
import java.util.stream.Collectors;

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
                    .body(Map.of("error", "Người dùng chưa đăng nhập"));
            }
            
            log.debug("User found: {}", user.getEmail());
            Map<String, Object> userProfile = userService.getUserProfile(user);
            
            UserProfile profile = user.getProfile();
            if (profile != null) {
                userProfile.put("bio", profile.getBio());
                userProfile.put("avatar", profile.getAvatar());
                userProfile.put("friends", profile.getFriends());
                userProfile.put("settings", profile.getSettings());
            }
            
            log.debug("User profile: {}", userProfile);
            
            return ResponseEntity.ok(userProfile);
        } catch (Exception e) {
            log.error("Error in getCurrentUser: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Lỗi hệ thống"));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable String id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/{id}/profile")
    public ResponseEntity<UserProfile> getUserProfile(@PathVariable String id) {
        return ResponseEntity.ok(userProfileService.getUserProfile(id));
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
                .body(Map.of("error", "Không thể cập nhật thông tin người dùng"));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<Map<String, Object>>> searchUsers(
            @RequestParam String query) {
        List<User> users = userProfileService.searchUsers(query);
        List<Map<String, Object>> results = users.stream()
            .map(user -> Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "email", user.getEmail(),
                "profile", user.getProfile()
            ))
            .collect(Collectors.toList());
        return ResponseEntity.ok(results);
    }

    @PostMapping("/{friendId}/add")
    public ResponseEntity<?> addFriend(
            @AuthenticationPrincipal User user,
            @PathVariable String friendId) {
        try {
            userProfileService.addFriend(user.getId(), friendId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error adding friend: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Không thể thêm bạn"));
        }
    }

    @DeleteMapping("/{friendId}/remove")
    public ResponseEntity<?> removeFriend(
            @AuthenticationPrincipal User user,
            @PathVariable String friendId) {
        try {
            userProfileService.removeFriend(user.getId(), friendId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error removing friend: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Không thể xóa bạn"));
        }
    }

    @GetMapping("/friends")
    public ResponseEntity<List<Map<String, Object>>> getUserFriends(
            @AuthenticationPrincipal User user) {
        try {
            List<User> friends = userProfileService.getUserFriends(user.getId());
            List<Map<String, Object>> friendsData = friends.stream()
                .map(friend -> Map.of(
                    "id", friend.getId(),
                    "username", friend.getUsername(),
                    "email", friend.getEmail(),
                    "profile", friend.getProfile()
                ))
                .collect(Collectors.toList());
            return ResponseEntity.ok(friendsData);
        } catch (Exception e) {
            log.error("Error getting friends: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
        }
    }
}