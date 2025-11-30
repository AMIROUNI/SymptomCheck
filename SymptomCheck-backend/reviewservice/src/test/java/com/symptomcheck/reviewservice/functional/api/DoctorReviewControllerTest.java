package com.symptomcheck.reviewservice.functional.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.symptomcheck.reviewservice.controllers.DoctorReviewController;
import com.symptomcheck.reviewservice.dtos.DoctorReviewRequest;
import com.symptomcheck.reviewservice.dtos.DoctorReviewResponse;
import com.symptomcheck.reviewservice.dtos.DoctorReviewStats;
import com.symptomcheck.reviewservice.services.DoctorReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DoctorReviewController.class)
class DoctorReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DoctorReviewService doctorReviewService;

    private String patientId;
    private String doctorId;
    private DoctorReviewRequest reviewRequest;
    private DoctorReviewResponse reviewResponse;
    private DoctorReviewStats reviewStats;
    private Jwt jwt;

    @BeforeEach
    void setUp() {
        patientId = "patient-123";
        doctorId = "doctor-456";

        // Create mock JWT
        jwt = Jwt.withTokenValue("mock-jwt-token")
                .header("alg", "none")
                .claim("sub", patientId)
                .claim("realm_access", Map.of("roles", List.of("PATIENT")))
                .build();

        // Create request DTO
        reviewRequest = new DoctorReviewRequest();
        reviewRequest.setDoctorId(doctorId);
        reviewRequest.setRating(5);
        reviewRequest.setComment("Excellent doctor, very professional and caring");

        // Create response DTO
        reviewResponse = new DoctorReviewResponse();
        reviewResponse.setId(1L);
        reviewResponse.setPatientId(patientId);
        reviewResponse.setDoctorId(doctorId);
        reviewResponse.setRating(5);
        reviewResponse.setComment("Excellent doctor, very professional and caring");
        reviewResponse.setDatePosted(Instant.now());

        // Create stats DTO
        reviewStats = new DoctorReviewStats();
        reviewStats.setDoctorId(doctorId);
        reviewStats.setAverageRating(4.5);
        reviewStats.setTotalReviews(10L);
        reviewStats.setRatingDistribution(Map.of(1, 1L, 2, 2L, 3, 3L, 4, 2L, 5, 2L));
    }

    @Test
    void createReview_ShouldReturnCreatedReview() throws Exception {
        when(doctorReviewService.createReview(eq(patientId), any(DoctorReviewRequest.class)))
                .thenReturn(reviewResponse);

        mockMvc.perform(post("/api/v1/reviews")
                        .with(jwt().jwt(jwt))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.patientId").value(patientId))
                .andExpect(jsonPath("$.doctorId").value(doctorId))
                .andExpect(jsonPath("$.rating").value(5))
                .andExpect(jsonPath("$.comment").value("Excellent doctor, very professional and caring"));

        verify(doctorReviewService).createReview(eq(patientId), any(DoctorReviewRequest.class));
    }

    @Test
    void createReview_WithInvalidInput_ShouldReturnBadRequest() throws Exception {
        DoctorReviewRequest invalidRequest = new DoctorReviewRequest();
        invalidRequest.setDoctorId(""); // Blank doctor ID
        invalidRequest.setRating(6); // Invalid rating
        invalidRequest.setComment(""); // Blank comment

        mockMvc.perform(post("/api/v1/reviews")
                        .with(jwt().jwt(jwt))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(doctorReviewService, never()).createReview(any(), any());
    }

    @Test
    void updateReview_ShouldReturnUpdatedReview() throws Exception {
        Long reviewId = 1L;
        when(doctorReviewService.updateReview(eq(reviewId), eq(patientId), any(DoctorReviewRequest.class)))
                .thenReturn(reviewResponse);

        mockMvc.perform(put("/api/v1/reviews/{reviewId}", reviewId)
                        .with(jwt().jwt(jwt))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.rating").value(5));

        verify(doctorReviewService).updateReview(eq(reviewId), eq(patientId), any(DoctorReviewRequest.class));
    }

    @Test
    void deleteReview_ShouldReturnNoContent() throws Exception {
        Long reviewId = 1L;
        doNothing().when(doctorReviewService).deleteReview(reviewId, patientId);

        mockMvc.perform(delete("/api/v1/reviews/{reviewId}", reviewId)
                        .with(jwt().jwt(jwt)))
                .andExpect(status().isNoContent());

        verify(doctorReviewService).deleteReview(reviewId, patientId);
    }

    @Test
    void getPatientReviews_ShouldReturnPatientReviews() throws Exception {
        DoctorReviewResponse review2 = new DoctorReviewResponse();
        review2.setId(2L);
        review2.setPatientId(patientId);
        review2.setDoctorId("doctor-789");
        review2.setRating(4);
        review2.setComment("Another review");

        when(doctorReviewService.getReviewsByPatient(patientId))
                .thenReturn(List.of(reviewResponse, review2));

        mockMvc.perform(get("/api/v1/reviews/patient/my-reviews")
                        .with(jwt().jwt(jwt)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].patientId").value(patientId))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].doctorId").value("doctor-789"));

        verify(doctorReviewService).getReviewsByPatient(patientId);
    }

    @Test
    void hasPatientReviewedDoctor_WhenHasReviewed_ShouldReturnTrue() throws Exception {
        when(doctorReviewService.hasPatientReviewedDoctor(patientId, doctorId)).thenReturn(true);

        mockMvc.perform(get("/api/v1/reviews/doctor/{doctorId}/has-reviewed", doctorId)
                        .with(jwt().jwt(jwt)))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(doctorReviewService).hasPatientReviewedDoctor(patientId, doctorId);
    }

    @Test
    void hasPatientReviewedDoctor_WhenHasNotReviewed_ShouldReturnFalse() throws Exception {
        when(doctorReviewService.hasPatientReviewedDoctor(patientId, doctorId)).thenReturn(false);

        mockMvc.perform(get("/api/v1/reviews/doctor/{doctorId}/has-reviewed", doctorId)
                        .with(jwt().jwt(jwt)))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));

        verify(doctorReviewService).hasPatientReviewedDoctor(patientId, doctorId);
    }

    @Test
    void getMyReviewForDoctor_WhenReviewExists_ShouldReturnReview() throws Exception {
        when(doctorReviewService.getPatientReviewForDoctor(patientId, doctorId))
                .thenReturn(Optional.of(reviewResponse));

        mockMvc.perform(get("/api/v1/reviews/doctor/{doctorId}/my-review", doctorId)
                        .with(jwt().jwt(jwt)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.patientId").value(patientId))
                .andExpect(jsonPath("$.doctorId").value(doctorId));

        verify(doctorReviewService).getPatientReviewForDoctor(patientId, doctorId);
    }

    @Test
    void getMyReviewForDoctor_WhenReviewNotExists_ShouldReturnNotFound() throws Exception {
        when(doctorReviewService.getPatientReviewForDoctor(patientId, doctorId))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/reviews/doctor/{doctorId}/my-review", doctorId)
                        .with(jwt().jwt(jwt)))
                .andExpect(status().isNotFound());

        verify(doctorReviewService).getPatientReviewForDoctor(patientId, doctorId);
    }

    @Test
    void getPatientReviews_WithoutAuthentication_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/reviews/patient/my-reviews"))
                .andExpect(status().isUnauthorized());
    }

}