package com.chatapp.controller;

import com.chatapp.model.Message;
import com.chatapp.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;

    @MessageMapping("/chat.send")
    public void sendMessage(@Payload Message message, SimpMessageHeaderAccessor headerAccessor) {
        messageService.sendMessage(message);
    }

    @PostMapping("/api/messages")
    public ResponseEntity<Message> createMessage(@RequestBody Message message) {
        return ResponseEntity.ok(messageService.sendMessage(message));
    }

    @GetMapping("/api/messages/room/{roomId}")
    public List<Message> getRoomMessages(@PathVariable String roomId) {
        return messageService.getRoomMessages(roomId);
    }

    @GetMapping("/api/messages/user/{userId}")
    public List<Message> getUserMessages(@PathVariable String userId) {
        return messageService.getUserMessages(userId);
    }

    @DeleteMapping("/api/messages/{id}")
    public void deleteMessage(@PathVariable String id) {
        messageService.deleteMessage(id);
    }
}
