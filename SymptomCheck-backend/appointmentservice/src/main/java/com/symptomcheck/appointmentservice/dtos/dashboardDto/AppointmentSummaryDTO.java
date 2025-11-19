package com.symptomcheck.appointmentservice.dtos.dashboardDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public  class AppointmentSummaryDTO {
    private Long id;
    private LocalDateTime dateTime;
    private String patientName;
    private String status;
    private String serviceType;
}