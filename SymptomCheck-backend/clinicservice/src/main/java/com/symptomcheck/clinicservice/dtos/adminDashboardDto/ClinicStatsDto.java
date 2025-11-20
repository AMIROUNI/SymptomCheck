package com.symptomcheck.clinicservice.dtos.adminDashboardDto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ClinicStatsDto {
    private Long totalClinics;
    private Long clinicsWithDoctors;
    private Long clinicsInEachCity;
    private LocalDateTime lastUpdated;
}
