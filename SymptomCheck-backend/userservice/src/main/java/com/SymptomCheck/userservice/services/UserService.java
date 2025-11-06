package com.SymptomCheck.userservice.services;


import com.SymptomCheck.userservice.dtos.UserRegistrationRequest;
import com.SymptomCheck.userservice.enums.UserRole;
import com.SymptomCheck.userservice.models.User;
import com.SymptomCheck.userservice.models.UserData;
import com.SymptomCheck.userservice.repositories.UserDataRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Var;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor

public class UserService {

    private final KeycloakService keycloakService;
    private  final UserDataRepository userDataRepository;


    /**
     * Enregistre un utilisateur COMPLET dans Keycloak
     */
    public String registerMyUser(UserRegistrationRequest userRegistrationRequest) {
        try {
            User user = new User();
            user.setRole(UserRole.valueOf(userRegistrationRequest.getRole().toUpperCase()));
            user.setUsername(userRegistrationRequest.getUsername());
            user.setEmail(userRegistrationRequest.getEmail());
            user.setPasswordHash(userRegistrationRequest.getPassword());
            user.setFirstName(userRegistrationRequest.getFirstName());
            user.setLastName(userRegistrationRequest.getLastName());

            // This call is already blocking (waits until Keycloak finishes)
            String userId = keycloakService.registerUser(user);

            UserData userData = new UserData();
            userData.setId(userId);
            userData.setPhoneNumber(userRegistrationRequest.getPhoneNumber());
            userData.setProfileComplete(false);
            userData.setProfilePhotoUrl(userRegistrationRequest.getProfilePhotoUrl());
            userDataRepository.save(userData);

            return userId;
        } catch (Exception e) {
            throw new RuntimeException("Failed to register user in Keycloak", e);
        }
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