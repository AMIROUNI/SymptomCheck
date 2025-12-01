package com.SymptomCheck.userservice.functional.api;

import com.SymptomCheck.userservice.config.KeycloakSecurityConfig;
import com.SymptomCheck.userservice.controllers.AdminDashboardController;
import com.SymptomCheck.userservice.dtos.adminDashboardDto.AdminUserDto;
import com.SymptomCheck.userservice.dtos.adminDashboardDto.UserStatsDto;
import com.SymptomCheck.userservice.exceptions.UserNotFountException;
import com.SymptomCheck.userservice.services.AdminDashboardService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminDashboardController.class)
@Import(KeycloakSecurityConfig.class)
class AdminDashboardControllerApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AdminDashboardService adminDashboardService;

    private UserStatsDto userStatsDto;
    private AdminUserDto adminUserDto;
    private List<AdminUserDto> adminUserList;

    private static final String ADMIN_USERNAME = "admin";
    private static final String TEST_USER_ID = "gc6274ff-630a-44f0-6666-17ada9054fe8";

    @BeforeEach
    void setUp() {
        // Setup UserStatsDto using regular object instantiation
        userStatsDto = new UserStatsDto();
        userStatsDto.setTotalUsers(150L);
        userStatsDto.setTotalDoctors(45L);
        userStatsDto.setTotalPatients(90L);
        userStatsDto.setCompletedProfiles(128L);
        userStatsDto.setIncompleteProfiles(22L);
        userStatsDto.setNewUsersThisWeek(15L);
        userStatsDto.setLastUpdated(LocalDateTime.now());

        // Setup AdminUserDto using regular object instantiation
        adminUserDto = new AdminUserDto();
        adminUserDto.setId(TEST_USER_ID);
        adminUserDto.setPhoneNumber("+1234567890");
        adminUserDto.setProfilePhotoUrl("https://example.com/photo.jpg");
        adminUserDto.setProfileComplete(true);
        adminUserDto.setClinicId(1L);
        adminUserDto.setSpeciality("Cardiology");
        adminUserDto.setDescription("Experienced cardiologist");
        adminUserDto.setDiploma("MD, Cardiology Board Certified");
        adminUserDto.setCreatedAt(Instant.now().minusSeconds(864000)); // 10 days ago
        adminUserDto.setUpdatedAt(Instant.now().minusSeconds(86400)); // 1 day ago
        adminUserDto.setRole("DOCTOR");

        // Setup second user (patient)
        AdminUserDto patientUser = new AdminUserDto();
        patientUser.setId("auth0|987654321");
        patientUser.setPhoneNumber("+1987654321");
        patientUser.setProfilePhotoUrl("https://example.com/photo2.jpg");
        patientUser.setProfileComplete(false);
        patientUser.setClinicId(null); // Patient doesn't have clinic
        patientUser.setSpeciality(null); // Patient doesn't have speciality
        patientUser.setDescription(null);
        patientUser.setDiploma(null);
        patientUser.setCreatedAt(Instant.now().minusSeconds(432000)); // 5 days ago
        patientUser.setUpdatedAt(Instant.now().minusSeconds(3600)); // 1 hour ago
        patientUser.setRole("PATIENT");

        // Setup user list
        adminUserList = List.of(adminUserDto, patientUser);
    }

    // ──────────────────────────────────────────────────────────────
    // GET DASHBOARD STATS
    // ──────────────────────────────────────────────────────────────
    @Nested
    @WithMockUser(username = ADMIN_USERNAME, roles = "ADMIN")
    class GetDashboardStats {

        @Test
        void shouldReturnDashboardStatsSuccessfully() throws Exception {
            // Given
            given(adminDashboardService.getUserStatistics()).willReturn(userStatsDto);

            // When & Then
            mockMvc.perform(get("/api/admin/dashboard/stats")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalUsers").value(150))
                    .andExpect(jsonPath("$.totalDoctors").value(45))
                    .andExpect(jsonPath("$.totalPatients").value(90))
                    .andExpect(jsonPath("$.completedProfiles").value(128))
                    .andExpect(jsonPath("$.incompleteProfiles").value(22))
                    .andExpect(jsonPath("$.newUsersThisWeek").value(15))
                    .andExpect(jsonPath("$.lastUpdated").exists());

            // Verify service method was called
            verify(adminDashboardService).getUserStatistics();
        }

        @Test
        void shouldReturnEmptyStatsWhenNoData() throws Exception {
            // Given
            UserStatsDto emptyStats = new UserStatsDto();
            emptyStats.setTotalUsers(0L);
            emptyStats.setTotalDoctors(0L);
            emptyStats.setTotalPatients(0L);
            emptyStats.setCompletedProfiles(0L);
            emptyStats.setIncompleteProfiles(0L);
            emptyStats.setNewUsersThisWeek(0L);
            emptyStats.setLastUpdated(LocalDateTime.now());
            given(adminDashboardService.getUserStatistics()).willReturn(emptyStats);

            // When & Then
            mockMvc.perform(get("/api/admin/dashboard/stats"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalUsers").value(0))
                    .andExpect(jsonPath("$.totalDoctors").value(0))
                    .andExpect(jsonPath("$.totalPatients").value(0))
                    .andExpect(jsonPath("$.completedProfiles").value(0))
                    .andExpect(jsonPath("$.incompleteProfiles").value(0))
                    .andExpect(jsonPath("$.newUsersThisWeek").value(0));
        }

        @Test
        void shouldHandleNullValuesInStats() throws Exception {
            // Given - Stats with some null values
            UserStatsDto partialStats = new UserStatsDto();
            partialStats.setTotalUsers(100L);
            partialStats.setTotalDoctors(null);
            partialStats.setTotalPatients(80L);
            partialStats.setCompletedProfiles(null);
            partialStats.setIncompleteProfiles(20L);
            partialStats.setNewUsersThisWeek(10L);
            partialStats.setLastUpdated(LocalDateTime.now());
            given(adminDashboardService.getUserStatistics()).willReturn(partialStats);

            // When & Then
            mockMvc.perform(get("/api/admin/dashboard/stats"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalUsers").value(100))
                    .andExpect(jsonPath("$.totalPatients").value(80))
                    .andExpect(jsonPath("$.incompleteProfiles").value(20))
                    .andExpect(jsonPath("$.newUsersThisWeek").value(10));
        }


    }

    // ──────────────────────────────────────────────────────────────
    // GET ALL USERS
    // ──────────────────────────────────────────────────────────────
    @Nested
    @WithMockUser(username = ADMIN_USERNAME, roles = "ADMIN")
    class GetAllUsers {

        @Test
        void shouldReturnAllUsersSuccessfully() throws Exception {
            // Given
            given(adminDashboardService.getAllUsers()).willReturn(adminUserList);

            // When & Then
            mockMvc.perform(get("/api/admin/users")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].id").value(TEST_USER_ID))
                    .andExpect(jsonPath("$[0].phoneNumber").value("+1234567890"))
                    .andExpect(jsonPath("$[0].profilePhotoUrl").value("https://example.com/photo.jpg"))
                    .andExpect(jsonPath("$[0].profileComplete").value(true))
                    .andExpect(jsonPath("$[0].clinicId").value(1))
                    .andExpect(jsonPath("$[0].speciality").value("Cardiology"))
                    .andExpect(jsonPath("$[0].description").value("Experienced cardiologist"))
                    .andExpect(jsonPath("$[0].diploma").value("MD, Cardiology Board Certified"))
                    .andExpect(jsonPath("$[0].role").value("DOCTOR"))
                    .andExpect(jsonPath("$[0].createdAt").exists())
                    .andExpect(jsonPath("$[0].updatedAt").exists())
                    .andExpect(jsonPath("$[1].role").value("PATIENT"))
                    .andExpect(jsonPath("$[1].profileComplete").value(false));

            // Verify service method was called
            verify(adminDashboardService).getAllUsers();
        }

        @Test
        void shouldReturnEmptyListWhenNoUsers() throws Exception {
            // Given
            given(adminDashboardService.getAllUsers()).willReturn(List.of());

            // When & Then
            mockMvc.perform(get("/api/admin/users"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)))
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$").isEmpty());
        }

        @Test
        void shouldHandleUsersWithPartialData() throws Exception {
            // Given - Users with some null fields
            AdminUserDto partialUser = new AdminUserDto();
            partialUser.setId("auth0|partial1");
            partialUser.setPhoneNumber(null);
            partialUser.setProfilePhotoUrl(null);
            partialUser.setProfileComplete(false);
            partialUser.setClinicId(null);
            partialUser.setSpeciality(null);
            partialUser.setDescription(null);
            partialUser.setDiploma(null);
            partialUser.setCreatedAt(Instant.now());
            partialUser.setUpdatedAt(Instant.now());
            partialUser.setRole("PATIENT");

            List<AdminUserDto> partialUsers = List.of(partialUser);
            given(adminDashboardService.getAllUsers()).willReturn(partialUsers);

            // When & Then
            mockMvc.perform(get("/api/admin/users"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value("auth0|partial1"))
                    .andExpect(jsonPath("$[0].profileComplete").value(false))
                    .andExpect(jsonPath("$[0].role").value("PATIENT"));
        }
    }

    // ──────────────────────────────────────────────────────────────
    // GET USERS BY ROLE
    // ──────────────────────────────────────────────────────────────
    @Nested
    @WithMockUser(username = ADMIN_USERNAME, roles = "ADMIN")
    class GetUsersByRole {

        @Test
        void shouldReturnDoctorsByRoleSuccessfully() throws Exception {
            // Given
            String role = "DOCTOR";
            List<AdminUserDto> doctors = adminUserList.stream()
                    .filter(user -> "DOCTOR".equals(user.getRole()))
                    .toList();
            given(adminDashboardService.getUsersByRole(role)).willReturn(doctors);

            // When & Then
            mockMvc.perform(get("/api/admin/users/role/{role}", role)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].role").value("DOCTOR"))
                    .andExpect(jsonPath("$[0].speciality").value("Cardiology"))
                    .andExpect(jsonPath("$[0].clinicId").value(1));

            // Verify service method was called with correct parameter
            verify(adminDashboardService).getUsersByRole(role);
        }

        @Test
        void shouldReturnPatientsByRoleSuccessfully() throws Exception {
            // Given
            String role = "PATIENT";
            List<AdminUserDto> patients = adminUserList.stream()
                    .filter(user -> "PATIENT".equals(user.getRole()))
                    .toList();
            given(adminDashboardService.getUsersByRole(role)).willReturn(patients);

            // When & Then
            mockMvc.perform(get("/api/admin/users/role/{role}", role))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].role").value("PATIENT"))
                    .andExpect(jsonPath("$[0].speciality").doesNotExist())
                    .andExpect(jsonPath("$[0].clinicId").doesNotExist());
        }

        @Test
        void shouldReturnEmptyListForNonExistentRole() throws Exception {
            // Given
            String nonExistentRole = "MANAGER";
            given(adminDashboardService.getUsersByRole(nonExistentRole)).willReturn(List.of());

            // When & Then
            mockMvc.perform(get("/api/admin/users/role/{role}", nonExistentRole))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)))
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$").isEmpty());
        }

        @Test
        void shouldHandleCaseSensitiveRoles() throws Exception {
            // Given
            String lowerCaseRole = "doctor";
            given(adminDashboardService.getUsersByRole(lowerCaseRole)).willReturn(List.of());

            // When & Then
            mockMvc.perform(get("/api/admin/users/role/{role}", lowerCaseRole))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));

            verify(adminDashboardService).getUsersByRole(lowerCaseRole);
        }
    }

    // ──────────────────────────────────────────────────────────────
    // UPDATE USER PROFILE STATUS
    // ──────────────────────────────────────────────────────────────
    @Nested
    @WithMockUser(username = ADMIN_USERNAME, roles = "ADMIN")
    class UpdateUserProfileStatus {

        @Test
        void shouldUpdateProfileStatusToCompleteSuccessfully() throws Exception {
            // Given
            boolean newStatus = true;
            AdminUserDto updatedUser = new AdminUserDto();
            updatedUser.setId(TEST_USER_ID);
            updatedUser.setPhoneNumber("+1234567890");
            updatedUser.setProfileComplete(newStatus);
            updatedUser.setClinicId(1L);
            updatedUser.setSpeciality("Cardiology");
            updatedUser.setRole("DOCTOR");
            updatedUser.setUpdatedAt(Instant.now());

            given(adminDashboardService.updateUserProfileStatus(TEST_USER_ID, newStatus)).willReturn(updatedUser);

            // When & Then
            mockMvc.perform(put("/api/admin/users/{userId}/status", TEST_USER_ID)
                            .param("profileComplete", String.valueOf(newStatus))
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(TEST_USER_ID))
                    .andExpect(jsonPath("$.profileComplete").value(true))
                    .andExpect(jsonPath("$.speciality").value("Cardiology"))
                    .andExpect(jsonPath("$.updatedAt").exists());

            // Verify service method was called with correct parameters
            verify(adminDashboardService).updateUserProfileStatus(TEST_USER_ID, newStatus);
        }

        @Test
        void shouldUpdateProfileStatusToIncompleteSuccessfully() throws Exception {
            // Given
            boolean newStatus = false;
            AdminUserDto updatedUser = new AdminUserDto();
            updatedUser.setId(TEST_USER_ID);
            updatedUser.setProfileComplete(newStatus);
            updatedUser.setRole("PATIENT");
            updatedUser.setUpdatedAt(Instant.now());

            given(adminDashboardService.updateUserProfileStatus(TEST_USER_ID, newStatus)).willReturn(updatedUser);

            // When & Then
            mockMvc.perform(put("/api/admin/users/{userId}/status", TEST_USER_ID)
                            .param("profileComplete", String.valueOf(newStatus))
                            .with(csrf()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(TEST_USER_ID))
                    .andExpect(jsonPath("$.profileComplete").value(false));
        }

        @Test
        void shouldUpdatePatientProfileStatus() throws Exception {
            // Given
            boolean newStatus = true;
            String patientId = "fc6274ff-730a-44f0-9245-17ada9054fe8";
            AdminUserDto updatedPatient = new AdminUserDto();
            updatedPatient.setId(patientId);
            updatedPatient.setPhoneNumber("+1234567890");
            updatedPatient.setProfileComplete(newStatus);
            updatedPatient.setClinicId(null);
            updatedPatient.setSpeciality(null);
            updatedPatient.setRole("PATIENT");
            updatedPatient.setUpdatedAt(Instant.now());

            given(adminDashboardService.updateUserProfileStatus(patientId, newStatus)).willReturn(updatedPatient);

            // When & Then
            mockMvc.perform(put("/api/admin/users/{userId}/status", patientId)
                            .param("profileComplete", "true")
                            .with(csrf()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(patientId))
                    .andExpect(jsonPath("$.profileComplete").value(true))
                    .andExpect(jsonPath("$.role").value("PATIENT"));
        }

        @Test
        void shouldReturnNotFoundForNonExistentUser() throws Exception {
            // Given
            String nonExistentUserId = "fc6274ff-xxxx-44f0-9245-17ada9054fe8";
            given(adminDashboardService.updateUserProfileStatus(nonExistentUserId, true))
                    .willThrow(new UserNotFountException("User not found"));

            // When & Then
            mockMvc.perform(put("/api/admin/users/{userId}/status", nonExistentUserId)
                            .param("profileComplete", "true")
                            .with(csrf()))
                    .andDo(print())
                    .andExpect(status().isNotFound());
        }

        @Test
        void shouldReturnBadRequestWhenMissingProfileCompleteParameter() throws Exception {
            // When & Then
            mockMvc.perform(put("/api/admin/users/{userId}/status", TEST_USER_ID)
                            .with(csrf()))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }
    }

    // ──────────────────────────────────────────────────────────────
    // SECURITY TESTS - Access Control
    // ──────────────────────────────────────────────────────────────
    @Nested
    class SecurityTests {

        @Test
        @WithMockUser(username = "doctor", roles = "DOCTOR")
        void shouldReturnForbiddenForDoctorRole() throws Exception {
            mockMvc.perform(get("/api/admin/dashboard/stats"))
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(username = "patient", roles = "PATIENT")
        void shouldReturnForbiddenForPatientRole() throws Exception {
            mockMvc.perform(get("/api/admin/users"))
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }

        @Test
        void shouldReturnUnauthorizedForUnauthenticatedUser() throws Exception {
            mockMvc.perform(get("/api/admin/dashboard/stats"))
                    .andDo(print())
                    .andExpect(status().isUnauthorized());
        }
    }

    // ──────────────────────────────────────────────────────────────
    // DATA VALIDATION AND EDGE CASES
    // ──────────────────────────────────────────────────────────────
    @Nested
    @WithMockUser(username = ADMIN_USERNAME, roles = "ADMIN")
    class DataValidationAndEdgeCases {



        @Test
        void shouldValidateAllDataTypesInStatsResponse() throws Exception {
            // Given
            given(adminDashboardService.getUserStatistics()).willReturn(userStatsDto);

            // When & Then - Verify data types
            mockMvc.perform(get("/api/admin/dashboard/stats"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalUsers").isNumber())
                    .andExpect(jsonPath("$.totalDoctors").isNumber())
                    .andExpect(jsonPath("$.totalPatients").isNumber())
                    .andExpect(jsonPath("$.completedProfiles").isNumber())
                    .andExpect(jsonPath("$.incompleteProfiles").isNumber())
                    .andExpect(jsonPath("$.newUsersThisWeek").isNumber())
                    .andExpect(jsonPath("$.lastUpdated").isString());
        }

        @Test
        void shouldValidateAllDataTypesInUserResponse() throws Exception {
            // Given
            given(adminDashboardService.getAllUsers()).willReturn(adminUserList);

            // When & Then - Verify data types
            mockMvc.perform(get("/api/admin/users"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").isString())
                    .andExpect(jsonPath("$[0].phoneNumber").isString())
                    .andExpect(jsonPath("$[0].profilePhotoUrl").isString())
                    .andExpect(jsonPath("$[0].profileComplete").isBoolean())
                    .andExpect(jsonPath("$[0].clinicId").isNumber())
                    .andExpect(jsonPath("$[0].speciality").isString())
                    .andExpect(jsonPath("$[0].description").isString())
                    .andExpect(jsonPath("$[0].diploma").isString())
                    .andExpect(jsonPath("$[0].role").isString())
                    .andExpect(jsonPath("$[0].createdAt").isString())
                    .andExpect(jsonPath("$[0].updatedAt").isString());
        }
    }

    // ──────────────────────────────────────────────────────────────
    // PERFORMANCE TESTS
    // ──────────────────────────────────────────────────────────────
    @Nested
    @WithMockUser(username = ADMIN_USERNAME, roles = "ADMIN")
    class PerformanceTests {

        @Test
        void shouldHandleLargeNumberOfUsersEfficiently() throws Exception {
            // Given - Create a list of 50 users
            List<AdminUserDto> largeUserList = createLargeUserList(50);
            given(adminDashboardService.getAllUsers()).willReturn(largeUserList);

            // When & Then
            mockMvc.perform(get("/api/admin/users"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(50)))
                    .andExpect(jsonPath("$[0].id").value("auth0|user0"))
                    .andExpect(jsonPath("$[49].id").value("auth0|user49"));
        }
    }

    // ──────────────────────────────────────────────────────────────
    // HELPER METHODS
    // ──────────────────────────────────────────────────────────────
    private List<AdminUserDto> createLargeUserList(int count) {
        return java.util.stream.IntStream.range(0, count)
                .mapToObj(i -> {
                    AdminUserDto user = new AdminUserDto();
                    user.setId("auth0|user" + i);
                    user.setPhoneNumber("+123456789" + i);
                    user.setProfilePhotoUrl("https://example.com/photo" + i + ".jpg");
                    user.setProfileComplete(i % 2 == 0);
                    user.setClinicId(i % 3 == 0 ? 1L : null);
                    user.setSpeciality(i % 3 == 1 ? "Speciality" + i : null);
                    user.setDescription(i % 2 == 0 ? "Description for user " + i : null);
                    user.setDiploma(i % 3 == 1 ? "Diploma " + i : null);
                    user.setCreatedAt(Instant.now().minusSeconds(i * 86400L));
                    user.setUpdatedAt(Instant.now().minusSeconds(i * 3600L));

                    String role;
                    if (i % 3 == 0) {
                        role = "PATIENT";
                    } else if (i % 3 == 1) {
                        role = "DOCTOR";
                    } else {
                        role = "ADMIN";
                    }
                    user.setRole(role);

                    return user;
                })
                .toList();
    }
}