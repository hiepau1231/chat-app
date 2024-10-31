package com.chatapp.service;

import com.chatapp.model.ChatRoom;
import com.chatapp.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;

    public ChatRoom createRoom(ChatRoom room) {
        return chatRoomRepository.save(room);
    }

    public ChatRoom getRoom(String id) {
        return chatRoomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Chat room not found"));
    }

    public List<ChatRoom> getRoomsByUser(String userId) {
        return chatRoomRepository.findByMembersContaining(userId);
    }

    public ChatRoom addMember(String roomId, String userId) {
        ChatRoom room = getRoom(roomId);
        room.getMembers().add(userId);
        return chatRoomRepository.save(room);
    }

    public ChatRoom removeMember(String roomId, String userId) {
        ChatRoom room = getRoom(roomId);
        room.getMembers().remove(userId);
        return chatRoomRepository.save(room);
    }

    public void deleteRoom(String id) {
        chatRoomRepository.deleteById(id);
    }

    public ChatRoom updateRoom(String id, ChatRoom roomDetails) {
        ChatRoom room = getRoom(id);
        room.setName(roomDetails.getName());
        room.setPrivate(roomDetails.isPrivate());
        if (roomDetails.getMembers() != null && !roomDetails.getMembers().isEmpty()) {
            room.setMembers(roomDetails.getMembers());
        }
        return chatRoomRepository.save(room);
    }
}