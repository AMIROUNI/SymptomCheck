package com.symptomcheck.appointmentservice.unit.controllers;

import com.symptomcheck.appointmentservice.controllers.AdminDashboardController;
import com.symptomcheck.appointmentservice.dtos.admindashboarddto.AdminAppointmentDto;
import com.symptomcheck.appointmentservice.dtos.admindashboarddto.AppointmentStatsDto;
import com.symptomcheck.appointmentservice.services.AdminDashboardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminDashboardControllerTest {

    @Mock
    private AdminDashboardService adminDashboardService;

    @InjectMocks
    private AdminDashboardController adminDashboardController;

    private AppointmentStatsDto mockStatsDto;
    private List<AdminAppointmentDto> mockAppointments;
    private AdminAppointmentDto mockAppointmentDto;
    private UUID mockDoctorId;

    @BeforeEach
    void setUp() {
        // Setup mock AppointmentStatsDto
        mockStatsDto = new AppointmentStatsDto();
        mockStatsDto.setTotalAppointments(100L);
        mockStatsDto.setPendingAppointments(20L);
        mockStatsDto.setConfirmedAppointments(30L);
        mockStatsDto.setCompletedAppointments(40L);
        mockStatsDto.setCancelledAppointments(10L);
        mockStatsDto.setTodayAppointments(5L);
        mockStatsDto.setWeeklyAppointments(25L);

        Map<String, Long> statusDistribution = new HashMap<>();
        statusDistribution.put("PENDING", 20L);
        statusDistribution.put("CONFIRMED", 30L);
        statusDistribution.put("COMPLETED", 40L);
        statusDistribution.put("CANCELLED", 10L);
        mockStatsDto.setStatusDistribution(statusDistribution);
        mockStatsDto.setLastUpdated(LocalDateTime.now());

        // Setup mock AdminAppointmentDto
        mockAppointmentDto = new AdminAppointmentDto();
        mockAppointmentDto.setId(1L);
        mockAppointmentDto.setDateTime(LocalDateTime.now().plusDays(1));
        mockAppointmentDto.setPatientId(UUID.randomUUID().toString());
        mockAppointmentDto.setDoctorId(UUID.randomUUID().toString());
        mockAppointmentDto.setStatus("PENDING");
        mockAppointmentDto.setDescription("Regular checkup");
        mockAppointmentDto.setPaymentTransactionId(12345L);
        mockAppointmentDto.setCreatedAt(Instant.now().minus(Duration.ofDays(2)));
        mockAppointmentDto.setUpdatedAt(Instant.now());

        // Setup mock appointments list
        mockAppointments = Arrays.asList(mockAppointmentDto);

        // Setup mock doctor ID
        mockDoctorId = UUID.randomUUID();
    }

    @Test
    void getDashboardStats_ShouldReturnStats() {
        // Arrange
        when(adminDashboardService.getAppointmentStatistics()).thenReturn(mockStatsDto);

        // Act
        ResponseEntity<AppointmentStatsDto> response = adminDashboardController.getDashboardStats();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        AppointmentStatsDto body = response.getBody();
        assertEquals(mockStatsDto.getTotalAppointments(), body.getTotalAppointments());
        assertEquals(mockStatsDto.getPendingAppointments(), body.getPendingAppointments());
        assertEquals(mockStatsDto.getTodayAppointments(), body.getTodayAppointments());
        assertNotNull(body.getLastUpdated());
        assertNotNull(body.getStatusDistribution());

        verify(adminDashboardService, times(1)).getAppointmentStatistics();
    }

    @Test
    void getAllAppointments_ShouldReturnAllAppointments() {
        // Arrange
        when(adminDashboardService.getAllAppointments()).thenReturn(mockAppointments);

        // Act
        ResponseEntity<List<AdminAppointmentDto>> response = adminDashboardController.getAllAppointments();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());

        AdminAppointmentDto firstAppointment = response.getBody().get(0);
        assertEquals(mockAppointmentDto.getId(), firstAppointment.getId());
        assertEquals(mockAppointmentDto.getStatus(), firstAppointment.getStatus());

        verify(adminDashboardService, times(1)).getAllAppointments();
    }

    @Test
    void getAllAppointments_WhenEmptyList_ShouldReturnEmptyList() {
        // Arrange
        when(adminDashboardService.getAllAppointments()).thenReturn(Collections.emptyList());

        // Act
        ResponseEntity<List<AdminAppointmentDto>> response = adminDashboardController.getAllAppointments();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());

        verify(adminDashboardService, times(1)).getAllAppointments();
    }

    @Test
    void getAppointmentsByStatus_ShouldReturnFilteredAppointments() {
        // Arrange
        String status = "PENDING";
        when(adminDashboardService.getAppointmentsByStatus(eq(status))).thenReturn(mockAppointments);

        // Act
        ResponseEntity<List<AdminAppointmentDto>> response =
                adminDashboardController.getAppointmentsByStatus(status);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());

        AdminAppointmentDto firstAppointment = response.getBody().get(0);
        assertEquals(mockAppointmentDto.getStatus(), firstAppointment.getStatus());

        verify(adminDashboardService, times(1)).getAppointmentsByStatus(eq(status));
    }

    @Test
    void getAppointmentsByDateRange_ShouldReturnDateFilteredAppointments() {
        // Arrange
        LocalDateTime start = LocalDateTime.now().minusDays(7);
        LocalDateTime end = LocalDateTime.now().plusDays(7);

        when(adminDashboardService.getAppointmentsByDateRange(eq(start), eq(end)))
                .thenReturn(mockAppointments);

        // Act
        ResponseEntity<List<AdminAppointmentDto>> response =
                adminDashboardController.getAppointmentsByDateRange(start, end);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());

        verify(adminDashboardService, times(1))
                .getAppointmentsByDateRange(eq(start), eq(end));
    }

    @Test
    void getAppointmentsByDoctor_ShouldReturnDoctorAppointments() {
        // Arrange
        when(adminDashboardService.getAppointmentsByDoctor(eq(mockDoctorId)))
                .thenReturn(mockAppointments);

        // Act
        ResponseEntity<List<AdminAppointmentDto>> response =
                adminDashboardController.getAppointmentsByDoctor(mockDoctorId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());

        AdminAppointmentDto firstAppointment = response.getBody().get(0);
        assertEquals(mockAppointmentDto.getDoctorId(), firstAppointment.getDoctorId());

        verify(adminDashboardService, times(1))
                .getAppointmentsByDoctor(eq(mockDoctorId));
    }

    @Test
    void getAppointmentsByDoctor_WithInvalidDoctorId_ShouldReturnEmptyList() {
        // Arrange
        UUID invalidDoctorId = UUID.randomUUID();
        when(adminDashboardService.getAppointmentsByDoctor(eq(invalidDoctorId)))
                .thenReturn(Collections.emptyList());

        // Act
        ResponseEntity<List<AdminAppointmentDto>> response =
                adminDashboardController.getAppointmentsByDoctor(invalidDoctorId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());

        verify(adminDashboardService, times(1))
                .getAppointmentsByDoctor(eq(invalidDoctorId));
    }

    @Test
    void updateAppointmentStatus_ShouldReturnUpdatedAppointment() {
        // Arrange
        Long appointmentId = 1L;
        String newStatus = "CONFIRMED";

        AdminAppointmentDto updatedAppointmentDto = new AdminAppointmentDto();
        updatedAppointmentDto.setId(appointmentId);
        updatedAppointmentDto.setStatus(newStatus);
        updatedAppointmentDto.setDateTime(mockAppointmentDto.getDateTime());
        updatedAppointmentDto.setPatientId(mockAppointmentDto.getPatientId());
        updatedAppointmentDto.setDoctorId(mockAppointmentDto.getDoctorId());

        when(adminDashboardService.updateAppointmentStatus(eq(appointmentId), eq(newStatus)))
                .thenReturn(updatedAppointmentDto);

        // Act
        ResponseEntity<AdminAppointmentDto> response =
                adminDashboardController.updateAppointmentStatus(appointmentId, newStatus);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        AdminAppointmentDto body = response.getBody();
        assertEquals(appointmentId, body.getId());
        assertEquals(newStatus, body.getStatus());

        verify(adminDashboardService, times(1))
                .updateAppointmentStatus(eq(appointmentId), eq(newStatus));
    }

    @Test
    void updateAppointmentStatus_WithInvalidAppointmentId_ShouldThrowException() {
        // Arrange
        Long invalidAppointmentId = 999L;
        String newStatus = "CONFIRMED";

        when(adminDashboardService.updateAppointmentStatus(eq(invalidAppointmentId), eq(newStatus)))
                .thenThrow(new RuntimeException("Appointment not found"));

        // Act & Assert
        assertThrows(RuntimeException.class, () ->
                adminDashboardController.updateAppointmentStatus(invalidAppointmentId, newStatus));

        verify(adminDashboardService, times(1))
                .updateAppointmentStatus(eq(invalidAppointmentId), eq(newStatus));
    }

    @Test
    void updateAppointmentStatus_WithInvalidStatus_ShouldThrowException() {
        // Arrange
        Long appointmentId = 1L;
        String invalidStatus = "INVALID_STATUS";

        when(adminDashboardService.updateAppointmentStatus(eq(appointmentId), eq(invalidStatus)))
                .thenThrow(new IllegalArgumentException("Invalid status"));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                adminDashboardController.updateAppointmentStatus(appointmentId, invalidStatus));

        verify(adminDashboardService, times(1))
                .updateAppointmentStatus(eq(appointmentId), eq(invalidStatus));
    }

    @Test
    void getAppointmentsByStatus_WithInvalidStatus_ShouldThrowException() {
        // Arrange
        String invalidStatus = "INVALID";

        when(adminDashboardService.getAppointmentsByStatus(eq(invalidStatus)))
                .thenThrow(new IllegalArgumentException("Invalid status value"));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                adminDashboardController.getAppointmentsByStatus(invalidStatus));

        verify(adminDashboardService, times(1))
                .getAppointmentsByStatus(eq(invalidStatus));
    }

    @Test
    void getAppointmentsByDateRange_WithInvalidDateRange_ShouldHandleGracefully() {
        // Arrange
        LocalDateTime end = LocalDateTime.now().minusDays(7); // End before start
        LocalDateTime start = LocalDateTime.now();

        when(adminDashboardService.getAppointmentsByDateRange(eq(start), eq(end)))
                .thenReturn(Collections.emptyList());

        // Act
        ResponseEntity<List<AdminAppointmentDto>> response =
                adminDashboardController.getAppointmentsByDateRange(start, end);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());

        verify(adminDashboardService, times(1))
                .getAppointmentsByDateRange(eq(start), eq(end));
    }

    @Test
    void getAllEndpoints_ShouldCallServiceMethodsCorrectly() {
        // Test all endpoints in sequence to ensure no side effects

        // Test getDashboardStats
        when(adminDashboardService.getAppointmentStatistics()).thenReturn(mockStatsDto);
        ResponseEntity<AppointmentStatsDto> statsResponse = adminDashboardController.getDashboardStats();
        assertNotNull(statsResponse);
        assertEquals(HttpStatus.OK, statsResponse.getStatusCode());

        // Test getAllAppointments
        when(adminDashboardService.getAllAppointments()).thenReturn(mockAppointments);
        ResponseEntity<List<AdminAppointmentDto>> allResponse = adminDashboardController.getAllAppointments();
        assertNotNull(allResponse);
        assertEquals(HttpStatus.OK, allResponse.getStatusCode());

        // Test getAppointmentsByStatus
        when(adminDashboardService.getAppointmentsByStatus(anyString())).thenReturn(mockAppointments);
        ResponseEntity<List<AdminAppointmentDto>> statusResponse =
                adminDashboardController.getAppointmentsByStatus("PENDING");
        assertNotNull(statusResponse);
        assertEquals(HttpStatus.OK, statusResponse.getStatusCode());

        // Verify all service calls were made
        verify(adminDashboardService, times(1)).getAppointmentStatistics();
        verify(adminDashboardService, times(1)).getAllAppointments();
        verify(adminDashboardService, times(1)).getAppointmentsByStatus(anyString());
    }
}