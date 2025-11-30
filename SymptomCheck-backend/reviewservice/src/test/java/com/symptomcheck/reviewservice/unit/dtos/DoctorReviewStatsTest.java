package com.symptomcheck.reviewservice.unit.dtos;

import com.symptomcheck.reviewservice.dtos.DoctorReviewStats;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DoctorReviewStatsTest {

    @Test
    void testNoArgsConstructor() {
        // When
        DoctorReviewStats stats = new DoctorReviewStats();

        // Then
        assertNull(stats.getDoctorId());
        assertNull(stats.getAverageRating());
        assertNull(stats.getTotalReviews());
        assertNull(stats.getRatingDistribution());
    }

    @Test
    void testAllArgsConstructor() {
        // Given
        String doctorId = "doctor-123";
        Double averageRating = 4.5;
        Long totalReviews = 10L;
        Map<Integer, Long> ratingDistribution = Map.of(1, 1L, 2, 2L, 3, 3L, 4, 2L, 5, 2L);

        // When
        DoctorReviewStats stats = new DoctorReviewStats(doctorId, averageRating, totalReviews, ratingDistribution);

        // Then
        assertEquals(doctorId, stats.getDoctorId());
        assertEquals(averageRating, stats.getAverageRating());
        assertEquals(totalReviews, stats.getTotalReviews());
        assertEquals(ratingDistribution, stats.getRatingDistribution());
    }

    @Test
    void testBuilder() {
        // Given
        String doctorId = "doctor-456";
        Double averageRating = 3.8;
        Long totalReviews = 5L;
        Map<Integer, Long> ratingDistribution = Map.of(3, 2L, 4, 2L, 5, 1L);

        // When
        DoctorReviewStats stats = DoctorReviewStats.builder()
                .doctorId(doctorId)
                .averageRating(averageRating)
                .totalReviews(totalReviews)
                .ratingDistribution(ratingDistribution)
                .build();

        // Then
        assertEquals(doctorId, stats.getDoctorId());
        assertEquals(averageRating, stats.getAverageRating());
        assertEquals(totalReviews, stats.getTotalReviews());
        assertEquals(ratingDistribution, stats.getRatingDistribution());
    }

    @Test
    void testSettersAndGetters() {
        // Given
        DoctorReviewStats stats = new DoctorReviewStats();
        String doctorId = "doctor-789";
        Double averageRating = 4.2;
        Long totalReviews = 15L;
        Map<Integer, Long> ratingDistribution = Map.of(1, 1L, 2, 1L, 3, 3L, 4, 5L, 5, 5L);

        // When
        stats.setDoctorId(doctorId);
        stats.setAverageRating(averageRating);
        stats.setTotalReviews(totalReviews);
        stats.setRatingDistribution(ratingDistribution);

        // Then
        assertEquals(doctorId, stats.getDoctorId());
        assertEquals(averageRating, stats.getAverageRating());
        assertEquals(totalReviews, stats.getTotalReviews());
        assertEquals(ratingDistribution, stats.getRatingDistribution());
    }

    @Test
    void testEqualsAndHashCode() {
        // Given
        Map<Integer, Long> distribution = Map.of(1, 1L, 2, 2L, 3, 3L);

        DoctorReviewStats stats1 = DoctorReviewStats.builder()
                .doctorId("doctor-123")
                .averageRating(4.0)
                .totalReviews(6L)
                .ratingDistribution(distribution)
                .build();

        DoctorReviewStats stats2 = DoctorReviewStats.builder()
                .doctorId("doctor-123")
                .averageRating(4.0)
                .totalReviews(6L)
                .ratingDistribution(distribution)
                .build();

        DoctorReviewStats stats3 = DoctorReviewStats.builder()
                .doctorId("doctor-456")
                .averageRating(3.5)
                .totalReviews(8L)
                .ratingDistribution(Map.of(1, 2L, 2, 3L))
                .build();

        // Then
        assertEquals(stats1, stats2);
        assertNotEquals(stats1, stats3);
        assertEquals(stats1.hashCode(), stats2.hashCode());
        assertNotEquals(stats1.hashCode(), stats3.hashCode());
    }

    @Test
    void testToString() {
        // Given
        DoctorReviewStats stats = DoctorReviewStats.builder()
                .doctorId("doctor-123")
                .averageRating(4.5)
                .totalReviews(10L)
                .ratingDistribution(Map.of(5, 10L))
                .build();

        // When
        String toString = stats.toString();

        // Then
        assertNotNull(toString);
        assertTrue(toString.contains("DoctorReviewStats"));
        assertTrue(toString.contains("doctorId=doctor-123"));
        assertTrue(toString.contains("averageRating=4.5"));
        assertTrue(toString.contains("totalReviews=10"));
    }

    @Test
    void testWithNullValues() {
        // When
        DoctorReviewStats stats = DoctorReviewStats.builder().build();

        // Then
        assertNull(stats.getDoctorId());
        assertNull(stats.getAverageRating());
        assertNull(stats.getTotalReviews());
        assertNull(stats.getRatingDistribution());
    }

    @Test
    void testWithZeroValues() {
        // Given
        DoctorReviewStats stats = DoctorReviewStats.builder()
                .doctorId("doctor-zero")
                .averageRating(0.0)
                .totalReviews(0L)
                .ratingDistribution(Map.of())
                .build();

        // Then
        assertEquals("doctor-zero", stats.getDoctorId());
        assertEquals(0.0, stats.getAverageRating());
        assertEquals(0L, stats.getTotalReviews());
        assertTrue(stats.getRatingDistribution().isEmpty());
    }

    @Test
    void testWithDecimalAverageRating() {
        // Given
        DoctorReviewStats stats = DoctorReviewStats.builder()
                .doctorId("doctor-decimal")
                .averageRating(3.75)
                .totalReviews(4L)
                .ratingDistribution(Map.of(3, 1L, 4, 3L))
                .build();

        // Then
        assertEquals(3.75, stats.getAverageRating());
    }
}