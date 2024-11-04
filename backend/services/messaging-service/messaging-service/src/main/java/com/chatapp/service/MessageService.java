package com.chatapp.service;

import com.chatapp.event.MessageSentEvent;
import com.chatapp.model.Message;
import com.chatapp.model.MessageStatus;
import com.chatapp.model.TypingStatus;
import com.chatapp.repository.MessageRepository;
import com.chatapp.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final ApplicationEventPublisher eventPublisher;

    public Message sendMessage(Message message) {
        message.setCreatedAt(LocalDateTime.now());
        Message savedMessage = messageRepository.save(message);
        
        // Publish event instead of direct service call
        eventPublisher.publishEvent(new MessageSentEvent(savedMessage));
        
        // Broadcast message to room
        messagingTemplate.convertAndSend(
            "/topic/room/" + message.getRoomId(),
            savedMessage
        );
        
        return savedMessage;
    }

    public List<Message> getRoomMessages(String roomId) {
        return messageRepository.findByRoomIdOrderByCreatedAtDesc(roomId);
    }

    public List<Message> getUserMessages(String userId) {
        return messageRepository.findBySenderIdOrReceiverIdOrderByCreatedAtDesc(userId, userId);
    }

    public void deleteMessage(String messageId) {
        Message message = messageRepository.findById(messageId)
            .orElseThrow(() -> new ResourceNotFoundException("Message not found"));
            
        messageRepository.delete(message);
        
        // Notify room about message deletion
        messagingTemplate.convertAndSend(
            "/topic/room/" + message.getRoomId() + "/delete",
            messageId
        );
    }

    public void updateMessageStatus(MessageStatus status) {
        Message message = messageRepository.findById(status.getMessageId())
            .orElseThrow(() -> new ResourceNotFoundException("Message not found"));
            
        message.updateStatus(status.getUserId(), status.getStatus());
        messageRepository.save(message);
        
        // Broadcast status update
        messagingTemplate.convertAndSend(
            "/topic/room/" + message.getRoomId() + "/status",
            status
        );
    }

    public void updateTypingStatus(TypingStatus status) {
        // Broadcast typing status to room
        messagingTemplate.convertAndSend(
            "/topic/room/" + status.getRoomId() + "/typing",
            Map.of(
                "userId", status.getUserId(),
                "isTyping", status.isTyping(),
                "timestamp", status.getTimestamp()
            )
        );
    }
}