package com.SymptomCheck.userservice.unit.dtos;

import com.SymptomCheck.userservice.dtos.LoginRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoginRequestTest {

    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest();
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("should set and get username and password correctly")
        void shouldSetAndGetUsernameAndPassword() {
            // Given
            String username = "john_doe";
            String password = "securePassword123";

            // When
            loginRequest.setUsername(username);
            loginRequest.setPassword(password);

            // Then
            assertEquals(username, loginRequest.getUsername());
            assertEquals(password, loginRequest.getPassword());
        }

        @Test
        @DisplayName("should handle empty string username and password")
        void shouldHandleEmptyStringUsernameAndPassword() {
            // Given
            String emptyUsername = "";
            String emptyPassword = "";

            // When
            loginRequest.setUsername(emptyUsername);
            loginRequest.setPassword(emptyPassword);

            // Then
            assertEquals(emptyUsername, loginRequest.getUsername());
            assertEquals(emptyPassword, loginRequest.getPassword());
        }

        @Test
        @DisplayName("should handle null username and password")
        void shouldHandleNullUsernameAndPassword() {
            // When
            loginRequest.setUsername(null);
            loginRequest.setPassword(null);

            // Then
            assertNull(loginRequest.getUsername());
            assertNull(loginRequest.getPassword());
        }

        @Test
        @DisplayName("should handle whitespace in username and password")
        void shouldHandleWhitespaceInUsernameAndPassword() {
            // Given
            String usernameWithSpaces = "  john_doe  ";
            String passwordWithSpaces = "  password  ";

            // When
            loginRequest.setUsername(usernameWithSpaces);
            loginRequest.setPassword(passwordWithSpaces);

            // Then
            assertEquals(usernameWithSpaces, loginRequest.getUsername());
            assertEquals(passwordWithSpaces, loginRequest.getPassword());
        }
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("should create object with all-args constructor")
        void shouldCreateObjectWithAllArgsConstructor() {
            // Given
            String username = "jane_smith";
            String password = "anotherPassword";

            // When
            LoginRequest newRequest = new LoginRequest(username, password);

            // Then
            assertEquals(username, newRequest.getUsername());
            assertEquals(password, newRequest.getPassword());
        }

        @Test
        @DisplayName("should create object with empty values using all-args constructor")
        void shouldCreateObjectWithEmptyValues() {
            // Given
            String emptyUsername = "";
            String emptyPassword = "";

            // When
            LoginRequest newRequest = new LoginRequest(emptyUsername, emptyPassword);

            // Then
            assertEquals(emptyUsername, newRequest.getUsername());
            assertEquals(emptyPassword, newRequest.getPassword());
        }

        @Test
        @DisplayName("should create object with null values using all-args constructor")
        void shouldCreateObjectWithNullValues() {
            // When
            LoginRequest newRequest = new LoginRequest(null, null);

            // Then
            assertNull(newRequest.getUsername());
            assertNull(newRequest.getPassword());
        }

        @Test
        @DisplayName("should create object with mixed null values using all-args constructor")
        void shouldCreateObjectWithMixedNullValues() {
            // When
            LoginRequest newRequest = new LoginRequest("usernameOnly", null);
            LoginRequest newRequest2 = new LoginRequest(null, "passwordOnly");

            // Then
            assertEquals("usernameOnly", newRequest.getUsername());
            assertNull(newRequest.getPassword());
            assertNull(newRequest2.getUsername());
            assertEquals("passwordOnly", newRequest2.getPassword());
        }
    }

    @Nested
    @DisplayName("Equals and HashCode Tests")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("should not be equal when usernames differ")
        void shouldNotBeEqualWhenUsernamesDiffer() {
            // Given
            LoginRequest request1 = new LoginRequest();
            request1.setUsername("user1");
            request1.setPassword("same_password");

            LoginRequest request2 = new LoginRequest();
            request2.setUsername("user2");
            request2.setPassword("same_password");

            // Then
            assertNotEquals(request1, request2);
        }

        @Test
        @DisplayName("should not be equal when passwords differ")
        void shouldNotBeEqualWhenPasswordsDiffer() {
            // Given
            LoginRequest request1 = new LoginRequest();
            request1.setUsername("same_user");
            request1.setPassword("password1");

            LoginRequest request2 = new LoginRequest();
            request2.setUsername("same_user");
            request2.setPassword("password2");

            // Then
            assertNotEquals(request1, request2);
        }

        @Test
        @DisplayName("should not be equal when compared with null")
        void shouldNotBeEqualWhenComparedWithNull() {
            // Given
            LoginRequest request = new LoginRequest();
            request.setUsername("test_user");
            request.setPassword("test_password");

            // Then
            assertNotEquals(null, request);
        }

        @Test
        @DisplayName("should not be equal when compared with different class")
        void shouldNotBeEqualWhenComparedWithDifferentClass() {
            // Given
            LoginRequest request = new LoginRequest();
            request.setUsername("test_user");
            request.setPassword("test_password");

            // Then
            assertNotEquals("string-object", request);
        }

        @Test
        @DisplayName("should handle null username in equals")
        void shouldHandleNullUsernameInEquals() {
            // Given
            LoginRequest request1 = new LoginRequest();
            request1.setUsername(null);
            request1.setPassword("password");

            LoginRequest request2 = new LoginRequest();
            request2.setUsername("some_user");
            request2.setPassword("password");

            // Then
            assertNotEquals(request1, request2);
        }

        @Test
        @DisplayName("should handle null password in equals")
        void shouldHandleNullPasswordInEquals() {
            // Given
            LoginRequest request1 = new LoginRequest();
            request1.setUsername("user");
            request1.setPassword(null);

            LoginRequest request2 = new LoginRequest();
            request2.setUsername("user");
            request2.setPassword("some_password");

            // Then
            assertNotEquals(request1, request2);
        }


    }

    @Nested
    @DisplayName("ToString Tests")
    class ToStringTests {

        @Test
        @DisplayName("toString should handle null username and password gracefully")
        void toStringShouldHandleNullUsernameAndPassword() {
            // Given - loginRequest with null fields

            // When
            String toStringResult = loginRequest.toString();

            // Then
            assertNotNull(toStringResult);
            // Should not throw NullPointerException
        }

        @Test
        @DisplayName("toString should handle empty username and password gracefully")
        void toStringShouldHandleEmptyUsernameAndPassword() {
            // Given
            loginRequest.setUsername("");
            loginRequest.setPassword("");

            // When
            String toStringResult = loginRequest.toString();

            // Then
            assertNotNull(toStringResult);
            // Should not throw any exception
        }

    }

    @Nested
    @DisplayName("Edge Case Tests")
    class EdgeCaseTests {

        @Test
        @DisplayName("should handle very long username and password")
        void shouldHandleVeryLongUsernameAndPassword() {
            // Given
            String longUsername = "u".repeat(1000);
            String longPassword = "p".repeat(1000);

            // When
            loginRequest.setUsername(longUsername);
            loginRequest.setPassword(longPassword);

            // Then
            assertEquals(longUsername, loginRequest.getUsername());
            assertEquals(longPassword, loginRequest.getPassword());
        }

        @Test
        @DisplayName("should handle username and password with special characters")
        void shouldHandleUsernameAndPasswordWithSpecialCharacters() {
            // Given
            String specialUsername = "user!@#$%^&*()_+-=name";
            String specialPassword = "pass!@#$%^&*()_+-=word";

            // When
            loginRequest.setUsername(specialUsername);
            loginRequest.setPassword(specialPassword);

            // Then
            assertEquals(specialUsername, loginRequest.getUsername());
            assertEquals(specialPassword, loginRequest.getPassword());
        }

        @Test
        @DisplayName("should handle email as username")
        void shouldHandleEmailAsUsername() {
            // Given
            String emailUsername = "user@example.com";
            String password = "emailUserPassword";

            // When
            loginRequest.setUsername(emailUsername);
            loginRequest.setPassword(password);

            // Then
            assertEquals(emailUsername, loginRequest.getUsername());
            assertEquals(password, loginRequest.getPassword());
        }

        @Test
        @DisplayName("should handle numeric username")
        void shouldHandleNumericUsername() {
            // Given
            String numericUsername = "123456789";
            String password = "numericUserPass";

            // When
            loginRequest.setUsername(numericUsername);
            loginRequest.setPassword(password);

            // Then
            assertEquals(numericUsername, loginRequest.getUsername());
            assertEquals(password, loginRequest.getPassword());
        }
    }

    @Nested
    @DisplayName("Consistency Tests")
    class ConsistencyTests {

        @Test
        @DisplayName("should be reflexive in equals")
        void shouldBeReflexiveInEquals() {
            // Given
            LoginRequest request = new LoginRequest("reflexive_user", "reflexive_pass");

            // Then
            assertEquals(request, request);
        }



    }
}