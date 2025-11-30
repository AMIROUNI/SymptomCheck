package com.symptomcheck.reviewservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

import com.symptomcheck.reviewservice.dtos.DoctorReviewRequest;
import com.symptomcheck.reviewservice.dtos.DoctorReviewResponse;
import com.symptomcheck.reviewservice.dtos.DoctorReviewStats;
import com.symptomcheck.reviewservice.services.DoctorReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
@Import(SecurityTestConfig.class)
class ReviewserviceApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DoctorReviewService doctorReviewService;

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("review_testdb")
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

    @Test
    void contextLoads() {
        // This will verify that the application context loads successfully
    }

    private DoctorReviewRequest doctorReviewRequest;
    private DoctorReviewResponse doctorReviewResponse;
    private DoctorReviewStats doctorReviewStats;

    @BeforeEach
    void setUp() {
        // Create request DTO
        doctorReviewRequest = new DoctorReviewRequest();
        doctorReviewRequest.setDoctorId("doctor-456");
        doctorReviewRequest.setRating(5);
        doctorReviewRequest.setComment("Excellent doctor, very professional and caring");

        // Create response DTO
        doctorReviewResponse = new DoctorReviewResponse();
        doctorReviewResponse.setId(1L);
        doctorReviewResponse.setPatientId("patient-123");
        doctorReviewResponse.setDoctorId("doctor-456");
        doctorReviewResponse.setRating(5);
        doctorReviewResponse.setComment("Excellent doctor, very professional and caring");
        doctorReviewResponse.setDatePosted(Instant.now());
        doctorReviewResponse.setLastUpdated(Instant.now());

        // Create stats DTO
        doctorReviewStats = new DoctorReviewStats();
        doctorReviewStats.setDoctorId("doctor-456");
        doctorReviewStats.setAverageRating(4.5);
        doctorReviewStats.setTotalReviews(10L);
        doctorReviewStats.setRatingDistribution(Map.of(1, 1L, 2, 2L, 3, 3L, 4, 2L, 5, 2L));

        // Setup common mock behaviors
        setupCommonMocks();
    }

    private void setupCommonMocks() {
        // Mock service responses for common endpoints
        when(doctorReviewService.getReviewsByDoctor(any(String.class)))
                .thenReturn(List.of(doctorReviewResponse));

        when(doctorReviewService.getReviewsByPatient(any(String.class)))
                .thenReturn(List.of(doctorReviewResponse));

        when(doctorReviewService.getReviewById(any(Long.class)))
                .thenReturn(doctorReviewResponse);

        when(doctorReviewService.getDoctorReviewStats(any(String.class)))
                .thenReturn(doctorReviewStats);

        when(doctorReviewService.hasPatientReviewedDoctor(any(String.class), any(String.class)))
                .thenReturn(false);

        when(doctorReviewService.getPatientReviewForDoctor(any(String.class), any(String.class)))
                .thenReturn(Optional.empty());

        when(doctorReviewService.createReview(any(String.class), any(DoctorReviewRequest.class)))
                .thenReturn(doctorReviewResponse);

        when(doctorReviewService.updateReview(any(Long.class), any(String.class), any(DoctorReviewRequest.class)))
                .thenReturn(doctorReviewResponse);
    }

    @Nested
    class SecurityTests {

        @Test
        @WithMockUser(username = "user", roles = {"USER"})
        void whenUserRoleAccessReviewEndpoints_shouldReturnForbidden() throws Exception {
            mockMvc.perform(get("/api/v1/reviews/doctor/doctor-123")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(username = "user", roles = {"USER"})
        void whenUserRoleTriesToCreateReview_shouldReturnForbidden() throws Exception {
            mockMvc.perform(post("/api/v1/reviews")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(doctorReviewRequest)))
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }

        @Test
        void whenUnauthenticatedAccessReviewEndpoints_shouldReturnUnauthorized() throws Exception {
            mockMvc.perform(get("/api/v1/reviews/doctor/doctor-123")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockUser(username = "user", roles = {"USER"})
        void whenUserRoleTriesToUpdateReview_shouldReturnForbidden() throws Exception {
            mockMvc.perform(put("/api/v1/reviews/1")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(doctorReviewRequest)))
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(username = "user", roles = {"USER"})
        void whenUserRoleTriesToDeleteReview_shouldReturnForbidden() throws Exception {
            mockMvc.perform(delete("/api/v1/reviews/1")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(username = "user", roles = {"USER"})
        void whenUserRoleTriesToGetPatientReviews_shouldReturnForbidden() throws Exception {
            mockMvc.perform(get("/api/v1/reviews/patient/my-reviews")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(username = "user", roles = {"USER"})
        void whenUserRoleTriesToCheckIfReviewed_shouldReturnForbidden() throws Exception {
            mockMvc.perform(get("/api/v1/reviews/doctor/doctor-123/has-reviewed")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(username = "user", roles = {"USER"})
        void whenUserRoleTriesToGetMyReview_shouldReturnForbidden() throws Exception {
            mockMvc.perform(get("/api/v1/reviews/doctor/doctor-123/my-review")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    class PatientRoleTests {

        @Test
        @WithMockUser(username = "patient-123", roles = {"PATIENT"})
        void whenPatientRoleAccessReviewEndpoints_shouldReturnOk() throws Exception {
            mockMvc.perform(get("/api/v1/reviews/doctor/doctor-123")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        @Test
        @WithMockUser(username = "patient-123", roles = {"PATIENT"})
        void whenPatientRoleTriesToGetPatientReviews_shouldReturnOk() throws Exception {
            mockMvc.perform(get("/api/v1/reviews/patient/my-reviews")
                            .with(csrf())
                            .header("Authorization", "Bearer patient-token")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        @Test
        @WithMockUser(username = "patient-123", roles = {"PATIENT"})
        void whenPatientRoleTriesToGetDoctorStats_shouldReturnOk() throws Exception {
            mockMvc.perform(get("/api/v1/reviews/doctor/doctor-123/stats")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk());
        }


        @Test
        void whenPatientRoleTriesToCreateReview_shouldReturnOk() throws Exception {
            mockMvc.perform(post("/api/v1/reviews")
                            .with(csrf())
                            .header("Authorization", "Bearer patient-token")   // This triggers patient JWT
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(doctorReviewRequest)))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        @Nested
        class DoctorRoleTests {

            @Test
            @WithMockUser(username = "doctor-456", roles = {"DOCTOR"})
            void whenDoctorRoleAccessReviewEndpoints_shouldReturnOk() throws Exception {
                mockMvc.perform(get("/api/v1/reviews/doctor/doctor-456")
                                .contentType(MediaType.APPLICATION_JSON))
                        .andDo(print())
                        .andExpect(status().isOk());
            }

            @Test
            @WithMockUser(username = "doctor-456", roles = {"DOCTOR"})
            void whenDoctorRoleTriesToGetDoctorStats_shouldReturnOk() throws Exception {
                mockMvc.perform(get("/api/v1/reviews/doctor/doctor-456/stats")
                                .contentType(MediaType.APPLICATION_JSON))
                        .andDo(print())
                        .andExpect(status().isOk());
            }

        }

        @Nested
        class AdminRoleTests {

            @Test
            @WithMockUser(username = "admin-789", roles = {"ADMIN"})
            void whenAdminRoleAccessReviewEndpoints_shouldReturnOk() throws Exception {
                mockMvc.perform(get("/api/v1/reviews/doctor/doctor-123")
                                .contentType(MediaType.APPLICATION_JSON))
                        .andDo(print())
                        .andExpect(status().isOk());
            }

            @Test
            @WithMockUser(username = "admin-789", roles = {"ADMIN"})
            void whenAdminRoleTriesToGetDoctorStats_shouldReturnOk() throws Exception {
                mockMvc.perform(get("/api/v1/reviews/doctor/doctor-123/stats")
                                .contentType(MediaType.APPLICATION_JSON))
                        .andDo(print())
                        .andExpect(status().isOk());
            }

            @Test
            @WithMockUser(username = "admin-789", roles = {"ADMIN"})
            void whenAdminRoleTriesToGetAllReviews_shouldReturnOk() throws Exception {
                mockMvc.perform(get("/api/v1/reviews/1")
                                .contentType(MediaType.APPLICATION_JSON))
                        .andDo(print())
                        .andExpect(status().isOk());
            }
        }

        @Nested
        class ValidationTests {

            @Test
            @WithMockUser(username = "patient-123", roles = {"PATIENT"})
            void whenCreateReviewWithInvalidRating_shouldReturnBadRequest() throws Exception {
                DoctorReviewRequest invalidRequest = new DoctorReviewRequest();
                invalidRequest.setDoctorId("doctor-456");
                invalidRequest.setRating(6); // Invalid rating > 5
                invalidRequest.setComment("Test comment");

                mockMvc.perform(post("/api/v1/reviews")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidRequest)))
                        .andDo(print())
                        .andExpect(status().isBadRequest());
            }

            @Test
            @WithMockUser(username = "patient-123", roles = {"PATIENT"})
            void whenCreateReviewWithBlankComment_shouldReturnBadRequest() throws Exception {
                DoctorReviewRequest invalidRequest = new DoctorReviewRequest();
                invalidRequest.setDoctorId("doctor-456");
                invalidRequest.setRating(5);
                invalidRequest.setComment(""); // Blank comment

                mockMvc.perform(post("/api/v1/reviews")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidRequest)))
                        .andDo(print())
                        .andExpect(status().isBadRequest());
            }

            @Test
            @WithMockUser(username = "patient-123", roles = {"PATIENT"})
            void whenCreateReviewWithBlankDoctorId_shouldReturnBadRequest() throws Exception {
                DoctorReviewRequest invalidRequest = new DoctorReviewRequest();
                invalidRequest.setDoctorId(""); // Blank doctor ID
                invalidRequest.setRating(5);
                invalidRequest.setComment("Test comment");

                mockMvc.perform(post("/api/v1/reviews")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidRequest)))
                        .andDo(print())
                        .andExpect(status().isBadRequest());
            }
        }
    }
}