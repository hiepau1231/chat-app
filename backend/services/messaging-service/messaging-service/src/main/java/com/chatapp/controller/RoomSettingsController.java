package com.chatapp.controller;

import com.chatapp.model.RoomSettings;
import com.chatapp.service.RoomSettingsService;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/rooms/{roomId}/settings")
@RequiredArgsConstructor
public class RoomSettingsController {
    private final RoomSettingsService settingsService;

    @GetMapping
    public RoomSettings getRoomSettings(@PathVariable String roomId) {
        return settingsService.getRoomSettings(roomId);
    }

    @PutMapping
    public RoomSettings updateRoomSettings(
            @PathVariable String roomId,
            @RequestBody RoomSettings settings) {
        settingsService.updateRoomSettings(roomId, settings);
        return settings;
    }
} 