package com.chatapp.model;

import java.time.LocalDateTime;

public enum MessageStatus {
    SENT,
    DELIVERED,
    READ;

    public enum Status {
        SENT,
        DELIVERED,
        READ,
        FAILED
    }

    public static class MessageStatusDetails {
        private Status status;
        private LocalDateTime timestamp;
        private String userId;

        public MessageStatusDetails(Status status, String userId) {
            this.status = status;
            this.userId = userId;
            this.timestamp = LocalDateTime.now();
        }

        public Status getStatus() {
            return status;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }

        public String getUserId() {
            return userId;
        }
    }
}