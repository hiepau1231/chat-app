package com.chatapp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TypingStatus {
    private String roomId;
    private String userId;
    private boolean typing;
    private long timestamp;
} 