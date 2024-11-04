package com.chatapp.event;

import com.chatapp.model.Message;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class MessageSentEvent extends ApplicationEvent {
    private final Message message;

    public MessageSentEvent(Message message) {
        super(message);
        this.message = message;
    }
} 