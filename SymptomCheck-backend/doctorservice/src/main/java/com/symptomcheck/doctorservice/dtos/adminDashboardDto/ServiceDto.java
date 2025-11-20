package com.symptomcheck.doctorservice.dtos.adminDashboardDto;

import lombok.Data;

@Data
public class ServiceDto {
    private Long id;
    private String name;
    private String description;
    private String category;
    private Double price;
    private Integer durationMinutes;
}