package com.chatapp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageStatus {
    private String messageId;
    private String userId;
    private Status status;
    private long timestamp;

    public enum Status {
        SENT,
        DELIVERED,
        READ
    }
} 