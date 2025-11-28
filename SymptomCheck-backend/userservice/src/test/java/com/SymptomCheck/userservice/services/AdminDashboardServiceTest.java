package com.SymptomCheck.userservice.services;

import com.SymptomCheck.userservice.dtos.adminDashboardDto.AdminUserDto;
import com.SymptomCheck.userservice.dtos.adminDashboardDto.UserStatsDto;
import com.SymptomCheck.userservice.models.UserData;
import com.SymptomCheck.userservice.repositories.UserDataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AdminDashboardService Unit Tests")
class AdminDashboardServiceTest {

    @Mock
    private UserDataRepository userDataRepository;

    @InjectMocks
    private AdminDashboardService adminDashboardService;

    private UserData doctorUser;
    private UserData patientUser;
    private UserData incompleteUser;

    @BeforeEach
    void setUp() {
        // Setup doctor user
        doctorUser = new UserData();
        doctorUser.setId("doctor-123");
        doctorUser.setPhoneNumber("+1234567890");
        doctorUser.setProfilePhotoUrl("https://example.com/doctor.jpg");
        doctorUser.setProfileComplete(true);
        doctorUser.setClinicId(1L);
        doctorUser.setSpeciality("Cardiology");
        doctorUser.setDescription("Experienced cardiologist");
        doctorUser.setDiploma("MD, PhD");
        doctorUser.setCreatedAt(Instant.now().minus(10, ChronoUnit.DAYS));
        doctorUser.setUpdatedAt(Instant.now());

        // Setup patient user
        patientUser = new UserData();
        patientUser.setId("patient-123");
        patientUser.setPhoneNumber("+0987654321");
        patientUser.setProfilePhotoUrl("https://example.com/patient.jpg");
        patientUser.setProfileComplete(true);
        patientUser.setClinicId(null);
        patientUser.setSpeciality(null);
        patientUser.setDescription(null);
        patientUser.setDiploma(null);
        patientUser.setCreatedAt(Instant.now().minus(5, ChronoUnit.DAYS));
        patientUser.setUpdatedAt(Instant.now());

        // Setup incomplete profile user
        incompleteUser = new UserData();
        incompleteUser.setId("incomplete-123");
        incompleteUser.setPhoneNumber(null);
        incompleteUser.setProfilePhotoUrl(null);
        incompleteUser.setProfileComplete(false);
        incompleteUser.setClinicId(null);
        incompleteUser.setSpeciality(null);
        incompleteUser.setDescription(null);
        incompleteUser.setDiploma(null);
        incompleteUser.setCreatedAt(Instant.now().minus(1, ChronoUnit.DAYS));
        incompleteUser.setUpdatedAt(null);
    }

    @Nested
    @DisplayName("Get User Statistics Tests")
    class GetUserStatisticsTests {

        @Test
        @DisplayName("should calculate statistics correctly with mixed users")
        void shouldCalculateStatisticsCorrectlyWithMixedUsers() {
            // Given
            List<UserData> allUsers = List.of(doctorUser, patientUser, incompleteUser);
            List<UserData> doctors = List.of(doctorUser);
            List<UserData> patients = List.of(patientUser);
            List<UserData> completedProfiles = List.of(doctorUser, patientUser);
            Long newUsersThisWeek = 1L;

            when(userDataRepository.findAll()).thenReturn(allUsers);
            when(userDataRepository.findByClinicIdIsNotNull()).thenReturn(doctors);
            when(userDataRepository.findByClinicIdIsNullAndSpecialityIsNull()).thenReturn(patients);
            when(userDataRepository.findByProfileCompleteTrue()).thenReturn(completedProfiles);
            when(userDataRepository.countByCreatedAtAfter(any(Instant.class))).thenReturn(newUsersThisWeek);

            // When
            UserStatsDto stats = adminDashboardService.getUserStatistics();

            // Then
            assertNotNull(stats);
            assertEquals(3L, stats.getTotalUsers());
            assertEquals(1L, stats.getTotalDoctors());
            assertEquals(1L, stats.getTotalPatients());
            assertEquals(2L, stats.getCompletedProfiles());
            assertEquals(1L, stats.getIncompleteProfiles()); // 3 total - 2 completed = 1 incomplete
            assertEquals(1L, stats.getNewUsersThisWeek());
            assertNotNull(stats.getLastUpdated());
            assertTrue(stats.getLastUpdated().isBefore(LocalDateTime.now().plusSeconds(1)));

            verify(userDataRepository).findAll();
            verify(userDataRepository).findByClinicIdIsNotNull();
            verify(userDataRepository).findByClinicIdIsNullAndSpecialityIsNull();
            verify(userDataRepository).findByProfileCompleteTrue();
            verify(userDataRepository).countByCreatedAtAfter(any(Instant.class));
        }

