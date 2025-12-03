package com.SymptomCheck.userservice;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@TestConfiguration
@Profile("test")
public class SecurityTestConfig {

    @Bean
    public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Allow public access to registration endpoints
                        .requestMatchers("/api/v1/users/register").permitAll()
                        .requestMatchers("/api/v1/users/details/**").permitAll()
                        // All other requests require authentication
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(jwtAuthenticationConverter())
                        )
                );

        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return token -> {
            String userType = extractUserTypeFromToken(token);

            Map<String, Object> claims = switch (userType) {
                case "patient" -> Map.of(
                        "sub", "patient-123",
                        "preferred_username", "patient_user",
                        "email", "patient@example.com",
                        "given_name", "Ali",
                        "family_name", "Ben",
                        "realm_access", Map.of("roles", List.of("PATIENT", "USER"))
                );
                case "doctor" -> Map.of(
                        "sub", "doctor-456",
                        "preferred_username", "dr_smith",
                        "email", "doctor@example.com",
                        "given_name", "Dr",
                        "family_name", "Smith",
                        "realm_access", Map.of("roles", List.of("DOCTOR", "USER"))
                );
                case "admin" -> Map.of(
                        "sub", "admin-789",
                        "preferred_username", "admin",
                        "email", "admin@example.com",
                        "given_name", "Admin",
                        "family_name", "User",
                        "realm_access", Map.of("roles", List.of("ADMIN", "USER"))
                );
                default -> Map.of(
                        "sub", "test-user-123",
                        "preferred_username", "testuser",
                        "email", "test@example.com",
                        "given_name", "Test",
                        "family_name", "User",
                        "realm_access", Map.of("roles", List.of("USER"))
                );
            };

            return Jwt.withTokenValue(token != null ? token : "mock-jwt-token")
                    .headers(headers -> {
                        headers.put("alg", "none");
                        headers.put("typ", "JWT");
                    })
                    .claims(c -> c.putAll(claims))
                    .issuedAt(Instant.now())
                    .expiresAt(Instant.now().plusSeconds(3600))
                    .build();
        };
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();

        // Extract roles from realm_access.roles and prefix with "ROLE_"
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            Map<String, Object> realmAccess = (Map<String, Object>) jwt.getClaim("realm_access");
            if (realmAccess == null || realmAccess.get("roles") == null) {
                return List.of();
            }

            List<String> roles = (List<String>) realmAccess.get("roles");
            return roles.stream()
                    .map(role -> "ROLE_" + role.toUpperCase())
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        });

        converter.setPrincipalClaimName("preferred_username");
        return converter;
    }

    private static String extractUserTypeFromToken(String token) {
        if (token == null) return "user";
        String lower = token.toLowerCase();
        if (lower.contains("patient")) return "patient";
        if (lower.contains("doctor")) return "doctor";
        if (lower.contains("admin")) return "admin";
        return "user";
    }
}