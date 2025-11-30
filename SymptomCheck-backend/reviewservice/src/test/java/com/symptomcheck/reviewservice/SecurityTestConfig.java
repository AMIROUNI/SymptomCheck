package com.symptomcheck.reviewservice;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@TestConfiguration
@Profile("test")
public class SecurityTestConfig {

    @Bean
    public JwtDecoder jwtDecoder() {
        return token -> {
            // For testing, create a mock JWT based on the token value
            return createMockJwt(token);
        };
    }

    private static Jwt createMockJwt(String token) {
        // Extract user type from token or use default
        String userType = extractUserTypeFromToken(token);

        Map<String, Object> claims = switch (userType) {
            case "patient" -> Map.of(
                    "sub", "patient-123",
                    "realm_access", Map.of("roles", List.of("PATIENT")),
                    "scope", "read write",
                    "email", "patient@example.com"
            );
            case "doctor" -> Map.of(
                    "sub", "doctor-456",
                    "realm_access", Map.of("roles", List.of("DOCTOR")),
                    "scope", "read write",
                    "email", "doctor@example.com"
            );
            case "admin" -> Map.of(
                    "sub", "admin-789",
                    "realm_access", Map.of("roles", List.of("ADMIN")),
                    "scope", "read write",
                    "email", "admin@example.com"
            );
            default -> Map.of(
                    "sub", "test-user",
                    "realm_access", Map.of("roles", List.of("USER")),
                    "scope", "read"
            );
        };

        return Jwt.withTokenValue(token)
                .header("alg", "none")
                .header("typ", "JWT")
                .claims(c -> c.putAll(claims))
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();
    }

    private static String extractUserTypeFromToken(String token) {
        // Simple logic to determine user type from token
        if (token.contains("patient")) return "patient";
        if (token.contains("doctor")) return "doctor";
        if (token.contains("admin")) return "admin";
        return "user";
    }
}