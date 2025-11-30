package com.symptomcheck.reviewservice.unit.models;

import com.symptomcheck.reviewservice.models.DoctorReview;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.time.Instant;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class DoctorReviewTest {

    private Validator validator;
    private Instant testInstant;

    @BeforeEach
    void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
        testInstant = Instant.now();
    }

    @Nested
    class ConstructorTests {


        @Test
        void shouldCreateDoctorReviewWithAllArgsConstructor() {
            // Given
            Long id = 1L;
            String patientId = "patient-123";
            String doctorId = "doctor-456";
            Integer rating = 5;
            String comment = "Excellent service";
            Instant datePosted = testInstant;
            Instant lastUpdated = testInstant;

            // When
            DoctorReview doctorReview = new DoctorReview(id, patientId, doctorId, rating, comment, datePosted, lastUpdated);

            // Then
            assertNotNull(doctorReview);
            assertEquals(id, doctorReview.getId());
            assertEquals(patientId, doctorReview.getPatientId());
            assertEquals(doctorId, doctorReview.getDoctorId());
            assertEquals(rating, doctorReview.getRating());
            assertEquals(comment, doctorReview.getComment());
            assertEquals(datePosted, doctorReview.getDatePosted());
            assertEquals(lastUpdated, doctorReview.getLastUpdated());
        }

        @Test
        void shouldCreateDoctorReviewWithBuilder() {
            // When
            DoctorReview doctorReview = DoctorReview.builder()
                    .id(1L)
                    .patientId("patient-123")
                    .doctorId("doctor-456")
                    .rating(5)
                    .comment("Excellent service")
                    .datePosted(testInstant)
                    .lastUpdated(testInstant)
                    .build();

            // Then
            assertNotNull(doctorReview);
            assertEquals(1L, doctorReview.getId());
            assertEquals("patient-123", doctorReview.getPatientId());
            assertEquals("doctor-456", doctorReview.getDoctorId());
            assertEquals(5, doctorReview.getRating());
            assertEquals("Excellent service", doctorReview.getComment());
            assertEquals(testInstant, doctorReview.getDatePosted());
            assertEquals(testInstant, doctorReview.getLastUpdated());
        }
    }

    @Nested
    class ValidationTests {
        @Test
        void shouldPassValidationWithValidData() {
            // Given
            DoctorReview doctorReview = DoctorReview.builder()
                    .patientId("patient-123")
                    .doctorId("doctor-456")
                    .rating(3)
                    .comment("Good service")
                    .build();

            // When
            Set<ConstraintViolation<DoctorReview>> violations = validator.validate(doctorReview);

            // Then
            assertTrue(violations.isEmpty(), "There should be no validation violations");
        }

        @Test
        void shouldFailValidationWhenPatientIdIsBlank() {
            // Given
            DoctorReview doctorReview = DoctorReview.builder()
                    .patientId("") // Blank patient ID
                    .doctorId("doctor-456")
                    .rating(3)
                    .comment("Good service")
                    .build();

            // When
            Set<ConstraintViolation<DoctorReview>> violations = validator.validate(doctorReview);

            // Then
            assertFalse(violations.isEmpty());
            assertEquals(1, violations.size());
            ConstraintViolation<DoctorReview> violation = violations.iterator().next();
            assertEquals("Patient ID is required", violation.getMessage());
        }

        @Test
        void shouldFailValidationWhenPatientIdIsNull() {
            // Given
            DoctorReview doctorReview = DoctorReview.builder()
                    .patientId(null) // Null patient ID
                    .doctorId("doctor-456")
                    .rating(3)
                    .comment("Good service")
                    .build();

            // When
            Set<ConstraintViolation<DoctorReview>> violations = validator.validate(doctorReview);

            // Then
            assertFalse(violations.isEmpty());
            assertEquals(1, violations.size());
            ConstraintViolation<DoctorReview> violation = violations.iterator().next();
            assertEquals("Patient ID is required", violation.getMessage());
        }

        @Test
        void shouldFailValidationWhenDoctorIdIsBlank() {
            // Given
            DoctorReview doctorReview = DoctorReview.builder()
                    .patientId("patient-123")
                    .doctorId("") // Blank doctor ID
                    .rating(3)
                    .comment("Good service")
                    .build();

            // When
            Set<ConstraintViolation<DoctorReview>> violations = validator.validate(doctorReview);

            // Then
            assertFalse(violations.isEmpty());
            assertEquals(1, violations.size());
            ConstraintViolation<DoctorReview> violation = violations.iterator().next();
            assertEquals("Doctor ID is required", violation.getMessage());
        }

        @Test
        void shouldFailValidationWhenRatingIsNull() {
            // Given
            DoctorReview doctorReview = DoctorReview.builder()
                    .patientId("patient-123")
                    .doctorId("doctor-456")
                    .rating(null) // Null rating
                    .comment("Good service")
                    .build();

            // When
            Set<ConstraintViolation<DoctorReview>> violations = validator.validate(doctorReview);

            // Then
            assertFalse(violations.isEmpty());
            assertEquals(1, violations.size());
            ConstraintViolation<DoctorReview> violation = violations.iterator().next();
            assertEquals("Rating is required", violation.getMessage());
        }

        @Test
        void shouldFailValidationWhenRatingIsBelowMinimum() {
            // Given
            DoctorReview doctorReview = DoctorReview.builder()
                    .patientId("patient-123")
                    .doctorId("doctor-456")
                    .rating(0) // Below minimum
                    .comment("Good service")
                    .build();

            // When
            Set<ConstraintViolation<DoctorReview>> violations = validator.validate(doctorReview);

            // Then
            assertFalse(violations.isEmpty());
            assertEquals(1, violations.size());
            ConstraintViolation<DoctorReview> violation = violations.iterator().next();
            assertEquals("Rating must be at least 1", violation.getMessage());
        }

        @Test
        void shouldFailValidationWhenRatingIsAboveMaximum() {
            // Given
            DoctorReview doctorReview = DoctorReview.builder()
                    .patientId("patient-123")
                    .doctorId("doctor-456")
                    .rating(6) // Above maximum
                    .comment("Good service")
                    .build();

            // When
            Set<ConstraintViolation<DoctorReview>> violations = validator.validate(doctorReview);

            // Then
            assertFalse(violations.isEmpty());
            assertEquals(1, violations.size());
            ConstraintViolation<DoctorReview> violation = violations.iterator().next();
            assertEquals("Rating must be at most 5", violation.getMessage());
        }

        @Test
        void shouldFailValidationWhenCommentIsBlank() {
            // Given
            DoctorReview doctorReview = DoctorReview.builder()
                    .patientId("patient-123")
                    .doctorId("doctor-456")
                    .rating(3)
                    .comment("") // Blank comment
                    .build();

            // When
            Set<ConstraintViolation<DoctorReview>> violations = validator.validate(doctorReview);

            // Then
            assertFalse(violations.isEmpty());
            assertEquals(1, violations.size());
            ConstraintViolation<DoctorReview> violation = violations.iterator().next();
            assertEquals("Comment cannot be empty", violation.getMessage());
        }

        @Test
        void shouldFailValidationWhenCommentIsNull() {
            // Given
            DoctorReview doctorReview = DoctorReview.builder()
                    .patientId("patient-123")
                    .doctorId("doctor-456")
                    .rating(3)
                    .comment(null) // Null comment
                    .build();

            // When
            Set<ConstraintViolation<DoctorReview>> violations = validator.validate(doctorReview);

            // Then
            assertFalse(violations.isEmpty());
            assertEquals(1, violations.size());
            ConstraintViolation<DoctorReview> violation = violations.iterator().next();
            assertEquals("Comment cannot be empty", violation.getMessage());
        }

        @Test
        void shouldFailValidationWhenCommentExceedsMaxLength() {
            // Given
            String longComment = "a".repeat(2001); // 2001 characters - exceeds limit
            DoctorReview doctorReview = DoctorReview.builder()
                    .patientId("patient-123")
                    .doctorId("doctor-456")
                    .rating(3)
                    .comment(longComment)
                    .build();

            // When
            Set<ConstraintViolation<DoctorReview>> violations = validator.validate(doctorReview);

            // Then
            assertFalse(violations.isEmpty());
            assertEquals(1, violations.size());
            ConstraintViolation<DoctorReview> violation = violations.iterator().next();
            assertEquals("Comment must not exceed 2000 characters", violation.getMessage());
        }

        @Test
        void shouldPassValidationWhenCommentIsExactlyMaxLength() {
            // Given
            String maxLengthComment = "a".repeat(2000); // Exactly 2000 characters
            DoctorReview doctorReview = DoctorReview.builder()
                    .patientId("patient-123")
                    .doctorId("doctor-456")
                    .rating(3)
                    .comment(maxLengthComment)
                    .build();

            // When
            Set<ConstraintViolation<DoctorReview>> violations = validator.validate(doctorReview);

            // Then
            assertTrue(violations.isEmpty(), "There should be no validation violations");
        }
    }

    @Nested
    class GetterSetterTests {
        @Test
        void shouldSetAndGetAllProperties() {
            // Given
            DoctorReview doctorReview = new DoctorReview();
            Long id = 1L;
            String patientId = "patient-123";
            String doctorId = "doctor-456";
            Integer rating = 5;
            String comment = "Excellent service";
            Instant datePosted = testInstant;
            Instant lastUpdated = testInstant;

            // When
            doctorReview.setId(id);
            doctorReview.setPatientId(patientId);
            doctorReview.setDoctorId(doctorId);
            doctorReview.setRating(rating);
            doctorReview.setComment(comment);
            doctorReview.setDatePosted(datePosted);
            doctorReview.setLastUpdated(lastUpdated);

            // Then
            assertEquals(id, doctorReview.getId());
            assertEquals(patientId, doctorReview.getPatientId());
            assertEquals(doctorId, doctorReview.getDoctorId());
            assertEquals(rating, doctorReview.getRating());
            assertEquals(comment, doctorReview.getComment());
            assertEquals(datePosted, doctorReview.getDatePosted());
            assertEquals(lastUpdated, doctorReview.getLastUpdated());
        }
    }

    @Nested
    class LifecycleCallbackTests {
        @Test
        void shouldSetDatePostedOnCreateWhenNull() throws Exception {
            // Given
            DoctorReview doctorReview = new DoctorReview();
            doctorReview.setDatePosted(null);

            // When - Invoke @PrePersist method using reflection
            Method onCreateMethod = DoctorReview.class.getDeclaredMethod("onCreate");
            onCreateMethod.setAccessible(true);
            onCreateMethod.invoke(doctorReview);

            // Then
            assertNotNull(doctorReview.getDatePosted());
        }

        @Test
        void shouldNotChangeDatePostedOnCreateWhenAlreadySet() throws Exception {
            // Given
            DoctorReview doctorReview = new DoctorReview();
            doctorReview.setDatePosted(testInstant);

            // When - Invoke @PrePersist method using reflection
            Method onCreateMethod = DoctorReview.class.getDeclaredMethod("onCreate");
            onCreateMethod.setAccessible(true);
            onCreateMethod.invoke(doctorReview);

            // Then
            assertEquals(testInstant, doctorReview.getDatePosted());
        }

        @Test
        void shouldSetLastUpdatedOnUpdate() throws Exception {
            // Given
            DoctorReview doctorReview = new DoctorReview();
            doctorReview.setLastUpdated(null);

            // When - Invoke @PreUpdate method using reflection
            Method onUpdateMethod = DoctorReview.class.getDeclaredMethod("onUpdate");
            onUpdateMethod.setAccessible(true);
            onUpdateMethod.invoke(doctorReview);

            // Then
            assertNotNull(doctorReview.getLastUpdated());
        }

        @Test
        void shouldUpdateLastUpdatedOnUpdate() throws Exception {
            // Given
            DoctorReview doctorReview = new DoctorReview();
            doctorReview.setLastUpdated(testInstant);

            // Wait a moment to ensure different timestamp
            Thread.sleep(10);

            // When - Invoke @PreUpdate method using reflection
            Method onUpdateMethod = DoctorReview.class.getDeclaredMethod("onUpdate");
            onUpdateMethod.setAccessible(true);
            onUpdateMethod.invoke(doctorReview);

            // Then
            assertNotNull(doctorReview.getLastUpdated());
            assertNotEquals(testInstant, doctorReview.getLastUpdated());
        }
    }

    @Nested
    class BuilderDefaultTests {
        @Test
        void shouldSetDefaultDatePostedWhenUsingBuilder() {
            // When
            DoctorReview doctorReview = DoctorReview.builder()
                    .patientId("patient-123")
                    .doctorId("doctor-456")
                    .rating(3)
                    .comment("Test comment")
                    .build();

            // Then
            assertNotNull(doctorReview.getDatePosted());
            assertNull(doctorReview.getLastUpdated());
        }

        @Test
        void shouldOverrideDefaultDatePostedWhenExplicitlySet() {
            // Given
            Instant customDate = Instant.now().minusSeconds(3600); // 1 hour ago

            // When
            DoctorReview doctorReview = DoctorReview.builder()
                    .patientId("patient-123")
                    .doctorId("doctor-456")
                    .rating(3)
                    .comment("Test comment")
                    .datePosted(customDate)
                    .build();

            // Then
            assertEquals(customDate, doctorReview.getDatePosted());
        }
    }



}