package com.symptomcheck.doctorservice.dtos.admindashboarddto;

import lombok.Data;


@Data
public  class AvailabilityDto {
    private Long id;
    private String dayOfWeek;
    private String startTime;
    private String endTime;
}
