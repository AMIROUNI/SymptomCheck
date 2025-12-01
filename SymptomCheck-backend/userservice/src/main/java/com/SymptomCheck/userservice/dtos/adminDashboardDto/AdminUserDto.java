package com.SymptomCheck.userservice.dtos.adminDashboardDto;


import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data

public class AdminUserDto {
    private String id;
    private String phoneNumber;
    private String profilePhotoUrl;
    private boolean profileComplete;
    private Long clinicId;
    private String speciality;
    private String description;
    private String diploma;
    private Instant createdAt;
    private Instant updatedAt;
    private String role; // DOCTOR or PATIENT
}