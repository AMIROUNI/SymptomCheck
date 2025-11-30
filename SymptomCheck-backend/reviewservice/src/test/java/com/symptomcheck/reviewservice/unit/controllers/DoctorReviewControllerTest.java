package com.symptomcheck.reviewservice.unit.controllers;

import com.symptomcheck.reviewservice.controllers.DoctorReviewController;
import com.symptomcheck.reviewservice.dtos.DoctorReviewRequest;
import com.symptomcheck.reviewservice.dtos.DoctorReviewResponse;
import com.symptomcheck.reviewservice.dtos.DoctorReviewStats;
import com.symptomcheck.reviewservice.services.DoctorReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DoctorReviewControllerTest {

    @InjectMocks
    private DoctorReviewController controller;

    @Mock
    private DoctorReviewService service;

    @Mock
    private Jwt jwt;

    private String patientId;
    private String doctorId;
    private DoctorReviewRequest reviewRequest;
    private DoctorReviewResponse reviewResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        patientId = "patient-123";
        doctorId = "doctor-456";

        reviewRequest = new DoctorReviewRequest();
        reviewRequest.setDoctorId(doctorId);
        reviewRequest.setRating(5);
        reviewRequest.setComment("Excellent doctor");

        reviewResponse = new DoctorReviewResponse();
        reviewResponse.setId(1L);
        reviewResponse.setPatientId(patientId);
        reviewResponse.setDoctorId(doctorId);
        reviewResponse.setRating(5);
        reviewResponse.setComment("Excellent doctor");
        reviewResponse.setDatePosted(Instant.now());

        when(jwt.getSubject()).thenReturn(patientId);
    }

    @Test
    void testCreateReview_Success() {
        when(service.createReview(patientId, reviewRequest)).thenReturn(reviewResponse);

        ResponseEntity<DoctorReviewResponse> response = controller.createReview(jwt, reviewRequest);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1L, response.getBody().getId());
        assertEquals("Excellent doctor", response.getBody().getComment());
    }



    @Test
    void testUpdateReview_Success() {
        when(service.updateReview(1L, patientId, reviewRequest)).thenReturn(reviewResponse);

        ResponseEntity<DoctorReviewResponse> response = controller.updateReview(jwt, 1L, reviewRequest);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1L, response.getBody().getId());
    }



    @Test
    void testDeleteReview_Success() {
        doNothing().when(service).deleteReview(1L, patientId);

        ResponseEntity<Void> response = controller.deleteReview(jwt, 1L);

        assertEquals(204, response.getStatusCodeValue());
        verify(service, times(1)).deleteReview(1L, patientId);
    }

    @Test
    void testGetDoctorReviews() {
        DoctorReviewResponse review2 = new DoctorReviewResponse();
        review2.setId(2L);
        review2.setPatientId("patient-456");
        review2.setDoctorId(doctorId);
        review2.setRating(4);
        review2.setComment("Good service");

        when(service.getReviewsByDoctor(doctorId)).thenReturn(Arrays.asList(reviewResponse, review2));

        ResponseEntity<List<DoctorReviewResponse>> response = controller.getDoctorReviews(doctorId);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
        assertEquals("Excellent doctor", response.getBody().get(0).getComment());
    }

    @Test
    void testGetPatientReviews() {
        DoctorReviewResponse review2 = new DoctorReviewResponse();
        review2.setId(2L);
        review2.setPatientId(patientId);
        review2.setDoctorId("doctor-789");
        review2.setRating(4);
        review2.setComment("Another review");

        when(service.getReviewsByPatient(patientId)).thenReturn(Arrays.asList(reviewResponse, review2));

        ResponseEntity<List<DoctorReviewResponse>> response = controller.getPatientReviews(jwt);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
        assertEquals(patientId, response.getBody().get(0).getPatientId());
    }

    @Test
    void testGetReviewById() {
        when(service.getReviewById(1L)).thenReturn(reviewResponse);

        ResponseEntity<DoctorReviewResponse> response = controller.getReviewById(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1L, response.getBody().getId());
        assertEquals(doctorId, response.getBody().getDoctorId());
    }

    @Test
    void testGetDoctorStats() {
        DoctorReviewStats stats = new DoctorReviewStats();
        stats.setDoctorId(doctorId);
        stats.setAverageRating(4.5);
        stats.setTotalReviews(10L);
        stats.setRatingDistribution(Map.of(1, 1L, 2, 2L, 3, 3L, 4, 2L, 5, 2L));

        when(service.getDoctorReviewStats(doctorId)).thenReturn(stats);

        ResponseEntity<DoctorReviewStats> response = controller.getDoctorStats(doctorId);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(4.5, response.getBody().getAverageRating());
        assertEquals(10L, response.getBody().getTotalReviews());
    }

    @Test
    void testHasPatientReviewedDoctor_True() {
        when(service.hasPatientReviewedDoctor(patientId, doctorId)).thenReturn(true);

        ResponseEntity<Boolean> response = controller.hasPatientReviewedDoctor(jwt, doctorId);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody());
    }

    @Test
    void testHasPatientReviewedDoctor_False() {
        when(service.hasPatientReviewedDoctor(patientId, doctorId)).thenReturn(false);

        ResponseEntity<Boolean> response = controller.hasPatientReviewedDoctor(jwt, doctorId);

        assertEquals(200, response.getStatusCodeValue());
        assertFalse(response.getBody());
    }

    @Test
    void testGetMyReviewForDoctor_Exists() {
        when(service.getPatientReviewForDoctor(patientId, doctorId)).thenReturn(Optional.of(reviewResponse));

        ResponseEntity<DoctorReviewResponse> response = controller.getMyReviewForDoctor(jwt, doctorId);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1L, response.getBody().getId());
    }

    @Test
    void testGetMyReviewForDoctor_NotFound() {
        when(service.getPatientReviewForDoctor(patientId, doctorId)).thenReturn(Optional.empty());

        ResponseEntity<DoctorReviewResponse> response = controller.getMyReviewForDoctor(jwt, doctorId);

        assertEquals(404, response.getStatusCodeValue());
    }


}