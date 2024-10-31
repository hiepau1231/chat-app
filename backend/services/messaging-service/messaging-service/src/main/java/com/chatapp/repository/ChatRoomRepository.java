package com.chatapp.repository;

import com.chatapp.model.ChatRoom;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface ChatRoomRepository extends MongoRepository<ChatRoom, String> {
    List<ChatRoom> findByMembersContaining(String userId);
    List<ChatRoom> findByIsPrivateAndMembersContaining(boolean isPrivate, String userId);
}