package com.chatapp.repository;

import com.chatapp.model.RoomStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RoomStatusRepository extends MongoRepository<RoomStatus, String> {
    Optional<RoomStatus> findByRoomId(String roomId);
} 