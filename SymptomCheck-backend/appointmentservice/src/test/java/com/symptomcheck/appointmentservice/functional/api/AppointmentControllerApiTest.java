package com.symptomcheck.appointmentservice.functional.api;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.symptomcheck.appointmentservice.controllers.AppointmentController;
import com.symptomcheck.appointmentservice.dtos.AppointmentDto;
import com.symptomcheck.appointmentservice.enums.AppointmentStatus;
import com.symptomcheck.appointmentservice.models.Appointment;
import com.symptomcheck.appointmentservice.services.AppointmentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Controller-slice tests for AppointmentController using mocked AppointmentService.
 */
@WebMvcTest(controllers = AppointmentController.class)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AppointmentControllerApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AppointmentService appointmentService;

    @Autowired
    private ObjectMapper objectMapper;

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor patientJwt() {
        Jwt jwt = Jwt.withTokenValue("patient-token")
                .header("alg", "none")
                .claim("sub", "patient-sub")
                .claim("realm_access", java.util.Map.of("roles", List.of("PATIENT")))
                .build();
        return jwt().jwt(jwt).authorities(new SimpleGrantedAuthority("ROLE_PATIENT"));
    }

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor doctorJwt() {
        Jwt jwt = Jwt.withTokenValue("doctor-token")
                .header("alg", "none")
                .claim("sub", "doctor-sub")
                .claim("realm_access", java.util.Map.of("roles", List.of("DOCTOR")))
                .build();
        return jwt().jwt(jwt).authorities(new SimpleGrantedAuthority("ROLE_DOCTOR"));
    }

    private Appointment makeAppointment(Long id, LocalDateTime dt, UUID patient, UUID doctor, AppointmentStatus status, String desc) {
        Appointment a = new Appointment();
        a.setId(id);
        a.setDateTime(dt);
        a.setPatientId(patient);
        a.setDoctorId(doctor);
        a.setStatus(status);
        a.setDescription(desc);
        a.setCreatedAt(Instant.now());
        a.setUpdatedAt(Instant.now());
        return a;
    }

    @Test
    void createAppointment_withPatientJwt_returnsSavedAppointment() throws Exception {
        UUID patient = UUID.randomUUID();
        UUID doctor = UUID.randomUUID();

        AppointmentDto dto = new AppointmentDto();
        dto.setDateTime(LocalDateTime.now().plusDays(2).withHour(10));
        dto.setPatientId(patient);
        dto.setDoctorId(doctor);
        dto.setDescription("create test");

        Appointment saved = makeAppointment(101L, dto.getDateTime(), patient, doctor, AppointmentStatus.PENDING, dto.getDescription());
        Mockito.when(appointmentService.makeAppointment(any(AppointmentDto.class), anyString())).thenReturn(saved);

        mockMvc.perform(post("/api/v1/appointments/create")
                        .with(patientJwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(101)))
                .andExpect(jsonPath("$.patientId", is(patient.toString())))
                .andExpect(jsonPath("$.doctorId", is(doctor.toString())))
                .andExpect(jsonPath("$.description", is("create test")));
    }

    @Test
    void getByDoctor_returnsAppointments_forDoctor() throws Exception {
        UUID p = UUID.randomUUID();
        UUID d = UUID.randomUUID();

        Appointment a1 = makeAppointment(201L, LocalDateTime.now().plusDays(1), p, d, AppointmentStatus.PENDING, "a1");
        Appointment a2 = makeAppointment(202L, LocalDateTime.now().plusDays(3), p, d, AppointmentStatus.CONFIRMED, "a2");

        Mockito.when(appointmentService.getByDoctor(eq(d))).thenReturn(List.of(a1, a2));

        mockMvc.perform(get("/api/v1/appointments/doctor/{doctorId}", d)
                        .with(doctorJwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].doctorId", is(d.toString())));
    }

    @Test
    void getTakenAppointments_returnsTimes_forGivenDate() throws Exception {
        UUID p = UUID.randomUUID();
        UUID d = UUID.randomUUID();
        LocalDate date = LocalDate.now().plusDays(4);

        Mockito.when(appointmentService.getTakenAppointments(eq(d), eq(date))).thenReturn(List.of("09:00", "14:30"));

        mockMvc.perform(get("/api/v1/appointments/taken-appointments/{doctorId}", d)
                        .param("date", date.toString())
                        .with(doctorJwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void getByPatient_returnsAppointments_forPatient() throws Exception {
        UUID patient = UUID.randomUUID();
        UUID doctor = UUID.randomUUID();

        Appointment a1 = makeAppointment(301L, LocalDateTime.now().plusDays(1), patient, doctor, AppointmentStatus.PENDING, "byPatient1");
        Appointment a2 = makeAppointment(302L, LocalDateTime.now().plusDays(2), patient, doctor, AppointmentStatus.CONFIRMED, "byPatient2");

        Mockito.when(appointmentService.getByPatientId(eq(patient))).thenReturn(List.of(a1, a2));

        mockMvc.perform(get("/api/v1/appointments/{userId}", patient)
                        .with(patientJwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].patientId", is(patient.toString())));
    }

    @Test
    void updateStatus_withDoctorJwt_changesStatus() throws Exception {
        UUID patient = UUID.randomUUID();
        UUID doctor = UUID.randomUUID();

        // service.updateAppointmentStatus returns true; controller returns ResponseEntity.ok(true)
        Mockito.when(appointmentService.updateAppointmentStatus(eq(401L), eq(1))).thenReturn(true);

        mockMvc.perform(put("/api/v1/appointments/{id}/status/{statusNumber}", 401L, 1)
                        .with(doctorJwt()))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }
}
