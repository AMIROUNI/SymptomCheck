package com.SymptomCheck.userservice.services;


import com.SymptomCheck.userservice.models.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {

    private final KeycloakService keycloakService;

    /**
     * Enregistre un utilisateur COMPLET dans Keycloak
     */
    public String registerUser(User user) {
        return keycloakService.registerUser(user);
    }

    /**
     * Vérifie si un utilisateur existe dans Keycloak
     */
    public boolean userExists(String username) {
        return keycloakService.userExists(username);
    }

    public Map<String, Object> getUserDetails(String username) {
        return keycloakService.getUserDetails(username);
    }
}