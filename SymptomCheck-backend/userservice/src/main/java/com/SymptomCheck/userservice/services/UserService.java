package com.SymptomCheck.userservice.services;

import com.SymptomCheck.userservice.dtos.DoctorProfileDto;
import com.SymptomCheck.userservice.dtos.UserRegistrationRequest;
import com.SymptomCheck.userservice.dtos.UserUpdateDto;
import com.SymptomCheck.userservice.enums.UserRole;
import com.SymptomCheck.userservice.models.User;
import com.SymptomCheck.userservice.models.UserData;
import com.SymptomCheck.userservice.repositories.UserDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final KeycloakService keycloakService;
    private final UserDataRepository userDataRepository;
    private final LocalFileStorageService localFileStorageService;

    /**
     * Enregistre un utilisateur COMPLET dans Keycloak
     */
    public String registerMyUser(UserRegistrationRequest userRegistrationRequest,
                                 MultipartFile file) {
        try {
            User user = new User();
            user.setRole(UserRole.valueOf(userRegistrationRequest.getRole().toUpperCase()));
            user.setUsername(userRegistrationRequest.getUsername());
            user.setEmail(userRegistrationRequest.getEmail());
            user.setPasswordHash(userRegistrationRequest.getPassword());
            user.setFirstName(userRegistrationRequest.getFirstName());
            user.setLastName(userRegistrationRequest.getLastName());

            String userId = keycloakService.registerUser(user);

            UserData userData = new UserData();
            userData.setId(userId);
            userData.setPhoneNumber(userRegistrationRequest.getPhoneNumber());
            userData.setProfileComplete(false);
            userData.setDescription(userRegistrationRequest.getDescription());
            userData.setDiploma(userRegistrationRequest.getDiploma());
            userData.setClinicId(userRegistrationRequest.getClinicId());
            userData.setSpeciality(userRegistrationRequest.getSpeciality());
            userData.setProfilePhotoUrl(localFileStorageService.store(file));
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
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or empty.");
        }

        try {
            return keycloakService.userExists(username);
        } catch (Exception e) {
            throw new RuntimeException("Failed to check user existence", e);
        }
    }

    public Map<String, Object> getUserDetails(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }

        return keycloakService.getUserDetails(username);
    }

    public void disableUser(String userId, boolean isEnabled) {
        keycloakService.disableUser(userId, isEnabled);
    }

    public List<UserRegistrationRequest> getUsersByRole(String roleName) {
        return keycloakService.getUsersByRole(roleName);
    }

    public UserRegistrationRequest getUserById(String userId) {
        return keycloakService.getUserById(userId);
    }

    public UserRegistrationRequest updateUser(String userId, UserUpdateDto userUpdateDto) {
        try {
            log.info("🔄 Updating user basic info: {}", userId);

            // 1. Récupérer le rôle actuel de l'utilisateur
            UserRegistrationRequest currentUser = keycloakService.getUserById(userId);
            String currentRole = currentUser.getRole();
            log.info("ℹ️ Current user role: {}", currentRole);

            // 2. Mettre à jour uniquement les informations basiques dans Keycloak
            UserRepresentation updatedUser = keycloakService.updateUserBasicInfo(userId, userUpdateDto);

            // 3. Mettre à jour dans la base de données locale
            Optional<UserData> optionalUserData = userDataRepository.findById(userId);
            UserData userData;

            if (optionalUserData.isPresent()) {
                userData = optionalUserData.get();
                userData.setUpdatedAt(Instant.now());
            } else {
                userData = new UserData();
                userData.setId(userId);
                userData.setCreatedAt(Instant.now());
                userData.setUpdatedAt(Instant.now());
                userData.setProfileComplete(false);
            }

            userData.setPhoneNumber(userUpdateDto.getPhoneNumber());
            userDataRepository.save(userData);

            // 4. Retourner les données mises à jour
            UserRegistrationRequest response = new UserRegistrationRequest();
            response.setId(userId);
            response.setFirstName(userUpdateDto.getFirstName());
            response.setLastName(userUpdateDto.getLastName());
            response.setEmail(userUpdateDto.getEmail());
            response.setPhoneNumber(userUpdateDto.getPhoneNumber());
            response.setRole(currentRole); // ✅ Toujours utiliser le rôle actuel

            log.info("✅ User basic info updated successfully (role unchanged): {}", response);
            return response;

        } catch (Exception e) {
            log.error("❌ Error updating user: {}", e.getMessage());
            throw new RuntimeException("Failed to update user: " + e.getMessage());
        }
    }

    // Méthode séparée pour mettre à jour le rôle (si nécessaire)
    public void updateUserRole(String userId, String newRole) {
        try {
            log.info("🔄 Updating user role: {} -> {}", userId, newRole);
            keycloakService.updateUserRole(userId, newRole);
            log.info("✅ User role updated successfully");
        } catch (Exception e) {
            log.error("❌ Error updating user role: {}", e.getMessage());
            throw new RuntimeException("Failed to update user role: " + e.getMessage());
        }
    }

    /**
     * Compléter/mettre à jour le profil docteur
     */
    public UserData completeDoctorProfile(String userId, DoctorProfileDto doctorProfileDto) {
        try {
            log.info("🔄 Completing doctor profile for user: {}", userId);

            Optional<UserData> optionalUserData = userDataRepository.findById(userId);
            UserData userData;

            if (optionalUserData.isPresent()) {
                userData = optionalUserData.get();
                // ✅ CORRIGÉ : Utiliser Instant.now() au lieu de conversion
                userData.setUpdatedAt(Instant.now());
            } else {
                userData = new UserData();
                userData.setId(userId);
                // ✅ CORRIGÉ : Utiliser Instant.now() au lieu de conversion
                userData.setCreatedAt(Instant.now());
                userData.setUpdatedAt(Instant.now());
            }

            // Mettre à jour les champs du profil docteur
            userData.setSpeciality(doctorProfileDto.getSpeciality());
            userData.setDescription(doctorProfileDto.getDescription());
            userData.setDiploma(doctorProfileDto.getDiploma());
            userData.setClinicId(doctorProfileDto.getClinicId());

            // Mettre à jour la photo de profil si fournie
            if (doctorProfileDto.getProfilePhotoUrl() != null &&
                    !doctorProfileDto.getProfilePhotoUrl().isEmpty()) {
                userData.setProfilePhotoUrl(doctorProfileDto.getProfilePhotoUrl());
            }

            // Marquer le profil comme complet si tous les champs requis sont remplis
            boolean isProfileComplete = doctorProfileDto.getSpeciality() != null &&
                    !doctorProfileDto.getSpeciality().isEmpty() &&
                    doctorProfileDto.getDiploma() != null &&
                    !doctorProfileDto.getDiploma().isEmpty();
            userData.setProfileComplete(isProfileComplete);

            UserData savedUserData = userDataRepository.save(userData);

            log.info("✅ Doctor profile completed successfully: {}", savedUserData);
            return savedUserData;

        } catch (Exception e) {
            log.error("❌ Error completing doctor profile: {}", e.getMessage());
            throw new RuntimeException("Failed to complete doctor profile: " + e.getMessage());
        }
    }

    /**
     * Upload de photo de profil
     */
    public String uploadProfilePhoto(String userId, MultipartFile file) {
        try {
            log.info("📤 Uploading profile photo for user: {}", userId);

            if (file == null || file.isEmpty()) {
                throw new IllegalArgumentException("File cannot be null or empty");
            }

            // Sauvegarder le fichier
            String profilePhotoUrl = localFileStorageService.store(file);

            // Mettre à jour l'URL dans la base de données
            Optional<UserData> optionalUserData = userDataRepository.findById(userId);
            UserData userData;

            if (optionalUserData.isPresent()) {
                userData = optionalUserData.get();
                // ✅ CORRIGÉ : Utiliser Instant.now() au lieu de conversion
                userData.setUpdatedAt(Instant.now());
            } else {
                userData = new UserData();
                userData.setId(userId);
                // ✅ CORRIGÉ : Utiliser Instant.now() au lieu de conversion
                userData.setCreatedAt(Instant.now());
                userData.setUpdatedAt(Instant.now());
                userData.setProfileComplete(false);
            }

            userData.setProfilePhotoUrl(profilePhotoUrl);
            userDataRepository.save(userData);

            log.info("✅ Profile photo uploaded successfully: {}", profilePhotoUrl);
            return profilePhotoUrl;

        } catch (Exception e) {
            log.error("❌ Error uploading profile photo: {}", e.getMessage());
            throw new RuntimeException("Failed to upload profile photo: " + e.getMessage());
        }
    }

    // ✅ MÉTHODE UTILITAIRE pour convertir LocalDateTime en Instant (si vraiment nécessaire)
    private Instant toInstant(LocalDateTime localDateTime) {
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant();
    }
}