package com.chatapp.config;

import com.chatapp.service.DatabaseMigrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final DatabaseMigrationService databaseMigrationService;
    private final MongoTemplate mongoTemplate;

    @Bean
    @Profile("!test") // Không chạy trong môi trường test
    public CommandLineRunner initializeData() {
        return args -> {
            log.info("Checking if data initialization is needed...");
            
            // Kiểm tra xem collection users đã có dữ liệu chưa
            if (mongoTemplate.getCollection("users").countDocuments() == 0) {
                log.info("Users collection is empty. Starting data initialization...");
                databaseMigrationService.initializeSampleData();
            } else {
                log.info("Users collection already has data. Skipping initialization.");
            }
        };
    }
}