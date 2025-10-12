package com.SymptomCheck.userservice.models;


import com.SymptomCheck.userservice.enums.UserRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank @Column(unique = true)
    private String username;

    @NotBlank
    private String passwordHash;

    @NotNull
    @Enumerated(EnumType.STRING)
    private UserRole role = UserRole.PATIENT;

    private String firstName;
    private String lastName;

    @Email
    private String email;

    private String phoneNumber;
    private String profilePhotoUrl;
    private boolean isProfileComplete = false;

    private Long clinicId; // référence à Clinic (autre service) par id

    private Instant createdAt = Instant.now();
    private Instant updatedAt;




}




