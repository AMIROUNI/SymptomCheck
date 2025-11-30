package com.SymptomCheck.userservice.unit.controllers;

import com.SymptomCheck.userservice.controllers.AdminDashboardController;
import com.SymptomCheck.userservice.dtos.adminDashboardDto.AdminUserDto;
import com.SymptomCheck.userservice.dtos.adminDashboardDto.UserStatsDto;
import com.SymptomCheck.userservice.services.AdminDashboardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AdminDashboardController Unit Tests")
class AdminDashboardControllerTest {

    @Mock
    private AdminDashboardService adminDashboardService;

    @InjectMocks
    private AdminDashboardController adminDashboardController;

    private UserStatsDto userStatsDto;
    private AdminUserDto adminUserDto;

    @BeforeEach
    void setUp() {
        // Setup UserStatsDto
        userStatsDto = new UserStatsDto();
        userStatsDto.setTotalUsers(1000L);
        userStatsDto.setTotalDoctors(50L);
        userStatsDto.setTotalPatients(950L);
        userStatsDto.setCompletedProfiles(800L);
        userStatsDto.setIncompleteProfiles(200L);
        userStatsDto.setNewUsersThisWeek(25L);
        userStatsDto.setLastUpdated(LocalDateTime.now());

        // Setup AdminUserDto
        adminUserDto = new AdminUserDto();
        adminUserDto.setId("user-123");
        adminUserDto.setPhoneNumber("+1234567890");
        adminUserDto.setProfilePhotoUrl("https://example.com/photo.jpg");
        adminUserDto.setProfileComplete(true);
        adminUserDto.setClinicId(1L);
        adminUserDto.setSpeciality("Cardiology");
        adminUserDto.setDescription("Experienced cardiologist");
        adminUserDto.setDiploma("MD, PhD");
        adminUserDto.setRole("DOCTOR");
    }

    @Nested
    @DisplayName("Get Dashboard Stats Tests")
    class GetDashboardStatsTests {

        @Test
        @DisplayName("should return dashboard statistics successfully")
        void shouldReturnDashboardStatisticsSuccessfully() {
            // Given
            when(adminDashboardService.getUserStatistics()).thenReturn(userStatsDto);

            // When
            ResponseEntity<UserStatsDto> response = adminDashboardController.getDashboardStats();

            // Then
            assertEquals(200, response.getStatusCodeValue());
            assertEquals(userStatsDto, response.getBody());
            verify(adminDashboardService).getUserStatistics();
        }

        @Test
        @DisplayName("should return statistics with correct data")
        void shouldReturnStatisticsWithCorrectData() {
            // Given
            when(adminDashboardService.getUserStatistics()).thenReturn(userStatsDto);

            // When
            ResponseEntity<UserStatsDto> response = adminDashboardController.getDashboardStats();

            // Then
            UserStatsDto responseBody = response.getBody();
            assertNotNull(responseBody);
            assertEquals(1000L, responseBody.getTotalUsers());
            assertEquals(50L, responseBody.getTotalDoctors());
            assertEquals(950L, responseBody.getTotalPatients());
            assertEquals(800L, responseBody.getCompletedProfiles());
            assertEquals(200L, responseBody.getIncompleteProfiles());
            assertEquals(25L, responseBody.getNewUsersThisWeek());
            assertNotNull(responseBody.getLastUpdated());
        }

        @Test
        @DisplayName("should call service exactly once")
        void shouldCallServiceExactlyOnce() {
            // Given
            when(adminDashboardService.getUserStatistics()).thenReturn(userStatsDto);

            // When
            adminDashboardController.getDashboardStats();

            // Then
            verify(adminDashboardService, times(1)).getUserStatistics();
            verifyNoMoreInteractions(adminDashboardService);
        }
    }

    @Nested
    @DisplayName("Get All Users Tests")
    class GetAllUsersTests {

        @Test
        @DisplayName("should return all users successfully")
        void shouldReturnAllUsersSuccessfully() {
            // Given
            List<AdminUserDto> users = List.of(adminUserDto);
            when(adminDashboardService.getAllUsers()).thenReturn(users);

            // When
            ResponseEntity<List<AdminUserDto>> response = adminDashboardController.getAllUsers();

            // Then
            assertEquals(200, response.getStatusCodeValue());
            assertEquals(1, response.getBody().size());
            assertEquals(adminUserDto, response.getBody().get(0));
            verify(adminDashboardService).getAllUsers();
        }

