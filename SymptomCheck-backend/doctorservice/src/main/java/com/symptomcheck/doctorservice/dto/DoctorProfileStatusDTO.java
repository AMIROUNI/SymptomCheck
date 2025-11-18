package com.symptomcheck.doctorservice.dto;

import lombok.Data;

@Data
public class DoctorProfileStatusDTO {
    private boolean availabilityCompleted;
    private boolean healthcareServiceCompleted;
    private boolean profileCompleted;
}
