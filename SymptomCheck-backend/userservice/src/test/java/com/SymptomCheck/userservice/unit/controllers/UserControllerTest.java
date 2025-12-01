package com.SymptomCheck.userservice.unit.controllers;

import com.SymptomCheck.userservice.controllers.UserController;
import com.SymptomCheck.userservice.dtos.UserRegistrationRequest;
import com.SymptomCheck.userservice.models.UserData;
import com.SymptomCheck.userservice.services.UserDataService;
import com.SymptomCheck.userservice.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserController Unit Tests")
class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private UserDataService userDataService;

    @Mock
    private Jwt jwt;

    @InjectMocks
    private UserController userController;

    private UserRegistrationRequest userRegistrationRequest;
    private MultipartFile multipartFile;

    @BeforeEach
    void setUp() {
        userRegistrationRequest = new UserRegistrationRequest();
        userRegistrationRequest.setUsername("testuser");
        userRegistrationRequest.setEmail("test@example.com");
        userRegistrationRequest.setPassword("password");
        userRegistrationRequest.setRole("PATIENT");

        multipartFile = new MockMultipartFile(
                "file", "test.jpg", "image/jpeg", "test image content".getBytes()
        );
    }

    @Nested
    @DisplayName("Register User Tests")
    class RegisterUserTests {

        @Test
        @DisplayName("should register user successfully")
        void shouldRegisterUserSuccessfully() {
            // Given
            String userId = "keycloak-user-123";
            when(userService.registerMyUser(any(UserRegistrationRequest.class), any(MultipartFile.class)))
                    .thenReturn(userId);

            // When
            ResponseEntity<?> response = userController.registerUser(userRegistrationRequest, multipartFile);

            // Then
            assertEquals(200, response.getStatusCodeValue());
            assertNotNull(response.getBody());

            @SuppressWarnings("unchecked")
            Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
            assertEquals("User registered successfully in Keycloak with ALL attributes", responseBody.get("message"));
            assertEquals(userId, responseBody.get("keycloakUserId"));
            assertEquals("testuser", responseBody.get("username"));

            verify(userService).registerMyUser(userRegistrationRequest, multipartFile);
        }

        @Test
        @DisplayName("should register user without file successfully")
        void shouldRegisterUserWithoutFileSuccessfully() {
            // Given
            String userId = "keycloak-user-456";
            when(userService.registerMyUser(any(UserRegistrationRequest.class), isNull()))
                    .thenReturn(userId);

            // When
            ResponseEntity<?> response = userController.registerUser(userRegistrationRequest, null);

            // Then
            assertEquals(200, response.getStatusCodeValue());

            @SuppressWarnings("unchecked")
            Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
            assertEquals("User registered successfully in Keycloak with ALL attributes", responseBody.get("message"));

            verify(userService).registerMyUser(userRegistrationRequest, null);
        }

        @Test
        @DisplayName("should return bad request when registration fails")
        void shouldReturnBadRequestWhenRegistrationFails() {
            // Given
            when(userService.registerMyUser(any(UserRegistrationRequest.class), any(MultipartFile.class)))
                    .thenThrow(new RuntimeException("Registration failed"));

            // When
            ResponseEntity<?> response = userController.registerUser(userRegistrationRequest, multipartFile);

            // Then
            assertEquals(400, response.getStatusCodeValue());

            @SuppressWarnings("unchecked")
            Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
            assertEquals("Registration failed", responseBody.get("error"));

            verify(userService).registerMyUser(userRegistrationRequest, multipartFile);
        }
    }

    @Nested
    @DisplayName("Get User Details Tests")
    class GetUserDetailsTests {

        @Test
        @DisplayName("should return user details when user exists")
        void shouldReturnUserDetailsWhenUserExists() {
            // Given
            String username = "existinguser";
            Map<String, Object> userDetails = Map.of("username", username, "email", "user@example.com");
            when(userService.getUserDetails(username)).thenReturn(userDetails);

            // When
            ResponseEntity<?> response = userController.getUserDetails(username);

            // Then
            assertEquals(200, response.getStatusCodeValue());
            assertEquals(userDetails, response.getBody());
            verify(userService).getUserDetails(username);
        }

        @Test
        @DisplayName("should return not found when user does not exist")
        void shouldReturnNotFoundWhenUserDoesNotExist() {
            // Given
            String username = "nonexistentuser";
            when(userService.getUserDetails(username)).thenReturn(null);

            // When
            ResponseEntity<?> response = userController.getUserDetails(username);

            // Then
            assertEquals(404, response.getStatusCodeValue());
            verify(userService).getUserDetails(username);
        }

        @Test
        @DisplayName("should return bad request when service throws exception")
        void shouldReturnBadRequestWhenServiceThrowsException() {
            // Given
            String username = "erroruser";
            when(userService.getUserDetails(username))
                    .thenThrow(new RuntimeException("Service error"));

            // When
            ResponseEntity<?> response = userController.getUserDetails(username);

            // Then
            assertEquals(400, response.getStatusCodeValue());

            @SuppressWarnings("unchecked")
            Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
            assertEquals("Service error", responseBody.get("error"));

            verify(userService).getUserDetails(username);
        }
    }

    @Nested
    @DisplayName("Get Current User Tests")
    class GetCurrentUserTests {

        @BeforeEach
        void setUpJwt() {
            when(jwt.getSubject()).thenReturn("keycloak-user-123");
            when(jwt.getClaim("preferred_username")).thenReturn("testuser");
            when(jwt.getClaim("email")).thenReturn("test@example.com");
            when(jwt.getClaim("given_name")).thenReturn("John");
            when(jwt.getClaim("family_name")).thenReturn("Doe");
            when(jwt.getClaim("email_verified")).thenReturn(true);

            Map<String, Object> realmAccess = Map.of("roles", List.of("PATIENT", "USER"));
            when(jwt.getClaim("realm_access")).thenReturn(realmAccess);
        }

        @Test
        @DisplayName("should return current user with user data")
        void shouldReturnCurrentUserWithUserData() {
            // Given
            UserData userData = new UserData();
            userData.setId("keycloak-user-123");
            userData.setPhoneNumber("+1234567890");
            userData.setProfilePhotoUrl("http://example.com/photo.jpg");
            userData.setProfileComplete(true);
            userData.setClinicId(1L);
            userData.setSpeciality("Cardiology");
            userData.setDescription("Experienced doctor");
            userData.setDiploma("MD");
            userData.setCreatedAt(Instant.now());
            userData.setUpdatedAt(Instant.now());

            when(userDataService.getUserDataById("keycloak-user-123"))
                    .thenReturn(Optional.of(userData));

            // When
            ResponseEntity<?> response = userController.getCurrentUser(jwt);

            // Then
            assertEquals(200, response.getStatusCodeValue());
            assertNotNull(response.getBody());

            @SuppressWarnings("unchecked")
            Map<String, Object> responseBody = (Map<String, Object>) response.getBody();

            // Basic JWT claims
            assertEquals("keycloak-user-123", responseBody.get("id"));
            assertEquals("testuser", responseBody.get("username"));
            assertEquals("test@example.com", responseBody.get("email"));
            assertEquals("John", responseBody.get("firstName"));
            assertEquals("Doe", responseBody.get("lastName"));
            assertEquals(true, responseBody.get("emailVerified"));

            // Roles
            @SuppressWarnings("unchecked")
            List<String> roles = (List<String>) responseBody.get("roles");
            assertTrue(roles.contains("PATIENT"));
            assertTrue(roles.contains("USER"));

            // UserData fields
            assertEquals("+1234567890", responseBody.get("phoneNumber"));
            assertEquals("http://example.com/photo.jpg", responseBody.get("profilePhotoUrl"));
            assertEquals(true, responseBody.get("isProfileComplete"));
            assertEquals(1L, responseBody.get("clinicId"));
            assertEquals("Cardiology", responseBody.get("speciality"));
            assertEquals("Experienced doctor", responseBody.get("description"));
            assertEquals("MD", responseBody.get("diploma"));
            assertNotNull(responseBody.get("createdAt"));
            assertNotNull(responseBody.get("updatedAt"));

            verify(userDataService).getUserDataById("keycloak-user-123");
        }

        @Test
        @DisplayName("should return current user without user data when not found")
        void shouldReturnCurrentUserWithoutUserDataWhenNotFound() {
            // Given
            when(userDataService.getUserDataById("keycloak-user-123"))
                    .thenReturn(Optional.empty());

            // When
            ResponseEntity<?> response = userController.getCurrentUser(jwt);

            // Then
            assertEquals(200, response.getStatusCodeValue());
            assertNotNull(response.getBody());

            @SuppressWarnings("unchecked")
            Map<String, Object> responseBody = (Map<String, Object>) response.getBody();

            // Basic JWT claims should still be present
            assertEquals("keycloak-user-123", responseBody.get("id"));
            assertEquals("testuser", responseBody.get("username"));
            assertEquals("test@example.com", responseBody.get("email"));

            // UserData fields should be null or default
            assertNull(responseBody.get("phoneNumber"));
            assertNull(responseBody.get("profilePhotoUrl"));
            assertEquals(false, responseBody.get("isProfileComplete"));
            assertNull(responseBody.get("clinicId"));
            assertNull(responseBody.get("speciality"));
            assertNull(responseBody.get("description"));
            assertNull(responseBody.get("diploma"));
            assertNull(responseBody.get("createdAt"));
            assertNull(responseBody.get("updatedAt"));

            verify(userDataService).getUserDataById("keycloak-user-123");
        }

        @Test
        @DisplayName("should handle null realm access in JWT")
        void shouldHandleNullRealmAccessInJwt() {
            // Given
            when(jwt.getClaim("realm_access")).thenReturn(null);
            when(userDataService.getUserDataById("keycloak-user-123"))
                    .thenReturn(Optional.empty());

            // When
            ResponseEntity<?> response = userController.getCurrentUser(jwt);

            // Then
            assertEquals(200, response.getStatusCodeValue());

            @SuppressWarnings("unchecked")
            Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
            assertNull(responseBody.get("roles"));
        }

        @Test
        @DisplayName("should handle null roles in realm access")
        void shouldHandleNullRolesInRealmAccess() {
            // Given
            Map<String, Object> realmAccess = new HashMap<>();
            realmAccess.put("roles", null);
            when(jwt.getClaim("realm_access")).thenReturn(realmAccess);
            when(userDataService.getUserDataById("keycloak-user-123"))
                    .thenReturn(Optional.empty());

            // When
            ResponseEntity<?> response = userController.getCurrentUser(jwt);

            // Then
            assertEquals(200, response.getStatusCodeValue());

            @SuppressWarnings("unchecked")
            Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
            assertNull(responseBody.get("roles"));
        }
    }

    @Nested
    @DisplayName("Disable User Tests")
    class DisableUserTests {

        @Test
        @DisplayName("should disable user successfully")
        void shouldDisableUserSuccessfully() {
            // Given
            String userId = "user-to-disable";
            boolean isEnable = false;
            doNothing().when(userService).disableUser(userId, isEnable);

            // When
            ResponseEntity<Boolean> response = userController.disableUser(userId, isEnable);

            // Then
            assertEquals(200, response.getStatusCodeValue());
            assertEquals(true, response.getBody());
            verify(userService).disableUser(userId, isEnable);
        }

        @Test
        @DisplayName("should enable user successfully")
        void shouldEnableUserSuccessfully() {
            // Given
            String userId = "user-to-enable";
            boolean isEnable = true;
            doNothing().when(userService).disableUser(userId, isEnable);

            // When
            ResponseEntity<Boolean> response = userController.disableUser(userId, isEnable);

            // Then
            assertEquals(200, response.getStatusCodeValue());
            assertEquals(true, response.getBody());
            verify(userService).disableUser(userId, isEnable);
        }

        @Test
        @DisplayName("should return internal server error when disable fails")
        void shouldReturnInternalServerErrorWhenDisableFails() {
            // Given
            String userId = "user-error";
            boolean isEnable = false;
            doThrow(new RuntimeException("Disable failed")).when(userService).disableUser(userId, isEnable);

            // When
            ResponseEntity<Boolean> response = userController.disableUser(userId, isEnable);

            // Then
            assertEquals(500, response.getStatusCodeValue());
            assertEquals(false, response.getBody());
            verify(userService).disableUser(userId, isEnable);
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
            List<UserRegistrationRequest> users = List.of(
                    new UserRegistrationRequest(),
                    new UserRegistrationRequest()
            );
            when(userService.getUsersByRole("DOCTOR")).thenReturn(users);

            // When
            ResponseEntity<List<UserRegistrationRequest>> response = userController.getUsersByRole(role);

            // Then
            assertEquals(200, response.getStatusCodeValue());
            assertEquals(2, response.getBody().size());
            verify(userService).getUsersByRole("DOCTOR");
        }

        @Test
        @DisplayName("should convert role to uppercase")
        void shouldConvertRoleToUppercase() {
            // Given
            String role = "doctor";
            List<UserRegistrationRequest> users = List.of(new UserRegistrationRequest());
            when(userService.getUsersByRole("DOCTOR")).thenReturn(users);

            // When
            ResponseEntity<List<UserRegistrationRequest>> response = userController.getUsersByRole(role);

            // Then
            assertEquals(200, response.getStatusCodeValue());
            verify(userService).getUsersByRole("DOCTOR");
        }

        @Test
        @DisplayName("should return internal server error when service fails")
        void shouldReturnInternalServerErrorWhenServiceFails() {
            // Given
            String role = "PATIENT";
            when(userService.getUsersByRole("PATIENT"))
                    .thenThrow(new RuntimeException("Service error"));

            // When
            ResponseEntity<List<UserRegistrationRequest>> response = userController.getUsersByRole(role);

            // Then
            assertEquals(500, response.getStatusCodeValue());
            assertNull(response.getBody());
            verify(userService).getUsersByRole("PATIENT");
        }
    }

    @Nested
    @DisplayName("Get User By ID Tests")
    class GetUserByIdTests {

        @Test
        @DisplayName("should return user by ID successfully")
        void shouldReturnUserByIdSuccessfully() {
            // Given
            String userId = "user-123";
            UserRegistrationRequest user = new UserRegistrationRequest();
            user.setId(userId);
            user.setUsername("testuser");

            when(userService.getUserById(userId)).thenReturn(user);

            // When
            ResponseEntity<?> response = userController.getUserById(userId);

            // Then
            assertEquals(200, response.getStatusCodeValue());
            assertEquals(user, response.getBody());
            verify(userService).getUserById(userId);
        }

        @Test
        @DisplayName("should return internal server error when service fails")
        void shouldReturnInternalServerErrorWhenServiceFails() {
            // Given
            String userId = "user-error";
            when(userService.getUserById(userId))
                    .thenThrow(new RuntimeException("User not found"));

            // When
            ResponseEntity<?> response = userController.getUserById(userId);

            // Then
            assertEquals(500, response.getStatusCodeValue());
            assertEquals(false, response.getBody());
            verify(userService).getUserById(userId);
        }
    }

    @Nested
    @DisplayName("Edge Case Tests")
    class EdgeCaseTests {

        @Test
        @DisplayName("should handle null JWT claims gracefully")
        void shouldHandleNullJwtClaimsGracefully() {
            // Given
            when(jwt.getSubject()).thenReturn("user-123");
            when(jwt.getClaim("preferred_username")).thenReturn(null);
            when(jwt.getClaim("email")).thenReturn(null);
            when(jwt.getClaim("given_name")).thenReturn(null);
            when(jwt.getClaim("family_name")).thenReturn(null);
            when(jwt.getClaim("email_verified")).thenReturn(null);
            when(jwt.getClaim("realm_access")).thenReturn(null);
            when(userDataService.getUserDataById("user-123"))
                    .thenReturn(Optional.empty());

            // When
            ResponseEntity<?> response = userController.getCurrentUser(jwt);

            // Then
            assertEquals(200, response.getStatusCodeValue());

            @SuppressWarnings("unchecked")
            Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
            assertEquals("user-123", responseBody.get("id"));
            assertNull(responseBody.get("username"));
            assertNull(responseBody.get("email"));
            assertNull(responseBody.get("firstName"));
            assertNull(responseBody.get("lastName"));
            assertNull(responseBody.get("emailVerified"));
            assertNull(responseBody.get("roles"));
        }

    }
}