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
import org.springframework.data.redis.RedisConnectionFailureException;

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
            // Validate request
            if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
                throw new IllegalArgumentException("Email không được để trống");
            }
            if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
                throw new IllegalArgumentException("Tên người dùng không được để trống");
            }
            if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
                throw new IllegalArgumentException("Mật khẩu không được để trống");
            }

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
            
            // Create user settings with default values
            User.UserSettings settings = User.UserSettings.builder()
                .emailNotifications(true)
                .pushNotifications(true)
                .theme("light")
                .language("vi")
                .build();

            // Create user profile
            User.UserProfile profile = User.UserProfile.builder()
                .settings(settings)
                .build();

            // Create new user
            User user = User.builder()
                .email(request.getEmail())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .createdAt(LocalDateTime.now())
                .status(UserStatus.OFFLINE)
                .profile(profile)
                .build();

            log.debug("Attempting to save user to database: {}", user.getEmail());
            
            // Save user to MongoDB
            user = userRepository.save(user);
            log.info("Successfully saved user to database: {}", user.getEmail());
            
            // Generate tokens
            log.debug("Generating tokens for user: {}", user.getEmail());
            String accessToken = jwtService.generateToken(user);
            String refreshToken;
            
            try {
                refreshToken = jwtService.generateRefreshToken(user);
            } catch (RedisConnectionFailureException e) {
                log.warn("Redis connection failed. Using fallback token generation: {}", e.getMessage());
                // Fallback: Generate refresh token without Redis caching
                refreshToken = jwtService.generateTokenWithoutCaching(user);
            }

            log.info("Successfully completed registration for user: {}", user.getEmail());
            return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(mapToUserResponse(user))
                .build();

        } catch (IllegalArgumentException | UserAlreadyExistsException e) {
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

        String accessToken = jwtService.generateToken(user);
        String refreshToken;
        
        try {
            refreshToken = jwtService.generateRefreshToken(user);
        } catch (RedisConnectionFailureException e) {
            log.warn("Redis connection failed. Using fallback token generation: {}", e.getMessage());
            // Fallback: Generate refresh token without Redis caching
            refreshToken = jwtService.generateTokenWithoutCaching(user);
        }

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