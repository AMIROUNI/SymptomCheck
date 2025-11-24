package com.symptomcheck.appointmentservice.dtos.dashboardDto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class AppointmentStatsDTOTest {

    @Test
    void testNoArgsConstructorAndSetters() {
        AppointmentStatsDTO dto = new AppointmentStatsDTO();

        dto.setTotalAppointments(10L);
        dto.setTodayAppointments(2L);
        dto.setPendingAppointments(4L);
        dto.setCompletedAppointments(3L);
        dto.setCancelledAppointments(1L);

        assertEquals(10L, dto.getTotalAppointments());
        assertEquals(2L, dto.getTodayAppointments());
        assertEquals(4L, dto.getPendingAppointments());
        assertEquals(3L, dto.getCompletedAppointments());
        assertEquals(1L, dto.getCancelledAppointments());
    }

    @Test
    void testAllArgsConstructor() {
        AppointmentStatsDTO dto = new AppointmentStatsDTO(
                20L,
                5L,
                8L,
                6L,
                1L
        );

        assertEquals(20L, dto.getTotalAppointments());
        assertEquals(5L, dto.getTodayAppointments());
        assertEquals(8L, dto.getPendingAppointments());
        assertEquals(6L, dto.getCompletedAppointments());
        assertEquals(1L, dto.getCancelledAppointments());
    }

    @Test
    void testEqualsAndHashCode() {
        AppointmentStatsDTO dto1 = new AppointmentStatsDTO(10L, 3L, 5L, 2L, 0L);
        AppointmentStatsDTO dto2 = new AppointmentStatsDTO(10L, 3L, 5L, 2L, 0L);

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void testToString() {
        AppointmentStatsDTO dto = new AppointmentStatsDTO(1L, 1L, 1L, 1L, 1L);
        assertNotNull(dto.toString());
        assertTrue(dto.toString().contains("AppointmentStatsDTO"));
    }
}
