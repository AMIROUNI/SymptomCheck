package com.symptomcheck.appointmentservice.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.symptomcheck.appointmentservice.config.SecurityConfig;
import com.symptomcheck.appointmentservice.controllers.AdminDashboardController;
import com.symptomcheck.appointmentservice.dtos.admindashboarddto.AdminAppointmentDto;
import com.symptomcheck.appointmentservice.dtos.admindashboarddto.AppointmentStatsDto;
import com.symptomcheck.appointmentservice.services.AdminDashboardService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminDashboardController.class)
@Import(SecurityConfig.class)
@Testcontainers
@ActiveProfiles("test")
class AdminDashboardIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminDashboardService adminDashboardService;

    @Autowired
    private ObjectMapper objectMapper;

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

    // ---------------------------------------------------------
    // 1) GET /dashboard/stats
    // ---------------------------------------------------------
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("GET /api/admin/dashboard/stats → returns statistics")
    void shouldReturnDashboardStats() throws Exception {

        AppointmentStatsDto stats = new AppointmentStatsDto();
        stats.setTotalAppointments(10L);
        stats.setPendingAppointments(3L);
        stats.setCompletedAppointments(4L);
        stats.setCancelledAppointments(2L);
        stats.setTodayAppointments(1L);
        stats.setStatusDistribution(Map.of("PENDING", 3L));
        stats.setLastUpdated(LocalDateTime.now());

        Mockito.when(adminDashboardService.getAppointmentStatistics()).thenReturn(stats);

        mockMvc.perform(get("/api/admin/dashboard/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalAppointments").value(10))
                .andExpect(jsonPath("$.pendingAppointments").value(3));
    }

    // ---------------------------------------------------------
    // 2) GET /appointments
    // ---------------------------------------------------------
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})  // <-- REQUIRED to avoid 401/403
    @DisplayName("GET /api/admin/appointments → returns all appointments")
    void shouldReturnAllAppointments() throws Exception {

        AdminAppointmentDto dto = new AdminAppointmentDto();
        dto.setId(1L);
        dto.setStatus("PENDING");

        Mockito.when(adminDashboardService.getAllAppointments())
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/api/admin/appointments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].status").value("PENDING"));
    }

    // ---------------------------------------------------------
    // 3) GET /appointments/status/{status}
    // ---------------------------------------------------------
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})  // <-- REQUIRED to avoid 401/403
    @DisplayName("GET /api/admin/appointments/status/PENDING → returns filtered list")
    void shouldReturnAppointmentsByStatus() throws Exception {

        AdminAppointmentDto dto = new AdminAppointmentDto();
        dto.setId(2L);
        dto.setStatus("PENDING");

        Mockito.when(adminDashboardService.getAppointmentsByStatus("PENDING"))
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/api/admin/appointments/status/PENDING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$[0].status").value("PENDING"));
    }

    // ---------------------------------------------------------
    // 4) GET /appointments/date-range
    // ---------------------------------------------------------
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})  // <-- REQUIRED to avoid 401/403
    @DisplayName("GET /api/admin/appointments/date-range → returns list within range")
    void shouldReturnAppointmentsByDateRange() throws Exception {

        LocalDateTime start = LocalDateTime.now().minusDays(2);
        LocalDateTime end = LocalDateTime.now();

        AdminAppointmentDto dto = new AdminAppointmentDto();
        dto.setId(3L);
        dto.setStatus("COMPLETED");

        Mockito.when(adminDashboardService.getAppointmentsByDateRange(any(), any()))
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/api/admin/appointments/date-range")
                        .param("start", start.toString())
                        .param("end", end.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(3))
                .andExpect(jsonPath("$[0].status").value("COMPLETED"));
    }

    // ---------------------------------------------------------
    // 5) GET /appointments/doctor/{doctorId}
    // ---------------------------------------------------------
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})  // <-- REQUIRED to avoid 401/403
    @DisplayName("GET /api/admin/appointments/doctor/{id} → returns doctor's appointments")
    void shouldReturnAppointmentsByDoctor() throws Exception {

        UUID doctorId = UUID.randomUUID();

        AdminAppointmentDto dto = new AdminAppointmentDto();
        dto.setId(4L);
        dto.setDoctorId(doctorId.toString());

        Mockito.when(adminDashboardService.getAppointmentsByDoctor(doctorId))
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/api/admin/appointments/doctor/" + doctorId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(4))
                .andExpect(jsonPath("$[0].doctorId").value(doctorId.toString()));
    }

    // ---------------------------------------------------------
    // 6) PUT /appointments/{appointmentId}/status
    // ---------------------------------------------------------
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})  // <-- REQUIRED to avoid 401/403
    @DisplayName("PUT /api/admin/appointments/{id}/status → updates the status")
    void shouldUpdateAppointmentStatus() throws Exception {

        AdminAppointmentDto dto = new AdminAppointmentDto();
        dto.setId(5L);
        dto.setStatus("COMPLETED");

        Mockito.when(adminDashboardService.updateAppointmentStatus(eq(5L), eq("COMPLETED")))
                .thenReturn(dto);

        mockMvc.perform(put("/api/admin/appointments/5/status")
                        .param("status", "COMPLETED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    // ---------------------------------------------------------
    // Unauthorized test for PUT
    // ---------------------------------------------------------
    @Test
    @DisplayName("PUT /appointments/{id}/status → unauthorized without ADMIN role")
    void shouldRejectUpdateWhenNotAdmin() throws Exception {

        mockMvc.perform(put("/api/admin/appointments/5/status")
                        .param("status", "CANCELLED"))
                .andExpect(status().isUnauthorized());
    }
}
