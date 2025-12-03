package com.symptomcheck.doctorservice.functional.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.symptomcheck.doctorservice.controllers.DoctorDashboardController;
import com.symptomcheck.doctorservice.dtos.dashboarddto.*;
import com.symptomcheck.doctorservice.services.DoctorDashboardService;
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

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DoctorDashboardController.class)
class DoctorDashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DoctorDashboardService dashboardService;

    private UUID doctorId;
    private DoctorDashboardDTO mockDashboard;

    @BeforeEach
    void setUp() {
        doctorId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        // Create nested DTO objects with correct constructors
        DoctorStatsDTO stats = new DoctorStatsDTO(5L, 20L, true, 85);

        List<DoctorServiceDTO> services = Arrays.asList(
                new DoctorServiceDTO(1L, "Cardiology Consultation", "Cardiology", 150.0, 30, "Heart health consultation"),
                new DoctorServiceDTO(2L, "General Checkup", "General Medicine", 100.0, 45, "Comprehensive health check")
        );

        List<DoctorAvailabilityDTO> availability = Arrays.asList(
                new DoctorAvailabilityDTO("avail-1", "Monday", "09:00", "17:00", true),
                new DoctorAvailabilityDTO("avail-2", "Wednesday", "09:00", "17:00", true)
        );

        ProfileCompletionDTO profileCompletion = new ProfileCompletionDTO(true, true, true, 85);

        mockDashboard = new DoctorDashboardDTO(stats, services, availability, profileCompletion);
    }

    @Nested
    class GetDoctorDashboardTests {

        @Test
        @WithMockUser(roles = "DOCTOR")
        void shouldReturnDoctorDashboardSuccessfully() throws Exception {
            // Given
            when(dashboardService.getDoctorDashboard(doctorId)).thenReturn(mockDashboard);

            // When
            ResultActions result = mockMvc.perform(get("/api/v1/doctor/dashboard/{doctorId}", doctorId));

            // Then
            result.andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.stats.totalServices").value(5))
                    .andExpect(jsonPath("$.stats.totalAvailabilitySlots").value(20))
                    .andExpect(jsonPath("$.stats.isProfileComplete").value(true))
                    .andExpect(jsonPath("$.stats.completionPercentage").value(85))
                    .andExpect(jsonPath("$.services[0].id").value(1))
                    .andExpect(jsonPath("$.services[0].name").value("Cardiology Consultation"))
                    .andExpect(jsonPath("$.services[0].category").value("Cardiology"))
                    .andExpect(jsonPath("$.services[0].price").value(150.0))
                    .andExpect(jsonPath("$.services[0].duration").value(30))
                    .andExpect(jsonPath("$.services[1].name").value("General Checkup"))
                    .andExpect(jsonPath("$.availability[0].id").value("avail-1"))
                    .andExpect(jsonPath("$.availability[0].dayOfWeek").value("Monday"))
                    .andExpect(jsonPath("$.availability[0].startTime").value("09:00"))
                    .andExpect(jsonPath("$.availability[0].endTime").value("17:00"))
                    .andExpect(jsonPath("$.availability[0].isActive").value(true))
                    .andExpect(jsonPath("$.profileCompletion.hasAvailability").value(true))
                    .andExpect(jsonPath("$.profileCompletion.hasServices").value(true))
                    .andExpect(jsonPath("$.profileCompletion.hasBasicInfo").value(true))
                    .andExpect(jsonPath("$.profileCompletion.completionPercentage").value(85));
        }

        @Test
        @WithMockUser(roles = "DOCTOR")
        void shouldReturnEmptyDashboardWhenNoDataFound() throws Exception {
            // Given
            DoctorStatsDTO emptyStats = new DoctorStatsDTO(0L, 0L, false, 0);
            ProfileCompletionDTO emptyProfile = new ProfileCompletionDTO(false, false, false, 0);
            DoctorDashboardDTO emptyDashboard = new DoctorDashboardDTO(
                    emptyStats,
                    Arrays.asList(),
                    Arrays.asList(),
                    emptyProfile
            );

            when(dashboardService.getDoctorDashboard(doctorId)).thenReturn(emptyDashboard);

            // When
            ResultActions result = mockMvc.perform(get("/api/v1/doctor/dashboard/{doctorId}", doctorId));

            // Then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.stats.totalServices").value(0))
                    .andExpect(jsonPath("$.stats.totalAvailabilitySlots").value(0))
                    .andExpect(jsonPath("$.stats.isProfileComplete").value(false))
                    .andExpect(jsonPath("$.stats.completionPercentage").value(0))
                    .andExpect(jsonPath("$.services").isEmpty())
                    .andExpect(jsonPath("$.availability").isEmpty())
                    .andExpect(jsonPath("$.profileCompletion.hasAvailability").value(false))
                    .andExpect(jsonPath("$.profileCompletion.hasServices").value(false))
                    .andExpect(jsonPath("$.profileCompletion.hasBasicInfo").value(false))
                    .andExpect(jsonPath("$.profileCompletion.completionPercentage").value(0));
        }

        @Test
        @WithMockUser(roles = "DOCTOR")
        void shouldReturnPartialProfileDashboard() throws Exception {
            // Given - Doctor has services but no availability
            DoctorStatsDTO partialStats = new DoctorStatsDTO(3L, 0L, false, 50);
            List<DoctorServiceDTO> services = Arrays.asList(
                    new DoctorServiceDTO(1L, "Consultation", "General", 100.0, 30, "Basic consultation")
            );
            ProfileCompletionDTO partialProfile = new ProfileCompletionDTO(false, true, true, 50);
            DoctorDashboardDTO partialDashboard = new DoctorDashboardDTO(
                    partialStats, services, Arrays.asList(), partialProfile
            );

            when(dashboardService.getDoctorDashboard(doctorId)).thenReturn(partialDashboard);

            // When
            ResultActions result = mockMvc.perform(get("/api/v1/doctor/dashboard/{doctorId}", doctorId));

            // Then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.stats.totalServices").value(3))
                    .andExpect(jsonPath("$.stats.totalAvailabilitySlots").value(0))
                    .andExpect(jsonPath("$.stats.isProfileComplete").value(false))
                    .andExpect(jsonPath("$.stats.completionPercentage").value(50))
                    .andExpect(jsonPath("$.services").isArray())
                    .andExpect(jsonPath("$.availability").isEmpty())
                    .andExpect(jsonPath("$.profileCompletion.hasAvailability").value(false))
                    .andExpect(jsonPath("$.profileCompletion.hasServices").value(true))
                    .andExpect(jsonPath("$.profileCompletion.completionPercentage").value(50));
        }

        @Test
        @WithMockUser(roles = "DOCTOR")
        void shouldReturnInternalServerErrorWhenServiceFails() throws Exception {
            // Given
            when(dashboardService.getDoctorDashboard(doctorId))
                    .thenThrow(new RuntimeException("Database connection failed"));

            // When
            ResultActions result = mockMvc.perform(get("/api/v1/doctor/dashboard/{doctorId}", doctorId));

            // Then
            result.andExpect(status().isInternalServerError());
        }

        @Test
        void shouldReturnUnauthorizedWhenNoAuthentication() throws Exception {
            // When
            ResultActions result = mockMvc.perform(get("/api/v1/doctor/dashboard/{doctorId}", doctorId));

            // Then
            result.andExpect(status().isUnauthorized());
        }



        @Test
        @WithMockUser(roles = "DOCTOR")
        void shouldHandleInvalidDoctorIdFormat() throws Exception {
            // When - Invalid UUID format
            ResultActions result = mockMvc.perform(get("/api/v1/doctor/dashboard/{doctorId}", "invalid-uuid"));

            // Then
            result.andExpect(status().isBadRequest());
        }
    }

    @Nested
    class GetServiceCategoriesTests {

        @Test
        @WithMockUser(roles = "DOCTOR")
        void shouldReturnServiceCategoriesSuccessfully() throws Exception {
            // Given
            List<String> categories = Arrays.asList("Cardiology", "General Medicine", "Pediatrics");
            when(dashboardService.getServiceCategories(doctorId)).thenReturn(categories);

            // When
            ResultActions result = mockMvc.perform(get("/api/v1/doctor/dashboard/{doctorId}/service-categories", doctorId));

            // Then
            result.andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$[0]").value("Cardiology"))
                    .andExpect(jsonPath("$[1]").value("General Medicine"))
                    .andExpect(jsonPath("$[2]").value("Pediatrics"));
        }

        @Test
        @WithMockUser(roles = "DOCTOR")
        void shouldReturnEmptyCategoriesList() throws Exception {
            // Given
            when(dashboardService.getServiceCategories(doctorId)).thenReturn(Arrays.asList());

            // When
            ResultActions result = mockMvc.perform(get("/api/v1/doctor/dashboard/{doctorId}/service-categories", doctorId));

            // Then
            result.andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isEmpty());
        }

        @Test
        @WithMockUser(roles = "DOCTOR")
        void shouldReturnInternalServerErrorWhenCategoriesServiceFails() throws Exception {
            // Given
            when(dashboardService.getServiceCategories(doctorId))
                    .thenThrow(new RuntimeException("Service unavailable"));

            // When
            ResultActions result = mockMvc.perform(get("/api/v1/doctor/dashboard/{doctorId}/service-categories", doctorId));

            // Then
            result.andExpect(status().isInternalServerError());
        }
    }

    @Nested
    class GetProfileStatusTests {

        @Test
        @WithMockUser(roles = "DOCTOR")
        void shouldReturnTrueWhenProfileIsComplete() throws Exception {
            // Given
            when(dashboardService.isProfileComplete(doctorId)).thenReturn(true);

            // When
            ResultActions result = mockMvc.perform(get("/api/v1/doctor/dashboard/{doctorId}/profile-status", doctorId));

            // Then
            result.andExpect(status().isOk())
                    .andExpect(content().string("true"));
        }

        @Test
        @WithMockUser(roles = "DOCTOR")
        void shouldReturnFalseWhenProfileIsIncomplete() throws Exception {
            // Given
            when(dashboardService.isProfileComplete(doctorId)).thenReturn(false);

            // When
            ResultActions result = mockMvc.perform(get("/api/v1/doctor/dashboard/{doctorId}/profile-status", doctorId));

            // Then
            result.andExpect(status().isOk())
                    .andExpect(content().string("false"));
        }

        @Test
        @WithMockUser(roles = "DOCTOR")
        void shouldReturnInternalServerErrorWhenProfileStatusServiceFails() throws Exception {
            // Given
            when(dashboardService.isProfileComplete(doctorId))
                    .thenThrow(new RuntimeException("Profile validation error"));

            // When
            ResultActions result = mockMvc.perform(get("/api/v1/doctor/dashboard/{doctorId}/profile-status", doctorId));

            // Then
            result.andExpect(status().isInternalServerError());
        }
    }

    @Nested
    class GetServicesCountTests {

        @Test
        @WithMockUser(roles = "DOCTOR")
        void shouldReturnServicesCountSuccessfully() throws Exception {
            // Given
            when(dashboardService.getTotalServicesCount(doctorId)).thenReturn(15L);

            // When
            ResultActions result = mockMvc.perform(get("/api/v1/doctor/dashboard/{doctorId}/services-count", doctorId));

            // Then
            result.andExpect(status().isOk())
                    .andExpect(content().string("15"));
        }

        @Test
        @WithMockUser(roles = "DOCTOR")
        void shouldReturnZeroWhenNoServices() throws Exception {
            // Given
            when(dashboardService.getTotalServicesCount(doctorId)).thenReturn(0L);

            // When
            ResultActions result = mockMvc.perform(get("/api/v1/doctor/dashboard/{doctorId}/services-count", doctorId));

            // Then
            result.andExpect(status().isOk())
                    .andExpect(content().string("0"));
        }

        @Test
        @WithMockUser(roles = "DOCTOR")
        void shouldReturnInternalServerErrorWhenServicesCountFails() throws Exception {
            // Given
            when(dashboardService.getTotalServicesCount(doctorId))
                    .thenThrow(new RuntimeException("Count calculation failed"));

            // When
            ResultActions result = mockMvc.perform(get("/api/v1/doctor/dashboard/{doctorId}/services-count", doctorId));

            // Then
            result.andExpect(status().isInternalServerError());
        }
    }

    @Nested
    class GetAvailabilitySlotsTests {

        @Test
        @WithMockUser(roles = "DOCTOR")
        void shouldReturnAvailabilitySlotsSuccessfully() throws Exception {
            // Given
            when(dashboardService.getTotalAvailabilitySlots(doctorId)).thenReturn(42L);

            // When
            ResultActions result = mockMvc.perform(get("/api/v1/doctor/dashboard/{doctorId}/availability-slots", doctorId));

            // Then
            result.andExpect(status().isOk())
                    .andExpect(content().string("42"));
        }

        @Test
        @WithMockUser(roles = "DOCTOR")
        void shouldReturnZeroWhenNoAvailabilitySlots() throws Exception {
            // Given
            when(dashboardService.getTotalAvailabilitySlots(doctorId)).thenReturn(0L);

            // When
            ResultActions result = mockMvc.perform(get("/api/v1/doctor/dashboard/{doctorId}/availability-slots", doctorId));

            // Then
            result.andExpect(status().isOk())
                    .andExpect(content().string("0"));
        }

        @Test
        @WithMockUser(roles = "DOCTOR")
        void shouldReturnInternalServerErrorWhenAvailabilitySlotsServiceFails() throws Exception {
            // Given
            when(dashboardService.getTotalAvailabilitySlots(doctorId))
                    .thenThrow(new RuntimeException("Slot calculation error"));

            // When
            ResultActions result = mockMvc.perform(get("/api/v1/doctor/dashboard/{doctorId}/availability-slots", doctorId));

            // Then
            result.andExpect(status().isInternalServerError());
        }
    }

    @Nested
    class EdgeCaseTests {

        @Test
        @WithMockUser(roles = "DOCTOR")
        void shouldHandleNonExistentDoctorId() throws Exception {
            // Given
            UUID nonExistentDoctorId = UUID.fromString("99999999-9999-9999-9999-999999999999");
            DoctorStatsDTO emptyStats = new DoctorStatsDTO(0L, 0L, false, 0);
            ProfileCompletionDTO emptyProfile = new ProfileCompletionDTO(false, false, false, 0);
            DoctorDashboardDTO emptyDashboard = new DoctorDashboardDTO(
                    emptyStats, Arrays.asList(), Arrays.asList(), emptyProfile
            );

            when(dashboardService.getDoctorDashboard(nonExistentDoctorId)).thenReturn(emptyDashboard);

            // When
            ResultActions result = mockMvc.perform(get("/api/v1/doctor/dashboard/{doctorId}", nonExistentDoctorId));

            // Then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.stats.totalServices").value(0))
                    .andExpect(jsonPath("$.stats.isProfileComplete").value(false))
                    .andExpect(jsonPath("$.profileCompletion.completionPercentage").value(0));
        }



        @Test
        @WithMockUser(roles = "DOCTOR")
        void shouldHandleLargeNumbersInCounts() throws Exception {
            // Given
            when(dashboardService.getTotalServicesCount(doctorId)).thenReturn(999999L);
            when(dashboardService.getTotalAvailabilitySlots(doctorId)).thenReturn(999999L);

            // When
            ResultActions servicesResult = mockMvc.perform(get("/api/v1/doctor/dashboard/{doctorId}/services-count", doctorId));
            ResultActions slotsResult = mockMvc.perform(get("/api/v1/doctor/dashboard/{doctorId}/availability-slots", doctorId));

            // Then
            servicesResult.andExpect(status().isOk())
                    .andExpect(content().string("999999"));
            slotsResult.andExpect(status().isOk())
                    .andExpect(content().string("999999"));
        }
    }
}