package com.SymptomCheck.userservice.functional.api;

import com.SymptomCheck.userservice.config.KeycloakSecurityConfig;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.Instant;
import java.util.*;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@ActiveProfiles("test")
@Import(KeycloakSecurityConfig.class)
class UserControllerApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private UserDataService userDataService;

    private UserRegistrationRequest regRequest;
    private UserUpdateDto updateDto;
    private DoctorProfileDto doctorDto;
    private UserData userData;

    private static final String CURRENT_USER_ID = "gc6274gg-730g-44g0-9245-17gdg9054fe8";
    private static final String CURRENT_USERNAME = "testuser";
    private static final String OTHER_USER_ID = "auth0|other-user";

    private static final String CURRENT_USER_ID2 = "fc6274ff-730a-44f0-9245-17ada9054fe8";
    private     Jwt jwt ;

    private String patientId;

    @BeforeEach
    void setUp() {
        patientId = "patient-123";

        jwt =  Jwt.withTokenValue("mock-jwt-token")
                        .header("alg", "none")
                        .claim("sub", patientId)
                        .claim("realm_access", Map.of("roles", List.of("PATIENT")))
                        .build();

        //  Valid UserRegistrationRequest with all required fields
        regRequest = new UserRegistrationRequest();
        regRequest.setUsername("newuser");
        regRequest.setEmail("new@example.com");
        regRequest.setPassword("pass123");
        regRequest.setFirstName("New");
        regRequest.setLastName("User");
        regRequest.setPhoneNumber("+1234567890");
        regRequest.setRole("PATIENT");

        //  Valid UserUpdateDto with all required fields
        updateDto = new UserUpdateDto();
        updateDto.setFirstName("Updated");
        updateDto.setLastName("UpdatedLastName");  //
        updateDto.setEmail("updated@example.com");
        updateDto.setPhoneNumber("+9876543210");

        //  Valid DoctorProfileDto with all required fields
        doctorDto = new DoctorProfileDto();
        doctorDto.setId("auth0|doc123");  // REQUIRED
        doctorDto.setSpeciality("Cardiology");
        doctorDto.setDiploma("MD");
        doctorDto.setClinicId(1L);
        doctorDto.setDescription("Experienced cardiologist");

        //  Valid UserData for mocking
        userData = new UserData();
        userData.setId(CURRENT_USER_ID);
        userData.setPhoneNumber("+1234567890");
        userData.setProfilePhotoUrl("https://photo.com/me.jpg");
        userData.setProfileComplete(true);
        userData.setSpeciality("Cardiology");
        userData.setDescription("Heart specialist");
        userData.setDiploma("MD");
        userData.setClinicId(1L);
        userData.setCreatedAt(Instant.now().minusSeconds(86400));
        userData.setUpdatedAt(Instant.now());
    }

    // ──────────────────────────────────────────────────────────────
    // REGISTER USER
    // ──────────────────────────────────────────────────────────────
    @Nested
    class RegisterUser {
        @Test
        void shouldRegisterSuccessfully() throws Exception {
            given(userService.registerMyUser(any(), any())).willReturn("kc-123");

            MockMultipartFile userJson = new MockMultipartFile("user", "", "application/json",
                    objectMapper.writeValueAsBytes(regRequest));
            MockMultipartFile file = new MockMultipartFile("file", "photo.jpg", "image/jpeg", "data".getBytes());

            mockMvc.perform(multipart("/api/v1/users/register")
                            .file(userJson)
                            .file(file)
                            .with(csrf()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("User registered successfully in Keycloak with ALL attributes"))
                    .andExpect(jsonPath("$.keycloakUserId").value("kc-123"))
                    .andExpect(jsonPath("$.username").value("newuser"));
        }

        @Test
        void shouldReturnBadRequestOnFailure() throws Exception {
            given(userService.registerMyUser(any(), any())).willThrow(new RuntimeException("Keycloak error"));

            MockMultipartFile userJson = new MockMultipartFile("user", "", "application/json",
                    objectMapper.writeValueAsBytes(regRequest));

            mockMvc.perform(multipart("/api/v1/users/register")
                            .file(userJson)
                            .with(csrf()))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("Keycloak error"));
        }
    }

    // ──────────────────────────────────────────────────────────────
    // GET CURRENT USER (/me)
    // ──────────────────────────────────────────────────────────────
    @Nested
    class GetCurrentUser {
        @Test
        void shouldReturnFullProfile() throws Exception {
            given(userDataService.getUserDataById(CURRENT_USER_ID)).willReturn(Optional.of(userData));

            mockMvc.perform(get("/api/v1/users/me")
                            .with(jwt()
                                    .jwt(j -> j
                                            .subject(CURRENT_USER_ID)
                                            .claim("preferred_username", CURRENT_USERNAME)
                                            .claim("email", "test@example.com")
                                            .claim("given_name", "Test")
                                            .claim("family_name", "User")
                                            .claim("email_verified", true)
                                            .claim("realm_access", Map.of("roles", List.of("USER")))
                                    )))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(CURRENT_USER_ID))
                    .andExpect(jsonPath("$.username").value(CURRENT_USERNAME))
                    .andExpect(jsonPath("$.email").value("test@example.com"))
                    .andExpect(jsonPath("$.firstName").value("Test"))
                    .andExpect(jsonPath("$.lastName").value("User"))
                    .andExpect(jsonPath("$.phoneNumber").value("+1234567890"))
                    .andExpect(jsonPath("$.profilePhotoUrl").value("https://photo.com/me.jpg"))
                    .andExpect(jsonPath("$.isProfileComplete").value(true))
                    .andExpect(jsonPath("$.speciality").value("Cardiology"))
                    .andExpect(jsonPath("$.diploma").value("MD"))
                    .andExpect(jsonPath("$.clinicId").value(1))
                    .andExpect(jsonPath("$.description").value("Heart specialist"));
        }

        @Test
        void shouldReturnBasicInfoWhenNoUserData() throws Exception {
            given(userDataService.getUserDataById(CURRENT_USER_ID)).willReturn(Optional.empty());

            mockMvc.perform(get("/api/v1/users/me")
                            .with(jwt()
                                    .jwt(j -> j
                                            .subject(CURRENT_USER_ID)
                                            .claim("preferred_username", CURRENT_USERNAME)
                                            .claim("email", "test@example.com")
                                            .claim("given_name", "Test")
                                            .claim("family_name", "User")
                                            .claim("email_verified", false)
                                    )))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(CURRENT_USER_ID))
                    .andExpect(jsonPath("$.username").value(CURRENT_USERNAME))
                    .andExpect(jsonPath("$.isProfileComplete").value(false))
                    .andExpect(jsonPath("$.phoneNumber").value((String) null))
                    .andExpect(jsonPath("$.profilePhotoUrl").value((String) null))
                    .andExpect(jsonPath("$.speciality").value((String) null))
                    .andExpect(jsonPath("$.diploma").value((String) null))
                    .andExpect(jsonPath("$.clinicId").value((Integer) null));
        }
    }

    // ──────────────────────────────────────────────────────────────
    // UPDATE USER
    // ──────────────────────────────────────────────────────────────
    @Nested
    class UpdateUser {

        private UserUpdateDto validUpdateDto;

        @BeforeEach
        void setUp() {
            // Create valid test data that passes all validation constraints
            validUpdateDto = new UserUpdateDto();
            validUpdateDto.setFirstName("UpdatedName"); // More than 1 character
            validUpdateDto.setLastName("UpdatedLastName"); // More than 1 character
            validUpdateDto.setEmail("valid.email@example.com"); // Valid email format
            validUpdateDto.setPhoneNumber("+1234567890"); // Valid phone format
        }

        @Test
        void shouldUpdateOwnProfile() throws Exception {
            UserRegistrationRequest updatedUser = new UserRegistrationRequest();
            updatedUser.setId(CURRENT_USER_ID2);
            updatedUser.setFirstName("UpdatedName");
            updatedUser.setLastName("UpdatedLastName");
            updatedUser.setEmail("valid.email@example.com");
            updatedUser.setPhoneNumber("+1234567890");

            given(userService.updateUser(eq(CURRENT_USER_ID2), any(UserUpdateDto.class)))
                    .willReturn(updatedUser);

            mockMvc.perform(put("/api/v1/users/{id}", CURRENT_USER_ID2)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validUpdateDto)) // Use the valid DTO
                            .with(csrf())
                            .with(jwt().jwt(j -> j.subject(CURRENT_USER_ID2))))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("User updated successfully"))
                    .andExpect(jsonPath("$.user.firstName").value("UpdatedName"))
                    .andExpect(jsonPath("$.user.email").value("valid.email@example.com"));
        }

        @Test
        void shouldReturnForbiddenWhenUpdatingOtherUser() throws Exception {
            mockMvc.perform(put("/api/v1/users/{id}", OTHER_USER_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validUpdateDto)) // Use the valid DTO
                            .with(csrf())
                            .with(jwt().jwt(j -> j.subject(CURRENT_USER_ID2))))
                    .andDo(print())
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.error").value("You don't have permission to update this profile"));
        }

        @Test
        void shouldRejectNaNUserId() throws Exception {
            mockMvc.perform(put("/api/v1/users/NaN")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validUpdateDto)) // Use the valid DTO
                            .with(csrf())
                            .with(jwt().jwt(j -> j.subject(CURRENT_USER_ID2))))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("Invalid user ID"))
                    .andExpect(jsonPath("$.message").value("User ID cannot be NaN"));
        }

        @Test
        void shouldAllowAdminToUpdateOtherUser() throws Exception {
            UserRegistrationRequest updatedUser = new UserRegistrationRequest();
            updatedUser.setId(OTHER_USER_ID);
            updatedUser.setFirstName("UpdatedName");

            given(userService.updateUser(eq(OTHER_USER_ID), any(UserUpdateDto.class)))
                    .willReturn(updatedUser);

            mockMvc.perform(put("/api/v1/users/{id}", OTHER_USER_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validUpdateDto)) // Use the valid DTO
                            .with(csrf())
                            .with(jwt().jwt(j -> j
                                    .subject(CURRENT_USER_ID)
                                    .claim("realm_access", Map.of("roles", List.of("admin")))
                                    .claim("scope", "openid profile email"))))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("User updated successfully"));
        }
    }
    // ──────────────────────────────────────────────────────────────
    // UPLOAD PROFILE PHOTO
    // ──────────────────────────────────────────────────────────────
    @Nested
    class UploadProfilePhoto {
        @Test
        void shouldUploadSuccessfully() throws Exception {
            given(userService.uploadProfilePhoto(eq(CURRENT_USER_ID), any()))
                    .willReturn("https://newphoto.com/me.jpg");

            MockMultipartFile file = new MockMultipartFile("file", "img.jpg", "image/jpeg", "data".getBytes());

            mockMvc.perform(multipart("/api/v1/users/{id}/profile-photo", CURRENT_USER_ID)
                            .file(file)
                            .with(csrf())
                            .with(jwt().jwt(j -> j.subject(CURRENT_USER_ID))))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Profile photo uploaded successfully"))
                    .andExpect(jsonPath("$.profilePhotoUrl").value("https://newphoto.com/me.jpg"));
        }

        @Test
        void shouldReturnForbiddenForOtherUser() throws Exception {
            MockMultipartFile file = new MockMultipartFile("file", "img.jpg", "image/jpeg", "data".getBytes());

            mockMvc.perform(multipart("/api/v1/users/{id}/profile-photo", OTHER_USER_ID)
                            .file(file)
                            .with(csrf())
                            .with(jwt().jwt(j -> j.subject(CURRENT_USER_ID))))
                    .andDo(print())
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.error").value("You don't have permission to update this profile"));
        }

        @Test
        void shouldReturnBadRequestOnError() throws Exception {
            given(userService.uploadProfilePhoto(eq(CURRENT_USER_ID), any()))
                    .willThrow(new RuntimeException("Storage error"));

            MockMultipartFile file = new MockMultipartFile("file", "img.jpg", "image/jpeg", "data".getBytes());

            mockMvc.perform(multipart("/api/v1/users/{id}/profile-photo", CURRENT_USER_ID)
                            .file(file)
                            .with(csrf())
                            .with(jwt().jwt(j -> j.subject(CURRENT_USER_ID))))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("Storage error"));
        }
    }

    // ──────────────────────────────────────────────────────────────
    // COMPLETE DOCTOR PROFILE
    // ──────────────────────────────────────────────────────────────
    @Nested
    class CompleteDoctorProfile {
        private static final String DOCTOR_ID = "auth0|doc123";

        @Test
        void shouldCompleteProfile() throws Exception {
            UserData completedData = new UserData();
            completedData.setId(DOCTOR_ID);
            completedData.setSpeciality("Cardiology");
            completedData.setDiploma("MD");
            completedData.setClinicId(1L);
            completedData.setDescription("Experienced cardiologist");
            completedData.setProfileComplete(true);

            given(userService.completeDoctorProfile(eq(DOCTOR_ID), any(DoctorProfileDto.class)))
                    .willReturn(completedData);

            mockMvc.perform(put("/api/v1/users/{id}/complete-profile", DOCTOR_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(doctorDto))
                            .with(csrf())
                            .with(jwt().jwt(j -> j.subject(DOCTOR_ID))))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Doctor profile completed successfully"))
                    .andExpect(jsonPath("$.userData.speciality").value("Cardiology"))
                    .andExpect(jsonPath("$.userData.diploma").value("MD"))
                    .andExpect(jsonPath("$.userData.profileComplete").value(true));
        }

        @Test
        void shouldReturnForbiddenForOtherDoctor() throws Exception {
            // ✅ Create a separate doctorDto for this test with OTHER_USER_ID
            DoctorProfileDto otherDoctorDto = new DoctorProfileDto();
            otherDoctorDto.setId(OTHER_USER_ID);  // ✅ Set the correct ID
            otherDoctorDto.setSpeciality("Cardiology");
            otherDoctorDto.setDiploma("MD");
            otherDoctorDto.setClinicId(1L);
            otherDoctorDto.setDescription("Experienced cardiologist");

            mockMvc.perform(put("/api/v1/users/{id}/complete-profile", OTHER_USER_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(otherDoctorDto))
                            .with(csrf())
                            .with(jwt().jwt(j -> j.subject(CURRENT_USER_ID))))
                    .andDo(print())
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.error").value("You don't have permission to update this profile"));
        }

        @Test
        void shouldReturnErrorOnServiceFailure() throws Exception {
            //  Create a doctorDto with CURRENT_USER_ID
            DoctorProfileDto currentUserDoctorDto = new DoctorProfileDto();
            currentUserDoctorDto.setId(CURRENT_USER_ID);  //  Set the correct ID
            currentUserDoctorDto.setSpeciality("Cardiology");
            currentUserDoctorDto.setDiploma("MD");
            currentUserDoctorDto.setClinicId(1L);
            currentUserDoctorDto.setDescription("Experienced cardiologist");

            given(userService.completeDoctorProfile(eq(CURRENT_USER_ID), any(DoctorProfileDto.class)))
                    .willThrow(new RuntimeException("Database error"));

            mockMvc.perform(put("/api/v1/users/{id}/complete-profile", CURRENT_USER_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(currentUserDoctorDto))
                            .with(csrf())
                            .with(jwt().jwt(j -> j.subject(CURRENT_USER_ID))))
                    .andDo(print())
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.error").value("Database error"));
        }
    }

    // ──────────────────────────────────────────────────────────────
    // ADMIN ENDPOINTS
    // ──────────────────────────────────────────────────────────────
    @Nested
    @WithMockUser(username = "admin", roles = "ADMIN")
    class AdminEndpoints {
        @Test
        void shouldDisableUser() throws Exception {
            doNothing().when(userService).disableUser("user-123", false);

            mockMvc.perform(patch("/api/v1/users/disable/user-123")
                            .param("isEnable", "false")
                            .with(csrf()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().string("true"));
        }

        @Test
        void shouldEnableUser() throws Exception {
            doNothing().when(userService).disableUser("user-123", true);

            mockMvc.perform(patch("/api/v1/users/disable/user-123")
                            .param("isEnable", "true")
                            .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(content().string("true"));
        }

        @Test
        void shouldGetUsersByRole() throws Exception {
            UserRegistrationRequest doctor1 = new UserRegistrationRequest();
            doctor1.setUsername("doctor1");
            UserRegistrationRequest doctor2 = new UserRegistrationRequest();
            doctor2.setUsername("doctor2");

            given(userService.getUsersByRole("DOCTOR"))
                    .willReturn(List.of(doctor1, doctor2));

            mockMvc.perform(get("/api/v1/users/by-role").param("role", "DOCTOR"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$", hasSize(2)));
        }

        @Test
        void shouldGetUserById() throws Exception {
            UserRegistrationRequest user = new UserRegistrationRequest();
            user.setId("user-123");
            user.setUsername("testuser");

            given(userService.getUserById("user-123")).willReturn(user);

            mockMvc.perform(get("/api/v1/users/user-123"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value("user-123"))
                    .andExpect(jsonPath("$.username").value("testuser"));
        }
    }

    // ──────────────────────────────────────────────────────────────
    // GET USER DETAILS
    // ──────────────────────────────────────────────────────────────
    @Nested
    class GetUserDetails {
        @Test
        void shouldGetUserDetails() throws Exception {
            Map<String, Object> details = Map.of(
                    "username", "testuser",
                    "email", "test@example.com",
                    "firstName", "Test",
                    "lastName", "User"
            );

            given(userService.getUserDetails("testuser")).willReturn(details);

            mockMvc.perform(get("/api/v1/users/details/testuser"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.username").value("testuser"))
                    .andExpect(jsonPath("$.email").value("test@example.com"));
        }

        @Test
        void shouldReturnNotFoundWhenUserDoesNotExist() throws Exception {
            given(userService.getUserDetails("nonexistent")).willReturn(null);

            mockMvc.perform(get("/api/v1/users/details/nonexistent"))
                    .andDo(print())
                    .andExpect(status().isNotFound());
        }
    }
}