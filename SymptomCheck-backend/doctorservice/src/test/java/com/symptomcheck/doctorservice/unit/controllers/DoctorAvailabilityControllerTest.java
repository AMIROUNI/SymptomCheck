package com.symptomcheck.doctorservice.controllers;

import com.symptomcheck.doctorservice.models.DoctorAvailability;
import com.symptomcheck.doctorservice.services.DoctorAvailabilityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DoctorAvailabilityController Unit Tests")
class DoctorAvailabilityControllerTest {

    @Mock
    private DoctorAvailabilityService doctorAvailabilityService;

    @InjectMocks
    private DoctorAvailabilityController controller;

    private UUID doctorId;
    private DoctorAvailability availability;

    @BeforeEach
    void setUp() {
        doctorId = UUID.randomUUID();

        availability = new DoctorAvailability();
        availability.setId(1L);
        availability.setDoctorId(doctorId);
        availability.setDaysOfWeek(List.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY));
        availability.setStartTime(LocalTime.of(9, 0));
        availability.setEndTime(LocalTime.of(17, 0));
    }

    @Test
    @DisplayName("GET /{doctorId} - should return doctor's availability")
    void testGetDoctorAvailabilityByDoctorId() {
        when(doctorAvailabilityService.getAvailabilityByDoctorId(doctorId))
                .thenReturn(List.of(availability));

        ResponseEntity<List<DoctorAvailability>> response =
                controller.getDoctorAvailabilityByDoctorId(doctorId);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        assertEquals(availability.getId(), response.getBody().get(0).getId());
        verify(doctorAvailabilityService).getAvailabilityByDoctorId(doctorId);
    }

    @Test
    @DisplayName("GET /isAvailable/{id}/{dateTime} - should return availability true")
    void testIsAvailableTrue() {
        LocalDateTime dateTime = LocalDateTime.of(2025, 11, 25, 10, 0);
        when(doctorAvailabilityService.isDoctorAvailable(doctorId, dateTime)).thenReturn(true);

        ResponseEntity<?> response = controller.isAvailable(doctorId, dateTime);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue((Boolean) response.getBody());
        verify(doctorAvailabilityService).isDoctorAvailable(doctorId, dateTime);
    }

    @Test
    @DisplayName("GET /isAvailable/{id}/{dateTime} - should handle exception gracefully")
    void testIsAvailableException() {
        LocalDateTime dateTime = LocalDateTime.of(2025, 11, 25, 10, 0);
        when(doctorAvailabilityService.isDoctorAvailable(doctorId, dateTime))
                .thenThrow(new RuntimeException("Database error"));

        ResponseEntity<?> response = controller.isAvailable(doctorId, dateTime);

        assertEquals(500, response.getStatusCodeValue());
        assertEquals("Database error", response.getBody());
        verify(doctorAvailabilityService).isDoctorAvailable(doctorId, dateTime);
    }

    @Test
    @DisplayName("GET /daily - should return daily availability slots")
    void testGetDailyAvailability() {
        LocalDate date = LocalDate.of(2025, 11, 25);
        List<String> slots = List.of("09:00-10:00", "10:00-11:00");
        when(doctorAvailabilityService.getAvailableSlotsForDate(doctorId, date)).thenReturn(slots);

        ResponseEntity<List<String>> response = controller.getDailyAvailability(doctorId, date);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(slots, response.getBody());
        verify(doctorAvailabilityService).getAvailableSlotsForDate(doctorId, date);
    }
}
