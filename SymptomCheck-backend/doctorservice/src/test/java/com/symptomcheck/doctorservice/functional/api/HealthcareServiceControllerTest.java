package com.symptomcheck.doctorservice.functional.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.symptomcheck.doctorservice.controllers.HealthcareServiceController;
import com.symptomcheck.doctorservice.dtos.HealthcareServiceDto;
import com.symptomcheck.doctorservice.models.HealthcareService;
import com.symptomcheck.doctorservice.services.HealthcareServiceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HealthcareServiceController.class)
class HealthcareServiceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private HealthcareServiceService healthcareServiceService;

    private UUID doctorId;
    private HealthcareService healthcareService;
    private HealthcareServiceDto healthcareServiceDto;
    private Jwt jwt;

    @BeforeEach
    void setUp() {
        doctorId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        // Create mock JWT for authentication
        jwt = Jwt.withTokenValue("mock-jwt-token")
                .header("alg", "none")
                .claim("sub", "test-doctor")
                .claim("scope", "openid")
                .claim("realm_access", java.util.Map.of("roles", Arrays.asList("doctor")))
                .build();

        // Create real JSON-compatible HealthcareService
        healthcareService = new HealthcareService();
        healthcareService.setId(1L);
        healthcareService.setDoctorId(doctorId);
        healthcareService.setName("Cardiology Consultation");
        healthcareService.setDescription("Comprehensive heart health consultation with ECG");
        healthcareService.setCategory("Cardiology");
        healthcareService.setDurationMinutes(30);
        healthcareService.setPrice(150.0);
        healthcareService.setImageUrl("/uploads/cardiology.jpg");

        // Create real JSON-compatible HealthcareServiceDto
        healthcareServiceDto = new HealthcareServiceDto();
        healthcareServiceDto.setDoctorId(doctorId);
        healthcareServiceDto.setName("Cardiology Consultation");
        healthcareServiceDto.setDescription("Comprehensive heart health consultation with ECG");
        healthcareServiceDto.setCategory("Cardiology");
        healthcareServiceDto.setDurationMinutes(30);
        healthcareServiceDto.setPrice(150.0);
    }

    @Nested
    class GetHealthcareServiceTests {
        @Test
        void shouldReturnAllHealthcareServicesSuccessfully() throws Exception {
            // Given
            List<HealthcareService> services = Arrays.asList(healthcareService);
            when(healthcareServiceService.getAll()).thenReturn(services);

            // When
            ResultActions result = mockMvc.perform(get("/api/v1/doctor/healthcare/service")
                    .with(jwt().jwt(jwt)));

            // Then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value(1))
                    .andExpect(jsonPath("$[0].doctorId").value(doctorId.toString()))
                    .andExpect(jsonPath("$[0].name").value("Cardiology Consultation"))
                    .andExpect(jsonPath("$[0].description").value("Comprehensive heart health consultation with ECG"))
                    .andExpect(jsonPath("$[0].category").value("Cardiology"))
                    .andExpect(jsonPath("$[0].durationMinutes").value(30))
                    .andExpect(jsonPath("$[0].price").value(150.0))
                    .andExpect(jsonPath("$[0].imageUrl").value("/uploads/cardiology.jpg"));
        }

        @Test
        void shouldReturnEmptyListWhenNoServices() throws Exception {
            // Given
            when(healthcareServiceService.getAll()).thenReturn(Collections.emptyList());

            // When
            ResultActions result = mockMvc.perform(get("/api/v1/doctor/healthcare/service")
                    .with(jwt().jwt(jwt)));

            // Then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$").isEmpty());
        }

        @Test
        void shouldReturnInternalServerErrorWhenServiceFails() throws Exception {
            // Given
            when(healthcareServiceService.getAll()).thenThrow(new RuntimeException("Database error"));

            // When
            ResultActions result = mockMvc.perform(get("/api/v1/doctor/healthcare/service")
                    .with(jwt().jwt(jwt)));

            // Then
            result.andExpect(status().isInternalServerError());
        }

        @Test
        void shouldReturnUnauthorizedWhenNoAuthentication() throws Exception {
            // When
            ResultActions result = mockMvc.perform(get("/api/v1/doctor/healthcare/service"));

            // Then
            result.andExpect(status().isUnauthorized());
        }
    }

    @Nested
    class GetDoctorHealthcareServiceTests {
        @Test
        void shouldReturnDoctorHealthcareServicesSuccessfully() throws Exception {
            // Given
            List<HealthcareService> services = Arrays.asList(healthcareService);
            when(healthcareServiceService.getHealthcareServiceByDoctorId(doctorId)).thenReturn(services);

            // When
            ResultActions result = mockMvc.perform(get("/api/v1/doctor/healthcare/service/doctor/{doctorId}", doctorId)
                    .with(jwt().jwt(jwt)));

            // Then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value(1))
                    .andExpect(jsonPath("$[0].doctorId").value(doctorId.toString()))
                    .andExpect(jsonPath("$[0].name").value("Cardiology Consultation"));
        }

        @Test
        void shouldReturnEmptyListWhenDoctorHasNoServices() throws Exception {
            // Given
            when(healthcareServiceService.getHealthcareServiceByDoctorId(doctorId)).thenReturn(Collections.emptyList());

            // When
            ResultActions result = mockMvc.perform(get("/api/v1/doctor/healthcare/service/doctor/{doctorId}", doctorId)
                    .with(jwt().jwt(jwt)));

            // Then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$").isEmpty());
        }

        @Test
        void shouldReturnInternalServerErrorWhenServiceFails() throws Exception {
            // Given
            when(healthcareServiceService.getHealthcareServiceByDoctorId(doctorId))
                    .thenThrow(new RuntimeException("Service error"));

            // When
            ResultActions result = mockMvc.perform(get("/api/v1/doctor/healthcare/service/doctor/{doctorId}", doctorId)
                    .with(jwt().jwt(jwt)));

            // Then
            result.andExpect(status().isInternalServerError());
        }

        @Test
        void shouldHandleInvalidDoctorIdFormat() throws Exception {
            // Given - This would be handled by Spring's path variable conversion
            String invalidUuid = "invalid-uuid";

            // When
            ResultActions result = mockMvc.perform(get("/api/v1/doctor/healthcare/service/doctor/{doctorId}", invalidUuid)
                    .with(jwt().jwt(jwt)));

            // Then - Spring will return 400 for invalid UUID format
            result.andExpect(status().isBadRequest());
        }
    }

    @Nested
    class SaveHealthcareServiceTests {
        @Test
        void shouldSaveHealthcareServiceSuccessfully() throws Exception {
            // Given
            when(healthcareServiceService.createHealthcareService(any(HealthcareServiceDto.class), any()))
                    .thenReturn(healthcareService);

            // Create multipart files
            String dtoJson = objectMapper.writeValueAsString(healthcareServiceDto);
            MockMultipartFile dtoPart = new MockMultipartFile(
                    "dto", "", "application/json", dtoJson.getBytes());

            MockMultipartFile imagePart = new MockMultipartFile(
                    "file", "test-image.jpg", "image/jpeg", "test image content".getBytes());

            // When
            ResultActions result = mockMvc.perform(multipart("/api/v1/doctor/healthcare/service")
                    .file(dtoPart)
                    .file(imagePart)
                    .with(jwt().jwt(jwt))
                    .contentType(MediaType.MULTIPART_FORM_DATA));

            // Then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.name").value("Cardiology Consultation"))
                    .andExpect(jsonPath("$.doctorId").value(doctorId.toString()))
                    .andExpect(jsonPath("$.imageUrl").value("/uploads/cardiology.jpg"));
        }

        @Test
        void shouldHandleMissingDtoPart() throws Exception {
            // Given - Only image part, no dto part
            MockMultipartFile imagePart = new MockMultipartFile(
                    "file", "test-image.jpg", "image/jpeg", "test image content".getBytes());

            // When
            ResultActions result = mockMvc.perform(multipart("/api/v1/doctor/healthcare/service")
                    .file(imagePart)
                    .with(jwt().jwt(jwt))
                    .contentType(MediaType.MULTIPART_FORM_DATA));

            // Then - Spring will return 400 for missing required part
            result.andExpect(status().isBadRequest());
        }

        @Test
        void shouldHandleMissingFilePart() throws Exception {
            // Given - Only dto part, no file part
            String dtoJson = objectMapper.writeValueAsString(healthcareServiceDto);
            MockMultipartFile dtoPart = new MockMultipartFile(
                    "dto", "", "application/json", dtoJson.getBytes());

            // When
            ResultActions result = mockMvc.perform(multipart("/api/v1/doctor/healthcare/service")
                    .file(dtoPart)
                    .with(jwt().jwt(jwt))
                    .contentType(MediaType.MULTIPART_FORM_DATA));

            // Then - Spring will return 400 for missing required part
            result.andExpect(status().isBadRequest());
        }

        @Test
        void shouldHandleInvalidDtoJson() throws Exception {
            // Given - Invalid JSON for dto part
            MockMultipartFile dtoPart = new MockMultipartFile(
                    "dto", "", "application/json", "invalid json".getBytes());

            MockMultipartFile imagePart = new MockMultipartFile(
                    "file", "test-image.jpg", "image/jpeg", "test image content".getBytes());

            // When
            ResultActions result = mockMvc.perform(multipart("/api/v1/doctor/healthcare/service")
                    .file(dtoPart)
                    .file(imagePart)
                    .with(jwt().jwt(jwt))
                    .contentType(MediaType.MULTIPART_FORM_DATA));

            // Then - JSON parsing error
            result.andExpect(status().isBadRequest());
        }

        @Test
        void shouldReturnInternalServerErrorWhenServiceFails() throws Exception {
            // Given
            when(healthcareServiceService.createHealthcareService(any(HealthcareServiceDto.class), any()))
                    .thenThrow(new RuntimeException("File storage error"));

            String dtoJson = objectMapper.writeValueAsString(healthcareServiceDto);
            MockMultipartFile dtoPart = new MockMultipartFile(
                    "dto", "", "application/json", dtoJson.getBytes());

            MockMultipartFile imagePart = new MockMultipartFile(
                    "file", "test-image.jpg", "image/jpeg", "test image content".getBytes());

            // When
            ResultActions result = mockMvc.perform(multipart("/api/v1/doctor/healthcare/service")
                    .file(dtoPart)
                    .file(imagePart)
                    .with(jwt().jwt(jwt))
                    .contentType(MediaType.MULTIPART_FORM_DATA));

            // Then
            result.andExpect(status().isInternalServerError());
        }
    }
}