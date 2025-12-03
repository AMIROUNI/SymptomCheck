package com.symptomcheck.doctorservice.unit.services;

import com.symptomcheck.doctorservice.dtos.admindashboarddto.AdminDoctorDto;
import com.symptomcheck.doctorservice.dtos.admindashboarddto.DoctorStatsDto;
import com.symptomcheck.doctorservice.models.DoctorAvailability;
import com.symptomcheck.doctorservice.models.HealthcareService;
import com.symptomcheck.doctorservice.repositories.DoctorAvailabilityRepository;
import com.symptomcheck.doctorservice.repositories.HealthcareServiceRepository;
import com.symptomcheck.doctorservice.services.AdminDashboardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminDashboardServiceTest {

    @Mock
    private HealthcareServiceRepository healthcareRepo;

    @Mock
    private DoctorAvailabilityRepository availabilityRepo;

    @InjectMocks
    private AdminDashboardService adminService;

    private UUID doctorId;
    private HealthcareService service;
    private DoctorAvailability availability;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        doctorId = UUID.randomUUID();

        service = new HealthcareService();
        service.setId(1L);
        service.setDoctorId(doctorId);
        service.setName("Cardiology");
        service.setCategory("Heart");
        service.setPrice(150.0);
        service.setDurationMinutes(30);
        service.setDescription("Heart checkup");

        // CORRECT: Initialize List<DayOfWeek> properly
        List<DayOfWeek> days = Arrays.asList(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY);

        availability = new DoctorAvailability();
        availability.setId(1L);
        availability.setDoctorId(doctorId);
        availability.setDaysOfWeek(days); // Set the list
        availability.setStartTime(LocalTime.of(9, 0));
        availability.setEndTime(LocalTime.of(12, 0));
    }
    @Nested
    @DisplayName("getDoctorStatistics method tests")
    class GetDoctorStatisticsTests {

        @Test
        @DisplayName("should return correct statistics")
        void testGetDoctorStatistics() {
            when(healthcareRepo.findAll()).thenReturn(List.of(service));
            when(availabilityRepo.countDoctorsWithAvailability()).thenReturn(1L);
            when(healthcareRepo.count()).thenReturn(1L);

            DoctorStatsDto stats = adminService.getDoctorStatistics();

            assertEquals(1L, stats.getTotalDoctors());
            assertEquals(1L, stats.getApprovedDoctors());
            assertEquals(0L, stats.getRejectedDoctors());
            assertEquals(1L, stats.getTotalServices());
            assertEquals(1L, stats.getDoctorsWithAvailability());
            assertNotNull(stats.getLastUpdated());
        }
    }

    @Nested
    @DisplayName("getAllDoctors method tests")
    class GetAllDoctorsTests {

        @Test
        @DisplayName("should return all doctors with services and availability")
        void testGetAllDoctors() {
            when(healthcareRepo.findAll()).thenReturn(List.of(service));
            when(availabilityRepo.findByDoctorId(doctorId)).thenReturn(List.of(availability));

            List<AdminDoctorDto> doctors = adminService.getAllDoctors();

            assertEquals(1, doctors.size());
            AdminDoctorDto dto = doctors.get(0);
            assertEquals(doctorId, dto.getDoctorId());
            assertEquals("Heart", dto.getSpeciality());
            assertEquals(1, dto.getServices().size());
            assertEquals(1, dto.getAvailabilities().size());
        }
    }

    @Nested
    @DisplayName("getDoctorsBySpeciality method tests")
    class GetDoctorsBySpecialityTests {

        @Test
        @DisplayName("should filter doctors by speciality")
        void testGetDoctorsBySpeciality() {
            when(healthcareRepo.findAll()).thenReturn(List.of(service));
            when(availabilityRepo.findByDoctorId(doctorId)).thenReturn(List.of(availability));

            List<AdminDoctorDto> doctors = adminService.getDoctorsBySpeciality("Heart");

            assertEquals(1, doctors.size());
            assertEquals(doctorId, doctors.get(0).getDoctorId());
        }
    }

    @Nested
    @DisplayName("updateDoctorStatus method tests")
    class UpdateDoctorStatusTests {

        @Test
        @DisplayName("should return updated doctor DTO")
        void testUpdateDoctorStatus() {
            when(healthcareRepo.findByDoctorId(doctorId)).thenReturn(List.of(service));
            when(availabilityRepo.findByDoctorId(doctorId)).thenReturn(List.of(availability));

            AdminDoctorDto dto = adminService.updateDoctorStatus(doctorId, "APPROVED");

            assertEquals(doctorId, dto.getDoctorId());
            assertEquals("APPROVED", dto.getStatus());
            assertEquals(1, dto.getServices().size());
            assertEquals(1, dto.getAvailabilities().size());
        }
    }
}