        @Test
        @DisplayName("should return empty list when no users exist")
        void shouldReturnEmptyListWhenNoUsersExist() {
            // Given
            List<AdminUserDto> emptyList = List.of();
            when(adminDashboardService.getAllUsers()).thenReturn(emptyList);

            // When
            ResponseEntity<List<AdminUserDto>> response = adminDashboardController.getAllUsers();

            // Then
            assertEquals(200, response.getStatusCodeValue());
            assertTrue(response.getBody().isEmpty());
            verify(adminDashboardService).getAllUsers();
        }

        @Test
        @DisplayName("should return multiple users")
        void shouldReturnMultipleUsers() {
            // Given
            AdminUserDto user2 = new AdminUserDto();
            user2.setId("user-456");
            user2.setRole("PATIENT");

            List<AdminUserDto> users = List.of(adminUserDto, user2);
            when(adminDashboardService.getAllUsers()).thenReturn(users);

            // When
            ResponseEntity<List<AdminUserDto>> response = adminDashboardController.getAllUsers();

            // Then
            assertEquals(200, response.getStatusCodeValue());
            assertEquals(2, response.getBody().size());
            assertEquals("user-123", response.getBody().get(0).getId());
            assertEquals("user-456", response.getBody().get(1).getId());
            verify(adminDashboardService).getAllUsers();
        }
    }

    @Nested
    @DisplayName("Get Users By Role Tests")
    class GetUsersByRoleTests {

        @Test
        @DisplayName("should return users by role successfully")
        void shouldReturnUsersByRoleSuccessfully() {
            // Given
            String role = "DOCTOR";
            List<AdminUserDto> doctors = List.of(adminUserDto);
            when(adminDashboardService.getUsersByRole(role)).thenReturn(doctors);

            // When
            ResponseEntity<List<AdminUserDto>> response = adminDashboardController.getUsersByRole(role);

            // Then
            assertEquals(200, response.getStatusCodeValue());
            assertEquals(1, response.getBody().size());
            assertEquals("DOCTOR", response.getBody().get(0).getRole());
            verify(adminDashboardService).getUsersByRole(role);
        }

        @Test
        @DisplayName("should handle PATIENT role")
        void shouldHandlePatientRole() {
            // Given
            String role = "PATIENT";
            AdminUserDto patient = new AdminUserDto();
            patient.setId("patient-123");
            patient.setRole("PATIENT");

            List<AdminUserDto> patients = List.of(patient);
            when(adminDashboardService.getUsersByRole(role)).thenReturn(patients);

            // When
            ResponseEntity<List<AdminUserDto>> response = adminDashboardController.getUsersByRole(role);

            // Then
            assertEquals(200, response.getStatusCodeValue());
            assertEquals(1, response.getBody().size());
            assertEquals("PATIENT", response.getBody().get(0).getRole());
            verify(adminDashboardService).getUsersByRole(role);
        }

        @Test
        @DisplayName("should return empty list for non-existent role")
        void shouldReturnEmptyListForNonExistentRole() {
            // Given
            String role = "NON_EXISTENT";
            List<AdminUserDto> emptyList = List.of();
            when(adminDashboardService.getUsersByRole(role)).thenReturn(emptyList);

            // When
            ResponseEntity<List<AdminUserDto>> response = adminDashboardController.getUsersByRole(role);

            // Then
            assertEquals(200, response.getStatusCodeValue());
            assertTrue(response.getBody().isEmpty());
            verify(adminDashboardService).getUsersByRole(role);
        }

        @Test
        @DisplayName("should handle case-sensitive roles")
        void shouldHandleCaseSensitiveRoles() {
            // Given
            String role = "doctor"; // lowercase
            List<AdminUserDto> doctors = List.of(adminUserDto);
            when(adminDashboardService.getUsersByRole(role)).thenReturn(doctors);

            // When
            ResponseEntity<List<AdminUserDto>> response = adminDashboardController.getUsersByRole(role);

            // Then
            assertEquals(200, response.getStatusCodeValue());
            verify(adminDashboardService).getUsersByRole("doctor"); // exact match
        }

        @Test
        @DisplayName("should handle empty role")
        void shouldHandleEmptyRole() {
            // Given
            String role = "";
            List<AdminUserDto> emptyList = List.of();
            when(adminDashboardService.getUsersByRole(role)).thenReturn(emptyList);

            // When
            ResponseEntity<List<AdminUserDto>> response = adminDashboardController.getUsersByRole(role);

            // Then
            assertEquals(200, response.getStatusCodeValue());
            assertTrue(response.getBody().isEmpty());
            verify(adminDashboardService).getUsersByRole(role);
        }
    }

    @Nested
    @DisplayName("Update User Profile Status Tests")
    class UpdateUserProfileStatusTests {

