package com.symptomcheck.doctorservice.integration;

import com.symptomcheck.doctorservice.dtos.admindashboarddto.AdminDoctorDto;
import com.symptomcheck.doctorservice.dtos.admindashboarddto.DoctorStatsDto;
import com.symptomcheck.doctorservice.dtos.admindashboarddto.ServiceDto;
import com.symptomcheck.doctorservice.services.AdminDashboardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AdminDashboardControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminDashboardService adminDashboardService;

    private UUID doctorId;

    @BeforeEach
    void setUp() {
        doctorId = UUID.randomUUID();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getDashboardStats_ShouldReturnStats() throws Exception {
        DoctorStatsDto stats = new DoctorStatsDto();
        stats.setTotalDoctors(5L);
        stats.setApprovedDoctors(4L);
        stats.setPendingDoctors(1L);

        when(adminDashboardService.getDoctorStatistics()).thenReturn(stats);

        mockMvc.perform(get("/api/admin/dashboard/stats"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.totalDoctors").value(5))
                .andExpect(jsonPath("$.approvedDoctors").value(4))
                .andExpect(jsonPath("$.pendingDoctors").value(1));

        verify(adminDashboardService, times(1)).getDoctorStatistics();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllDoctors_ShouldReturnList() throws Exception {
        AdminDoctorDto doctor = new AdminDoctorDto();
        doctor.setDoctorId(doctorId);
        doctor.setStatus("APPROVED");
        doctor.setServices(List.of(new ServiceDto()));

        when(adminDashboardService.getAllDoctors()).thenReturn(List.of(doctor));

        mockMvc.perform(get("/api/admin/doctors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].doctorId").value(doctorId.toString()))
                .andExpect(jsonPath("$[0].status").value("APPROVED"));

        verify(adminDashboardService, times(1)).getAllDoctors();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getDoctorsBySpeciality_ShouldReturnFilteredList() throws Exception {
        AdminDoctorDto doctor = new AdminDoctorDto();
        doctor.setDoctorId(doctorId);
        doctor.setSpeciality("Cardiology");
        doctor.setStatus("APPROVED");

        when(adminDashboardService.getDoctorsBySpeciality("Cardiology")).thenReturn(List.of(doctor));

        mockMvc.perform(get("/api/admin/doctors/speciality/{speciality}", "Cardiology"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].doctorId").value(doctorId.toString()))
                .andExpect(jsonPath("$[0].speciality").value("Cardiology"));

        verify(adminDashboardService, times(1)).getDoctorsBySpeciality("Cardiology");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateDoctorStatus_ShouldReturnUpdatedDoctor() throws Exception {
        AdminDoctorDto doctor = new AdminDoctorDto();
        doctor.setDoctorId(doctorId);
        doctor.setStatus("APPROVED");

        when(adminDashboardService.updateDoctorStatus(doctorId, "APPROVED")).thenReturn(doctor);

        mockMvc.perform(put("/api/admin/doctors/{doctorId}/status", doctorId)
                        .param("status", "APPROVED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.doctorId").value(doctorId.toString()))
                .andExpect(jsonPath("$.status").value("APPROVED"));

        verify(adminDashboardService, times(1)).updateDoctorStatus(doctorId, "APPROVED");
    }
}
