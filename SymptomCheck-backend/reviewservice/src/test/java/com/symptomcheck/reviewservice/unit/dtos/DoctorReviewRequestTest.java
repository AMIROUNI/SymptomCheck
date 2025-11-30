package com.symptomcheck.reviewservice.unit.dtos;

import com.symptomcheck.reviewservice.dtos.DoctorReviewRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class DoctorReviewRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void testNoArgsConstructor() {
        // When
        DoctorReviewRequest request = new DoctorReviewRequest();

        // Then
        assertNull(request.getDoctorId());
        assertNull(request.getRating());
        assertNull(request.getComment());
    }

    @Test
    void testAllArgsConstructor() {
        // Given
        String doctorId = "doctor-123";
        Integer rating = 5;
        String comment = "Excellent service";

        // When
        DoctorReviewRequest request = new DoctorReviewRequest(doctorId, rating, comment);

        // Then
        assertEquals(doctorId, request.getDoctorId());
        assertEquals(rating, request.getRating());
        assertEquals(comment, request.getComment());
    }

    @Test
    void testBuilder() {
        // Given
        String doctorId = "doctor-456";
        Integer rating = 4;
        String comment = "Good service";

        // When
        DoctorReviewRequest request = DoctorReviewRequest.builder()
                .doctorId(doctorId)
                .rating(rating)
                .comment(comment)
                .build();

        // Then
        assertEquals(doctorId, request.getDoctorId());
        assertEquals(rating, request.getRating());
        assertEquals(comment, request.getComment());
    }

    @Test
    void testSettersAndGetters() {
        // Given
        DoctorReviewRequest request = new DoctorReviewRequest();
        String doctorId = "doctor-789";
        Integer rating = 3;
        String comment = "Average service";

        // When
        request.setDoctorId(doctorId);
        request.setRating(rating);
        request.setComment(comment);

        // Then
        assertEquals(doctorId, request.getDoctorId());
        assertEquals(rating, request.getRating());
        assertEquals(comment, request.getComment());
    }

    @Test
    void testEqualsAndHashCode() {
        // Given
        DoctorReviewRequest request1 = DoctorReviewRequest.builder()
                .doctorId("doctor-123")
                .rating(5)
                .comment("Excellent")
                .build();

        DoctorReviewRequest request2 = DoctorReviewRequest.builder()
                .doctorId("doctor-123")
                .rating(5)
                .comment("Excellent")
                .build();

        DoctorReviewRequest request3 = DoctorReviewRequest.builder()
                .doctorId("doctor-456")
                .rating(3)
                .comment("Average")
                .build();

        // Then
        assertEquals(request1, request2);
        assertNotEquals(request1, request3);
        assertEquals(request1.hashCode(), request2.hashCode());
        assertNotEquals(request1.hashCode(), request3.hashCode());
    }

    @Test
    void testToString() {
        // Given
        DoctorReviewRequest request = DoctorReviewRequest.builder()
                .doctorId("doctor-123")
                .rating(5)
                .comment("Great doctor")
                .build();

        // When
        String toString = request.toString();

        // Then
        assertNotNull(toString);
        assertTrue(toString.contains("DoctorReviewRequest"));
        assertTrue(toString.contains("doctorId=doctor-123"));
        assertTrue(toString.contains("rating=5"));
        assertTrue(toString.contains("comment=Great doctor"));
    }

    @Test
    void testValidRequest() {
        // Given
        DoctorReviewRequest request = DoctorReviewRequest.builder()
                .doctorId("doctor-123")
                .rating(3)
                .comment("Good service")
                .build();

        // When
        Set<ConstraintViolation<DoctorReviewRequest>> violations = validator.validate(request);

        // Then
        assertTrue(violations.isEmpty(), "There should be no validation violations");
    }

    @Test
    void testInvalidWhenDoctorIdIsBlank() {
        // Given
        DoctorReviewRequest request = DoctorReviewRequest.builder()
                .doctorId("") // Blank doctor ID
                .rating(3)
                .comment("Good service")
                .build();

        // When
        Set<ConstraintViolation<DoctorReviewRequest>> violations = validator.validate(request);

        // Then
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        ConstraintViolation<DoctorReviewRequest> violation = violations.iterator().next();
        assertEquals("Doctor ID is required", violation.getMessage());
    }

    @Test
    void testInvalidWhenDoctorIdIsNull() {
        // Given
        DoctorReviewRequest request = DoctorReviewRequest.builder()
                .doctorId(null) // Null doctor ID
                .rating(3)
                .comment("Good service")
                .build();

        // When
        Set<ConstraintViolation<DoctorReviewRequest>> violations = validator.validate(request);

        // Then
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        ConstraintViolation<DoctorReviewRequest> violation = violations.iterator().next();
        assertEquals("Doctor ID is required", violation.getMessage());
    }

    @Test
    void testInvalidWhenRatingIsNull() {
        // Given
        DoctorReviewRequest request = DoctorReviewRequest.builder()
                .doctorId("doctor-123")
                .rating(null) // Null rating
                .comment("Good service")
                .build();

        // When
        Set<ConstraintViolation<DoctorReviewRequest>> violations = validator.validate(request);

        // Then
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        ConstraintViolation<DoctorReviewRequest> violation = violations.iterator().next();
        assertEquals("Rating is required", violation.getMessage());
    }

    @Test
    void testInvalidWhenCommentIsBlank() {
        // Given
        DoctorReviewRequest request = DoctorReviewRequest.builder()
                .doctorId("doctor-123")
                .rating(3)
                .comment("") // Blank comment
                .build();

        // When
        Set<ConstraintViolation<DoctorReviewRequest>> violations = validator.validate(request);

        // Then
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        ConstraintViolation<DoctorReviewRequest> violation = violations.iterator().next();
        assertEquals("Comment cannot be empty", violation.getMessage());
    }

    @Test
    void testInvalidWhenCommentIsNull() {
        // Given
        DoctorReviewRequest request = DoctorReviewRequest.builder()
                .doctorId("doctor-123")
                .rating(3)
                .comment(null) // Null comment
                .build();

        // When
        Set<ConstraintViolation<DoctorReviewRequest>> violations = validator.validate(request);

        // Then
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        ConstraintViolation<DoctorReviewRequest> violation = violations.iterator().next();
        assertEquals("Comment cannot be empty", violation.getMessage());
    }

    @Test
    void testValidWhenCommentIsExactlyMaxLength() {
        // Given
        String maxLengthComment = "a".repeat(2000); // Exactly 2000 characters
        DoctorReviewRequest request = DoctorReviewRequest.builder()
                .doctorId("doctor-123")
                .rating(3)
                .comment(maxLengthComment)
                .build();

        // When
        Set<ConstraintViolation<DoctorReviewRequest>> violations = validator.validate(request);

        // Then
        assertTrue(violations.isEmpty(), "There should be no validation violations");
    }

    @Test
    void testWithMinimumRating() {
        // Given
        DoctorReviewRequest request = DoctorReviewRequest.builder()
                .doctorId("doctor-123")
                .rating(1) // Minimum rating
                .comment("Poor service")
                .build();

        // When
        Set<ConstraintViolation<DoctorReviewRequest>> violations = validator.validate(request);

        // Then
        assertTrue(violations.isEmpty(), "There should be no validation violations");
        assertEquals(1, request.getRating());
    }

    @Test
    void testWithMaximumRating() {
        // Given
        DoctorReviewRequest request = DoctorReviewRequest.builder()
                .doctorId("doctor-123")
                .rating(5) // Maximum rating
                .comment("Excellent service")
                .build();

        // When
        Set<ConstraintViolation<DoctorReviewRequest>> violations = validator.validate(request);

        // Then
        assertTrue(violations.isEmpty(), "There should be no validation violations");
        assertEquals(5, request.getRating());
    }

    @Test
    void testMultipleValidationErrors() {
        // Given
        DoctorReviewRequest request = DoctorReviewRequest.builder()
                .doctorId(null) // Invalid
                .rating(0)      // Invalid
                .comment("")    // Invalid
                .build();

        // When
        Set<ConstraintViolation<DoctorReviewRequest>> violations = validator.validate(request);

        // Then
        assertFalse(violations.isEmpty());
        assertEquals(3, violations.size());
    }
}