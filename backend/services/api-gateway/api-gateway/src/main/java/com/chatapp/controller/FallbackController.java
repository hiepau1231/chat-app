package com.chatapp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/user-service")
    public ResponseEntity<String> userServiceFallback() {
        return ResponseEntity
            .status(503)
            .body("User service is currently unavailable. Please try again later.");
    }

    @GetMapping("/messaging-service")
    public ResponseEntity<String> messagingServiceFallback() {
        return ResponseEntity
            .status(503)
            .body("Messaging service is currently unavailable. Please try again later.");
    }
} 