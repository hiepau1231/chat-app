package com.chatapp.controller;

import com.chatapp.model.Message;
import com.chatapp.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;

    @PostMapping
    public Message sendMessage(@RequestBody Message message) {
        return messageService.sendMessage(message);
    }

    @GetMapping("/room/{roomId}")
    public List<Message> getRoomMessages(@PathVariable String roomId) {
        return messageService.getRoomMessages(roomId);
    }

    @GetMapping("/user/{userId}")
    public List<Message> getUserMessages(@PathVariable String userId) {
        return messageService.getUserMessages(userId);
    }

    @DeleteMapping("/{messageId}")
    public ResponseEntity<?> deleteMessage(@PathVariable String messageId) {
        messageService.deleteMessage(messageId);
        return ResponseEntity.ok().build();
    }
}
