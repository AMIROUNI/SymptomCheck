package com.symptomcheck.doctorservice.services;

import com.symptomcheck.doctorservice.models.DoctorAvailability;
import com.symptomcheck.doctorservice.repositories.DoctorAvailabilityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DoctorAvailabilityServiceTest {

    @Mock
    private DoctorAvailabilityRepository doctorAvailabilityRepository;

    @InjectMocks
    private DoctorAvailabilityService doctorAvailabilityService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void isDoctorAvailable_shouldReturnTrue_whenDoctorHasAvailability() {
        // Arrange
        Long doctorId = 1L;
        LocalDateTime dateTime = LocalDateTime.of(2025, 10, 29, 10, 0); // mercredi 10h
        DoctorAvailability availability = new DoctorAvailability();
        availability.setDoctorId(doctorId);
        availability.setDayOfWeek(dateTime.getDayOfWeek());
        availability.setStartTime(LocalTime.of(9, 0));
        availability.setEndTime(LocalTime.of(12, 0));

        when(doctorAvailabilityRepository.findIfAvailable(
                doctorId,
                dateTime.getDayOfWeek(),
                dateTime.toLocalTime()
        )).thenReturn(Optional.of(availability));

        // Act
        boolean result = doctorAvailabilityService.isDoctorAvailable(doctorId, dateTime);

        // Assert
        assertTrue(result);
        verify(doctorAvailabilityRepository, times(1))
                .findIfAvailable(doctorId, dateTime.getDayOfWeek(), dateTime.toLocalTime());
    }

    @Test
    void isDoctorAvailable_shouldReturnFalse_whenDoctorHasNoAvailability() {
        // Arrange
        Long doctorId = 1L;
        LocalDateTime dateTime = LocalDateTime.of(2025, 10, 29, 20, 0); // mercredi 20h

        when(doctorAvailabilityRepository.findIfAvailable(
                doctorId,
                dateTime.getDayOfWeek(),
                dateTime.toLocalTime()
        )).thenReturn(Optional.empty());

        // Act
        boolean result = doctorAvailabilityService.isDoctorAvailable(doctorId, dateTime);

        // Assert
        assertFalse(result);
        verify(doctorAvailabilityRepository, times(1))
                .findIfAvailable(doctorId, dateTime.getDayOfWeek(), dateTime.toLocalTime());
    }
}
