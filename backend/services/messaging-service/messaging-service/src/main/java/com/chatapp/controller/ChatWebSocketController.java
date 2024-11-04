package com.chatapp.controller;

import com.chatapp.model.Message;
import com.chatapp.model.TypingStatus;
import com.chatapp.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {
    private final MessageService messageService;

    @MessageMapping("/chat.send")
    public void sendMessage(@Payload Message message) {
        messageService.sendMessage(message);
    }

    @MessageMapping("/chat.typing")
    public void updateTypingStatus(@Payload TypingStatus status) {
        messageService.updateTypingStatus(status);
    }
} 