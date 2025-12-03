package com.symptomcheck.doctorservice.dtos.admindashboarddto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DoctorStatsDto {
    private Long totalDoctors;
    private Long pendingDoctors;
    private Long approvedDoctors;
    private Long rejectedDoctors;
    private Long totalServices;
    private Long doctorsWithAvailability;
    private LocalDateTime lastUpdated;
}