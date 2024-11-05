package com.chatapp.security;

import com.chatapp.model.User;
import com.chatapp.service.JwtService;
import com.chatapp.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.util.AntPathMatcher;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserService userService;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    private final List<String> publicPaths = Arrays.asList(
        "/api/auth/register",
        "/api/auth/login",
        "/api/auth/refresh"
    );

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return publicPaths.stream()
            .anyMatch(path -> pathMatcher.match(path, request.getServletPath()));
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7);
        final String userEmail = jwtService.extractUsername(jwt);

        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            User user = userService.getUserByEmail(userEmail)
                    .orElse(null);
            
            if (user != null && jwtService.isTokenValid(jwt)) {
                JwtAuthenticationToken authToken = new JwtAuthenticationToken(user);
                SecurityContextHolder.getContext().setAuthentication(authToken);
                request.setAttribute("user", user);
            }
        }
        
        filterChain.doFilter(request, response);
    }
}