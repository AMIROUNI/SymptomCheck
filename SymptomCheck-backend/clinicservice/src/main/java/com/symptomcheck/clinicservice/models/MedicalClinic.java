package com.symptomcheck.clinicservice.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;



@Entity
@Table(name = "clinics")
@Data
public class MedicalClinic {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;
    private String address;
    private String phone;
    private String websiteUrl;
    private String city;
    private String country;

}
