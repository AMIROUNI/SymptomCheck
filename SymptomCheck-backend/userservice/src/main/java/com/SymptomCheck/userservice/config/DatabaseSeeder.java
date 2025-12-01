package com.SymptomCheck.userservice.config;

import com.SymptomCheck.userservice.dtos.UserRegistrationRequest;
import com.SymptomCheck.userservice.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {

    private final UserService userService;

    @Override
    public void run(String... args) throws Exception {
        // Créer un utilisateur patient COMPLET
        if (!userService.userExists("patient1")) {
            UserRegistrationRequest patient = new UserRegistrationRequest();
            patient.setUsername("patient1");
            patient.setPassword("password123");
            patient.setRole("PATIENT");
            patient.setFirstName("John");
            patient.setLastName("Doe");
            patient.setEmail("john.doe@example.com");


            try {
                userService.registerMyUser(patient,null);
                System.out.println("✅ Patient user created in Keycloak with ALL attributes");
            } catch (Exception e) {
                System.out.println("❌ Failed to create patient: " + e.getMessage());
            }
        }

        // Créer un utilisateur docteur COMPLET
        if (!userService.userExists("doctor1")) {
            UserRegistrationRequest doctor = new UserRegistrationRequest();
            doctor.setUsername("doctor1");
            doctor.setPassword("password123");
            doctor.setRole("DOCTOR");
            doctor.setFirstName("Jane");
            doctor.setLastName("Smith");
            doctor.setEmail("jane.smith@example.com");


            try {
                userService.registerMyUser(doctor,null);
                System.out.println("✅ Doctor user created in Keycloak with ALL attributes");
            } catch (Exception e) {
                System.out.println("❌ Failed to create doctor: " + e.getMessage());
            }
        }
    }
}