package com.chatapp.repository;

import com.chatapp.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
    
    @Query("{ 'status': 'ONLINE' }")
    List<User> findAllOnlineUsers();
    
    @Query("{ 'username': { $regex: ?0, $options: 'i' }}")
    List<User> findByUsernameContainingIgnoreCase(String username);
    
    @Query("{ 'email': { $regex: ?0, $options: 'i' }}")
    List<User> findByEmailContainingIgnoreCase(String email);
    
    @Query(value = "{ 'lastLogin': { $gte: ?0 } }")
    List<User> findActiveUsersSince(LocalDateTime date);
    
    @Query("{ 'profile.settings.emailNotifications': true }")
    List<User> findByProfileSettingsEmailNotificationsTrue();
    
    @Query("{ 'profile.settings.pushNotifications': true }")
    List<User> findByProfileSettingsPushNotificationsTrue();
    
    @Query("{ 'status': { $in: ['ONLINE', 'AWAY'] }}")
    List<User> findAllActiveUsers();

    @Query("{ 'profile.friends': ?0 }")
    List<User> findByProfileFriendsContaining(String userId);
}