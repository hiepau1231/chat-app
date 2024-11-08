package com.chatapp.service;

import com.chatapp.model.User;
import com.chatapp.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.MongoTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class MigrationServiceTest {

    @Autowired
    private MigrationService migrationService;

    @MockBean
    private MongoTemplate mongoTemplate;

    @MockBean
    private UserRepository userRepository;

    @Test
    void migrateData_Success() {
        when(mongoTemplate.collectionExists(User.class)).thenReturn(false);
        when(userRepository.count()).thenReturn(0L);

        assertDoesNotThrow(() -> migrationService.migrateData());
        
        verify(mongoTemplate).createCollection(User.class);
        verify(userRepository).saveAll(anyList());
    }

    @Test
    void verifyMigration_Success() {
        when(mongoTemplate.collectionExists(User.class)).thenReturn(true);
        when(userRepository.count()).thenReturn(3L);

        boolean result = migrationService.verifyMigration();
        
        assertTrue(result);
    }
}