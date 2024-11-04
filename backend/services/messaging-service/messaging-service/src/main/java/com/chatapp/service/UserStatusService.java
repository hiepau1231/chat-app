package com.chatapp.service;

import com.chatapp.model.UserStatus;
import com.chatapp.repository.UserStatusRepository;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class UserStatusService {
    private final UserStatusRepository userStatusRepository;
    private static final long OFFLINE_THRESHOLD_MINUTES = 5;

    public void updateLastSeen(String userId) {
        UserStatus status = userStatusRepository.findByUserId(userId)
            .orElse(new UserStatus(userId));
        status.setLastSeen(LocalDateTime.now());
        userStatusRepository.save(status);
    }

    public boolean isOnline(String userId) {
        return userStatusRepository.findByUserId(userId)
            .map(status -> {
                LocalDateTime lastSeen = status.getLastSeen();
                LocalDateTime now = LocalDateTime.now();
                return ChronoUnit.MINUTES.between(lastSeen, now) < OFFLINE_THRESHOLD_MINUTES;
            })
            .orElse(false);
    }
} 