package com.symptomcheck.appointmentservice.dtos.admindashboarddto;


import lombok.Data;
import java.time.LocalDateTime;
import java.util.Map;

@Data
public class AppointmentStatsDto {
    private Long totalAppointments;
    private Long pendingAppointments;
    private Long confirmedAppointments;
    private Long completedAppointments;
    private Long cancelledAppointments;
    private Long todayAppointments;
    private Long weeklyAppointments;
    private Map<String, Long> statusDistribution;
    private LocalDateTime lastUpdated;
}