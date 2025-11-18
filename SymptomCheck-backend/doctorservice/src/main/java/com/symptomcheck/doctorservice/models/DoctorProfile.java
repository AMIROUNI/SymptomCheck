package com.symptomcheck.doctorservice.models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "doctor_profiles")
@Data
public class DoctorProfile {
    @Id
    private UUID doctorId; // même id que dans User Service
    private String clinicName; // info supplémentaire

    // services offerts : stocker en JSON ou table séparée


}


