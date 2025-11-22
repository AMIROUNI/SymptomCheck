package com.symptomcheck.reviewservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorReviewResponse {
    private Long id;
    private String patientId; // Changed to String
    private String doctorId;  // Changed to String
    private Integer rating;
    private String comment;
    private Instant datePosted;
    private Instant lastUpdated;
    private String patientName; // Pour l'affichage
    private String doctorName;  // Pour l'affichage
}