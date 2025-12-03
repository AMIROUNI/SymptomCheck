package com.symptomcheck.doctorservice.unit.services;

import com.symptomcheck.doctorservice.dtos.dashboarddto.*;
import com.symptomcheck.doctorservice.models.DoctorAvailability;
import com.symptomcheck.doctorservice.models.HealthcareService;
import com.symptomcheck.doctorservice.repositories.DoctorAvailabilityRepository;
import com.symptomcheck.doctorservice.repositories.HealthcareServiceRepository;
import com.symptomcheck.doctorservice.services.DoctorDashboardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DoctorDashboardServiceTest {

    private DoctorAvailabilityRepository availabilityRepository;
    private HealthcareServiceRepository healthcareServiceRepository;
    private DoctorDashboardService dashboardService;
    private UUID doctorId;

    @BeforeEach
    void setUp() {
        availabilityRepository = mock(DoctorAvailabilityRepository.class);
        healthcareServiceRepository = mock(HealthcareServiceRepository.class);
        dashboardService = new DoctorDashboardService(availabilityRepository, healthcareServiceRepository);
        doctorId = UUID.randomUUID();
        when(availabilityRepository.existsByDoctorId(doctorId)).thenReturn(true);
        when(healthcareServiceRepository.existsByDoctorId(doctorId)).thenReturn(true);
    }

    @Nested
    @DisplayName("getDoctorDashboard")
    class GetDoctorDashboard {

        @BeforeEach
        void setupMocks() {
            DoctorAvailability availability = new DoctorAvailability();
            availability.setId(1L);
            availability.setDaysOfWeek(List.of(DayOfWeek.MONDAY));
            availability.setStartTime(LocalTime.of(9, 0));
            availability.setEndTime(LocalTime.of(12, 0));

            HealthcareService service = new HealthcareService();
            service.setId(1L);
            service.setName("Cardiology");
            service.setCategory("Heart");
            service.setPrice(150.0);
            service.setDurationMinutes(30);
            service.setDescription("Heart checkup");

            when(availabilityRepository.findByDoctorId(doctorId)).thenReturn(List.of(availability));
            when(availabilityRepository.existsByDoctorId(doctorId)).thenReturn(true);
            when(availabilityRepository.countByDoctorId(doctorId)).thenReturn(1L);

            when(healthcareServiceRepository.findByDoctorId(doctorId)).thenReturn(List.of(service));
            when(healthcareServiceRepository.existsByDoctorId(doctorId)).thenReturn(true);
            when(healthcareServiceRepository.countByDoctorId(doctorId)).thenReturn(1L);
        }

        @Test
        @DisplayName("should build dashboard correctly")
        void testGetDoctorDashboard() {
            DoctorDashboardDTO dashboard = dashboardService.getDoctorDashboard(doctorId);

            assertNotNull(dashboard);
            assertEquals(1, dashboard.getServices().size());
            assertEquals(1, dashboard.getAvailability().size());
            assertTrue(dashboard.getStats().getIsProfileComplete());
            assertEquals(100, dashboard.getStats().getCompletionPercentage());
        }
    }

    @Nested
    @DisplayName("getDoctorServices")
    class GetDoctorServices {

        @BeforeEach
        void setupMocks() {
            HealthcareService service = new HealthcareService();
            service.setId(2L);
            service.setName("Cardiology");
            service.setCategory("Heart");
            service.setPrice(150.0);
            service.setDurationMinutes(30);
            service.setDescription("Heart checkup");

            when(healthcareServiceRepository.findByDoctorId(doctorId)).thenReturn(List.of(service));
        }

        @Test
        @DisplayName("should return doctor services")
        void testGetDoctorServices() {
            UUID doctorId = UUID.randomUUID();

            HealthcareService service = new HealthcareService();
            service.setId(1L);
            service.setName("Cardiology");
            service.setCategory("Heart");
            service.setPrice(150.0);
            service.setDurationMinutes(30);
            service.setDescription("Heart checkup");

            when(healthcareServiceRepository.findByDoctorId(doctorId))
                    .thenReturn(List.of(service));

            var services = dashboardService.getDoctorServices(doctorId);

            assertEquals(1, services.size());
            assertEquals("Cardiology", services.get(0).getName());
        }

    }

    @Nested
    @DisplayName("getDoctorAvailability")
    class GetDoctorAvailability {

        @BeforeEach
        void setupMocks() {
            DoctorAvailability availability = new DoctorAvailability();
            availability.setId(1L);
            availability.setDaysOfWeek(List.of(DayOfWeek.MONDAY));
            availability.setStartTime(LocalTime.of(9, 0));
            availability.setEndTime(LocalTime.of(12, 0));

            when(availabilityRepository.findByDoctorId(doctorId)).thenReturn(List.of(availability));
        }

        @Test
        @DisplayName("should return doctor availability")
        void testGetDoctorAvailability() {
            var availability = dashboardService.getDoctorAvailability(doctorId);
            assertEquals(1, availability.size());
            assertEquals("MONDAY", availability.get(0).getDayOfWeek());
        }
    }

    @Nested
    @DisplayName("getProfileCompletion")
    class GetProfileCompletion {

        @BeforeEach
        void setupMocks() {
            when(availabilityRepository.existsByDoctorId(doctorId)).thenReturn(true);
            when(healthcareServiceRepository.existsByDoctorId(doctorId)).thenReturn(true);
        }

        @Test
        @DisplayName("should calculate profile completion correctly")
        void testGetProfileCompletion() {
            ProfileCompletionDTO profile = dashboardService.getProfileCompletion(doctorId);
            assertEquals(100, profile.getCompletionPercentage());
        }
    }
}
