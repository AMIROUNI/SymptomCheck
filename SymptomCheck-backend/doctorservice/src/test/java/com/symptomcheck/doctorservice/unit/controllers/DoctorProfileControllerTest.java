package com.symptomcheck.doctorservice.unit.controllers;

import com.symptomcheck.doctorservice.controllers.DoctorProfileController;
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
import static org.mockito.ArgumentMatchers.any;
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

    // ------------------------------------------------------------
    // TEST 1 : Profile status returns true
    // ------------------------------------------------------------
    @Test
    @DisplayName("GET /{doctorId}/profile-status - returns true when both availability and healthcare service exist")
    void testGetProfileStatusTrue() {

        when(availabilityService.existsByDoctorId(doctorId)).thenReturn(true);
        when(healthcareServiceService.existsByDoctorId(doctorId.toString())).thenReturn(true);

        ResponseEntity<Boolean> response = controller.getProfileStatus(doctorId.toString());

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody());

        verify(availabilityService).existsByDoctorId(doctorId);
        verify(healthcareServiceService).existsByDoctorId(doctorId.toString());
    }

    // ------------------------------------------------------------
    // TEST 2 : Profile status returns false
    // ------------------------------------------------------------
    @Test
    @DisplayName("GET /{doctorId}/profile-status - returns false when one service is missing")
    void testGetProfileStatusFalse() {

        when(availabilityService.existsByDoctorId(doctorId)).thenReturn(true);
        when(healthcareServiceService.existsByDoctorId(doctorId.toString())).thenReturn(false);

        ResponseEntity<Boolean> response = controller.getProfileStatus(doctorId.toString());

        assertEquals(200, response.getStatusCodeValue());
        assertFalse(response.getBody());

        verify(availabilityService).existsByDoctorId(doctorId);
        verify(healthcareServiceService).existsByDoctorId(doctorId.toString());
    }

    // ------------------------------------------------------------
    // TEST 3 : POST complete profile success
    // ------------------------------------------------------------
    @Test
    @DisplayName("POST /completeprofile - completes profile successfully")
    void testCompleteProfileSuccess() {

        when(availabilityService.createAvailabilityHealth(any()))
                .thenReturn(true); // or whatever your service returns

        ResponseEntity<?> response = controller.complete(availabilityHealthDto);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue((Boolean) response.getBody());

        verify(availabilityService).createAvailabilityHealth(availabilityHealthDto);
    }

    // ------------------------------------------------------------
    // TEST 4 : POST complete profile exception
    // ------------------------------------------------------------
    @Test
    @DisplayName("POST /completeprofile - handles exception and returns error message")
    void testCompleteProfileException() {

        doThrow(new RuntimeException("Database error"))
                .when(availabilityService).createAvailabilityHealth(any());

        ResponseEntity<?> response = controller.complete(availabilityHealthDto);

        assertEquals(500, response.getStatusCodeValue());
        assertEquals("Database error", response.getBody());

        verify(availabilityService).createAvailabilityHealth(availabilityHealthDto);
    }
}
