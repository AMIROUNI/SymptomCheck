package com.symptomcheck.appointmentservice.functional.api;

import com.symptomcheck.appointmentservice.controllers.AppointmentDashboardController;
import com.symptomcheck.appointmentservice.dtos.dashboarddto.AppointmentDashboardDTO;
import com.symptomcheck.appointmentservice.dtos.dashboarddto.AppointmentStatsDTO;
import com.symptomcheck.appointmentservice.enums.AppointmentStatus;
import com.symptomcheck.appointmentservice.models.Appointment;
import com.symptomcheck.appointmentservice.services.AppointmentDashboardService;
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
import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Controller slice test for AppointmentDashboardController using mocked service (no DB).
 */
@WebMvcTest(controllers = AppointmentDashboardController.class)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ImportAutoConfiguration(exclude = { OAuth2ResourceServerAutoConfiguration.class })

public class AppointmentDashboardControllerApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AppointmentDashboardService dashboardService;

    // Helper to produce a Jwt post-processor with ROLE_DOCTOR authority
    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor doctorJwt() {
        Jwt jwt = Jwt.withTokenValue("doctor-token")
                .header("alg", "none")
                .claim("sub", "doctor-sub")
                .claim("realm_access", Map.of("roles", List.of("DOCTOR")))
                .build();

        return jwt().jwt(jwt).authorities(new SimpleGrantedAuthority("ROLE_DOCTOR"));
    }

    // Simple Appointment helper
    private Appointment appointment(Long id, LocalDateTime dt, UUID patientId, UUID doctorId, AppointmentStatus status, String desc) {
        Appointment a = new Appointment();
        a.setId(id);
        a.setDateTime(dt);
        a.setPatientId(patientId);
        a.setDoctorId(doctorId);
        a.setStatus(status);
        a.setDescription(desc);
        a.setCreatedAt(Instant.now());
        a.setUpdatedAt(Instant.now());
        return a;
    }

    @Test
    void getAppointmentDashboard_returnsDto_whenDoctorAuthorized() throws Exception {
        UUID doctorId = UUID.randomUUID();

        // Prepare mocked stats DTO
        AppointmentStatsDTO stats = new AppointmentStatsDTO(
                3L,    // totalAppointments
                1L,    // todayAppointments
                1L,    // pending
                1L,    // completed
                0L     // cancelled
        );

        Map<String, Long> byStatus = Map.of(
                "PENDING", 1L,
                "CONFIRMED", 1L,
                "COMPLETED", 1L
        );

        Map<String, Long> weekly = new LinkedHashMap<>();
        weekly.put("MONDAY", 0L);
        weekly.put("TUESDAY", 0L);
        weekly.put("WEDNESDAY", 1L);
        weekly.put("THURSDAY", 0L);
        weekly.put("FRIDAY", 1L);
        weekly.put("SATURDAY", 0L);
        weekly.put("SUNDAY", 1L);

        AppointmentDashboardDTO dashboardDto = new AppointmentDashboardDTO(stats, byStatus, weekly);

        Mockito.when(dashboardService.getAppointmentDashboard(eq(doctorId))).thenReturn(dashboardDto);

        mockMvc.perform(get("/api/v1/appointments/dashboard/doctor/{doctorId}", doctorId)
                        .with(doctorJwt())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stats.totalAppointments", is(3)))
                .andExpect(jsonPath("$.appointmentsByStatus.PENDING", is(1)))
                .andExpect(jsonPath("$.weeklyAppointments.WEDNESDAY", is(1)))
                .andExpect(jsonPath("$.stats.todayAppointments", is(1)));
    }

    @Test
    void getTodayAppointments_returnsList_whenDoctorAuthorized() throws Exception {
        UUID doctorId = UUID.randomUUID();
        UUID patient = UUID.randomUUID();

        List<Appointment> today = List.of(
                appointment(1L, LocalDateTime.now().withHour(9), patient, doctorId, AppointmentStatus.PENDING, "a1"),
                appointment(2L, LocalDateTime.now().withHour(11), patient, doctorId, AppointmentStatus.CONFIRMED, "a2")
        );

        Mockito.when(dashboardService.getTodayAppointments(eq(doctorId))).thenReturn(today);

        mockMvc.perform(get("/api/v1/appointments/dashboard/doctor/{doctorId}/today", doctorId)
                        .with(doctorJwt())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].doctorId", is(doctorId.toString())));
    }

    @Test
    void getUpcomingAppointments_returnsList_withDefaultDays_whenDoctorAuthorized() throws Exception {
        UUID doctorId = UUID.randomUUID();
        UUID patient = UUID.randomUUID();

        List<Appointment> upcoming = List.of(
                appointment(10L, LocalDateTime.now().plusDays(1), patient, doctorId, AppointmentStatus.PENDING, "up1"),
                appointment(11L, LocalDateTime.now().plusDays(3), patient, doctorId, AppointmentStatus.CONFIRMED, "up2")
        );

        Mockito.when(dashboardService.getUpcomingAppointments(eq(doctorId), Mockito.anyInt())).thenReturn(upcoming);

        mockMvc.perform(get("/api/v1/appointments/dashboard/doctor/{doctorId}/upcoming", doctorId)
                        .with(doctorJwt())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[1].description", is("up2")));
    }

    @Test
    void getAppointmentAnalytics_returnsMap_whenDoctorAuthorized() throws Exception {
        UUID doctorId = UUID.randomUUID();

        Map<String, Object> analytics = new HashMap<>();
        analytics.put("totalAppointments", 4);
        analytics.put("completionRate", 50.0);
        analytics.put("averageAppointmentsPerWeek", 1.0);

        Mockito.when(dashboardService.getAppointmentAnalytics(eq(doctorId))).thenReturn(analytics);

        mockMvc.perform(get("/api/v1/appointments/dashboard/doctor/{doctorId}/analytics", doctorId)
                        .with(doctorJwt())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalAppointments", is(4)))
                .andExpect(jsonPath("$.completionRate", is(50.0)))
                .andExpect(jsonPath("$.averageAppointmentsPerWeek", is(1.0)));
    }
}
