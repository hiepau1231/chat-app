package com.chatapp.controller;

import com.chatapp.model.Message;
import com.chatapp.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@Slf4j
public class MessageController {
    private final MessageService messageService;

    @GetMapping
    public ResponseEntity<?> getAllMessages() {
        log.info("Received request to get all messages");
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            log.info("User {} requesting all messages", auth.getName());
            
            List<Message> messages = messageService.getAllMessages();
            log.info("Retrieved {} messages", messages.size());
            return ResponseEntity.ok(messages);
        } catch (DataAccessException e) {
            log.error("Database error while retrieving messages: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("Database error occurred: " + e.getMessage());
        } catch (Exception e) {
            log.error("Error retrieving all messages: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<Message> sendMessage(@RequestBody Message message) {
        log.info("Received request to send message: {}", message);
        try {
            Message savedMessage = messageService.saveMessage(message);
            log.info("Message sent successfully: {}", savedMessage);
            return ResponseEntity.ok(savedMessage);
        } catch (Exception e) {
            log.error("Error sending message: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/room/{roomId}")
    public ResponseEntity<List<Message>> getMessagesByRoomId(@PathVariable String roomId) {
        log.info("Received request to get messages for room: {}", roomId);
        try {
            List<Message> messages = messageService.getRoomMessages(roomId);
            log.info("Retrieved {} messages for room {}", messages.size(), roomId);
            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            log.error("Error retrieving messages for room {}: {}", roomId, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{messageId}/status")
    public ResponseEntity<Message> updateMessageStatus(@PathVariable String messageId, @RequestBody Message message) {
        log.info("Received request to update status for message: {}", messageId);
        try {
            Message updatedMessage = messageService.updateMessageStatus(messageId, message.getStatus());
            log.info("Message status updated successfully: {}", updatedMessage);
            return ResponseEntity.ok(updatedMessage);
        } catch (Exception e) {
            log.error("Error updating message status: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}