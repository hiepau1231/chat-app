package com.chatapp.service;

import com.chatapp.model.User;
import com.chatapp.model.UserProfile;
import com.chatapp.repository.UserProfileRepository;
import com.chatapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public List<Map<String, Object>> searchUsers(String searchTerm) {
        List<UserProfile> profiles = userProfileRepository.searchProfiles(searchTerm);
        
        return profiles.stream()
            .map(profile -> Map.of(
                "id", profile.getUser().getId(),
                "username", profile.getUser().getUsername(),
                "displayName", profile.getDisplayName(),
                "avatarUrl", profile.getAvatarUrl(),
                "bio", profile.getBio()
            ))
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
                
                return Map.of(
                    "id", friend.getId(),
                    "username", friend.getUsername(),
                    "displayName", friendProfile.getDisplayName(),
                    "avatarUrl", friendProfile.getAvatarUrl()
                );
            })
            .collect(Collectors.toList());
    }
}