package com.symptomcheck.doctorservice.models;

import jakarta.persistence.*;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

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
    public Long getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }

    public String getSpeciality() {
        return speciality;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }

    public String getDiploma() {
        return diploma;
    }

    public void setDiploma(String diploma) {
        this.diploma = diploma;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getClinicName() {
        return clinicName;
    }

    public void setClinicName(String clinicName) {
        this.clinicName = clinicName;
    }

    public List<HealthcareService> getServices() {
        return services;
    }

    public void setServices(List<HealthcareService> services) {
        this.services = services;
    }

}


