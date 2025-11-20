package com.symptomcheck.clinicservice.dtos.adminDashboardDto;


import lombok.Data;

@Data
public class AdminClinicDto {
    private Long id;
    private String name;
    private String address;
    private String phone;
    private String websiteUrl;
    private String city;
    private String country;
    private Long doctorCount;
    private Long appointmentCount;
}
