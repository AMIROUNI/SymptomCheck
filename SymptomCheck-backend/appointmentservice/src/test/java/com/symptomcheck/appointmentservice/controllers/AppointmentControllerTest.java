package com.symptomcheck.appointmentservice.controllers;

import com.symptomcheck.appointmentservice.dtos.AppointmentDto;
import com.symptomcheck.appointmentservice.models.Appointment;
import com.symptomcheck.appointmentservice.services.AppointmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AppointmentControllerTest {

    private AppointmentService appointmentService;
    private AppointmentController controller;
    private UUID doctorId;
    private Jwt jwt;

    @BeforeEach
    void setUp() {
        appointmentService = mock(AppointmentService.class);
        controller = new AppointmentController(appointmentService);

        doctorId = UUID.randomUUID();
        jwt = mock(Jwt.class);
        when(jwt.getTokenValue()).thenReturn("fake-token");
    }

    // ----------------------------------------------------------
    // CREATE APPOINTMENT
    // ----------------------------------------------------------
    @Nested
    class CreateAppointmentTests {

        @Test
        void shouldCreateAppointmentSuccessfully() {
            AppointmentDto dto = new AppointmentDto();
            Appointment saved = new Appointment();
            saved.setId(10L);

            when(appointmentService.makeAppointment(dto, "fake-token"))
                    .thenReturn(saved);

            ResponseEntity<?> response = controller.createAppointment(dto, jwt);

            assertEquals(200, response.getStatusCodeValue());
            assertEquals(saved, response.getBody());
            verify(appointmentService).makeAppointment(dto, "fake-token");
        }

        @Test
        void shouldReturnBadRequestOnIllegalArgument() {
            AppointmentDto dto = new AppointmentDto();

            when(appointmentService.makeAppointment(dto, "fake-token"))
                    .thenThrow(new IllegalArgumentException("Invalid"));

            ResponseEntity<?> response = controller.createAppointment(dto, jwt);

            assertEquals(400, response.getStatusCodeValue());
            assertEquals("Invalid", response.getBody());
        }

        @Test
        void shouldReturn500OnUnexpectedError() {
            AppointmentDto dto = new AppointmentDto();

            when(appointmentService.makeAppointment(dto, "fake-token"))
                    .thenThrow(new RuntimeException("Server fail"));

            ResponseEntity<?> response = controller.createAppointment(dto, jwt);

            assertEquals(500, response.getStatusCodeValue());
            assertEquals("Internal server error: Server fail", response.getBody());
        }
    }


    // ----------------------------------------------------------
    // GET BY DOCTOR (LOCAL)
    // ----------------------------------------------------------
    @Nested
    class GetByDoctorTests {

        @Test
        void shouldReturnAppointments() {
            List<Appointment> list = List.of(new Appointment(), new Appointment());

            when(appointmentService.getByDoctor(doctorId)).thenReturn(list);

            ResponseEntity<List<Appointment>> response = controller.getByDoctor(doctorId);

            assertEquals(200, response.getStatusCodeValue());
            assertEquals(2, response.getBody().size());
        }

        @Test
        void shouldReturn500OnException() {
            when(appointmentService.getByDoctor(doctorId))
                    .thenThrow(new RuntimeException("fail"));

            ResponseEntity<List<Appointment>> response = controller.getByDoctor(doctorId);

            assertEquals(500, response.getStatusCodeValue());
            assertTrue(response.getBody().isEmpty());
        }
    }


    // ----------------------------------------------------------
    // GET BY DOCTOR REMOTE
    // ----------------------------------------------------------
    @Nested
    class GetByDoctorRemoteTests {

        @Test
        void shouldReturnRemoteAppointments() {
            List<Appointment> list = List.of(new Appointment());

            when(appointmentService.getByDoctorFromDoctorService(doctorId, "fake-token"))
                    .thenReturn(list);

            ResponseEntity<List<Appointment>> response =
                    controller.getByDoctorRemote(doctorId, jwt);

            assertEquals(200, response.getStatusCodeValue());
            assertEquals(1, response.getBody().size());
        }
    }


    // ----------------------------------------------------------
    // AVAILABLE DATE
    // ----------------------------------------------------------
    @Nested
    class GetAvailableDateTests {



        @Test
        void shouldReturn500OnException() {
            when(appointmentService.getAvailableDate(doctorId))
                    .thenThrow(new RuntimeException("fail"));

            ResponseEntity<?> response = controller.getAvailableDate(doctorId);

            assertEquals(500, response.getStatusCodeValue());
            assertEquals(List.of(), response.getBody());
        }
    }


    // ----------------------------------------------------------
    // TAKEN APPOINTMENTS
    // ----------------------------------------------------------
    @Nested
    class GetTakenAppointmentsTests {

        @Test
        void shouldReturnTakenAppointments() {
            LocalDate date = LocalDate.now();
            List<String> slots = List.of("10:00", "11:00");

            when(appointmentService.getTakenAppointments(doctorId, date))
                    .thenReturn(slots);

            List<String> result = controller.getTakenAppointments(doctorId, date);

            assertEquals(2, result.size());
            assertEquals("10:00", result.get(0));
        }
    }
}
