package com.symptomcheck.reviewservice.unit.dtos;

import com.symptomcheck.reviewservice.dtos.DoctorReviewResponse;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class DoctorReviewResponseTest {

    @Test
    void testNoArgsConstructor() {
        // When
        DoctorReviewResponse response = new DoctorReviewResponse();

        // Then
        assertNull(response.getId());
        assertNull(response.getPatientId());
        assertNull(response.getDoctorId());
        assertNull(response.getRating());
        assertNull(response.getComment());
        assertNull(response.getDatePosted());
        assertNull(response.getLastUpdated());
        assertNull(response.getPatientName());
        assertNull(response.getDoctorName());
    }

    @Test
    void testAllArgsConstructor() {
        // Given
        Long id = 1L;
        String patientId = "patient-123";
        String doctorId = "doctor-456";
        Integer rating = 5;
        String comment = "Excellent service";
        Instant datePosted = Instant.now();
        Instant lastUpdated = Instant.now().plusSeconds(3600);
        String patientName = "John Doe";
        String doctorName = "Dr. Smith";

        // When
        DoctorReviewResponse response = new DoctorReviewResponse(
                id, patientId, doctorId, rating, comment,
                datePosted, lastUpdated, patientName, doctorName
        );

        // Then
        assertEquals(id, response.getId());
        assertEquals(patientId, response.getPatientId());
        assertEquals(doctorId, response.getDoctorId());
        assertEquals(rating, response.getRating());
        assertEquals(comment, response.getComment());
        assertEquals(datePosted, response.getDatePosted());
        assertEquals(lastUpdated, response.getLastUpdated());
        assertEquals(patientName, response.getPatientName());
        assertEquals(doctorName, response.getDoctorName());
    }

    @Test
    void testBuilder() {
        // Given
        Long id = 2L;
        String patientId = "patient-789";
        String doctorId = "doctor-999";
        Integer rating = 4;
        String comment = "Good service";
        Instant datePosted = Instant.now();
        Instant lastUpdated = Instant.now().plusSeconds(7200);
        String patientName = "Jane Smith";
        String doctorName = "Dr. Johnson";

        // When
        DoctorReviewResponse response = DoctorReviewResponse.builder()
                .id(id)
                .patientId(patientId)
                .doctorId(doctorId)
                .rating(rating)
                .comment(comment)
                .datePosted(datePosted)
                .lastUpdated(lastUpdated)
                .patientName(patientName)
                .doctorName(doctorName)
                .build();

        // Then
        assertEquals(id, response.getId());
        assertEquals(patientId, response.getPatientId());
        assertEquals(doctorId, response.getDoctorId());
        assertEquals(rating, response.getRating());
        assertEquals(comment, response.getComment());
        assertEquals(datePosted, response.getDatePosted());
        assertEquals(lastUpdated, response.getLastUpdated());
        assertEquals(patientName, response.getPatientName());
        assertEquals(doctorName, response.getDoctorName());
    }

    @Test
    void testSettersAndGetters() {
        // Given
        DoctorReviewResponse response = new DoctorReviewResponse();
        Long id = 3L;
        String patientId = "patient-111";
        String doctorId = "doctor-222";
        Integer rating = 3;
        String comment = "Average service";
        Instant datePosted = Instant.now();
        Instant lastUpdated = Instant.now().plusSeconds(1800);
        String patientName = "Bob Wilson";
        String doctorName = "Dr. Brown";

        // When
        response.setId(id);
        response.setPatientId(patientId);
        response.setDoctorId(doctorId);
        response.setRating(rating);
        response.setComment(comment);
        response.setDatePosted(datePosted);
        response.setLastUpdated(lastUpdated);
        response.setPatientName(patientName);
        response.setDoctorName(doctorName);

        // Then
        assertEquals(id, response.getId());
        assertEquals(patientId, response.getPatientId());
        assertEquals(doctorId, response.getDoctorId());
        assertEquals(rating, response.getRating());
        assertEquals(comment, response.getComment());
        assertEquals(datePosted, response.getDatePosted());
        assertEquals(lastUpdated, response.getLastUpdated());
        assertEquals(patientName, response.getPatientName());
        assertEquals(doctorName, response.getDoctorName());
    }

    @Test
    void testEqualsAndHashCode() {
        // Given
        Instant now = Instant.now();

        DoctorReviewResponse response1 = DoctorReviewResponse.builder()
                .id(1L)
                .patientId("patient-123")
                .doctorId("doctor-456")
                .rating(5)
                .comment("Excellent")
                .datePosted(now)
                .lastUpdated(now)
                .patientName("John Doe")
                .doctorName("Dr. Smith")
                .build();

        DoctorReviewResponse response2 = DoctorReviewResponse.builder()
                .id(1L)
                .patientId("patient-123")
                .doctorId("doctor-456")
                .rating(5)
                .comment("Excellent")
                .datePosted(now)
                .lastUpdated(now)
                .patientName("John Doe")
                .doctorName("Dr. Smith")
                .build();

        DoctorReviewResponse response3 = DoctorReviewResponse.builder()
                .id(2L)
                .patientId("patient-999")
                .doctorId("doctor-888")
                .rating(3)
                .comment("Average")
                .datePosted(now.plusSeconds(3600))
                .lastUpdated(now.plusSeconds(7200))
                .patientName("Alice Brown")
                .doctorName("Dr. Wilson")
                .build();

        // Then
        assertEquals(response1, response2);
        assertNotEquals(response1, response3);
        assertEquals(response1.hashCode(), response2.hashCode());
        assertNotEquals(response1.hashCode(), response3.hashCode());
    }

    @Test
    void testToString() {
        // Given
        DoctorReviewResponse response = DoctorReviewResponse.builder()
                .id(1L)
                .patientId("patient-123")
                .doctorId("doctor-456")
                .rating(5)
                .comment("Great doctor")
                .datePosted(Instant.now())
                .patientName("John Doe")
                .doctorName("Dr. Smith")
                .build();

        // When
        String toString = response.toString();

        // Then
        assertNotNull(toString);
        assertTrue(toString.contains("DoctorReviewResponse"));
        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("patientId=patient-123"));
        assertTrue(toString.contains("doctorId=doctor-456"));
        assertTrue(toString.contains("rating=5"));
        assertTrue(toString.contains("comment=Great doctor"));
        assertTrue(toString.contains("patientName=John Doe"));
        assertTrue(toString.contains("doctorName=Dr. Smith"));
    }

    @Test
    void testWithNullLastUpdated() {
        // Given
        DoctorReviewResponse response = DoctorReviewResponse.builder()
                .id(1L)
                .patientId("patient-123")
                .doctorId("doctor-456")
                .rating(4)
                .comment("Good service")
                .datePosted(Instant.now())
                .lastUpdated(null) // Explicitly set to null
                .patientName("John Doe")
                .doctorName("Dr. Smith")
                .build();

        // Then
        assertEquals(1L, response.getId());
        assertEquals("patient-123", response.getPatientId());
        assertEquals("doctor-456", response.getDoctorId());
        assertEquals(4, response.getRating());
        assertEquals("Good service", response.getComment());
        assertNotNull(response.getDatePosted());
        assertNull(response.getLastUpdated());
        assertEquals("John Doe", response.getPatientName());
        assertEquals("Dr. Smith", response.getDoctorName());
    }

    @Test
    void testWithNullDisplayNames() {
        // Given
        DoctorReviewResponse response = DoctorReviewResponse.builder()
                .id(1L)
                .patientId("patient-123")
                .doctorId("doctor-456")
                .rating(5)
                .comment("Excellent")
                .datePosted(Instant.now())
                .patientName(null) // No patient name
                .doctorName(null)  // No doctor name
                .build();

        // Then
        assertEquals(1L, response.getId());
        assertEquals("patient-123", response.getPatientId());
        assertEquals("doctor-456", response.getDoctorId());
        assertEquals(5, response.getRating());
        assertEquals("Excellent", response.getComment());
        assertNotNull(response.getDatePosted());
        assertNull(response.getPatientName());
        assertNull(response.getDoctorName());
    }

    @Test
    void testWithMinimumRating() {
        // Given
        DoctorReviewResponse response = DoctorReviewResponse.builder()
                .id(1L)
                .patientId("patient-123")
                .doctorId("doctor-456")
                .rating(1) // Minimum rating
                .comment("Poor service")
                .datePosted(Instant.now())
                .build();

        // Then
        assertEquals(1, response.getRating());
    }

    @Test
    void testWithMaximumRating() {
        // Given
        DoctorReviewResponse response = DoctorReviewResponse.builder()
                .id(1L)
                .patientId("patient-123")
                .doctorId("doctor-456")
                .rating(5) // Maximum rating
                .comment("Excellent service")
                .datePosted(Instant.now())
                .build();

        // Then
        assertEquals(5, response.getRating());
    }

    @Test
    void testWithLongComment() {
        // Given
        String longComment = "This is a very detailed review about the doctor's excellent service, " +
                "professionalism, and caring attitude. The doctor took time to listen to all my concerns " +
                "and provided thorough explanations for everything.";

        DoctorReviewResponse response = DoctorReviewResponse.builder()
                .id(1L)
                .patientId("patient-123")
                .doctorId("doctor-456")
                .rating(5)
                .comment(longComment)
                .datePosted(Instant.now())
                .build();

        // Then
        assertEquals(longComment, response.getComment());
    }
}