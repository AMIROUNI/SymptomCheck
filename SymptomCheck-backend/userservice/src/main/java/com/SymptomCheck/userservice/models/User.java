package com.SymptomCheck.userservice.models;

import com.SymptomCheck.userservice.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Long id; // ✅ Supprimé @Id @GeneratedValue

    @NotBlank
    private String username; // ✅ Supprimé @Column(unique = true)

    @NotBlank
    private String passwordHash;

    @NotNull
    private UserRole role = UserRole.PATIENT; // ✅ Supprimé @Enumerated

    private String firstName;
    private String lastName;

    @Email
    private String email;


}