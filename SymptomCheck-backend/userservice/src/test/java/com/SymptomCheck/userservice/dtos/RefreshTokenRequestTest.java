package com.SymptomCheck.userservice.dtos;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RefreshTokenRequestTest {

    private RefreshTokenRequest refreshTokenRequest;

    @BeforeEach
    void setUp() {
        refreshTokenRequest = new RefreshTokenRequest();
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("should set and get refreshToken correctly")
        void shouldSetAndGetRefreshToken() {
            // Given
            String refreshToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...";

            // When
            refreshTokenRequest.setRefreshToken(refreshToken);

            // Then
            assertEquals(refreshToken, refreshTokenRequest.getRefreshToken());
        }

        @Test
        @DisplayName("should handle empty string refreshToken")
        void shouldHandleEmptyStringRefreshToken() {
            // Given
            String emptyToken = "";

            // When
            refreshTokenRequest.setRefreshToken(emptyToken);

            // Then
            assertEquals(emptyToken, refreshTokenRequest.getRefreshToken());
        }

        @Test
        @DisplayName("should handle null refreshToken")
        void shouldHandleNullRefreshToken() {
            // When
            refreshTokenRequest.setRefreshToken(null);

            // Then
            assertNull(refreshTokenRequest.getRefreshToken());
        }
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("should create object with all-args constructor")
        void shouldCreateObjectWithAllArgsConstructor() {
            // Given
            String refreshToken = "valid-refresh-token-123";

            // When
            RefreshTokenRequest newRequest = new RefreshTokenRequest(refreshToken);

            // Then
            assertEquals(refreshToken, newRequest.getRefreshToken());
        }

        @Test
        @DisplayName("should create object with empty token using all-args constructor")
        void shouldCreateObjectWithEmptyToken() {
            // Given
            String emptyToken = "";

            // When
            RefreshTokenRequest newRequest = new RefreshTokenRequest(emptyToken);

            // Then
            assertEquals(emptyToken, newRequest.getRefreshToken());
        }

        @Test
        @DisplayName("should create object with null token using all-args constructor")
        void shouldCreateObjectWithNullToken() {
            // When
            RefreshTokenRequest newRequest = new RefreshTokenRequest(null);

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
            RefreshTokenRequest request1 = new RefreshTokenRequest();
            request1.setRefreshToken("token-1");

            RefreshTokenRequest request2 = new RefreshTokenRequest();
            request2.setRefreshToken("token-2");

            // Then
            assertNotEquals(request1, request2);
        }

        @Test
        @DisplayName("should not be equal when compared with null")
        void shouldNotBeEqualWhenComparedWithNull() {
            // Given
            RefreshTokenRequest request = new RefreshTokenRequest();
            request.setRefreshToken("test-token");

            // Then
            assertNotEquals(null, request);
        }

        @Test
        @DisplayName("should not be equal when compared with different class")
        void shouldNotBeEqualWhenComparedWithDifferentClass() {
            // Given
            RefreshTokenRequest request = new RefreshTokenRequest();
            request.setRefreshToken("test-token");

            // Then
            assertNotEquals("string-object", request);
        }

        @Test
        @DisplayName("should handle null refreshToken in equals")
        void shouldHandleNullRefreshTokenInEquals() {
            // Given
            RefreshTokenRequest request1 = new RefreshTokenRequest();
            request1.setRefreshToken(null);

            RefreshTokenRequest request2 = new RefreshTokenRequest();
            request2.setRefreshToken("some-token");

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
            // Given - refreshTokenRequest with null token

            // When
            String toStringResult = refreshTokenRequest.toString();

            // Then
            assertNotNull(toStringResult);
            // Should not throw NullPointerException
        }

        @Test
        @DisplayName("toString should handle empty refreshToken gracefully")
        void toStringShouldHandleEmptyRefreshToken() {
            // Given
            refreshTokenRequest.setRefreshToken("");

            // When
            String toStringResult = refreshTokenRequest.toString();

            // Then
            assertNotNull(toStringResult);
            // Should not throw any exception
        }
    }

    @Nested
    @DisplayName("Edge Case Tests")
    class EdgeCaseTests {

        @Test
        @DisplayName("should handle very long refresh token")
        void shouldHandleVeryLongRefreshToken() {
            // Given
            String longToken = "a".repeat(1000);

            // When
            refreshTokenRequest.setRefreshToken(longToken);

            // Then
            assertEquals(longToken, refreshTokenRequest.getRefreshToken());
        }

        @Test
        @DisplayName("should handle refresh token with special characters")
        void shouldHandleRefreshTokenWithSpecialCharacters() {
            // Given
            String specialToken = "token!@#$%^&*()_+-=[]{}|;:,.<>?";

            // When
            refreshTokenRequest.setRefreshToken(specialToken);

            // Then
            assertEquals(specialToken, refreshTokenRequest.getRefreshToken());
        }
    }
}