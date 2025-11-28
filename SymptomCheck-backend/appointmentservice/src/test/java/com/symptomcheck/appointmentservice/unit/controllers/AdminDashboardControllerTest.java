package com.symptomcheck.appointmentservice.unit.controllers;

import com.symptomcheck.appointmentservice.AppointmentserviceApplication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = AppointmentserviceApplication.class)
@AutoConfigureMockMvc
class AdminDashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private String startDate;
    private String endDate;

    @BeforeEach
    void setup() {
        startDate = LocalDateTime.now().minusDays(1).toString();
        endDate = LocalDateTime.now().toString();
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testGetAllAppointments() throws Exception {
        mockMvc.perform(get("/api/admin/appointments"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testGetAppointmentsByDateRange() throws Exception {
        mockMvc.perform(get("/api/admin/appointments/date-range")
                        .param("start", startDate)
                        .param("end", endDate))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testGetAppointmentsByDoctor() throws Exception {
        mockMvc.perform(get("/api/admin/appointments/doctor/6e12016e-e4ff-4e6f-822e-bb8416469697"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testGetAppointmentsByStatus() throws Exception {
        mockMvc.perform(get("/api/admin/appointments/status/PENDING"))
                .andExpect(status().isOk());
    }


}
