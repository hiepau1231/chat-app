package com.chatapp.migration;

import com.chatapp.model.ChatRoom;
import com.chatapp.model.Message;
import com.chatapp.model.MessageStatus;
import com.chatapp.model.RoomSettings;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Updates;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Slf4j
@Component
public class DatabaseMigration {

    @Autowired
    private MongoTemplate mongoTemplate;

    @PostConstruct
    public void migrate() {
        log.info("Starting database migration...");
        migrateMessages();
        migrateChatRooms();
        log.info("Database migration completed successfully");
    }

    private void migrateMessages() {
        log.info("Migrating messages collection...");
        MongoDatabase db = mongoTemplate.getDb();
        MongoCollection<Document> messages = db.getCollection("messages");

        // Cập nhật tất cả messages
        messages.updateMany(
            new Document(), // filter tất cả documents
            Updates.combine(
                // Đổi tên timestamp thành createdAt nếu chưa có
                Updates.rename("timestamp", "createdAt"),
                // Thêm trường status nếu chưa có
                Updates.setOnInsert("status", MessageStatus.SENT.name()),
                // Xóa các trường không cần thiết
                Updates.unset("type"),
                Updates.unset("receiverId")
            )
        );
    }

    private void migrateChatRooms() {
        log.info("Migrating chat_rooms collection...");
        MongoDatabase db = mongoTemplate.getDb();
        MongoCollection<Document> chatRooms = db.getCollection("chat_rooms");

        // Cập nhật tất cả chat rooms
        chatRooms.updateMany(
            new Document(), // filter tất cả documents
            Updates.combine(
                // Thêm trường settings nếu chưa có
                Updates.setOnInsert("settings", new Document()
                    .append("mutedUsers", new ArrayList<>())
                    .append("blockedUsers", new ArrayList<>())
                    .append("admins", new ArrayList<>())
                    .append("muteNotifications", false)
                ),
                // Xóa trường unreadCount
                Updates.unset("unreadCount")
            )
        );

        // Cập nhật admins cho mỗi room
        chatRooms.find().forEach(room -> {
            ArrayList<String> members = (ArrayList<String>) room.get("members");
            if (members != null && !members.isEmpty()) {
                String adminId = members.get(0);
                chatRooms.updateOne(
                    new Document("_id", room.get("_id")),
                    Updates.set("settings.admins", new ArrayList<String>() {{
                        add(adminId);
                    }})
                );
            }
        });
    }
}