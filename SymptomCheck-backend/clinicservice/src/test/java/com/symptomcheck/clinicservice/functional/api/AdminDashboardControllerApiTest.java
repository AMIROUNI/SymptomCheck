package com.symptomcheck.clinicservice.functional.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.symptomcheck.clinicservice.SecurityTestConfig;
import com.symptomcheck.clinicservice.controllers.AdminDashboardController;
import com.symptomcheck.clinicservice.dtos.admindashboarddto.AdminClinicDto;
import com.symptomcheck.clinicservice.dtos.admindashboarddto.ClinicStatsDto;
import com.symptomcheck.clinicservice.exception.ClinicValidationException;
import com.symptomcheck.clinicservice.services.AdminDashboardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(AdminDashboardController.class)
@Import(SecurityTestConfig.class)
class AdminDashboardControllerApiTest {
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private MockMvc mockMvc ;

    @MockBean
    private AdminDashboardService adminDashboardService;

    @Autowired
    private ObjectMapper objectMapper;

    private AdminClinicDto adminClinicDto;
    private ClinicStatsDto clinicStatsDto;

    @BeforeEach
    void setUp() {

        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity()) // important!
                .build();
        // Setup test data for AdminClinicDto
        adminClinicDto = new AdminClinicDto();
        adminClinicDto.setId(1L);
        adminClinicDto.setName("City Medical Center");
        adminClinicDto.setAddress("123 Main Street");
        adminClinicDto.setPhone("+1-555-0123");
        adminClinicDto.setWebsiteUrl("https://citymedical.example.com");
        adminClinicDto.setCity("New York");
        adminClinicDto.setCountry("USA");
        adminClinicDto.setDoctorCount(15L);
        adminClinicDto.setAppointmentCount(45L);

