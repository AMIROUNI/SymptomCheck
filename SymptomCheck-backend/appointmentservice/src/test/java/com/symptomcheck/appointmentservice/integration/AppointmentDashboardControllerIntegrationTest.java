package com.symptomcheck.appointmentservice.integration;

import com.symptomcheck.appointmentservice.enums.AppointmentStatus;
import com.symptomcheck.appointmentservice.models.Appointment;
import com.symptomcheck.appointmentservice.repositories.AppointmentRepository;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
class AppointmentDashboardControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AppointmentRepository appointmentRepository;

    private UUID doctorId;

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
    void setup() {
        appointmentRepository.deleteAll();
        doctorId = UUID.randomUUID();

        // 1️⃣ PENDING tomorrow
        Appointment appt = new Appointment();
        appt.setDateTime(LocalDateTime.now().plusDays(1));
        appt.setPatientId(UUID.randomUUID());
        appt.setDoctorId(doctorId);
        appt.setStatus(AppointmentStatus.PENDING);
        appt.setDescription("Test");
        appointmentRepository.save(appt);

        // 2️⃣ COMPLETED today
        Appointment appt1 = new Appointment();
        appt1.setDateTime(LocalDateTime.now());
        appt1.setPatientId(UUID.randomUUID());
        appt1.setDoctorId(doctorId);
        appt1.setStatus(AppointmentStatus.COMPLETED);
        appt1.setDescription("Test");
        appointmentRepository.save(appt1);

        // 3️⃣ CANCELLED last week
        Appointment appt2 = new Appointment();
        appt2.setDateTime(LocalDateTime.now().minusDays(7));
        appt2.setPatientId(UUID.randomUUID());
        appt2.setDoctorId(doctorId);
        appt2.setStatus(AppointmentStatus.CANCELLED);
        appt2.setDescription("Test");
        appointmentRepository.save(appt2);
    }


    /** --------------------------- DASHBOARD ---------------------------- */

    @Test
    @DisplayName("GET /doctor/{doctorId} → returns dashboard DTO")
    void shouldReturnAppointmentDashboard() throws Exception {

        mockMvc.perform(get("/api/v1/appointments/dashboard/doctor/" + doctorId)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(() -> "ROLE_DOCTOR"))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stats.totalAppointments", is(3)))
                .andExpect(jsonPath("$.stats.todayAppointments", is(1)))
                .andExpect(jsonPath("$.stats.pendingAppointments", is(1)))
                .andExpect(jsonPath("$.stats.completedAppointments", is(1)))
                .andExpect(jsonPath("$.stats.cancelledAppointments", is(1)))

                // by status map
                .andExpect(jsonPath("$.appointmentsByStatus.PENDING", is(1)))
                .andExpect(jsonPath("$.appointmentsByStatus.COMPLETED", is(1)))
                .andExpect(jsonPath("$.appointmentsByStatus.CANCELLED", is(1)))

                // 7 days weekly map
                .andExpect(jsonPath("$.weeklyAppointments", aMapWithSize(7)));
    }

    /** --------------------------- TODAY APPOINTMENTS ---------------------------- */

    @Test
    @DisplayName("GET /doctor/{doctorId}/today → returns today's appointments")
    void shouldReturnTodayAppointments() throws Exception {

        mockMvc.perform(get("/api/v1/appointments/dashboard/doctor/" + doctorId + "/today")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(() -> "ROLE_DOCTOR"))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1))) // we inserted 1 today
                .andExpect(jsonPath("$[0].status", is("COMPLETED")));
    }

    /** --------------------------- UPCOMING APPOINTMENTS ---------------------------- */

    @Test
    @DisplayName("GET /doctor/{doctorId}/upcoming → returns future appointments")
    void shouldReturnUpcomingAppointments() throws Exception {

        // Add a future appointment
        Appointment appt = new Appointment();
        appt.setDoctorId(doctorId);
        appt.setPatientId(UUID.randomUUID());
        appt.setDateTime(LocalDateTime.now().plusDays(3));
        appt.setStatus(AppointmentStatus.PENDING);
        appt.setDescription(null);
        appt.setPaymentTransactionId(null);
        appt.setUpdatedAt(null);

        appointmentRepository.save(appt);


        mockMvc.perform(get("/api/v1/appointments/dashboard/doctor/" + doctorId + "/upcoming?days=7")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(() -> "ROLE_DOCTOR"))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2 ))); // only the future one
    }

    /** --------------------------- ANALYTICS ---------------------------- */

    @Test
    @DisplayName("GET /doctor/{doctorId}/analytics → returns analytics map")
    void shouldReturnAnalytics() throws Exception {

        mockMvc.perform(get("/api/v1/appointments/dashboard/doctor/" + doctorId + "/analytics")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(() -> "ROLE_DOCTOR"))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalAppointments", is(3)))
                .andExpect(jsonPath("$.completionRate", is(approx(33.33f))))
                .andExpect(jsonPath("$.averageAppointmentsPerWeek", is(closeTo(0.75, 1.0))));
    }

    // Helper: approximate number matching
    private static Matcher<Double> approx(double value) {
        return closeTo(value, 0.5);
    }
}
