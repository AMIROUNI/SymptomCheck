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
import java.time.LocalDateTime;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AdminDashboardControllerApiTest {

    // Testcontainers PostgreSQL container (you may add .withReuse(true) locally but be careful about state)
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

    // Defensive DynamicPropertySource: ensure the container is started before reading mapped ports.
    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        if (!POSTGRES_CONTAINER.isRunning()) {
            POSTGRES_CONTAINER.start();
        }

        registry.add("spring.datasource.url", POSTGRES_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRES_CONTAINER::getPassword);

        // Ensure Hibernate creates schema for tests (optional - adapt to your test profile)
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "update");
    }

    @AfterEach
    void cleanup() {
        appointmentRepository.deleteAll();
    }

    /**
     * Helper: create an appointment instance ready to be saved.
     *
     * IMPORTANT: adapt this to your actual Appointment entity if field names / constructors differ.
     */
    private Appointment createAppointment(LocalDateTime dateTime,
                                          UUID patientId,
                                          UUID doctorId,
                                          AppointmentStatus status,
                                          String description) {
        Appointment a = new Appointment();
        a.setDateTime(dateTime);
        a.setPatientId(patientId);
        a.setDoctorId(doctorId);
        a.setStatus(status);
        a.setDescription(description);
        a.setPaymentTransactionId(null);
        a.setCreatedAt(Instant.now());
        a.setUpdatedAt(Instant.now());
        return a;
    }

    /**
     * Builds a Jwt post-processor that contains realm roles (for realism) and explicitly grants ROLE_ADMIN
     * authority so security checks will pass reliably in tests.
     */
    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor adminJwt() {
        Jwt jwt = Jwt.withTokenValue("test-token")
                .header("alg", "none")
                // keep realistic claim so your app's converter could read it if needed
                .claim("realm_access", java.util.Map.of("roles", java.util.List.of("ADMIN")))
                .build();

        // Attach the JWT and explicitly grant ROLE_ADMIN to avoid mapping issues in tests
        return jwt().jwt(jwt).authorities(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }

    @Test
    void getDashboardStats_returnsCountsAndStatusDistribution() throws Exception {
        // seed
        UUID patient1 = UUID.randomUUID();
        UUID doctor1 = UUID.randomUUID();

        appointmentRepository.save(createAppointment(LocalDateTime.now().minusDays(1), patient1, doctor1, AppointmentStatus.PENDING, "p1"));
        appointmentRepository.save(createAppointment(LocalDateTime.now(), patient1, doctor1, AppointmentStatus.CONFIRMED, "p2"));
        appointmentRepository.save(createAppointment(LocalDateTime.now(), patient1, doctor1, AppointmentStatus.COMPLETED, "p3"));

        mockMvc.perform(get("/api/admin/dashboard/stats")
                        .with(adminJwt())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalAppointments", is(3)))
                .andExpect(jsonPath("$.statusDistribution.PENDING", isA(Number.class)))
                .andExpect(jsonPath("$.statusDistribution.CONFIRMED", isA(Number.class)))
                .andExpect(jsonPath("$.statusDistribution.COMPLETED", isA(Number.class)))
                .andExpect(jsonPath("$.lastUpdated").exists());
    }

    @Test
    void getAllAppointments_returnsList() throws Exception {
        UUID patient = UUID.randomUUID();
        UUID doctor = UUID.randomUUID();

        Appointment saved = appointmentRepository.save(
                createAppointment(LocalDateTime.now().plusDays(1), patient, doctor, AppointmentStatus.PENDING, "future appointment")
        );

        mockMvc.perform(get("/api/admin/appointments")
                        .with(adminJwt())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(saved.getId().intValue())))
                .andExpect(jsonPath("$[0].status", is("PENDING")));
    }

    @Test
    void getAppointmentsByStatus_filtersCorrectly() throws Exception {
        UUID p = UUID.randomUUID();
        UUID d = UUID.randomUUID();

        appointmentRepository.save(createAppointment(LocalDateTime.now(), p, d, AppointmentStatus.CONFIRMED, "c1"));
        appointmentRepository.save(createAppointment(LocalDateTime.now(), p, d, AppointmentStatus.PENDING, "p1"));

        mockMvc.perform(get("/api/admin/appointments/status/CONFIRMED")
                        .with(adminJwt())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].status", is("CONFIRMED")));
    }

    @Test
    void updateAppointmentStatus_changesStatus() throws Exception {
        UUID p = UUID.randomUUID();
        UUID d = UUID.randomUUID();

        Appointment appt = appointmentRepository.save(createAppointment(LocalDateTime.now(), p, d, AppointmentStatus.PENDING, "to update"));

        mockMvc.perform(put("/api/admin/appointments/{appointmentId}/status", appt.getId())
                        .with(adminJwt())
                        .param("status", "CONFIRMED")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(appt.getId().intValue())))
                .andExpect(jsonPath("$.status", is("CONFIRMED")));
    }

}
