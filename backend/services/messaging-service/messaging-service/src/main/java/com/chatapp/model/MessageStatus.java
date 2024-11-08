package com.chatapp.model;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum MessageStatus {
    SENT("sent"),
    DELIVERED("delivered"),
    READ("read");

    private final String value;

    MessageStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static MessageStatus fromString(String value) {
        if (value == null) {
            return null;
        }
        
        for (MessageStatus status : MessageStatus.values()) {
            if (status.value.equals(value)) { // Thay đổi từ equalsIgnoreCase sang equals
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown MessageStatus value: " + value);
    }

    @Override
    public String toString() {
        return value;
    }

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