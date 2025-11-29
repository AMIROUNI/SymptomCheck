package com.symptomcheck.doctorservice.functional.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.symptomcheck.doctorservice.controllers.DoctorProfileController;
import com.symptomcheck.doctorservice.dtos.AvailabilityHealthDto;
import com.symptomcheck.doctorservice.services.DoctorAvailabilityService;
import com.symptomcheck.doctorservice.services.HealthcareServiceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DoctorProfileController.class)
class DoctorAvailabilityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DoctorAvailabilityService availabilityService;

    @MockBean
    private HealthcareServiceService healthcareServiceService;

    private UUID doctorId;
    private AvailabilityHealthDto availabilityHealthDto;

    @BeforeEach
    void setUp() {
        doctorId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        // Create valid AvailabilityHealthDto with all fields
        availabilityHealthDto = new AvailabilityHealthDto();
        availabilityHealthDto.setDoctorId(doctorId);
        availabilityHealthDto.setDaysOfWeek(Arrays.asList(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY));
        availabilityHealthDto.setStartTime(LocalTime.of(9, 0));
        availabilityHealthDto.setEndTime(LocalTime.of(17, 0));
        availabilityHealthDto.setName("Cardiology Consultation");
        availabilityHealthDto.setDescription("Comprehensive heart health consultation with ECG and blood pressure monitoring. This service includes a thorough examination of cardiovascular health.");
        availabilityHealthDto.setCategory("Cardiology");
        availabilityHealthDto.setImageUrl("https://example.com/images/cardiology.jpg");
        availabilityHealthDto.setDurationMinutes(30);
        availabilityHealthDto.setPrice(150.0);
    }

    @Nested
    class GetProfileStatusTests {

        @Test
        @WithMockUser(roles = "DOCTOR")
        void shouldReturnTrueWhenProfileIsComplete() throws Exception {
            // Given
            when(availabilityService.existsByDoctorId(doctorId)).thenReturn(true);
            when(healthcareServiceService.existsByDoctorId(doctorId.toString())).thenReturn(true);

            // When
            ResultActions result = mockMvc.perform(get("/api/v1/doctor/profile/{doctorId}/profile-status", doctorId));

            // Then
            result.andExpect(status().isOk())
                    .andExpect(content().string("true"));
        }

        @Test
        @WithMockUser(roles = "DOCTOR")
        void shouldReturnFalseWhenAvailabilityNotCompleted() throws Exception {
            // Given
            when(availabilityService.existsByDoctorId(doctorId)).thenReturn(false);
            when(healthcareServiceService.existsByDoctorId(doctorId.toString())).thenReturn(true);

            // When
            ResultActions result = mockMvc.perform(get("/api/v1/doctor/profile/{doctorId}/profile-status", doctorId));

            // Then
            result.andExpect(status().isOk())
                    .andExpect(content().string("false"));
        }

        @Test
        @WithMockUser(roles = "DOCTOR")
        void shouldReturnFalseWhenHealthcareServiceNotCompleted() throws Exception {
            // Given
            when(availabilityService.existsByDoctorId(doctorId)).thenReturn(true);
            when(healthcareServiceService.existsByDoctorId(doctorId.toString())).thenReturn(false);

            // When
            ResultActions result = mockMvc.perform(get("/api/v1/doctor/profile/{doctorId}/profile-status", doctorId));

            // Then
            result.andExpect(status().isOk())
                    .andExpect(content().string("false"));
        }

        @Test
        @WithMockUser(roles = "DOCTOR")
        void shouldReturnFalseWhenBothNotCompleted() throws Exception {
            // Given
            when(availabilityService.existsByDoctorId(doctorId)).thenReturn(false);
            when(healthcareServiceService.existsByDoctorId(doctorId.toString())).thenReturn(false);

            // When
            ResultActions result = mockMvc.perform(get("/api/v1/doctor/profile/{doctorId}/profile-status", doctorId));

            // Then
            result.andExpect(status().isOk())
                    .andExpect(content().string("false"));
        }

        @Test
        void shouldReturnUnauthorizedWhenNoAuthentication() throws Exception {
            // When
            ResultActions result = mockMvc.perform(get("/api/v1/doctor/profile/{doctorId}/profile-status", doctorId));

            // Then
            result.andExpect(status().isUnauthorized());
        }


    }

    @Nested
    class CompleteProfileTests {

        @Test
        @WithMockUser(roles = "DOCTOR")
        void shouldCompleteProfileSuccessfully() throws Exception {
            // Given
            when(availabilityService.createAvailabilityHealth(any(AvailabilityHealthDto.class)))
                    .thenReturn(true);

            // When
            ResultActions result = mockMvc.perform(post("/api/v1/doctor/profile/completeprofile")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(availabilityHealthDto)));

            // Then
            result.andExpect(status().isOk())
                    .andExpect(content().string("true"));
        }

        @Test
        @WithMockUser(roles = "DOCTOR")
        void shouldHandleCompleteAvailabilityHealthDtoSuccessfully() throws Exception {
            // Given - Complete DTO with all optional fields populated
            AvailabilityHealthDto completeDto = new AvailabilityHealthDto();
            completeDto.setDoctorId(doctorId);
            completeDto.setDaysOfWeek(Arrays.asList(
                    DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
                    DayOfWeek.THURSDAY, DayOfWeek.FRIDAY
            ));
            completeDto.setStartTime(LocalTime.of(8, 0));
            completeDto.setEndTime(LocalTime.of(18, 0));
            completeDto.setName("General Medicine Consultation");
            completeDto.setDescription("Comprehensive health checkup including physical examination, blood pressure check, heart rate monitoring, and detailed health counseling. This service covers all basic health assessments.");
            completeDto.setCategory("General Medicine");
            completeDto.setImageUrl("https://example.com/images/general-medicine.jpg");
            completeDto.setDurationMinutes(45);
            completeDto.setPrice(120.0);

            when(availabilityService.createAvailabilityHealth(any(AvailabilityHealthDto.class)))
                    .thenReturn(true);

            // When
            ResultActions result = mockMvc.perform(post("/api/v1/doctor/profile/completeprofile")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(completeDto)));

            // Then
            result.andExpect(status().isOk())
                    .andExpect(content().string("true"));
        }

        @Test
        @WithMockUser(roles = "DOCTOR")
        void shouldHandleWeekendAvailability() throws Exception {
            // Given - Weekend availability
            AvailabilityHealthDto weekendDto = new AvailabilityHealthDto();
            weekendDto.setDoctorId(doctorId);
            weekendDto.setDaysOfWeek(Arrays.asList(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY));
            weekendDto.setStartTime(LocalTime.of(10, 0));
            weekendDto.setEndTime(LocalTime.of(16, 0));
            weekendDto.setName("Weekend Emergency Consultation");
            weekendDto.setDescription("Emergency medical consultation available on weekends for urgent health concerns that cannot wait until regular business hours.");
            weekendDto.setCategory("Emergency Medicine");
            weekendDto.setImageUrl("https://example.com/images/emergency.jpg");
            weekendDto.setDurationMinutes(60);
            weekendDto.setPrice(200.0);

            when(availabilityService.createAvailabilityHealth(any(AvailabilityHealthDto.class)))
                    .thenReturn(true);

            // When
            ResultActions result = mockMvc.perform(post("/api/v1/doctor/profile/completeprofile")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(weekendDto)));

            // Then
            result.andExpect(status().isOk())
                    .andExpect(content().string("true"));
        }

        @Test
        @WithMockUser(roles = "DOCTOR")
        void shouldHandleEveningHours() throws Exception {
            // Given - Evening availability
            AvailabilityHealthDto eveningDto = new AvailabilityHealthDto();
            eveningDto.setDoctorId(doctorId);
            eveningDto.setDaysOfWeek(Arrays.asList(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY));
            eveningDto.setStartTime(LocalTime.of(17, 0));
            eveningDto.setEndTime(LocalTime.of(21, 0));
            eveningDto.setName("Evening Consultation");
            eveningDto.setDescription("After-hours consultation service designed for working patients who cannot visit during regular daytime hours.");
            eveningDto.setCategory("General Medicine");
            eveningDto.setImageUrl("https://example.com/images/evening-consultation.jpg");
            eveningDto.setDurationMinutes(30);
            eveningDto.setPrice(180.0);

            when(availabilityService.createAvailabilityHealth(any(AvailabilityHealthDto.class)))
                    .thenReturn(true);

            // When
            ResultActions result = mockMvc.perform(post("/api/v1/doctor/profile/completeprofile")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(eveningDto)));

            // Then
            result.andExpect(status().isOk())
                    .andExpect(content().string("true"));
        }

        @Test
        @WithMockUser(roles = "DOCTOR")
        void shouldHandleMinimalRequiredFields() throws Exception {
            // Given - Only required fields (doctorId, daysOfWeek, startTime, endTime)
            AvailabilityHealthDto minimalDto = new AvailabilityHealthDto();
            minimalDto.setDoctorId(doctorId);
            minimalDto.setDaysOfWeek(Arrays.asList(DayOfWeek.MONDAY));
            minimalDto.setStartTime(LocalTime.of(9, 0));
            minimalDto.setEndTime(LocalTime.of(17, 0));
            // Optional fields are null/empty

            when(availabilityService.createAvailabilityHealth(any(AvailabilityHealthDto.class)))
                    .thenReturn(true);

            // When
            ResultActions result = mockMvc.perform(post("/api/v1/doctor/profile/completeprofile")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(minimalDto)));

            // Then
            result.andExpect(status().isOk())
                    .andExpect(content().string("true"));
        }

        @Test
        @WithMockUser(roles = "DOCTOR")
        void shouldReturnInternalServerErrorWhenServiceFails() throws Exception {
            // Given
            when(availabilityService.createAvailabilityHealth(any(AvailabilityHealthDto.class)))
                    .thenThrow(new RuntimeException("Failed to create availability"));

            // When
            ResultActions result = mockMvc.perform(post("/api/v1/doctor/profile/completeprofile")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(availabilityHealthDto)));

            // Then
            result.andExpect(status().isInternalServerError());
        }

        @Test
        @WithMockUser(roles = "DOCTOR")
        void shouldHandleValidationErrorsForMissingRequiredFields() throws Exception {
            // Given - DTO with missing @NotNull fields (daysOfWeek, startTime, endTime)
            AvailabilityHealthDto invalidDto = new AvailabilityHealthDto();
            invalidDto.setDoctorId(doctorId);
            // daysOfWeek is null - violates @NotNull
            // startTime is null - violates @NotNull
            // endTime is null - violates @NotNull
            invalidDto.setName("Test Service");
            invalidDto.setDescription("Test description");
            invalidDto.setCategory("Test");
            invalidDto.setDurationMinutes(30);
            invalidDto.setPrice(100.0);

            // When
            ResultActions result = mockMvc.perform(post("/api/v1/doctor/profile/completeprofile")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidDto)));

            // Then - Should return 400 Bad Request due to validation errors
            result.andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser(roles = "DOCTOR")
        void shouldHandleLongDescription() throws Exception {
            // Given - DTO with long description (testing @Column(length = 1000))
            AvailabilityHealthDto longDescDto = new AvailabilityHealthDto();
            longDescDto.setDoctorId(doctorId);
            longDescDto.setDaysOfWeek(Arrays.asList(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY));
            longDescDto.setStartTime(LocalTime.of(9, 0));
            longDescDto.setEndTime(LocalTime.of(17, 0));
            longDescDto.setName("Comprehensive Health Service");
            longDescDto.setDescription("This is a very long description that tests the 1000 character limit. ".repeat(20)); // ~500 chars
            longDescDto.setCategory("Comprehensive");
            longDescDto.setDurationMinutes(60);
            longDescDto.setPrice(200.0);

            when(availabilityService.createAvailabilityHealth(any(AvailabilityHealthDto.class)))
                    .thenReturn(true);

            // When
            ResultActions result = mockMvc.perform(post("/api/v1/doctor/profile/completeprofile")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(longDescDto)));

            // Then
            result.andExpect(status().isOk())
                    .andExpect(content().string("true"));
        }

        @Test
        void shouldReturnUnauthorizedWhenNoAuthentication() throws Exception {
            // When
            ResultActions result = mockMvc.perform(post("/api/v1/doctor/profile/completeprofile")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(availabilityHealthDto)));

            // Then
            result.andExpect(status().isUnauthorized());
        }


        @Test
        @WithMockUser(roles = "DOCTOR")
        void shouldHandleInvalidJson() throws Exception {
            // Given - Invalid JSON content
            String invalidJson = "{ invalid json }";

            // When
            ResultActions result = mockMvc.perform(post("/api/v1/doctor/profile/completeprofile")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(invalidJson));

            // Then
            result.andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser(roles = "DOCTOR")
        void shouldHandleMissingCsrfToken() throws Exception {
            // When - Request without CSRF token
            ResultActions result = mockMvc.perform(post("/api/v1/doctor/profile/completeprofile")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(availabilityHealthDto)));

            // Then
            result.andExpect(status().isForbidden());
        }
    }
}