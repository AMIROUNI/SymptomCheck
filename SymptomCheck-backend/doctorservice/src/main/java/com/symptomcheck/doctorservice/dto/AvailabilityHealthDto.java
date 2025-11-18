package com.symptomcheck.doctorservice.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;

@Data

public class AvailabilityHealthDto {
    private UUID doctorId;
    @NotNull
    private DayOfWeek dayOfWeek;
    @NotNull
    private LocalTime startTime;
    @NotNull
    private LocalTime endTime;
    private String name;
    @Column(length = 1000)
    private String description;
    private String category;
    private String imageUrl;
    private Integer durationMinutes;
    private Double price;
}
