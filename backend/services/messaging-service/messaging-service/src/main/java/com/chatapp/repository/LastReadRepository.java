package com.chatapp.repository;

import com.chatapp.model.LastRead;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface LastReadRepository extends MongoRepository<LastRead, String> {
    Optional<LastRead> findByUserIdAndRoomId(String userId, String roomId);
} 