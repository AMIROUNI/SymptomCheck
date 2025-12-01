package com.symptomcheck.appointmentservice;

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
        return tokenValue -> {
            String role = extractRoleFromToken(tokenValue);

            Map<String, Object> claims = switch (role) {
                case "patient" -> Map.of(
                        "sub", "patient-123",
                        "email", "patient@example.com",
                        "realm_access", Map.of("roles", List.of("PATIENT")) // UPPERCASE for Keycloak converter
                );
                case "doctor" -> Map.of(
                        "sub", "doctor-456",
                        "email", "doctor@example.com",
                        "realm_access", Map.of("roles", List.of("DOCTOR")) // UPPERCASE
                );
                case "admin" -> Map.of(
                        "sub", "admin-789",
                        "email", "admin@example.com",
                        "realm_access", Map.of("roles", List.of("ADMIN")) // UPPERCASE
                );
                default -> Map.of(
                        "sub", "user-999",
                        "email", "user@example.com",
                        "realm_access", Map.of("roles", List.of("USER")) // UPPERCASE
                );
            };

            return Jwt.withTokenValue(tokenValue)
                    .header("alg", "none")
                    .header("typ", "JWT")
                    .claims(c -> c.putAll(claims))
                    .issuedAt(Instant.now().minusSeconds(60))
                    .expiresAt(Instant.now().plusSeconds(3600))
                    .build();
        };
    }

    private String extractRoleFromToken(String token) {
        if (token == null) return "user";
        String lower = token.toLowerCase();
        if (lower.contains("patient")) return "patient";
        if (lower.contains("doctor")) return "doctor";
        if (lower.contains("admin")) return "admin";
        return "user";
    }
}