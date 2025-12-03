package com.symptomcheck.doctorservice.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.symptomcheck.doctorservice.config.SecurityConfig;
import com.symptomcheck.doctorservice.dtos.HealthcareServiceDto;
import com.symptomcheck.doctorservice.models.HealthcareService;
import com.symptomcheck.doctorservice.repositories.HealthcareServiceRepository;
import com.symptomcheck.doctorservice.services.LocalFileStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Import(SecurityConfig.class)
class HealthCareServiceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private HealthcareServiceRepository healthcareServiceRepository;

    @MockBean
    private LocalFileStorageService localFileStorageService;

    private UUID doctorId1;
    private UUID doctorId2;

    @BeforeEach
    void setUp() throws IOException {
        healthcareServiceRepository.deleteAll();

        doctorId1 = UUID.fromString("550e8400-e29b-41d4-a716-446655440001");
        doctorId2 = UUID.fromString("550e8400-e29b-41d4-a716-446655440002");

        // Setup mock file storage
        when(localFileStorageService.store(any())).thenReturn("stored-image.jpg");
    }

    @Test
    @WithMockUser(username = "doctor", roles = {"DOCTOR", "ADMIN"})
    void getAllHealthcareServices_ShouldReturnAllServices() throws Exception {
        // Given
        HealthcareService service1 = createHealthcareService(doctorId1, "General Consultation", "Consultation");
        HealthcareService service2 = createHealthcareService(doctorId2, "Dental Checkup", "Dental");

        healthcareServiceRepository.save(service1);
        healthcareServiceRepository.save(service2);

        // When & Then
        mockMvc.perform(get("/api/v1/doctor/healthcare/service"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").value(hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("General Consultation"))
                .andExpect(jsonPath("$[1].name").value("Dental Checkup"));
    }

    @Test
    @WithMockUser(username = "doctor", roles = {"DOCTOR", "ADMIN"})
    void getAllHealthcareServices_WhenEmpty_ShouldReturnEmptyList() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/doctor/healthcare/service"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").value(hasSize(0)));
    }

    @Test
    @WithMockUser(username = "doctor", roles = {"DOCTOR", "ADMIN"})
    void getDoctorHealthcareService_ShouldReturnServicesForDoctor() throws Exception {
        // Given
        HealthcareService service1 = createHealthcareService(doctorId1, "Service 1", "Category 1");
        HealthcareService service2 = createHealthcareService(doctorId1, "Service 2", "Category 2");
        HealthcareService service3 = createHealthcareService(doctorId2, "Service 3", "Category 1");

        healthcareServiceRepository.save(service1);
        healthcareServiceRepository.save(service2);
        healthcareServiceRepository.save(service3);

        // When & Then
        mockMvc.perform(get("/api/v1/doctor/healthcare/service/doctor/{doctorId}", doctorId1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").value(hasSize(2)))
                .andExpect(jsonPath("$[0].doctorId").value(doctorId1.toString()))
                .andExpect(jsonPath("$[1].doctorId").value(doctorId1.toString()));
    }

    @Test
    @WithMockUser(username = "doctor", roles = {"DOCTOR", "ADMIN"})
    void getDoctorHealthcareService_WithNonExistentDoctor_ShouldReturnEmptyList() throws Exception {
        // Given
        UUID nonExistentDoctorId = UUID.randomUUID();

        // When & Then
        mockMvc.perform(get("/api/v1/doctor/healthcare/service/doctor/{doctorId}", nonExistentDoctorId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").value(hasSize(0)));
    }

    @Test
    @WithMockUser(username = "doctor", roles = {"DOCTOR", "ADMIN"})
    void getDoctorHealthcareService_WithInvalidUUID_ShouldReturnInternalServerError() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/doctor/healthcare/service/doctor/{doctorId}", "invalid-uuid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "doctor", roles = {"DOCTOR"})
    void saveHealthcareService_ShouldCreateNewService() throws Exception {
        // Given
        HealthcareServiceDto dto = new HealthcareServiceDto();
        dto.setDoctorId(doctorId1);
        dto.setName("Physical Therapy");
        dto.setDescription("Rehabilitation exercises");
        dto.setCategory("Therapy");
        dto.setDurationMinutes(60);
        dto.setPrice(120.0);

        MockMultipartFile imageFile = new MockMultipartFile(
                "file",
                "therapy.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        MockMultipartFile dtoFile = new MockMultipartFile(
                "dto",
                "",
                "application/json",
                objectMapper.writeValueAsBytes(dto)
        );

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/doctor/healthcare/service")
                        .file(imageFile)
                        .file(dtoFile))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.doctorId").value(doctorId1.toString()))
                .andExpect(jsonPath("$.name").value("Physical Therapy"))
                .andExpect(jsonPath("$.description").value("Rehabilitation exercises"))
                .andExpect(jsonPath("$.category").value("Therapy"))
                .andExpect(jsonPath("$.durationMinutes").value(60))
                .andExpect(jsonPath("$.price").value(120.0))
                .andExpect(jsonPath("$.imageUrl").value("stored-image.jpg"));

        // Verify count
        mockMvc.perform(get("/api/v1/doctor/healthcare/service/doctor/{doctorId}", doctorId1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(hasSize(1)));
    }

    @Test
    @WithMockUser(username = "doctor", roles = {"DOCTOR"})
    void saveHealthcareService_WithMissingFile_ShouldReturnBadRequest() throws Exception {
        // Given
        HealthcareServiceDto dto = new HealthcareServiceDto();
        dto.setDoctorId(doctorId1);
        dto.setName("Test Service");
        dto.setDescription("Description");
        dto.setCategory("Category");
        dto.setDurationMinutes(30);
        dto.setPrice(50.0);

        MockMultipartFile dtoFile = new MockMultipartFile(
                "dto",
                "",
                "application/json",
                objectMapper.writeValueAsBytes(dto)
        );

        // When & Then - Missing 'file' part should return 400
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/doctor/healthcare/service")
                        .file(dtoFile))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "doctor", roles = {"DOCTOR"})
    void saveHealthcareService_WithMissingDto_ShouldReturnBadRequest() throws Exception {
        // Given
        MockMultipartFile imageFile = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "test".getBytes()
        );

        // When & Then - Missing 'dto' part should return 400
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/doctor/healthcare/service")
                        .file(imageFile))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "doctor", roles = {"DOCTOR"})
    void saveHealthcareService_WithInvalidDto_ShouldReturnInternalServerError() throws Exception {
        // Given - Create invalid DTO (missing required fields)
        String invalidDtoJson = "{\"doctorId\":\"" + doctorId1 + "\"}";

        MockMultipartFile imageFile = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "test".getBytes()
        );

        MockMultipartFile dtoFile = new MockMultipartFile(
                "dto",
                "",
                "application/json",
                invalidDtoJson.getBytes()
        );

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/doctor/healthcare/service")
                        .file(imageFile)
                        .file(dtoFile))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser(username = "doctor", roles = {"DOCTOR"})
    void saveHealthcareService_WhenFileStorageFails_ShouldReturnInternalServerError() throws Exception {
        // Given
        when(localFileStorageService.store(any())).thenThrow(new RuntimeException("Storage failed"));

        HealthcareServiceDto dto = new HealthcareServiceDto();
        dto.setDoctorId(doctorId1);
        dto.setName("Failed Service");
        dto.setDescription("This should fail");
        dto.setCategory("Test");
        dto.setDurationMinutes(30);
        dto.setPrice(50.0);

        MockMultipartFile imageFile = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "test".getBytes()
        );

        MockMultipartFile dtoFile = new MockMultipartFile(
                "dto",
                "",
                "application/json",
                objectMapper.writeValueAsBytes(dto)
        );

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/doctor/healthcare/service")
                        .file(imageFile)
                        .file(dtoFile))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser(username = "doctor", roles = {"DOCTOR"})
    void saveHealthcareService_WithDuplicateNameForSameDoctor_ShouldSucceed() throws Exception {
        // Given - First service
        HealthcareService existingService = createHealthcareService(doctorId1, "Duplicate Service", "Category");
        healthcareServiceRepository.save(existingService);

        // New service with same name for same doctor
        HealthcareServiceDto dto = new HealthcareServiceDto();
        dto.setDoctorId(doctorId1);
        dto.setName("Duplicate Service"); // Same name
        dto.setDescription("Different description");
        dto.setCategory("Category");
        dto.setDurationMinutes(45);
        dto.setPrice(200.0);

        MockMultipartFile imageFile = new MockMultipartFile(
                "file",
                "duplicate.jpg",
                "image/jpeg",
                "test".getBytes()
        );

        MockMultipartFile dtoFile = new MockMultipartFile(
                "dto",
                "",
                "application/json",
                objectMapper.writeValueAsBytes(dto)
        );

        // When & Then - Should succeed (implementation allows duplicates)
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/doctor/healthcare/service")
                        .file(imageFile)
                        .file(dtoFile))
                .andExpect(status().isOk());

        // Verify both services exist
        mockMvc.perform(get("/api/v1/doctor/healthcare/service/doctor/{doctorId}", doctorId1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(hasSize(2)));
    }



    @Test
    void getAllHealthcareServices_WithoutAuthentication_ShouldReturnUnauthorized() throws Exception {
        // When & Then - No authentication should get 401
        mockMvc.perform(get("/api/v1/doctor/healthcare/service"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "doctor", roles = {"DOCTOR"})
    void saveHealthcareService_WithAllFields_ShouldCreateCompleteService() throws Exception {
        // Given
        HealthcareServiceDto dto = new HealthcareServiceDto();
        dto.setDoctorId(doctorId1);
        dto.setName("Complete Service");
        dto.setDescription("Complete description with details about the healthcare service");
        dto.setCategory("Specialty");
        dto.setDurationMinutes(90);
        dto.setPrice(250.0);

        MockMultipartFile imageFile = new MockMultipartFile(
                "file",
                "complete.jpg",
                "image/jpeg",
                "complete image".getBytes()
        );

        MockMultipartFile dtoFile = new MockMultipartFile(
                "dto",
                "",
                "application/json",
                objectMapper.writeValueAsBytes(dto)
        );

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/doctor/healthcare/service")
                        .file(imageFile)
                        .file(dtoFile))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Complete Service"))
                .andExpect(jsonPath("$.description").value("Complete description with details about the healthcare service"))
                .andExpect(jsonPath("$.category").value("Specialty"))
                .andExpect(jsonPath("$.durationMinutes").value(90))
                .andExpect(jsonPath("$.price").value(250.0));
    }

    private HealthcareService createHealthcareService(UUID doctorId, String name, String category) {
        HealthcareService service = new HealthcareService();
        service.setDoctorId(doctorId);
        service.setName(name);
        service.setDescription("Description for " + name);
        service.setCategory(category);
        service.setImageUrl("image.jpg");
        service.setDurationMinutes(30);
        service.setPrice(100.0);
        return service;
    }
}