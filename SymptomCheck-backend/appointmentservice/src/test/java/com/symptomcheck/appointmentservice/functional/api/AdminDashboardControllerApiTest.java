package com.symptomcheck.appointmentservice.functional.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.symptomcheck.appointmentservice.SecurityTestConfig;
import com.symptomcheck.appointmentservice.controllers.AdminDashboardController;
import com.symptomcheck.appointmentservice.dtos.admindashboarddto.AdminAppointmentDto;
import com.symptomcheck.appointmentservice.dtos.admindashboarddto.AppointmentStatsDto;
import com.symptomcheck.appointmentservice.services.AdminDashboardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@WebMvcTest(AdminDashboardController.class)
@Import(SecurityTestConfig.class)
class AdminDashboardControllerApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AdminDashboardService adminDashboardService;

    private UUID doctorId;
    private Long appointmentId;
    private AppointmentStatsDto statsDto;
    private AdminAppointmentDto adminAppointmentDto;
    private List<AdminAppointmentDto> appointmentList;
    private Jwt jwt;

    @BeforeEach
    void setUp() {
        doctorId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        appointmentId = 1L;

        // Create mock JWT for authentication
        jwt = Jwt.withTokenValue("mock-jwt-token")
                .header("alg", "none")
                .claim("sub", "admin-user")
                .claim("scope", "openid")
                .claim("roles", Arrays.asList("ADMIN"))
                .build();

        // Create real JSON-compatible AppointmentStatsDto
        Map<String, Long> statusDistribution = new HashMap<>();
        statusDistribution.put("PENDING", 10L);
        statusDistribution.put("CONFIRMED", 25L);
        statusDistribution.put("COMPLETED", 60L);
        statusDistribution.put("CANCELLED", 5L);

        statsDto = new AppointmentStatsDto();
        statsDto.setTotalAppointments(100L);
        statsDto.setPendingAppointments(10L);
        statsDto.setConfirmedAppointments(25L);
        statsDto.setCompletedAppointments(60L);
        statsDto.setCancelledAppointments(5L);
        statsDto.setTodayAppointments(5L);
        statsDto.setWeeklyAppointments(20L);
        statsDto.setStatusDistribution(statusDistribution);
        statsDto.setLastUpdated(LocalDateTime.of(2024, 1, 15, 14, 30));

        // Create real JSON-compatible AdminAppointmentDto
        adminAppointmentDto = new AdminAppointmentDto();
        adminAppointmentDto.setId(appointmentId);
        adminAppointmentDto.setDateTime(LocalDateTime.of(2024, 1, 15, 10, 30));
        adminAppointmentDto.setPatientId("123e4567-e89b-12d3-a456-426614174001");
        adminAppointmentDto.setDoctorId(doctorId.toString());
        adminAppointmentDto.setStatus("PENDING");
        adminAppointmentDto.setDescription("Regular checkup");
        adminAppointmentDto.setPaymentTransactionId(1001L);
        adminAppointmentDto.setCreatedAt(Instant.parse("2024-01-10T10:00:00Z"));
        adminAppointmentDto.setUpdatedAt(Instant.parse("2024-01-12T14:30:00Z"));

        // Create list of appointments
        appointmentList = Arrays.asList(adminAppointmentDto);
    }

    @Nested
    class GetDashboardStatsTests {
        @Test
        void shouldReturnDashboardStatsSuccessfully() throws Exception {
            // Given
            when(adminDashboardService.getAppointmentStatistics())
                    .thenReturn(statsDto);

            // When
            ResultActions result = mockMvc.perform(get("/api/admin/dashboard/stats")
                    .with(jwt().jwt(jwt).authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))));

            // Then $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalAppointments").value(100))
                    .andExpect(jsonPath("$.pendingAppointments").value(10))
                    .andExpect(jsonPath("$.confirmedAppointments").value(25))
                    .andExpect(jsonPath("$.completedAppointments").value(60))
                    .andExpect(jsonPath("$.cancelledAppointments").value(5))
                    .andExpect(jsonPath("$.todayAppointments").value(5))
                    .andExpect(jsonPath("$.weeklyAppointments").value(20))
                    .andExpect(jsonPath("$.statusDistribution.PENDING").value(10))
                    .andExpect(jsonPath("$.statusDistribution.COMPLETED").value(60))
                    .andExpect(jsonPath("$.lastUpdated").exists());
        }

        @Test
        void shouldReturnEmptyStatsWhenNoData() throws Exception {
            // Given
            AppointmentStatsDto emptyStats = new AppointmentStatsDto();
            emptyStats.setTotalAppointments(0L);
            emptyStats.setPendingAppointments(0L);
            emptyStats.setConfirmedAppointments(0L);
            emptyStats.setCompletedAppointments(0L);
            emptyStats.setCancelledAppointments(0L);
            emptyStats.setTodayAppointments(0L);
            emptyStats.setWeeklyAppointments(0L);
            emptyStats.setStatusDistribution(Map.of());
            emptyStats.setLastUpdated(LocalDateTime.now());

            when(adminDashboardService.getAppointmentStatistics())
                    .thenReturn(emptyStats);

            // When
            ResultActions result = mockMvc.perform(get("/api/admin/dashboard/stats")
                    .with(jwt().jwt(jwt).authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))));

            // Then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalAppointments").value(0))
                    .andExpect(jsonPath("$.statusDistribution").isMap())
                    .andExpect(jsonPath("$.statusDistribution").isEmpty());
        }

        @Test
        void shouldReturnUnauthorizedWhenNoAuthentication() throws Exception {
            // When
            ResultActions result = mockMvc.perform(get("/api/admin/dashboard/stats"));

            // Then
            result.andExpect(status().isUnauthorized());
        }
    }

    @Nested
    class GetAllAppointmentsTests {
        @Test
        void shouldReturnAllAppointmentsSuccessfully() throws Exception {
            // Given
            when(adminDashboardService.getAllAppointments())
                    .thenReturn(appointmentList);

            // When
            ResultActions result = mockMvc.perform(get("/api/admin/appointments")
                    .with(jwt().jwt(jwt).authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))));

            // Then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value(1))
                    .andExpect(jsonPath("$[0].patientId").value("123e4567-e89b-12d3-a456-426614174001"))
                    .andExpect(jsonPath("$[0].doctorId").value(doctorId.toString()))
                    .andExpect(jsonPath("$[0].status").value("PENDING"))
                    .andExpect(jsonPath("$[0].description").value("Regular checkup"))
                    .andExpect(jsonPath("$[0].paymentTransactionId").value(1001))
                    .andExpect(jsonPath("$[0].createdAt").exists())
                    .andExpect(jsonPath("$[0].updatedAt").exists());
        }

        @Test
        void shouldReturnEmptyListWhenNoAppointments() throws Exception {
            // Given
            when(adminDashboardService.getAllAppointments())
                    .thenReturn(Collections.emptyList());

            // When
            ResultActions result = mockMvc.perform(get("/api/admin/appointments")
                    .with(jwt().jwt(jwt).authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))));

            // Then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$").isEmpty());
        }

        @Test
        void shouldReturnUnauthorizedWhenNoAuthentication() throws Exception {
            // When
            ResultActions result = mockMvc.perform(get("/api/admin/appointments"));

            // Then
            result.andExpect(status().isUnauthorized());
        }
    }

    @Nested
    class GetAppointmentsByStatusTests {
        @Test
        void shouldReturnAppointmentsByStatusSuccessfully() throws Exception {
            // Given
            when(adminDashboardService.getAppointmentsByStatus("PENDING"))
                    .thenReturn(appointmentList);

            // When
            ResultActions result = mockMvc.perform(get("/api/admin/appointments/status/{status}", "PENDING")
                    .with(jwt().jwt(jwt).authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))));

            // Then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value(1))
                    .andExpect(jsonPath("$[0].status").value("PENDING"));
        }

        @Test
        void shouldReturnEmptyListWhenNoAppointmentsWithStatus() throws Exception {
            // Given
            when(adminDashboardService.getAppointmentsByStatus("COMPLETED"))
                    .thenReturn(Collections.emptyList());

            // When
            ResultActions result = mockMvc.perform(get("/api/admin/appointments/status/{status}", "COMPLETED")
                    .with(jwt().jwt(jwt).authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))));

            // Then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$").isEmpty());
        }

        @Test
        void shouldHandleCaseInsensitiveStatus() throws Exception {
            // Given
            when(adminDashboardService.getAppointmentsByStatus("pending"))
                    .thenReturn(appointmentList);

            // When
            ResultActions result = mockMvc.perform(get("/api/admin/appointments/status/{status}", "pending")
                    .with(jwt().jwt(jwt).authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))));

            // Then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].status").value("PENDING"));
        }

        @Test
        void shouldReturnUnauthorizedWhenNoAuthentication() throws Exception {
            // When
            ResultActions result = mockMvc.perform(get("/api/admin/appointments/status/{status}", "PENDING"));

            // Then
            result.andExpect(status().isUnauthorized());
        }
    }

    @Nested
    class GetAppointmentsByDateRangeTests {
        @Test
        void shouldReturnAppointmentsByDateRangeSuccessfully() throws Exception {

            when(adminDashboardService.getAppointmentsByDateRange(any(LocalDateTime.class), any(LocalDateTime.class)))
                    .thenReturn(appointmentList);

            // When
            ResultActions result = mockMvc.perform(get("/api/admin/appointments/date-range")
                    .param("start", "2024-01-01T00:00:00")
                    .param("end", "2024-01-31T23:59:59")
                    .with(jwt().jwt(jwt).authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))));

            // Then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value(1))
                    .andExpect(jsonPath("$[0].dateTime").exists());
        }

        @Test
        void shouldReturnEmptyListWhenNoAppointmentsInDateRange() throws Exception {
            // Given
            when(adminDashboardService.getAppointmentsByDateRange(any(LocalDateTime.class), any(LocalDateTime.class)))
                    .thenReturn(Collections.emptyList());

            // When
            ResultActions result = mockMvc.perform(get("/api/admin/appointments/date-range")
                    .param("start", "2024-02-01T00:00:00")
                    .param("end", "2024-02-28T23:59:59")
                    .with(jwt().jwt(jwt).authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))));

            // Then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$").isEmpty());
        }

        @Test
        void shouldReturnUnauthorizedWhenNoAuthentication() throws Exception {
            // When
            ResultActions result = mockMvc.perform(get("/api/admin/appointments/date-range")
                    .param("start", "2024-01-01T00:00:00")
                    .param("end", "2024-01-31T23:59:59"));

            // Then
            result.andExpect(status().isUnauthorized());
        }
    }

    @Nested
    class GetAppointmentsByDoctorTests {
        @Test
        void shouldReturnAppointmentsByDoctorSuccessfully() throws Exception {
            // Given
            when(adminDashboardService.getAppointmentsByDoctor(doctorId))
                    .thenReturn(appointmentList);

            // When
            ResultActions result = mockMvc.perform(get("/api/admin/appointments/doctor/{doctorId}", doctorId)
                    .with(jwt().jwt(jwt).authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))));

            // Then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value(1))
                    .andExpect(jsonPath("$[0].doctorId").value(doctorId.toString()));
        }

        @Test
        void shouldReturnEmptyListWhenDoctorHasNoAppointments() throws Exception {
            // Given
            UUID unknownDoctorId = UUID.fromString("123e4567-e89b-12d3-a456-426614174999");
            when(adminDashboardService.getAppointmentsByDoctor(unknownDoctorId))
                    .thenReturn(Collections.emptyList());

            // When
            ResultActions result = mockMvc.perform(get("/api/admin/appointments/doctor/{doctorId}", unknownDoctorId)
                    .with(jwt().jwt(jwt).authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))));

            // Then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$").isEmpty());
        }

        @Test
        void shouldReturnUnauthorizedWhenNoAuthentication() throws Exception {
            // When
            ResultActions result = mockMvc.perform(get("/api/admin/appointments/doctor/{doctorId}", doctorId));

            // Then
            result.andExpect(status().isUnauthorized());
        }
    }

    @Nested
    class UpdateAppointmentStatusTests {
        @Test
        void shouldUpdateAppointmentStatusSuccessfully() throws Exception {
            // Given
            AdminAppointmentDto updatedAppointment = new AdminAppointmentDto();
            updatedAppointment.setId(appointmentId);
            updatedAppointment.setDateTime(LocalDateTime.of(2024, 1, 15, 10, 30));
            updatedAppointment.setPatientId("123e4567-e89b-12d3-a456-426614174001");
            updatedAppointment.setDoctorId(doctorId.toString());
            updatedAppointment.setStatus("CONFIRMED");
            updatedAppointment.setDescription("Regular checkup");
            updatedAppointment.setPaymentTransactionId(1001L);
            updatedAppointment.setCreatedAt(Instant.parse("2024-01-10T10:00:00Z"));
            updatedAppointment.setUpdatedAt(Instant.now());

            when(adminDashboardService.updateAppointmentStatus(appointmentId, "CONFIRMED"))
                    .thenReturn(updatedAppointment);

            // When
            ResultActions result = mockMvc.perform(put("/api/admin/appointments/{appointmentId}/status", appointmentId)
                    .param("status", "CONFIRMED")
                    .contentType(MediaType.APPLICATION_JSON)
                    .with(jwt().jwt(jwt).authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))));

            // Then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.status").value("CONFIRMED"))
                    .andExpect(jsonPath("$.updatedAt").exists());
        }

        @Test
        void shouldHandleCaseInsensitiveStatusInUpdate() throws Exception {
            // Given
            AdminAppointmentDto updatedAppointment = new AdminAppointmentDto();
            updatedAppointment.setId(appointmentId);
            updatedAppointment.setStatus("COMPLETED");
            updatedAppointment.setUpdatedAt(Instant.now());

            when(adminDashboardService.updateAppointmentStatus(appointmentId, "completed"))
                    .thenReturn(updatedAppointment);

            // When
            ResultActions result = mockMvc.perform(put("/api/admin/appointments/{appointmentId}/status", appointmentId)
                    .param("status", "completed")
                    .contentType(MediaType.APPLICATION_JSON)
                    .with(jwt().jwt(jwt).authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))));

            // Then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("COMPLETED"));
        }

    }

    @Test
    void shouldAllowAccessWithAdminRole() throws Exception {
        // Test that all endpoints work with ADMIN role
        when(adminDashboardService.getAppointmentStatistics()).thenReturn(statsDto);
        when(adminDashboardService.getAllAppointments()).thenReturn(Collections.emptyList());
        when(adminDashboardService.getAppointmentsByStatus(anyString())).thenReturn(Collections.emptyList());
        when(adminDashboardService.getAppointmentsByDateRange(any(), any())).thenReturn(Collections.emptyList());
        when(adminDashboardService.getAppointmentsByDoctor(any())).thenReturn(Collections.emptyList());
        when(adminDashboardService.updateAppointmentStatus(any(), any())).thenReturn(adminAppointmentDto);

        // All endpoints should return 200 with ADMIN role
        mockMvc.perform(get("/api/admin/dashboard/stats")
                        .with(jwt().jwt(jwt).authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/admin/appointments")
                        .with(jwt().jwt(jwt).authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/admin/appointments/status/PENDING")
                        .with(jwt().jwt(jwt).authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/admin/appointments/date-range")
                        .param("start", "2024-01-01T00:00:00")
                        .param("end", "2024-01-31T23:59:59")
                        .with(jwt().jwt(jwt).authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/admin/appointments/doctor/{doctorId}", doctorId)
                        .with(jwt().jwt(jwt).authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isOk());

        mockMvc.perform(put("/api/admin/appointments/{appointmentId}/status", appointmentId)
                        .param("status", "CONFIRMED")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(jwt().jwt(jwt).authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isOk());
    }
}