package com.symptomcheck.doctorservice.dto;

import jakarta.persistence.Column;
import lombok.Data;

import java.util.UUID;


@Data
public class HealthcareServiceDto {

    private UUID doctorId;
    private String name;
    @Column(length = 1000)
    private String description;
    private String category;
    private Integer durationMinutes;
    private Double price;

}
