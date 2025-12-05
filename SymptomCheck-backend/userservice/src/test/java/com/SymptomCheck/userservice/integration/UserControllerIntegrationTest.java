package com.SymptomCheck.userservice.integration;

import com.SymptomCheck.userservice.config.KeycloakSecurityConfig;
import com.SymptomCheck.userservice.controllers.UserController;
import com.SymptomCheck.userservice.dtos.DoctorProfileDto;
import com.SymptomCheck.userservice.dtos.UserRegistrationRequest;
import com.SymptomCheck.userservice.dtos.UserUpdateDto;
import com.SymptomCheck.userservice.models.UserData;
import com.SymptomCheck.userservice.services.KeycloakService;
import com.SymptomCheck.userservice.services.LocalFileStorageService;
import com.SymptomCheck.userservice.services.UserDataService;
import com.SymptomCheck.userservice.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import(KeycloakSecurityConfig.class)
@ActiveProfiles("test")
@Testcontainers
class UserControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private UserService userService;
    @MockBean
    private UserDataService userDataService;
    @MockBean
    private KeycloakService keycloakService;
    @MockBean
    private LocalFileStorageService localFileStorageService;

    // UUID constants for test users
    protected static final UUID TEST_USER_UUID = UUID.fromString("fc6274ff-730a-44f0-9245-17ada9054fe8");
    protected static final UUID ADMIN_USER_UUID = UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890");
    protected static final UUID OTHER_USER_UUID = UUID.fromString("d4e5f6a7-b8c9-4d01-ef23-4567890abc12");
    protected static final UUID DIFFERENT_USER_UUID = UUID.fromString("12345678-90ab-cdef-1234-567890abcdef");

    // Base test data - shared across all nested test classes
    protected UserRegistrationRequest testUserRegistration;
    protected UserUpdateDto testUserUpdateDto;
    protected DoctorProfileDto testDoctorProfileDto;
    protected UserData testUserData;
    protected Jwt mockJwt;
    protected Jwt mockAdminJwt;

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test")
            .withReuse(true);

    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.jpa.properties.hibernate.dialect", () -> "org.hibernate.dialect.PostgreSQLDialect");
        registry.add("spring.sql.init.mode", () -> "always");
    }


    @BeforeEach
    void setUpBaseData() {
        // Setup test user registration request
        testUserRegistration = new UserRegistrationRequest();
        testUserRegistration.setUsername("testuser");
        testUserRegistration.setEmail("test@example.com");
        testUserRegistration.setPassword("password123");
        testUserRegistration.setFirstName("John");
        testUserRegistration.setLastName("Doe");
        testUserRegistration.setRole("DOCTOR");
        testUserRegistration.setPhoneNumber("1234567890");
        testUserRegistration.setProfileComplete(true);

        // Setup user update DTO
        testUserUpdateDto = new UserUpdateDto();
        testUserUpdateDto.setFirstName("Jane");
        testUserUpdateDto.setLastName("Smith");
        testUserUpdateDto.setEmail("jane@example.com");
        testUserUpdateDto.setPhoneNumber("0987654321");

        // Setup doctor profile DTO - FIXED: Set the ID field
        testDoctorProfileDto = new DoctorProfileDto();
        testDoctorProfileDto.setId(TEST_USER_UUID.toString()); // THIS WAS MISSING
        testDoctorProfileDto.setSpeciality("Cardiology");
        testDoctorProfileDto.setDescription("Experienced cardiologist");
        testDoctorProfileDto.setDiploma("MD, Cardiology");
        testDoctorProfileDto.setClinicId(1L);
        testDoctorProfileDto.setProfilePhotoUrl("http://example.com/photo.jpg");

        // Setup user data with UUID
        testUserData = new UserData();
        testUserData.setId(TEST_USER_UUID.toString());
        testUserData.setPhoneNumber("1234567890");
        testUserData.setProfilePhotoUrl("http://example.com/photo.jpg");
        testUserData.setProfileComplete(true);
        testUserData.setClinicId(1L);
        testUserData.setSpeciality("Cardiology");
        testUserData.setDescription("Experienced doctor");
        testUserData.setDiploma("MD");
        testUserData.setCreatedAt(Instant.now());
        testUserData.setUpdatedAt(Instant.now());

        // Setup regular user JWT with UUID
        Map<String, Object> userClaims = new HashMap<>();
        userClaims.put("sub", TEST_USER_UUID.toString());
        userClaims.put("preferred_username", "testuser");
        userClaims.put("email", "test@example.com");
        userClaims.put("given_name", "John");
        userClaims.put("family_name", "Doe");
        userClaims.put("email_verified", true);
        Map<String, Object> realmAccess = new HashMap<>();
        realmAccess.put("roles", Arrays.asList("DOCTOR"));
        userClaims.put("realm_access", realmAccess);

        mockJwt = Jwt.withTokenValue("test-token")
                .header("alg", "RS256")
                .claims(claims -> claims.putAll(userClaims))
                .build();

        // Setup admin JWT with UUID
        Map<String, Object> adminClaims = new HashMap<>();
        adminClaims.put("sub", ADMIN_USER_UUID.toString());
        adminClaims.put("preferred_username", "admin");
        adminClaims.put("email", "admin@example.com");
        adminClaims.put("given_name", "Admin");
        adminClaims.put("family_name", "User");
        adminClaims.put("email_verified", true);
        Map<String, Object> adminRealmAccess = new HashMap<>();
        adminRealmAccess.put("roles", Arrays.asList("ADMIN", "DOCTOR"));
        adminClaims.put("realm_access", adminRealmAccess);

        mockAdminJwt = Jwt.withTokenValue("admin-token")
                .header("alg", "RS256")
                .claims(claims -> claims.putAll(adminClaims))
                .build();
    }

    // Helper method to create JWT for different users with UUID
    protected Jwt createJwtForUser(UUID userId, String username, List<String> roles) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", userId.toString());
        claims.put("preferred_username", username);
        claims.put("email", username + "@example.com");
        claims.put("given_name", "Test");
        claims.put("family_name", "User");
        claims.put("email_verified", true);
        Map<String, Object> realmAccess = new HashMap<>();
        realmAccess.put("roles", roles);
        claims.put("realm_access", realmAccess);

        return Jwt.withTokenValue("token-" + userId)
                .header("alg", "RS256")
                .claims(c -> c.putAll(claims))
                .build();
    }

    @Nested
    class RegisterUserTests {
        @Test
        void registerUser_Success() throws Exception {
            // Given
            when(userService.registerMyUser(any(UserRegistrationRequest.class), any()))
                    .thenReturn(TEST_USER_UUID.toString());

            // Create multipart request
            MockMultipartFile userPart = new MockMultipartFile(
                    "user",
                    "",
                    "application/json",
                    objectMapper.writeValueAsBytes(testUserRegistration)
            );
            MockMultipartFile filePart = new MockMultipartFile(
                    "file",
                    "test.jpg",
                    MediaType.IMAGE_JPEG_VALUE,
                    "test image content".getBytes()
            );

            // When & Then
            mockMvc.perform(multipart("/api/v1/users/register")
                            .file(userPart)
                            .file(filePart)
                            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("User registered successfully in Keycloak with ALL attributes"))
                    .andExpect(jsonPath("$.keycloakUserId").value(TEST_USER_UUID.toString()))
                    .andExpect(jsonPath("$.username").value(testUserRegistration.getUsername()));
        }

        @Test
        void registerUser_WithoutFile() throws Exception {
            // Given
            when(userService.registerMyUser(any(UserRegistrationRequest.class), any()))
                    .thenReturn(TEST_USER_UUID.toString());

            MockMultipartFile userPart = new MockMultipartFile(
                    "user",
                    "",
                    "application/json",
                    objectMapper.writeValueAsBytes(testUserRegistration)
            );

            // When & Then - No file provided
            mockMvc.perform(multipart("/api/v1/users/register")
                            .file(userPart)
                            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                    .andExpect(status().isOk());
        }

        @Test
        void registerUser_ValidationError() throws Exception {
            // Given - create invalid user (missing required fields)
            UserRegistrationRequest invalidUser = new UserRegistrationRequest();
            invalidUser.setUsername(""); // Empty username

            MockMultipartFile userPart = new MockMultipartFile(
                    "user",
                    "",
                    "application/json",
                    objectMapper.writeValueAsBytes(invalidUser)
            );

            // When & Then
            mockMvc.perform(multipart("/api/v1/users/register")
                            .file(userPart)
                            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void registerUser_ServiceException() throws Exception {
            // Given
            when(userService.registerMyUser(any(UserRegistrationRequest.class), any()))
                    .thenThrow(new RuntimeException("Keycloak service error"));

            MockMultipartFile userPart = new MockMultipartFile(
                    "user",
                    "",
                    "application/json",
                    objectMapper.writeValueAsBytes(testUserRegistration)
            );

            // When & Then
            mockMvc.perform(multipart("/api/v1/users/register")
                            .file(userPart)
                            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").exists());
        }
    }

    @Nested
    class GetUserDetailsTests {
        @Test
        void getUserDetails_Success() throws Exception {
            // Given
            String username = "testuser";
            Map<String, Object> userDetails = new HashMap<>();
            userDetails.put("id", TEST_USER_UUID.toString());
            userDetails.put("username", username);
            userDetails.put("email", "test@example.com");
            userDetails.put("firstName", "John");
            userDetails.put("lastName", "Doe");

            when(userService.getUserDetails(username)).thenReturn(userDetails);

            // When & Then
            mockMvc.perform(get("/api/v1/users/details/{username}", username))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.username").value(username))
                    .andExpect(jsonPath("$.email").value("test@example.com"))
                    .andExpect(jsonPath("$.firstName").value("John"))
                    .andExpect(jsonPath("$.lastName").value("Doe"));
        }

        @Test
        void getUserDetails_NotFound() throws Exception {
            // Given
            String username = "nonexistent";
            when(userService.getUserDetails(username)).thenReturn(null);

            // When & Then
            mockMvc.perform(get("/api/v1/users/details/{username}", username))
                    .andExpect(status().isNotFound());
        }

        @Test
        void getUserDetails_ServiceException() throws Exception {
            // Given
            String username = "testuser";
            when(userService.getUserDetails(username))
                    .thenThrow(new RuntimeException("Service error"));

            // When & Then
            mockMvc.perform(get("/api/v1/users/details/{username}", username))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").exists());
        }
    }

    @Nested
    class GetCurrentUserTests {
        @Test
        @WithMockUser
        void getCurrentUser_Success_WithUserData() throws Exception {
            // Given
            when(userDataService.getUserDataById(TEST_USER_UUID.toString()))
                    .thenReturn(Optional.of(testUserData));

            // When & Then
            mockMvc.perform(get("/api/v1/users/me")
                            .with(jwt().jwt(mockJwt)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(TEST_USER_UUID.toString()))
                    .andExpect(jsonPath("$.username").value("testuser"))
                    .andExpect(jsonPath("$.email").value("test@example.com"))
                    .andExpect(jsonPath("$.firstName").value("John"))
                    .andExpect(jsonPath("$.lastName").value("Doe"))
                    .andExpect(jsonPath("$.emailVerified").value(true))
                    .andExpect(jsonPath("$.roles").isArray())
                    .andExpect(jsonPath("$.phoneNumber").value("1234567890"))
                    .andExpect(jsonPath("$.profilePhotoUrl").value("http://example.com/photo.jpg"))
                    .andExpect(jsonPath("$.isProfileComplete").value(true))
                    .andExpect(jsonPath("$.clinicId").value(1))
                    .andExpect(jsonPath("$.speciality").value("Cardiology"));
        }

        @Test
        @WithMockUser
        void getCurrentUser_Success_WithoutUserData() throws Exception {
            // Given
            when(userDataService.getUserDataById(TEST_USER_UUID.toString()))
                    .thenReturn(Optional.empty());

            // When & Then
            mockMvc.perform(get("/api/v1/users/me")
                            .with(jwt().jwt(mockJwt)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(TEST_USER_UUID.toString()))
                    .andExpect(jsonPath("$.username").value("testuser"))
                    .andExpect(jsonPath("$.email").value("test@example.com"))
                    .andExpect(jsonPath("$.firstName").value("John"))
                    .andExpect(jsonPath("$.lastName").value("Doe"))
                    .andExpect(jsonPath("$.phoneNumber").isEmpty())
                    .andExpect(jsonPath("$.isProfileComplete").value(false));
        }

        @Test
        void getCurrentUser_Unauthorized() throws Exception {
            // When & Then - No authentication provided
            mockMvc.perform(get("/api/v1/users/me"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockUser
        void getCurrentUser_WithAdminRole() throws Exception {
            // Given
            when(userDataService.getUserDataById(ADMIN_USER_UUID.toString()))
                    .thenReturn(Optional.empty());

            // When & Then
            mockMvc.perform(get("/api/v1/users/me")
                            .with(jwt().jwt(mockAdminJwt)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(ADMIN_USER_UUID.toString()))
                    .andExpect(jsonPath("$.username").value("admin"))
                    .andExpect(jsonPath("$.roles").isArray());
        }
    }

    @Nested
    class DisableUserTests {
        @Test
        @WithMockUser(roles = "ADMIN")
        void disableUser_Success_AsAdmin() throws Exception {
            // Given
            String userId = TEST_USER_UUID.toString();
            // Note: The service method returns void, so we don't need to mock anything

            // When & Then
            mockMvc.perform(patch("/api/v1/users/disable/{userId}", userId)
                            .param("isEnable", "false")
            )
                    .andExpect(status().isOk())
                    .andExpect(content().string("true"));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void disableUser_Success_AsAdmin_EnableUser() throws Exception {
            // Given
            String userId = TEST_USER_UUID.toString();

            // When & Then - Enable user (isEnable = true)
            mockMvc.perform(patch("/api/v1/users/disable/{userId}", userId)
                            .param("isEnable", "true")
            )
                    .andExpect(status().isOk())
                    .andExpect(content().string("true"));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void disableUser_InternalServerError() throws Exception {
            String userId = TEST_USER_UUID.toString();

            doThrow(new RuntimeException("Keycloak error"))
                    .when(userService)
                    .disableUser(userId, false);

            mockMvc.perform(patch("/api/v1/users/disable/{userId}", userId)
                            .param("isEnable", "false"))
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().string("false"));
        }


        @Test
        void disableUser_Unauthorized() throws Exception {
            // Given
            String userId = TEST_USER_UUID.toString();

            // When & Then - No authentication
            mockMvc.perform(patch("/api/v1/users/disable/{userId}", userId)
                            .param("isEnable", "false"))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    class GetUsersByRoleTests {
        @Test
        void getUsersByRole_Success() throws Exception {
            // Given
            String role = "DOCTOR";
            List<UserRegistrationRequest> users = Arrays.asList(testUserRegistration);
            when(userService.getUsersByRole(role)).thenReturn(users);

            // When & Then
            mockMvc.perform(get("/api/v1/users/by-role")
                            .param("role", role)
                            .with(jwt().jwt(mockAdminJwt)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].username").value("testuser"))
                    .andExpect(jsonPath("$[0].email").value("test@example.com"))
                    .andExpect(jsonPath("$[0].role").value("DOCTOR"));
        }

        @Test
        void getUsersByRole_InternalServerError() throws Exception {
            // Given
            String role = "DOCTOR";
            when(userService.getUsersByRole(role))
                    .thenThrow(new RuntimeException("Service error"));

            // When & Then
            mockMvc.perform(get("/api/v1/users/by-role")
                            .param("role", role)
                            .with(jwt().jwt(mockAdminJwt)))
                    .andExpect(status().isInternalServerError());
        }

        @Test
        void getUsersByRole_Unauthorized() throws Exception {
            // Given
            String role = "DOCTOR";

            // When & Then - No authentication
            mockMvc.perform(get("/api/v1/users/by-role")
                            .param("role", role))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void getUsersByRole_EmptyResult() throws Exception {
            // Given
            String role = "NURSE";
            when(userService.getUsersByRole(role)).thenReturn(Collections.emptyList());

            // When & Then
            mockMvc.perform(get("/api/v1/users/by-role")
                            .param("role", role)
                            .with(jwt().jwt(mockAdminJwt)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$").isEmpty());
        }
    }

    @Nested
    class GetUserByIdTests {
        @Test
        void getUserById_Success() throws Exception {
            // Given
            String userId = TEST_USER_UUID.toString();
            when(userService.getUserById(userId)).thenReturn(testUserRegistration);

            // When & Then
            mockMvc.perform(get("/api/v1/users/{userId}", userId)
                            .with(jwt().jwt(mockAdminJwt)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.username").value("testuser"))
                    .andExpect(jsonPath("$.email").value("test@example.com"));
        }

        @Test
        void getUserById_InternalServerError() throws Exception {
            // Given
            String userId = TEST_USER_UUID.toString();
            when(userService.getUserById(userId))
                    .thenThrow(new RuntimeException("Service error"));

            // When & Then
            mockMvc.perform(get("/api/v1/users/{userId}", userId)
                            .with(jwt().jwt(mockAdminJwt)))
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().string("false"));
        }

        @Test
        void getUserById_Unauthorized() throws Exception {
            // Given
            String userId = TEST_USER_UUID.toString();

            // When & Then - No authentication
            mockMvc.perform(get("/api/v1/users/{userId}", userId))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    class UpdateUserTests {
        @Test
        void updateUser_Success_OwnProfile() throws Exception {
            // Given
            String userId = TEST_USER_UUID.toString();
            when(userService.updateUser(eq(userId), any(UserUpdateDto.class)))
                    .thenReturn(testUserRegistration);

            // When & Then - User updating their own profile
            mockMvc.perform(put("/api/v1/users/{userId}", userId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testUserUpdateDto))
                            .with(jwt().jwt(mockJwt))) // Same user ID as path variable
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("User updated successfully"))
                    .andExpect(jsonPath("$.user").exists());
        }

        @Test
        void updateUser_Success_AdminUpdatingOtherProfile() throws Exception {
            // Given
            String userId = TEST_USER_UUID.toString(); // Different from admin ID
            when(userService.updateUser(eq(userId), any(UserUpdateDto.class)))
                    .thenReturn(testUserRegistration);

            // When & Then - Admin updating another user's profile
            mockMvc.perform(put("/api/v1/users/{userId}", userId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testUserUpdateDto))
                            .with(jwt().jwt(mockAdminJwt))) // Admin JWT
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("User updated successfully"));
        }

        @Test
        void updateUser_Forbidden_UserUpdatingOtherProfile() throws Exception {
            // Given
            String userId = DIFFERENT_USER_UUID.toString(); // Different from logged-in user
            Jwt otherUserJwt = createJwtForUser(OTHER_USER_UUID, "otheruser", Arrays.asList("DOCTOR"));

            // When & Then - User trying to update someone else's profile
            mockMvc.perform(put("/api/v1/users/{userId}", userId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testUserUpdateDto))
                            .with(jwt().jwt(otherUserJwt)))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.error").value("You don't have permission to update this profile"));
        }

        @Test
        void updateUser_Unauthorized() throws Exception {
            // Given
            String userId = TEST_USER_UUID.toString();

            // When & Then - No authentication
            mockMvc.perform(put("/api/v1/users/{userId}", userId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testUserUpdateDto)))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void updateUser_InvalidUserId_Empty() throws Exception {
            // Given - Invalid user ID (empty string)
            String invalidUserId = "";

            // When & Then - Note: This will be treated as a path variable, not as userId in controller
            // The controller receives the empty string from @PathVariable
            mockMvc.perform(put("/api/v1/users/{userId}", invalidUserId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testUserUpdateDto))
                            .with(jwt().jwt(mockJwt)))
                    .andExpect(status().isNotFound()); // This should be 400, not 404
        }

        @Test
        void updateUser_InvalidUserId_Null() throws Exception {
            // Given - Null user ID (as string "null")
            String invalidUserId = "null";

            // When & Then - This is a valid string "null", not a null value
            mockMvc.perform(put("/api/v1/users/{userId}", invalidUserId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testUserUpdateDto))
                            .with(jwt().jwt(mockJwt)))
                    .andExpect(status().isForbidden()); // User "null" is different from current user
        }

        @Test
        void updateUser_NanUserId() throws Exception {
            // Given - NaN user ID
            String nanUserId = "NaN";

            // When & Then
            mockMvc.perform(put("/api/v1/users/{userId}", nanUserId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testUserUpdateDto))
                            .with(jwt().jwt(mockJwt)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("Invalid user ID"))
                    .andExpect(jsonPath("$.message").value("User ID cannot be NaN"));
        }

        @Test
        void updateUser_InvalidUUIDFormat() throws Exception {
            // Given - Invalid UUID format
            String invalidUserId = "not-a-valid-uuid";

            // When & Then - This is treated as a regular string, controller checks permission
            mockMvc.perform(put("/api/v1/users/{userId}", invalidUserId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testUserUpdateDto))
                            .with(jwt().jwt(mockJwt)))
                    .andExpect(status().isForbidden()); // Different user ID, so permission denied
        }

        @Test
        void updateUser_ValidationError() throws Exception {
            // Given - Invalid update DTO
            String userId = TEST_USER_UUID.toString();
            UserUpdateDto invalidUpdateDto = new UserUpdateDto();
            invalidUpdateDto.setEmail("invalid-email"); // Invalid email format
            invalidUpdateDto.setFirstName(""); // Empty first name
            invalidUpdateDto.setLastName(""); // Empty last name

            // When & Then
            mockMvc.perform(put("/api/v1/users/{userId}", userId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidUpdateDto))
                            .with(jwt().jwt(mockJwt)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void updateUser_ServiceException() throws Exception {
            // Given
            String userId = TEST_USER_UUID.toString();
            when(userService.updateUser(eq(userId), any(UserUpdateDto.class)))
                    .thenThrow(new RuntimeException("Keycloak update failed"));

            // When & Then
            mockMvc.perform(put("/api/v1/users/{userId}", userId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testUserUpdateDto))
                            .with(jwt().jwt(mockJwt)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").exists());
        }
    }

    @Nested
    class CompleteDoctorProfileTests {
        @Test
        @WithMockUser
        void completeDoctorProfile_Success() throws Exception {
            // Given
            String userId = TEST_USER_UUID.toString();
            when(userService.completeDoctorProfile(eq(userId), any(DoctorProfileDto.class)))
                    .thenReturn(testUserData);

            // When & Then - Make sure DoctorProfileDto has the ID set
            mockMvc.perform(put("/api/v1/users/{userId}/complete-profile", userId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testDoctorProfileDto))
                            .with(jwt().jwt(mockJwt)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Doctor profile completed successfully"))
                    .andExpect(jsonPath("$.userData").exists());
        }

        @Test
        void completeDoctorProfile_Forbidden() throws Exception {
            // Given
            String userId = DIFFERENT_USER_UUID.toString();
            Jwt otherUserJwt = createJwtForUser(OTHER_USER_UUID, "otheruser", Arrays.asList("DOCTOR"));

            // Create DTO with ID for the target user - FIXED: Set the ID
            DoctorProfileDto dtoForDifferentUser = new DoctorProfileDto();
            dtoForDifferentUser.setId(DIFFERENT_USER_UUID.toString());
            dtoForDifferentUser.setSpeciality("Cardiology");
            dtoForDifferentUser.setDescription("Experienced cardiologist");
            dtoForDifferentUser.setDiploma("MD, Cardiology");
            dtoForDifferentUser.setClinicId(1L);
            dtoForDifferentUser.setProfilePhotoUrl("http://example.com/photo.jpg");

            // When & Then
            mockMvc.perform(put("/api/v1/users/{userId}/complete-profile", userId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dtoForDifferentUser))
                            .with(jwt().jwt(otherUserJwt)))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.error").value("You don't have permission to update this profile"));
        }

        @Test
        void completeDoctorProfile_AdminCanUpdateAnyProfile() throws Exception {
            // Given
            String userId = TEST_USER_UUID.toString();
            when(userService.completeDoctorProfile(eq(userId), any(DoctorProfileDto.class)))
                    .thenReturn(testUserData);

            // When & Then - Admin updating another user's profile
            mockMvc.perform(put("/api/v1/users/{userId}/complete-profile", userId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testDoctorProfileDto))
                            .with(jwt().jwt(mockAdminJwt)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Doctor profile completed successfully"));
        }

        @Test
        void completeDoctorProfile_ValidationError_MissingFields() throws Exception {
            // Given
            String userId = TEST_USER_UUID.toString();
            DoctorProfileDto invalidDto = new DoctorProfileDto();
            invalidDto.setId(TEST_USER_UUID.toString()); // Only ID is set, missing required fields

            // When & Then
            mockMvc.perform(put("/api/v1/users/{userId}/complete-profile", userId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidDto))
                            .with(jwt().jwt(mockJwt)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void completeDoctorProfile_ServiceException() throws Exception {
            // Given
            String userId = TEST_USER_UUID.toString();
            when(userService.completeDoctorProfile(eq(userId), any(DoctorProfileDto.class)))
                    .thenThrow(new RuntimeException("Database error"));

            // When & Then
            mockMvc.perform(put("/api/v1/users/{userId}/complete-profile", userId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testDoctorProfileDto))
                            .with(jwt().jwt(mockJwt)))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.error").exists());
        }

        @Test
        void completeDoctorProfile_ValidationError_MissingID() throws Exception {
            // Given
            String userId = TEST_USER_UUID.toString();
            DoctorProfileDto dtoMissingId = new DoctorProfileDto();
            dtoMissingId.setSpeciality("Cardiology");
            dtoMissingId.setDescription("Experienced cardiologist");
            dtoMissingId.setDiploma("MD, Cardiology");
            dtoMissingId.setClinicId(1L);
            // ID is NOT set - this should cause validation error

            // When & Then
            mockMvc.perform(put("/api/v1/users/{userId}/complete-profile", userId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dtoMissingId))
                            .with(jwt().jwt(mockJwt)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    class UploadProfilePhotoTests {
        @Test
        void uploadProfilePhoto_Success() throws Exception {
            // Given
            String userId = TEST_USER_UUID.toString();
            String expectedPhotoUrl = "http://example.com/uploaded-photo.jpg";
            when(userService.uploadProfilePhoto(eq(userId), any()))
                    .thenReturn(expectedPhotoUrl);

            MockMultipartFile file = new MockMultipartFile(
                    "file",
                    "profile.jpg",
                    MediaType.IMAGE_JPEG_VALUE,
                    "test image content".getBytes()
            );

            // When & Then
            mockMvc.perform(multipart(HttpMethod.POST, "/api/v1/users/{userId}/profile-photo", userId)
                            .file(file)
                            .with(jwt().jwt(mockJwt)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Profile photo uploaded successfully"))
                    .andExpect(jsonPath("$.profilePhotoUrl").value(expectedPhotoUrl));
        }

        @Test
        void uploadProfilePhoto_Forbidden() throws Exception {
            // Given
            String userId = DIFFERENT_USER_UUID.toString();
            Jwt otherUserJwt = createJwtForUser(OTHER_USER_UUID, "otheruser", Arrays.asList("DOCTOR"));

            MockMultipartFile file = new MockMultipartFile(
                    "file",
                    "profile.jpg",
                    MediaType.IMAGE_JPEG_VALUE,
                    "test image content".getBytes()
            );

            // When & Then
            mockMvc.perform(multipart(HttpMethod.POST, "/api/v1/users/{userId}/profile-photo", userId)
                            .file(file)
                            .with(jwt().jwt(otherUserJwt)))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.error").value("You don't have permission to update this profile"));
        }

        @Test
        void uploadProfilePhoto_AdminCanUploadForAnyUser() throws Exception {
            // Given
            String userId = TEST_USER_UUID.toString();
            String expectedPhotoUrl = "http://example.com/admin-uploaded-photo.jpg";
            when(userService.uploadProfilePhoto(eq(userId), any()))
                    .thenReturn(expectedPhotoUrl);

            MockMultipartFile file = new MockMultipartFile(
                    "file",
                    "profile.jpg",
                    MediaType.IMAGE_JPEG_VALUE,
                    "test image content".getBytes()
            );

            // When & Then - Admin uploading for another user
            mockMvc.perform(multipart(HttpMethod.POST, "/api/v1/users/{userId}/profile-photo", userId)
                            .file(file)
                            .with(jwt().jwt(mockAdminJwt)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Profile photo uploaded successfully"));
        }

        @Test
        void uploadProfilePhoto_NoFileProvided() throws Exception {
            // Given
            String userId = TEST_USER_UUID.toString();

            // When & Then - No file parameter
            mockMvc.perform(multipart(HttpMethod.POST, "/api/v1/users/{userId}/profile-photo", userId)
                            .with(jwt().jwt(mockJwt)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void uploadProfilePhoto_EmptyFile() throws Exception {
            // Given
            String userId = TEST_USER_UUID.toString();
            MockMultipartFile emptyFile = new MockMultipartFile(
                    "file",
                    "empty.jpg",
                    MediaType.IMAGE_JPEG_VALUE,
                    new byte[0]
            );

            when(userService.uploadProfilePhoto(eq(userId), any()))
                    .thenThrow(new IllegalArgumentException("File cannot be null or empty"));

            // When & Then
            mockMvc.perform(multipart(HttpMethod.POST, "/api/v1/users/{userId}/profile-photo", userId)
                            .file(emptyFile)
                            .with(jwt().jwt(mockJwt)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").exists());
        }

        @Test
        void uploadProfilePhoto_ServiceException() throws Exception {
            // Given
            String userId = TEST_USER_UUID.toString();
            MockMultipartFile file = new MockMultipartFile(
                    "file",
                    "profile.jpg",
                    MediaType.IMAGE_JPEG_VALUE,
                    "test image content".getBytes()
            );

            when(userService.uploadProfilePhoto(eq(userId), any()))
                    .thenThrow(new RuntimeException("Storage service error"));

            // When & Then
            mockMvc.perform(multipart(HttpMethod.POST, "/api/v1/users/{userId}/profile-photo", userId)
                            .file(file)
                            .with(jwt().jwt(mockJwt)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").exists());
        }

        @Test
        void uploadProfilePhoto_InvalidFileType() throws Exception {
            // Given
            String userId = TEST_USER_UUID.toString();
            MockMultipartFile invalidFile = new MockMultipartFile(
                    "file",
                    "document.pdf",
                    MediaType.APPLICATION_PDF_VALUE,
                    "test pdf content".getBytes()
            );

            when(userService.uploadProfilePhoto(eq(userId), any()))
                    .thenThrow(new IllegalArgumentException("Only image files are allowed"));

            // When & Then
            mockMvc.perform(multipart(HttpMethod.POST, "/api/v1/users/{userId}/profile-photo", userId)
                            .file(invalidFile)
                            .with(jwt().jwt(mockJwt)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").exists());
        }
    }

    // Additional test cases for edge scenarios

    @Nested
    class EdgeCaseTests {
        @Test
        void getUserDetails_WithSpecialCharacters() throws Exception {
            // Given
            String username = "test.user@company";
            Map<String, Object> userDetails = new HashMap<>();
            userDetails.put("id", TEST_USER_UUID.toString());
            userDetails.put("username", username);
            userDetails.put("email", "test.user@company.com");

            when(userService.getUserDetails(username)).thenReturn(userDetails);

            // When & Then
            mockMvc.perform(get("/api/v1/users/details/{username}", username))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.username").value(username));
        }

        @Test
        void getCurrentUser_WithEmptyRoles() throws Exception {
            // Given - Create JWT without roles
            Map<String, Object> claims = new HashMap<>();
            claims.put("sub", TEST_USER_UUID.toString());
            claims.put("preferred_username", "noroleuser");
            claims.put("email", "norole@example.com");
            claims.put("given_name", "No");
            claims.put("family_name", "Role");
            claims.put("email_verified", true);
            Map<String, Object> realmAccess = new HashMap<>();
            realmAccess.put("roles", Collections.emptyList()); // Empty roles list
            claims.put("realm_access", realmAccess);

            Jwt noRoleJwt = Jwt.withTokenValue("token-no-role")
                    .header("alg", "RS256")
                    .claims(c -> c.putAll(claims))
                    .build();

            when(userDataService.getUserDataById(TEST_USER_UUID.toString()))
                    .thenReturn(Optional.empty());

            // When & Then
            mockMvc.perform(get("/api/v1/users/me")
                            .with(jwt().jwt(noRoleJwt)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(TEST_USER_UUID.toString()))
                    .andExpect(jsonPath("$.username").value("noroleuser"))
                    .andExpect(jsonPath("$.roles").isArray())
                    .andExpect(jsonPath("$.roles").isEmpty());
        }

        @Test
        void updateUser_WithMinimalData() throws Exception {
            // Given
            String userId = TEST_USER_UUID.toString();
            UserUpdateDto minimalUpdateDto = new UserUpdateDto();
            minimalUpdateDto.setFirstName("Min");
            minimalUpdateDto.setLastName("User");
            minimalUpdateDto.setEmail("min@example.com");
            // No phone number

            when(userService.updateUser(eq(userId), any(UserUpdateDto.class)))
                    .thenReturn(testUserRegistration);

            // When & Then
            mockMvc.perform(put("/api/v1/users/{userId}", userId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(minimalUpdateDto))
                            .with(jwt().jwt(mockJwt)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("User updated successfully"));
        }

        @Test
        void completeDoctorProfile_WithMaxLengthFields() throws Exception {
            // Given
            String userId = TEST_USER_UUID.toString();
            DoctorProfileDto maxLengthDto = new DoctorProfileDto();
            maxLengthDto.setId(TEST_USER_UUID.toString());

            // Create very long strings for testing
            String longSpeciality = "A".repeat(100);
            String longDescription = "B".repeat(1000);
            String longDiploma = "C".repeat(200);

            maxLengthDto.setSpeciality(longSpeciality);
            maxLengthDto.setDescription(longDescription);
            maxLengthDto.setDiploma(longDiploma);
            maxLengthDto.setClinicId(999999L);

            when(userService.completeDoctorProfile(eq(userId), any(DoctorProfileDto.class)))
                    .thenReturn(testUserData);

            // When & Then
            mockMvc.perform(put("/api/v1/users/{userId}/complete-profile", userId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(maxLengthDto))
                            .with(jwt().jwt(mockJwt)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Doctor profile completed successfully"));
        }

        @Test
        void registerUser_WithExistingEmail() throws Exception {
            // Given
            when(userService.registerMyUser(any(UserRegistrationRequest.class), any()))
                    .thenThrow(new RuntimeException("User with email already exists"));

            MockMultipartFile userPart = new MockMultipartFile(
                    "user",
                    "",
                    "application/json",
                    objectMapper.writeValueAsBytes(testUserRegistration)
            );

            // When & Then
            mockMvc.perform(multipart("/api/v1/users/register")
                            .file(userPart)
                            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").exists());
        }
    }
}