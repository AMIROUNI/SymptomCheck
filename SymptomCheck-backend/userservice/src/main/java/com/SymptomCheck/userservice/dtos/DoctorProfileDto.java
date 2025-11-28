package com.SymptomCheck.userservice.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DoctorProfileDto {
    @NotBlank(message = "ID is required")
    private String id;

    @NotBlank(message = "Speciality is required for doctors")
    private String speciality;

    private String description;

    @NotBlank(message = "Diploma is required for doctors")
    private String diploma;

    private String profilePhotoUrl;

    private Long clinicId;
}