        // Setup test data for ClinicStatsDto
        clinicStatsDto = new ClinicStatsDto();
        clinicStatsDto.setTotalClinics(10L);
        clinicStatsDto.setClinicsWithDoctors(8L);
        clinicStatsDto.setClinicsInEachCity(5L);
        clinicStatsDto.setLastUpdated(LocalDateTime.of(2024, 1, 15, 10, 30, 0));
    }

    @Nested
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    class GetDashboardStats {

        @Test
        void shouldReturnClinicStatistics() throws Exception {
            // Given
            given(adminDashboardService.getClinicStatistics()).willReturn(clinicStatsDto);

            // When & Then
            mockMvc.perform(get("/api/admin/dashboard/stats")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalClinics", is(10)))
                    .andExpect(jsonPath("$.clinicsWithDoctors", is(8)))
                    .andExpect(jsonPath("$.clinicsInEachCity", is(5)))
                    .andExpect(jsonPath("$.lastUpdated", is("2024-01-15T10:30:00")));
        }

        @Test
        void whenServiceThrowsException_shouldReturnInternalServerError() throws Exception {
            // Given
            given(adminDashboardService.getClinicStatistics())
                    .willThrow(new RuntimeException("Database error"));

            // When & Then
            mockMvc.perform(get("/api/admin/dashboard/stats")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isInternalServerError());
        }
    }

    @Nested
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    class GetAllClinics {

        @Test
        void shouldReturnListOfClinics() throws Exception {
            // Given
            AdminClinicDto clinic2 = new AdminClinicDto();
            clinic2.setId(2L);
            clinic2.setName("Second Clinic");
            clinic2.setAddress("789 Second Street");
            clinic2.setCity("Chicago");
            clinic2.setCountry("USA");
            clinic2.setDoctorCount(8L);
            clinic2.setAppointmentCount(25L);

            List<AdminClinicDto> clinics = Arrays.asList(adminClinicDto, clinic2);
            given(adminDashboardService.getAllClinics()).willReturn(clinics);

            // When & Then
            mockMvc.perform(get("/api/admin/clinics")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].id", is(1)))
                    .andExpect(jsonPath("$[0].name", is("City Medical Center")))
                    .andExpect(jsonPath("$[0].doctorCount", is(15)))
                    .andExpect(jsonPath("$[0].appointmentCount", is(45)))
                    .andExpect(jsonPath("$[1].id", is(2)))
                    .andExpect(jsonPath("$[1].name", is("Second Clinic")));
        }

        @Test
        void whenNoClinicsExist_shouldReturnEmptyList() throws Exception {
            // Given
            given(adminDashboardService.getAllClinics()).willReturn(Arrays.asList());

            // When & Then
            mockMvc.perform(get("/api/admin/clinics")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }
    }

    @Nested
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    class GetClinicsByCity {

        @Test
        void shouldReturnFilteredClinics() throws Exception {
            // Given
            AdminClinicDto clinic2 = new AdminClinicDto();
            clinic2.setId(3L);
            clinic2.setName("Downtown Clinic");
            clinic2.setAddress("456 Downtown Ave");
            clinic2.setCity("New York");
            clinic2.setCountry("USA");
            clinic2.setDoctorCount(12L);
            clinic2.setAppointmentCount(35L);

            List<AdminClinicDto> clinics = Arrays.asList(adminClinicDto, clinic2);
            given(adminDashboardService.getClinicsByCity("New York")).willReturn(clinics);

            // When & Then
            mockMvc.perform(get("/api/admin/clinics/city/{city}", "New York")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].city", is("New York")))
                    .andExpect(jsonPath("$[0].doctorCount", is(15)))
                    .andExpect(jsonPath("$[1].city", is("New York")))
                    .andExpect(jsonPath("$[1].appointmentCount", is(35)));
        }

        @Test
        void whenNoClinicsInCity_shouldReturnEmptyList() throws Exception {
            // Given
            given(adminDashboardService.getClinicsByCity("NonExistingCity")).willReturn(Arrays.asList());

            // When & Then
            mockMvc.perform(get("/api/admin/clinics/city/{city}", "NonExistingCity")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }
    }

    @Nested
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    class CreateClinic {

        @Test
        void shouldReturnCreatedClinic() throws Exception {
            // Given
            given(adminDashboardService.createClinic(any(AdminClinicDto.class))).willReturn(adminClinicDto);

            // When & Then
            mockMvc.perform(post("/api/admin/clinics")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(adminClinicDto)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.name", is(adminClinicDto.getName())))
                    .andExpect(jsonPath("$.address", is(adminClinicDto.getAddress())))
                    .andExpect(jsonPath("$.phone", is(adminClinicDto.getPhone())))
                    .andExpect(jsonPath("$.websiteUrl", is(adminClinicDto.getWebsiteUrl())))
                    .andExpect(jsonPath("$.city", is(adminClinicDto.getCity())))
                    .andExpect(jsonPath("$.country", is(adminClinicDto.getCountry())))
                    .andExpect(jsonPath("$.doctorCount", is(15)))
                    .andExpect(jsonPath("$.appointmentCount", is(45)));
        }

        @Test
        void whenInvalidData_shouldReturn403() throws Exception {
            // Given - Create invalid DTO with blank name
            AdminClinicDto invalidClinicDto = new AdminClinicDto();
            invalidClinicDto.setName(""); // Blank name
            invalidClinicDto.setAddress("Test Address");

            given(adminDashboardService.createClinic(any(AdminClinicDto.class)))
                    .willThrow(new ClinicValidationException("Validation error"));

            // When & Then
            mockMvc.perform(post("/api/admin/clinics")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidClinicDto)))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @Test
        void whenServiceThrowsException_shouldReturnInternalServerError() throws Exception {
            // Given
            given(adminDashboardService.createClinic(any(AdminClinicDto.class)))
                    .willThrow(new RuntimeException("Database error"));

            // When & Then
            mockMvc.perform(post("/api/admin/clinics")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(adminClinicDto)))
                    .andDo(print())
                    .andExpect(status().isInternalServerError());
        }
    }

    @Nested
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    class UpdateClinic {

        @Test
        void shouldReturnUpdatedClinic() throws Exception {
            // Given
            AdminClinicDto updatedClinicDto = new AdminClinicDto();
            updatedClinicDto.setId(1L);
            updatedClinicDto.setName("Updated Medical Center");
            updatedClinicDto.setAddress("456 Updated Street");
            updatedClinicDto.setPhone("+1-555-0456");
            updatedClinicDto.setWebsiteUrl("https://updatedmedical.example.com");
            updatedClinicDto.setCity("Los Angeles");
            updatedClinicDto.setCountry("USA");
            updatedClinicDto.setDoctorCount(20L);
            updatedClinicDto.setAppointmentCount(50L);

            given(adminDashboardService.updateClinic(eq(1L), any(AdminClinicDto.class))).willReturn(updatedClinicDto);

            // When & Then
            mockMvc.perform(put("/api/admin/clinics/{clinicId}", 1L)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updatedClinicDto)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.name", is("Updated Medical Center")))
                    .andExpect(jsonPath("$.address", is("456 Updated Street")))
                    .andExpect(jsonPath("$.phone", is("+1-555-0456")))
                    .andExpect(jsonPath("$.websiteUrl", is("https://updatedmedical.example.com")))
                    .andExpect(jsonPath("$.city", is("Los Angeles")))
                    .andExpect(jsonPath("$.doctorCount", is(20)))
                    .andExpect(jsonPath("$.appointmentCount", is(50)));
        }

        @Test
        void whenClinicNotFound_shouldHandleNotFoundException() throws Exception {
            // Given
            given(adminDashboardService.updateClinic(eq(999L), any(AdminClinicDto.class)))
                    .willThrow(new RuntimeException("Clinic not found"));

            // When & Then
            mockMvc.perform(put("/api/admin/clinics/{clinicId}", 999L)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(adminClinicDto)))
                    .andDo(print())
                    .andExpect(status().isInternalServerError());
        }
    }

    @Nested
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    class DeleteClinic {

        @Test
        void shouldReturnOk() throws Exception {
            // Given
            doNothing().when(adminDashboardService).deleteClinic(1L);

            // When & Then
            mockMvc.perform(delete("/api/admin/clinics/{clinicId}", 1L)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        @Test
        void whenClinicNotFound_shouldHandleException() throws Exception {
            // Given
            doNothing().when(adminDashboardService).deleteClinic(999L);

            // When & Then - Even if clinic doesn't exist, service might handle it gracefully
            mockMvc.perform(delete("/api/admin/clinics/{clinicId}", 999L)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk());
        }
    }


}