package com.SymptomCheck.userservice.services;

import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.ws.rs.core.Response;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class KeycloakService {

    private static final Logger log = LoggerFactory.getLogger(KeycloakService.class);

    @Value("${keycloak.admin.server-url}")
    private String serverUrl;

    @Value("${keycloak.admin.realm}")
    private String adminRealm;

    @Value("${keycloak.admin.username}")
    private String adminUsername;

    @Value("${keycloak.admin.password}")
    private String adminPassword;

    @Value("${keycloak.admin.client-id}")
    private String adminClientId;

    @Value("${keycloak.app.realm}")
    private String appRealm;

    @Value("${keycloak.app.client-id}")
    private String appClientId;

    private Keycloak keycloak;

    @PostConstruct
    public void init() {
        try {
            this.keycloak = KeycloakBuilder.builder()
                    .serverUrl(serverUrl)
                    .realm(adminRealm)
                    .clientId(adminClientId)
                    .username(adminUsername)
                    .password(adminPassword)
                    .build();
            log.info("✅ KeycloakService initialized successfully");
        } catch (Exception e) {
            log.error("❌ Failed to initialize KeycloakService: {}", e.getMessage());
        }
    }

    /**
     * Crée un utilisateur COMPLET dans Keycloak avec tous les attributs
     */
    public String registerUser(com.SymptomCheck.userservice.models.User user) {
        try {
            // Vérifier si l'utilisateur existe déjà
            if (userExists(user.getUsername())) {
                throw new RuntimeException("User already exists: " + user.getUsername());
            }

            // Création du corps utilisateur avec TOUS les champs
            UserRepresentation keycloakUser = new UserRepresentation();
            keycloakUser.setUsername(user.getUsername());
            keycloakUser.setEmail(user.getEmail());
            keycloakUser.setFirstName(user.getFirstName());
            keycloakUser.setLastName(user.getLastName());
            keycloakUser.setEnabled(true);
            keycloakUser.setEmailVerified(true);
            keycloakUser.setRealmRoles(Collections.singletonList(user.getRole().name()));

            // ✅ AJOUT DE TOUS LES ATTRIBUTS PERSONNALISÉS
            Map<String, List<String>> attributes = new HashMap<>();

            // Attributs de base
            if (user.getPhoneNumber() != null) {
                attributes.put("phoneNumber", Collections.singletonList(user.getPhoneNumber()));
            }
            if (user.getProfilePhotoUrl() != null) {
                attributes.put("profilePhotoUrl", Collections.singletonList(user.getProfilePhotoUrl()));
            }

            // Attributs booléens
            attributes.put("isProfileComplete", Collections.singletonList(String.valueOf(user.isProfileComplete())));

            // Attributs numériques
            if (user.getClinicId() != null) {
                attributes.put("clinicId", Collections.singletonList(user.getClinicId().toString()));
            }

            // Timestamps
            if (user.getCreatedAt() != null) {
                attributes.put("createdAt", Collections.singletonList(
                        DateTimeFormatter.ISO_INSTANT.format(user.getCreatedAt())
                ));
            }
            if (user.getUpdatedAt() != null) {
                attributes.put("updatedAt", Collections.singletonList(
                        DateTimeFormatter.ISO_INSTANT.format(user.getUpdatedAt())
                ));
            } else {
                // Set updatedAt to current time if null
                attributes.put("updatedAt", Collections.singletonList(
                        DateTimeFormatter.ISO_INSTANT.format(Instant.now())
                ));
            }

            // ID de la base locale (si vous l'utilisez encore pour référence)
            if (user.getId() != null) {
                attributes.put("localUserId", Collections.singletonList(user.getId().toString()));
            }

            keycloakUser.setAttributes(attributes);

            // Création du mot de passe
            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setTemporary(false);
            credential.setType(CredentialRepresentation.PASSWORD);
            credential.setValue(user.getPasswordHash()); // Utilise le passwordHash
            keycloakUser.setCredentials(Collections.singletonList(credential));

            // Envoi vers Keycloak
            Response response = keycloak.realm(appRealm).users().create(keycloakUser);

            if (response.getStatus() == 201) {
                String userId = CreatedResponseUtil.getCreatedId(response);
                log.info("✅ User created successfully in Keycloak with ALL attributes: {}", user.getUsername());

                // Log des détails pour vérification
                logUserCreationDetails(user);

                return userId;
            } else {
                String error = "Failed to create user in Keycloak: " + response.getStatusInfo();
                log.error("❌ {}", error);
                throw new RuntimeException(error);
            }
        } catch (Exception e) {
            log.error("❌ Error creating user in Keycloak: {}", e.getMessage());
            throw new RuntimeException("Error creating user in Keycloak: " + e.getMessage());
        }
    }

    /**
     * Log les détails de la création utilisateur
     */
    private void logUserCreationDetails(com.SymptomCheck.userservice.models.User user) {
        log.info("📋 User created with ALL attributes:");
        log.info("   - Username: {}", user.getUsername());
        log.info("   - Email: {}", user.getEmail());
        log.info("   - First Name: {}", user.getFirstName());
        log.info("   - Last Name: {}", user.getLastName());
        log.info("   - Role: {}", user.getRole());
        log.info("   - Phone: {}", user.getPhoneNumber());
        log.info("   - Profile Photo: {}", user.getProfilePhotoUrl());
        log.info("   - Profile Complete: {}", user.isProfileComplete());
        log.info("   - Clinic ID: {}", user.getClinicId());
        log.info("   - Created At: {}", user.getCreatedAt());
        log.info("   - Updated At: {}", user.getUpdatedAt());
    }

    /**
     * Vérifie si un utilisateur existe dans Keycloak
     */
    public boolean userExists(String username) {
        try {
            var users = keycloak.realm(appRealm).users().search(username);
            return !users.isEmpty();
        } catch (Exception e) {
            log.error("❌ Error checking if user exists: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Récupère tous les détails d'un utilisateur depuis Keycloak
     */
    public Map<String, Object> getUserDetails(String username) {
        try {
            List<UserRepresentation> users = keycloak.realm(appRealm).users().search(username);
            if (!users.isEmpty()) {
                UserRepresentation user = users.get(0);

                Map<String, Object> userDetails = new HashMap<>();
                userDetails.put("id", user.getId());
                userDetails.put("username", user.getUsername());
                userDetails.put("email", user.getEmail());
                userDetails.put("firstName", user.getFirstName());
                userDetails.put("lastName", user.getLastName());
                userDetails.put("enabled", user.isEnabled());
                userDetails.put("emailVerified", user.isEmailVerified());
                userDetails.put("attributes", user.getAttributes());
                userDetails.put("realmRoles", user.getRealmRoles());

                return userDetails;
            }
            return null;
        } catch (Exception e) {
            log.error("❌ Error getting user details: {}", e.getMessage());
            return null;
        }
    }
}