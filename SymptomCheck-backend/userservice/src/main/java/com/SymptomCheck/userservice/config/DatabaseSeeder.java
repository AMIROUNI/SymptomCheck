package com.SymptomCheck.userservice.config;

import com.SymptomCheck.userservice.enums.UserRole;
import com.SymptomCheck.userservice.models.User;
import com.SymptomCheck.userservice.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {

    private final UserService userService;

    @Override
    public void run(String... args) throws Exception {
        // Créer un utilisateur patient COMPLET
        if (!userService.userExists("patient1")) {
            User patient = new User();
            patient.setId(1L); // ✅ Vous pouvez définir un ID manuellement ou le supprimer
            patient.setUsername("patient1");
            patient.setPasswordHash("password123");
            patient.setRole(UserRole.PATIENT);
            patient.setFirstName("John");
            patient.setLastName("Doe");
            patient.setEmail("john.doe@example.com");
            patient.setPhoneNumber("+1234567890");
            patient.setProfilePhotoUrl("/images/patient1.jpg");
            patient.setProfileComplete(true);
            patient.setClinicId(1L);
            patient.setCreatedAt(Instant.now());
            patient.setUpdatedAt(Instant.now());

            try {
                userService.registerUser(patient);
                System.out.println("✅ Patient user created in Keycloak with ALL attributes");
            } catch (Exception e) {
                System.out.println("❌ Failed to create patient: " + e.getMessage());
            }
        }

        // Créer un utilisateur docteur COMPLET
        if (!userService.userExists("doctor1")) {
            User doctor = new User();
            doctor.setId(2L); // ✅ ID manuel
            doctor.setUsername("doctor1");
            doctor.setPasswordHash("password123");
            doctor.setRole(UserRole.DOCTOR);
            doctor.setFirstName("Jane");
            doctor.setLastName("Smith");
            doctor.setEmail("jane.smith@example.com");
            doctor.setPhoneNumber("+0987654321");
            doctor.setProfilePhotoUrl("/images/doctor1.jpg");
            doctor.setProfileComplete(true);
            doctor.setClinicId(1L);
            doctor.setCreatedAt(Instant.now());
            doctor.setUpdatedAt(Instant.now());

            try {
                userService.registerUser(doctor);
                System.out.println("✅ Doctor user created in Keycloak with ALL attributes");
            } catch (Exception e) {
                System.out.println("❌ Failed to create doctor: " + e.getMessage());
            }
        }
    }
}