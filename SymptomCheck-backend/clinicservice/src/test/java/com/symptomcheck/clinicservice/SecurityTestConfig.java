package com.symptomcheck.clinicservice;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;

import java.time.Instant;
import java.util.Map;

@TestConfiguration
@Profile("test")
public class SecurityTestConfig {

    @Bean
    public JwtDecoder jwtDecoder() {
        return new JwtDecoder() {
            @Override
            public Jwt decode(String token) throws JwtException {
                // Return a mock JWT with basic claims
                return new Jwt(
                        token,
                        Instant.now(),
                        Instant.now().plusSeconds(3600),
                        Map.of("alg", "none"),
                        Map.of(
                                "sub", "test-user",
                                "scope", "read write",
                                "roles", new String[]{"USER"}
                        )
                );
            }
        };
    }
}