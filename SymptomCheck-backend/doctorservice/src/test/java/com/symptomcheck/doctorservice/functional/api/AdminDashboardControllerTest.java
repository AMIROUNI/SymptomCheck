package com.symptomcheck.doctorservice.functional.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.symptomcheck.doctorservice.controllers.AdminDashboardController;
import com.symptomcheck.doctorservice.dtos.adminDashboardDto.*;
import com.symptomcheck.doctorservice.services.AdminDashboardService;
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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminDashboardController.class)
class AdminDashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AdminDashboardService adminDashboardService;

    private UUID doctorId;
    private DoctorStatsDto mockStats;
    private AdminDoctorDto mockDoctor;

    @BeforeEach
    void setUp() {
        doctorId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        // Create mock stats
        mockStats = new DoctorStatsDto();
        mockStats.setTotalDoctors(150L);
        mockStats.setPendingDoctors(25L);
        mockStats.setApprovedDoctors(120L);
        mockStats.setRejectedDoctors(5L);
        mockStats.setTotalServices(450L);
        mockStats.setDoctorsWithAvailability(110L);
        mockStats.setLastUpdated(LocalDateTime.of(2024, 1, 15, 10, 30));

        // Create mock services
        ServiceDto service1 = new ServiceDto();
        service1.setId(1L);
        service1.setName("Cardiology Consultation");
        service1.setDescription("Heart health consultation");
        service1.setCategory("Cardiology");
        service1.setPrice(150.0);
        service1.setDurationMinutes(30);

        ServiceDto service2 = new ServiceDto();
        service2.setId(2L);
        service2.setName("General Checkup");
        service2.setDescription("Comprehensive health check");
        service2.setCategory("General Medicine");
        service2.setPrice(100.0);
        service2.setDurationMinutes(45);

        // Create mock availability
        AvailabilityDto availability1 = new AvailabilityDto();
        availability1.setId(1L);
        availability1.setDayOfWeek("Monday");
        availability1.setStartTime("09:00");
        availability1.setEndTime("17:00");

        AvailabilityDto availability2 = new AvailabilityDto();
        availability2.setId(2L);
        availability2.setDayOfWeek("Wednesday");
        availability2.setStartTime("09:00");
        availability2.setEndTime("17:00");

        // Create mock doctor
        mockDoctor = new AdminDoctorDto();
        mockDoctor.setDoctorId(doctorId);
        mockDoctor.setSpeciality("Cardiology");
        mockDoctor.setDescription("Senior cardiologist with 15 years experience");
        mockDoctor.setStatus("APPROVED");
        mockDoctor.setRating(4.8);
        mockDoctor.setTotalReviews(125);
        mockDoctor.setServices(Arrays.asList(service1, service2));
        mockDoctor.setAvailabilities(Arrays.asList(availability1, availability2));
    }

    @Nested
    class GetDashboardStatsTests {

        @Test
        @WithMockUser(roles = "ADMIN")
        void shouldReturnDashboardStatsSuccessfully() throws Exception {
            // Given
            when(adminDashboardService.getDoctorStatistics()).thenReturn(mockStats);

            // When
            ResultActions result = mockMvc.perform(get("/api/admin/dashboard/stats"));

            // Then
            result.andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.totalDoctors").value(150))
                    .andExpect(jsonPath("$.pendingDoctors").value(25))
                    .andExpect(jsonPath("$.approvedDoctors").value(120))
                    .andExpect(jsonPath("$.rejectedDoctors").value(5))
                    .andExpect(jsonPath("$.totalServices").value(450))
                    .andExpect(jsonPath("$.doctorsWithAvailability").value(110))
                    .andExpect(jsonPath("$.lastUpdated").exists());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void shouldReturnEmptyStatsWhenNoData() throws Exception {
            // Given
            DoctorStatsDto emptyStats = new DoctorStatsDto();
            emptyStats.setTotalDoctors(0L);
            emptyStats.setPendingDoctors(0L);
            emptyStats.setApprovedDoctors(0L);
            emptyStats.setRejectedDoctors(0L);
            emptyStats.setTotalServices(0L);
            emptyStats.setDoctorsWithAvailability(0L);
            emptyStats.setLastUpdated(LocalDateTime.now());

            when(adminDashboardService.getDoctorStatistics()).thenReturn(emptyStats);

            // When
            ResultActions result = mockMvc.perform(get("/api/admin/dashboard/stats"));

            // Then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalDoctors").value(0))
                    .andExpect(jsonPath("$.approvedDoctors").value(0))
                    .andExpect(jsonPath("$.totalServices").value(0));
        }


        @Test
        void shouldReturnUnauthorizedWhenNoAuthentication() throws Exception {
            // When
            ResultActions result = mockMvc.perform(get("/api/admin/dashboard/stats"));

            // Then
            result.andExpect(status().isUnauthorized());
        }

    }

    @Nested
    class GetAllDoctorsTests {

        @Test
        @WithMockUser(roles = "ADMIN")
        void shouldReturnAllDoctorsSuccessfully() throws Exception {
            // Given
            List<AdminDoctorDto> doctors = Arrays.asList(mockDoctor);
            when(adminDashboardService.getAllDoctors()).thenReturn(doctors);

            // When
            ResultActions result = mockMvc.perform(get("/api/admin/doctors"));

            // Then
            result.andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$[0].doctorId").value(doctorId.toString()))
                    .andExpect(jsonPath("$[0].speciality").value("Cardiology"))
                    .andExpect(jsonPath("$[0].status").value("APPROVED"))
                    .andExpect(jsonPath("$[0].rating").value(4.8))
                    .andExpect(jsonPath("$[0].totalReviews").value(125))
                    .andExpect(jsonPath("$[0].services[0].name").value("Cardiology Consultation"))
                    .andExpect(jsonPath("$[0].services[0].price").value(150.0))
                    .andExpect(jsonPath("$[0].availabilities[0].dayOfWeek").value("Monday"))
                    .andExpect(jsonPath("$[0].availabilities[0].startTime").value("09:00"));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void shouldReturnEmptyDoctorsList() throws Exception {
            // Given
            when(adminDashboardService.getAllDoctors()).thenReturn(Arrays.asList());

            // When
            ResultActions result = mockMvc.perform(get("/api/admin/doctors"));

            // Then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$").isEmpty());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void shouldReturnMultipleDoctors() throws Exception {
            // Given
            AdminDoctorDto doctor2 = new AdminDoctorDto();
            doctor2.setDoctorId(UUID.fromString("223e4567-e89b-12d3-a456-426614174000"));
            doctor2.setSpeciality("Pediatrics");
            doctor2.setStatus("PENDING");
            doctor2.setRating(4.5);
            doctor2.setTotalReviews(80);

            List<AdminDoctorDto> doctors = Arrays.asList(mockDoctor, doctor2);
            when(adminDashboardService.getAllDoctors()).thenReturn(doctors);

            // When
            ResultActions result = mockMvc.perform(get("/api/admin/doctors"));

            // Then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].speciality").value("Cardiology"))
                    .andExpect(jsonPath("$[1].speciality").value("Pediatrics"))
                    .andExpect(jsonPath("$[1].status").value("PENDING"));
        }

    }

    @Nested
    class GetDoctorsBySpecialityTests {

        @Test
        @WithMockUser(roles = "ADMIN")
        void shouldReturnDoctorsBySpecialitySuccessfully() throws Exception {
            // Given
            List<AdminDoctorDto> doctors = Arrays.asList(mockDoctor);
            when(adminDashboardService.getDoctorsBySpeciality("Cardiology")).thenReturn(doctors);

            // When
            ResultActions result = mockMvc.perform(get("/api/admin/doctors/speciality/{speciality}", "Cardiology"));

            // Then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].speciality").value("Cardiology"))
                    .andExpect(jsonPath("$[0].doctorId").value(doctorId.toString()));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void shouldReturnEmptyListForNonExistentSpeciality() throws Exception {
            // Given
            when(adminDashboardService.getDoctorsBySpeciality("Dermatology")).thenReturn(Arrays.asList());

            // When
            ResultActions result = mockMvc.perform(get("/api/admin/doctors/speciality/{speciality}", "Dermatology"));

            // Then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$").isEmpty());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void shouldHandleSpecialityWithSpaces() throws Exception {
            // Given
            AdminDoctorDto doctor = new AdminDoctorDto();
            doctor.setDoctorId(doctorId);
            doctor.setSpeciality("General Medicine");
            doctor.setStatus("APPROVED");

            when(adminDashboardService.getDoctorsBySpeciality("General Medicine")).thenReturn(Arrays.asList(doctor));

            // When
            ResultActions result = mockMvc.perform(get("/api/admin/doctors/speciality/{speciality}", "General Medicine"));

            // Then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].speciality").value("General Medicine"));
        }



    @Nested
    class EdgeCaseTests {

        @Test
        @WithMockUser(roles = "ADMIN")
        void shouldHandleDoctorWithNoServices() throws Exception {
            // Given
            AdminDoctorDto doctorNoServices = new AdminDoctorDto();
            doctorNoServices.setDoctorId(doctorId);
            doctorNoServices.setSpeciality("Cardiology");
            doctorNoServices.setStatus("APPROVED");
            doctorNoServices.setServices(Arrays.asList());
            doctorNoServices.setAvailabilities(Arrays.asList());

            when(adminDashboardService.getAllDoctors()).thenReturn(Arrays.asList(doctorNoServices));

            // When
            ResultActions result = mockMvc.perform(get("/api/admin/doctors"));

            // Then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].services").isEmpty())
                    .andExpect(jsonPath("$[0].availabilities").isEmpty());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void shouldHandleDoctorWithNoRating() throws Exception {
            // Given
            AdminDoctorDto doctorNoRating = new AdminDoctorDto();
            doctorNoRating.setDoctorId(doctorId);
            doctorNoRating.setSpeciality("Cardiology");
            doctorNoRating.setStatus("PENDING");
            doctorNoRating.setRating(null);
            doctorNoRating.setTotalReviews(0);

            when(adminDashboardService.getAllDoctors()).thenReturn(Arrays.asList(doctorNoRating));

            // When
            ResultActions result = mockMvc.perform(get("/api/admin/doctors"));

            // Then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].rating").isEmpty())
                    .andExpect(jsonPath("$[0].totalReviews").value(0));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void shouldHandleCaseInsensitiveSpeciality() throws Exception {
            // Given
            when(adminDashboardService.getDoctorsBySpeciality("cardiology")).thenReturn(Arrays.asList(mockDoctor));

            // When
            ResultActions result = mockMvc.perform(get("/api/admin/doctors/speciality/{speciality}", "cardiology"));

            // Then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].speciality").value("Cardiology"));
        }
    }
}
}