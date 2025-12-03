package com.SymptomCheck.userservice.controllers;

import com.SymptomCheck.userservice.dtos.DoctorProfileDto;
import com.SymptomCheck.userservice.dtos.UserRegistrationRequest;
import com.SymptomCheck.userservice.dtos.UserUpdateDto;
import com.SymptomCheck.userservice.models.UserData;
import com.SymptomCheck.userservice.services.UserDataService;
import com.SymptomCheck.userservice.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j  //  Add this annotation for logging
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
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal Jwt jwt) {
        log.info("=== /me endpoint called ===");
        log.info("JWT Subject (Keycloak ID): {}", jwt.getSubject());
        log.info("JWT Claims: {}", jwt.getClaims());

        String keycloakUserId = jwt.getSubject();
        log.info(" Looking up user data for Keycloak ID: '{}'", keycloakUserId);

        // Get UserData from database
        Optional<UserData> optionalData = userDataService.getUserDataById(keycloakUserId);

        if (optionalData.isEmpty()) {
            log.warn(" UserData not found for Keycloak ID: '{}'", keycloakUserId);
        } else {
            log.info(" UserData found: {}", optionalData.get());
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
            userMap.put("isProfileComplete", userData.getProfileComplete());
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



    @PatchMapping("disable/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Boolean> disableUser(@PathVariable("userId") String userId,
                                               @RequestParam boolean isEnable) {
        try {
            userService.disableUser(userId, isEnable);
            return ResponseEntity.ok(true);
        }
        catch (Exception e) {
            log.info(e.getMessage());
            return   ResponseEntity.internalServerError().body(false);
        }
    }


    @GetMapping("/by-role")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<UserRegistrationRequest>> getUsersByRole(@RequestParam String role) {
        try {
            List<UserRegistrationRequest> users = userService.getUsersByRole(role.toUpperCase());
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getUserById(@PathVariable("userId") String userId) {
        try {
            return ResponseEntity.ok( userService.getUserById(userId));
        }
        catch (Exception e) {
            log.info(e.getMessage());
            return ResponseEntity.internalServerError().body(false);
        }
    }
    @PutMapping("/{userId}")
    public ResponseEntity<?> updateUser(
            @PathVariable String userId,
            @Valid @RequestBody UserUpdateDto userUpdateDto,
            @AuthenticationPrincipal Jwt jwt) {

        // Remove the @Pattern annotation and handle validation manually
        if (userId == null || userId.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Invalid user ID",
                    "message", "User ID cannot be empty"
            ));
        }

        // Reject "NaN" explicitly
        if ("NaN".equals(userId)) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Invalid user ID",
                    "message", "User ID cannot be NaN"
            ));
        }

        try {
            log.info("🔄 Updating user with ID: {}", userId);

            // Check if JWT is present
            if (jwt == null) {
                return ResponseEntity.status(401).body(Map.of(
                        "error", "Authentication required"
                ));
            }

            // Vérifier que l'utilisateur peut modifier ce profil
            if (!canUserModifyProfile(jwt, userId)) {
                return ResponseEntity.status(403).body(Map.of(
                        "error", "You don't have permission to update this profile"
                ));
            }

            UserRegistrationRequest updatedUser = userService.updateUser(userId, userUpdateDto);

            log.info("✅ User updated successfully: {}", updatedUser);
            return ResponseEntity.ok(Map.of(
                    "message", "User updated successfully",
                    "user", updatedUser
            ));
        } catch (Exception e) {
            log.error("❌ Error updating user: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage()
            ));
        }
    }
    /**
     * Compléter/mettre à jour le profil docteur
     */
    @PutMapping("/{userId}/complete-profile")
    public ResponseEntity<?> completeDoctorProfile(@PathVariable String userId,
                                                   @Valid @RequestBody DoctorProfileDto doctorProfileDto,
                                                   @AuthenticationPrincipal Jwt jwt) {
        try {
            log.info(" Completing doctor profile for user ID: {}", userId);

            // Vérifier que l'utilisateur peut modifier ce profil
            if (!canUserModifyProfile(jwt, userId)) {
                return ResponseEntity.status(403).body(Map.of(
                        "error", "You don't have permission to update this profile"
                ));
            }

            UserData updatedUserData = userService.completeDoctorProfile(userId, doctorProfileDto);

            log.info(" Doctor profile completed successfully: {}", updatedUserData);
            return ResponseEntity.ok(Map.of(
                    "message", "Doctor profile completed successfully",
                    "userData", updatedUserData
            ));
        } catch (Exception e) {
            log.error(" Error completing doctor profile: {}", e.getMessage());
            return ResponseEntity.internalServerError().body(Map.of(
                    "error", e.getMessage()
            ));
        }
    }

    /**
     * Upload de photo de profil
     */
    @PostMapping(value = "/{userId}/profile-photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadProfilePhoto(@PathVariable String userId,
                                                @RequestParam("file") MultipartFile file,
                                                @AuthenticationPrincipal Jwt jwt) {
        try {
            log.info(" Uploading profile photo for user ID: {}", userId);

            // Vérifier que l'utilisateur peut modifier ce profil
            if (!canUserModifyProfile(jwt, userId)) {
                return ResponseEntity.status(403).body(Map.of(
                        "error", "You don't have permission to update this profile"
                ));
            }

            String profilePhotoUrl = userService.uploadProfilePhoto(userId, file);

            log.info(" Profile photo uploaded successfully: {}", profilePhotoUrl);
            return ResponseEntity.ok(Map.of(
                    "message", "Profile photo uploaded successfully",
                    "profilePhotoUrl", profilePhotoUrl
            ));
        } catch (Exception e) {
            log.error(" Error uploading profile photo: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage()
            ));
        }
    }

    /**
     * Vérifier si l'utilisateur peut modifier le profil
     */
    private boolean canUserModifyProfile(Jwt jwt, String targetUserId) {
        String currentUserId = jwt.getSubject();

        // L'utilisateur peut modifier son propre profil
        if (currentUserId.equals(targetUserId)) {
            return true;
        }

        // Vérifier si l'utilisateur est admin
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        if (realmAccess != null && realmAccess.get("roles") != null) {
            List<String> roles = (List<String>) realmAccess.get("roles");
            if (roles.contains("admin") || roles.contains("ADMIN")) {
                return true;
            }
        }

        return false;
    }

}
