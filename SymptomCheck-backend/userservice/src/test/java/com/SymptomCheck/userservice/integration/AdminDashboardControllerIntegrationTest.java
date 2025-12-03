package com.SymptomCheck.userservice.integration;

import com.SymptomCheck.userservice.config.KeycloakSecurityConfig;
import com.SymptomCheck.userservice.controllers.AdminDashboardController;
import com.SymptomCheck.userservice.dtos.admindashboarddto.AdminUserDto;
import com.SymptomCheck.userservice.dtos.admindashboarddto.UserStatsDto;
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
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminDashboardController.class)
@Import(KeycloakSecurityConfig.class)
class AdminDashboardControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AdminDashboardService adminDashboardService;

    private Jwt mockAdminJwt;
    private Jwt mockUserJwt;
    private UserStatsDto mockStatsDto;
    private AdminUserDto mockAdminUserDto;
    private List<AdminUserDto> mockUserList;

    @BeforeEach
    void setUp() {
        // Setup admin JWT
        Map<String, Object> adminClaims = new HashMap<>();
        adminClaims.put("sub", "admin-user-id");
        adminClaims.put("preferred_username", "admin");
        adminClaims.put("email", "admin@example.com");
        adminClaims.put("given_name", "Admin");
        adminClaims.put("family_name", "User");
        adminClaims.put("email_verified", true);
        Map<String, Object> adminRealmAccess = new HashMap<>();
        adminRealmAccess.put("roles", Arrays.asList("ADMIN"));
        adminClaims.put("realm_access", adminRealmAccess);

        mockAdminJwt = Jwt.withTokenValue("admin-token")
                .header("alg", "RS256")
                .claims(claims -> claims.putAll(adminClaims))
                .build();

        // Setup regular user JWT (non-admin)
        Map<String, Object> userClaims = new HashMap<>();
        userClaims.put("sub", "regular-user-id");
        userClaims.put("preferred_username", "user");
        userClaims.put("email", "user@example.com");
        userClaims.put("given_name", "Regular");
        userClaims.put("family_name", "User");
        adminClaims.put("email_verified", true);
        Map<String, Object> userRealmAccess = new HashMap<>();
        userRealmAccess.put("roles", Arrays.asList("DOCTOR"));
        userClaims.put("realm_access", userRealmAccess);

        mockUserJwt = Jwt.withTokenValue("user-token")
                .header("alg", "RS256")
                .claims(claims -> claims.putAll(userClaims))
                .build();

        // Setup mock stats DTO
        mockStatsDto = new UserStatsDto();
        mockStatsDto.setTotalUsers(100L);
        mockStatsDto.setTotalDoctors(30L);
        mockStatsDto.setTotalPatients(70L);
        mockStatsDto.setCompletedProfiles(60L);
        mockStatsDto.setIncompleteProfiles(40L);
        mockStatsDto.setNewUsersThisWeek(10L);
        mockStatsDto.setLastUpdated(LocalDateTime.now());

        // Setup mock admin user DTO
        mockAdminUserDto = new AdminUserDto();
        mockAdminUserDto.setId("test-user-id");
        mockAdminUserDto.setPhoneNumber("1234567890");
        mockAdminUserDto.setProfilePhotoUrl("http://example.com/photo.jpg");
        mockAdminUserDto.setProfileComplete(true);
        mockAdminUserDto.setClinicId(1L);
        mockAdminUserDto.setSpeciality("Cardiology");
        mockAdminUserDto.setDescription("Experienced doctor");
        mockAdminUserDto.setDiploma("MD");
        mockAdminUserDto.setRole("DOCTOR");
        mockAdminUserDto.setCreatedAt(Instant.now().minus(30, java.time.temporal.ChronoUnit.DAYS));
        mockAdminUserDto.setUpdatedAt(Instant.now());

        // Setup mock user list
        mockUserList = Arrays.asList(mockAdminUserDto);
    }

    @Nested
    class GetDashboardStatsTests {
        @Test
        @WithMockUser(roles = "ADMIN")
        void getDashboardStats_Success() throws Exception {
            // Given
            when(adminDashboardService.getUserStatistics()).thenReturn(mockStatsDto);

            // When & Then
            mockMvc.perform(get("/api/admin/dashboard/stats")
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalUsers").value(100))
                    .andExpect(jsonPath("$.totalDoctors").value(30))
                    .andExpect(jsonPath("$.totalPatients").value(70))
                    .andExpect(jsonPath("$.completedProfiles").value(60))
                    .andExpect(jsonPath("$.incompleteProfiles").value(40))
                    .andExpect(jsonPath("$.newUsersThisWeek").value(10))
                    .andExpect(jsonPath("$.lastUpdated").exists());
        }

        @Test
        void getDashboardStats_Unauthorized_NoAuthentication() throws Exception {
            // When & Then - No authentication
            mockMvc.perform(get("/api/admin/dashboard/stats"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void getDashboardStats_Forbidden_NonAdminUser() throws Exception {
            // When & Then - Non-admin user trying to access admin endpoint
            mockMvc.perform(get("/api/admin/dashboard/stats")
                            .with(jwt().jwt(mockUserJwt)))
                    .andExpect(status().isForbidden());
        }


    }

    @Nested
    class GetAllUsersTests {
        @Test
        @WithMockUser(roles = {"ADMIN"})
        void getAllUsers_Success() throws Exception {
            // Given
            when(adminDashboardService.getAllUsers()).thenReturn(mockUserList);

            // When & Then
            mockMvc.perform(get("/api/admin/users"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value("test-user-id"))
                    .andExpect(jsonPath("$[0].phoneNumber").value("1234567890"))
                    .andExpect(jsonPath("$[0].profileComplete").value(true))
                    .andExpect(jsonPath("$[0].speciality").value("Cardiology"))
                    .andExpect(jsonPath("$[0].role").value("DOCTOR"));
        }

        @Test
        @WithMockUser(roles = {"ADMIN"})
        void getAllUsers_EmptyList() throws Exception {
            // Given
            when(adminDashboardService.getAllUsers()).thenReturn(List.of());

            // When & Then
            mockMvc.perform(get("/api/admin/users")
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$").isEmpty());
        }

        @Test
        void getAllUsers_Unauthorized_NoAuthentication() throws Exception {
            // When & Then - No authentication
            mockMvc.perform(get("/api/admin/users"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockUser(roles = {"DOCTOR"})
        void getAllUsers_Forbidden_NonAdminUser() throws Exception {
            // When & Then - Non-admin user trying to access admin endpoint
            mockMvc.perform(get("/api/admin/users")
                    )
                    .andExpect(status().isForbidden());
        }


    }

    @Nested
    class GetUsersByRoleTests {
        @Test
        @WithMockUser(roles = {"ADMIN"})
        void getUsersByRole_Success_DoctorRole() throws Exception {
            // Given
            String role = "DOCTOR";
            when(adminDashboardService.getUsersByRole(role)).thenReturn(mockUserList);

            // When & Then
            mockMvc.perform(get("/api/admin/users/role/{role}", role)
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value("test-user-id"))
                    .andExpect(jsonPath("$[0].role").value("DOCTOR"));
        }

        @Test
        @WithMockUser(roles = {"ADMIN"})
        void getUsersByRole_Success_PatientRole() throws Exception {
            // Given
            String role = "PATIENT";

            // Create a patient user
            AdminUserDto patientUser = new AdminUserDto();
            patientUser.setId("patient-id");
            patientUser.setRole("PATIENT");
            patientUser.setProfileComplete(false);

            when(adminDashboardService.getUsersByRole(role)).thenReturn(Arrays.asList(patientUser));

            // When & Then
            mockMvc.perform(get("/api/admin/users/role/{role}", role)
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value("patient-id"))
                    .andExpect(jsonPath("$[0].role").value("PATIENT"));
        }

        @Test
        @WithMockUser(roles = {"ADMIN"})
        void getUsersByRole_Success_AllUsers() throws Exception {
            // Given
            String role = "ALL";
            when(adminDashboardService.getUsersByRole(role)).thenReturn(mockUserList);

            // When & Then
            mockMvc.perform(get("/api/admin/users/role/{role}", role)
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value("test-user-id"));
        }

        @Test
        @WithMockUser(roles = {"ADMIN"})
        void getUsersByRole_EmptyResult() throws Exception {
            // Given
            String role = "NURSE"; // Non-existent role
            when(adminDashboardService.getUsersByRole(role)).thenReturn(List.of());

            // When & Then
            mockMvc.perform(get("/api/admin/users/role/{role}", role)
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$").isEmpty());
        }

        @Test
        @WithMockUser(roles = {"ADMIN"})
        void getUsersByRole_CaseInsensitiveRole() throws Exception {
            // Given
            String role = "doctor"; // Lowercase
            when(adminDashboardService.getUsersByRole(role)).thenReturn(mockUserList);

            // When & Then
            mockMvc.perform(get("/api/admin/users/role/{role}", role)
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].role").value("DOCTOR"));
        }

        @Test
        void getUsersByRole_Unauthorized_NoAuthentication() throws Exception {
            // Given
            String role = "DOCTOR";

            // When & Then - No authentication
            mockMvc.perform(get("/api/admin/users/role/{role}", role))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void getUsersByRole_Forbidden_NonAdminUser() throws Exception {
            // Given
            String role = "DOCTOR";

            // When & Then - Non-admin user trying to access admin endpoint
            mockMvc.perform(get("/api/admin/users/role/{role}", role)
                            .with(jwt().jwt(mockUserJwt)))
                    .andExpect(status().isForbidden());
        }


        @Test
        @WithMockUser(roles = {"ADMIN"})
        void getUsersByRole_InvalidRoleSpecialCharacters() throws Exception {
            // Given
            String role = "DOCTOR@#$"; // Role with special characters
            when(adminDashboardService.getUsersByRole(role)).thenReturn(mockUserList);

            // When & Then
            mockMvc.perform(get("/api/admin/users/role/{role}", role)
                    )
                    .andExpect(status().isOk());
        }

        @Test
        @WithMockUser(roles = {"ADMIN"})
        void getUsersByRole_EmptyRoleParameter() throws Exception {
            // Given - Empty role string
            String role = "";

            // When & Then
            mockMvc.perform(get("/api/admin/users/role/{role}", role)
                    )
                    .andExpect(status().isNotFound()); // Service will handle empty role as "ALL"
        }
    }

    @Nested
    class UpdateUserProfileStatusTests {
        @Test
        @WithMockUser(roles = {"ADMIN"})
        void updateUserProfileStatus_Success_EnableProfile() throws Exception {
            // Given
            String userId = "test-user-id";
            boolean profileComplete = true;
            when(adminDashboardService.updateUserProfileStatus(userId, profileComplete))
                    .thenReturn(mockAdminUserDto);

            // When & Then
            mockMvc.perform(put("/api/admin/users/{userId}/status", userId)
                            .param("profileComplete", "true")
                         )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(userId))
                    .andExpect(jsonPath("$.profileComplete").value(true));
        }

        @Test
        @WithMockUser(roles = {"ADMIN"})
        void updateUserProfileStatus_Success_DisableProfile() throws Exception {
            // Given
            String userId = "test-user-id";
            boolean profileComplete = false;

            // Create user with disabled profile
            AdminUserDto disabledUser = new AdminUserDto();
            disabledUser.setId(userId);
            disabledUser.setProfileComplete(false);
            disabledUser.setRole("DOCTOR");

            when(adminDashboardService.updateUserProfileStatus(userId, profileComplete))
                    .thenReturn(disabledUser);

            // When & Then
            mockMvc.perform(put("/api/admin/users/{userId}/status", userId)
                            .param("profileComplete", "false")
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(userId))
                    .andExpect(jsonPath("$.profileComplete").value(false));
        }

        @Test
        @WithMockUser(roles = {"ADMIN"})
        void updateUserProfileStatus_UserNotFound() throws Exception {
            // Given
            String userId = "non-existent-user-id";
            boolean profileComplete = true;

            when(adminDashboardService.updateUserProfileStatus(userId, profileComplete))
                    .thenThrow(new UserNotFountException("User not found"));

            // When & Then
            mockMvc.perform(put("/api/admin/users/{userId}/status", userId)
                            .param("profileComplete", "true")
                    )
                    .andExpect(status().isNotFound());
        }

        @Test
        @WithMockUser(roles = {"ADMIN"})
        void updateUserProfileStatus_EmptyUserId() throws Exception {
            // Given - Empty user ID
            String userId = "";

            // When & Then
            mockMvc.perform(put("/api/admin/users/{userId}/status", userId)
                            .param("profileComplete", "true")
                    )
                    .andExpect(status().isBadRequest()); // Spring will return 404 for empty path variable
        }

        @Test
        @WithMockUser(roles = {"ADMIN"})
        void updateUserProfileStatus_MissingProfileCompleteParameter() throws Exception {
            // Given
            String userId = "test-user-id";

            // When & Then - Missing required parameter
            mockMvc.perform(put("/api/admin/users/{userId}/status", userId)
                    )
                    .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser(roles = {"ADMIN"})
        void updateUserProfileStatus_InvalidProfileCompleteParameter() throws Exception {
            // Given
            String userId = "test-user-id";

            // When & Then - Invalid boolean parameter
            mockMvc.perform(put("/api/admin/users/{userId}/status", userId)
                            .param("profileComplete", "not-a-boolean")
                    )
                    .andExpect(status().isBadRequest());
        }

        @Test
        void updateUserProfileStatus_Unauthorized_NoAuthentication() throws Exception {
            // Given
            String userId = "test-user-id";

            // When & Then - No authentication
            mockMvc.perform(put("/api/admin/users/{userId}/status", userId)
                            .param("profileComplete", "true"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void updateUserProfileStatus_Forbidden_NonAdminUser() throws Exception {
            // Given
            String userId = "test-user-id";

            // When & Then - Non-admin user trying to access admin endpoint
            mockMvc.perform(put("/api/admin/users/{userId}/status", userId)
                            .param("profileComplete", "true")
                            .with(jwt().jwt(mockUserJwt)))
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(roles = {"ADMIN"})
        void updateUserProfileStatus_ServiceException() throws Exception {
            // Given
            String userId = "test-user-id";
            boolean profileComplete = true;

            when(adminDashboardService.updateUserProfileStatus(userId, profileComplete))
                    .thenThrow(new RuntimeException("Database error"));

            // When & Then
            mockMvc.perform(put("/api/admin/users/{userId}/status", userId)
                            .param("profileComplete", "true")
                    )
                    .andExpect(status().isInternalServerError());
        }

        @Test
        @WithMockUser(roles = {"ADMIN"})
        void updateUserProfileStatus_SpecialCharactersInUserId() throws Exception {
            // Given
            String userId = "user-id-with-special@#$";
            boolean profileComplete = true;

            when(adminDashboardService.updateUserProfileStatus(userId, profileComplete))
                    .thenReturn(mockAdminUserDto);

            // When & Then
            mockMvc.perform(put("/api/admin/users/{userId}/status", userId)
                            .param("profileComplete", "true")
                    )
                    .andExpect(status().isOk());
        }

        @Test
        @WithMockUser(roles = {"ADMIN"})
        void updateUserProfileStatus_LongUserId() throws Exception {
            // Given
            String userId = "a".repeat(100); // Very long user ID
            boolean profileComplete = true;

            when(adminDashboardService.updateUserProfileStatus(userId, profileComplete))
                    .thenReturn(mockAdminUserDto);

            // When & Then
            mockMvc.perform(put("/api/admin/users/{userId}/status", userId)
                            .param("profileComplete", "true")
                    )
                    .andExpect(status().isOk());
        }
    }

    @Nested
    class SecurityTests {
        @Test
        void allEndpoints_RequireAuthentication() throws Exception {
            // Test all endpoints without authentication
            mockMvc.perform(get("/api/admin/dashboard/stats"))
                    .andExpect(status().isUnauthorized());

            mockMvc.perform(get("/api/admin/users"))
                    .andExpect(status().isUnauthorized());

            mockMvc.perform(get("/api/admin/users/role/DOCTOR"))
                    .andExpect(status().isUnauthorized());

            mockMvc.perform(put("/api/admin/users/test-id/status")
                            .param("profileComplete", "true"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void allEndpoints_RequireAdminRole() throws Exception {
            // Test all endpoints with non-admin user
            mockMvc.perform(get("/api/admin/dashboard/stats")
                            .with(jwt().jwt(mockUserJwt)))
                    .andExpect(status().isForbidden());

            mockMvc.perform(get("/api/admin/users")
                            .with(jwt().jwt(mockUserJwt)))
                    .andExpect(status().isForbidden());

            mockMvc.perform(get("/api/admin/users/role/DOCTOR")
                            .with(jwt().jwt(mockUserJwt)))
                    .andExpect(status().isForbidden());

            mockMvc.perform(put("/api/admin/users/test-id/status")
                            .param("profileComplete", "true")
                            .with(jwt().jwt(mockUserJwt)))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    class ValidationTests {


        @Nested
        class PerformanceAndEdgeCases {
            @Test
            @WithMockUser(roles = {"ADMIN"})
            void multipleConsecutiveCalls_Success() throws Exception {
                // Given
                when(adminDashboardService.getUserStatistics()).thenReturn(mockStatsDto);
                when(adminDashboardService.getAllUsers()).thenReturn(mockUserList);
                when(adminDashboardService.getUsersByRole("DOCTOR")).thenReturn(mockUserList);

                // When & Then - Make multiple consecutive calls
                mockMvc.perform(get("/api/admin/dashboard/stats")
                        )
                        .andExpect(status().isOk());

                mockMvc.perform(get("/api/admin/users")
                        )
                        .andExpect(status().isOk());

                mockMvc.perform(get("/api/admin/users/role/DOCTOR")
                        )
                        .andExpect(status().isOk());
            }

            @Test
            @WithMockUser(roles = {"ADMIN"})
            void updateUserProfileStatus_ToggleMultipleTimes() throws Exception {
                // Given
                String userId = "test-user-id";

                // First call - enable profile
                AdminUserDto enabledUser = new AdminUserDto();
                enabledUser.setId(userId);
                enabledUser.setProfileComplete(true);

                // Second call - disable profile
                AdminUserDto disabledUser = new AdminUserDto();
                disabledUser.setId(userId);
                disabledUser.setProfileComplete(false);

                when(adminDashboardService.updateUserProfileStatus(userId, true))
                        .thenReturn(enabledUser);
                when(adminDashboardService.updateUserProfileStatus(userId, false))
                        .thenReturn(disabledUser);

                // When & Then - First call
                mockMvc.perform(put("/api/admin/users/{userId}/status", userId)
                                .param("profileComplete", "true")
                        )
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.profileComplete").value(true));

                // When & Then - Second call
                mockMvc.perform(put("/api/admin/users/{userId}/status", userId)
                                .param("profileComplete", "false")
                        )
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.profileComplete").value(false));
            }
        }
    }
}