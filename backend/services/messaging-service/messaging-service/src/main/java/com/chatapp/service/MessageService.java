package com.chatapp.service;

import com.chatapp.model.Message;
import com.chatapp.model.MessageStatus;
import com.chatapp.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;

    public List<Message> getAllMessages() {
        return messageRepository.findAll();
    }

    public List<Message> getRoomMessages(String roomId) {
        return messageRepository.findByRoomIdOrderByTimestampDesc(roomId);
    }

    public Message sendMessage(Message message) {
        message.setTimestamp(LocalDateTime.now());
        message.setStatus(MessageStatus.SENT);
        return messageRepository.save(message);
    }

    public Message saveMessage(Message message) {
        return messageRepository.save(message);
    }

    public void deleteMessage(String messageId) {
        messageRepository.deleteById(messageId);
    }

    public Long countMessagesByRoom(String roomId) {
        return messageRepository.countByRoomId(roomId);
    }

    public Long countNewMessages(String roomId, LocalDateTime since) {
        return messageRepository.countByRoomIdAndTimestampAfter(roomId, since);
    }

    public Optional<Message> getLastMessage(String roomId) {
        return messageRepository.findFirstByRoomIdOrderByTimestampDesc(roomId);
    }

    public Message updateMessageStatus(String messageId, MessageStatus newStatus) {
        return messageRepository.findById(messageId)
            .map(message -> {
                message.setStatus(newStatus);
                return messageRepository.save(message);
            })
            .orElseThrow(() -> new RuntimeException("Message not found"));
    }
}