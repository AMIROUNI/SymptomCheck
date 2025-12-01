package com.SymptomCheck.userservice.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Data
@NoArgsConstructor @AllArgsConstructor
public class UserRegistrationRequest {
    private  String id;


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
   private  boolean enabled ;


    private Boolean profileComplete = false;
}