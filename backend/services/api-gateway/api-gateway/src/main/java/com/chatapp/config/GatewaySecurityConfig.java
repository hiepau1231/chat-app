package com.chatapp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.server.ServerWebExchange;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import reactor.core.publisher.Mono;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import org.springframework.core.io.buffer.DataBuffer;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.support.ipresolver.XForwardedRemoteAddressResolver;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;

/**
 * Cấu hình bảo mật toàn diện cho API Gateway
 * Quản lý xác thực, CORS, và các quy tắc bảo mật
 * 
 * Lưu ý: Cấu hình rate limiter được thực hiện riêng trong application.yml
 * Phiên bản Spring Security: 3.2.0
 */
@Configuration
@EnableWebFluxSecurity
public class GatewaySecurityConfig {

    @Value("${jwt.secret}")
    private String jwtSecret;

    private static final Logger log = LoggerFactory.getLogger(GatewaySecurityConfig.class);

    /**
     * Cấu hình SecurityWebFilterChain cho API Gateway
     * 
     * Chi tiết cấu hình:
     * - Vô hiệu hóa CSRF
     * - Cấu hình CORS
     * - Vô hiệu hóa HTTP Basic và Form Login
     * - Xác định quyền truy cập cho các endpoint
     * - Cấu hình xác thực JWT
     * - Xử lý các trường hợp ngoại lệ xác thực
     * 
     * Lưu ý: Không sử dụng requestRateLimiter() để tránh lỗi với Spring Security 3.2.0
     */
    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .httpBasic(httpBasic -> httpBasic.disable())
            .formLogin(formLogin -> formLogin.disable())
            .authorizeExchange(exchanges -> exchanges
                .pathMatchers("/api/auth/**").permitAll()
                .pathMatchers("/ws/**").permitAll()
                .pathMatchers("/actuator/**").permitAll()
                .anyExchange().authenticated()
            )
            .authenticationManager(authenticationManager())
            .securityContextRepository(securityContextRepository())
            .exceptionHandling(exceptionHandling -> exceptionHandling
                .authenticationEntryPoint((exchange, ex) -> {
                    ServerHttpResponse response = exchange.getResponse();
                    response.setStatusCode(HttpStatus.UNAUTHORIZED);
                    response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
                    String errorResponse = "{\"error\": \"Unauthorized\", \"message\": \"" + ex.getMessage() + "\"}";
                    byte[] bytes = errorResponse.getBytes(StandardCharsets.UTF_8);
                    DataBuffer buffer = response.bufferFactory().wrap(bytes);
                    return response.writeWith(Mono.just(buffer));
                })
            )
            .build();
    }

    /**
     * Tạo KeyResolver để xác định khóa giới hạn tốc độ
     * Sử dụng địa chỉ IP làm khóa, hỗ trợ các ứng dụng đằng sau proxy
     * 
     * Lưu ý: Cấu hình chi tiết rate limiter được thực hiện trong application.yml
     */
    @Bean
    public KeyResolver ipKeyResolver() {
        return exchange -> {
            XForwardedRemoteAddressResolver resolver = XForwardedRemoteAddressResolver.maxTrustedIndex(1);
            return Mono.justOrEmpty(resolver.resolve(exchange).getHostString());
        };
    }

    /**
     * Cấu hình Rate Limiter sử dụng Redis
     * Giới hạn số lượng yêu cầu từ mỗi địa chỉ IP
     * 
     * Lưu ý: Cấu hình chi tiết được thực hiện trong application.yml
     */
    @Bean
    public RedisRateLimiter redisRateLimiter() {
        return new RedisRateLimiter(100, 100, 60);
    }

    /**
     * Tạo ServerSecurityContextRepository để quản lý SecurityContext
     * Xác thực token và lưu trữ thông tin xác thực
     */
    @Bean
    public ServerSecurityContextRepository securityContextRepository() {
        return new ServerSecurityContextRepository() {
            @Override
            public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
                return Mono.empty();
            }

            @Override
            public Mono<SecurityContext> load(ServerWebExchange exchange) {
                String token = extractToken(exchange);
                if (token != null) {
                    return authenticationManager()
                        .authenticate(new UsernamePasswordAuthenticationToken(token, null))
                        .map(authentication -> {
                            SecurityContext context = new SecurityContextImpl();
                            context.setAuthentication(authentication);
                            return context;
                        });
                }
                return Mono.empty();
            }
        };
    }

    /**
     * Trích xuất JWT token từ header Authorization
     */
    private String extractToken(ServerWebExchange exchange) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            exchange.getAttributes().put("token", token);
            return token;
        }
        return null;
    }

    /**
     * Tạo ReactiveAuthenticationManager để xác thực JWT token
     * Kiểm tra tính hợp lệ của token và trích xuất thông tin người dùng
     */
    @Bean
    public ReactiveAuthenticationManager authenticationManager() {
        return authentication -> {
            try {
                String token = authentication.getPrincipal().toString();
                log.debug("Processing token: {}", token);

                Claims claims = validateTokenAndGetClaims(token);
                if (claims == null) {
                    log.error("Invalid JWT token");
                    return Mono.error(new RuntimeException("Invalid JWT token"));
                }

                List<SimpleGrantedAuthority> authorities = 
                    Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));

                Authentication auth = new UsernamePasswordAuthenticationToken(
                    claims.getSubject(),
                    null,
                    authorities
                );
                return Mono.just(auth);
            } catch (ExpiredJwtException e) {
                log.error("JWT token has expired: {}", e.getMessage());
                return Mono.error(new RuntimeException("Token has expired"));
            } catch (MalformedJwtException e) {
                log.error("Malformed JWT token: {}", e.getMessage());
                return Mono.error(new RuntimeException("Malformed token"));
            } catch (SignatureException e) {
                log.error("Invalid JWT signature: {}", e.getMessage());
                return Mono.error(new RuntimeException("Invalid token signature"));
            } catch (Exception e) {
                log.error("Authentication failed: {}", e.getMessage());
                return Mono.error(new RuntimeException("Authentication failed"));
            }
        };
    }

    /**
     * Xác thực và trích xuất thông tin từ JWT token
     * Sử dụng khóa bí mật để giải mã và xác thực token
     */
    private Claims validateTokenAndGetClaims(String token) {
        try {
            byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
            Key signingKey = Keys.hmacShaKeyFor(keyBytes);
            
            return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        } catch (Exception e) {
            log.error("Error validating token: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Cấu hình CORS cho API Gateway
     * Cho phép các yêu cầu từ localhost:5173 với các phương thức và header cụ thể
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD"));
        configuration.setAllowedHeaders(Arrays.asList(
            HttpHeaders.AUTHORIZATION,
            HttpHeaders.CONTENT_TYPE,
            HttpHeaders.ACCEPT,
            "X-Requested-With",
            "Origin"
        ));
        configuration.setExposedHeaders(Arrays.asList(HttpHeaders.AUTHORIZATION));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}