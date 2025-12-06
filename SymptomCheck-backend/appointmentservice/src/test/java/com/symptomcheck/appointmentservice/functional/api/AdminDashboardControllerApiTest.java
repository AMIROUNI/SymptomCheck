package com.symptomcheck.appointmentservice.functional.api;

import com.symptomcheck.appointmentservice.controllers.AdminDashboardController;
import com.symptomcheck.appointmentservice.dtos.admindashboarddto.AdminAppointmentDto;
import com.symptomcheck.appointmentservice.dtos.admindashboarddto.AppointmentStatsDto;
import com.symptomcheck.appointmentservice.services.AdminDashboardService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Controller-slice tests for AdminDashboardController using mocked AdminDashboardService.
 */
@WebMvcTest(controllers = AdminDashboardController.class)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ImportAutoConfiguration(exclude = { OAuth2ResourceServerAutoConfiguration.class })

public class AdminDashboardControllerApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminDashboardService adminDashboardService;

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor adminJwt() {
        Jwt jwt = Jwt.withTokenValue("admin-token")
                .header("alg", "none")
                .claim("sub", "admin-sub")
                .claim("realm_access", Map.of("roles", List.of("ADMIN")))
                .build();
        return jwt().jwt(jwt).authorities(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }

    private AdminAppointmentDto adminAppointmentDto(Long id, LocalDateTime dt, UUID patientId, UUID doctorId, String status, String desc) {
        AdminAppointmentDto dto = new AdminAppointmentDto();
        dto.setId(id);
        dto.setDateTime(dt);
        dto.setPatientId(patientId.toString());
        dto.setDoctorId(doctorId.toString());
        dto.setStatus(status);
        dto.setDescription(desc);
        dto.setPaymentTransactionId(null);
        dto.setCreatedAt(Instant.now());
        dto.setUpdatedAt(Instant.now());
        return dto;
    }

    @Test
    void getDashboardStats_returnsDto_whenAdminAuthorized() throws Exception {
        AppointmentStatsDto stats = new AppointmentStatsDto();
        stats.setTotalAppointments(5L);
        stats.setPendingAppointments(2L);
        stats.setConfirmedAppointments(1L);
        stats.setCompletedAppointments(1L);
        stats.setCancelledAppointments(1L);
        stats.setTodayAppointments(1L);
        stats.setWeeklyAppointments(3L);
        stats.setStatusDistribution(Map.of("PENDING", 2L, "CONFIRMED", 1L));
        stats.setLastUpdated(LocalDateTime.now());

        Mockito.when(adminDashboardService.getAppointmentStatistics()).thenReturn(stats);

        mockMvc.perform(get("/api/admin/dashboard/stats")
                        .with(adminJwt())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalAppointments", is(5)))
                .andExpect(jsonPath("$.statusDistribution.PENDING", is(2)))
                .andExpect(jsonPath("$.lastUpdated").exists());
    }

    @Test
    void getAllAppointments_returnsList_whenAdminAuthorized() throws Exception {
        UUID patient = UUID.randomUUID();
        UUID doctor = UUID.randomUUID();

        AdminAppointmentDto dto = adminAppointmentDto(11L, LocalDateTime.now().plusDays(1), patient, doctor, "PENDING", "desc");
        Mockito.when(adminDashboardService.getAllAppointments()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/admin/appointments")
                        .with(adminJwt())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(11)))
                .andExpect(jsonPath("$[0].status", is("PENDING")));
    }

    @Test
    void getAppointmentsByStatus_filtersCorrectly_whenAdminAuthorized() throws Exception {
        UUID p = UUID.randomUUID();
        UUID d = UUID.randomUUID();

        AdminAppointmentDto confirmed = adminAppointmentDto(21L, LocalDateTime.now(), p, d, "CONFIRMED", "c1");
        Mockito.when(adminDashboardService.getAppointmentsByStatus(eq("CONFIRMED"))).thenReturn(List.of(confirmed));

        mockMvc.perform(get("/api/admin/appointments/status/{status}", "CONFIRMED")
                        .with(adminJwt())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].status", is("CONFIRMED")));
    }

    @Test
    void updateAppointmentStatus_changesStatus_whenAdminAuthorized() throws Exception {
        UUID p = UUID.randomUUID();
        UUID d = UUID.randomUUID();

        AdminAppointmentDto updated = adminAppointmentDto(31L, LocalDateTime.now(), p, d, "CONFIRMED", "updated");
        Mockito.when(adminDashboardService.updateAppointmentStatus(eq(31L), eq("CONFIRMED"))).thenReturn(updated);

        mockMvc.perform(put("/api/admin/appointments/{appointmentId}/status", 31L)
                        .with(adminJwt())
                        .param("status", "CONFIRMED")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(31)))
                .andExpect(jsonPath("$.status", is("CONFIRMED")));
    }
}
