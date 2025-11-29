package com.symptomcheck.doctorservice.controllers;

import com.symptomcheck.doctorservice.dtos.dashboardDto.DoctorDashboardDTO;
import com.symptomcheck.doctorservice.dtos.dashboardDto.DoctorStatsDTO;
import com.symptomcheck.doctorservice.dtos.dashboardDto.DoctorServiceDTO;
import com.symptomcheck.doctorservice.dtos.dashboardDto.DoctorAvailabilityDTO;
import com.symptomcheck.doctorservice.dtos.dashboardDto.ProfileCompletionDTO;
import com.symptomcheck.doctorservice.services.DoctorDashboardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DoctorDashboardController Unit Tests")
class DoctorDashboardControllerTest {

    @Mock
    private DoctorDashboardService dashboardService;

    @InjectMocks
    private DoctorDashboardController controller;

    private UUID doctorId;
    private DoctorDashboardDTO dashboardDTO;

    @BeforeEach
    void setUp() {
        doctorId = UUID.randomUUID();

        DoctorStatsDTO stats = new DoctorStatsDTO(3L, 5L, true, 100);
        DoctorServiceDTO service1 = new DoctorServiceDTO(1L, "Checkup", "General Checkup", 50.0, 30, "Basic checkup");
        DoctorAvailabilityDTO availability1 = new DoctorAvailabilityDTO("1", "MONDAY", "09:00", "17:00", true);
        ProfileCompletionDTO profileCompletion = new ProfileCompletionDTO(true, true, true, 100);

        dashboardDTO = new DoctorDashboardDTO(stats, List.of(service1), List.of(availability1), profileCompletion);
    }

    @Test
    @DisplayName("GET /{doctorId} - should return full dashboard")
    void testGetDoctorDashboardSuccess() {
        when(dashboardService.getDoctorDashboard(doctorId)).thenReturn(dashboardDTO);

        ResponseEntity<DoctorDashboardDTO> response = controller.getDoctorDashboard(doctorId);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(dashboardDTO, response.getBody());
        verify(dashboardService).getDoctorDashboard(doctorId);
    }

    @Test
    @DisplayName("GET /{doctorId}/service-categories - should return list of categories")
    void testGetServiceCategoriesSuccess() {
        List<String> categories = List.of("Checkup", "Surgery");
        when(dashboardService.getServiceCategories(doctorId)).thenReturn(categories);

        ResponseEntity<List<String>> response = controller.getServiceCategories(doctorId);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(categories, response.getBody());
        verify(dashboardService).getServiceCategories(doctorId);
    }

    @Test
    @DisplayName("GET /{doctorId}/profile-status - should return profile completion status")
    void testIsProfileCompleteSuccess() {
        when(dashboardService.isProfileComplete(doctorId)).thenReturn(true);

        ResponseEntity<Boolean> response = controller.isProfileComplete(doctorId);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody());
        verify(dashboardService).isProfileComplete(doctorId);
    }

    @Test
    @DisplayName("GET /{doctorId}/services-count - should return total services count")
    void testGetServicesCountSuccess() {
        when(dashboardService.getTotalServicesCount(doctorId)).thenReturn(5L);

        ResponseEntity<Long> response = controller.getServicesCount(doctorId);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(5L, response.getBody());
        verify(dashboardService).getTotalServicesCount(doctorId);
    }

    @Test
    @DisplayName("GET /{doctorId}/availability-slots - should return total availability slots")
    void testGetAvailabilitySlotsSuccess() {
        when(dashboardService.getTotalAvailabilitySlots(doctorId)).thenReturn(10L);

        ResponseEntity<Long> response = controller.getAvailabilitySlots(doctorId);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(10L, response.getBody());
        verify(dashboardService).getTotalAvailabilitySlots(doctorId);
    }

    @Test
    @DisplayName("should handle exception gracefully for dashboard endpoint")
    void testGetDoctorDashboardException() {
        when(dashboardService.getDoctorDashboard(doctorId)).thenThrow(new RuntimeException("Database error"));

        ResponseEntity<DoctorDashboardDTO> response = controller.getDoctorDashboard(doctorId);

        assertEquals(500, response.getStatusCodeValue());
        assertNull(response.getBody());
        verify(dashboardService).getDoctorDashboard(doctorId);
    }
}
