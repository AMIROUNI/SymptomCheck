package com.symptomcheck.doctorservice.controllers;

import com.symptomcheck.doctorservice.dtos.HealthcareServiceDto;
import com.symptomcheck.doctorservice.models.HealthcareService;
import com.symptomcheck.doctorservice.services.HealthcareServiceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("HealthcareServiceController Unit Tests")
class HealthcareServiceControllerTest {

    @Mock
    private HealthcareServiceService healthcareService;

    @InjectMocks
    private HealthcareServiceController controller;

    private HealthcareServiceDto dto;
    private HealthcareService entity;
    private UUID doctorId;

    @BeforeEach
    void setUp() {
        doctorId = UUID.randomUUID();

        dto = new HealthcareServiceDto();
        dto.setDoctorId(doctorId);
        dto.setName("General Checkup");
        dto.setCategory("Checkup");
        dto.setDescription("Basic health checkup");
        dto.setPrice(50.0);
        dto.setDurationMinutes(30);

        entity = new HealthcareService();
        entity.setId(1L);
        entity.setDoctorId(doctorId);
        entity.setName("General Checkup");
        entity.setCategory("Checkup");
        entity.setDescription("Basic health checkup");
        entity.setPrice(50.0);
        entity.setDurationMinutes(30);
        entity.setImageUrl("image.jpg");
    }

    @Test
    @DisplayName("GET /api/v1/doctor/healthcare/service - should return all healthcare services")
    void testGetAllHealthcareServices() {
        // Arrange
        List<HealthcareService> services = List.of(entity);
        when(healthcareService.getAll()).thenReturn(services);

        // Act
        ResponseEntity<?> response = controller.getHealthcareService();

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(services, response.getBody());

        verify(healthcareService).getAll();
    }

    @Test
    @DisplayName("GET /api/v1/doctor/healthcare/service/doctor/{doctorId} - should return services for a doctor")
    void testGetHealthcareServiceByDoctorId() {
        // Arrange
        List<HealthcareService> services = List.of(entity);
        when(healthcareService.getHealthcareServiceByDoctorId(doctorId))
                .thenReturn(services);

        // Act
        ResponseEntity<?> response = controller.getDoctorHealthcareService(doctorId);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(services, response.getBody());

        verify(healthcareService).getHealthcareServiceByDoctorId(doctorId);
    }

    @Test
    @DisplayName("POST /api/v1/doctor/healthcare/service - should create a healthcare service")
    void testCreateHealthcareService() throws Exception {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.jpg", "image/jpeg", "dummy image content".getBytes()
        );

        when(healthcareService.createHealthcareService(dto, file)).thenReturn(entity);

        // Act
        ResponseEntity<?> response = controller.saveHealthcareService(dto, file);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(entity, response.getBody());

        verify(healthcareService).createHealthcareService(dto, file);
    }

    @Test
    @DisplayName("GET /api/v1/doctor/healthcare/service - should handle exception")
    void testGetAllHealthcareServicesException() {
        when(healthcareService.getAll()).thenThrow(new RuntimeException("DB error"));

        ResponseEntity<?> response = controller.getHealthcareService();

        assertEquals(500, response.getStatusCodeValue());
    }

    @Test
    @DisplayName("GET /api/v1/doctor/healthcare/service/doctor/{doctorId} - should handle exception")
    void testGetHealthcareServiceByDoctorIdException() {
        when(healthcareService.getHealthcareServiceByDoctorId(doctorId))
                .thenThrow(new RuntimeException("DB error"));

        ResponseEntity<?> response = controller.getDoctorHealthcareService(doctorId);

        assertEquals(500, response.getStatusCodeValue());
    }

    @Test
    @DisplayName("POST /api/v1/doctor/healthcare/service - should handle exception")
    void testCreateHealthcareServiceException() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.jpg", "image/jpeg", "dummy image content".getBytes()
        );

        when(healthcareService.createHealthcareService(dto, file))
                .thenThrow(new RuntimeException("Storage error"));

        ResponseEntity<?> response = controller.saveHealthcareService(dto, file);

        assertEquals(500, response.getStatusCodeValue());
    }
}
