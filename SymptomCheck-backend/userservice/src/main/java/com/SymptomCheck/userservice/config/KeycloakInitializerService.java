package com.SymptomCheck.userservice.config;

import jakarta.annotation.PostConstruct;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class KeycloakInitializerService {

    private static final Logger log = LoggerFactory.getLogger(KeycloakInitializerService.class);

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

    @Value("${keycloak.app.roles}")
    private String appRoles;

    @Value("${keycloak.app.client-secret}")
    private String clientSecret;

    private Keycloak keycloak;

    @PostConstruct
    public void init() {
        try {
            keycloak = KeycloakBuilder.builder()
                    .serverUrl(serverUrl)
                    .realm(adminRealm)
                    .clientId(adminClientId)
                    .username(adminUsername)
                    .password(adminPassword)
                    .build();

            log.info("🚀 Starting Keycloak Initialization...");
            createRealmIfNotExists();
            createClientIfNotExists();
            createRolesIfNotExist();
            log.info("✅ Keycloak initialization complete!");
        } catch (Exception e) {
            log.error("❌ Keycloak initialization failed: {}", e.getMessage());
        }
    }

    private void createRealmIfNotExists() {
        try {
            boolean exists = keycloak.realms().findAll().stream()
                    .anyMatch(r -> r.getRealm().equals(appRealm));

            if (!exists) {
                RealmRepresentation realm = new RealmRepresentation();
                realm.setRealm(appRealm);
                realm.setEnabled(true);
                keycloak.realms().create(realm);
                log.info("🆕 Realm '{}' created", appRealm);
            } else {
                log.info("ℹ️ Realm '{}' already exists", appRealm);
            }
        } catch (Exception e) {
            log.error("Error creating realm: {}", e.getMessage());
        }
    }

    private void createClientIfNotExists() {
        try {
            List<ClientRepresentation> clients = keycloak.realm(appRealm).clients().findAll();

            boolean exists = clients.stream()
                    .anyMatch(c -> c.getClientId().equals(appClientId));

            if (!exists) {
                ClientRepresentation client = new ClientRepresentation();
                client.setClientId(appClientId);
                client.setName(appClientId);
                client.setEnabled(true);
                client.setPublicClient(false);
                client.setSecret(clientSecret);
                client.setDirectAccessGrantsEnabled(true);
                client.setServiceAccountsEnabled(true);
                client.setStandardFlowEnabled(true);

                // Configuration des URLs
                client.setRedirectUris(List.of("http://localhost:8081/*"));
                client.setWebOrigins(List.of("*"));

                keycloak.realm(appRealm).clients().create(client);
                log.info("🆕 Client '{}' created successfully", appClientId);
            } else {
                log.info("ℹ️ Client '{}' already exists", appClientId);
            }
        } catch (Exception e) {
            log.error("Error creating client: {}", e.getMessage());
        }
    }

    private void createRolesIfNotExist() {
        try {
            List<String> roles = Arrays.asList(appRoles.split(","));

            for (String roleName : roles) {
                roleName = roleName.trim();
                String finalRoleName = roleName;
                boolean exists = keycloak.realm(appRealm).roles().list().stream()
                        .anyMatch(r -> r.getName().equals(finalRoleName));

                if (!exists) {
                    RoleRepresentation role = new RoleRepresentation();
                    role.setName(roleName);
                    role.setDescription("Auto-created role: " + roleName);
                    keycloak.realm(appRealm).roles().create(role);
                    log.info("🆕 Role '{}' created", roleName);
                } else {
                    log.info("ℹ️ Role '{}' already exists", roleName);
                }
            }
        } catch (Exception e) {
            log.error("Error creating roles: {}", e.getMessage());
        }
    }
}