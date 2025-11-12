package com.SymptomCheck.userservice.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistrationRequest {

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    @Email
    @NotBlank
    private String email;

    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String profilePhotoUrl;


    @NotBlank
    private String role; // "PATIENT", "DOCTOR", "ADMIN"
    private String speciality;
    private  String description;
    private  String diploma;
   private Long clinicId;
}