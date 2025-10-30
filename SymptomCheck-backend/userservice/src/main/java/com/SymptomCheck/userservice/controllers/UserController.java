package com.SymptomCheck.userservice.controllers;

import com.SymptomCheck.userservice.models.User;
import com.SymptomCheck.userservice.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody User user) {
        try {
            // S'assurer que les timestamps sont définis
            if (user.getCreatedAt() == null) {
                user.setCreatedAt(Instant.now());
            }
            user.setUpdatedAt(Instant.now());

            String userId = userService.registerUser(user);
            return ResponseEntity.ok(Map.of(
                    "message", "User registered successfully in Keycloak with ALL attributes",
                    "keycloakUserId", userId,
                    "username", user.getUsername()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/details/{username}")
    public ResponseEntity<?> getUserDetails(@PathVariable String username) {
        try {
            var userDetails = userService.getUserDetails(username);
            if (userDetails != null) {
                return ResponseEntity.ok(userDetails);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(Map.of(
                "username", jwt.getClaim("preferred_username"),
                "email", jwt.getClaim("email"),
                "firstName", jwt.getClaim("given_name"),
                "lastName", jwt.getClaim("family_name"),
                "roles", jwt.getClaim("realm_access"),
                "userId", jwt.getSubject(),
                "allClaims", jwt.getClaims() // Pour voir tous les attributs
        ));
    }
}