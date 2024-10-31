package com.chatapp.controller;

import com.chatapp.model.ChatRoom;
import com.chatapp.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class ChatRoomController {
    private final ChatRoomService chatRoomService;

    @PostMapping
    public ResponseEntity<ChatRoom> createRoom(@RequestBody ChatRoom room) {
        return ResponseEntity.ok(chatRoomService.createRoom(room));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChatRoom> getRoom(@PathVariable String id) {
        return ResponseEntity.ok(chatRoomService.getRoom(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ChatRoom>> getRoomsByUser(@PathVariable String userId) {
        return ResponseEntity.ok(chatRoomService.getRoomsByUser(userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ChatRoom> updateRoom(@PathVariable String id, @RequestBody ChatRoom room) {
        return ResponseEntity.ok(chatRoomService.updateRoom(id, room));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoom(@PathVariable String id) {
        chatRoomService.deleteRoom(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{roomId}/members/{userId}")
    public ResponseEntity<ChatRoom> addMember(@PathVariable String roomId, @PathVariable String userId) {
        return ResponseEntity.ok(chatRoomService.addMember(roomId, userId));
    }

    @DeleteMapping("/{roomId}/members/{userId}")
    public ResponseEntity<ChatRoom> removeMember(@PathVariable String roomId, @PathVariable String userId) {
        return ResponseEntity.ok(chatRoomService.removeMember(roomId, userId));
    }
}