        @Test
        @DisplayName("should handle empty database")
        void shouldHandleEmptyDatabase() {
            // Given
            List<UserData> emptyList = List.of();
            when(userDataRepository.findAll()).thenReturn(emptyList);
            when(userDataRepository.findByClinicIdIsNotNull()).thenReturn(emptyList);
            when(userDataRepository.findByClinicIdIsNullAndSpecialityIsNull()).thenReturn(emptyList);
            when(userDataRepository.findByProfileCompleteTrue()).thenReturn(emptyList);
            when(userDataRepository.countByCreatedAtAfter(any(Instant.class))).thenReturn(0L);

            // When
            UserStatsDto stats = adminDashboardService.getUserStatistics();

            // Then
            assertNotNull(stats);
            assertEquals(0L, stats.getTotalUsers());
            assertEquals(0L, stats.getTotalDoctors());
            assertEquals(0L, stats.getTotalPatients());
            assertEquals(0L, stats.getCompletedProfiles());
            assertEquals(0L, stats.getIncompleteProfiles());
            assertEquals(0L, stats.getNewUsersThisWeek());
            assertNotNull(stats.getLastUpdated());
        }

        @Test
        @DisplayName("should calculate incomplete profiles correctly")
        void shouldCalculateIncompleteProfilesCorrectly() {
            // Given
            List<UserData> allUsers = List.of(doctorUser, incompleteUser); // 1 complete, 1 incomplete
            List<UserData> doctors = List.of(doctorUser);
            List<UserData> patients = List.of();
            List<UserData> completedProfiles = List.of(doctorUser);
            Long newUsersThisWeek = 0L;

            when(userDataRepository.findAll()).thenReturn(allUsers);
            when(userDataRepository.findByClinicIdIsNotNull()).thenReturn(doctors);
            when(userDataRepository.findByClinicIdIsNullAndSpecialityIsNull()).thenReturn(patients);
            when(userDataRepository.findByProfileCompleteTrue()).thenReturn(completedProfiles);
            when(userDataRepository.countByCreatedAtAfter(any(Instant.class))).thenReturn(newUsersThisWeek);

            // When
            UserStatsDto stats = adminDashboardService.getUserStatistics();

            // Then
            assertEquals(2L, stats.getTotalUsers());
            assertEquals(1L, stats.getCompletedProfiles());
            assertEquals(1L, stats.getIncompleteProfiles()); // 2 - 1 = 1
        }

        @Test
        @DisplayName("should use correct time range for new users this week")
        void shouldUseCorrectTimeRangeForNewUsersThisWeek() {
            // Given
            List<UserData> allUsers = List.of(doctorUser);
            when(userDataRepository.findAll()).thenReturn(allUsers);
            when(userDataRepository.findByClinicIdIsNotNull()).thenReturn(allUsers);
            when(userDataRepository.findByClinicIdIsNullAndSpecialityIsNull()).thenReturn(List.of());
            when(userDataRepository.findByProfileCompleteTrue()).thenReturn(allUsers);
            when(userDataRepository.countByCreatedAtAfter(any(Instant.class))).thenReturn(1L);

            // When
            UserStatsDto stats = adminDashboardService.getUserStatistics();

            // Then
            assertEquals(1L, stats.getNewUsersThisWeek());
            verify(userDataRepository).countByCreatedAtAfter(any(Instant.class));
        }
    }

    @Nested
    @DisplayName("Get All Users Tests")
    class GetAllUsersTests {

        @Test
        @DisplayName("should return all users as AdminUserDto")
        void shouldReturnAllUsersAsAdminUserDto() {
            // Given
            List<UserData> users = List.of(doctorUser, patientUser);
            when(userDataRepository.findAll()).thenReturn(users);

            // When
            List<AdminUserDto> result = adminDashboardService.getAllUsers();

            // Then
            assertNotNull(result);
            assertEquals(2, result.size());

            // Verify doctor conversion
            AdminUserDto doctorDto = result.get(0);
            assertEquals("doctor-123", doctorDto.getId());
            assertEquals("+1234567890", doctorDto.getPhoneNumber());
            assertEquals("https://example.com/doctor.jpg", doctorDto.getProfilePhotoUrl());
            assertTrue(doctorDto.isProfileComplete());
            assertEquals(1L, doctorDto.getClinicId());
            assertEquals("Cardiology", doctorDto.getSpeciality());
            assertEquals("Experienced cardiologist", doctorDto.getDescription());
            assertEquals("MD, PhD", doctorDto.getDiploma());
            assertEquals("DOCTOR", doctorDto.getRole());

            // Verify patient conversion
            AdminUserDto patientDto = result.get(1);
            assertEquals("patient-123", patientDto.getId());
            assertEquals("PATIENT", patientDto.getRole());

            verify(userDataRepository).findAll();
        }

        @Test
        @DisplayName("should handle empty user list")
        void shouldHandleEmptyUserList() {
            // Given
            when(userDataRepository.findAll()).thenReturn(List.of());

            // When
            List<AdminUserDto> result = adminDashboardService.getAllUsers();

            // Then
            assertNotNull(result);
            assertTrue(result.isEmpty());
            verify(userDataRepository).findAll();
        }

