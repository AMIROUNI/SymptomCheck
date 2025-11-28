package com.symptomcheck.doctorservice.controllers;

import com.symptomcheck.doctorservice.dtos.adminDashboardDto.AdminDoctorDto;
import com.symptomcheck.doctorservice.dtos.adminDashboardDto.DoctorStatsDto;
import com.symptomcheck.doctorservice.services.AdminDashboardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AdminDashboardController Unit Tests")
class AdminDashboardControllerTest {

    @Mock
    private AdminDashboardService adminDashboardService;

    @InjectMocks
    private AdminDashboardController controller;

    private UUID doctorId;
    private AdminDoctorDto adminDoctor;
    private DoctorStatsDto stats;

    @BeforeEach
    void setUp() {
        doctorId = UUID.randomUUID();

        adminDoctor = new AdminDoctorDto();
        adminDoctor.setDoctorId(doctorId);
        adminDoctor.setSpeciality("Cardiology");
        adminDoctor.setDescription("Experienced cardiologist");
        adminDoctor.setStatus("PENDING");

        stats = new DoctorStatsDto();
        stats.setTotalDoctors(10L);
        stats.setPendingDoctors(2L);
        stats.setApprovedDoctors(7L);
        stats.setRejectedDoctors(1L);
        stats.setTotalServices(50L);
        stats.setDoctorsWithAvailability(8L);
        stats.setLastUpdated(LocalDateTime.now());
    }

    @Test
    @DisplayName("GET /dashboard/stats - should return doctor statistics")
    void testGetDashboardStats() {
        when(adminDashboardService.getDoctorStatistics()).thenReturn(stats);

        ResponseEntity<DoctorStatsDto> response = controller.getDashboardStats();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(stats, response.getBody());
        verify(adminDashboardService).getDoctorStatistics();
    }

    @Test
    @DisplayName("GET /doctors - should return all doctors")
    void testGetAllDoctors() {
        when(adminDashboardService.getAllDoctors()).thenReturn(List.of(adminDoctor));

        ResponseEntity<List<AdminDoctorDto>> response = controller.getAllDoctors();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        assertEquals(adminDoctor.getDoctorId(), response.getBody().get(0).getDoctorId());
        verify(adminDashboardService).getAllDoctors();
    }

    @Test
    @DisplayName("GET /doctors/speciality/{speciality} - should return doctors by speciality")
    void testGetDoctorsBySpeciality() {
        when(adminDashboardService.getDoctorsBySpeciality("Cardiology")).thenReturn(List.of(adminDoctor));

        ResponseEntity<List<AdminDoctorDto>> response = controller.getDoctorsBySpeciality("Cardiology");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        assertEquals("Cardiology", response.getBody().get(0).getSpeciality());
        verify(adminDashboardService).getDoctorsBySpeciality("Cardiology");
    }

    @Test
    @DisplayName("PUT /doctors/{doctorId}/status - should update doctor status")
    void testUpdateDoctorStatus() {
        when(adminDashboardService.updateDoctorStatus(doctorId, "APPROVED")).thenReturn(adminDoctor);

        ResponseEntity<AdminDoctorDto> response = controller.updateDoctorStatus(doctorId, "APPROVED");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(adminDoctor, response.getBody());
        verify(adminDashboardService).updateDoctorStatus(doctorId, "APPROVED");
    }
}
