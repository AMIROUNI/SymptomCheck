package com.symptomcheck.appointmentservice;

import com.symptomcheck.appointmentservice.dtos.AppointmentDto;
import com.symptomcheck.appointmentservice.enums.AppointmentStatus;
import com.symptomcheck.appointmentservice.models.Appointment;
import com.symptomcheck.appointmentservice.repositories.AppointmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
@Import(SecurityTestConfig.class)
class AppointmentserviceApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.jpa.properties.hibernate.dialect", () -> "org.hibernate.dialect.PostgreSQLDialect");
        registry.add("spring.sql.init.mode", () -> "always");
    }

    private AppointmentDto appointmentDto;
    private Appointment testAppointment;
    private UUID testDoctorId;
    private UUID testPatientId;

    @BeforeEach
    void setUp() {
        // Clean database before each test
        appointmentRepository.deleteAll();

        // Initialize test UUIDs
        testDoctorId = UUID.fromString("d4e5f6a1-b2c3-4567-89ab-cdef01234567");
        testPatientId = UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890");

        // Create test appointment DTO
        appointmentDto = new AppointmentDto();
        appointmentDto.setDateTime(LocalDateTime.now().plusDays(1));
        appointmentDto.setPatientId(testPatientId);
        appointmentDto.setDoctorId(testDoctorId);
        appointmentDto.setDescription("Regular checkup appointment");

        // Create and save a test appointment for tests that need existing data
        testAppointment = new Appointment();
        testAppointment.setPatientId(testPatientId);
        testAppointment.setDoctorId(testDoctorId);
        testAppointment.setDateTime(LocalDateTime.now().plusDays(1));
        testAppointment.setStatus(AppointmentStatus.PENDING);
        testAppointment.setDescription("Test appointment");
        testAppointment.setCreatedAt(Instant.now());
        testAppointment = appointmentRepository.save(testAppointment);
    }

    @Test
    void contextLoads() {
        // Basic context test
    }

    @Nested
    class AppointmentControllerTests {



        @Test
        @WithMockUser(roles = "DOCTOR")
        void whenGetAppointmentsByDoctor_shouldReturnOk() throws Exception {
            mockMvc.perform(get("/api/v1/appointments/doctor/{doctorId}", testDoctorId))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        @Test
        @WithMockUser(roles = "DOCTOR")
        void whenGetTakenAppointments_shouldReturnOk() throws Exception {
            LocalDate date = LocalDate.now().plusDays(1);

            mockMvc.perform(get("/api/v1/appointments/taken-appointments/{doctorId}", testDoctorId)
                            .param("date", date.toString()))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        @Test
        @WithMockUser(roles = "PATIENT")
        void whenGetAppointmentsByPatient_shouldReturnOk() throws Exception {
            mockMvc.perform(get("/api/v1/appointments/{userId}", testPatientId))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        @Test
        @WithMockUser(roles = "DOCTOR")
        void whenUpdateAppointmentStatus_shouldReturnOk() throws Exception {
            // Use the existing appointment ID from setUp
            mockMvc.perform(put("/api/v1/appointments/{id}/status/{statusNumber}",
                            testAppointment.getId(), 1)
                            .with(csrf()))
                    .andDo(print())
                    .andExpect(status().isOk());
        }
    }

    @Nested
    class AdminDashboardControllerTests {

        @Test
        @WithMockUser(roles = "ADMIN")
        void whenAdminGetsDashboardStats_shouldReturnOk() throws Exception {
            mockMvc.perform(get("/api/admin/dashboard/stats"))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void whenAdminGetsAllAppointments_shouldReturnOk() throws Exception {
            mockMvc.perform(get("/api/admin/appointments"))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void whenAdminGetsAppointmentsByStatus_shouldReturnOk() throws Exception {
            mockMvc.perform(get("/api/admin/appointments/status/{status}", "PENDING"))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void whenAdminUpdatesAppointmentStatus_withExistingAppointment_shouldReturnOk() throws Exception {
            // Use the existing appointment ID from setUp
            mockMvc.perform(put("/api/admin/appointments/{appointmentId}/status",
                            testAppointment.getId())
                            .with(csrf())
                            .param("status", "CONFIRMED"))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        @Test
        @WithMockUser(roles = "PATIENT")
        void whenNonAdminTriesToAccessAdminEndpoint_shouldReturnForbidden() throws Exception {
            mockMvc.perform(get("/api/admin/dashboard/stats"))
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    class AppointmentDashboardControllerTests {

        @Test
        @WithMockUser(roles = "DOCTOR")
        void whenDoctorGetsDashboard_shouldReturnOk() throws Exception {
            mockMvc.perform(get("/api/v1/appointments/dashboard/doctor/{doctorId}", testDoctorId))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void whenAdminAccessesDoctorDashboard_shouldReturnOk() throws Exception {
            // Admin should have access to doctor dashboard
            mockMvc.perform(get("/api/v1/appointments/dashboard/doctor/{doctorId}", testDoctorId))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        @Test
        @WithMockUser(roles = "DOCTOR")
        void whenDoctorGetsTodayAppointments_shouldReturnOk() throws Exception {
            mockMvc.perform(get("/api/v1/appointments/dashboard/doctor/{doctorId}/today", testDoctorId))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        @Test
        @WithMockUser(roles = "DOCTOR")
        void whenDoctorGetsUpcomingAppointments_shouldReturnOk() throws Exception {
            mockMvc.perform(get("/api/v1/appointments/dashboard/doctor/{doctorId}/upcoming", testDoctorId)
                            .param("days", "7"))
                    .andDo(print())
                    .andExpect(status().isOk());
        }
    }

    @Nested
    class SecurityTests {

        @Test
        @WithMockUser(roles = "USER")
        void whenUserTriesToAccessAdminEndpoints_shouldReturnForbidden() throws Exception {
            mockMvc.perform(get("/api/admin/dashboard/stats"))
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(roles = "PATIENT")
        void whenPatientTriesToAccessAdminEndpoints_shouldReturnForbidden() throws Exception {
            mockMvc.perform(get("/api/admin/dashboard/stats"))
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(roles = "DOCTOR")
        void whenDoctorTriesToAccessAdminEndpoints_shouldReturnForbidden() throws Exception {
            mockMvc.perform(get("/api/admin/dashboard/stats"))
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithAnonymousUser
        void whenUnauthenticatedAccess_shouldReturnUnauthorized() throws Exception {
            mockMvc.perform(get("/api/v1/appointments/doctor/{doctorId}", UUID.randomUUID()))
                    .andDo(print())
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @WithAnonymousUser
        void whenNoAuthorizationHeader_shouldReturnUnauthorized() throws Exception {
            mockMvc.perform(post("/api/v1/appointments/create")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(appointmentDto)))
                    .andDo(print())
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    class PublicEndpointTests {

        @Test
        @WithAnonymousUser
        void whenAccessingSwaggerUI_shouldBePermitted() throws Exception {
            mockMvc.perform(get("/swagger-ui/index.html"))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        @Test
        @WithAnonymousUser
        void whenAccessingApiDocs_shouldBePermitted() throws Exception {
            mockMvc.perform(get("/v3/api-docs"))
                    .andDo(print())
                    .andExpect(status().isOk());
        }
    }
}