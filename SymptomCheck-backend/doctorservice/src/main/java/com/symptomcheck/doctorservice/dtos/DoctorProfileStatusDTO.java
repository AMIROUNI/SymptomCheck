package com.symptomcheck.doctorservice.dtos;

import lombok.Data;

@Data
public class DoctorProfileStatusDTO {
    private boolean availabilityCompleted;
    private boolean healthcareServiceCompleted;
    private boolean profileCompleted;
}
