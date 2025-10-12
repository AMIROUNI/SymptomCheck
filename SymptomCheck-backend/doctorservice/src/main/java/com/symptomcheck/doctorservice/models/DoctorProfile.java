package com.symptomcheck.doctorservice.models;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "doctor_profiles")
public class DoctorProfile {
    @Id
    private Long doctorId; // même id que dans User Service

    private String speciality;
    private String diploma;
    @Column(length = 1000)
    private String description;
    private String clinicName; // info supplémentaire

    // services offerts : stocker en JSON ou table séparée
    @OneToMany(mappedBy = "doctorProfile", cascade = CascadeType.ALL)
    private List<HealthcareService> services;

}


