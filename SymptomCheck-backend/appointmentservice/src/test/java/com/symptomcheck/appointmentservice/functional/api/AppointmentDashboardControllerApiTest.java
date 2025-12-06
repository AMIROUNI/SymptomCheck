package com.symptomcheck.appointmentservice.functional.api;

import com.symptomcheck.appointmentservice.enums.AppointmentStatus;
import com.symptomcheck.appointmentservice.models.Appointment;
import com.symptomcheck.appointmentservice.repositories.AppointmentRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Functional tests for AppointmentDashboardController.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AppointmentDashboardControllerApiTest {

    @Container
    static final PostgreSQLContainer<?> POSTGRES_CONTAINER =
            new PostgreSQLContainer<>("postgres:15-alpine")
                    .withDatabaseName("testdb")
                    .withUsername("test")
                    .withPassword("test");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        if (!POSTGRES_CONTAINER.isRunning()) {
            POSTGRES_CONTAINER.start();
        }
        registry.add("spring.datasource.url", POSTGRES_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRES_CONTAINER::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "update");
    }

    @AfterEach
    void cleanup() {
        appointmentRepository.deleteAll();
    }

    /* ----------------------
       JWT helper for DOCTOR role
       ---------------------- */
    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor doctorJwt() {
        Jwt jwt = Jwt.withTokenValue("doctor-token")
                .header("alg", "none")
                .claim("sub", "doctor-sub")
                .claim("realm_access", java.util.Map.of("roles", List.of("DOCTOR")))
                .build();
        return jwt().jwt(jwt).authorities(new SimpleGrantedAuthority("ROLE_DOCTOR"));
    }

    /* ----------------------
       Helper to save appointment
       ---------------------- */
    private Appointment saveAppointment(LocalDateTime dateTime, UUID patientId, UUID doctorId, AppointmentStatus status, String desc) {
        Appointment a = new Appointment();
        a.setDateTime(dateTime);
        a.setPatientId(patientId);
        a.setDoctorId(doctorId);
        a.setStatus(status);
        a.setDescription(desc);
        a.setCreatedAt(Instant.now());
        a.setUpdatedAt(Instant.now());
        return appointmentRepository.save(a);
    }

    @Test
    void getAppointmentDashboard_returnsExpectedStructure() throws Exception {
        UUID p = UUID.randomUUID();
        UUID d = UUID.randomUUID();

        // seed: 3 appointments across days and statuses
        saveAppointment(LocalDateTime.now().minusDays(1).withHour(9), p, d, AppointmentStatus.PENDING, "yesterday");
        saveAppointment(LocalDateTime.now().withHour(10), p, d, AppointmentStatus.COMPLETED, "today");
        saveAppointment(LocalDateTime.now().plusDays(2).withHour(11), p, d, AppointmentStatus.CONFIRMED, "future");

        mockMvc.perform(get("/api/v1/appointments/dashboard/doctor/{doctorId}", d)
                        .with(doctorJwt())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                // top-level DTO shape: expecting stats, appointmentsByStatus, weeklyAppointments
                .andExpect(jsonPath("$.stats").exists())
                .andExpect(jsonPath("$.appointmentsByStatus").exists())
                .andExpect(jsonPath("$.weeklyAppointments").exists())
                // stats inside should contain totalAppointments
                .andExpect(jsonPath("$.stats.totalAppointments", is(3)))
                .andExpect(jsonPath("$.appointmentsByStatus.PENDING", isA(Number.class)))
                .andExpect(jsonPath("$.weeklyAppointments", aMapWithSize(7)));
    }

    @Test
    void getTodayAppointments_returnsOnlyTodayForDoctor() throws Exception {
        UUID p = UUID.randomUUID();
        UUID d = UUID.randomUUID();

        // one appointment today, one yesterday
        saveAppointment(LocalDateTime.now().withHour(9), p, d, AppointmentStatus.PENDING, "today1");
        saveAppointment(LocalDateTime.now().minusDays(1).withHour(10), p, d, AppointmentStatus.PENDING, "yesterday");

        mockMvc.perform(get("/api/v1/appointments/dashboard/doctor/{doctorId}/today", d)
                        .with(doctorJwt())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].doctorId", is(d.toString())));
    }

    @Test
    void getUpcomingAppointments_returnsAppointmentsWithinDaysWindow() throws Exception {
        UUID p = UUID.randomUUID();
        UUID d = UUID.randomUUID();

        // now, +3 days (within default 7), +10 days (outside default)
        saveAppointment(LocalDateTime.now().plusDays(1), p, d, AppointmentStatus.PENDING, "in1");
        saveAppointment(LocalDateTime.now().plusDays(3), p, d, AppointmentStatus.PENDING, "in3");
        saveAppointment(LocalDateTime.now().plusDays(10), p, d, AppointmentStatus.PENDING, "in10");

        mockMvc.perform(get("/api/v1/appointments/dashboard/doctor/{doctorId}/upcoming", d)
                        .with(doctorJwt())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].doctorId", is(d.toString())));
    }

    @Test
    void getAppointmentAnalytics_returnsMetrics() throws Exception {
        UUID p = UUID.randomUUID();
        UUID d = UUID.randomUUID();

        // seed: 4 appointments, 2 completed
        saveAppointment(LocalDateTime.now().minusDays(10), p, d, AppointmentStatus.COMPLETED, "c1");
        saveAppointment(LocalDateTime.now().minusDays(7), p, d, AppointmentStatus.COMPLETED, "c2");
        saveAppointment(LocalDateTime.now().minusDays(3), p, d, AppointmentStatus.PENDING, "p1");
        saveAppointment(LocalDateTime.now().plusDays(2), p, d, AppointmentStatus.CONFIRMED, "f1");

        mockMvc.perform(get("/api/v1/appointments/dashboard/doctor/{doctorId}/analytics", d)
                        .with(doctorJwt())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalAppointments", is(4)))
                .andExpect(jsonPath("$.completionRate", isA(Number.class)))
                .andExpect(jsonPath("$.averageAppointmentsPerWeek", isA(Number.class)));
    }
}
