package com.symptomcheck.doctorservice.dto.dashboardDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public  class DoctorAvailabilityDTO {
    private String id;
    private String dayOfWeek;
    private String startTime;
    private String endTime;
    private Boolean isActive;
}