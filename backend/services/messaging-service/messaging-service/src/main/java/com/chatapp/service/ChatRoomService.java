package com.chatapp.service;

import com.chatapp.event.MessageSentEvent;
import com.chatapp.model.ChatRoom;
import com.chatapp.model.Message;
import com.chatapp.model.LastRead;
import com.chatapp.model.RoomSettings;
import com.chatapp.model.MessageStatus;
import com.chatapp.repository.ChatRoomRepository;
import com.chatapp.repository.MessageRepository;
import com.chatapp.repository.LastReadRepository;
import com.chatapp.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final MessageRepository messageRepository;
    private final LastReadRepository lastReadRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @EventListener
    public void handleMessageSent(MessageSentEvent event) {
        Message message = event.getMessage();
        updateLastMessage(message.getRoomId(), message);
    }

    public Page<ChatRoom> getRoomsForUser(String userId, Pageable pageable) {
        return chatRoomRepository.findByMembersContainingOrderByUpdatedAtDesc(userId, pageable);
    }

    public Page<ChatRoom> searchRooms(String userId, String searchTerm, Pageable pageable) {
        return chatRoomRepository.searchRooms(userId, searchTerm, pageable);
    }

    public ChatRoom getRoom(String roomId) {
        return chatRoomRepository.findById(roomId)
            .orElseThrow(() -> new RuntimeException("Room not found"));
    }

    public ChatRoom createRoom(ChatRoom room) {
        room.setCreatedAt(LocalDateTime.now());
        room.setUpdatedAt(LocalDateTime.now());
        return chatRoomRepository.save(room);
    }

    public void updateLastMessage(String roomId, Message message) {
        ChatRoom room = chatRoomRepository.findById(roomId)
            .orElseThrow(() -> new RuntimeException("Room not found"));
        room.setLastMessage(message);
        room.setUpdatedAt(LocalDateTime.now());
        chatRoomRepository.save(room);
    }

    public long getUnreadCount(String roomId, String userId) {
        LastRead lastRead = lastReadRepository
            .findByUserIdAndRoomId(userId, roomId)
            .orElse(null);

        if (lastRead == null) {
            return messageRepository.countByRoomId(roomId);
        }

        return messageRepository.countByRoomIdAndCreatedAtAfter(
            roomId, 
            lastRead.getTimestamp()
        );
    }

    public void markAsRead(String roomId, String userId, String messageId) {
        LastRead lastRead = lastReadRepository
            .findByUserIdAndRoomId(userId, roomId)
            .orElse(new LastRead());

        lastRead.setUserId(userId);
        lastRead.setRoomId(roomId);
        lastRead.setLastMessageId(messageId);
        lastRead.setTimestamp(LocalDateTime.now());

        lastReadRepository.save(lastRead);

        // Notify other users about read status
        Message message = messageRepository.findById(messageId)
            .orElseThrow(() -> new ResourceNotFoundException("Message not found"));
        
        message.updateStatus(userId, com.chatapp.model.MessageStatus.Status.READ);
        messageRepository.save(message);
    }

    public void markRoomAsRead(String roomId, String userId) {
        // Get latest message in room
        Message latestMessage = messageRepository
            .findFirstByRoomIdOrderByCreatedAtDesc(roomId)
            .orElse(null);

        if (latestMessage != null) {
            LastRead lastRead = lastReadRepository
                .findByUserIdAndRoomId(userId, roomId)
                .orElse(new LastRead());

            lastRead.setUserId(userId);
            lastRead.setRoomId(roomId);
            lastRead.setLastMessageId(latestMessage.getId());
            lastRead.setTimestamp(LocalDateTime.now());

            lastReadRepository.save(lastRead);

            // Notify room about read status
            messagingTemplate.convertAndSend(
                "/topic/room/" + roomId + "/read",
                Map.of("userId", userId, "timestamp", lastRead.getTimestamp())
            );
        }
    }

    public void updateRoomActivity(String roomId) {
        ChatRoom room = getRoom(roomId);
        room.setUpdatedAt(LocalDateTime.now());
        chatRoomRepository.save(room);
    }

    public ResponseEntity<?> updateRoomSettings(String roomId, RoomSettings settings) {
        ChatRoom room = chatRoomRepository.findById(roomId)
            .orElseThrow(() -> new ResourceNotFoundException("Room not found"));
        
        room.setSettings(settings);
        chatRoomRepository.save(room);
        
        // Broadcast settings update to room members
        messagingTemplate.convertAndSend("/topic/room/" + roomId + "/settings", settings);
        
        return ResponseEntity.ok(settings);
    }

    public ResponseEntity<?> toggleMuteNotifications(String roomId, boolean muted) {
        ChatRoom room = chatRoomRepository.findById(roomId)
            .orElseThrow(() -> new ResourceNotFoundException("Room not found"));
        
        RoomSettings settings = room.getSettings();
        settings.setMuteNotifications(muted);
        
        room.setSettings(settings);
        chatRoomRepository.save(room);
        
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<?> toggleBlockUser(String roomId, String userId) {
        ChatRoom room = chatRoomRepository.findById(roomId)
            .orElseThrow(() -> new ResourceNotFoundException("Room not found"));
        
        RoomSettings settings = room.getSettings();
        List<String> blockedUsers = settings.getBlockedUsers();
        
        if (blockedUsers.contains(userId)) {
            blockedUsers.remove(userId);
        } else {
            blockedUsers.add(userId);
        }
        
        chatRoomRepository.save(room);
        
        // Notify room members about blocked user update
        messagingTemplate.convertAndSend("/topic/room/" + roomId + "/blocked", blockedUsers);
        
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<?> toggleAdminPrivilege(String roomId, String userId) {
        ChatRoom room = chatRoomRepository.findById(roomId)
            .orElseThrow(() -> new ResourceNotFoundException("Room not found"));
        
        RoomSettings settings = room.getSettings();
        List<String> admins = settings.getAdmins();
        
        if (admins.contains(userId)) {
            admins.remove(userId);
        } else {
            admins.add(userId);
        }
        
        chatRoomRepository.save(room);
        
        // Notify room members about admin changes
        messagingTemplate.convertAndSend("/topic/room/" + roomId + "/admins", admins);
        
        return ResponseEntity.ok().build();
    }

    public Long getMessageCount(String roomId) {
        return messageRepository.countByRoomId(roomId);
    }

    public Long getNewMessageCount(String roomId, LocalDateTime since) {
        return messageRepository.countByRoomIdAndTimestampAfter(roomId, since);
    }

    public void markMessagesAsRead(String roomId, String userId) {
        List<Message> unreadMessages = messageRepository.findByRoomIdOrderByTimestampDesc(roomId);
        unreadMessages.forEach(message -> {
            if (!message.getSenderId().equals(userId)) {
                message.updateStatus(MessageStatus.READ);
                messageRepository.save(message);
            }
        });
    }

    public Message getLastMessage(String roomId) {
        return messageRepository.findFirstByRoomIdOrderByTimestampDesc(roomId)
            .orElse(null);
    }
}