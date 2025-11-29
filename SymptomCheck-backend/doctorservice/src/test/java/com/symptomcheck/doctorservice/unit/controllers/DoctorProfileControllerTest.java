package com.symptomcheck.doctorservice.controllers;

import com.symptomcheck.doctorservice.dtos.AvailabilityHealthDto;
import com.symptomcheck.doctorservice.services.DoctorAvailabilityService;
import com.symptomcheck.doctorservice.services.HealthcareServiceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DoctorProfileController Unit Tests")
class DoctorProfileControllerTest {

    @Mock
    private DoctorAvailabilityService availabilityService;

    @Mock
    private HealthcareServiceService healthcareServiceService;

    @InjectMocks
    private DoctorProfileController controller;

    private UUID doctorId;
    private AvailabilityHealthDto availabilityHealthDto;

    @BeforeEach
    void setUp() {
        doctorId = UUID.randomUUID();

        availabilityHealthDto = new AvailabilityHealthDto();
        availabilityHealthDto.setDoctorId(doctorId);
        availabilityHealthDto.setDaysOfWeek(List.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY));
        availabilityHealthDto.setStartTime(LocalTime.of(9, 0));
        availabilityHealthDto.setEndTime(LocalTime.of(17, 0));
        availabilityHealthDto.setName("General Checkup");
        availabilityHealthDto.setDescription("Basic health check");
        availabilityHealthDto.setCategory("Checkup");
        availabilityHealthDto.setDurationMinutes(30);
        availabilityHealthDto.setPrice(50.0);
    }

    @Test
    @DisplayName("GET /{doctorId}/profile-status - should return true when both availability and healthcare service exist")
    void testGetProfileStatusTrue() {
        // Arrange
        when(availabilityService.existsByDoctorId(doctorId)).thenReturn(true);
        when(healthcareServiceService.existsByDoctorId(doctorId.toString())).thenReturn(true);

        // Act
        ResponseEntity<Boolean> response = controller.getProfileStatus(doctorId.toString());

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody());

        verify(availabilityService).existsByDoctorId(doctorId);
        verify(healthcareServiceService).existsByDoctorId(doctorId.toString());
    }

    @Test
    @DisplayName("GET /{doctorId}/profile-status - should return false when one service is missing")
    void testGetProfileStatusFalse() {
        when(availabilityService.existsByDoctorId(doctorId)).thenReturn(true);
        when(healthcareServiceService.existsByDoctorId(doctorId.toString())).thenReturn(false);

        ResponseEntity<Boolean> response = controller.getProfileStatus(doctorId.toString());

        assertEquals(200, response.getStatusCodeValue());
        assertFalse(response.getBody());

        verify(availabilityService).existsByDoctorId(doctorId);
        verify(healthcareServiceService).existsByDoctorId(doctorId.toString());
    }

    @Test
    @DisplayName("POST /completeprofile - should complete profile successfully")
    void testCompleteProfileSuccess() {
        // Arrange
        doNothing().when(availabilityService).createAvailabilityHealth(availabilityHealthDto);

        // Act
        ResponseEntity<?> response = controller.complete(availabilityHealthDto);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(true, response.getBody());

        verify(availabilityService).createAvailabilityHealth(availabilityHealthDto);
    }

    @Test
    @DisplayName("POST /completeprofile - should handle exception")
    void testCompleteProfileException() {
        // Arrange
        doThrow(new RuntimeException("Database error"))
                .when(availabilityService).createAvailabilityHealth(availabilityHealthDto);

        // Act
        ResponseEntity<?> response = controller.complete(availabilityHealthDto);

        // Assert
        assertEquals(500, response.getStatusCodeValue());
        assertEquals("Database error", response.getBody());

        verify(availabilityService).createAvailabilityHealth(availabilityHealthDto);
    }
}
