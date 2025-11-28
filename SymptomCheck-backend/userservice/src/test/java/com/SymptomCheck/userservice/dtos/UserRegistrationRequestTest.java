package com.SymptomCheck.userservice.dtos;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserRegistrationRequestTest {

    private UserRegistrationRequest request;

    @BeforeEach
    void setUp() {
        request = new UserRegistrationRequest();
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("should set and get all fields correctly")
        void shouldSetAndGetAllFields() {
            // Given
            String id = "user-123";
            String username = "john_doe";
            String password = "securePassword123";
            String email = "john.doe@example.com";
            String firstName = "John";
            String lastName = "Doe";
            String phoneNumber = "+1234567890";
            String profilePhotoUrl = "https://example.com/photo.jpg";
            String role = "DOCTOR";
            String speciality = "Cardiology";
            String description = "Experienced cardiologist";
            String diploma = "MD, PhD";
            Long clinicId = 1L;
            boolean enabled = true;
            Boolean profileComplete = true;

            // When
            request.setId(id);
            request.setUsername(username);
            request.setPassword(password);
            request.setEmail(email);
            request.setFirstName(firstName);
            request.setLastName(lastName);
            request.setPhoneNumber(phoneNumber);
            request.setProfilePhotoUrl(profilePhotoUrl);
            request.setRole(role);
            request.setSpeciality(speciality);
            request.setDescription(description);
            request.setDiploma(diploma);
            request.setClinicId(clinicId);
            request.setEnabled(enabled);
            request.setProfileComplete(profileComplete);

            // Then
            assertEquals(id, request.getId());
            assertEquals(username, request.getUsername());
            assertEquals(password, request.getPassword());
            assertEquals(email, request.getEmail());
            assertEquals(firstName, request.getFirstName());
            assertEquals(lastName, request.getLastName());
            assertEquals(phoneNumber, request.getPhoneNumber());
            assertEquals(profilePhotoUrl, request.getProfilePhotoUrl());
            assertEquals(role, request.getRole());
            assertEquals(speciality, request.getSpeciality());
            assertEquals(description, request.getDescription());
            assertEquals(diploma, request.getDiploma());
            assertEquals(clinicId, request.getClinicId());
            assertEquals(enabled, request.isEnabled());
            assertEquals(profileComplete, request.getProfileComplete());
        }
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("should create object with all-args constructor")
        void shouldCreateObjectWithAllArgsConstructor() {
            // Given
            String id = "user-456";
            String username = "jane_smith";
            String password = "anotherPassword";
            String email = "jane.smith@example.com";
            String firstName = "Jane";
            String lastName = "Smith";
            String phoneNumber = "+9876543210";
            String profilePhotoUrl = "https://example.com/jane.jpg";
            String role = "PATIENT";
            String speciality = null;
            String description = null;
            String diploma = null;
            Long clinicId = null;
            boolean enabled = false;
            Boolean profileComplete = false;

            // When
            UserRegistrationRequest newRequest = new UserRegistrationRequest(
                    id, username, password, email, firstName, lastName, phoneNumber,
                    profilePhotoUrl, role, speciality, description, diploma, clinicId,
                    enabled, profileComplete
            );

            // Then
            assertEquals(id, newRequest.getId());
            assertEquals(username, newRequest.getUsername());
            assertEquals(password, newRequest.getPassword());
            assertEquals(email, newRequest.getEmail());
            assertEquals(firstName, newRequest.getFirstName());
            assertEquals(lastName, newRequest.getLastName());
            assertEquals(phoneNumber, newRequest.getPhoneNumber());
            assertEquals(profilePhotoUrl, newRequest.getProfilePhotoUrl());
            assertEquals(role, newRequest.getRole());
            assertNull(newRequest.getSpeciality());
            assertNull(newRequest.getDescription());
            assertNull(newRequest.getDiploma());
            assertNull(newRequest.getClinicId());
            assertFalse(newRequest.isEnabled());
            assertFalse(newRequest.getProfileComplete());
        }
    }

    @Nested
    @DisplayName("Default Value Tests")
    class DefaultValueTests {

        @Test
        @DisplayName("should have false as default enabled")
        void shouldHaveFalseAsDefaultEnabled() {
            // When creating new instance with no-args constructor
            UserRegistrationRequest newRequest = new UserRegistrationRequest();

            // Then
            assertFalse(newRequest.isEnabled());
        }

        @Test
        @DisplayName("should have false as default profileComplete")
        void shouldHaveFalseAsDefaultProfileComplete() {
            // When creating new instance with no-args constructor
            UserRegistrationRequest newRequest = new UserRegistrationRequest();

            // Then
            assertFalse(newRequest.getProfileComplete());
        }

        @Test
        @DisplayName("should allow null values for optional fields")
        void shouldAllowNullValuesForOptionalFields() {
            // When setting optional fields to null
            request.setFirstName(null);
            request.setLastName(null);
            request.setPhoneNumber(null);
            request.setProfilePhotoUrl(null);
            request.setSpeciality(null);
            request.setDescription(null);
            request.setDiploma(null);
            request.setClinicId(null);

            // Then
            assertNull(request.getFirstName());
            assertNull(request.getLastName());
            assertNull(request.getPhoneNumber());
            assertNull(request.getProfilePhotoUrl());
            assertNull(request.getSpeciality());
            assertNull(request.getDescription());
            assertNull(request.getDiploma());
            assertNull(request.getClinicId());
        }
    }

    @Nested
    @DisplayName("Equals and HashCode Tests")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("should not be equal when IDs differ")
        void shouldNotBeEqualWhenIdsDiffer() {
            // Given
            UserRegistrationRequest request1 = new UserRegistrationRequest();
            request1.setId("id-1");

            UserRegistrationRequest request2 = new UserRegistrationRequest();
            request2.setId("id-2");

            // Then
            assertNotEquals(request1, request2);
        }

        @Test
        @DisplayName("should handle null ID in equals")
        void shouldHandleNullIdInEquals() {
            // Given
            UserRegistrationRequest request1 = new UserRegistrationRequest();
            request1.setId(null);

            UserRegistrationRequest request2 = new UserRegistrationRequest();
            request2.setId("some-id");

            // Then
            assertNotEquals(request1, request2);
        }
    }

    @Nested
    @DisplayName("ToString Tests")
    class ToStringTests {

        @Test
        @DisplayName("toString should handle null fields gracefully")
        void toStringShouldHandleNullFields() {
            // Given - request with default null fields

            // When
            String toStringResult = request.toString();

            // Then
            assertNotNull(toStringResult);
            // Should not throw NullPointerException
        }
    }

    @Nested
    @DisplayName("Boolean Field Tests")
    class BooleanFieldTests {

        @Test
        @DisplayName("should handle primitive boolean enabled field")
        void shouldHandlePrimitiveBooleanEnabledField() {
            // When
            request.setEnabled(true);

            // Then
            assertTrue(request.isEnabled());

            // When
            request.setEnabled(false);

            // Then
            assertFalse(request.isEnabled());
        }

        @Test
        @DisplayName("should handle Boolean wrapper profileComplete field")
        void shouldHandleBooleanWrapperProfileCompleteField() {
            // When
            request.setProfileComplete(true);

            // Then
            assertTrue(request.getProfileComplete());

            // When
            request.setProfileComplete(false);

            // Then
            assertFalse(request.getProfileComplete());

            // When
            request.setProfileComplete(null);

            // Then
            assertNull(request.getProfileComplete());
        }
    }
}