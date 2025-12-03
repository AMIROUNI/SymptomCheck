package com.symptomcheck.appointmentservice.dtos.dashboarddto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentDashboardDTO {
    private AppointmentStatsDTO stats;
    private Map<String, Long> appointmentsByStatus;
    private Map<String, Long> weeklyAppointments;
}