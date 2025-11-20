package com.symptomcheck.appointmentservice.dtos.dashboardDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public  class AppointmentStatsDTO {
    private Long totalAppointments;
    private Long todayAppointments;
    private Long pendingAppointments;
    private Long completedAppointments;
    private Long cancelledAppointments;
}
