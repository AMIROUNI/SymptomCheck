package com.symptomcheck.doctorservice.dtos.dashboardDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorStatsDTO {
    private Long totalServices;
    private Long totalAvailabilitySlots;
    private Boolean isProfileComplete;
    private Integer completionPercentage;
}