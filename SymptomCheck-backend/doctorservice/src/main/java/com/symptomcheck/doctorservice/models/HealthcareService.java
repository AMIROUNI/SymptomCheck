package com.symptomcheck.doctorservice.models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;


@Entity
@Table(name = "healthcare_services")
@Data
public class HealthcareService {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private UUID doctorId;
    private String name;
    @Column(length = 1000)
    private String description;
    private String category;
    private String imageUrl;
    private Integer durationMinutes;
    private Double price;



}
