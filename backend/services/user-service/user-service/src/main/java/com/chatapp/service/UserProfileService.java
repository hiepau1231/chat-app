package com.chatapp.service;

import com.chatapp.model.User;
import com.chatapp.model.UserProfile;
import com.chatapp.repository.UserProfileRepository;
import com.chatapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserProfileService {
    private final UserProfileRepository userProfileRepository;
    private final UserRepository userRepository;

    @Transactional
    public UserProfile createOrUpdateProfile(User user, UserProfile profileDetails) {
        UserProfile profile = userProfileRepository.findByUser_Id(user.getId())
            .orElse(new UserProfile());
        
        profile.setUser(user);
        profile.setDisplayName(profileDetails.getDisplayName());
        profile.setBio(profileDetails.getBio());
        profile.setLocation(profileDetails.getLocation());
        profile.setBirthDate(profileDetails.getBirthDate());
        profile.setAvatarUrl(profileDetails.getAvatarUrl());
        profile.setInterests(profileDetails.getInterests());
        profile.setProfilePrivacy(profileDetails.getProfilePrivacy());

        return userProfileRepository.save(profile);
    }

    public Optional<UserProfile> getUserProfile(UUID userId) {
        return userProfileRepository.findByUser_Id(userId);
    }

    public Optional<UserProfile> getUserProfileByEmail(String email) {
        return userProfileRepository.findByUser_Email(email);
    }

    @Transactional
    public void addFriend(UUID userId, UUID friendId) {
        UserProfile userProfile = userProfileRepository.findByUser_Id(userId)
            .orElseThrow(() -> new RuntimeException("User profile not found"));
        
        userProfile.addFriend(friendId);
        userProfileRepository.save(userProfile);
    }

    @Transactional
    public void removeFriend(UUID userId, UUID friendId) {
        UserProfile userProfile = userProfileRepository.findByUser_Id(userId)
            .orElseThrow(() -> new RuntimeException("User profile not found"));
        
        userProfile.removeFriend(friendId);
        userProfileRepository.save(userProfile);
    }

    private Map<String, Object> createUserMap(User user, UserProfile profile) {
        Map<String, Object> userMap = new HashMap<>();
        
        // Safely add user details with null checks and default values
        if (user != null) {
            userMap.put("id", user.getId() != null ? user.getId().toString() : null);
            userMap.put("username", user.getUsername() != null ? user.getUsername() : "");
        } else {
            userMap.put("id", null);
            userMap.put("username", "");
        }
        
        // Safely add profile details with null checks and default values
        if (profile != null) {
            userMap.put("displayName", profile.getDisplayName() != null ? profile.getDisplayName() : "");
            userMap.put("avatarUrl", profile.getAvatarUrl() != null ? profile.getAvatarUrl() : "");
            userMap.put("bio", profile.getBio() != null ? profile.getBio() : "");
        } else {
            userMap.put("displayName", "");
            userMap.put("avatarUrl", "");
            userMap.put("bio", "");
        }
        
        return userMap;
    }

    public List<Map<String, Object>> searchUsers(String searchTerm) {
        List<UserProfile> profiles = userProfileRepository.searchProfiles(searchTerm);
        
        return profiles.stream()
            .filter(profile -> profile != null && profile.getUser() != null)
            .map(profile -> createUserMap(profile.getUser(), profile))
            .collect(Collectors.toList());
    }

    public List<Map<String, Object>> getUserFriends(UUID userId) {
        UserProfile userProfile = userProfileRepository.findByUser_Id(userId)
            .orElseThrow(() -> new RuntimeException("User profile not found"));
        
        return userProfile.getFriends().stream()
            .map(friendId -> {
                User friend = userRepository.findById(friendId)
                    .orElseThrow(() -> new RuntimeException("Friend not found"));
                UserProfile friendProfile = userProfileRepository.findByUser_Id(friendId)
                    .orElseThrow(() -> new RuntimeException("Friend profile not found"));
                
                return createUserMap(friend, friendProfile);
            })
            .collect(Collectors.toList());
    }
}