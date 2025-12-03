package com.symptomcheck.clinicservice.dtos.admindashboarddto;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AdminClinicDto {
    private Long id;
    @NotBlank(message = "Clinic name is required")
    private String name;
    private String address;
    private String phone;
    private String websiteUrl;
    private String city;
    private String country;
    private Long doctorCount;
    private Long appointmentCount;
}
