package com.symptomcheck.clinicservice.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class MedicalClinicDto {
    @NotBlank
    private String name;
    private String address;
    private String phone;
    private String websiteUrl;
    private String city;
    private String country;
}
