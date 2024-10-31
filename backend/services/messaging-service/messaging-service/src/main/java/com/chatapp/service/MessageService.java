package com.chatapp.service;

import com.chatapp.model.Message;
import com.chatapp.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public Message sendMessage(Message message) {
        Message savedMessage = messageRepository.save(message);
        
        // Send to room topic
        messagingTemplate.convertAndSend("/topic/room/" + message.getRoomId(), savedMessage);
        
        // Send to specific user if it's a private message
        if (message.getReceiverId() != null) {
            messagingTemplate.convertAndSendToUser(
                message.getReceiverId(),
                "/queue/messages",
                savedMessage
            );
        }
        
        return savedMessage;
    }

    public List<Message> getRoomMessages(String roomId) {
        return messageRepository.findByRoomIdOrderByCreatedAtDesc(roomId);
    }

    public List<Message> getUserMessages(String userId) {
        return messageRepository.findBySenderIdOrReceiverIdOrderByCreatedAtDesc(userId, userId);
    }

    public Message getMessage(String id) {
        return messageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Message not found"));
    }

    public void deleteMessage(String id) {
        messageRepository.deleteById(id);
    }
}