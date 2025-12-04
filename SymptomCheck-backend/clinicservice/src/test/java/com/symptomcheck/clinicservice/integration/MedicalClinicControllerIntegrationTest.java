package com.symptomcheck.clinicservice.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.symptomcheck.clinicservice.config.SecurityConfig;
import com.symptomcheck.clinicservice.dtos.MedicalClinicDto;
import com.symptomcheck.clinicservice.models.MedicalClinic;
import com.symptomcheck.clinicservice.repositories.MedicalClinicRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Testcontainers
@Import(SecurityConfig.class)
class MedicalClinicControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MedicalClinicRepository medicalClinicRepository;


    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test")
            .withReuse(true);

    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.jpa.properties.hibernate.dialect", () -> "org.hibernate.dialect.PostgreSQLDialect");
        registry.add("spring.sql.init.mode", () -> "always");
    }

    @BeforeEach
    void setUp() {
        medicalClinicRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void createClinic_ShouldCreateNewClinic() throws Exception {
        // Given
        MedicalClinicDto clinicDto = new MedicalClinicDto();
        clinicDto.setName("Test Clinic");
        clinicDto.setAddress("123 Test St");
        clinicDto.setCity("Test City");
        clinicDto.setCountry("Test Country");

        // When & Then
        mockMvc.perform(post("/api/v1/medical/clinic")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clinicDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Clinic"));

        // Verify in database
        assertThat(medicalClinicRepository.findAll()).hasSize(1);
    }

    @Test
    void getAllClinics_ShouldReturnAllClinics() throws Exception {
        // Given
        MedicalClinic clinic1 = new MedicalClinic();
        clinic1.setName("Clinic 1");
        clinic1.setCity("City A");
        medicalClinicRepository.save(clinic1);

        MedicalClinic clinic2 = new MedicalClinic();
        clinic2.setName("Clinic 2");
        clinic2.setCity("City B");
        medicalClinicRepository.save(clinic2);

        // When & Then
        mockMvc.perform(get("/api/v1/medical/clinic"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").value(hasSize(2)));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getClinicById_ShouldReturnClinic() throws Exception {
        // Given
        MedicalClinic clinic = new MedicalClinic();
        clinic.setName("Test Clinic");
        clinic.setCity("Test City");
        clinic = medicalClinicRepository.save(clinic);

        // When & Then
        mockMvc.perform(get("/api/v1/medical/clinic/{id}", clinic.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(clinic.getId()))
                .andExpect(jsonPath("$.name").value("Test Clinic"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void updateClinic_ShouldUpdateClinic() throws Exception {
        // Given
        MedicalClinic clinic = new MedicalClinic();
        clinic.setName("Old Name");
        clinic.setCity("Old City");
        clinic = medicalClinicRepository.save(clinic);

        MedicalClinic updateData = new MedicalClinic();
        updateData.setName("New Name");
        updateData.setCity("New City");

        // When & Then
        mockMvc.perform(put("/api/v1/medical/clinic/{id}", clinic.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Name"));

        // Verify in database
        MedicalClinic updated = medicalClinicRepository.findById(clinic.getId()).orElseThrow();
        assertThat(updated.getName()).isEqualTo("New Name");
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deleteClinic_ShouldDeleteClinic() throws Exception {
        // Given
        MedicalClinic clinic = new MedicalClinic();
        clinic.setName("To Delete");
        clinic = medicalClinicRepository.save(clinic);

        // When & Then
        mockMvc.perform(delete("/api/v1/medical/clinic/{id}", clinic.getId()))
                .andExpect(status().isOk());

        // Verify deleted
        assertThat(medicalClinicRepository.findById(clinic.getId())).isEmpty();
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void createClinic_WhenError_ShouldReturnServerError() throws Exception {
        // Test error handling
        // You can test with invalid data if you add validation
        MedicalClinicDto invalidClinic = new MedicalClinicDto();
        // Don't set required name field

        mockMvc.perform(post("/api/v1/medical/clinic")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidClinic)))
                .andExpect(status().isInternalServerError()); // or .isBadRequest() if you add validation
    }
}