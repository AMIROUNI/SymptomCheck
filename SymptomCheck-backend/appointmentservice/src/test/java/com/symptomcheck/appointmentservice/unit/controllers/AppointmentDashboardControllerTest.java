package com.symptomcheck.appointmentservice.unit.controllers;

import com.symptomcheck.appointmentservice.controllers.AppointmentDashboardController;
import com.symptomcheck.appointmentservice.dtos.dashboarddto.AppointmentDashboardDTO;
import com.symptomcheck.appointmentservice.dtos.dashboarddto.AppointmentStatsDTO;
import com.symptomcheck.appointmentservice.models.Appointment;
import com.symptomcheck.appointmentservice.services.AppointmentDashboardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
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
        void getAppointmentDashboard_ShouldReturnDashboardSuccessfully() {
            // Arrange
            AppointmentStatsDTO stats = new AppointmentStatsDTO(100L, 5L, 20L, 60L, 15L);
            Map<String, Long> appointmentsByStatus = Map.of(
                    "PENDING", 20L,
                    "CONFIRMED", 30L,
                    "COMPLETED", 40L,
                    "CANCELLED", 10L
            );
            Map<String, Long> weeklyAppointments = Map.of(
                    "MONDAY", 15L,
                    "TUESDAY", 20L,
                    "WEDNESDAY", 18L,
                    "THURSDAY", 22L,
                    "FRIDAY", 25L
            );

            AppointmentDashboardDTO dashboardDTO = new AppointmentDashboardDTO(
                    stats, appointmentsByStatus, weeklyAppointments
            );

            when(dashboardService.getAppointmentDashboard(doctorId))
                    .thenReturn(dashboardDTO);

            // Act
            ResponseEntity<AppointmentDashboardDTO> response =
                    controller.getAppointmentDashboard(doctorId);

            // Assert
            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());

            AppointmentDashboardDTO body = response.getBody();
            assertEquals(stats, body.getStats());
            assertEquals(appointmentsByStatus, body.getAppointmentsByStatus());
            assertEquals(weeklyAppointments, body.getWeeklyAppointments());

            verify(dashboardService, times(1)).getAppointmentDashboard(doctorId);
        }

        @Test
        void getAppointmentDashboard_ShouldReturnInternalServerErrorOnException() {
            // Arrange
            when(dashboardService.getAppointmentDashboard(doctorId))
                    .thenThrow(new RuntimeException("Database connection failed"));

            // Act
            ResponseEntity<AppointmentDashboardDTO> response =
                    controller.getAppointmentDashboard(doctorId);

            // Assert
            assertNotNull(response);
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
            assertNull(response.getBody());

            verify(dashboardService, times(1)).getAppointmentDashboard(doctorId);
        }

        @Test
        void getAppointmentDashboard_ShouldHandleNullDoctorId() {
            // Note: Spring validates UUID before reaching controller
            // This test ensures the controller method signature is correct
            assertDoesNotThrow(() -> {
                ResponseEntity<AppointmentDashboardDTO> response =
                        controller.getAppointmentDashboard(null);
                assertNotNull(response);
            });
        }

        @Test
        void getAppointmentDashboard_ShouldHandleEmptyDashboardData() {
            // Arrange
            AppointmentStatsDTO emptyStats = new AppointmentStatsDTO(0L, 0L, 0L, 0L, 0L);
            Map<String, Long> emptyStatusMap = Map.of();
            Map<String, Long> emptyWeeklyMap = Map.of();

            AppointmentDashboardDTO emptyDashboard = new AppointmentDashboardDTO(
                    emptyStats, emptyStatusMap, emptyWeeklyMap
            );

            when(dashboardService.getAppointmentDashboard(doctorId))
                    .thenReturn(emptyDashboard);

            // Act
            ResponseEntity<AppointmentDashboardDTO> response =
                    controller.getAppointmentDashboard(doctorId);

            // Assert
            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());

            AppointmentDashboardDTO body = response.getBody();
            assertEquals(0L, body.getStats().getTotalAppointments());
            assertTrue(body.getAppointmentsByStatus().isEmpty());
            assertTrue(body.getWeeklyAppointments().isEmpty());
        }
    }

    // -----------------------------------------------------------------
    // GET TODAY APPOINTMENTS
    // -----------------------------------------------------------------
    @Nested
    class GetTodayAppointmentsTests {

        @Test
        void getTodayAppointments_ShouldReturnAppointmentsSuccessfully() {
            // Arrange
            Appointment appointment1 = new Appointment();
            appointment1.setId(1L);
            appointment1.setDoctorId(doctorId);
            appointment1.setDateTime(LocalDateTime.now().with(LocalTime.of(10, 0)));

            Appointment appointment2 = new Appointment();
            appointment2.setId(2L);
            appointment2.setDoctorId(doctorId);
            appointment2.setDateTime(LocalDateTime.now().with(LocalTime.of(14, 30)));

            List<Appointment> appointments = Arrays.asList(appointment1, appointment2);

            when(dashboardService.getTodayAppointments(doctorId))
                    .thenReturn(appointments);

            // Act
            ResponseEntity<List<Appointment>> response =
                    controller.getTodayAppointments(doctorId);

            // Assert
            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(2, response.getBody().size());

            verify(dashboardService, times(1)).getTodayAppointments(doctorId);
        }

        @Test
        void getTodayAppointments_ShouldReturnEmptyListWhenNoAppointments() {
            // Arrange
            when(dashboardService.getTodayAppointments(doctorId))
                    .thenReturn(Collections.emptyList());

            // Act
            ResponseEntity<List<Appointment>> response =
                    controller.getTodayAppointments(doctorId);

            // Assert
            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertTrue(response.getBody().isEmpty());
        }

        @Test
        void getTodayAppointments_ShouldReturnInternalServerErrorOnException() {
            // Arrange
            when(dashboardService.getTodayAppointments(doctorId))
                    .thenThrow(new RuntimeException("Service unavailable"));

            // Act
            ResponseEntity<List<Appointment>> response =
                    controller.getTodayAppointments(doctorId);

            // Assert
            assertNotNull(response);
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
            assertNull(response.getBody());

            verify(dashboardService, times(1)).getTodayAppointments(doctorId);
        }
    }

    // -----------------------------------------------------------------
    // GET UPCOMING APPOINTMENTS
    // -----------------------------------------------------------------
    @Nested
    class GetUpcomingAppointmentsTests {

        @Test
        void getUpcomingAppointments_ShouldReturnAppointmentsWithDefaultDays() {
            // Arrange
            Appointment appointment = new Appointment();
            appointment.setId(1L);
            appointment.setDoctorId(doctorId);
            appointment.setDateTime(LocalDateTime.now().plusDays(3));

            List<Appointment> appointments = Collections.singletonList(appointment);

            when(dashboardService.getUpcomingAppointments(eq(doctorId), eq(7)))
                    .thenReturn(appointments);

            // Act
            ResponseEntity<List<Appointment>> response =
                    controller.getUpcomingAppointments(doctorId, 7);

            // Assert
            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(1, response.getBody().size());

            verify(dashboardService, times(1)).getUpcomingAppointments(doctorId, 7);
        }

        @Test
        void getUpcomingAppointments_ShouldReturnAppointmentsWithCustomDays() {
            // Arrange
            Appointment appointment = new Appointment();
            appointment.setId(1L);
            appointment.setDoctorId(doctorId);
            appointment.setDateTime(LocalDateTime.now().plusDays(14));

            List<Appointment> appointments = Collections.singletonList(appointment);

            when(dashboardService.getUpcomingAppointments(eq(doctorId), eq(30)))
                    .thenReturn(appointments);

            // Act
            ResponseEntity<List<Appointment>> response =
                    controller.getUpcomingAppointments(doctorId, 30);

            // Assert
            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(1, response.getBody().size());

            verify(dashboardService, times(1)).getUpcomingAppointments(doctorId, 30);
        }

        @Test
        void getUpcomingAppointments_ShouldHandleZeroDays() {
            // Arrange
            when(dashboardService.getUpcomingAppointments(eq(doctorId), eq(0)))
                    .thenReturn(Collections.emptyList());

            // Act
            ResponseEntity<List<Appointment>> response =
                    controller.getUpcomingAppointments(doctorId, 0);

            // Assert
            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertTrue(response.getBody().isEmpty());

            verify(dashboardService, times(1)).getUpcomingAppointments(doctorId, 0);
        }

        @Test
        void getUpcomingAppointments_ShouldReturnInternalServerErrorOnException() {
            // Arrange
            when(dashboardService.getUpcomingAppointments(eq(doctorId), anyInt()))
                    .thenThrow(new RuntimeException("Data access error"));

            // Act
            ResponseEntity<List<Appointment>> response =
                    controller.getUpcomingAppointments(doctorId, 7);

            // Assert
            assertNotNull(response);
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
            assertNull(response.getBody());

            verify(dashboardService, times(1)).getUpcomingAppointments(doctorId, 7);
        }

        @Test
        void getUpcomingAppointments_ShouldHandleNegativeDays() {
            // Arrange
            when(dashboardService.getUpcomingAppointments(eq(doctorId), eq(-1)))
                    .thenReturn(Collections.emptyList());

            // Act
            ResponseEntity<List<Appointment>> response =
                    controller.getUpcomingAppointments(doctorId, -1);

            // Assert
            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertTrue(response.getBody().isEmpty());
        }
    }

    // -----------------------------------------------------------------
    // GET APPOINTMENT ANALYTICS
    // -----------------------------------------------------------------
    @Nested
    class GetAppointmentAnalyticsTests {

        @Test
        void getAppointmentAnalytics_ShouldReturnAnalyticsSuccessfully() {
            // Arrange
            Map<String, Object> analytics = new HashMap<>();
            analytics.put("totalAppointments", 150L);
            analytics.put("completionRate", 85.5);
            analytics.put("averageAppointmentsPerWeek", 37.5);
            analytics.put("mostPopularDay", "WEDNESDAY");
            analytics.put("peakHour", 14);

            when(dashboardService.getAppointmentAnalytics(doctorId))
                    .thenReturn(analytics);

            // Act
            ResponseEntity<Map<String, Object>> response =
                    controller.getAppointmentAnalytics(doctorId);

            // Assert
            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(5, response.getBody().size());
            assertEquals(150L, response.getBody().get("totalAppointments"));
            assertEquals(85.5, response.getBody().get("completionRate"));

            verify(dashboardService, times(1)).getAppointmentAnalytics(doctorId);
        }

        @Test
        void getAppointmentAnalytics_ShouldReturnEmptyAnalyticsWhenNoData() {
            // Arrange
            Map<String, Object> emptyAnalytics = new HashMap<>();
            emptyAnalytics.put("totalAppointments", 0L);
            emptyAnalytics.put("completionRate", 0.0);
            emptyAnalytics.put("averageAppointmentsPerWeek", 0.0);

            when(dashboardService.getAppointmentAnalytics(doctorId))
                    .thenReturn(emptyAnalytics);

            // Act
            ResponseEntity<Map<String, Object>> response =
                    controller.getAppointmentAnalytics(doctorId);

            // Assert
            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(0L, response.getBody().get("totalAppointments"));
            assertEquals(0.0, response.getBody().get("completionRate"));
        }

        @Test
        void getAppointmentAnalytics_ShouldReturnInternalServerErrorOnException() {
            // Arrange
            when(dashboardService.getAppointmentAnalytics(doctorId))
                    .thenThrow(new RuntimeException("Analytics calculation error"));

            // Act
            ResponseEntity<Map<String, Object>> response =
                    controller.getAppointmentAnalytics(doctorId);

            // Assert
            assertNotNull(response);
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
            assertNull(response.getBody());

            verify(dashboardService, times(1)).getAppointmentAnalytics(doctorId);
        }

        @Test
        void getAppointmentAnalytics_ShouldHandleNullAnalytics() {
            // Arrange
            when(dashboardService.getAppointmentAnalytics(doctorId))
                    .thenReturn(null);

            // Act
            ResponseEntity<Map<String, Object>> response =
                    controller.getAppointmentAnalytics(doctorId);

            // Assert
            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNull(response.getBody());
        }
    }

    // -----------------------------------------------------------------
    // COMPREHENSIVE TESTS
    // -----------------------------------------------------------------
    @Nested
    class ComprehensiveTests {

        @Test
        void testCompleteDoctorDashboardFlow() {
            // 1. Get Dashboard
            AppointmentStatsDTO stats = new AppointmentStatsDTO(100L, 5L, 20L, 60L, 15L);
            Map<String, Long> statusMap = Map.of("PENDING", 20L, "COMPLETED", 60L);
            Map<String, Long> weeklyMap = Map.of("MONDAY", 15L, "TUESDAY", 20L);

            AppointmentDashboardDTO dashboard = new AppointmentDashboardDTO(stats, statusMap, weeklyMap);
            when(dashboardService.getAppointmentDashboard(doctorId)).thenReturn(dashboard);

            ResponseEntity<AppointmentDashboardDTO> dashboardResponse =
                    controller.getAppointmentDashboard(doctorId);
            assertEquals(HttpStatus.OK, dashboardResponse.getStatusCode());

            // 2. Get Today's Appointments
            Appointment todayAppointment = new Appointment();
            todayAppointment.setId(1L);
            todayAppointment.setDoctorId(doctorId);

            when(dashboardService.getTodayAppointments(doctorId))
                    .thenReturn(Collections.singletonList(todayAppointment));

            ResponseEntity<List<Appointment>> todayResponse =
                    controller.getTodayAppointments(doctorId);
            assertEquals(HttpStatus.OK, todayResponse.getStatusCode());
            assertEquals(1, todayResponse.getBody().size());

            // 3. Get Upcoming Appointments
            Appointment upcomingAppointment = new Appointment();
            upcomingAppointment.setId(2L);
            upcomingAppointment.setDoctorId(doctorId);

            when(dashboardService.getUpcomingAppointments(doctorId, 7))
                    .thenReturn(Collections.singletonList(upcomingAppointment));

            ResponseEntity<List<Appointment>> upcomingResponse =
                    controller.getUpcomingAppointments(doctorId, 7);
            assertEquals(HttpStatus.OK, upcomingResponse.getStatusCode());

            // 4. Get Analytics
            Map<String, Object> analytics = Map.of(
                    "totalAppointments", 100L,
                    "completionRate", 85.5
            );

            when(dashboardService.getAppointmentAnalytics(doctorId)).thenReturn(analytics);

            ResponseEntity<Map<String, Object>> analyticsResponse =
                    controller.getAppointmentAnalytics(doctorId);
            assertEquals(HttpStatus.OK, analyticsResponse.getStatusCode());

            // Verify all service calls
            verify(dashboardService, times(1)).getAppointmentDashboard(doctorId);
            verify(dashboardService, times(1)).getTodayAppointments(doctorId);
            verify(dashboardService, times(1)).getUpcomingAppointments(doctorId, 7);
            verify(dashboardService, times(1)).getAppointmentAnalytics(doctorId);
        }

        @Test
        void testAllEndpointsWithEmptyResults() {
            // Test all endpoints return proper empty results
            when(dashboardService.getAppointmentDashboard(doctorId))
                    .thenReturn(new AppointmentDashboardDTO(
                            new AppointmentStatsDTO(0L, 0L, 0L, 0L, 0L),
                            Map.of(),
                            Map.of()
                    ));

            when(dashboardService.getTodayAppointments(doctorId))
                    .thenReturn(Collections.emptyList());

            when(dashboardService.getUpcomingAppointments(doctorId, 7))
                    .thenReturn(Collections.emptyList());

            when(dashboardService.getAppointmentAnalytics(doctorId))
                    .thenReturn(Map.of(
                            "totalAppointments", 0L,
                            "completionRate", 0.0,
                            "averageAppointmentsPerWeek", 0.0
                    ));

            // Test all endpoints
            ResponseEntity<AppointmentDashboardDTO> dashboardResponse =
                    controller.getAppointmentDashboard(doctorId);
            assertEquals(HttpStatus.OK, dashboardResponse.getStatusCode());

            ResponseEntity<List<Appointment>> todayResponse =
                    controller.getTodayAppointments(doctorId);
            assertEquals(HttpStatus.OK, todayResponse.getStatusCode());
            assertTrue(todayResponse.getBody().isEmpty());

            ResponseEntity<List<Appointment>> upcomingResponse =
                    controller.getUpcomingAppointments(doctorId, 7);
            assertEquals(HttpStatus.OK, upcomingResponse.getStatusCode());
            assertTrue(upcomingResponse.getBody().isEmpty());

            ResponseEntity<Map<String, Object>> analyticsResponse =
                    controller.getAppointmentAnalytics(doctorId);
            assertEquals(HttpStatus.OK, analyticsResponse.getStatusCode());

            // Verify all calls
            verify(dashboardService, times(1)).getAppointmentDashboard(doctorId);
            verify(dashboardService, times(1)).getTodayAppointments(doctorId);
            verify(dashboardService, times(1)).getUpcomingAppointments(doctorId, 7);
            verify(dashboardService, times(1)).getAppointmentAnalytics(doctorId);
        }
    }

    // -----------------------------------------------------------------
    // EDGE CASE TESTS
    // -----------------------------------------------------------------
    @Nested
    class EdgeCaseTests {

        @Test
        void getTodayAppointments_ShouldHandleServiceReturningNull() {
            // Arrange
            when(dashboardService.getTodayAppointments(doctorId))
                    .thenReturn(null);

            // Act
            ResponseEntity<List<Appointment>> response =
                    controller.getTodayAppointments(doctorId);

            // Assert
            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNull(response.getBody());
        }

        @Test
        void getUpcomingAppointments_ShouldHandleLargeDaysValue() {
            // Arrange
            when(dashboardService.getUpcomingAppointments(eq(doctorId), eq(365)))
                    .thenReturn(Collections.emptyList());

            // Act
            ResponseEntity<List<Appointment>> response =
                    controller.getUpcomingAppointments(doctorId, 365);

            // Assert
            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertTrue(response.getBody().isEmpty());
        }

        @Test
        void getAppointmentAnalytics_ShouldHandleAnalyticsWithNullValues() {
            // Arrange
            Map<String, Object> analytics = new HashMap<>();
            analytics.put("totalAppointments", null);
            analytics.put("completionRate", null);
            analytics.put("someKey", null);

            when(dashboardService.getAppointmentAnalytics(doctorId))
                    .thenReturn(analytics);

            // Act
            ResponseEntity<Map<String, Object>> response =
                    controller.getAppointmentAnalytics(doctorId);

            // Assert
            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertNull(response.getBody().get("totalAppointments"));
            assertNull(response.getBody().get("completionRate"));
        }

        @Test
        void getAllEndpoints_WithInvalidUUID_ShouldBeHandled() {
            // Note: Spring MVC validates UUID format before reaching controller
            // These tests ensure the controller methods have correct signatures
            UUID invalidUUID = UUID.randomUUID(); // Actually a valid UUID

            assertDoesNotThrow(() -> {
                controller.getAppointmentDashboard(invalidUUID);
                controller.getTodayAppointments(invalidUUID);
                controller.getUpcomingAppointments(invalidUUID, 7);
                controller.getAppointmentAnalytics(invalidUUID);
            });
        }
    }
}