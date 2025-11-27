package com.symptomcheck.appointmentservice.unit.controllers;

import com.symptomcheck.appointmentservice.controllers.AppointmentDashboardController;
import com.symptomcheck.appointmentservice.dtos.dashboardDto.AppointmentDashboardDTO;
import com.symptomcheck.appointmentservice.dtos.dashboardDto.AppointmentStatsDTO;
import com.symptomcheck.appointmentservice.models.Appointment;
import com.symptomcheck.appointmentservice.services.AppointmentDashboardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AppointmentDashboardControllerTest {

    private AppointmentDashboardService dashboardService;
    private AppointmentDashboardController controller;
    private UUID doctorId;

    @BeforeEach
    void setUp() {
        dashboardService = mock(AppointmentDashboardService.class);
        controller = new AppointmentDashboardController(dashboardService);
        doctorId = UUID.randomUUID();
    }

    // -----------------------------------------------------------------
    // GET APPOINTMENT DASHBOARD
    // -----------------------------------------------------------------
    @Nested
    class GetAppointmentDashboardTests {

        @Test
        void shouldReturnDashboard() {
            AppointmentDashboardDTO dto = new AppointmentDashboardDTO(
                    new AppointmentStatsDTO(10L, 2L, 3L, 4L, 1L),
                    Map.of("PENDING", 3L),
                    Map.of("Monday", 5L)
            );

            when(dashboardService.getAppointmentDashboard(doctorId)).thenReturn(dto);

            ResponseEntity<AppointmentDashboardDTO> response =
                    controller.getAppointmentDashboard(doctorId);

            assertEquals(200, response.getStatusCodeValue());
            assertEquals(dto, response.getBody());
        }

        @Test
        void shouldReturn500OnError() {
            when(dashboardService.getAppointmentDashboard(doctorId))
                    .thenThrow(new RuntimeException("fail"));

            ResponseEntity<AppointmentDashboardDTO> response =
                    controller.getAppointmentDashboard(doctorId);

            assertEquals(500, response.getStatusCodeValue());
            assertNull(response.getBody());
        }
    }


    // -----------------------------------------------------------------
    // GET TODAY APPOINTMENTS
    // -----------------------------------------------------------------
    @Nested
    class GetTodayAppointmentsTests {

        @Test
        void shouldReturnTodayAppointments() {
            List<Appointment> list = List.of(new Appointment(), new Appointment());

            when(dashboardService.getTodayAppointments(doctorId)).thenReturn(list);

            ResponseEntity<List<Appointment>> response =
                    controller.getTodayAppointments(doctorId);

            assertEquals(200, response.getStatusCodeValue());
            assertEquals(2, response.getBody().size());
        }

        @Test
        void shouldReturn500OnError() {
            when(dashboardService.getTodayAppointments(doctorId))
                    .thenThrow(new RuntimeException("fail"));

            ResponseEntity<List<Appointment>> response =
                    controller.getTodayAppointments(doctorId);

            assertEquals(500, response.getStatusCodeValue());
            assertNull(response.getBody());
        }
    }


    // -----------------------------------------------------------------
    // GET UPCOMING APPOINTMENTS
    // -----------------------------------------------------------------
    @Nested
    class GetUpcomingAppointmentsTests {

        @Test
        void shouldReturnUpcomingAppointments() {
            List<Appointment> list = List.of(new Appointment());

            when(dashboardService.getUpcomingAppointments(doctorId, 7))
                    .thenReturn(list);

            ResponseEntity<List<Appointment>> response =
                    controller.getUpcomingAppointments(doctorId, 7);

            assertEquals(200, response.getStatusCodeValue());
            assertEquals(1, response.getBody().size());
        }

        @Test
        void shouldReturn500OnError() {
            when(dashboardService.getUpcomingAppointments(doctorId, 7))
                    .thenThrow(new RuntimeException("fail"));

            ResponseEntity<List<Appointment>> response =
                    controller.getUpcomingAppointments(doctorId, 7);

            assertEquals(500, response.getStatusCodeValue());
            assertNull(response.getBody());
        }
    }


    // -----------------------------------------------------------------
    // GET APPOINTMENT ANALYTICS
    // -----------------------------------------------------------------
    @Nested
    class GetAppointmentAnalyticsTests {

        @Test
        void shouldReturnAnalytics() {
            Map<String, Object> analytics = Map.of("total", 50);

            when(dashboardService.getAppointmentAnalytics(doctorId))
                    .thenReturn(analytics);

            ResponseEntity<Map<String, Object>> response =
                    controller.getAppointmentAnalytics(doctorId);

            assertEquals(200, response.getStatusCodeValue());
            assertEquals(analytics, response.getBody());
        }

        @Test
        void shouldReturn500OnError() {
            when(dashboardService.getAppointmentAnalytics(doctorId))
                    .thenThrow(new RuntimeException("fail"));

            ResponseEntity<Map<String, Object>> response =
                    controller.getAppointmentAnalytics(doctorId);

            assertEquals(500, response.getStatusCodeValue());
            assertNull(response.getBody());
        }
    }
}
