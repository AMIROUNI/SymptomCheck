package com.SymptomCheck.userservice.unit.dtos.adminDashboardDto;

import com.SymptomCheck.userservice.dtos.admindashboarddto.UserStatsDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class UserStatsDtoTest {

    private UserStatsDto userStatsDto;

    @BeforeEach
    void setUp() {
        userStatsDto = new UserStatsDto();
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("should set and get all fields correctly")
        void shouldSetAndGetAllFields() {
            // Given
            Long totalUsers = 1000L;
            Long totalDoctors = 50L;
            Long totalPatients = 950L;
            Long completedProfiles = 800L;
            Long incompleteProfiles = 200L;
            Long newUsersThisWeek = 25L;
            LocalDateTime lastUpdated = LocalDateTime.of(2025, 11, 25, 14, 30, 0);

            // When
            userStatsDto.setTotalUsers(totalUsers);
            userStatsDto.setTotalDoctors(totalDoctors);
            userStatsDto.setTotalPatients(totalPatients);
            userStatsDto.setCompletedProfiles(completedProfiles);
            userStatsDto.setIncompleteProfiles(incompleteProfiles);
            userStatsDto.setNewUsersThisWeek(newUsersThisWeek);
            userStatsDto.setLastUpdated(lastUpdated);

            // Then
            assertEquals(totalUsers, userStatsDto.getTotalUsers());
            assertEquals(totalDoctors, userStatsDto.getTotalDoctors());
            assertEquals(totalPatients, userStatsDto.getTotalPatients());
            assertEquals(completedProfiles, userStatsDto.getCompletedProfiles());
            assertEquals(incompleteProfiles, userStatsDto.getIncompleteProfiles());
            assertEquals(newUsersThisWeek, userStatsDto.getNewUsersThisWeek());
            assertEquals(lastUpdated, userStatsDto.getLastUpdated());
        }

        @Test
        @DisplayName("should handle zero values")
        void shouldHandleZeroValues() {
            // Given
            Long zero = 0L;
            LocalDateTime now = LocalDateTime.now();

            // When
            userStatsDto.setTotalUsers(zero);
            userStatsDto.setTotalDoctors(zero);
            userStatsDto.setTotalPatients(zero);
            userStatsDto.setCompletedProfiles(zero);
            userStatsDto.setIncompleteProfiles(zero);
            userStatsDto.setNewUsersThisWeek(zero);
            userStatsDto.setLastUpdated(now);

            // Then
            assertEquals(zero, userStatsDto.getTotalUsers());
            assertEquals(zero, userStatsDto.getTotalDoctors());
            assertEquals(zero, userStatsDto.getTotalPatients());
            assertEquals(zero, userStatsDto.getCompletedProfiles());
            assertEquals(zero, userStatsDto.getIncompleteProfiles());
            assertEquals(zero, userStatsDto.getNewUsersThisWeek());
            assertEquals(now, userStatsDto.getLastUpdated());
        }

        @Test
        @DisplayName("should handle null values")
        void shouldHandleNullValues() {
            // When
            userStatsDto.setTotalUsers(null);
            userStatsDto.setTotalDoctors(null);
            userStatsDto.setTotalPatients(null);
            userStatsDto.setCompletedProfiles(null);
            userStatsDto.setIncompleteProfiles(null);
            userStatsDto.setNewUsersThisWeek(null);
            userStatsDto.setLastUpdated(null);

            // Then
            assertNull(userStatsDto.getTotalUsers());
            assertNull(userStatsDto.getTotalDoctors());
            assertNull(userStatsDto.getTotalPatients());
            assertNull(userStatsDto.getCompletedProfiles());
            assertNull(userStatsDto.getIncompleteProfiles());
            assertNull(userStatsDto.getNewUsersThisWeek());
            assertNull(userStatsDto.getLastUpdated());
        }

        @Test
        @DisplayName("should handle large number values")
        void shouldHandleLargeNumberValues() {
            // Given
            Long largeNumber = 1_000_000L;
            LocalDateTime futureDate = LocalDateTime.of(2030, 1, 1, 0, 0);

            // When
            userStatsDto.setTotalUsers(largeNumber);
            userStatsDto.setTotalDoctors(largeNumber);
            userStatsDto.setTotalPatients(largeNumber);
            userStatsDto.setCompletedProfiles(largeNumber);
            userStatsDto.setIncompleteProfiles(largeNumber);
            userStatsDto.setNewUsersThisWeek(largeNumber);
            userStatsDto.setLastUpdated(futureDate);

            // Then
            assertEquals(largeNumber, userStatsDto.getTotalUsers());
            assertEquals(largeNumber, userStatsDto.getTotalDoctors());
            assertEquals(largeNumber, userStatsDto.getTotalPatients());
            assertEquals(largeNumber, userStatsDto.getCompletedProfiles());
            assertEquals(largeNumber, userStatsDto.getIncompleteProfiles());
            assertEquals(largeNumber, userStatsDto.getNewUsersThisWeek());
            assertEquals(futureDate, userStatsDto.getLastUpdated());
        }
    }

    @Nested
    @DisplayName("Data Consistency Tests")
    class DataConsistencyTests {

        @Test
        @DisplayName("should allow totalUsers to be sum of doctors and patients")
        void shouldAllowTotalUsersToBeSumOfDoctorsAndPatients() {
            // Given
            Long totalDoctors = 30L;
            Long totalPatients = 70L;
            Long totalUsers = 100L; // 30 + 70

            // When
            userStatsDto.setTotalDoctors(totalDoctors);
            userStatsDto.setTotalPatients(totalPatients);
            userStatsDto.setTotalUsers(totalUsers);

            // Then
            assertEquals(totalDoctors, userStatsDto.getTotalDoctors());
            assertEquals(totalPatients, userStatsDto.getTotalPatients());
            assertEquals(totalUsers, userStatsDto.getTotalUsers());
        }

        @Test
        @DisplayName("should allow completed and incomplete profiles to equal total users")
        void shouldAllowCompletedAndIncompleteProfilesToEqualTotalUsers() {
            // Given
            Long completedProfiles = 80L;
            Long incompleteProfiles = 20L;
            Long totalUsers = 100L; // 80 + 20

            // When
            userStatsDto.setCompletedProfiles(completedProfiles);
            userStatsDto.setIncompleteProfiles(incompleteProfiles);
            userStatsDto.setTotalUsers(totalUsers);

            // Then
            assertEquals(completedProfiles, userStatsDto.getCompletedProfiles());
            assertEquals(incompleteProfiles, userStatsDto.getIncompleteProfiles());
            assertEquals(totalUsers, userStatsDto.getTotalUsers());
        }

        @Test
        @DisplayName("should handle new users this week as subset of total users")
        void shouldHandleNewUsersThisWeekAsSubsetOfTotalUsers() {
            // Given
            Long totalUsers = 1000L;
            Long newUsersThisWeek = 25L; // Should be <= totalUsers

            // When
            userStatsDto.setTotalUsers(totalUsers);
            userStatsDto.setNewUsersThisWeek(newUsersThisWeek);

            // Then
            assertEquals(totalUsers, userStatsDto.getTotalUsers());
            assertEquals(newUsersThisWeek, userStatsDto.getNewUsersThisWeek());
        }
    }

    @Nested
    @DisplayName("Equals and HashCode Tests")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("should be equal when all fields match")
        void shouldBeEqualWhenAllFieldsMatch() {
            // Given
            LocalDateTime timestamp = LocalDateTime.now();

            UserStatsDto stats1 = new UserStatsDto();
            stats1.setTotalUsers(100L);
            stats1.setTotalDoctors(10L);
            stats1.setTotalPatients(90L);
            stats1.setCompletedProfiles(80L);
            stats1.setIncompleteProfiles(20L);
            stats1.setNewUsersThisWeek(5L);
            stats1.setLastUpdated(timestamp);

            UserStatsDto stats2 = new UserStatsDto();
            stats2.setTotalUsers(100L);
            stats2.setTotalDoctors(10L);
            stats2.setTotalPatients(90L);
            stats2.setCompletedProfiles(80L);
            stats2.setIncompleteProfiles(20L);
            stats2.setNewUsersThisWeek(5L);
            stats2.setLastUpdated(timestamp);

            // Then
            assertEquals(stats1, stats2);
            assertEquals(stats1.hashCode(), stats2.hashCode());
        }

        @Test
        @DisplayName("should not be equal when any field differs")
        void shouldNotBeEqualWhenAnyFieldDiffers() {
            // Given
            LocalDateTime timestamp = LocalDateTime.now();

            UserStatsDto stats1 = new UserStatsDto();
            stats1.setTotalUsers(100L);
            stats1.setTotalDoctors(10L);
            stats1.setLastUpdated(timestamp);

            UserStatsDto stats2 = new UserStatsDto();
            stats2.setTotalUsers(100L);
            stats2.setTotalDoctors(11L); // Different
            stats2.setLastUpdated(timestamp);

            // Then
            assertNotEquals(stats1, stats2);
        }

        @Test
        @DisplayName("should not be equal when compared with null")
        void shouldNotBeEqualWhenComparedWithNull() {
            // Given
            UserStatsDto stats = new UserStatsDto();
            stats.setTotalUsers(100L);

            // Then
            assertNotEquals(null, stats);
        }

        @Test
        @DisplayName("should not be equal when compared with different class")
        void shouldNotBeEqualWhenComparedWithDifferentClass() {
            // Given
            UserStatsDto stats = new UserStatsDto();
            stats.setTotalUsers(100L);

            // Then
            assertNotEquals("string-object", stats);
        }

        @Test
        @DisplayName("should handle null fields in equals")
        void shouldHandleNullFieldsInEquals() {
            // Given
            UserStatsDto stats1 = new UserStatsDto();
            stats1.setTotalUsers(null);
            stats1.setTotalDoctors(10L);

            UserStatsDto stats2 = new UserStatsDto();
            stats2.setTotalUsers(100L);
            stats2.setTotalDoctors(10L);

            // Then
            assertNotEquals(stats1, stats2);
        }

        @Test
        @DisplayName("should be equal when all fields are null")
        void shouldBeEqualWhenAllFieldsAreNull() {
            // Given
            UserStatsDto stats1 = new UserStatsDto();
            UserStatsDto stats2 = new UserStatsDto();

            // Then
            assertEquals(stats1, stats2);
            assertEquals(stats1.hashCode(), stats2.hashCode());
        }

        @Test
        @DisplayName("should handle different lastUpdated timestamps")
        void shouldHandleDifferentLastUpdatedTimestamps() {
            // Given
            UserStatsDto stats1 = new UserStatsDto();
            stats1.setTotalUsers(100L);
            stats1.setLastUpdated(LocalDateTime.of(2025, 1, 1, 0, 0));

            UserStatsDto stats2 = new UserStatsDto();
            stats2.setTotalUsers(100L);
            stats2.setLastUpdated(LocalDateTime.of(2025, 1, 1, 0, 1)); // 1 minute later

            // Then
            assertNotEquals(stats1, stats2);
        }
    }

    @Nested
    @DisplayName("ToString Tests")
    class ToStringTests {

        @Test
        @DisplayName("should generate non-null toString")
        void shouldGenerateNonNullToString() {
            // Given
            userStatsDto.setTotalUsers(1000L);
            userStatsDto.setTotalDoctors(50L);
            userStatsDto.setTotalPatients(950L);
            userStatsDto.setCompletedProfiles(800L);
            userStatsDto.setNewUsersThisWeek(25L);

            // When
            String toStringResult = userStatsDto.toString();

            // Then
            assertNotNull(toStringResult);
            assertTrue(toStringResult.contains("1000"));
            assertTrue(toStringResult.contains("50"));
            assertTrue(toStringResult.contains("950"));
            assertTrue(toStringResult.contains("800"));
            assertTrue(toStringResult.contains("25"));
            assertTrue(toStringResult.contains("totalUsers"));
            assertTrue(toStringResult.contains("totalDoctors"));
            assertTrue(toStringResult.contains("totalPatients"));
            assertTrue(toStringResult.contains("completedProfiles"));
            assertTrue(toStringResult.contains("newUsersThisWeek"));
        }

        @Test
        @DisplayName("toString should handle null fields gracefully")
        void toStringShouldHandleNullFields() {
            // Given - userStatsDto with null fields

            // When
            String toStringResult = userStatsDto.toString();

            // Then
            assertNotNull(toStringResult);
            // Should not throw NullPointerException
        }

        @Test
        @DisplayName("toString should include all field names")
        void toStringShouldIncludeAllFieldNames() {
            // When
            String toStringResult = userStatsDto.toString();

            // Then
            assertNotNull(toStringResult);
            assertTrue(toStringResult.contains("totalUsers"));
            assertTrue(toStringResult.contains("totalDoctors"));
            assertTrue(toStringResult.contains("totalPatients"));
            assertTrue(toStringResult.contains("completedProfiles"));
            assertTrue(toStringResult.contains("incompleteProfiles"));
            assertTrue(toStringResult.contains("newUsersThisWeek"));
            assertTrue(toStringResult.contains("lastUpdated"));
        }
    }

    @Nested
    @DisplayName("Edge Case Tests")
    class EdgeCaseTests {

        @Test
        @DisplayName("should handle maximum Long values")
        void shouldHandleMaximumLongValues() {
            // Given
            Long maxLong = Long.MAX_VALUE;
            LocalDateTime maxDateTime = LocalDateTime.MAX;

            // When
            userStatsDto.setTotalUsers(maxLong);
            userStatsDto.setTotalDoctors(maxLong);
            userStatsDto.setTotalPatients(maxLong);
            userStatsDto.setCompletedProfiles(maxLong);
            userStatsDto.setIncompleteProfiles(maxLong);
            userStatsDto.setNewUsersThisWeek(maxLong);
            userStatsDto.setLastUpdated(maxDateTime);

            // Then
            assertEquals(maxLong, userStatsDto.getTotalUsers());
            assertEquals(maxLong, userStatsDto.getTotalDoctors());
            assertEquals(maxLong, userStatsDto.getTotalPatients());
            assertEquals(maxLong, userStatsDto.getCompletedProfiles());
            assertEquals(maxLong, userStatsDto.getIncompleteProfiles());
            assertEquals(maxLong, userStatsDto.getNewUsersThisWeek());
            assertEquals(maxDateTime, userStatsDto.getLastUpdated());
        }

        @Test
        @DisplayName("should handle minimum Long values")
        void shouldHandleMinimumLongValues() {
            // Given
            Long minLong = Long.MIN_VALUE;
            LocalDateTime minDateTime = LocalDateTime.MIN;

            // When
            userStatsDto.setTotalUsers(minLong);
            userStatsDto.setTotalDoctors(minLong);
            userStatsDto.setTotalPatients(minLong);
            userStatsDto.setCompletedProfiles(minLong);
            userStatsDto.setIncompleteProfiles(minLong);
            userStatsDto.setNewUsersThisWeek(minLong);
            userStatsDto.setLastUpdated(minDateTime);

            // Then
            assertEquals(minLong, userStatsDto.getTotalUsers());
            assertEquals(minLong, userStatsDto.getTotalDoctors());
            assertEquals(minLong, userStatsDto.getTotalPatients());
            assertEquals(minLong, userStatsDto.getCompletedProfiles());
            assertEquals(minLong, userStatsDto.getIncompleteProfiles());
            assertEquals(minLong, userStatsDto.getNewUsersThisWeek());
            assertEquals(minDateTime, userStatsDto.getLastUpdated());
        }

        @Test
        @DisplayName("should handle very old lastUpdated timestamp")
        void shouldHandleVeryOldLastUpdatedTimestamp() {
            // Given
            LocalDateTime oldDate = LocalDateTime.of(2000, 1, 1, 0, 0);

            // When
            userStatsDto.setLastUpdated(oldDate);

            // Then
            assertEquals(oldDate, userStatsDto.getLastUpdated());
        }

        @Test
        @DisplayName("should handle very future lastUpdated timestamp")
        void shouldHandleVeryFutureLastUpdatedTimestamp() {
            // Given
            LocalDateTime futureDate = LocalDateTime.of(2100, 12, 31, 23, 59, 59);

            // When
            userStatsDto.setLastUpdated(futureDate);

            // Then
            assertEquals(futureDate, userStatsDto.getLastUpdated());
        }
    }

    @Nested
    @DisplayName("Consistency Tests")
    class ConsistencyTests {

        @Test
        @DisplayName("should maintain consistency between equals and hashCode")
        void shouldMaintainConsistencyBetweenEqualsAndHashCode() {
            // Given
            UserStatsDto stats1 = new UserStatsDto();
            stats1.setTotalUsers(500L);
            stats1.setTotalDoctors(25L);

            UserStatsDto stats2 = new UserStatsDto();
            stats2.setTotalUsers(500L);
            stats2.setTotalDoctors(25L);

            // Then
            assertEquals(stats1, stats2);
            assertEquals(stats1.hashCode(), stats2.hashCode());

            // When changing one object
            stats2.setTotalDoctors(26L);

            // Then they should no longer be equal
            assertNotEquals(stats1, stats2);
            assertNotEquals(stats1.hashCode(), stats2.hashCode());
        }

        @Test
        @DisplayName("should be reflexive in equals")
        void shouldBeReflexiveInEquals() {
            // Given
            UserStatsDto stats = new UserStatsDto();
            stats.setTotalUsers(100L);

            // Then
            assertEquals(stats, stats);
        }

        @Test
        @DisplayName("should be symmetric in equals")
        void shouldBeSymmetricInEquals() {
            // Given
            UserStatsDto stats1 = new UserStatsDto();
            stats1.setTotalUsers(200L);

            UserStatsDto stats2 = new UserStatsDto();
            stats2.setTotalUsers(200L);

            // Then
            assertEquals(stats1, stats2);
            assertEquals(stats2, stats1);
        }

        @Test
        @DisplayName("should be transitive in equals")
        void shouldBeTransitiveInEquals() {
            // Given
            UserStatsDto stats1 = new UserStatsDto();
            stats1.setTotalUsers(300L);

            UserStatsDto stats2 = new UserStatsDto();
            stats2.setTotalUsers(300L);

            UserStatsDto stats3 = new UserStatsDto();
            stats3.setTotalUsers(300L);

            // Then
            assertEquals(stats1, stats2);
            assertEquals(stats2, stats3);
            assertEquals(stats1, stats3);
        }
    }
}