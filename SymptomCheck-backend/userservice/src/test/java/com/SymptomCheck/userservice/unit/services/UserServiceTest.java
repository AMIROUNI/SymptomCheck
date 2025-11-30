package com.SymptomCheck.userservice.unit.services;

import com.SymptomCheck.userservice.dtos.UserRegistrationRequest;
import com.SymptomCheck.userservice.models.User;
import com.SymptomCheck.userservice.models.UserData;
import com.SymptomCheck.userservice.repositories.UserDataRepository;
import com.SymptomCheck.userservice.services.KeycloakService;
import com.SymptomCheck.userservice.services.LocalFileStorageService;
import com.SymptomCheck.userservice.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@DisplayName("User service tests" )
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private KeycloakService keycloakService;

    @Mock
    private UserDataRepository userDataRepository;

    @Mock
    private LocalFileStorageService localFileStorageService;

    @InjectMocks
    private UserService userService;

    private UserRegistrationRequest testRequest;
    private MultipartFile testFile;

    @BeforeEach
    void setUp() throws Exception {
        testRequest = new UserRegistrationRequest();
        testRequest.setUsername("johndoe");
        testRequest.setEmail("john@example.com");
        testRequest.setPassword("password");
        testRequest.setFirstName("John");
        testRequest.setLastName("Doe");
        testRequest.setRole("PATIENT");

        // Mock MultipartFile (can be a simple in-memory file)
        testFile = new MockMultipartFile("profile.jpg", "content".getBytes());

        // Optional: set other fields
        testRequest.setPhoneNumber("555-1234");
    }

    @Nested
    @DisplayName("registartion tests")
    class RegistartionTests {

        @Test
        @DisplayName("should register user successfully")
        void shouldRegisterUserSuccessfully() throws Exception {
            // Given
            when(localFileStorageService.store(testFile)).thenReturn("uploads/profile.jpg");
            when(keycloakService.registerUser(any(User.class))).thenReturn("user-id-123");

            // When
            String userId = userService.registerMyUser(testRequest, testFile);

            // Then
            assertEquals("user-id-123", userId);
            verify(localFileStorageService).store(testFile);
            verify(keycloakService).registerUser(any(User.class));
            verify(userDataRepository).save(any(UserData.class));
        }

        @Test
        @DisplayName("should throw exception when file upload fails")
        void shouldThrowWhenFileUploadFails() throws Exception {
            // Given
            when(localFileStorageService.store(testFile)).thenThrow(new IOException("Upload error"));

            // When / Then
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> userService.registerMyUser(testRequest, testFile));

            assertTrue(exception.getMessage().contains("Failed to register user in Keycloak"));
        }

    }

    @Nested
    @DisplayName("userExists Tests")
    class UserExistsTests {

        @Test
        @DisplayName("Should return true when KeycloakService returns true")
        void shouldReturnTrueWhenUserExists() {
            // GIVEN
            when(keycloakService.userExists("johndoe")).thenReturn(true);

            // WHEN
            boolean result = userService.userExists("johndoe");

            // THEN
            assertTrue(result);
            verify(keycloakService, times(1)).userExists("johndoe");
        }

        @Test
        @DisplayName("Should return false when KeycloakService returns false")
        void shouldReturnFalseWhenUserDoesNotExist() {
            // GIVEN
            when(keycloakService.userExists("johndoe")).thenReturn(false);

            // WHEN
            boolean result = userService.userExists("johndoe");

            // THEN
            assertFalse(result);
            verify(keycloakService).userExists("johndoe");
        }

        @Test
        @DisplayName("Should throw RuntimeException when Keycloak throws exception")
        void shouldThrowRuntimeExceptionWhenKeycloakFails() {
            // GIVEN
            when(keycloakService.userExists("johndoe"))
                    .thenThrow(new RuntimeException("Keycloak down"));

            // WHEN + THEN
            RuntimeException ex = assertThrows(
                    RuntimeException.class,
                    () -> userService.userExists("johndoe")
            );

            assertEquals("Failed to check user existence", ex.getMessage());
            verify(keycloakService).userExists("johndoe");
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException for null or blank username")
        void shouldThrowIllegalArgumentExceptionForInvalidUsername() {
            assertThrows(IllegalArgumentException.class, () -> userService.userExists(null));
            assertThrows(IllegalArgumentException.class, () -> userService.userExists(""));
            assertThrows(IllegalArgumentException.class, () -> userService.userExists("   "));
        }
    }

    @Nested
    @DisplayName("getUserDetails() tests")
    class GetUserDetailsTests {

        @Test
        @DisplayName("should return user details when username exists")
        void shouldReturnUserDetails() {
            // Given
            String username = "johndoe";
            Map<String, Object> mockDetails = new HashMap<>();
            mockDetails.put("username", "johndoe");
            mockDetails.put("email", "john@example.com");

            when(keycloakService.getUserDetails(username)).thenReturn(mockDetails);

            // When
            Map<String, Object> result = userService.getUserDetails(username);

            // Then
            assertNotNull(result);
            assertEquals("johndoe", result.get("username"));
            assertEquals("john@example.com", result.get("email"));

            verify(keycloakService, times(1)).getUserDetails(username);
        }

        @Test
        @DisplayName("should throw exception when username is null or blank")
        void shouldThrowIfUsernameInvalid() {
            // When + Then
            assertThrows(IllegalArgumentException.class, () ->
                    userService.getUserDetails(" ")
            );

            assertThrows(IllegalArgumentException.class, () ->
                    userService.getUserDetails(null)
            );

            // Keycloak must NOT be called
            verify(keycloakService, never()).getUserDetails(anyString());
        }
    }
    @Nested
    @DisplayName("disableUser tests")
    class disableUser {

        @DisplayName("disapel user success test")
        @Test
        void testGetUsersByRole_Success() {
            // Arrange
            String role = "PATIENT";

            UserRegistrationRequest user1 = new UserRegistrationRequest();
            user1.setId("123");
            user1.setUsername("john");

            UserRegistrationRequest user2 = new UserRegistrationRequest();
            user2.setId("456");
            user2.setUsername("doe");

            List<UserRegistrationRequest> mockList = List.of(user1, user2);

            when(keycloakService.getUsersByRole(role)).thenReturn(mockList);

            // Act
            List<UserRegistrationRequest> result = userService.getUsersByRole(role);

            // Assert
            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals("john", result.get(0).getUsername());

            verify(keycloakService, times(1)).getUsersByRole(role);
        }
        @DisplayName("disapel user throw exeption test")
        @Test
        void testGetUsersByRole_Empty() {
            // Arrange
            String role = "PATIENT";

            when(keycloakService.getUsersByRole(role)).thenReturn(List.of());

            // Act
            List<UserRegistrationRequest> result = userService.getUsersByRole(role);

            // Assert
            assertNotNull(result);
            assertTrue(result.isEmpty());

            verify(keycloakService, times(1)).getUsersByRole(role);
        }
    }
    @Nested
    @DisplayName("User registration tests")
    class RegistrationUser {
        @DisplayName("user regestration success test")
        @Test
        void testGetUserById_Success() {
            // Arrange
            String userId = "abc123";

            UserRegistrationRequest mockUser = new UserRegistrationRequest();
            mockUser.setId(userId);
            mockUser.setUsername("john");
            mockUser.setEmail("john@example.com");

            when(keycloakService.getUserById(userId)).thenReturn(mockUser);

            // Act
            UserRegistrationRequest result = userService.getUserById(userId);

            // Assert
            assertNotNull(result);
            assertEquals("abc123", result.getId());
            assertEquals("john", result.getUsername());

            verify(keycloakService, times(1)).getUserById(userId);
        }
        @DisplayName("user regestration throw exeption test")
        @Test
        void testGetUserById_NotFound() {
            // Arrange
            String userId = "not_found";

            when(keycloakService.getUserById(userId)).thenReturn(null);

            // Act
            UserRegistrationRequest result = userService.getUserById(userId);

            // Assert
            assertNull(result);

            verify(keycloakService, times(1)).getUserById(userId);
        }
    }
}
