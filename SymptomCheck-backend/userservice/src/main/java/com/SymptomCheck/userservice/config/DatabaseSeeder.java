package com.SymptomCheck.userservice.config;

import com.SymptomCheck.userservice.enums.UserRole;
import com.SymptomCheck.userservice.models.User;
import com.SymptomCheck.userservice.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {

    private final UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        User patient = new User();
        patient.setUsername("john_doe");
        patient.setPasswordHash("12345");
        patient.setRole(UserRole.PATIENT);
        patient.setFirstName("John");
        patient.setLastName("Doe");
        patient.setEmail("john@example.com");
        patient.setPhoneNumber("1234567890");
        userRepository.save(patient);
    }
}
