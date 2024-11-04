package com.chatapp.repository;

import com.chatapp.model.UserStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserStatusRepository extends MongoRepository<UserStatus, String> {
    Optional<UserStatus> findByUserId(String userId);
} 