package com.symptomcheck.doctorservice.dtos.admindashboarddto;


import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
public class AdminDoctorDto {
    private UUID doctorId;
    private String speciality;
    private String description;
    private String status; // PENDING, APPROVED, REJECTED
    private Double rating;
    private Integer totalReviews;
    private List<ServiceDto> services;
    private List<AvailabilityDto> availabilities;
}