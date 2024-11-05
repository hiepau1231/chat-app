package com.chatapp.controller;

import com.chatapp.model.User;
import com.chatapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestAttribute("user") User user) {
        return ResponseEntity.ok(userService.getUserProfile(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable UUID id) {
        return userService.getUserById(id)
                .map(user -> ResponseEntity.ok(userService.getUserProfile(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<?> getUserByEmail(@PathVariable String email) {
        return userService.getUserByEmail(email)
                .map(user -> ResponseEntity.ok(userService.getUserProfile(user)))
                .orElse(ResponseEntity.notFound().build());
    }
}
