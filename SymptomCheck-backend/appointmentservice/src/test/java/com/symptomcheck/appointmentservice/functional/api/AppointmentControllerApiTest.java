package com.symptomcheck.appointmentservice.functional.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.symptomcheck.appointmentservice.SecurityTestConfig;
import com.symptomcheck.appointmentservice.controllers.AppointmentController;
import com.symptomcheck.appointmentservice.dtos.AppointmentDto;
import com.symptomcheck.appointmentservice.enums.AppointmentStatus;
import com.symptomcheck.appointmentservice.models.Appointment;
import com.symptomcheck.appointmentservice.services.AppointmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@WebMvcTest(AppointmentController.class)
@DisplayName("Appointment Controller Tests")
@Import(SecurityTestConfig.class)
class AppointmentControllerApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AppointmentService appointmentService;

    private UUID testPatientId;
    private UUID testDoctorId;
    private Appointment testAppointment;
    private AppointmentDto testAppointmentDto;

    @BeforeEach
    void setUp() {
        testPatientId = UUID.randomUUID();
        testDoctorId = UUID.randomUUID();

        testAppointment = new Appointment();
        testAppointment.setId(1L);
        testAppointment.setPatientId(testPatientId);
        testAppointment.setDoctorId(testDoctorId);
        testAppointment.setDateTime(LocalDateTime.now().plusDays(1));
        testAppointment.setStatus(AppointmentStatus.PENDING);
        testAppointment.setDescription("Regular checkup");
        testAppointment.setCreatedAt(Instant.now());

        testAppointmentDto = new AppointmentDto();
        testAppointmentDto.setPatientId(testPatientId);
        testAppointmentDto.setDoctorId(testDoctorId);
        testAppointmentDto.setDateTime(LocalDateTime.now().plusDays(1));
        testAppointmentDto.setDescription("Regular checkup");
    }

    @Nested
    @DisplayName("POST /api/v1/appointments/create - Create Appointment Tests")
    class CreateAppointmentTests {

        @Test
        @DisplayName("Should create appointment successfully with valid data")
        void shouldCreateAppointmentSuccessfully() throws Exception {
            // Given
            when(appointmentService.makeAppointment(any(AppointmentDto.class), anyString()))
                    .thenReturn(testAppointment);

            // When & Then
            mockMvc.perform(post("/api/v1/appointments/create")
                            .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_PATIENT")))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testAppointmentDto)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.patientId").value(testPatientId.toString()))
                    .andExpect(jsonPath("$.doctorId").value(testDoctorId.toString()))
                    .andExpect(jsonPath("$.status").value("PENDING"))
                    .andExpect(jsonPath("$.description").value("Regular checkup"));

            verify(appointmentService, times(1))
                    .makeAppointment(any(AppointmentDto.class), anyString());
        }

        @Test
        @DisplayName("Should return 400 when appointment data is invalid")
        void shouldReturnBadRequestWhenDataInvalid() throws Exception {
            // Given
            when(appointmentService.makeAppointment(any(AppointmentDto.class), anyString()))
                    .thenThrow(new IllegalArgumentException("Doctor is not available at this time"));

            // When & Then
            mockMvc.perform(post("/api/v1/appointments/create")
                            .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_PATIENT")))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testAppointmentDto)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("Doctor is not available at this time"));

            verify(appointmentService, times(1))
                    .makeAppointment(any(AppointmentDto.class), anyString());
        }

        @Test
        @DisplayName("Should return 500 when internal server error occurs")
        void shouldReturnInternalServerErrorWhenExceptionOccurs() throws Exception {
            // Given
            when(appointmentService.makeAppointment(any(AppointmentDto.class), anyString()))
                    .thenThrow(new RuntimeException("Database connection failed"));

            // When & Then
            mockMvc.perform(post("/api/v1/appointments/create")
                            .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_PATIENT")))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testAppointmentDto)))
                    .andDo(print())
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().string(containsString("Internal server error")));
        }


    @Nested
    @DisplayName("GET /api/v1/appointments/doctor/{doctorId} - Get Appointments by Doctor Tests")
    class GetByDoctorTests {

        @Test
        @DisplayName("Should return list of appointments for valid doctor ID")
        void shouldReturnAppointmentsForDoctor() throws Exception {
            // Given
            Appointment appointment2 = new Appointment();
            appointment2.setId(2L);
            appointment2.setPatientId(UUID.randomUUID());
            appointment2.setDoctorId(testDoctorId);
            appointment2.setDateTime(LocalDateTime.now().plusDays(2));
            appointment2.setStatus(AppointmentStatus.CONFIRMED);
            appointment2.setDescription("Follow-up");

            List<Appointment> appointments = Arrays.asList(testAppointment, appointment2);
            when(appointmentService.getByDoctor(testDoctorId)).thenReturn(appointments);

            // When & Then
            mockMvc.perform(get("/api/v1/appointments/doctor/{doctorId}", testDoctorId)
                            .with(jwt()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].id").value(1))
                    .andExpect(jsonPath("$[0].status").value("PENDING"))
                    .andExpect(jsonPath("$[1].id").value(2))
                    .andExpect(jsonPath("$[1].status").value("CONFIRMED"));

            verify(appointmentService, times(1)).getByDoctor(testDoctorId);
        }

        @Test
        @DisplayName("Should return empty list when doctor has no appointments")
        void shouldReturnEmptyListWhenNoAppointments() throws Exception {
            // Given
            when(appointmentService.getByDoctor(testDoctorId)).thenReturn(Collections.emptyList());

            // When & Then
            mockMvc.perform(get("/api/v1/appointments/doctor/{doctorId}", testDoctorId)
                            .with(jwt()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));

            verify(appointmentService, times(1)).getByDoctor(testDoctorId);
        }

        @Test
        @DisplayName("Should return empty list when exception occurs")
        void shouldReturnEmptyListOnException() throws Exception {
            // Given
            when(appointmentService.getByDoctor(testDoctorId))
                    .thenThrow(new RuntimeException("Database error"));

            // When & Then
            mockMvc.perform(get("/api/v1/appointments/doctor/{doctorId}", testDoctorId)
                            .with(jwt()))
                    .andDo(print())
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$", hasSize(0)));

            verify(appointmentService, times(1)).getByDoctor(testDoctorId);
        }
    }

    @Nested
    @DisplayName("GET /api/v1/appointments/taken-appointments/{doctorId} - Get Taken Appointments Tests")
    class GetTakenAppointmentsTests {

        @Test
        @DisplayName("Should return list of taken appointment times for a specific date")
        void shouldReturnTakenAppointmentTimes() throws Exception {
            // Given
            LocalDate testDate = LocalDate.now().plusDays(1);
            List<String> takenTimes = Arrays.asList("09:00:00", "10:30:00", "14:00:00");
            when(appointmentService.getTakenAppointments(testDoctorId, testDate))
                    .thenReturn(takenTimes);

            // When & Then
            mockMvc.perform(get("/api/v1/appointments/taken-appointments/{doctorId}", testDoctorId)
                            .with(jwt())
                            .param("date", testDate.toString()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(3)))
                    .andExpect(jsonPath("$[0]").value("09:00:00"))
                    .andExpect(jsonPath("$[1]").value("10:30:00"))
                    .andExpect(jsonPath("$[2]").value("14:00:00"));

            verify(appointmentService, times(1))
                    .getTakenAppointments(testDoctorId, testDate);
        }

        @Test
        @DisplayName("Should return empty list when no appointments on given date")
        void shouldReturnEmptyListWhenNoAppointmentsOnDate() throws Exception {
            // Given
            LocalDate testDate = LocalDate.now().plusDays(7);
            when(appointmentService.getTakenAppointments(testDoctorId, testDate))
                    .thenReturn(Collections.emptyList());

            // When & Then
            mockMvc.perform(get("/api/v1/appointments/taken-appointments/{doctorId}", testDoctorId)
                            .with(jwt())
                            .param("date", testDate.toString()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));

            verify(appointmentService, times(1))
                    .getTakenAppointments(testDoctorId, testDate);
        }

        @Test
        @DisplayName("Should return 400 when date parameter is missing")
        void shouldReturnBadRequestWhenDateMissing() throws Exception {
            // When & Then
            mockMvc.perform(get("/api/v1/appointments/taken-appointments/{doctorId}", testDoctorId)
                            .with(jwt()))
                    .andDo(print())
                    .andExpect(status().isBadRequest());

            verify(appointmentService, never())
                    .getTakenAppointments(any(UUID.class), any(LocalDate.class));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/appointments/{userId} - Get Appointments by Patient Tests")
    class GetByPatientTests {

        @Test
        @DisplayName("Should return list of appointments for patient")
        void shouldReturnAppointmentsForPatient() throws Exception {
            // Given
            List<Appointment> appointments = Arrays.asList(testAppointment);
            when(appointmentService.getByPatientId(testPatientId)).thenReturn(appointments);

            // When & Then
            mockMvc.perform(get("/api/v1/appointments/{userId}", testPatientId)
                            .with(jwt()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].id").value(1))
                    .andExpect(jsonPath("$[0].patientId").value(testPatientId.toString()));

            verify(appointmentService, times(1)).getByPatientId(testPatientId);
        }

        @Test
        @DisplayName("Should return empty list when patient has no appointments")
        void shouldReturnEmptyListWhenNoAppointmentsForPatient() throws Exception {
            // Given
            when(appointmentService.getByPatientId(testPatientId))
                    .thenReturn(Collections.emptyList());

            // When & Then
            mockMvc.perform(get("/api/v1/appointments/{userId}", testPatientId)
                            .with(jwt()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));

            verify(appointmentService, times(1)).getByPatientId(testPatientId);
        }

        @Test
        @DisplayName("Should return empty list when exception occurs")
        void shouldReturnEmptyListOnException() throws Exception {
            // Given
            when(appointmentService.getByPatientId(testPatientId))
                    .thenThrow(new RuntimeException("Service unavailable"));

            // When & Then
            mockMvc.perform(get("/api/v1/appointments/{userId}", testPatientId)
                            .with(jwt()))
                    .andDo(print())
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$", hasSize(0)));

            verify(appointmentService, times(1)).getByPatientId(testPatientId);
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/appointments/{id}/status/{statusNumber} - Update Status Tests")
    class UpdateStatusTests {

        @Test
        @DisplayName("Should update appointment status successfully")
        void shouldUpdateStatusSuccessfully() throws Exception {
            // Given
            Long appointmentId = 1L;
            int statusNumber = 1; // CONFIRMED
            when(appointmentService.updateAppointmentStatus(appointmentId, statusNumber))
                    .thenReturn(true);

            // When & Then
            mockMvc.perform(put("/api/v1/appointments/{id}/status/{statusNumber}",
                            appointmentId, statusNumber)
                            .with(jwt()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().string("true"));

            verify(appointmentService, times(1))
                    .updateAppointmentStatus(appointmentId, statusNumber);
        }

        @Test
        @DisplayName("Should return false when status number is invalid")
        void shouldReturnFalseWhenStatusNumberInvalid() throws Exception {
            // Given
            Long appointmentId = 1L;
            int invalidStatusNumber = 99;
            when(appointmentService.updateAppointmentStatus(appointmentId, invalidStatusNumber))
                    .thenThrow(new IllegalArgumentException("Invalid status number: 99"));

            // When & Then
            mockMvc.perform(put("/api/v1/appointments/{id}/status/{statusNumber}",
                            appointmentId, invalidStatusNumber)
                            .with(jwt()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().string("false"));

            verify(appointmentService, times(1))
                    .updateAppointmentStatus(appointmentId, invalidStatusNumber);
        }

        @Test
        @DisplayName("Should return false when appointment not found")
        void shouldReturnFalseWhenAppointmentNotFound() throws Exception {
            // Given
            Long appointmentId = 999L;
            int statusNumber = 1;
            when(appointmentService.updateAppointmentStatus(appointmentId, statusNumber))
                    .thenThrow(new IllegalArgumentException("Appointment with ID 999 not found"));

            // When & Then
            mockMvc.perform(put("/api/v1/appointments/{id}/status/{statusNumber}",
                            appointmentId, statusNumber)
                            .with(jwt()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().string("false"));

            verify(appointmentService, times(1))
                    .updateAppointmentStatus(appointmentId, statusNumber);
        }

        @Test
        @DisplayName("Should return 500 when internal server error occurs")
        void shouldReturnInternalServerErrorOnException() throws Exception {
            // Given
            Long appointmentId = 1L;
            int statusNumber = 1;
            when(appointmentService.updateAppointmentStatus(appointmentId, statusNumber))
                    .thenThrow(new RuntimeException("Database connection failed"));

            // When & Then
            mockMvc.perform(put("/api/v1/appointments/{id}/status/{statusNumber}",
                            appointmentId, statusNumber)
                            .with(jwt()))
                    .andDo(print())
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().string("false"));

            verify(appointmentService, times(1))
                    .updateAppointmentStatus(appointmentId, statusNumber);
        }

        @Test
        @DisplayName("Should update to each valid status")
        void shouldUpdateToEachValidStatus() throws Exception {
            // Test all valid status numbers (0-3 for PENDING, CONFIRMED, CANCELLED, COMPLETED)
            Long appointmentId = 1L;

            for (int statusNumber = 0; statusNumber < 4; statusNumber++) {
                when(appointmentService.updateAppointmentStatus(appointmentId, statusNumber))
                        .thenReturn(true);

                mockMvc.perform(put("/api/v1/appointments/{id}/status/{statusNumber}",
                                appointmentId, statusNumber)
                                .with(jwt()))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(content().string("true"));
            }

            verify(appointmentService, times(4))
                    .updateAppointmentStatus(eq(appointmentId), anyInt());
        }
    }

    @Nested
    @DisplayName("Integration Scenarios")
    class IntegrationScenarios {

        @Test
        @DisplayName("Should handle full appointment lifecycle")
        void shouldHandleFullAppointmentLifecycle() throws Exception {
            // 1. Create appointment
            when(appointmentService.makeAppointment(any(AppointmentDto.class), anyString()))
                    .thenReturn(testAppointment);

            mockMvc.perform(post("/api/v1/appointments/create")
                            .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_PATIENT")))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testAppointmentDto)))
                    .andExpect(status().isOk());

            // 2. Get appointments by doctor
            when(appointmentService.getByDoctor(testDoctorId))
                    .thenReturn(Arrays.asList(testAppointment));

            mockMvc.perform(get("/api/v1/appointments/doctor/{doctorId}", testDoctorId)
                            .with(jwt()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)));

            // 3. Update status
            when(appointmentService.updateAppointmentStatus(1L, 1))
                    .thenReturn(true);

            mockMvc.perform(put("/api/v1/appointments/{id}/status/{statusNumber}", 1L, 1)
                            .with(jwt()))
                    .andExpect(status().isOk())
                    .andExpect(content().string("true"));
        }
    }
}
}