package com.chatapp.controller;

import com.chatapp.model.ChatRoom;
import com.chatapp.dto.ChatRoomDTO;
import com.chatapp.service.ChatRoomService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class ChatRoomController {
    private final ChatRoomService chatRoomService;

    @GetMapping
    public Page<ChatRoomDTO> getRooms(
            @RequestParam String userId,
            Pageable pageable) {
        return chatRoomService.getRoomsForUser(userId, pageable)
                .map(ChatRoomDTO::fromChatRoom);
    }

    @GetMapping("/search")
    public Page<ChatRoomDTO> searchRooms(
            @RequestParam String userId,
            @RequestParam String searchTerm,
            Pageable pageable) {
        return chatRoomService.searchRooms(userId, searchTerm, pageable)
                .map(ChatRoomDTO::fromChatRoom);
    }

    @GetMapping("/{roomId}")
    public ChatRoomDTO getRoom(@PathVariable String roomId) {
        return ChatRoomDTO.fromChatRoom(chatRoomService.getRoom(roomId));
    }

    @PostMapping
    public ChatRoomDTO createRoom(@RequestBody ChatRoom room) {
        return ChatRoomDTO.fromChatRoom(chatRoomService.createRoom(room));
    }

    @GetMapping("/{roomId}/unread")
    public long getUnreadCount(
            @PathVariable String roomId,
            @RequestParam String userId) {
        return chatRoomService.getUnreadCount(roomId, userId);
    }

    @PostMapping("/{roomId}/read")
    public void markAsRead(
            @PathVariable String roomId,
            @RequestParam String userId,
            @RequestParam(required = false) String messageId) {
        if (messageId != null) {
            chatRoomService.markAsRead(roomId, userId, messageId);
        } else {
            chatRoomService.markRoomAsRead(roomId, userId);
        }
    }
}