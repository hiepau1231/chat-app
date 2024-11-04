package com.chatapp.repository;

import com.chatapp.model.Message;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends MongoRepository<Message, String> {
    List<Message> findByRoomIdOrderByCreatedAtDesc(String roomId);
    @Query("{ $or: [ { 'senderId': ?0 }, { 'receiverId': ?0 } ] }")
    List<Message> findBySenderIdOrReceiverIdOrderByCreatedAtDesc(String userId, String userId2);
    long countByRoomId(String roomId);
    long countByRoomIdAndCreatedAtAfter(String roomId, LocalDateTime timestamp);
    Optional<Message> findFirstByRoomIdOrderByCreatedAtDesc(String roomId);
    @Query("{ 'roomId': ?0, 'createdAt': { $gt: ?1 } }")
    List<Message> findByRoomIdAndCreatedAtAfter(String roomId, LocalDateTime timestamp);
    @Query("{'roomId': ?0}")
    List<Message> findByRoomId(String roomId);
}