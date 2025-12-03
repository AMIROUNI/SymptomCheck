package com.symptomcheck.appointmentservice.unit.dtos.adminDashboardDto;

import com.symptomcheck.appointmentservice.dtos.admindashboarddto.AppointmentStatsDto;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AppointmentStatsDtoTest {

    @Test
    void testGettersAndSetters() {
        AppointmentStatsDto dto = new AppointmentStatsDto();

        Long total = 100L;
        Long pending = 20L;
        Long confirmed = 30L;
        Long completed = 40L;
        Long cancelled = 10L;
        Long today = 5L;
        Long weekly = 25L;
        Map<String, Long> statusMap = Map.of(
                "PENDING", 20L,
                "CONFIRMED", 30L
        );
        LocalDateTime lastUpdated = LocalDateTime.now();

        dto.setTotalAppointments(total);
        dto.setPendingAppointments(pending);
        dto.setConfirmedAppointments(confirmed);
        dto.setCompletedAppointments(completed);
        dto.setCancelledAppointments(cancelled);
        dto.setTodayAppointments(today);
        dto.setWeeklyAppointments(weekly);
        dto.setStatusDistribution(statusMap);
        dto.setLastUpdated(lastUpdated);

        assertEquals(total, dto.getTotalAppointments());
        assertEquals(pending, dto.getPendingAppointments());
        assertEquals(confirmed, dto.getConfirmedAppointments());
        assertEquals(completed, dto.getCompletedAppointments());
        assertEquals(cancelled, dto.getCancelledAppointments());
        assertEquals(today, dto.getTodayAppointments());
        assertEquals(weekly, dto.getWeeklyAppointments());
        assertEquals(statusMap, dto.getStatusDistribution());
        assertEquals(lastUpdated, dto.getLastUpdated());
    }

    @Test
    void testEqualsAndHashCode() {
        Map<String, Long> statusMap = Map.of("PENDING", 10L);
        LocalDateTime now = LocalDateTime.now();

        AppointmentStatsDto dto1 = new AppointmentStatsDto();
        dto1.setTotalAppointments(100L);
        dto1.setPendingAppointments(20L);
        dto1.setConfirmedAppointments(30L);
        dto1.setCompletedAppointments(40L);
        dto1.setCancelledAppointments(10L);
        dto1.setTodayAppointments(5L);
        dto1.setWeeklyAppointments(25L);
        dto1.setStatusDistribution(statusMap);
        dto1.setLastUpdated(now);

        AppointmentStatsDto dto2 = new AppointmentStatsDto();
        dto2.setTotalAppointments(100L);
        dto2.setPendingAppointments(20L);
        dto2.setConfirmedAppointments(30L);
        dto2.setCompletedAppointments(40L);
        dto2.setCancelledAppointments(10L);
        dto2.setTodayAppointments(5L);
        dto2.setWeeklyAppointments(25L);
        dto2.setStatusDistribution(statusMap);
        dto2.setLastUpdated(now);

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void testToString() {
        AppointmentStatsDto dto = new AppointmentStatsDto();
        dto.setTotalAppointments(50L);
        dto.setPendingAppointments(10L);

        String toStringResult = dto.toString();

        assertNotNull(toStringResult);
        assertTrue(toStringResult.contains("AppointmentStatsDto"));
        assertTrue(toStringResult.contains("50"));
        assertTrue(toStringResult.contains("10"));
    }
}
