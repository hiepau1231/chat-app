package com.chatapp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Data
@Document(collection = "last_reads")
@NoArgsConstructor
@AllArgsConstructor
public class LastRead {
    private String userId;
    private String roomId;
    private String lastMessageId;
    private LocalDateTime timestamp;
} 