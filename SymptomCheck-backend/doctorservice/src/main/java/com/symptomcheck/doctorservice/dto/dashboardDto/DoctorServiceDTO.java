package com.symptomcheck.doctorservice.dto.dashboardDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public  class DoctorServiceDTO {
    private Long id;
    private String name;
    private String category;
    private Double price;
    private Integer duration;
    private String description;
}