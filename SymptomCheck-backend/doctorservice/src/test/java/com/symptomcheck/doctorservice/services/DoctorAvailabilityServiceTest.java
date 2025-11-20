package com.symptomcheck.doctorservice.services;

import com.symptomcheck.doctorservice.dtos.AvailabilityHealthDto;
import com.symptomcheck.doctorservice.models.DoctorAvailability;
import com.symptomcheck.doctorservice.models.HealthcareService;
import com.symptomcheck.doctorservice.repositories.DoctorAvailabilityRepository;
import com.symptomcheck.doctorservice.repositories.HealthcareServiceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DoctorAvailabilityServiceTest {

    @Mock
    private DoctorAvailabilityRepository availabilityRepository;

    @Mock
    private HealthcareServiceRepository healthcareRepo;

    @Mock
    private WebClient webClient;

    @InjectMocks
    private DoctorAvailabilityService availabilityService;

    private UUID doctorId;
    private LocalDateTime dateTime;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        doctorId = UUID.randomUUID();
        dateTime = LocalDateTime.of(2025, 11, 20, 10, 0);
    }

    @Nested
    @DisplayName("isDoctorAvailable method tests")
    class IsDoctorAvailableTests {

        @Test
        @DisplayName("should return true when doctor is available")
        void testDoctorIsAvailable() {
            DayOfWeek day = dateTime.getDayOfWeek();
            LocalTime time = dateTime.toLocalTime();

            DoctorAvailability availability = new DoctorAvailability();
            availability.setDoctorId(doctorId);
            availability.setDayOfWeek(day);
            availability.setStartTime(time.minusHours(1));
            availability.setEndTime(time.plusHours(1));

            when(availabilityRepository.findIfAvailable(doctorId, day, time))
                    .thenReturn(Optional.of(availability));

            assertTrue(availabilityService.isDoctorAvailable(doctorId, dateTime));
        }

        @Test
        @DisplayName("should return false when doctor is not available")
        void testDoctorIsNotAvailable() {
            when(availabilityRepository.findIfAvailable(any(), any(), any()))
                    .thenReturn(Optional.empty());

            assertFalse(availabilityService.isDoctorAvailable(doctorId, dateTime));
        }
    }

    @Nested
    @DisplayName("existsByDoctorId method tests")
    class ExistsByDoctorIdTests {

        @Test
        @DisplayName("should return true when doctor exists")
        void testDoctorExists() {
            when(availabilityRepository.existsByDoctorId(doctorId)).thenReturn(true);
            assertTrue(availabilityService.existsByDoctorId(doctorId));
        }

        @Test
        @DisplayName("should return false when doctor does not exist")
        void testDoctorDoesNotExist() {
            when(availabilityRepository.existsByDoctorId(doctorId)).thenReturn(false);
            assertFalse(availabilityService.existsByDoctorId(doctorId));
        }
    }

    @Nested
    @DisplayName("createAvailabilityHealth method tests")
    class CreateAvailabilityHealthTests {

        @Test
        @DisplayName("should create DoctorAvailability and HealthcareService")
        void testCreateAvailabilityHealth() {
            AvailabilityHealthDto dto = new AvailabilityHealthDto();
            dto.setDoctorId(doctorId);
            dto.setDayOfWeek(DayOfWeek.MONDAY);
            dto.setStartTime(LocalTime.of(9, 0));
            dto.setEndTime(LocalTime.of(12, 0));
            dto.setCategory("Heart");
            dto.setDescription("Heart checkup");
            dto.setName("Cardiology");
            dto.setPrice(150.0);
            dto.setImageUrl("image.png");
            dto.setDurationMinutes(30);

            availabilityService.createAvailabilityHealth(dto);

            verify(availabilityRepository, times(1)).save(any(DoctorAvailability.class));
            verify(healthcareRepo, times(1)).save(any(HealthcareService.class));
        }
    }
}
