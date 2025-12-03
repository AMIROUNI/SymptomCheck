package com.symptomcheck.reviewservice.controllers;

import com.symptomcheck.reviewservice.dtos.DoctorReviewRequest;
import com.symptomcheck.reviewservice.dtos.DoctorReviewResponse;
import com.symptomcheck.reviewservice.dtos.DoctorReviewStats;
import com.symptomcheck.reviewservice.services.DoctorReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class DoctorReviewController {

    private final DoctorReviewService doctorReviewService;

    @PostMapping
    public ResponseEntity<DoctorReviewResponse> createReview(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody DoctorReviewRequest request) {

        String patientId = extractUserId(jwt);
        DoctorReviewResponse response = doctorReviewService.createReview(patientId, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<DoctorReviewResponse> updateReview(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long reviewId,
            @Valid @RequestBody DoctorReviewRequest request) {

        String patientId = extractUserId(jwt);
        DoctorReviewResponse response = doctorReviewService.updateReview(reviewId, patientId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long reviewId) {

        String patientId = extractUserId(jwt);
        doctorReviewService.deleteReview(reviewId, patientId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<DoctorReviewResponse>> getDoctorReviews(
            @PathVariable String doctorId) {

        List<DoctorReviewResponse> reviews = doctorReviewService.getReviewsByDoctor(doctorId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/patient/my-reviews")
    public ResponseEntity<List<DoctorReviewResponse>> getPatientReviews(
            @AuthenticationPrincipal Jwt jwt) {

        String patientId = extractUserId(jwt);
        List<DoctorReviewResponse> reviews = doctorReviewService.getReviewsByPatient(patientId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<DoctorReviewResponse> getReviewById(@PathVariable Long reviewId) {
        DoctorReviewResponse review = doctorReviewService.getReviewById(reviewId);
        return ResponseEntity.ok(review);
    }

    @GetMapping("/doctor/{doctorId}/stats")
    public ResponseEntity<DoctorReviewStats> getDoctorStats(@PathVariable String doctorId) {
        DoctorReviewStats stats = doctorReviewService.getDoctorReviewStats(doctorId);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/doctor/{doctorId}/has-reviewed")
    public ResponseEntity<Boolean> hasPatientReviewedDoctor(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String doctorId) {

        String patientId = extractUserId(jwt);
        boolean hasReviewed = doctorReviewService.hasPatientReviewedDoctor(patientId, doctorId);
        return ResponseEntity.ok(hasReviewed);
    }

    @GetMapping("/doctor/{doctorId}/my-review")
    public ResponseEntity<DoctorReviewResponse> getMyReviewForDoctor(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String doctorId) {

        String patientId = extractUserId(jwt);
        Optional<DoctorReviewResponse> review = doctorReviewService.getPatientReviewForDoctor(patientId, doctorId);
        return review.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    private String extractUserId(Jwt jwt) {
        // Extraire l'ID utilisateur Keycloak du token JWT
        // Le subject du token JWT Keycloak contient l'ID utilisateur
        return jwt.getSubject();
    }

    // Méthode utilitaire pour extraire les rôles
    private List<String> extractRoles(Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        if (realmAccess != null) {
            return (List<String>) realmAccess.get("roles");
        }
        return List.of();
    }

    // Méthode utilitaire pour vérifier si l'utilisateur est un patient
    private boolean isPatient(Jwt jwt) {
        return extractRoles(jwt).contains("PATIENT");
    }

    // Méthode utilitaire pour vérifier si l'utilisateur est un docteur
    private boolean isDoctor(Jwt jwt) {
        return extractRoles(jwt).contains("DOCTOR");
    }
}