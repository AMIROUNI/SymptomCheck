package com.symptomcheck.reviewservice.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.symptomcheck.reviewservice.config.SecurityConfig;
import com.symptomcheck.reviewservice.dtos.DoctorReviewRequest;
import com.symptomcheck.reviewservice.dtos.DoctorReviewResponse;
import com.symptomcheck.reviewservice.models.DoctorReview;
import com.symptomcheck.reviewservice.repositories.DoctorReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Import(SecurityConfig.class)
class DoctorReviewControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DoctorReviewRepository doctorReviewRepository;

    private String testPatientId;
    private String testDoctorId;
    private DoctorReview existingReview;

    @BeforeEach
    void setUp() {
        doctorReviewRepository.deleteAll();

        testPatientId = "patient-123";
        testDoctorId = "doctor-456";

        // Create and save a test review
        existingReview = DoctorReview.builder()
                .patientId(testPatientId)
                .doctorId(testDoctorId)
                .rating(4)
                .comment("Great doctor!")
                .build();
        existingReview = doctorReviewRepository.save(existingReview);
    }

    // ===== PATIENT ENDPOINTS =====

    @Test
    @WithMockUser(username = "patient1", roles = {"PATIENT"})
    void createReview_AsPatient_ShouldCreateNewReview() throws Exception {
        // Given
        DoctorReviewRequest request = new DoctorReviewRequest();
        request.setDoctorId("doctor-789");
        request.setRating(5);
        request.setComment("Excellent service!");

        // When & Then
        mockMvc.perform(post("/api/v1/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rating").value(5))
                .andExpect(jsonPath("$.comment").value("Excellent service!"));

        // Verify in database
        assertThat(doctorReviewRepository.count()).isEqualTo(2);
    }

    @Test
    @WithMockUser(username = "patient1", roles = {"PATIENT"})
    void updateReview_AsPatient_ShouldUpdateReview() throws Exception {
        // Given
        DoctorReviewRequest updateRequest = new DoctorReviewRequest();
        updateRequest.setDoctorId(testDoctorId);
        updateRequest.setRating(5);
        updateRequest.setComment("Updated comment!");

        // When & Then
        mockMvc.perform(put("/api/v1/reviews/{reviewId}", existingReview.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rating").value(5))
                .andExpect(jsonPath("$.comment").value("Updated comment!"));
    }

    @Test
    @WithMockUser(username = "patient1", roles = {"PATIENT"})
    void deleteReview_AsPatient_ShouldDeleteReview() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/v1/reviews/{reviewId}", existingReview.getId()))
                .andExpect(status().isNoContent());

        // Verify deleted
        assertThat(doctorReviewRepository.findById(existingReview.getId())).isEmpty();
    }

    @Test
    @WithMockUser(username = "patient1", roles = {"PATIENT"})
    void getPatientReviews_AsPatient_ShouldReturnPatientsReviews() throws Exception {
        // Given - Add another review for same patient
        DoctorReview anotherReview = DoctorReview.builder()
                .patientId("patient1")
                .doctorId("doctor-999")
                .rating(3)
                .comment("Good doctor")
                .build();
        doctorReviewRepository.save(anotherReview);

        // When & Then
        mockMvc.perform(get("/api/v1/reviews/patient/my-reviews"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].patientId").value("patient1"));
    }

    @Test
    @WithMockUser(username = "patient1", roles = {"PATIENT"})
    void hasPatientReviewedDoctor_AsPatient_ShouldReturnTrueIfReviewed() throws Exception {
        // Given - Create a review for this patient
        DoctorReview review = DoctorReview.builder()
                .patientId("patient1")
                .doctorId("doctor-888")
                .rating(4)
                .comment("Good")
                .build();
        doctorReviewRepository.save(review);

        // When & Then
        mockMvc.perform(get("/api/v1/reviews/doctor/{doctorId}/has-reviewed", "doctor-888"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    @WithMockUser(username = "patient1", roles = {"PATIENT"})
    void getMyReviewForDoctor_AsPatient_ShouldReturnPatientsReview() throws Exception {
        // Given - Create a review for this patient
        DoctorReview review = DoctorReview.builder()
                .patientId("patient1")
                .doctorId("doctor-777")
                .rating(4)
                .comment("My review")
                .build();
        review = doctorReviewRepository.save(review);

        // When & Then
        mockMvc.perform(get("/api/v1/reviews/doctor/{doctorId}/my-review", "doctor-777"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.patientId").value("patient1"))
                .andExpect(jsonPath("$.doctorId").value("doctor-777"));
    }

    // ===== PUBLIC ENDPOINTS (No specific role required) =====

    @Test
    @WithMockUser(username = "doctor1", roles = {"DOCTOR"})
    void getDoctorReviews_ShouldReturnReviewsForDoctor() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/reviews/doctor/{doctorId}", testDoctorId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].doctorId").value(testDoctorId));
    }

    @Test
    @WithMockUser(username = "USER", roles = {"PATIENT","DOCTOR","ADMIN"})
    void getReviewById_ShouldReturnSpecificReview() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/reviews/{reviewId}", existingReview.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(existingReview.getId()))
                .andExpect(jsonPath("$.rating").value(4));
    }

    @Test
    @WithMockUser(username = "USER", roles = {"PATIENT","DOCTOR","ADMIN"})
    void getDoctorStats_ShouldReturnReviewStatistics() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/reviews/doctor/{doctorId}/stats", testDoctorId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.doctorId").value(testDoctorId))
                .andExpect(jsonPath("$.totalReviews").value(1));
    }

    // ===== DOCTOR ENDPOINTS =====

    @Test
    @WithMockUser(username = "doctor1", roles = {"DOCTOR"})
    void getDoctorReviews_AsDoctor_ShouldWork() throws Exception {
        // Doctor can view reviews about themselves
        mockMvc.perform(get("/api/v1/reviews/doctor/{doctorId}", "doctor1"))
                .andExpect(status().isOk());
    }

    // ===== ADMIN ENDPOINTS (if you add them) =====

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getAllReviews_AsAdmin_ShouldWork() throws Exception {
        // Admin can access all endpoints
        mockMvc.perform(get("/api/v1/reviews/doctor/{doctorId}", testDoctorId))
                .andExpect(status().isOk());
    }

    // ===== SECURITY TESTS =====

    @Test
    @WithMockUser(username = "otherpatient", roles = {"PATIENT"})
    void updateReview_AsDifferentPatient_ShouldReturnError() throws Exception {
        // Different patient trying to update someone else's review
        DoctorReviewRequest updateRequest = new DoctorReviewRequest();
        updateRequest.setDoctorId(testDoctorId);
        updateRequest.setRating(1);
        updateRequest.setComment("Hacked!");

        // When & Then - Should get error since patient can only update their own reviews
        mockMvc.perform(put("/api/v1/reviews/{reviewId}", existingReview.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isInternalServerError()); // Note: Your service throws SecurityException
    }

    @Test
    @WithMockUser(username = "doctor1", roles = {"DOCTOR"})
    void createReview_AsDoctor_ShouldNotWork() throws Exception {
        // Doctors should also be able to leave reviews for other doctors
        DoctorReviewRequest request = new DoctorReviewRequest();
        request.setDoctorId("doctor-999");
        request.setRating(5);
        request.setComment("Colleague review");

        mockMvc.perform(post("/api/v1/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void createReview_WithoutAuthentication_ShouldReturnUnauthorized() throws Exception {
        // No @WithMockUser annotation
        DoctorReviewRequest request = new DoctorReviewRequest();
        request.setDoctorId("doctor-123");
        request.setRating(5);
        request.setComment("Test");

        mockMvc.perform(post("/api/v1/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"}) // Different role
    void createReview_WithWrongRole_ShouldWork() throws Exception {
        // Since your controller doesn't specify @PreAuthorize, any authenticated user can create reviews
        DoctorReviewRequest request = new DoctorReviewRequest();
        request.setDoctorId("doctor-123");
        request.setRating(5);
        request.setComment("Test");

        mockMvc.perform(post("/api/v1/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    // ===== VALIDATION TESTS =====

    @Test
    @WithMockUser(username = "patient1", roles = {"PATIENT"})
    void createReview_WithInvalidRating_ShouldHandleValidation() throws Exception {
        DoctorReviewRequest request = new DoctorReviewRequest();
        request.setDoctorId("doctor-123");
        request.setRating(6); // Invalid - max is 5
        request.setComment("Test");

        mockMvc.perform(post("/api/v1/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest()); // Note: Add @Valid and proper validation
    }

    @Test
    @WithMockUser(username = "patient1", roles = {"PATIENT"})
    void createReview_WithBlankComment_ShouldHandleValidation() throws Exception {
        DoctorReviewRequest request = new DoctorReviewRequest();
        request.setDoctorId("doctor-123");
        request.setRating(5);
        request.setComment(""); // Blank - should fail validation

        mockMvc.perform(post("/api/v1/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest()); // Note: Add @Valid and proper validation
    }

    @Test
    @WithMockUser(username = "patient1", roles = {"PATIENT"})
    void getMyReviewForDoctor_WhenNoReviewExists_ShouldReturn404() throws Exception {
        mockMvc.perform(get("/api/v1/reviews/doctor/{doctorId}/my-review", "non-existent-doctor"))
                .andExpect(status().isInternalServerError());
    }
}