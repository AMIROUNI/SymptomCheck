package com.symptomcheck.reviewservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorReviewStats {
    private String doctorId; // Changed to String
    private Double averageRating;
    private Long totalReviews;
    private Map<Integer, Long> ratingDistribution;
}