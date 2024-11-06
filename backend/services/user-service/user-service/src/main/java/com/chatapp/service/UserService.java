package com.chatapp.service;

import com.chatapp.dto.RegisterRequest;
import com.chatapp.dto.UserResponse;
import com.chatapp.model.User;
import com.chatapp.model.Role;
import com.chatapp.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Primary;

import java.util.Map;
import java.util.Optional;

@Service
@Primary
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserResponse registerUser(RegisterRequest request) {
        log.debug("Registering new user with email: {}", request.getEmail());
        
        if (userRepository.existsByUsername(request.getUsername())) {
            log.warn("Username already taken: {}", request.getUsername());
            throw new RuntimeException("Tên người dùng đã tồn tại");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Email already registered: {}", request.getEmail());
            throw new RuntimeException("Email đã được đăng ký");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);

        User savedUser = userRepository.save(user);
        log.info("Successfully registered user: {}", request.getEmail());

        return UserResponse.builder()
            .id(savedUser.getId())
            .username(savedUser.getUsername())
            .email(savedUser.getEmail())
            .build();
    }

    public User createUser(User user) {
        log.debug("Creating new user with email: {}", user.getEmail());
        
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            log.warn("Email already exists: {}", user.getEmail());
            throw new RuntimeException("Email đã tồn tại");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);
        log.info("Successfully created user: {}", user.getEmail());
        return savedUser;
    }

    public Optional<User> getUserById(Long id) {
        log.debug("Getting user by id: {}", id);
        return userRepository.findById(id);
    }

    public Optional<User> getUserByEmail(String email) {
        log.debug("Getting user by email: {}", email);
        return userRepository.findByEmail(email);
    }

    public Map<String, Object> getUserProfile(User user) {
        log.debug("Getting profile for user: {}", user.getEmail());
        return Map.of(
            "id", user.getId(),
            "email", user.getEmail(),
            "username", user.getUsername()
        );
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Loading user by username (email): {}", username);
        return userRepository.findByEmail(username)
            .orElseThrow(() -> {
                log.warn("User not found with email: {}", username);
                return new UsernameNotFoundException("Không tìm thấy người dùng với email: " + username);
            });
    }
}