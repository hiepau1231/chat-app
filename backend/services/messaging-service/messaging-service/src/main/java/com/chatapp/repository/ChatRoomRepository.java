package com.chatapp.repository;

import com.chatapp.model.ChatRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRoomRepository extends MongoRepository<ChatRoom, String> {
    Page<ChatRoom> findByMembersContainingOrderByUpdatedAtDesc(String userId, Pageable pageable);
    
    @Query("{ 'members': ?0, 'name': { $regex: ?1, $options: 'i' } }")
    Page<ChatRoom> searchRooms(String userId, String searchTerm, Pageable pageable);
    
    @Query(value = "{ 'members': ?0 }", count = true)
    long countByMember(String userId);
}