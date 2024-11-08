package com.chatapp.repository;

import com.chatapp.model.UserProfile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserProfileRepository extends MongoRepository<UserProfile, String> {
    
    @Query("{ 'emailNotifications': true }")
    List<UserProfile> findAllWithEmailNotifications();
    
    @Query("{ 'pushNotifications': true }")
    List<UserProfile> findAllWithPushNotifications();
}