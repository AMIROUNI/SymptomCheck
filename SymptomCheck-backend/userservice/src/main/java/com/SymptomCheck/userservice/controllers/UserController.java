package com.SymptomCheck.userservice.controllers;

import com.SymptomCheck.userservice.dtos.UserRegistrationRequest;
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
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRegistrationRequest user) {
        try {
            // S'assurer que les timestamps sont définis


            String userId = userService.registerMyUser(user);
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
        Map<String, Object> userMap = Map.ofEntries(
                Map.entry("username", jwt.getClaim("preferred_username")),
                Map.entry("email", jwt.getClaim("email")),
                Map.entry("firstName", jwt.getClaim("given_name")),
                Map.entry("lastName", jwt.getClaim("family_name")),
                Map.entry("roles", jwt.getClaim("realm_access")),
                Map.entry("userId", jwt.getSubject()),
                Map.entry("phoneNumber", jwt.getClaim("phoneNumber")),
                Map.entry("profilePhotoUrl", jwt.getClaim("profilePhotoUrl")),
                Map.entry("isProfileComplete", jwt.getClaim("isProfileComplete")),
                Map.entry("clinicId", jwt.getClaim("clinicId")),
                Map.entry("localUserId", jwt.getClaim("localUserId")),
                Map.entry("createdAt", jwt.getClaim("createdAt")),
                Map.entry("updatedAt", jwt.getClaim("updatedAt")),
                Map.entry("allClaims", jwt.getClaims())
        );
        return ResponseEntity.ok(userMap);
    }

}
