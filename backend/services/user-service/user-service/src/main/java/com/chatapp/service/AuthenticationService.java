package com.chatapp.service;

import com.chatapp.dto.*;
import com.chatapp.exception.AuthenticationException;
import com.chatapp.exception.UserAlreadyExistsException;
import com.chatapp.model.User;
import com.chatapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public UserResponse register(RegisterRequest request) {
        log.debug("Registering new user with email: {}", request.getEmail());
        
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

            // Create new user
            var user = User.builder()
                    .email(request.getEmail())
                    .username(request.getUsername())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .build();

            // Save user
            try {
                user = userRepository.save(user);
                log.info("Successfully registered user: {}", request.getEmail());
                return mapToUserResponse(user);
            } catch (Exception e) {
                log.error("Error saving user to database", e);
                throw new RuntimeException("Lỗi khi đăng ký người dùng. Vui lòng thử lại sau.");
            }
        } catch (UserAlreadyExistsException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during registration", e);
            throw new RuntimeException("Lỗi hệ thống. Vui lòng thử lại sau.");
        }
    }

    public AuthResponse authenticate(AuthRequest request) {
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
                .build();
    }

    public AuthResponse refreshToken(String refreshToken) {
        try {
            String email = jwtService.extractUsername(refreshToken);
            User user = userRepository.findByEmail(email)
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