package com.symptomcheck.appointmentservice.unit.controllers;

import com.symptomcheck.appointmentservice.controllers.AppointmentController;
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

    @Nested
    class UpdateStatus {

        private final Long VALID_ID = 1L;
        private final int VALID_STATUS = 2;

        @Test
        void shouldReturnTrueWhenUpdateIsSuccessful() {
            // Act
            ResponseEntity<Boolean> response = controller.updateStatus(VALID_ID, VALID_STATUS);

            // Assert
            assertTrue(response.getBody());
            assertEquals(200, response.getStatusCodeValue());
            verify(appointmentService).updateAppointmentStatus(VALID_ID, VALID_STATUS);
        }

        @Test
        void shouldReturnFalseWhenIllegalArgumentExceptionIsThrown() {
            // Arrange
            doThrow(new IllegalArgumentException("Invalid status"))
                    .when(appointmentService)
                    .updateAppointmentStatus(VALID_ID, VALID_STATUS);

            // Act
            ResponseEntity<Boolean> response = controller.updateStatus(VALID_ID, VALID_STATUS);

            // Assert
            assertFalse(response.getBody());
            assertEquals(200, response.getStatusCodeValue());
            verify(appointmentService).updateAppointmentStatus(VALID_ID, VALID_STATUS);
        }

        @Test
        void shouldReturnFalseWith500StatusCodeWhenGenericExceptionIsThrown() {
            // Arrange
            doThrow(new RuntimeException("Database error"))
                    .when(appointmentService)
                    .updateAppointmentStatus(VALID_ID, VALID_STATUS);

            // Act
            ResponseEntity<Boolean> response = controller.updateStatus(VALID_ID, VALID_STATUS);

            // Assert
            assertFalse(response.getBody());
            assertEquals(500, response.getStatusCodeValue());
            verify(appointmentService).updateAppointmentStatus(VALID_ID, VALID_STATUS);
        }

        @Test
        void shouldHandleNullId() {
            // Act
            ResponseEntity<Boolean> response = controller.updateStatus(null, VALID_STATUS);

            // Assert
            assertNotNull(response);
            // The actual behavior depends on how appointmentService handles null ID
            // This test verifies the method doesn't throw an exception with null input
        }

        @Test
        void shouldHandleNegativeStatusNumber() {
            // Act
            ResponseEntity<Boolean> response = controller.updateStatus(VALID_ID, -1);

            // Assert
            assertNotNull(response);
            // The actual behavior depends on business logic validation
        }
    }
}
