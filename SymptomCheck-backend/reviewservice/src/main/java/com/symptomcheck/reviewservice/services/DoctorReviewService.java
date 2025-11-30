package com.symptomcheck.reviewservice.services;

import com.symptomcheck.reviewservice.dtos.*;
import com.symptomcheck.reviewservice.exceptions.ReviewAlreadyExistsException;
import com.symptomcheck.reviewservice.exceptions.ReviewNotFoundException;
import com.symptomcheck.reviewservice.models.DoctorReview;
import com.symptomcheck.reviewservice.repositories.DoctorReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DoctorReviewService {

    private final DoctorReviewRepository doctorReviewRepository;

    public DoctorReviewResponse createReview(String patientId, DoctorReviewRequest request) {


        // Vérifier si le patient a déjà review ce docteur
        if (doctorReviewRepository.existsByPatientIdAndDoctorId(patientId, request.getDoctorId())) {
            throw new ReviewAlreadyExistsException(
                    "Patient " + patientId + " has already reviewed doctor " + request.getDoctorId()
            );
        }

        DoctorReview review = DoctorReview.builder()
                .patientId(patientId)
                .doctorId(request.getDoctorId())
                .rating(request.getRating())
                .comment(request.getComment())
                .build();

        DoctorReview savedReview = doctorReviewRepository.save(review);
        log.info("Review created for doctor {} by patient {}", request.getDoctorId(), patientId);

        return mapToResponse(savedReview);
    }

    public DoctorReviewResponse updateReview(Long reviewId, String patientId, DoctorReviewRequest request) {
        DoctorReview review = doctorReviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException("Review not found with id: " + reviewId));

        // Vérifier que le patient est bien le propriétaire du review
        if (!review.getPatientId().equals(patientId)) {
            throw new SecurityException("Patient can only update their own reviews");
        }

        review.setRating(request.getRating());
        review.setComment(request.getComment());

        DoctorReview updatedReview = doctorReviewRepository.save(review);
        log.info("Review {} updated by patient {}", reviewId, patientId);

        return mapToResponse(updatedReview);
    }

    public void deleteReview(Long reviewId, String patientId) {
        DoctorReview review = doctorReviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException("Review not found with id: " + reviewId));

        // Vérifier que le patient est bien le propriétaire du review
        if (!review.getPatientId().equals(patientId)) {
            throw new SecurityException("Patient can only delete their own reviews");
        }

        doctorReviewRepository.delete(review);
        log.info("Review {} deleted by patient {}", reviewId, patientId);
    }

    public List<DoctorReviewResponse> getReviewsByDoctor(String doctorId) {
        return doctorReviewRepository.findByDoctorId(doctorId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<DoctorReviewResponse> getReviewsByPatient(String patientId) {
        return doctorReviewRepository.findByPatientId(patientId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public DoctorReviewResponse getReviewById(Long reviewId) {
        DoctorReview review = doctorReviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException("Review not found with id: " + reviewId));
        return mapToResponse(review);
    }

    public DoctorReviewStats getDoctorReviewStats(String doctorId) {
        Double averageRating = doctorReviewRepository.findAverageRatingByDoctorId(doctorId);
        Long totalReviews = doctorReviewRepository.countByDoctorId(doctorId);

        List<Object[]> distribution = doctorReviewRepository.getRatingDistributionByDoctorId(doctorId);
        Map<Integer, Long> ratingDistribution = distribution.stream()
                .collect(Collectors.toMap(
                        obj -> (Integer) obj[0],
                        obj -> (Long) obj[1]
                ));

        return DoctorReviewStats.builder()
                .doctorId(doctorId)
                .averageRating(averageRating != null ? Math.round(averageRating * 10.0) / 10.0 : 0.0)
                .totalReviews(totalReviews)
                .ratingDistribution(ratingDistribution)
                .build();
    }

    public boolean hasPatientReviewedDoctor(String patientId, String doctorId) {
        return doctorReviewRepository.existsByPatientIdAndDoctorId(patientId, doctorId);
    }

    public Optional<DoctorReviewResponse> getPatientReviewForDoctor(String patientId, String doctorId) {
        return doctorReviewRepository.findByPatientIdAndDoctorId(patientId, doctorId)
                .map(this::mapToResponse);
    }

    private DoctorReviewResponse mapToResponse(DoctorReview review) {
        return DoctorReviewResponse.builder()
                .id(review.getId())
                .patientId(review.getPatientId())
                .doctorId(review.getDoctorId())
                .rating(review.getRating())
                .comment(review.getComment())
                .datePosted(review.getDatePosted())
                .lastUpdated(review.getLastUpdated())
                .build();
    }
}