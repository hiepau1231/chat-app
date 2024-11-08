package com.chatapp.service;

import com.chatapp.model.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final RedisCacheService redisCacheService;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-expiration}")
    private long refreshTokenExpiration;

    public long getAccessTokenExpiration() {
        return accessTokenExpiration;
    }

    private Key getSigningKey() {
        try {
            byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (IllegalArgumentException e) {
            // Nếu secret không phải là Base64 hợp lệ, sử dụng nó trực tiếp
            return Keys.hmacShaKeyFor(jwtSecret.getBytes());
        }
    }

    public String generateToken(User user) {
        return generateToken(user, new HashMap<>(), accessTokenExpiration);
    }

    public String generateToken(User user, Map<String, Object> extraClaims, long expiration) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(user.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(User user) {
        String tokenId = UUID.randomUUID().toString();
        Map<String, Object> claims = new HashMap<>();
        claims.put("tokenId", tokenId);
        
        String token = generateToken(user, claims, refreshTokenExpiration);
        
        redisCacheService.setWithExpiration(
            "refresh_token:" + tokenId,
            user.getId().toString(),
            refreshTokenExpiration / 1000 // Convert to seconds
        );
        
        return token;
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public void revokeRefreshToken(String token) {
        try {
            Claims claims = extractAllClaims(token);
            String tokenId = claims.get("tokenId", String.class);
            if (tokenId != null) {
                redisCacheService.delete("refresh_token:" + tokenId);
            }
        } catch (JwtException e) {
            // Log error
        }
    }

    public void blacklistToken(String token) {
        try {
            Claims claims = extractAllClaims(token);
            String tokenId = claims.get("tokenId", String.class);
            if (tokenId != null) {
                long expirationTime = claims.getExpiration().getTime() - System.currentTimeMillis();
                redisCacheService.setWithExpiration(
                    "blacklist:" + tokenId,
                    "true",
                    expirationTime / 1000 // Convert to seconds
                );
            }
        } catch (JwtException e) {
            // Log error
        }
    }

    private boolean isTokenBlacklisted(String tokenId) {
        return redisCacheService.get("blacklist:" + tokenId) != null;
    }

    public void revokeAllUserTokens(String username) {
        String pattern = "refresh_token:*";
        redisCacheService.deletePattern(pattern);
    }
}