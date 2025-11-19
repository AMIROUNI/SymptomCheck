package com.symptomcheck.doctorservice.dto.dashboardDto;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorDashboardDTO {
    private DoctorStatsDTO stats;
    private List<DoctorServiceDTO> services;
    private List<DoctorAvailabilityDTO> availability;
    private ProfileCompletionDTO profileCompletion;
}

