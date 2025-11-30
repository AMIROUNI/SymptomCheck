package com.symptomcheck.reviewservice.unit.services;

import com.symptomcheck.reviewservice.dtos.DoctorReviewRequest;
import com.symptomcheck.reviewservice.dtos.DoctorReviewResponse;
import com.symptomcheck.reviewservice.dtos.DoctorReviewStats;
import com.symptomcheck.reviewservice.exceptions.ReviewAlreadyExistsException;
import com.symptomcheck.reviewservice.exceptions.ReviewNotFoundException;
import com.symptomcheck.reviewservice.models.DoctorReview;
import com.symptomcheck.reviewservice.repositories.DoctorReviewRepository;
import com.symptomcheck.reviewservice.services.DoctorReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DoctorReviewServiceTest {

    @Mock
    private DoctorReviewRepository doctorReviewRepository;

    @InjectMocks
    private DoctorReviewService doctorReviewService;

    private String patientId;
    private String doctorId;
    private DoctorReviewRequest reviewRequest;
    private DoctorReview doctorReview;
    private Instant fixedInstant;

    @BeforeEach
    void setUp() {
        patientId = "patient-123";
        doctorId = "doctor-456";
        fixedInstant = Instant.now();

        reviewRequest = DoctorReviewRequest.builder()
                .doctorId(doctorId)
                .rating(5)
                .comment("Excellent doctor, very professional and caring")
                .build();

        doctorReview = DoctorReview.builder()
                .id(1L)
                .patientId(patientId)
                .doctorId(doctorId)
                .rating(5)
                .comment("Excellent doctor, very professional and caring")
                .datePosted(fixedInstant)
                .lastUpdated(fixedInstant)
                .build();
    }

    @Nested
    class CreateReviewTests {
        @Test
        void shouldCreateReviewSuccessfully() {
            // Given
            when(doctorReviewRepository.existsByPatientIdAndDoctorId(patientId, doctorId))
                    .thenReturn(false);
            when(doctorReviewRepository.save(any(DoctorReview.class)))
                    .thenReturn(doctorReview);

            // When
            DoctorReviewResponse result = doctorReviewService.createReview(patientId, reviewRequest);

            // Then
            assertNotNull(result);
            assertEquals(1L, result.getId());
            assertEquals(patientId, result.getPatientId());
            assertEquals(doctorId, result.getDoctorId());
            assertEquals(5, result.getRating());
            assertEquals("Excellent doctor, very professional and caring", result.getComment());
            assertEquals(fixedInstant, result.getDatePosted());

            verify(doctorReviewRepository).existsByPatientIdAndDoctorId(patientId, doctorId);
            verify(doctorReviewRepository).save(any(DoctorReview.class));
        }

        @Test
        void shouldThrowReviewAlreadyExistsExceptionWhenPatientAlreadyReviewed() {
            // Given
            when(doctorReviewRepository.existsByPatientIdAndDoctorId(patientId, doctorId))
                    .thenReturn(true);

            // When & Then
            ReviewAlreadyExistsException exception = assertThrows(
                    ReviewAlreadyExistsException.class,
                    () -> doctorReviewService.createReview(patientId, reviewRequest)
            );

            assertEquals("Patient " + patientId + " has already reviewed doctor " + doctorId, exception.getMessage());
            verify(doctorReviewRepository).existsByPatientIdAndDoctorId(patientId, doctorId);
            verify(doctorReviewRepository, never()).save(any(DoctorReview.class));
        }
    }

    @Nested
    class UpdateReviewTests {
        @Test
        void shouldUpdateReviewSuccessfully() {
            // Given
            Long reviewId = 1L;
            DoctorReviewRequest updateRequest = DoctorReviewRequest.builder()
                    .doctorId(doctorId)
                    .rating(4)
                    .comment("Updated comment - still very good")
                    .build();

            DoctorReview existingReview = DoctorReview.builder()
                    .id(reviewId)
                    .patientId(patientId)
                    .doctorId(doctorId)
                    .rating(5)
                    .comment("Original comment")
                    .datePosted(fixedInstant)
                    .build();

            DoctorReview updatedReview = DoctorReview.builder()
                    .id(reviewId)
                    .patientId(patientId)
                    .doctorId(doctorId)
                    .rating(4)
                    .comment("Updated comment - still very good")
                    .datePosted(fixedInstant)
                    .lastUpdated(fixedInstant)
                    .build();

            when(doctorReviewRepository.findById(reviewId))
                    .thenReturn(Optional.of(existingReview));
            when(doctorReviewRepository.save(any(DoctorReview.class)))
                    .thenReturn(updatedReview);

            // When
            DoctorReviewResponse result = doctorReviewService.updateReview(reviewId, patientId, updateRequest);

            // Then
            assertNotNull(result);
            assertEquals(reviewId, result.getId());
            assertEquals(4, result.getRating());
            assertEquals("Updated comment - still very good", result.getComment());

            verify(doctorReviewRepository).findById(reviewId);
            verify(doctorReviewRepository).save(existingReview);
        }

        @Test
        void shouldThrowReviewNotFoundExceptionWhenReviewNotFound() {
            // Given
            Long reviewId = 999L;
            when(doctorReviewRepository.findById(reviewId))
                    .thenReturn(Optional.empty());

            // When & Then
            ReviewNotFoundException exception = assertThrows(
                    ReviewNotFoundException.class,
                    () -> doctorReviewService.updateReview(reviewId, patientId, reviewRequest)
            );

            assertEquals("Review not found with id: " + reviewId, exception.getMessage());
            verify(doctorReviewRepository).findById(reviewId);
            verify(doctorReviewRepository, never()).save(any(DoctorReview.class));
        }

        @Test
        void shouldThrowSecurityExceptionWhenPatientIsNotOwner() {
            // Given
            Long reviewId = 1L;
            String differentPatientId = "different-patient";
            DoctorReview existingReview = DoctorReview.builder()
                    .id(reviewId)
                    .patientId(patientId) // Original patient
                    .doctorId(doctorId)
                    .rating(5)
                    .comment("Original comment")
                    .build();

            when(doctorReviewRepository.findById(reviewId))
                    .thenReturn(Optional.of(existingReview));

            // When & Then
            SecurityException exception = assertThrows(
                    SecurityException.class,
                    () -> doctorReviewService.updateReview(reviewId, differentPatientId, reviewRequest)
            );

            assertEquals("Patient can only update their own reviews", exception.getMessage());
            verify(doctorReviewRepository).findById(reviewId);
            verify(doctorReviewRepository, never()).save(any(DoctorReview.class));
        }
    }

    @Nested
    class DeleteReviewTests {
        @Test
        void shouldDeleteReviewSuccessfully() {
            // Given
            Long reviewId = 1L;
            DoctorReview existingReview = DoctorReview.builder()
                    .id(reviewId)
                    .patientId(patientId)
                    .doctorId(doctorId)
                    .rating(5)
                    .comment("Test comment")
                    .build();

            when(doctorReviewRepository.findById(reviewId))
                    .thenReturn(Optional.of(existingReview));
            doNothing().when(doctorReviewRepository).delete(existingReview);

            // When
            doctorReviewService.deleteReview(reviewId, patientId);

            // Then
            verify(doctorReviewRepository).findById(reviewId);
            verify(doctorReviewRepository).delete(existingReview);
        }

        @Test
        void shouldThrowReviewNotFoundExceptionWhenReviewNotFound() {
            // Given
            Long reviewId = 999L;
            when(doctorReviewRepository.findById(reviewId))
                    .thenReturn(Optional.empty());

            // When & Then
            ReviewNotFoundException exception = assertThrows(
                    ReviewNotFoundException.class,
                    () -> doctorReviewService.deleteReview(reviewId, patientId)
            );

            assertEquals("Review not found with id: " + reviewId, exception.getMessage());
            verify(doctorReviewRepository).findById(reviewId);
            verify(doctorReviewRepository, never()).delete(any(DoctorReview.class));
        }

        @Test
        void shouldThrowSecurityExceptionWhenPatientIsNotOwner() {
            // Given
            Long reviewId = 1L;
            String differentPatientId = "different-patient";
            DoctorReview existingReview = DoctorReview.builder()
                    .id(reviewId)
                    .patientId(patientId) // Original patient
                    .doctorId(doctorId)
                    .rating(5)
                    .comment("Test comment")
                    .build();

            when(doctorReviewRepository.findById(reviewId))
                    .thenReturn(Optional.of(existingReview));

            // When & Then
            SecurityException exception = assertThrows(
                    SecurityException.class,
                    () -> doctorReviewService.deleteReview(reviewId, differentPatientId)
            );

            assertEquals("Patient can only delete their own reviews", exception.getMessage());
            verify(doctorReviewRepository).findById(reviewId);
            verify(doctorReviewRepository, never()).delete(any(DoctorReview.class));
        }
    }

    @Nested
    class GetReviewsTests {
        @Test
        void shouldGetReviewsByDoctorSuccessfully() {
            // Given
            List<DoctorReview> reviews = Arrays.asList(
                    doctorReview,
                    DoctorReview.builder()
                            .id(2L)
                            .patientId("patient-2")
                            .doctorId(doctorId)
                            .rating(4)
                            .comment("Another review")
                            .datePosted(fixedInstant)
                            .build()
            );

            when(doctorReviewRepository.findByDoctorId(doctorId))
                    .thenReturn(reviews);

            // When
            List<DoctorReviewResponse> result = doctorReviewService.getReviewsByDoctor(doctorId);

            // Then
            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals(1L, result.get(0).getId());
            assertEquals(2L, result.get(1).getId());
            verify(doctorReviewRepository).findByDoctorId(doctorId);
        }

        @Test
        void shouldGetReviewsByPatientSuccessfully() {
            // Given
            List<DoctorReview> reviews = Arrays.asList(
                    doctorReview,
                    DoctorReview.builder()
                            .id(2L)
                            .patientId(patientId)
                            .doctorId("doctor-789")
                            .rating(4)
                            .comment("Review for another doctor")
                            .datePosted(fixedInstant)
                            .build()
            );

            when(doctorReviewRepository.findByPatientId(patientId))
                    .thenReturn(reviews);

            // When
            List<DoctorReviewResponse> result = doctorReviewService.getReviewsByPatient(patientId);

            // Then
            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals(1L, result.get(0).getId());
            assertEquals(2L, result.get(1).getId());
            verify(doctorReviewRepository).findByPatientId(patientId);
        }

        @Test
        void shouldGetReviewByIdSuccessfully() {
            // Given
            Long reviewId = 1L;
            when(doctorReviewRepository.findById(reviewId))
                    .thenReturn(Optional.of(doctorReview));

            // When
            DoctorReviewResponse result = doctorReviewService.getReviewById(reviewId);

            // Then
            assertNotNull(result);
            assertEquals(reviewId, result.getId());
            assertEquals(patientId, result.getPatientId());
            assertEquals(doctorId, result.getDoctorId());
            verify(doctorReviewRepository).findById(reviewId);
        }

        @Test
        void shouldThrowReviewNotFoundExceptionWhenReviewByIdNotFound() {
            // Given
            Long reviewId = 999L;
            when(doctorReviewRepository.findById(reviewId))
                    .thenReturn(Optional.empty());

            // When & Then
            ReviewNotFoundException exception = assertThrows(
                    ReviewNotFoundException.class,
                    () -> doctorReviewService.getReviewById(reviewId)
            );

            assertEquals("Review not found with id: " + reviewId, exception.getMessage());
            verify(doctorReviewRepository).findById(reviewId);
        }
    }

    @Nested
    class ReviewStatsTests {
        @Test
        void shouldGetDoctorReviewStatsSuccessfully() {
            // Given
            Double averageRating = 4.5;
            Long totalReviews = 10L;
            List<Object[]> distribution = Arrays.asList(
                    new Object[]{1, 1L},
                    new Object[]{2, 2L},
                    new Object[]{3, 3L},
                    new Object[]{4, 2L},
                    new Object[]{5, 2L}
            );

            when(doctorReviewRepository.findAverageRatingByDoctorId(doctorId))
                    .thenReturn(averageRating);
            when(doctorReviewRepository.countByDoctorId(doctorId))
                    .thenReturn(totalReviews);
            when(doctorReviewRepository.getRatingDistributionByDoctorId(doctorId))
                    .thenReturn(distribution);

            // When
            DoctorReviewStats result = doctorReviewService.getDoctorReviewStats(doctorId);

            // Then
            assertNotNull(result);
            assertEquals(doctorId, result.getDoctorId());
            assertEquals(4.5, result.getAverageRating());
            assertEquals(10L, result.getTotalReviews());

            Map<Integer, Long> ratingDistribution = result.getRatingDistribution();
            assertEquals(5, ratingDistribution.size());
            assertEquals(1L, ratingDistribution.get(1));
            assertEquals(2L, ratingDistribution.get(2));
            assertEquals(3L, ratingDistribution.get(3));
            assertEquals(2L, ratingDistribution.get(4));
            assertEquals(2L, ratingDistribution.get(5));

            verify(doctorReviewRepository).findAverageRatingByDoctorId(doctorId);
            verify(doctorReviewRepository).countByDoctorId(doctorId);
            verify(doctorReviewRepository).getRatingDistributionByDoctorId(doctorId);
        }

        @Test
        void shouldGetDoctorReviewStatsWithNoReviews() {
            // Given
            when(doctorReviewRepository.findAverageRatingByDoctorId(doctorId))
                    .thenReturn(null);
            when(doctorReviewRepository.countByDoctorId(doctorId))
                    .thenReturn(0L);
            when(doctorReviewRepository.getRatingDistributionByDoctorId(doctorId))
                    .thenReturn(Arrays.asList());

            // When
            DoctorReviewStats result = doctorReviewService.getDoctorReviewStats(doctorId);

            // Then
            assertNotNull(result);
            assertEquals(doctorId, result.getDoctorId());
            assertEquals(0.0, result.getAverageRating());
            assertEquals(0L, result.getTotalReviews());
            assertTrue(result.getRatingDistribution().isEmpty());
        }

        @Test
        void shouldRoundAverageRatingToOneDecimal() {
            // Given
            Double averageRating = 4.333333;
            Long totalReviews = 3L;
            List<Object[]> distribution = Arrays.asList(
                    new Object[]{4, 2L},
                    new Object[]{5, 1L}
            );

            when(doctorReviewRepository.findAverageRatingByDoctorId(doctorId))
                    .thenReturn(averageRating);
            when(doctorReviewRepository.countByDoctorId(doctorId))
                    .thenReturn(totalReviews);
            when(doctorReviewRepository.getRatingDistributionByDoctorId(doctorId))
                    .thenReturn(distribution);

            // When
            DoctorReviewStats result = doctorReviewService.getDoctorReviewStats(doctorId);

            // Then
            assertEquals(4.3, result.getAverageRating()); // Rounded to one decimal
        }
    }

    @Nested
    class PatientReviewChecksTests {
        @Test
        void shouldReturnTrueWhenPatientHasReviewedDoctor() {
            // Given
            when(doctorReviewRepository.existsByPatientIdAndDoctorId(patientId, doctorId))
                    .thenReturn(true);

            // When
            boolean result = doctorReviewService.hasPatientReviewedDoctor(patientId, doctorId);

            // Then
            assertTrue(result);
            verify(doctorReviewRepository).existsByPatientIdAndDoctorId(patientId, doctorId);
        }

        @Test
        void shouldReturnFalseWhenPatientHasNotReviewedDoctor() {
            // Given
            when(doctorReviewRepository.existsByPatientIdAndDoctorId(patientId, doctorId))
                    .thenReturn(false);

            // When
            boolean result = doctorReviewService.hasPatientReviewedDoctor(patientId, doctorId);

            // Then
            assertFalse(result);
            verify(doctorReviewRepository).existsByPatientIdAndDoctorId(patientId, doctorId);
        }

        @Test
        void shouldGetPatientReviewForDoctorWhenExists() {
            // Given
            when(doctorReviewRepository.findByPatientIdAndDoctorId(patientId, doctorId))
                    .thenReturn(Optional.of(doctorReview));

            // When
            Optional<DoctorReviewResponse> result = doctorReviewService.getPatientReviewForDoctor(patientId, doctorId);

            // Then
            assertTrue(result.isPresent());
            assertEquals(1L, result.get().getId());
            verify(doctorReviewRepository).findByPatientIdAndDoctorId(patientId, doctorId);
        }

        @Test
        void shouldReturnEmptyWhenPatientHasNoReviewForDoctor() {
            // Given
            when(doctorReviewRepository.findByPatientIdAndDoctorId(patientId, doctorId))
                    .thenReturn(Optional.empty());

            // When
            Optional<DoctorReviewResponse> result = doctorReviewService.getPatientReviewForDoctor(patientId, doctorId);

            // Then
            assertFalse(result.isPresent());
            verify(doctorReviewRepository).findByPatientIdAndDoctorId(patientId, doctorId);
        }
    }

}