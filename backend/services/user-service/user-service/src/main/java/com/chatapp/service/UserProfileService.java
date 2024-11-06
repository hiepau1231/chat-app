package com.chatapp.service;

import com.chatapp.model.User;
import com.chatapp.model.UserProfile;
import com.chatapp.repository.UserProfileRepository;
import com.chatapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserProfileService {
    private final UserProfileRepository userProfileRepository;
    private final UserRepository userRepository;

    @Transactional
    public UserProfile createOrUpdateProfile(User user, UserProfile profileDetails) {
        log.debug("Creating/Updating profile for user: {}", user.getEmail());
        
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

        UserProfile savedProfile = userProfileRepository.save(profile);
        log.info("Successfully saved profile for user: {}", user.getEmail());
        return savedProfile;
    }

    public Optional<UserProfile> getUserProfile(Long userId) {
        log.debug("Getting profile for user id: {}", userId);
        return userProfileRepository.findByUser_Id(userId);
    }

    public Optional<UserProfile> getUserProfileByEmail(String email) {
        log.debug("Getting profile for user email: {}", email);
        return userProfileRepository.findByUser_Email(email);
    }

    @Transactional
    public void addFriend(Long userId, Long friendId) {
        log.debug("Adding friend {} for user {}", friendId, userId);
        
        UserProfile userProfile = userProfileRepository.findByUser_Id(userId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin người dùng"));
        
        userProfile.addFriend(friendId);
        userProfileRepository.save(userProfile);
        log.info("Successfully added friend {} for user {}", friendId, userId);
    }

    @Transactional
    public void removeFriend(Long userId, Long friendId) {
        log.debug("Removing friend {} for user {}", friendId, userId);
        
        UserProfile userProfile = userProfileRepository.findByUser_Id(userId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin người dùng"));
        
        userProfile.removeFriend(friendId);
        userProfileRepository.save(userProfile);
        log.info("Successfully removed friend {} for user {}", friendId, userId);
    }

    private Map<String, Object> createUserMap(User user, UserProfile profile) {
        Map<String, Object> userMap = new HashMap<>();
        
        if (user != null) {
            userMap.put("id", user.getId());
            userMap.put("username", user.getUsername() != null ? user.getUsername() : "");
        } else {
            userMap.put("id", null);
            userMap.put("username", "");
        }
        
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
        log.debug("Searching users with term: {}", searchTerm);
        List<UserProfile> profiles = userProfileRepository.searchProfiles(searchTerm);
        
        return profiles.stream()
            .filter(profile -> profile != null && profile.getUser() != null)
            .map(profile -> createUserMap(profile.getUser(), profile))
            .collect(Collectors.toList());
    }

    public List<Map<String, Object>> getUserFriends(Long userId) {
        log.debug("Getting friends for user: {}", userId);
        
        UserProfile userProfile = userProfileRepository.findByUser_Id(userId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin người dùng"));
        
        return userProfile.getFriends().stream()
            .map(friendId -> {
                User friend = userRepository.findById(friendId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin bạn bè"));
                UserProfile friendProfile = userProfileRepository.findByUser_Id(friendId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin bạn bè"));
                
                return createUserMap(friend, friendProfile);
            })
            .collect(Collectors.toList());
    }
}