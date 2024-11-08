package com.chatapp.service;

import com.chatapp.dto.*;
import com.chatapp.exception.AuthenticationException;
import com.chatapp.exception.UserAlreadyExistsException;
import com.chatapp.model.User;
import com.chatapp.model.UserStatus;
import com.chatapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        log.debug("Starting registration for user with email: {}", request.getEmail());
        
        try {
            // Check if user exists
            if (userRepository.findByEmail(request.getEmail()).isPresent()) {
                log.warn("Email already registered: {}", request.getEmail());
                throw new UserAlreadyExistsException("Email đã được đăng ký");
            }

            // Check if username exists
            if (userRepository.findByUsername(request.getUsername()).isPresent()) {
                log.warn("Username already taken: {}", request.getUsername());
                throw new UserAlreadyExistsException("Tên người dùng đã tồn tại");
            }

            log.debug("Creating new user object for: {}", request.getEmail());
            
            // Create user profile and settings
            User.UserSettings settings = new User.UserSettings();
            User.UserProfile profile = new User.UserProfile();
            profile.setSettings(settings);

            // Create new user with additional fields
            var user = User.builder()
                    .email(request.getEmail())
                    .username(request.getUsername())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .createdAt(LocalDateTime.now())
                    .status(UserStatus.OFFLINE)
                    .profile(profile)
                    .build();

            log.debug("Attempting to save user to database: {}", user);
            // Save user
            try {
                user = userRepository.save(user);
                log.info("Successfully saved user to database: {}", user.getEmail());
                
                log.debug("Generating tokens for user: {}", user.getEmail());
                var accessToken = jwtService.generateToken(user);
                var refreshToken = jwtService.generateRefreshToken(user);

                log.info("Successfully completed registration for user: {}", user.getEmail());
                return AuthResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .user(mapToUserResponse(user))
                        .build();
            } catch (Exception e) {
                log.error("Error saving user to database. Details: ", e);
                throw new RuntimeException("Lỗi khi đăng ký người dùng. Vui lòng thử lại sau.");
            }
        } catch (UserAlreadyExistsException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during registration. Details: ", e);
            throw new RuntimeException("Lỗi hệ thống. Vui lòng thử lại sau.");
        }
    }

    public AuthResponse login(AuthRequest request) {
        log.debug("Authenticating user: {}", request.getEmail());
        
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getEmail(),
                    request.getPassword()
                )
            );
        } catch (Exception e) {
            log.warn("Authentication failed for user: {}", request.getEmail());
            throw new AuthenticationException("Email hoặc mật khẩu không đúng");
        }

        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AuthenticationException("Không tìm thấy người dùng"));

        // Update user status and last login
        user.setStatus(UserStatus.ONLINE);
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        var accessToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

        log.info("Successfully authenticated user: {}", request.getEmail());
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(mapToUserResponse(user))
                .build();
    }

    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .status(user.getStatus())
                .profile(user.getProfile())
                .build();
    }

    public AuthResponse refreshToken(String refreshToken) {
        try {
            String username = jwtService.extractUsername(refreshToken);
            User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng"));
            
            if (jwtService.isTokenValid(refreshToken, user)) {
                String accessToken = jwtService.generateToken(user);
                return AuthResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .user(mapToUserResponse(user))
                    .build();
            }
            throw new RuntimeException("Token không hợp lệ");
        } catch (Exception e) {
            log.error("Error refreshing token", e);
            throw new RuntimeException("Lỗi khi làm mới token");
        }
    }
}