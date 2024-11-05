package com.chatapp.controller;

import com.chatapp.dto.AuthRequest;
import com.chatapp.dto.AuthResponse;
import com.chatapp.dto.RegisterRequest;
import com.chatapp.dto.UserResponse;
import com.chatapp.dto.ApiResponse;
import com.chatapp.service.AuthenticationService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest request) {
        log.info("Received registration request for user: {}", request.getUsername());
        try {
            UserResponse response = authenticationService.register(request);
            log.info("Successfully registered user: {}", request.getUsername());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error registering user: {}", e.getMessage());
            return ResponseEntity
                .badRequest()
                .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticate(@RequestBody AuthRequest request) {
        AuthResponse response = authenticationService.authenticate(request);
        return ResponseEntity.ok(response);
    }
}