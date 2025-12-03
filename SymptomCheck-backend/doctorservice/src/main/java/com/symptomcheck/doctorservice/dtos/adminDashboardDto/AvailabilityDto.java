package com.symptomcheck.doctorservice.dtos.adminDashboardDto;

import lombok.Data;


@Data
public  class AvailabilityDto {
    private Long id;
    private String dayOfWeek;
    private String startTime;
    private String endTime;
}
