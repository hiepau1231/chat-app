package com.chatapp.controller;

import com.chatapp.service.MigrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/migration")
@RequiredArgsConstructor
@Slf4j
public class MigrationController {

    private final MigrationService migrationService;

    @PostMapping("/start")
    public ResponseEntity<String> startMigration() {
        try {
            log.info("Starting data migration process...");
            migrationService.migrateData();
            return ResponseEntity.ok("Migration completed successfully");
        } catch (Exception e) {
            log.error("Migration failed: ", e);
            return ResponseEntity.internalServerError()
                .body("Migration failed: " + e.getMessage());
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verifyMigration() {
        try {
            log.info("Starting migration verification...");
            boolean isValid = migrationService.verifyMigration();
            if (isValid) {
                return ResponseEntity.ok("Migration verification successful");
            } else {
                return ResponseEntity.badRequest()
                    .body("Migration verification failed");
            }
        } catch (Exception e) {
            log.error("Verification failed: ", e);
            return ResponseEntity.internalServerError()
                .body("Verification failed: " + e.getMessage());
        }
    }
} 