        @Test
        @DisplayName("should update user profile status to complete successfully")
        void shouldUpdateUserProfileStatusToCompleteSuccessfully() {
            // Given
            String userId = "user-123";
            boolean profileComplete = true;

            AdminUserDto updatedUser = new AdminUserDto();
            updatedUser.setId(userId);
            updatedUser.setProfileComplete(true);

            when(adminDashboardService.updateUserProfileStatus(userId, profileComplete))
                    .thenReturn(updatedUser);

            // When
            ResponseEntity<AdminUserDto> response =
                    adminDashboardController.updateUserProfileStatus(userId, profileComplete);

            // Then
            assertEquals(200, response.getStatusCodeValue());
            assertEquals(updatedUser, response.getBody());
            assertTrue(response.getBody().isProfileComplete());
            verify(adminDashboardService).updateUserProfileStatus(userId, profileComplete);
        }

        @Test
        @DisplayName("should update user profile status to incomplete successfully")
        void shouldUpdateUserProfileStatusToIncompleteSuccessfully() {
            // Given
            String userId = "user-123";
            boolean profileComplete = false;

            AdminUserDto updatedUser = new AdminUserDto();
            updatedUser.setId(userId);
            updatedUser.setProfileComplete(false);

            when(adminDashboardService.updateUserProfileStatus(userId, profileComplete))
                    .thenReturn(updatedUser);

            // When
            ResponseEntity<AdminUserDto> response =
                    adminDashboardController.updateUserProfileStatus(userId, profileComplete);

            // Then
            assertEquals(200, response.getStatusCodeValue());
            assertEquals(updatedUser, response.getBody());
            assertFalse(response.getBody().isProfileComplete());
            verify(adminDashboardService).updateUserProfileStatus(userId, profileComplete);
        }

        @Test
        @DisplayName("should handle different user IDs")
        void shouldHandleDifferentUserIds() {
            // Given
            String userId = "different-user-456";
            boolean profileComplete = true;

            AdminUserDto updatedUser = new AdminUserDto();
            updatedUser.setId(userId);
            updatedUser.setProfileComplete(true);

            when(adminDashboardService.updateUserProfileStatus(userId, profileComplete))
                    .thenReturn(updatedUser);

            // When
            ResponseEntity<AdminUserDto> response =
                    adminDashboardController.updateUserProfileStatus(userId, profileComplete);

            // Then
            assertEquals(200, response.getStatusCodeValue());
            assertEquals("different-user-456", response.getBody().getId());
            verify(adminDashboardService).updateUserProfileStatus(userId, profileComplete);
        }

        @Test
        @DisplayName("should handle empty user ID")
        void shouldHandleEmptyUserId() {
            // Given
            String userId = "";
            boolean profileComplete = true;

            AdminUserDto updatedUser = new AdminUserDto();
            updatedUser.setId("");
            updatedUser.setProfileComplete(true);

            when(adminDashboardService.updateUserProfileStatus(userId, profileComplete))
                    .thenReturn(updatedUser);

            // When
            ResponseEntity<AdminUserDto> response =
                    adminDashboardController.updateUserProfileStatus(userId, profileComplete);

            // Then
            assertEquals(200, response.getStatusCodeValue());
            assertEquals("", response.getBody().getId());
            verify(adminDashboardService).updateUserProfileStatus(userId, profileComplete);
        }
    }

    @Nested
    @DisplayName("Controller Integration Tests")
    class ControllerIntegrationTests {

        @Test
        @DisplayName("should handle multiple consecutive service calls")
        void shouldHandleMultipleConsecutiveServiceCalls() {
            // Given
            when(adminDashboardService.getUserStatistics()).thenReturn(userStatsDto);
            when(adminDashboardService.getAllUsers()).thenReturn(List.of(adminUserDto));
            when(adminDashboardService.getUsersByRole("DOCTOR")).thenReturn(List.of(adminUserDto));

            // When - call multiple endpoints
            ResponseEntity<UserStatsDto> statsResponse = adminDashboardController.getDashboardStats();
            ResponseEntity<List<AdminUserDto>> allUsersResponse = adminDashboardController.getAllUsers();
            ResponseEntity<List<AdminUserDto>> roleResponse = adminDashboardController.getUsersByRole("DOCTOR");

            // Then
            assertEquals(200, statsResponse.getStatusCodeValue());
            assertEquals(200, allUsersResponse.getStatusCodeValue());
            assertEquals(200, roleResponse.getStatusCodeValue());

            verify(adminDashboardService).getUserStatistics();
            verify(adminDashboardService).getAllUsers();
            verify(adminDashboardService).getUsersByRole("DOCTOR");
        }

