package com.symptomcheck.doctorservice.dtos.dashboarddto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public  class ProfileCompletionDTO {
    private Boolean hasAvailability;
    private Boolean hasServices;
    private Boolean hasBasicInfo;
    private Integer completionPercentage;
}