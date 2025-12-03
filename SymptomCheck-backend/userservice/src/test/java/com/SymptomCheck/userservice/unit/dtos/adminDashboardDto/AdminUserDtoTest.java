package com.SymptomCheck.userservice.unit.dtos.adminDashboardDto;

import com.SymptomCheck.userservice.dtos.admindashboarddto.AdminUserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class AdminUserDtoTest {

    private AdminUserDto adminUserDto;

    @BeforeEach
    void setUp() {
        adminUserDto = new AdminUserDto();
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("should set and get all fields correctly")
        void shouldSetAndGetAllFields() {
            // Given
            String id = "user-123";
            String phoneNumber = "+1234567890";
            String profilePhotoUrl = "https://example.com/photo.jpg";
            boolean profileComplete = true;
            Long clinicId = 1L;
            String speciality = "Cardiology";
            String description = "Experienced cardiologist";
            String diploma = "MD, PhD";
            Instant createdAt = Instant.now();
            Instant updatedAt = Instant.now().plusSeconds(3600);
            String role = "DOCTOR";

            // When
            adminUserDto.setId(id);
            adminUserDto.setPhoneNumber(phoneNumber);
            adminUserDto.setProfilePhotoUrl(profilePhotoUrl);
            adminUserDto.setProfileComplete(profileComplete);
            adminUserDto.setClinicId(clinicId);
            adminUserDto.setSpeciality(speciality);
            adminUserDto.setDescription(description);
            adminUserDto.setDiploma(diploma);
            adminUserDto.setCreatedAt(createdAt);
            adminUserDto.setUpdatedAt(updatedAt);
            adminUserDto.setRole(role);

            // Then
            assertEquals(id, adminUserDto.getId());
            assertEquals(phoneNumber, adminUserDto.getPhoneNumber());
            assertEquals(profilePhotoUrl, adminUserDto.getProfilePhotoUrl());
            assertEquals(profileComplete, adminUserDto.isProfileComplete());
            assertEquals(clinicId, adminUserDto.getClinicId());
            assertEquals(speciality, adminUserDto.getSpeciality());
            assertEquals(description, adminUserDto.getDescription());
            assertEquals(diploma, adminUserDto.getDiploma());
            assertEquals(createdAt, adminUserDto.getCreatedAt());
            assertEquals(updatedAt, adminUserDto.getUpdatedAt());
            assertEquals(role, adminUserDto.getRole());
        }

        @Test
        @DisplayName("should handle null values for optional fields")
        void shouldHandleNullValuesForOptionalFields() {
            // When
            adminUserDto.setPhoneNumber(null);
            adminUserDto.setProfilePhotoUrl(null);
            adminUserDto.setClinicId(null);
            adminUserDto.setSpeciality(null);
            adminUserDto.setDescription(null);
            adminUserDto.setDiploma(null);
            adminUserDto.setUpdatedAt(null);

            // Then
            assertNull(adminUserDto.getPhoneNumber());
            assertNull(adminUserDto.getProfilePhotoUrl());
            assertNull(adminUserDto.getClinicId());
            assertNull(adminUserDto.getSpeciality());
            assertNull(adminUserDto.getDescription());
            assertNull(adminUserDto.getDiploma());
            assertNull(adminUserDto.getUpdatedAt());
        }

        @Test
        @DisplayName("should handle empty string values")
        void shouldHandleEmptyStringValues() {
            // Given
            String emptyString = "";

            // When
            adminUserDto.setPhoneNumber(emptyString);
            adminUserDto.setProfilePhotoUrl(emptyString);
            adminUserDto.setSpeciality(emptyString);
            adminUserDto.setDescription(emptyString);
            adminUserDto.setDiploma(emptyString);
            adminUserDto.setRole(emptyString);

            // Then
            assertEquals(emptyString, adminUserDto.getPhoneNumber());
            assertEquals(emptyString, adminUserDto.getProfilePhotoUrl());
            assertEquals(emptyString, adminUserDto.getSpeciality());
            assertEquals(emptyString, adminUserDto.getDescription());
            assertEquals(emptyString, adminUserDto.getDiploma());
            assertEquals(emptyString, adminUserDto.getRole());
        }
    }

    @Nested
    @DisplayName("Boolean Field Tests")
    class BooleanFieldTests {

        @Test
        @DisplayName("should handle profileComplete as true")
        void shouldHandleProfileCompleteAsTrue() {
            // When
            adminUserDto.setProfileComplete(true);

            // Then
            assertTrue(adminUserDto.isProfileComplete());
        }

        @Test
        @DisplayName("should handle profileComplete as false")
        void shouldHandleProfileCompleteAsFalse() {
            // When
            adminUserDto.setProfileComplete(false);

            // Then
            assertFalse(adminUserDto.isProfileComplete());
        }

        @Test
        @DisplayName("should have default profileComplete as false")
        void shouldHaveDefaultProfileCompleteAsFalse() {
            // When creating new instance
            AdminUserDto newDto = new AdminUserDto();

            // Then
            assertFalse(newDto.isProfileComplete());
        }
    }

    @Nested
    @DisplayName("Role Field Tests")
    class RoleFieldTests {

        @Test
        @DisplayName("should handle DOCTOR role")
        void shouldHandleDoctorRole() {
            // When
            adminUserDto.setRole("DOCTOR");

            // Then
            assertEquals("DOCTOR", adminUserDto.getRole());
        }

        @Test
        @DisplayName("should handle PATIENT role")
        void shouldHandlePatientRole() {
            // When
            adminUserDto.setRole("PATIENT");

            // Then
            assertEquals("PATIENT", adminUserDto.getRole());
        }

        @Test
        @DisplayName("should handle null role")
        void shouldHandleNullRole() {
            // When
            adminUserDto.setRole(null);

            // Then
            assertNull(adminUserDto.getRole());
        }

        @Test
        @DisplayName("should handle empty role")
        void shouldHandleEmptyRole() {
            // When
            adminUserDto.setRole("");

            // Then
            assertEquals("", adminUserDto.getRole());
        }

        @Test
        @DisplayName("should handle invalid role values")
        void shouldHandleInvalidRoleValues() {
            // When
            adminUserDto.setRole("ADMIN");
            adminUserDto.setRole("NURSE");
            adminUserDto.setRole("STAFF");

            // Then
            assertEquals("STAFF", adminUserDto.getRole());
        }
    }

    @Nested
    @DisplayName("Timestamp Field Tests")
    class TimestampFieldTests {

        @Test
        @DisplayName("should handle Instant timestamps correctly")
        void shouldHandleInstantTimestampsCorrectly() {
            // Given
            Instant past = Instant.parse("2020-01-01T00:00:00Z");
            Instant future = Instant.parse("2030-01-01T00:00:00Z");

            // When
            adminUserDto.setCreatedAt(past);
            adminUserDto.setUpdatedAt(future);

            // Then
            assertEquals(past, adminUserDto.getCreatedAt());
            assertEquals(future, adminUserDto.getUpdatedAt());
        }

        @Test
        @DisplayName("should handle null updatedAt")
        void shouldHandleNullUpdatedAt() {
            // When
            adminUserDto.setUpdatedAt(null);

            // Then
            assertNull(adminUserDto.getUpdatedAt());
        }

        @Test
        @DisplayName("should handle same timestamp for createdAt and updatedAt")
        void shouldHandleSameTimestampForCreatedAtAndUpdatedAt() {
            // Given
            Instant sameTime = Instant.now();

            // When
            adminUserDto.setCreatedAt(sameTime);
            adminUserDto.setUpdatedAt(sameTime);

            // Then
            assertEquals(sameTime, adminUserDto.getCreatedAt());
            assertEquals(sameTime, adminUserDto.getUpdatedAt());
        }
    }

    @Nested
    @DisplayName("Equals and HashCode Tests")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("should be equal when all fields match")
        void shouldBeEqualWhenAllFieldsMatch() {
            // Given
            Instant timestamp = Instant.now();

            AdminUserDto dto1 = new AdminUserDto();
            dto1.setId("same-id");
            dto1.setPhoneNumber("+1234567890");
            dto1.setProfileComplete(true);
            dto1.setRole("DOCTOR");
            dto1.setCreatedAt(timestamp);

            AdminUserDto dto2 = new AdminUserDto();
            dto2.setId("same-id");
            dto2.setPhoneNumber("+1234567890");
            dto2.setProfileComplete(true);
            dto2.setRole("DOCTOR");
            dto2.setCreatedAt(timestamp);

            // Then
            assertEquals(dto1, dto2);
            assertEquals(dto1.hashCode(), dto2.hashCode());
        }

        @Test
        @DisplayName("should not be equal when IDs differ")
        void shouldNotBeEqualWhenIdsDiffer() {
            // Given
            AdminUserDto dto1 = new AdminUserDto();
            dto1.setId("id-1");

            AdminUserDto dto2 = new AdminUserDto();
            dto2.setId("id-2");

            // Then
            assertNotEquals(dto1, dto2);
        }

        @Test
        @DisplayName("should not be equal when profileComplete differs")
        void shouldNotBeEqualWhenProfileCompleteDiffers() {
            // Given
            AdminUserDto dto1 = new AdminUserDto();
            dto1.setId("same-id");
            dto1.setProfileComplete(true);

            AdminUserDto dto2 = new AdminUserDto();
            dto2.setId("same-id");
            dto2.setProfileComplete(false);

            // Then
            assertNotEquals(dto1, dto2);
        }

        @Test
        @DisplayName("should not be equal when role differs")
        void shouldNotBeEqualWhenRoleDiffers() {
            // Given
            AdminUserDto dto1 = new AdminUserDto();
            dto1.setId("same-id");
            dto1.setRole("DOCTOR");

            AdminUserDto dto2 = new AdminUserDto();
            dto2.setId("same-id");
            dto2.setRole("PATIENT");

            // Then
            assertNotEquals(dto1, dto2);
        }

        @Test
        @DisplayName("should not be equal when compared with null")
        void shouldNotBeEqualWhenComparedWithNull() {
            // Given
            AdminUserDto dto = new AdminUserDto();
            dto.setId("test-id");

            // Then
            assertNotEquals(null, dto);
        }

        @Test
        @DisplayName("should not be equal when compared with different class")
        void shouldNotBeEqualWhenComparedWithDifferentClass() {
            // Given
            AdminUserDto dto = new AdminUserDto();
            dto.setId("test-id");

            // Then
            assertNotEquals("string-object", dto);
        }

        @Test
        @DisplayName("should handle null ID in equals")
        void shouldHandleNullIdInEquals() {
            // Given
            AdminUserDto dto1 = new AdminUserDto();
            dto1.setId(null);

            AdminUserDto dto2 = new AdminUserDto();
            dto2.setId("some-id");

            // Then
            assertNotEquals(dto1, dto2);
        }

        @Test
        @DisplayName("should be equal when both IDs are null")
        void shouldBeEqualWhenBothIdsAreNull() {
            // Given
            AdminUserDto dto1 = new AdminUserDto();
            dto1.setId(null);

            AdminUserDto dto2 = new AdminUserDto();
            dto2.setId(null);

            // Then
            assertEquals(dto1, dto2);
            assertEquals(dto1.hashCode(), dto2.hashCode());
        }
    }

    @Nested
    @DisplayName("ToString Tests")
    class ToStringTests {

        @Test
        @DisplayName("should generate non-null toString")
        void shouldGenerateNonNullToString() {
            // Given
            adminUserDto.setId("user-123");
            adminUserDto.setPhoneNumber("+1234567890");
            adminUserDto.setRole("DOCTOR");
            adminUserDto.setProfileComplete(true);
            adminUserDto.setSpeciality("Cardiology");

            // When
            String toStringResult = adminUserDto.toString();

            // Then
            assertNotNull(toStringResult);
            assertTrue(toStringResult.contains("user-123"));
            assertTrue(toStringResult.contains("+1234567890"));
            assertTrue(toStringResult.contains("DOCTOR"));
            assertTrue(toStringResult.contains("Cardiology"));
            assertTrue(toStringResult.contains("profileComplete=true"));
        }

        @Test
        @DisplayName("toString should handle null fields gracefully")
        void toStringShouldHandleNullFields() {
            // Given - adminUserDto with null fields

            // When
            String toStringResult = adminUserDto.toString();

            // Then
            assertNotNull(toStringResult);
            // Should not throw NullPointerException
        }

        @Test
        @DisplayName("toString should include all field names")
        void toStringShouldIncludeAllFieldNames() {
            // When
            String toStringResult = adminUserDto.toString();

            // Then
            assertNotNull(toStringResult);
            assertTrue(toStringResult.contains("id"));
            assertTrue(toStringResult.contains("phoneNumber"));
            assertTrue(toStringResult.contains("profilePhotoUrl"));
            assertTrue(toStringResult.contains("profileComplete"));
            assertTrue(toStringResult.contains("clinicId"));
            assertTrue(toStringResult.contains("speciality"));
            assertTrue(toStringResult.contains("description"));
            assertTrue(toStringResult.contains("diploma"));
            assertTrue(toStringResult.contains("createdAt"));
            assertTrue(toStringResult.contains("updatedAt"));
            assertTrue(toStringResult.contains("role"));
        }
    }

    @Nested
    @DisplayName("Edge Case Tests")
    class EdgeCaseTests {

        @Test
        @DisplayName("should handle very long strings")
        void shouldHandleVeryLongStrings() {
            // Given
            String longString = "x".repeat(1000);
            String longUrl = "https://example.com/" + "a".repeat(500) + ".jpg";

            // When
            adminUserDto.setId(longString);
            adminUserDto.setPhoneNumber(longString);
            adminUserDto.setProfilePhotoUrl(longUrl);
            adminUserDto.setSpeciality(longString);
            adminUserDto.setDescription(longString);
            adminUserDto.setDiploma(longString);
            adminUserDto.setRole(longString);

            // Then
            assertEquals(longString, adminUserDto.getId());
            assertEquals(longString, adminUserDto.getPhoneNumber());
            assertEquals(longUrl, adminUserDto.getProfilePhotoUrl());
            assertEquals(longString, adminUserDto.getSpeciality());
            assertEquals(longString, adminUserDto.getDescription());
            assertEquals(longString, adminUserDto.getDiploma());
            assertEquals(longString, adminUserDto.getRole());
        }

        @Test
        @DisplayName("should handle special characters in strings")
        void shouldHandleSpecialCharactersInStrings() {
            // Given
            String specialString = "user!@#$%^&*()_+-=[]{}|;:,.<>?/name";

            // When
            adminUserDto.setId(specialString);
            adminUserDto.setPhoneNumber(specialString);
            adminUserDto.setSpeciality(specialString);

            // Then
            assertEquals(specialString, adminUserDto.getId());
            assertEquals(specialString, adminUserDto.getPhoneNumber());
            assertEquals(specialString, adminUserDto.getSpeciality());
        }

        @Test
        @DisplayName("should handle maximum Long value for clinicId")
        void shouldHandleMaximumLongValueForClinicId() {
            // Given
            Long maxClinicId = Long.MAX_VALUE;

            // When
            adminUserDto.setClinicId(maxClinicId);

            // Then
            assertEquals(maxClinicId, adminUserDto.getClinicId());
        }

        @Test
        @DisplayName("should handle minimum Long value for clinicId")
        void shouldHandleMinimumLongValueForClinicId() {
            // Given
            Long minClinicId = Long.MIN_VALUE;

            // When
            adminUserDto.setClinicId(minClinicId);

            // Then
            assertEquals(minClinicId, adminUserDto.getClinicId());
        }

        @Test
        @DisplayName("should handle very old timestamps")
        void shouldHandleVeryOldTimestamps() {
            // Given
            Instant oldTimestamp = Instant.parse("1970-01-01T00:00:00Z");

            // When
            adminUserDto.setCreatedAt(oldTimestamp);
            adminUserDto.setUpdatedAt(oldTimestamp);

            // Then
            assertEquals(oldTimestamp, adminUserDto.getCreatedAt());
            assertEquals(oldTimestamp, adminUserDto.getUpdatedAt());
        }

        @Test
        @DisplayName("should handle very future timestamps")
        void shouldHandleVeryFutureTimestamps() {
            // Given
            Instant futureTimestamp = Instant.parse("2100-12-31T23:59:59Z");

            // When
            adminUserDto.setCreatedAt(futureTimestamp);
            adminUserDto.setUpdatedAt(futureTimestamp);

            // Then
            assertEquals(futureTimestamp, adminUserDto.getCreatedAt());
            assertEquals(futureTimestamp, adminUserDto.getUpdatedAt());
        }
    }

    @Nested
    @DisplayName("Consistency Tests")
    class ConsistencyTests {

        @Test
        @DisplayName("should maintain consistency between equals and hashCode")
        void shouldMaintainConsistencyBetweenEqualsAndHashCode() {
            // Given
            AdminUserDto dto1 = new AdminUserDto();
            dto1.setId("consistent-id");
            dto1.setRole("DOCTOR");

            AdminUserDto dto2 = new AdminUserDto();
            dto2.setId("consistent-id");
            dto2.setRole("DOCTOR");

            // Then
            assertEquals(dto1, dto2);
            assertEquals(dto1.hashCode(), dto2.hashCode());

            // When changing one object
            dto2.setRole("PATIENT");

            // Then they should no longer be equal
            assertNotEquals(dto1, dto2);
            assertNotEquals(dto1.hashCode(), dto2.hashCode());
        }

        @Test
        @DisplayName("should be reflexive in equals")
        void shouldBeReflexiveInEquals() {
            // Given
            AdminUserDto dto = new AdminUserDto();
            dto.setId("reflexive-id");

            // Then
            assertEquals(dto, dto);
        }

        @Test
        @DisplayName("should be symmetric in equals")
        void shouldBeSymmetricInEquals() {
            // Given
            AdminUserDto dto1 = new AdminUserDto();
            dto1.setId("symmetric-id");

            AdminUserDto dto2 = new AdminUserDto();
            dto2.setId("symmetric-id");

            // Then
            assertEquals(dto1, dto2);
            assertEquals(dto2, dto1);
        }

        @Test
        @DisplayName("should be transitive in equals")
        void shouldBeTransitiveInEquals() {
            // Given
            AdminUserDto dto1 = new AdminUserDto();
            dto1.setId("transitive-id");

            AdminUserDto dto2 = new AdminUserDto();
            dto2.setId("transitive-id");

            AdminUserDto dto3 = new AdminUserDto();
            dto3.setId("transitive-id");

            // Then
            assertEquals(dto1, dto2);
            assertEquals(dto2, dto3);
            assertEquals(dto1, dto3);
        }
    }
}