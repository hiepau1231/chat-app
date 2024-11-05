package com.chatapp.repository;

import com.chatapp.model.Message;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends MongoRepository<Message, String> {
    List<Message> findByRoomIdOrderByTimestampDesc(String roomId);
    Long countByRoomId(String roomId);
    Long countByRoomIdAndTimestampAfter(String roomId, LocalDateTime timestamp);
    Optional<Message> findFirstByRoomIdOrderByTimestampDesc(String roomId);
    Long countByRoomIdAndCreatedAtAfter(String roomId, LocalDateTime timestamp);
    Optional<Message> findFirstByRoomIdOrderByCreatedAtDesc(String roomId);
}