package com.chatapp.repository;

import com.chatapp.model.Message;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface MessageRepository extends MongoRepository<Message, String> {
    List<Message> findByRoomIdOrderByCreatedAtDesc(String roomId);
    List<Message> findBySenderIdOrReceiverIdOrderByCreatedAtDesc(String senderId, String receiverId);
}