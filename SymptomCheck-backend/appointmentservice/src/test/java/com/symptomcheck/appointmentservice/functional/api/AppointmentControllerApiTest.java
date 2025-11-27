package com.symptomcheck.appointmentservice.functional.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.symptomcheck.appointmentservice.controllers.AppointmentController;
import com.symptomcheck.appointmentservice.dtos.AppointmentDto;
import com.symptomcheck.appointmentservice.enums.AppointmentStatus;
import com.symptomcheck.appointmentservice.models.Appointment;
import com.symptomcheck.appointmentservice.services.AppointmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AppointmentController.class)
class AppointmentControllerFunctionalTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AppointmentService appointmentService;

    private UUID patientId;
    private UUID doctorId;
    private Long appointmentId;
    private AppointmentDto appointmentDto;
    private Appointment appointment;
    private String validToken;
    private Jwt jwt;

    @BeforeEach
    void setUp() {
        patientId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        doctorId = UUID.fromString("123e4567-e89b-12d3-a456-426614174001");
        appointmentId = 1L;
        validToken = "mock-jwt-token";

        // Create real JSON-compatible AppointmentDto
        appointmentDto = new AppointmentDto();
        appointmentDto.setDateTime(LocalDateTime.of(2024, 1, 15, 10, 30));
        appointmentDto.setPatientId(patientId);
        appointmentDto.setDoctorId(doctorId);
        appointmentDto.setDescription("Regular checkup");

        // Create real JSON-compatible Appointment
        appointment = new Appointment();
        appointment.setId(appointmentId);
        appointment.setDateTime(LocalDateTime.of(2024, 1, 15, 10, 30));
        appointment.setPatientId(patientId);
        appointment.setDoctorId(doctorId);
        appointment.setDescription("Regular checkup");
        appointment.setStatus(AppointmentStatus.PENDING);

        // Mock JWT
        jwt = Jwt.withTokenValue(validToken)
                .header("alg", "none")
                .claim("sub", "test-user")
                .claim("scope", "openid")
                .build();
    }

    @Nested
    class CreateAppointmentTests {
        @Test
        void shouldCreateAppointmentSuccessfully() throws Exception {
            // Given
            when(appointmentService.makeAppointment(any(AppointmentDto.class), eq(validToken)))
                    .thenReturn(appointment);

            // When
            ResultActions result = mockMvc.perform(post("/api/v1/appointments/create")
                    .with(jwt().jwt(jwt))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(appointmentDto)));

            // Then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(appointmentId))
                    .andExpect(jsonPath("$.patientId").value(patientId.toString()))
                    .andExpect(jsonPath("$.doctorId").value(doctorId.toString()))
                    .andExpect(jsonPath("$.status").value("PENDING"))
                    .andExpect(jsonPath("$.description").value("Regular checkup"));
        }

        @Test
        void shouldReturnBadRequestWhenIllegalArgumentException() throws Exception {
            // Given
            when(appointmentService.makeAppointment(any(AppointmentDto.class), eq(validToken)))
                    .thenThrow(new IllegalArgumentException("Invalid appointment data"));

            // When
            ResultActions result = mockMvc.perform(post("/api/v1/appointments/create")
                    .with(jwt().jwt(jwt))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(appointmentDto)));

            // Then
            result.andExpect(status().isBadRequest())
                    .andExpect(content().string("Invalid appointment data"));
        }

        @Test
        void shouldReturnInternalServerErrorWhenGenericException() throws Exception {
            // Given
            when(appointmentService.makeAppointment(any(AppointmentDto.class), eq(validToken)))
                    .thenThrow(new RuntimeException("Database error"));

            // When
            ResultActions result = mockMvc.perform(post("/api/v1/appointments/create")
                    .with(jwt().jwt(jwt))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(appointmentDto)));

            // Then
            result.andExpect(status().isInternalServerError())
                    .andExpect(content().string("Internal server error: Database error"));
        }
    }

    @Nested
    class GetByDoctorTests {
        @Test
        @WithMockUser(username = "doctor", roles = {"DOCTOR"})
        void shouldReturnAppointmentsForDoctor() throws Exception {
            // Given
            List<Appointment> appointments = List.of(appointment);
            when(appointmentService.getByDoctor(doctorId)).thenReturn(appointments);

            // When
            ResultActions result = mockMvc.perform(get("/api/v1/appointments/doctor/{doctorId}", doctorId));

            // Then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value(appointmentId))
                    .andExpect(jsonPath("$[0].patientId").value(patientId.toString()))
                    .andExpect(jsonPath("$[0].doctorId").value(doctorId.toString()));
        }

        @Test
        @WithMockUser(username = "doctor", roles = {"DOCTOR"})
        void shouldReturnEmptyListWhenExceptionOccurs() throws Exception {
            // Given
            when(appointmentService.getByDoctor(doctorId)).thenThrow(new RuntimeException("Service error"));

            // When
            ResultActions result = mockMvc.perform(get("/api/v1/appointments/doctor/{doctorId}", doctorId));

            // Then
            result.andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$").isEmpty());
        }
    }


    @Nested
    class GetAvailableDateTests {
        @Test
        @WithMockUser(username = "doctor", roles = {"DOCTOR"})
        void shouldReturnAvailableDates() throws Exception {
            // Given
            List<LocalDateTime> availableDates = List.of(
                    LocalDateTime.of(2024, 1, 15, 9, 0),
                    LocalDateTime.of(2024, 1, 16, 10, 0)
            );

            when(appointmentService.getAvailableDate(doctorId)).thenReturn(availableDates);


            // When
            ResultActions result = mockMvc.perform(get("/api/v1/appointments/available-date/{doctorId}", doctorId));

            // Then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$[0]").value("2024-01-15"))
                    .andExpect(jsonPath("$[1]").value("2024-01-16"));
        }

        @Test
        @WithMockUser(username = "doctor", roles = {"DOCTOR"})
        void shouldReturnEmptyListWhenServiceFails() throws Exception {
            // Given
            when(appointmentService.getAvailableDate(doctorId)).thenThrow(new RuntimeException("Service error"));

            // When
            ResultActions result = mockMvc.perform(get("/api/v1/appointments/available-date/{doctorId}", doctorId));

            // Then
            result.andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$").isEmpty());
        }
    }

    @Nested
    class GetTakenAppointmentsTests {
        @Test
        @WithMockUser(username = "doctor", roles = {"DOCTOR"})
        void shouldReturnTakenAppointments() throws Exception {
            // Given
            LocalDate date = LocalDate.of(2024, 1, 15);
            List<String> takenSlots = List.of("10:00", "11:00", "14:30");
            when(appointmentService.getTakenAppointments(doctorId, date)).thenReturn(takenSlots);

            // When
            ResultActions result = mockMvc.perform(get("/api/v1/appointments/taken-appointments/{doctorId}", doctorId)
                    .param("date", "2024-01-15"));

            // Then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$[0]").value("10:00"))
                    .andExpect(jsonPath("$[1]").value("11:00"))
                    .andExpect(jsonPath("$[2]").value("14:30"));
        }
    }

    @Nested
    class GetByPatientTests {
        @Test
        @WithMockUser(username = "doctor", roles = {"DOCTOR"})
        void shouldReturnAppointmentsForPatient() throws Exception {
            // Given
            List<Appointment> appointments = List.of(appointment);
            when(appointmentService.getByPatientId(patientId)).thenReturn(appointments);

            // When
            ResultActions result = mockMvc.perform(get("/api/v1/appointments/{userId}", patientId));

            // Then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value(appointmentId))
                    .andExpect(jsonPath("$[0].patientId").value(patientId.toString()))
                    .andExpect(jsonPath("$[0].doctorId").value(doctorId.toString()));
        }

        @Test
        @WithMockUser(username = "doctor", roles = {"DOCTOR"})
        void shouldReturnEmptyListWhenPatientNotFound() throws Exception {
            // Given
            when(appointmentService.getByPatientId(patientId)).thenThrow(new RuntimeException("Patient not found"));

            // When
            ResultActions result = mockMvc.perform(get("/api/v1/appointments/{userId}", patientId));

            // Then
            result.andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$").isEmpty());
        }
    }

    @Nested
    class UpdateStatusTests {
        @Test
        @WithMockUser(username = "doctor", roles = {"DOCTOR"})
        void shouldUpdateStatusSuccessfully() throws Exception {
            // Given
            when(appointmentService.updateAppointmentStatus(appointmentId, 1)).thenReturn(true);

            // When
            ResultActions result = mockMvc.perform(put("/api/v1/appointments/{id}/status/{statusNumber}", appointmentId, 1));

            // Then
            result.andExpect(status().isOk())
                    .andExpect(content().string("true"));
        }

        @Test
        @WithMockUser(username = "doctor", roles = {"DOCTOR"})
        void shouldReturnFalseWhenIllegalArgumentException() throws Exception {
            // Given
            when(appointmentService.updateAppointmentStatus(appointmentId, 1))
                    .thenThrow(new IllegalArgumentException("Invalid status"));

            // When
            ResultActions result = mockMvc.perform(put("/api/v1/appointments/{id}/status/{statusNumber}", appointmentId, 1));

            // Then
            result.andExpect(status().isOk())
                    .andExpect(content().string("false"));
        }

        @Test
        @WithMockUser(username = "doctor", roles = {"DOCTOR"})
        void shouldReturnFalseWhenInternalError() throws Exception {
            // Given
            when(appointmentService.updateAppointmentStatus(appointmentId, 1))
                    .thenThrow(new RuntimeException("Database error"));

            // When
            ResultActions result = mockMvc.perform(put("/api/v1/appointments/{id}/status/{statusNumber}", appointmentId, 1));

            // Then
            result.andExpect(status().isInternalServerError())
                    .andExpect(content().string("false"));
        }
    }
}