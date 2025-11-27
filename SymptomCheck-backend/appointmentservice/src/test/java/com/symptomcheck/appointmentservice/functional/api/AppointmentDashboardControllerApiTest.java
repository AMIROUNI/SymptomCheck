package com.symptomcheck.appointmentservice.functional.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.symptomcheck.appointmentservice.controllers.AppointmentDashboardController;
import com.symptomcheck.appointmentservice.dtos.dashboardDto.AppointmentDashboardDTO;
import com.symptomcheck.appointmentservice.dtos.dashboardDto.AppointmentStatsDTO;
import com.symptomcheck.appointmentservice.enums.AppointmentStatus;
import com.symptomcheck.appointmentservice.models.Appointment;
import com.symptomcheck.appointmentservice.services.AppointmentDashboardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AppointmentDashboardController.class)
class AppointmentDashboardControllerFunctionalTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AppointmentDashboardService dashboardService;

    private UUID doctorId;
    private String validToken;
    private Jwt jwt;
    private AppointmentDashboardDTO dashboardDTO;
    private AppointmentStatsDTO statsDTO;
    private Appointment appointment;

    @BeforeEach
    void setUp() {
        doctorId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        validToken = "mock-jwt-token";

        // Create mock JWT
        jwt = Jwt.withTokenValue(validToken)
                .header("alg", "none")
                .claim("sub", "test-doctor")
                .claim("scope", "openid")
                .build();

        // Create real JSON-compatible AppointmentStatsDTO
        statsDTO = new AppointmentStatsDTO(
                100L,  // totalAppointments
                5L,    // todayAppointments
                10L,   // pendingAppointments
                80L,   // completedAppointments
                5L     // cancelledAppointments
        );

        // Create real JSON-compatible maps
        Map<String, Long> appointmentsByStatus = new HashMap<>();
        appointmentsByStatus.put("PENDING", 10L);
        appointmentsByStatus.put("CONFIRMED", 20L);
        appointmentsByStatus.put("COMPLETED", 80L);
        appointmentsByStatus.put("CANCELLED", 5L);

        Map<String, Long> weeklyAppointments = new LinkedHashMap<>();
        weeklyAppointments.put("MONDAY", 15L);
        weeklyAppointments.put("TUESDAY", 20L);
        weeklyAppointments.put("WEDNESDAY", 18L);
        weeklyAppointments.put("THURSDAY", 22L);
        weeklyAppointments.put("FRIDAY", 16L);
        weeklyAppointments.put("SATURDAY", 8L);
        weeklyAppointments.put("SUNDAY", 1L);

        // Create real JSON-compatible AppointmentDashboardDTO
        dashboardDTO = new AppointmentDashboardDTO(statsDTO, appointmentsByStatus, weeklyAppointments);

        // Create real JSON-compatible Appointment
        appointment = new Appointment();
        appointment.setId(1L);
        appointment.setDateTime(LocalDateTime.of(2024, 1, 15, 10, 30));
        appointment.setPatientId(UUID.fromString("123e4567-e89b-12d3-a456-426614174001"));
        appointment.setDoctorId(doctorId);
        appointment.setDescription("Regular checkup");
        appointment.setStatus(AppointmentStatus.PENDING);
    }

    @Nested
    class GetAppointmentDashboardTests {
        @Test
        void shouldReturnAppointmentDashboardSuccessfully() throws Exception {
            // Given
            when(dashboardService.getAppointmentDashboard(doctorId))
                    .thenReturn(dashboardDTO);

            // When
            ResultActions result = mockMvc.perform(get("/api/v1/appointments/dashboard/doctor/{doctorId}", doctorId)
                    .with(jwt().jwt(jwt).authorities(new SimpleGrantedAuthority("ROLE_DOCTOR"))));

            // Then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.stats.totalAppointments").value(100))
                    .andExpect(jsonPath("$.stats.todayAppointments").value(5))
                    .andExpect(jsonPath("$.stats.pendingAppointments").value(10))
                    .andExpect(jsonPath("$.stats.completedAppointments").value(80))
                    .andExpect(jsonPath("$.stats.cancelledAppointments").value(5))
                    .andExpect(jsonPath("$.appointmentsByStatus.PENDING").value(10))
                    .andExpect(jsonPath("$.appointmentsByStatus.COMPLETED").value(80))
                    .andExpect(jsonPath("$.weeklyAppointments.MONDAY").value(15))
                    .andExpect(jsonPath("$.weeklyAppointments.SUNDAY").value(1));
        }
        @Test
        void shouldReturnInternalServerErrorWhenServiceFails() throws Exception {
            // Given
            when(dashboardService.getAppointmentDashboard(doctorId))
                    .thenThrow(new RuntimeException("Database connection failed"));

            // When
            ResultActions result = mockMvc.perform(get("/api/v1/appointments/dashboard/doctor/{doctorId}", doctorId)
                    .with(jwt().jwt(jwt).authorities(new SimpleGrantedAuthority("ROLE_DOCTOR"))));

            // Then
            result.andExpect(status().isInternalServerError());
        }
    }

    @Nested
    class GetTodayAppointmentsTests {
        @Test
        void shouldReturnTodayAppointmentsSuccessfully() throws Exception {
            // Given
            List<Appointment> todayAppointments = Arrays.asList(appointment);
            when(dashboardService.getTodayAppointments(doctorId))
                    .thenReturn(todayAppointments);

            // When
            ResultActions result = mockMvc.perform(get("/api/v1/appointments/dashboard/doctor/{doctorId}/today", doctorId)
                    .with(jwt().jwt(jwt).authorities(new SimpleGrantedAuthority("ROLE_DOCTOR"))));

            // Then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value(1))
                    .andExpect(jsonPath("$[0].doctorId").value(doctorId.toString()))
                    .andExpect(jsonPath("$[0].status").value("PENDING"))
                    .andExpect(jsonPath("$[0].description").value("Regular checkup"));
        }

        @Test
        void shouldReturnEmptyListWhenNoTodayAppointments() throws Exception {
            // Given
            when(dashboardService.getTodayAppointments(doctorId))
                    .thenReturn(Collections.emptyList());

            // When
            ResultActions result = mockMvc.perform(get("/api/v1/appointments/dashboard/doctor/{doctorId}/today", doctorId)
                    .with(jwt().jwt(jwt).authorities(new SimpleGrantedAuthority("ROLE_DOCTOR"))));

            // Then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$").isEmpty());
        }

        @Test
        void shouldReturnInternalServerErrorWhenServiceFails() throws Exception {
            // Given
            when(dashboardService.getTodayAppointments(doctorId))
                    .thenThrow(new RuntimeException("Service error"));

            // When
            ResultActions result = mockMvc.perform(get("/api/v1/appointments/dashboard/doctor/{doctorId}/today", doctorId)
                    .with(jwt().jwt(jwt).authorities(new SimpleGrantedAuthority("ROLE_DOCTOR"))));

            // Then
            result.andExpect(status().isInternalServerError());
        }
    }

    @Nested
    class GetUpcomingAppointmentsTests {
        @Test
        void shouldReturnUpcomingAppointmentsWithDefaultDays() throws Exception {
            // Given
            List<Appointment> upcomingAppointments = Arrays.asList(appointment);
            when(dashboardService.getUpcomingAppointments(eq(doctorId), eq(7)))
                    .thenReturn(upcomingAppointments);

            // When
            ResultActions result = mockMvc.perform(get("/api/v1/appointments/dashboard/doctor/{doctorId}/upcoming", doctorId)
                    .with(jwt().jwt(jwt).authorities(new SimpleGrantedAuthority("ROLE_DOCTOR"))));

            // Then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value(1))
                    .andExpect(jsonPath("$[0].doctorId").value(doctorId.toString()));
        }

        @Test
        void shouldReturnUpcomingAppointmentsWithCustomDays() throws Exception {
            // Given
            List<Appointment> upcomingAppointments = Arrays.asList(appointment);
            when(dashboardService.getUpcomingAppointments(eq(doctorId), eq(14)))
                    .thenReturn(upcomingAppointments);

            // When
            ResultActions result = mockMvc.perform(get("/api/v1/appointments/dashboard/doctor/{doctorId}/upcoming", doctorId)
                    .param("days", "14")
                    .with(jwt().jwt(jwt).authorities(new SimpleGrantedAuthority("ROLE_DOCTOR"))));

            // Then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value(1));
        }

        @Test
        void shouldReturnEmptyListWhenNoUpcomingAppointments() throws Exception {
            // Given
            when(dashboardService.getUpcomingAppointments(eq(doctorId), any(Integer.class)))
                    .thenReturn(Collections.emptyList());

            // When
            ResultActions result = mockMvc.perform(get("/api/v1/appointments/dashboard/doctor/{doctorId}/upcoming", doctorId)
                    .with(jwt().jwt(jwt).authorities(new SimpleGrantedAuthority("ROLE_DOCTOR"))));

            // Then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$").isEmpty());
        }

        @Test
        void shouldReturnInternalServerErrorWhenServiceFails() throws Exception {
            // Given
            when(dashboardService.getUpcomingAppointments(eq(doctorId), any(Integer.class)))
                    .thenThrow(new RuntimeException("Data access error"));

            // When
            ResultActions result = mockMvc.perform(get("/api/v1/appointments/dashboard/doctor/{doctorId}/upcoming", doctorId)
                    .with(jwt().jwt(jwt).authorities(new SimpleGrantedAuthority("ROLE_DOCTOR"))));

            // Then
            result.andExpect(status().isInternalServerError());
        }
    }

    @Nested
    class GetAppointmentAnalyticsTests {
        @Test
        void shouldReturnAppointmentAnalyticsSuccessfully() throws Exception {
            // Given
            Map<String, Object> analytics = new HashMap<>();
            analytics.put("totalAppointments", 150L);
            analytics.put("completionRate", 85.5);
            analytics.put("averageAppointmentsPerWeek", 37.5);
            analytics.put("cancellationRate", 12.3);

            when(dashboardService.getAppointmentAnalytics(doctorId))
                    .thenReturn(analytics);

            // When
            ResultActions result = mockMvc.perform(get("/api/v1/appointments/dashboard/doctor/{doctorId}/analytics", doctorId)
                    .with(jwt().jwt(jwt).authorities(new SimpleGrantedAuthority("ROLE_DOCTOR"))));

            // Then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalAppointments").value(150))
                    .andExpect(jsonPath("$.completionRate").value(85.5))
                    .andExpect(jsonPath("$.averageAppointmentsPerWeek").value(37.5))
                    .andExpect(jsonPath("$.cancellationRate").value(12.3));
        }

        @Test
        void shouldReturnEmptyAnalyticsWhenNoData() throws Exception {
            // Given
            Map<String, Object> analytics = new HashMap<>();
            analytics.put("totalAppointments", 0L);
            analytics.put("completionRate", 0.0);
            analytics.put("averageAppointmentsPerWeek", 0.0);

            when(dashboardService.getAppointmentAnalytics(doctorId))
                    .thenReturn(analytics);

            // When
            ResultActions result = mockMvc.perform(get("/api/v1/appointments/dashboard/doctor/{doctorId}/analytics", doctorId)
                    .with(jwt().jwt(jwt).authorities(new SimpleGrantedAuthority("ROLE_DOCTOR"))));

            // Then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalAppointments").value(0))
                    .andExpect(jsonPath("$.completionRate").value(0.0));
        }

        @Test
        void shouldReturnInternalServerErrorWhenServiceFails() throws Exception {
            // Given
            when(dashboardService.getAppointmentAnalytics(doctorId))
                    .thenThrow(new RuntimeException("Analytics calculation error"));

            // When
            ResultActions result = mockMvc.perform(get("/api/v1/appointments/dashboard/doctor/{doctorId}/analytics", doctorId)
                    .with(jwt().jwt(jwt).authorities(new SimpleGrantedAuthority("ROLE_DOCTOR"))));

            // Then
            result.andExpect(status().isInternalServerError());
        }
    }

    @Nested
    class SecurityTests {
        @Test
        void shouldReturnUnauthorizedWhenNoJwtToken() throws Exception {
            // When & Then - All endpoints should require authentication
            mockMvc.perform(get("/api/v1/appointments/dashboard/doctor/{doctorId}", doctorId))
                    .andExpect(status().isUnauthorized());

            mockMvc.perform(get("/api/v1/appointments/dashboard/doctor/{doctorId}/today", doctorId))
                    .andExpect(status().isUnauthorized());

            mockMvc.perform(get("/api/v1/appointments/dashboard/doctor/{doctorId}/upcoming", doctorId))
                    .andExpect(status().isUnauthorized());

            mockMvc.perform(get("/api/v1/appointments/dashboard/doctor/{doctorId}/analytics", doctorId))
                    .andExpect(status().isUnauthorized());
        }



    }
}