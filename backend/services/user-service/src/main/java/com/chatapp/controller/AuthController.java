package com.chatapp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final JwtService jwtService;
    
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            User user = userService.register(request);
            String token = jwtService.generateToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);
            
            return ResponseEntity.ok(new AuthResponse(user, token, refreshToken));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ErrorResponse("REGISTRATION_FAILED", e.getMessage()));
        }
    }
    
    @PostMapping("/login") 
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            User user = userService.authenticate(request);
            String token = jwtService.generateToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);
            
            return ResponseEntity.ok(new AuthResponse(user, token, refreshToken));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ErrorResponse("LOGIN_FAILED", e.getMessage()));
        }
    }
} 