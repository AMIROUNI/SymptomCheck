package com.symptomcheck.doctorservice.dtos.adminDashboardDto;

import lombok.Data;

import java.util.UUID;

@Data
public  class AvailabilityDto {
    private Long id;
    private String dayOfWeek;
    private String startTime;
    private String endTime;
}
