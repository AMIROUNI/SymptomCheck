package com.symptomcheck.doctorservice.integration;

import com.symptomcheck.doctorservice.config.SecurityConfig;
import com.symptomcheck.doctorservice.models.DoctorAvailability;
import com.symptomcheck.doctorservice.models.HealthcareService;
import com.symptomcheck.doctorservice.repositories.DoctorAvailabilityRepository;
import com.symptomcheck.doctorservice.repositories.HealthcareServiceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Import(SecurityConfig.class)
@Testcontainers
class DoctorDashboardControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private HealthcareServiceRepository serviceRepo;
    @Autowired private DoctorAvailabilityRepository availabilityRepo;

    private UUID doctorId;

    // -------------------------------
    // ADDING THE POSTGRES CONTAINER
    // -------------------------------
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
        registry.add("spring.sql.init.mode", () -> "never");
    }
    // -------------------------------

    @BeforeEach
    void setup() {
        serviceRepo.deleteAll();
        availabilityRepo.deleteAll();
        doctorId = UUID.fromString("550e8400-e29b-41d4-a716-446655440001");
    }




    // -----------------------------------------------------
    // Helper Methods
    // -----------------------------------------------------
    private void createService(UUID doctorId) {
        HealthcareService s = new HealthcareService();
        s.setDoctorId(doctorId);
        s.setName("Consultation");
        s.setCategory("General");
        s.setDescription("Desc");
        s.setPrice(50.0);
        s.setDurationMinutes(20);
        serviceRepo.save(s);
    }

    private void createAvailability(UUID doctorId) {
        DoctorAvailability a = new DoctorAvailability();
        a.setDoctorId(doctorId);
        a.setDaysOfWeek(List.of(DayOfWeek.MONDAY));
        a.setStartTime(LocalTime.of(9,0));
        a.setEndTime(LocalTime.of(12,0));
        availabilityRepo.save(a);
    }

    // -----------------------------------------------------
    // TESTS
    // -----------------------------------------------------

    @Test
    @WithMockUser(roles = "DOCTOR")
    void getDashboard_ShouldReturnFullDashboard() throws Exception {
        createService(doctorId);
        createAvailability(doctorId);

        mockMvc.perform(get("/api/v1/doctor/dashboard/{doctorId}", doctorId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stats.totalServices", is(1)))
                .andExpect(jsonPath("$.stats.totalAvailabilitySlots", is(1)))
                .andExpect(jsonPath("$.services", hasSize(1)))
                .andExpect(jsonPath("$.availability", hasSize(1)));
    }

    @Test
    @WithMockUser(roles = "DOCTOR")
    void getServiceCategories_ShouldReturnCategories() throws Exception {
        createService(doctorId);

        mockMvc.perform(get("/api/v1/doctor/dashboard/{doctorId}/service-categories", doctorId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0]", is("General")));
    }

    @Test
    @WithMockUser(roles = "DOCTOR")
    void profileStatus_ShouldReturnFalse_WhenIncomplete() throws Exception {
        createService(doctorId); // No availability

        mockMvc.perform(get("/api/v1/doctor/dashboard/{doctorId}/profile-status", doctorId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(false)));
    }

    @Test
    @WithMockUser(roles = "DOCTOR")
    void profileStatus_ShouldReturnTrue_WhenComplete() throws Exception {
        createService(doctorId);
        createAvailability(doctorId);

        mockMvc.perform(get("/api/v1/doctor/dashboard/{doctorId}/profile-status", doctorId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(true)));
    }

    @Test
    @WithMockUser(roles = "DOCTOR")
    void servicesCount_ShouldReturnCorrectValue() throws Exception {
        createService(doctorId);
        createService(doctorId);

        mockMvc.perform(get("/api/v1/doctor/dashboard/{doctorId}/services-count", doctorId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(2)));
    }

    @Test
    @WithMockUser(roles = "DOCTOR")
    void availabilitySlots_ShouldReturnCorrectValue() throws Exception {
        createAvailability(doctorId);
        createAvailability(doctorId);

        mockMvc.perform(get("/api/v1/doctor/dashboard/{doctorId}/availability-slots", doctorId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(2)));
    }

    @Test
    @WithMockUser(roles = "DOCTOR")
    void getDashboard_WhenInvalidUUID_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/api/v1/doctor/dashboard/{doctorId}", "invalid-uuid-format"))
                .andExpect(status().isBadRequest());
    }

}
