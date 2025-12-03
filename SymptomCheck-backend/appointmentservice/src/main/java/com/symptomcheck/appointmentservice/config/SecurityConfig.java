package com.symptomcheck.appointmentservice.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
public class SecurityConfig {


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // Disable CSRF for stateless APIs
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/webjars/**",
                                "/api/v1/users/register",
                                "/api/v1/users/public/**",
                                "/api/v1/auth/**",
                                "/actuator/health",
                                "/uploads/**").permitAll()
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()

                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/users/doctor/**").hasAnyRole("DOCTOR", "ADMIN")
                        .requestMatchers("/api/v1/users/patient/**").hasAnyRole("PATIENT", "ADMIN")

                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:4200")); // ✅ Angular app URL
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true); // ✅ Required when using Keycloak cookies/tokens

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public Converter<Jwt, AbstractAuthenticationToken> jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(new KeycloakRealmRoleConverter());
        return converter;
    }

    public static class KeycloakRealmRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

        @Override
        public Collection<GrantedAuthority> convert(Jwt jwt) {

            // Ensure claim exists (avoids returning null, required by @NonNullApi)
            Object realmAccessObj = jwt.getClaims().get("realm_access");
            if (!(realmAccessObj instanceof Map<?, ?> realmAccess)) {
                return List.of();
            }

            Object rolesObj = realmAccess.get("roles");
            if (!(rolesObj instanceof List<?> rolesList)) {
                return List.of();
            }

            // Safe conversion + non-null return
            return rolesList.stream()
                    .filter(String.class::isInstance)
                    .map(role -> "ROLE_" + ((String) role).toUpperCase())
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

        }
    }

}
