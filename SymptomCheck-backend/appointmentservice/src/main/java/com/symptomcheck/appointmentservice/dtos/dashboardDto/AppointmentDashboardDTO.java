package com.symptomcheck.appointmentservice.dtos.dashboardDto;

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