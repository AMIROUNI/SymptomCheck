package com.SymptomCheck.userservice.services;

import com.SymptomCheck.userservice.dtos.UserRegistrationRequest;
import com.SymptomCheck.userservice.models.User;
import com.SymptomCheck.userservice.models.UserData;
import com.SymptomCheck.userservice.repositories.UserDataRepository;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RoleResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.ws.rs.core.Response;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class KeycloakService {

    private static final Logger log = LoggerFactory.getLogger(KeycloakService.class);

    private final UserDataRepository userDataRepository ;



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
     * Crée un utilisateur COMPLET dans Keycloak avec tous les attributs personnalisés
     */
    public String registerUser(User user) {
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

            // Assigner le rôle
            keycloakUser.setRealmRoles(Collections.singletonList(user.getRole().name()));

            // ID de la base locale (si vous l'utilisez encore pour référence)

            // Création du mot de passe
            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setTemporary(false);
            credential.setType(CredentialRepresentation.PASSWORD);
            credential.setValue(user.getPasswordHash());
            keycloakUser.setCredentials(Collections.singletonList(credential));

            // Envoi vers Keycloak
            Response response = keycloak.realm(appRealm).users().create(keycloakUser);

            if (response.getStatus() == 201) {
                String userId = CreatedResponseUtil.getCreatedId(response);
                log.info("✅ User created successfully in Keycloak with ALL attributes: {}", user.getUsername());

                // Assigner explicitement le rôle
                assignRoleToUser(userId, user.getRole().name());

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
     * Assigner un rôle à un utilisateur
     */
    private void assignRoleToUser(String userId, String roleName) {
        try {
            // Récupérer le rôle du realm
            var role = keycloak.realm(appRealm).roles().get(roleName).toRepresentation();

            // Assigner le rôle à l'utilisateur
            keycloak.realm(appRealm).users().get(userId).roles().realmLevel().add(Collections.singletonList(role));

            log.info("✅ Role {} assigned to user {}", roleName, userId);
        } catch (Exception e) {
            log.error("❌ Error assigning role {} to user {}: {}", roleName, userId, e.getMessage());
        }
    }

    /**
     * Log les détails de la création utilisateur
     */
    private void logUserCreationDetails(User user) {
        log.info("📋 User created with ALL attributes:");
        log.info("   - Username: {}", user.getUsername());
        log.info("   - Email: {}", user.getEmail());
        log.info("   - First Name: {}", user.getFirstName());
        log.info("   - Last Name: {}", user.getLastName());
        log.info("   - Role: {}", user.getRole());

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






    public void disableUser(String userId,boolean  isEnabled) {
        Keycloak keycloak = KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm("master") // admin login always happens on master
                .clientId("admin-cli")
                .username("admin")
                .password("admin")
                .grantType("password")
                .build();

        UsersResource users = keycloak.realm(appRealm).users();
        UserRepresentation user = users.get(userId).toRepresentation();
        user.setEnabled(isEnabled);

        users.get(userId).update(user);
    }



    public List<UserRegistrationRequest> getUsersByRole(String roleName) {
        List<UserRegistrationRequest> result = new ArrayList<>();

        try {
            // 1️⃣ Get Keycloak users with this role

            log.info(roleName);
            List<UserRepresentation> keycloakUsers = new ArrayList<>(keycloak.realm(appRealm)
                    .roles()
                    .get(roleName)
                    .getRoleUserMembers());
            log.info("keycloakUsers size: {}", keycloakUsers.size());



            // 2️⃣ Map each Keycloak user to your UserRegistrationRequest
            for (UserRepresentation kcUser : keycloakUsers) {
                // Assuming you have a repository to fetch UserData by ID
                Optional<UserData> userDataOpt = userDataRepository.findById(kcUser.getId());

                UserRegistrationRequest ur = new UserRegistrationRequest();
                ur.setId(kcUser.getId());
                ur.setUsername(kcUser.getUsername());
                ur.setEmail(kcUser.getEmail());
                ur.setFirstName(kcUser.getFirstName());
                ur.setLastName(kcUser.getLastName());
                ur.setRole(roleName);
                ur.setEnabled(kcUser.isEnabled());

                // Fill additional fields if exists
                userDataOpt.ifPresent(userData -> {
                    ur.setPhoneNumber(userData.getPhoneNumber());
                    ur.setProfilePhotoUrl(userData.getProfilePhotoUrl());
                    ur.setSpeciality(userData.getSpeciality());
                    ur.setClinicId(userData.getClinicId());
                    ur.setDescription(userData.getDescription());
                    ur.setDiploma(userData.getDiploma());
                    // Ajoutez le profileComplete
                    ur.setProfileComplete(userData.getProfileComplete() != null ? userData.getProfileComplete() : false);
                });
                log.info("userData: {}", userDataOpt.orElse(null));
                result.add(ur);
            }

        } catch (Exception e) {
            log.error("❌ Error fetching users by role {}: {}", roleName, e.getMessage());
        }

        return result;
    }

    public UserRegistrationRequest getUserById(String userId) {
        UsersResource users = keycloak.realm(appRealm).users();
        UserRepresentation user = users.get(userId).toRepresentation();


        Optional<UserData> userDataOpt = userDataRepository.findById(userId);

        UserRegistrationRequest ur = new UserRegistrationRequest();
        ur.setId(user.getId());
        ur.setUsername(user.getUsername());
        ur.setEmail(user.getEmail());
        ur.setFirstName(user.getFirstName());
        ur.setLastName(user.getLastName());
        ur.setEnabled(user.isEnabled());

        // Fill additional fields if exists
        userDataOpt.ifPresent(userData -> {
            ur.setPhoneNumber(userData.getPhoneNumber());
            ur.setProfilePhotoUrl(userData.getProfilePhotoUrl());
            ur.setSpeciality(userData.getSpeciality());
            ur.setClinicId(userData.getClinicId());
            ur.setDescription(userData.getDescription());
            ur.setDiploma(userData.getDiploma());
        });
        return ur;
    }
}