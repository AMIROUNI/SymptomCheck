package com.symptomcheck.appointmentservice.functional.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.symptomcheck.appointmentservice.dtos.AppointmentDto;
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
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Functional tests for AppointmentController (endpoints under /api/v1/appointments).
 *
 * - Uses Testcontainers Postgres (defensive start in DynamicPropertySource).
 * - Uses MockMvc with JWT post-processor to attach roles (ROLE_PATIENT, ROLE_DOCTOR).
 * - Seeds data directly through AppointmentRepository.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AppointmentControllerApiTest {

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

    @Autowired
    private ObjectMapper objectMapper;

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        // defensive start to avoid "mapped port" race
        if (!POSTGRES_CONTAINER.isRunning()) {
            POSTGRES_CONTAINER.start();
        }

        registry.add("spring.datasource.url", POSTGRES_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRES_CONTAINER::getPassword);
        // allow Hibernate to create schema for tests
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "update");
    }

    @AfterEach
    void cleanup() {
        appointmentRepository.deleteAll();
    }

    /* ----------------------
       JWT helpers (attach authorities to ensure roles are present)
       ---------------------- */
    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor patientJwt() {
        Jwt jwt = Jwt.withTokenValue("patient-token")
                .header("alg", "none")
                .claim("sub", "patient-sub")
                .claim("realm_access", java.util.Map.of("roles", List.of("PATIENT")))
                .build();
        return jwt().jwt(jwt).authorities(new SimpleGrantedAuthority("ROLE_PATIENT"));
    }

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor doctorJwt() {
        Jwt jwt = Jwt.withTokenValue("doctor-token")
                .header("alg", "none")
                .claim("sub", "doctor-sub")
                .claim("realm_access", java.util.Map.of("roles", List.of("DOCTOR")))
                .build();
        return jwt().jwt(jwt).authorities(new SimpleGrantedAuthority("ROLE_DOCTOR"));
    }

    /**
     * Helper to build and save an Appointment quickly.
     */
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
    void createAppointment_withPatientJwt_returnsSavedAppointment() throws Exception {
        UUID patientId = UUID.randomUUID();
        UUID doctorId = UUID.randomUUID();

        AppointmentDto dto = new AppointmentDto();
        dto.setDateTime(LocalDateTime.now().plusDays(2).withHour(10).withMinute(0).withSecond(0).withNano(0));
        dto.setPatientId(patientId);
        dto.setDoctorId(doctorId);
        dto.setDescription("Functional test create");

        mockMvc.perform(post("/api/v1/appointments/create")
                        .with(patientJwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.patientId", is(patientId.toString())))
                .andExpect(jsonPath("$.doctorId", is(doctorId.toString())))
                .andExpect(jsonPath("$.description", is("Functional test create")));
    }

    @Test
    void getByDoctor_returnsAppointments_forDoctor() throws Exception {
        UUID p1 = UUID.randomUUID();
        UUID d = UUID.randomUUID();

        saveAppointment(LocalDateTime.now().plusDays(1).withHour(9), p1, d, AppointmentStatus.PENDING, "a1");
        saveAppointment(LocalDateTime.now().plusDays(3).withHour(11), p1, d, AppointmentStatus.CONFIRMED, "a2");

        mockMvc.perform(get("/api/v1/appointments/doctor/{doctorId}", d)
                        .with(doctorJwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].doctorId", is(d.toString())));
    }

    @Test
    void getTakenAppointments_returnsTimes_forGivenDate() throws Exception {
        UUID p1 = UUID.randomUUID();
        UUID d = UUID.randomUUID();
        LocalDate date = LocalDate.now().plusDays(4);

        saveAppointment(date.atTime(9, 0), p1, d, AppointmentStatus.PENDING, "t1");
        saveAppointment(date.atTime(14, 30), p1, d, AppointmentStatus.PENDING, "t2");

        mockMvc.perform(get("/api/v1/appointments/taken-appointments/{doctorId}", d)
                        .param("date", date.toString())
                        .with(doctorJwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0]", not(emptyString())));
    }

    @Test
    void getByPatient_returnsAppointments_forPatient() throws Exception {
        UUID patient = UUID.randomUUID();
        UUID doctor = UUID.randomUUID();

        saveAppointment(LocalDateTime.now().plusDays(1), patient, doctor, AppointmentStatus.PENDING, "byPatient1");
        saveAppointment(LocalDateTime.now().plusDays(2), patient, doctor, AppointmentStatus.CONFIRMED, "byPatient2");

        mockMvc.perform(get("/api/v1/appointments/{userId}", patient)
                        .with(patientJwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].patientId", is(patient.toString())));
    }

    @Test
    void updateStatus_withDoctorJwt_changesStatus() throws Exception {
        UUID p = UUID.randomUUID();
        UUID d = UUID.randomUUID();

        Appointment appt = saveAppointment(LocalDateTime.now().plusDays(1), p, d, AppointmentStatus.PENDING, "to-update");

        // statusNumber 1 -> CONFIRMED (based on enum order: PENDING=0, CONFIRMED=1, CANCELLED=2, COMPLETED=3)
        mockMvc.perform(put("/api/v1/appointments/{id}/status/{statusNumber}", appt.getId(), 1)
                        .with(doctorJwt()))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        // double-check DB
        Appointment updated = appointmentRepository.findById(appt.getId()).orElseThrow();
        org.assertj.core.api.Assertions.assertThat(updated.getStatus()).isEqualTo(AppointmentStatus.CONFIRMED);
    }
}
