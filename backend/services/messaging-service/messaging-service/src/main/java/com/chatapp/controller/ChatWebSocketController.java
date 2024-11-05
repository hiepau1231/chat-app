package com.chatapp.controller;

import com.chatapp.model.Message;
import com.chatapp.model.TypingStatus;
import com.chatapp.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload Message message) {
        Message savedMessage = messageService.saveMessage(message);
        messagingTemplate.convertAndSend("/topic/room." + message.getRoomId(), savedMessage);
    }

    @MessageMapping("/chat.typing")
    public void typing(@Payload TypingStatus typingStatus) {
        messagingTemplate.convertAndSend(
            "/topic/room." + typingStatus.getRoomId() + ".typing",
            typingStatus
        );
    }
} 