        @Test
        @DisplayName("should maintain separation between service calls")
        void shouldMaintainSeparationBetweenServiceCalls() {
            // Given
            when(adminDashboardService.getUserStatistics()).thenReturn(userStatsDto);
            when(adminDashboardService.getAllUsers()).thenReturn(List.of());

            // When
            adminDashboardController.getDashboardStats();
            adminDashboardController.getAllUsers();

            // Then
            verify(adminDashboardService, times(1)).getUserStatistics();
            verify(adminDashboardService, times(1)).getAllUsers();
            verifyNoMoreInteractions(adminDashboardService);
        }
    }

    @Nested
    @DisplayName("Edge Case Tests")
    class EdgeCaseTests {

        @Test
        @DisplayName("should handle null statistics from service")
        void shouldHandleNullStatisticsFromService() {
            // Given
            when(adminDashboardService.getUserStatistics()).thenReturn(null);

            // When
            ResponseEntity<UserStatsDto> response = adminDashboardController.getDashboardStats();

            // Then
            assertEquals(200, response.getStatusCodeValue());
            assertNull(response.getBody());
            verify(adminDashboardService).getUserStatistics();
        }

        @Test
        @DisplayName("should handle null users list from service")
        void shouldHandleNullUsersListFromService() {
            // Given
            when(adminDashboardService.getAllUsers()).thenReturn(null);

            // When
            ResponseEntity<List<AdminUserDto>> response = adminDashboardController.getAllUsers();

            // Then
            assertEquals(200, response.getStatusCodeValue());
            assertNull(response.getBody());
            verify(adminDashboardService).getAllUsers();
        }

        @Test
        @DisplayName("should handle null role users list from service")
        void shouldHandleNullRoleUsersListFromService() {
            // Given
            String role = "DOCTOR";
            when(adminDashboardService.getUsersByRole(role)).thenReturn(null);

            // When
            ResponseEntity<List<AdminUserDto>> response = adminDashboardController.getUsersByRole(role);

            // Then
            assertEquals(200, response.getStatusCodeValue());
            assertNull(response.getBody());
            verify(adminDashboardService).getUsersByRole(role);
        }

        @Test
        @DisplayName("should handle null updated user from service")
        void shouldHandleNullUpdatedUserFromService() {
            // Given
            String userId = "user-123";
            boolean profileComplete = true;
            when(adminDashboardService.updateUserProfileStatus(userId, profileComplete)).thenReturn(null);

            // When
            ResponseEntity<AdminUserDto> response =
                    adminDashboardController.updateUserProfileStatus(userId, profileComplete);

            // Then
            assertEquals(200, response.getStatusCodeValue());
            assertNull(response.getBody());
            verify(adminDashboardService).updateUserProfileStatus(userId, profileComplete);
        }
    }

    @Nested
    @DisplayName("Response Validation Tests")
    class ResponseValidationTests {

        @Test
        @DisplayName("should always return 200 OK for successful requests")
        void shouldAlwaysReturn200OkForSuccessfulRequests() {
            // Given
            when(adminDashboardService.getUserStatistics()).thenReturn(userStatsDto);
            when(adminDashboardService.getAllUsers()).thenReturn(List.of(adminUserDto));
            when(adminDashboardService.getUsersByRole("DOCTOR")).thenReturn(List.of(adminUserDto));
            when(adminDashboardService.updateUserProfileStatus(anyString(), anyBoolean())).thenReturn(adminUserDto);

            // When & Then
            assertEquals(200, adminDashboardController.getDashboardStats().getStatusCodeValue());
            assertEquals(200, adminDashboardController.getAllUsers().getStatusCodeValue());
            assertEquals(200, adminDashboardController.getUsersByRole("DOCTOR").getStatusCodeValue());
            assertEquals(200, adminDashboardController.updateUserProfileStatus("user-123", true).getStatusCodeValue());
        }

        @Test
        @DisplayName("should preserve service response data")
        void shouldPreserveServiceResponseData() {
            // Given
            UserStatsDto customStats = new UserStatsDto();
            customStats.setTotalUsers(500L);
            customStats.setTotalDoctors(25L);
            customStats.setTotalPatients(475L);

            when(adminDashboardService.getUserStatistics()).thenReturn(customStats);

            // When
            ResponseEntity<UserStatsDto> response = adminDashboardController.getDashboardStats();

            // Then
            UserStatsDto responseBody = response.getBody();
            assertNotNull(responseBody);
            assertEquals(500L, responseBody.getTotalUsers());
            assertEquals(25L, responseBody.getTotalDoctors());
            assertEquals(475L, responseBody.getTotalPatients());
        }
    }
}