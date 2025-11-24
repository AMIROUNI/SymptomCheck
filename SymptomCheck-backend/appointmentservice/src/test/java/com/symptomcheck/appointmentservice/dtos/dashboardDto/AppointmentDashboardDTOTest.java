package com.symptomcheck.appointmentservice.dtos.dashboardDto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.util.Map;

class AppointmentDashboardDTOTest {

    @Test
    void testNoArgsConstructorAndSetters() {
        AppointmentDashboardDTO dto = new AppointmentDashboardDTO();

        AppointmentStatsDTO stats = new AppointmentStatsDTO(10L, 2L, 5L, 3L, 0L);
        Map<String, Long> appointmentsByStatus = Map.of("PENDING", 5L, "COMPLETED", 3L);
        Map<String, Long> weeklyAppointments = Map.of("MONDAY", 2L, "TUESDAY", 1L);

        dto.setStats(stats);
        dto.setAppointmentsByStatus(appointmentsByStatus);
        dto.setWeeklyAppointments(weeklyAppointments);

        assertEquals(stats, dto.getStats());
        assertEquals(appointmentsByStatus, dto.getAppointmentsByStatus());
        assertEquals(weeklyAppointments, dto.getWeeklyAppointments());
    }

    @Test
    void testAllArgsConstructor() {
        AppointmentStatsDTO stats = new AppointmentStatsDTO(15L, 4L, 7L, 4L, 0L);
        Map<String, Long> appointmentsByStatus = Map.of("CANCELLED", 1L);
        Map<String, Long> weeklyAppointments = Map.of("FRIDAY", 4L);

        AppointmentDashboardDTO dto =
                new AppointmentDashboardDTO(stats, appointmentsByStatus, weeklyAppointments);

        assertSame(stats, dto.getStats());
        assertSame(appointmentsByStatus, dto.getAppointmentsByStatus());
        assertSame(weeklyAppointments, dto.getWeeklyAppointments());
    }

    @Test
    void testEqualsAndHashCode() {
        AppointmentStatsDTO stats = new AppointmentStatsDTO(20L, 3L, 8L, 6L, 1L);
        Map<String, Long> map1 = Map.of("TEST", 1L);

        AppointmentDashboardDTO dto1 = new AppointmentDashboardDTO(stats, map1, map1);
        AppointmentDashboardDTO dto2 = new AppointmentDashboardDTO(stats, map1, map1);

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void testToString() {
        AppointmentDashboardDTO dto = new AppointmentDashboardDTO();
        assertNotNull(dto.toString());
        assertTrue(dto.toString().contains("AppointmentDashboardDTO"));
    }
}
