package com.SymptomCheck.userservice.controllers;

import com.SymptomCheck.userservice.dtos.UserRegistrationRequest;
import com.SymptomCheck.userservice.models.User;
import com.SymptomCheck.userservice.models.UserData;
import com.SymptomCheck.userservice.services.UserDataService;
import com.SymptomCheck.userservice.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j  // ✅ Add this annotation for logging
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private  final UserDataService userDataService;


    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> registerUser(@Valid @RequestPart("user") UserRegistrationRequest user,
                                          @RequestPart(value = "file", required = false) MultipartFile file) {
        try {
            String userId = userService.registerMyUser(user, file);
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
        log.info("=== /me endpoint called ===");
        log.info("JWT Subject (Keycloak ID): {}", jwt.getSubject());
        log.info("JWT Claims: {}", jwt.getClaims());

        String keycloakUserId = jwt.getSubject();
        log.info("🔍 Looking up user data for Keycloak ID: '{}'", keycloakUserId);

        // Get UserData from database
        Optional<UserData> optionalData = userDataService.getUserDataById(keycloakUserId);

        if (optionalData.isEmpty()) {
            log.warn("⚠️ UserData not found for Keycloak ID: '{}'", keycloakUserId);
        } else {
            log.info("✅ UserData found: {}", optionalData.get());
        }

        UserData userData = optionalData.orElse(null);

        // Build response map
        Map<String, Object> userMap = new HashMap<>();

        // Basic Keycloak info (always available)
        userMap.put("id", keycloakUserId);
        userMap.put("username", jwt.getClaim("preferred_username"));
        userMap.put("email", jwt.getClaim("email"));
        userMap.put("firstName", jwt.getClaim("given_name"));
        userMap.put("lastName", jwt.getClaim("family_name"));
        userMap.put("emailVerified", jwt.getClaim("email_verified"));

        // Extract roles from JWT
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        if (realmAccess != null && realmAccess.get("roles") != null) {
            userMap.put("roles", realmAccess.get("roles"));
        }

        // Extended user data (from database)
        if (userData != null) {
            userMap.put("phoneNumber", userData.getPhoneNumber());
            userMap.put("profilePhotoUrl", userData.getProfilePhotoUrl());
            userMap.put("isProfileComplete", userData.isProfileComplete());
            userMap.put("clinicId", userData.getClinicId());
            userMap.put("speciality", userData.getSpeciality());
            userMap.put("description", userData.getDescription());
            userMap.put("diploma", userData.getDiploma());
            userMap.put("createdAt", userData.getCreatedAt());
            userMap.put("updatedAt", userData.getUpdatedAt());
        } else {
            // Add null values for missing fields
            userMap.put("phoneNumber", null);
            userMap.put("profilePhotoUrl", null);
            userMap.put("isProfileComplete", false);
            userMap.put("clinicId", null);
            userMap.put("speciality", null);
            userMap.put("description", null);
            userMap.put("diploma", null);
            userMap.put("createdAt", null);
            userMap.put("updatedAt", null);

            log.warn("⚠️ Returning user without extended profile data");
        }

        log.info("✅ Returning user data: {}", userMap);
        log.info("=== End /me endpoint ===");

        return ResponseEntity.ok(userMap);
    }

}
