package com.symptomcheck.doctorservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF (not needed for APIs)
                .csrf(csrf -> csrf.disable())
                // Authorization rules
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/**", "/public/**").permitAll() // public endpoints
                        .anyRequest().authenticated() // everything else needs a token
                )
                // Enable JWT authentication with Keycloak
                .oauth2ResourceServer(oauth2 -> oauth2.jwt());

        return http.build();
    }
}
