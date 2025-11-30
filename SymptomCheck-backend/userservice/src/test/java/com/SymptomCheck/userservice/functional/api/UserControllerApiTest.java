package com.SymptomCheck.userservice.functional.api;

import com.SymptomCheck.userservice.controllers.UserController;
import com.SymptomCheck.userservice.dtos.DoctorProfileDto;
import com.SymptomCheck.userservice.dtos.UserRegistrationRequest;
import com.SymptomCheck.userservice.dtos.UserUpdateDto;
import com.SymptomCheck.userservice.models.UserData;
import com.SymptomCheck.userservice.services.UserDataService;
import com.SymptomCheck.userservice.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(UserController.class)
class UserControllerApiTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private UserDataService userDataService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserRegistrationRequest userRegistrationRequest;
    private UserUpdateDto userUpdateDto;
    private DoctorProfileDto doctorProfileDto;
    private UserData userData;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();

        // Setup test data for UserRegistrationRequest
        userRegistrationRequest = new UserRegistrationRequest();
        userRegistrationRequest.setId("123e4567-e89b-12d3-a456-426614174000");
        userRegistrationRequest.setUsername("testuser");
        userRegistrationRequest.setPassword("password123");
        userRegistrationRequest.setEmail("test@example.com");
        userRegistrationRequest.setFirstName("John");
        userRegistrationRequest.setLastName("Doe");
        userRegistrationRequest.setPhoneNumber("+1234567890");
        userRegistrationRequest.setRole("PATIENT");
        userRegistrationRequest.setEnabled(true);
        userRegistrationRequest.setProfileComplete(false);

        // Setup test data for UserUpdateDto
        userUpdateDto = new UserUpdateDto();
        userUpdateDto.setFirstName("UpdatedJohn");
        userUpdateDto.setLastName("UpdatedDoe");
        userUpdateDto.setEmail("updated@example.com");
        userUpdateDto.setPhoneNumber("+0987654321");

        // Setup test data for DoctorProfileDto
        doctorProfileDto = new DoctorProfileDto();
        doctorProfileDto.setId("123e4567-e89b-12d3-a456-426614174000");
        doctorProfileDto.setSpeciality("Cardiology");
        doctorProfileDto.setDescription("Experienced cardiologist");
        doctorProfileDto.setDiploma("MD Cardiology");
        doctorProfileDto.setClinicId(1L);

        // Setup test data for UserData
        userData = new UserData();
        userData.setId("123e4567-e89b-12d3-a456-426614174000");
        userData.setPhoneNumber("+1234567890");
        userData.setProfilePhotoUrl("http://example.com/photo.jpg");
        userData.setProfileComplete(true);
        userData.setClinicId(1L);
        userData.setSpeciality("Cardiology");
        userData.setDescription("Experienced cardiologist");
        userData.setDiploma("MD Cardiology");
        userData.setCreatedAt(
                LocalDateTime.now().minusDays(1).toInstant(ZoneOffset.UTC)
        );
        userData.setUpdatedAt(
                LocalDateTime.now().toInstant(ZoneOffset.UTC)
        );
    }

    @Nested
    @WithMockUser(username = "user", roles = {"USER"})
    class RegisterUser {

        @Test
        void shouldRegisterUserSuccessfully() throws Exception {
            // Given
            String expectedUserId = "keycloak-user-id-123";
            given(userService.registerMyUser(any(UserRegistrationRequest.class), any()))
                    .willReturn(expectedUserId);

            MockMultipartFile userPart = new MockMultipartFile(
                    "user",
                    "",
                    "application/json",
                    objectMapper.writeValueAsBytes(userRegistrationRequest)
            );

            MockMultipartFile filePart = new MockMultipartFile(
                    "file",
                    "profile.jpg",
                    "image/jpeg",
                    "test image content".getBytes()
            );

            // When & Then
            mockMvc.perform(multipart("/api/v1/users/register")
                            .file(userPart)
                            .file(filePart)
                            .with(csrf())
                            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message", is("User registered successfully in Keycloak with ALL attributes")))
                    .andExpect(jsonPath("$.keycloakUserId", is(expectedUserId)))
                    .andExpect(jsonPath("$.username", is(userRegistrationRequest.getUsername())));
        }

        @Test
        void whenRegistrationFails_shouldReturnBadRequest() throws Exception {
            // Given
            given(userService.registerMyUser(any(UserRegistrationRequest.class), any()))
                    .willThrow(new RuntimeException("Registration failed"));

            MockMultipartFile userPart = new MockMultipartFile(
                    "user",
                    "",
                    "application/json",
                    objectMapper.writeValueAsBytes(userRegistrationRequest)
            );

            // When & Then
            mockMvc.perform(multipart("/api/v1/users/register")
                            .file(userPart)
                            .with(csrf())
                            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error", is("Registration failed")));
        }
    }

    @Nested
    @WithMockUser(username = "user", roles = {"USER"})
    class GetUserDetails {

        @Test
        void shouldReturnUserDetails() throws Exception {
            // Given
            String username = "testuser";
            Map<String, Object> mockUserDetails = Map.of(
                    "id", "123456",
                    "username", "testuser",
                    "email", "test@example.com",
                    "firstName", "John",
                    "lastName", "Doe",
                    "enabled", true,
                    "emailVerified", false,
                    "attributes", Map.of(
                            "phoneNumber", List.of("12345678"),
                            "speciality", List.of("Cardiology")
                    ),
                    "realmRoles", List.of("ROLE_USER", "ROLE_ADMIN")
            );


            given(userService.getUserDetails(username)).willReturn(mockUserDetails);

            // When & Then
            mockMvc.perform(get("/api/v1/users/details/{username}", username)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.username", is(userRegistrationRequest.getUsername())))
                    .andExpect(jsonPath("$.email", is(userRegistrationRequest.getEmail())))
                    .andExpect(jsonPath("$.firstName", is(userRegistrationRequest.getFirstName())));
        }

        @Test
        void whenUserNotFound_shouldReturnNotFound() throws Exception {
            // Given
            String username = "nonexistent";
            given(userService.getUserDetails(username)).willReturn(null);

            // When & Then
            mockMvc.perform(get("/api/v1/users/details/{username}", username)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @WithMockUser(username = "testuser", roles = {"USER"})
    class GetCurrentUser {

        @Test
        void shouldReturnCurrentUserWithDatabaseData() throws Exception {
            // Given
            given(userDataService.getUserDataById("testuser")).willReturn(Optional.of(userData));

            // When & Then
            mockMvc.perform(get("/api/v1/users/me")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is("testuser")))
                    .andExpect(jsonPath("$.phoneNumber", is(userData.getPhoneNumber())))
                    .andExpect(jsonPath("$.profilePhotoUrl", is(userData.getProfilePhotoUrl())))
                    .andExpect(jsonPath("$.isProfileComplete", is(true)))
                    .andExpect(jsonPath("$.clinicId", is(1)))
                    .andExpect(jsonPath("$.speciality", is(userData.getSpeciality())));
        }

        @Test
        void whenUserDataNotFound_shouldReturnBasicInfo() throws Exception {
            // Given
            given(userDataService.getUserDataById("testuser")).willReturn(Optional.empty());

            // When & Then
            mockMvc.perform(get("/api/v1/users/me")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is("testuser")))
                    .andExpect(jsonPath("$.isProfileComplete", is(false)))
                    .andExpect(jsonPath("$.phoneNumber").doesNotExist());
        }
    }

    @Nested
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    class DisableUser {

        @Test
        void shouldDisableUserSuccessfully() throws Exception {
            // Given
            String userId = "user-123";
            doNothing().when(userService).disableUser(userId, false);

            // When & Then
            mockMvc.perform(patch("/api/v1/users/disable/{userId}", userId)
                            .param("isEnable", "false")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().string("true"));
        }

        @Test
        void whenDisableUserFails_shouldReturnInternalServerError() throws Exception {
            // Given
            String userId = "user-123";

            doThrow(new RuntimeException("Disable failed"))
                    .when(userService)
                    .disableUser(userId, true);

            // When & Then
            mockMvc.perform(patch("/api/v1/users/disable/{userId}", userId)
                            .param("isEnable", "true")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().string("false"));
        }
    }

    @Nested
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    class GetUsersByRole {

        @Test
        void shouldReturnUsersByRole() throws Exception {
            // Given
            String role = "DOCTOR";
            UserRegistrationRequest doctor1 = new UserRegistrationRequest();
            doctor1.setUsername("doctor1");
            doctor1.setEmail("doctor1@example.com");
            doctor1.setRole("DOCTOR");

            UserRegistrationRequest doctor2 = new UserRegistrationRequest();
            doctor2.setUsername("doctor2");
            doctor2.setEmail("doctor2@example.com");
            doctor2.setRole("DOCTOR");

            List<UserRegistrationRequest> doctors = Arrays.asList(doctor1, doctor2);
            given(userService.getUsersByRole("DOCTOR")).willReturn(doctors);

            // When & Then
            mockMvc.perform(get("/api/v1/users/by-role")
                            .param("role", role)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].username", is("doctor1")))
                    .andExpect(jsonPath("$[0].role", is("DOCTOR")))
                    .andExpect(jsonPath("$[1].username", is("doctor2")));
        }

        @Test
        void whenNoUsersFound_shouldReturnEmptyList() throws Exception {
            // Given
            String role = "ADMIN";
            given(userService.getUsersByRole("ADMIN")).willReturn(Arrays.asList());

            // When & Then
            mockMvc.perform(get("/api/v1/users/by-role")
                            .param("role", role)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }
    }

    @Nested
    @WithMockUser(username = "user", roles = {"USER"})
    class GetUserById {

        @Test
        void shouldReturnUserById() throws Exception {
            // Given
            String userId = "user-123";
            given(userService.getUserById(userId)).willReturn(userRegistrationRequest);

            // When & Then
            mockMvc.perform(get("/api/v1/users/{userId}", userId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.username", is(userRegistrationRequest.getUsername())))
                    .andExpect(jsonPath("$.email", is(userRegistrationRequest.getEmail())));
        }

        @Test
        void whenUserNotFound_shouldReturnInternalServerError() throws Exception {
            // Given
            String userId = "nonexistent";
            given(userService.getUserById(userId))
                    .willThrow(new RuntimeException("User not found"));

            // When & Then
            mockMvc.perform(get("/api/v1/users/{userId}", userId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().string("false"));
        }
    }

    @Nested
    @WithMockUser(username = "testuser", roles = {"USER"})
    class UpdateUser {

        @Test
        void shouldUpdateUserSuccessfully() throws Exception {
            // Given
            String userId = "123e4567-e89b-12d3-a456-426614174000";
            given(userService.updateUser(eq(userId), any(UserUpdateDto.class)))
                    .willReturn(userRegistrationRequest);

            // When & Then
            mockMvc.perform(put("/api/v1/users/{userId}", userId)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(userUpdateDto)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message", is("User updated successfully")))
                    .andExpect(jsonPath("$.user.username", is(userRegistrationRequest.getUsername())));
        }

        @Test
        void whenInvalidUserIdFormat_shouldReturnBadRequest() throws Exception {
            // Given
            String invalidUserId = "invalid-id";

            // When & Then
            mockMvc.perform(put("/api/v1/users/{userId}", invalidUserId)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(userUpdateDto)))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @Test
        void whenUserIdIsNaN_shouldReturnBadRequest() throws Exception {
            // Given
            String nanUserId = "NaN";

            // When & Then
            mockMvc.perform(put("/api/v1/users/{userId}", nanUserId)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(userUpdateDto)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error", is("Invalid user ID")))
                    .andExpect(jsonPath("$.message", is("User ID cannot be NaN")));
        }

        @Test
        void whenUnauthorizedUser_shouldReturnForbidden() throws Exception {
            // Given
            String differentUserId = "different-user-id";

            // When & Then
            mockMvc.perform(put("/api/v1/users/{userId}", differentUserId)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(userUpdateDto)))
                    .andDo(print())
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.error", is("You don't have permission to update this profile")));
        }
    }

    @Nested
    @WithMockUser(username = "doctor", roles = {"DOCTOR"})
    class CompleteDoctorProfile {

        @Test
        void shouldCompleteDoctorProfileSuccessfully() throws Exception {
            // Given
            String userId = "doctor-user-id";
            given(userService.completeDoctorProfile(eq(userId), any(DoctorProfileDto.class)))
                    .willReturn(userData);

            // When & Then
            mockMvc.perform(put("/api/v1/users/{userId}/complete-profile", userId)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(doctorProfileDto)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message", is("Doctor profile completed successfully")))
                    .andExpect(jsonPath("$.userData.speciality", is(userData.getSpeciality())))
                    .andExpect(jsonPath("$.userData.diploma", is(userData.getDiploma())));
        }

        @Test
        void whenUnauthorizedUser_shouldReturnForbidden() throws Exception {
            // Given
            String differentUserId = "different-doctor-id";

            // When & Then
            mockMvc.perform(put("/api/v1/users/{userId}/complete-profile", differentUserId)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(doctorProfileDto)))
                    .andDo(print())
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.error", is("You don't have permission to update this profile")));
        }

        @Test
        void whenInvalidData_shouldReturnBadRequest() throws Exception {
            // Given
            String userId = "doctor-user-id";
            DoctorProfileDto invalidDto = new DoctorProfileDto(); // Missing required fields
            given(userService.completeDoctorProfile(eq(userId), any(DoctorProfileDto.class)))
                    .willThrow(new RuntimeException("Validation error"));

            // When & Then
            mockMvc.perform(put("/api/v1/users/{userId}/complete-profile", userId)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidDto)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error", is("Validation error")));
        }
    }

    @Nested
    @WithMockUser(username = "user", roles = {"USER"})
    class UploadProfilePhoto {

        @Test
        void shouldUploadProfilePhotoSuccessfully() throws Exception {
            // Given
            String userId = "user-123";
            String expectedPhotoUrl = "http://example.com/photos/profile.jpg";
            given(userService.uploadProfilePhoto(eq(userId), any()))
                    .willReturn(expectedPhotoUrl);

            MockMultipartFile file = new MockMultipartFile(
                    "file",
                    "profile.jpg",
                    "image/jpeg",
                    "test image content".getBytes()
            );

            // When & Then
            mockMvc.perform(multipart("/api/v1/users/{userId}/profile-photo", userId)
                            .file(file)
                            .with(csrf())
                            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message", is("Profile photo uploaded successfully")))
                    .andExpect(jsonPath("$.profilePhotoUrl", is(expectedPhotoUrl)));
        }

        @Test
        void whenUnauthorizedUser_shouldReturnForbidden() throws Exception {
            // Given
            String differentUserId = "different-user-id";

            MockMultipartFile file = new MockMultipartFile(
                    "file",
                    "profile.jpg",
                    "image/jpeg",
                    "test image content".getBytes()
            );

            // When & Then
            mockMvc.perform(multipart("/api/v1/users/{userId}/profile-photo", differentUserId)
                            .file(file)
                            .with(csrf())
                            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                    .andDo(print())
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.error", is("You don't have permission to update this profile")));
        }

        @Test
        void whenUploadFails_shouldReturnBadRequest() throws Exception {
            // Given
            String userId = "user-123";
            given(userService.uploadProfilePhoto(eq(userId), any()))
                    .willThrow(new RuntimeException("Upload failed"));

            MockMultipartFile file = new MockMultipartFile(
                    "file",
                    "profile.jpg",
                    "image/jpeg",
                    "test image content".getBytes()
            );

            // When & Then
            mockMvc.perform(multipart("/api/v1/users/{userId}/profile-photo", userId)
                            .file(file)
                            .with(csrf())
                            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error", is("Upload failed")));
        }
    }
}