        @Test
        @DisplayName("should handle users with null profileComplete")
        void shouldHandleUsersWithNullProfileComplete() {
            // Given
            UserData userWithNullProfile = new UserData();
            userWithNullProfile.setId("null-profile-user");
            userWithNullProfile.setProfileComplete(null);
            userWithNullProfile.setClinicId(null);

            when(userDataRepository.findAll()).thenReturn(List.of(userWithNullProfile));

            // When
            List<AdminUserDto> result = adminDashboardService.getAllUsers();

            // Then
            assertNotNull(result);
            assertEquals(1, result.size());
            assertFalse(result.get(0).isProfileComplete()); // Should default to false
            assertEquals("PATIENT", result.get(0).getRole());
        }
    }

    @Nested
    @DisplayName("Get Users By Role Tests")
    class GetUsersByRoleTests {

        @Test
        @DisplayName("should return doctors when role is DOCTOR")
        void shouldReturnDoctorsWhenRoleIsDoctor() {
            // Given
            List<UserData> doctors = List.of(doctorUser);
            when(userDataRepository.findByClinicIdIsNotNull()).thenReturn(doctors);

            // When
            List<AdminUserDto> result = adminDashboardService.getUsersByRole("DOCTOR");

            // Then
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals("DOCTOR", result.get(0).getRole());
            verify(userDataRepository).findByClinicIdIsNotNull();
            verify(userDataRepository, never()).findByClinicIdIsNullAndSpecialityIsNull();
            verify(userDataRepository, never()).findAll();
        }

        @Test
        @DisplayName("should return patients when role is PATIENT")
        void shouldReturnPatientsWhenRoleIsPatient() {
            // Given
            List<UserData> patients = List.of(patientUser);
            when(userDataRepository.findByClinicIdIsNullAndSpecialityIsNull()).thenReturn(patients);

            // When
            List<AdminUserDto> result = adminDashboardService.getUsersByRole("PATIENT");

            // Then
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals("PATIENT", result.get(0).getRole());
            verify(userDataRepository).findByClinicIdIsNullAndSpecialityIsNull();
            verify(userDataRepository, never()).findByClinicIdIsNotNull();
            verify(userDataRepository, never()).findAll();
        }

        @Test
        @DisplayName("should return all users when role is unknown")
        void shouldReturnAllUsersWhenRoleIsUnknown() {
            // Given
            List<UserData> allUsers = List.of(doctorUser, patientUser);
            when(userDataRepository.findAll()).thenReturn(allUsers);

            // When
            List<AdminUserDto> result = adminDashboardService.getUsersByRole("UNKNOWN");

            // Then
            assertNotNull(result);
            assertEquals(2, result.size());
            verify(userDataRepository).findAll();
            verify(userDataRepository, never()).findByClinicIdIsNotNull();
            verify(userDataRepository, never()).findByClinicIdIsNullAndSpecialityIsNull();
        }

        @Test
        @DisplayName("should handle case insensitive role")
        void shouldHandleCaseInsensitiveRole() {
            // Given
            List<UserData> doctors = List.of(doctorUser);
            when(userDataRepository.findByClinicIdIsNotNull()).thenReturn(doctors);

            // When
            List<AdminUserDto> result1 = adminDashboardService.getUsersByRole("doctor");
            List<AdminUserDto> result2 = adminDashboardService.getUsersByRole("DoCtOr");
            List<AdminUserDto> result3 = adminDashboardService.getUsersByRole("DOCTOR");

            // Then
            assertEquals(1, result1.size());
            assertEquals(1, result2.size());
            assertEquals(1, result3.size());
            verify(userDataRepository, times(3)).findByClinicIdIsNotNull();
        }

        @Test
        @DisplayName("should handle empty role")
        void shouldHandleEmptyRole() {
            // Given
            List<UserData> allUsers = List.of(doctorUser, patientUser);
            when(userDataRepository.findAll()).thenReturn(allUsers);

            // When
            List<AdminUserDto> result = adminDashboardService.getUsersByRole("");

            // Then
            assertNotNull(result);
            assertEquals(2, result.size());
            verify(userDataRepository).findAll();
        }

        @Nested
        @DisplayName("Update User Profile Status Tests")
        class UpdateUserProfileStatusTests {


            @Test
            @DisplayName("should throw exception when user not found")
            void shouldThrowExceptionWhenUserNotFound() {
                // Given
                String userId = "non-existent-user";
                when(userDataRepository.findById(userId)).thenReturn(Optional.empty());

                // When & Then
                RuntimeException exception = assertThrows(RuntimeException.class,
                        () -> adminDashboardService.updateUserProfileStatus(userId, true));

                assertEquals("User not found", exception.getMessage());
                verify(userDataRepository).findById(userId);
                verify(userDataRepository, never()).save(any());
            }

        }
    }
}