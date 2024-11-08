package com.chatapp.service;

import com.chatapp.exception.MigrationException;
import com.chatapp.model.Role;
import com.chatapp.model.User;
import com.chatapp.model.UserStatus;
import com.chatapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class MigrationService {

    private final MongoTemplate mongoTemplate;
    private final UserRepository userRepository;

    @Transactional
    public void migrateData() {
        log.info("Starting data migration to MongoDB...");
        
        try {
            // Kiểm tra collection đã tồn tại chưa
            if (!mongoTemplate.collectionExists(User.class)) {
                mongoTemplate.createCollection(User.class);
                log.info("Created users collection");
            }

            // Tạo một số user mẫu nếu chưa có data
            if (userRepository.count() == 0) {
                createSampleUsers();
                log.info("Created sample users");
            }

            log.info("Migration completed successfully");
        } catch (Exception e) {
            log.error("Migration failed: ", e);
            throw new MigrationException("Migration failed", e);
        }
    }

    private void createSampleUsers() {
        List<User> sampleUsers = List.of(
            User.builder()
                .username("admin")
                .email("admin@chatapp.com")
                .password("$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG") // password: admin
                .role(Role.ADMIN)
                .createdAt(LocalDateTime.now())
                .status(UserStatus.ONLINE)
                .build(),
            
            User.builder()
                .username("user1")
                .email("user1@chatapp.com")
                .password("$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG") // password: password
                .role(Role.USER)
                .createdAt(LocalDateTime.now())
                .status(UserStatus.OFFLINE)
                .build(),
            
            User.builder()
                .username("user2")
                .email("user2@chatapp.com")
                .password("$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG") // password: password
                .role(Role.USER)
                .createdAt(LocalDateTime.now())
                .status(UserStatus.OFFLINE)
                .build()
        );

        userRepository.saveAll(sampleUsers);
    }

    public boolean verifyMigration() {
        log.info("Verifying migration...");
        
        try {
            // Kiểm tra collection tồn tại
            boolean collectionExists = mongoTemplate.collectionExists(User.class);
            if (!collectionExists) {
                String msg = "Users collection does not exist";
                log.error(msg);
                throw new MigrationException(msg);
            }

            // Kiểm tra có users
            long userCount = userRepository.count();
            if (userCount == 0) {
                String msg = "No users found in database";
                log.error(msg);
                throw new MigrationException(msg);
            }

            log.info("Found {} users in database", userCount);
            return true;
        } catch (MigrationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Verification failed: ", e);
            throw new MigrationException("Verification failed", e);
        }
    }
}