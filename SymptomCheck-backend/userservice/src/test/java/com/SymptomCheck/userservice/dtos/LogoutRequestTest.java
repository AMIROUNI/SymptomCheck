package com.SymptomCheck.userservice.dtos;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LogoutRequestTest {

    private LogoutRequest logoutRequest;

    @BeforeEach
    void setUp() {
        logoutRequest = new LogoutRequest();
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("should set and get refreshToken correctly")
        void shouldSetAndGetRefreshToken() {
            // Given
            String refreshToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.logout.token";

            // When
            logoutRequest.setRefreshToken(refreshToken);

            // Then
            assertEquals(refreshToken, logoutRequest.getRefreshToken());
        }

        @Test
        @DisplayName("should handle empty string refreshToken")
        void shouldHandleEmptyStringRefreshToken() {
            // Given
            String emptyToken = "";

            // When
            logoutRequest.setRefreshToken(emptyToken);

            // Then
            assertEquals(emptyToken, logoutRequest.getRefreshToken());
        }

        @Test
        @DisplayName("should handle null refreshToken")
        void shouldHandleNullRefreshToken() {
            // When
            logoutRequest.setRefreshToken(null);

            // Then
            assertNull(logoutRequest.getRefreshToken());
        }
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("should create object with all-args constructor")
        void shouldCreateObjectWithAllArgsConstructor() {
            // Given
            String refreshToken = "logout-refresh-token-123";

            // When
            LogoutRequest newRequest = new LogoutRequest(refreshToken);

            // Then
            assertEquals(refreshToken, newRequest.getRefreshToken());
        }

        @Test
        @DisplayName("should create object with empty token using all-args constructor")
        void shouldCreateObjectWithEmptyToken() {
            // Given
            String emptyToken = "";

            // When
            LogoutRequest newRequest = new LogoutRequest(emptyToken);

            // Then
            assertEquals(emptyToken, newRequest.getRefreshToken());
        }

        @Test
        @DisplayName("should create object with null token using all-args constructor")
        void shouldCreateObjectWithNullToken() {
            // When
            LogoutRequest newRequest = new LogoutRequest(null);

            // Then
            assertNull(newRequest.getRefreshToken());
        }
    }

    @Nested
    @DisplayName("Equals and HashCode Tests")
    class EqualsHashCodeTests {


        @Test
        @DisplayName("should not be equal when refreshTokens differ")
        void shouldNotBeEqualWhenRefreshTokensDiffer() {
            // Given
            LogoutRequest request1 = new LogoutRequest();
            request1.setRefreshToken("logout-token-1");

            LogoutRequest request2 = new LogoutRequest();
            request2.setRefreshToken("logout-token-2");

            // Then
            assertNotEquals(request1, request2);
        }

        @Test
        @DisplayName("should not be equal when compared with null")
        void shouldNotBeEqualWhenComparedWithNull() {
            // Given
            LogoutRequest request = new LogoutRequest();
            request.setRefreshToken("test-logout-token");

            // Then
            assertNotEquals(null, request);
        }

        @Test
        @DisplayName("should not be equal when compared with different class")
        void shouldNotBeEqualWhenComparedWithDifferentClass() {
            // Given
            LogoutRequest request = new LogoutRequest();
            request.setRefreshToken("test-logout-token");

            // Then
            assertNotEquals("string-object", request);
        }

        @Test
        @DisplayName("should handle null refreshToken in equals")
        void shouldHandleNullRefreshTokenInEquals() {
            // Given
            LogoutRequest request1 = new LogoutRequest();
            request1.setRefreshToken(null);

            LogoutRequest request2 = new LogoutRequest();
            request2.setRefreshToken("some-logout-token");

            // Then
            assertNotEquals(request1, request2);
        }


    }

    @Nested
    @DisplayName("ToString Tests")
    class ToStringTests {


        @Test
        @DisplayName("toString should handle null refreshToken gracefully")
        void toStringShouldHandleNullRefreshToken() {
            // Given - logoutRequest with null token

            // When
            String toStringResult = logoutRequest.toString();

            // Then
            assertNotNull(toStringResult);
            // Should not throw NullPointerException
        }

        @Test
        @DisplayName("toString should handle empty refreshToken gracefully")
        void toStringShouldHandleEmptyRefreshToken() {
            // Given
            logoutRequest.setRefreshToken("");

            // When
            String toStringResult = logoutRequest.toString();

            // Then
            assertNotNull(toStringResult);
            // Should not throw any exception
        }
    }


    @Nested
    @DisplayName("Edge Case Tests")
    class EdgeCaseTests {

        @Test
        @DisplayName("should handle very long logout token")
        void shouldHandleVeryLongLogoutToken() {
            // Given
            String longToken = "x".repeat(2000);

            // When
            logoutRequest.setRefreshToken(longToken);

            // Then
            assertEquals(longToken, logoutRequest.getRefreshToken());
        }

        @Test
        @DisplayName("should handle logout token with special characters")
        void shouldHandleLogoutTokenWithSpecialCharacters() {
            // Given
            String specialToken = "logout!@#$%^&*()_+-=[]{}|;:,.<>?/token";

            // When
            logoutRequest.setRefreshToken(specialToken);

            // Then
            assertEquals(specialToken, logoutRequest.getRefreshToken());
        }

        @Test
        @DisplayName("should handle JWT format tokens")
        void shouldHandleJWTFormatTokens() {
            // Given
            String jwtToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";

            // When
            logoutRequest.setRefreshToken(jwtToken);

            // Then
            assertEquals(jwtToken, logoutRequest.getRefreshToken());
        }
    }

    @Nested
    @DisplayName("Consistency Tests")
    class ConsistencyTests {

        @Test
        @DisplayName("should be reflexive in equals")
        void shouldBeReflexiveInEquals() {
            // Given
            LogoutRequest request = new LogoutRequest("reflexive-token");

            // Then
            assertEquals(request, request);
        }

    }
}