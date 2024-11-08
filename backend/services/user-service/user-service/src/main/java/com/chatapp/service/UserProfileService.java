package com.chatapp.service;

import com.chatapp.exception.ResourceNotFoundException;
import com.chatapp.model.User;
import com.chatapp.model.User.UserProfile;
import com.chatapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserProfileService {
    private final UserRepository userRepository;

    public UserProfile getUserProfile(String userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        return user.getProfile();
    }

    public UserProfile getUserProfileByEmail(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        return user.getProfile();
    }

    public UserProfile createOrUpdateProfile(User user, UserProfile profileDetails) {
        user.setProfile(profileDetails);
        User savedUser = userRepository.save(user);
        return savedUser.getProfile();
    }

    public List<User> searchUsers(String keyword) {
        List<User> users = new ArrayList<>();
        users.addAll(userRepository.findByUsernameContainingIgnoreCase(keyword));
        users.addAll(userRepository.findByEmailContainingIgnoreCase(keyword));
        return users.stream().distinct().toList();
    }

    @Transactional
    public void addFriend(String userId, String friendId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        User friend = userRepository.findById(friendId)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", friendId));

        Set<String> friends = new HashSet<>(user.getProfile().getFriends());
        friends.add(friendId);
        user.getProfile().setFriends(new ArrayList<>(friends));

        Set<String> friendsFriends = new HashSet<>(friend.getProfile().getFriends());
        friendsFriends.add(userId);
        friend.getProfile().setFriends(new ArrayList<>(friendsFriends));

        userRepository.save(user);
        userRepository.save(friend);
    }

    @Transactional
    public void removeFriend(String userId, String friendId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        User friend = userRepository.findById(friendId)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", friendId));

        user.getProfile().getFriends().remove(friendId);
        friend.getProfile().getFriends().remove(userId);

        userRepository.save(user);
        userRepository.save(friend);
    }

    public List<User> getUserFriends(String userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        List<String> friendIds = user.getProfile().getFriends();
        return userRepository.findAllById(friendIds);
    }

    public List<User> getEmailNotificationEnabledUsers() {
        return userRepository.findByProfileSettingsEmailNotificationsTrue();
    }

    public List<User> getPushNotificationEnabledUsers() {
        return userRepository.findByProfileSettingsPushNotificationsTrue();
    }
}