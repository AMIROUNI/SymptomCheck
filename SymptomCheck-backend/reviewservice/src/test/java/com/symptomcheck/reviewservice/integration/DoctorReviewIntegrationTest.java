package com.symptomcheck.reviewservice.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.symptomcheck.reviewservice.config.SecurityConfig;
import com.symptomcheck.reviewservice.dtos.DoctorReviewRequest;
import com.symptomcheck.reviewservice.models.DoctorReview;
import com.symptomcheck.reviewservice.repositories.DoctorReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Import(SecurityConfig.class)
class DoctorReviewIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private DoctorReviewRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }
    @Test
    void patientCanCreateReview() throws Exception {
        String patientId = "gc6274gg-730g-44g0-9245-17gdg9054fe8";
        String doctorId = "gc6274gg-730g-44g0-9245-17gdg9054fe9";

        DoctorReviewRequest request = new DoctorReviewRequest();
        request.setDoctorId(doctorId);
        request.setRating(5);
        request.setComment("Great!");

        mockMvc.perform(post("/api/v1/reviews")
                        .with(jwt()
                                .jwt(jwt -> jwt
                                        .subject(patientId)
                                        .claim("realm_access", Map.of("roles", List.of("patient")))
                                        .claim("scope", "openid profile email"))
                                .authorities(new SimpleGrantedAuthority("ROLE_PATIENT"))) // Ajoutez ceci
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
    @Test
    void authenticatedUserCanViewDoctorReviews() throws Exception {
        // Setup
        DoctorReview review = DoctorReview.builder()
                .patientId("patient-1")
                .doctorId("doctor-1")
                .rating(4)
                .comment("Good")
                .build();
        repository.save(review);

        // Use authentication
        mockMvc.perform(get("/api/v1/reviews/doctor/{doctorId}", "doctor-1")
                        .with(jwt()
                                .jwt(jwt -> jwt
                                        .subject("viewer-1")
                                        .claim("realm_access", Map.of("roles", List.of("patient"))))
                                .authorities(new SimpleGrantedAuthority("ROLE_PATIENT"))))
                .andExpect(status().isOk());
    }
    @Test
    void patientCanViewTheirReviews() throws Exception {
        String patientId = "patient-123";

        DoctorReview review = DoctorReview.builder()
                .patientId(patientId)
                .doctorId("doctor-456")
                .rating(4)
                .comment("Good")
                .build();
        repository.save(review);

        mockMvc.perform(get("/api/v1/reviews/patient/my-reviews")
                        .with(jwt()
                                .jwt(jwt -> jwt
                                        .subject(patientId)
                                        .claim("realm_access", Map.of("roles", List.of("patient")))) // minuscules
                                .authorities(new SimpleGrantedAuthority("ROLE_PATIENT")))) // Ajoutez ceci
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].patientId").value(patientId))
                .andExpect(jsonPath("$[0].doctorId").value("doctor-456"))
                .andExpect(jsonPath("$[0].rating").value(4))
                .andExpect(jsonPath("$[0].comment").value("Good"));
    }
    @Test
    void unauthorizedAccessToPatientEndpointsShouldFail() throws Exception {
        // No PATIENT role
        mockMvc.perform(post("/api/v1/reviews")
                        .with(jwt().jwt(jwt -> jwt
                                .subject("user")
                                .claim("realm_access", Map.of("roles", List.of("USER")))))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());

        // No authentication
        mockMvc.perform(get("/api/v1/reviews/patient/my-reviews"))
                .andExpect(status().isUnauthorized());
    }


}