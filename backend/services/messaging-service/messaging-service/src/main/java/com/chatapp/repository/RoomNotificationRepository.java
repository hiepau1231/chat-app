package com.chatapp.repository;

import com.chatapp.model.RoomNotification;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RoomNotificationRepository extends MongoRepository<RoomNotification, String> {
    List<RoomNotification> findByUserIdAndReadFalseOrderByCreatedAtDesc(String userId);
    List<RoomNotification> findByUserIdAndReadFalse(String userId);
} 