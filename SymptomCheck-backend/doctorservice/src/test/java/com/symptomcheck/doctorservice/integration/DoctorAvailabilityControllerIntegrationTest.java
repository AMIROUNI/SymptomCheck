package com.symptomcheck.doctorservice.integration;

import com.symptomcheck.doctorservice.models.DoctorAvailability;
import com.symptomcheck.doctorservice.services.DoctorAvailabilityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class DoctorAvailabilityControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DoctorAvailabilityService availabilityService;

    private UUID doctorId;
    private LocalDate date;
    private LocalDateTime dateTime;

    // -------------------------------
    // ADDING THE POSTGRES CONTAINER
    // -------------------------------
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test")
            .withReuse(true);

    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.jpa.properties.hibernate.dialect", () -> "org.hibernate.dialect.PostgreSQLDialect");
        registry.add("spring.sql.init.mode", () -> "never");
    }
    // -------------------------------


    @BeforeEach
    void setUp() {
        doctorId = UUID.randomUUID();
        date = LocalDate.now();
        dateTime = LocalDateTime.of(date, LocalTime.of(10, 0));
    }

    @Test
    @WithMockUser(roles = "DOCTOR")
    void getDoctorAvailability_ShouldReturnList() throws Exception {
        List<DoctorAvailability> mockList = List.of(new DoctorAvailability());
        when(availabilityService.getAvailabilityByDoctorId(doctorId)).thenReturn(mockList);

        mockMvc.perform(get("/api/v1/doctor/availability/{doctorId}", doctorId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(availabilityService, times(1)).getAvailabilityByDoctorId(doctorId);
    }

    @Test
    @WithMockUser(roles = "DOCTOR")
    void getDoctorAvailability_InvalidUUID_ShouldReturn400() throws Exception {
        mockMvc.perform(get("/api/v1/doctor/availability/{doctorId}", "invalid-uuid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "DOCTOR")
    void isAvailable_ShouldReturnTrue() throws Exception {
        when(availabilityService.isDoctorAvailable(doctorId, dateTime)).thenReturn(true);

        mockMvc.perform(get("/api/v1/doctor/availability/isAvailable/{id}/{dateTime}", doctorId, dateTime.toString()))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(availabilityService, times(1)).isDoctorAvailable(doctorId, dateTime);
    }

    @Test
    @WithMockUser(roles = "DOCTOR")
    void isAvailable_ShouldReturnFalse() throws Exception {
        when(availabilityService.isDoctorAvailable(doctorId, dateTime)).thenReturn(false);

        mockMvc.perform(get("/api/v1/doctor/availability/isAvailable/{id}/{dateTime}", doctorId, dateTime.toString()))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));

        verify(availabilityService, times(1)).isDoctorAvailable(doctorId, dateTime);
    }

    @Test
    @WithMockUser(roles = "DOCTOR")
    void getDailyAvailability_ShouldReturnListOfSlots() throws Exception {
        List<String> slots = List.of("09:00", "10:00", "11:00");
        when(availabilityService.getAvailableSlotsForDate(doctorId, date)).thenReturn(slots);

        mockMvc.perform(get("/api/v1/doctor/availability/daily")
                        .param("doctorId", doctorId.toString())
                        .param("date", date.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(slots.size()));

        verify(availabilityService, times(1)).getAvailableSlotsForDate(doctorId, date);
    }

    @Test
    @WithMockUser(roles = "DOCTOR")
    void getDailyAvailability_InvalidUUID_ShouldReturn400() throws Exception {
        mockMvc.perform(get("/api/v1/doctor/availability/daily")
                        .param("doctorId", "invalid-uuid")
                        .param("date", date.toString()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "DOCTOR")
    void getDailyAvailability_NoAvailability_ShouldReturnEmptyList() throws Exception {
        when(availabilityService.getAvailableSlotsForDate(doctorId, date)).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/doctor/availability/daily")
                        .param("doctorId", doctorId.toString())
                        .param("date", date.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(availabilityService, times(1)).getAvailableSlotsForDate(doctorId, date);
    }
}
