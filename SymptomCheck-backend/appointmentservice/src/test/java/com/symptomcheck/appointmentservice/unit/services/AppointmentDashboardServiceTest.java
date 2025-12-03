package com.symptomcheck.appointmentservice.unit.services;

import com.symptomcheck.appointmentservice.dtos.dashboarddto.AppointmentDashboardDTO;
import com.symptomcheck.appointmentservice.dtos.dashboarddto.AppointmentStatsDTO;
import com.symptomcheck.appointmentservice.enums.AppointmentStatus;
import com.symptomcheck.appointmentservice.models.Appointment;
import com.symptomcheck.appointmentservice.repositories.AppointmentRepository;
import com.symptomcheck.appointmentservice.services.AppointmentDashboardService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Nested;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class AppointmentDashboardServiceTest {

    @Mock
    AppointmentRepository appointmentRepository;

    @InjectMocks
    AppointmentDashboardService dashboardService;

    AutoCloseable closeable;
    UUID doctorId;

    @BeforeEach
    void setup() {
        closeable = MockitoAnnotations.openMocks(this);
        doctorId = UUID.randomUUID();
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    // ---------------------------------------------------------
    // 1. getAppointmentDashboard()
    // ---------------------------------------------------------
    @Nested
    @DisplayName("getAppointmentDashboard() Tests")
    class GetAppointmentDashboardTest {

        @Test
        @DisplayName("Should return dashboard object correctly")
        void testGetAppointmentDashboard() {
            when(appointmentRepository.findByDoctorId(doctorId))
                    .thenReturn(List.of(new Appointment(), new Appointment()));

            when(appointmentRepository.countByDoctorIdAndStatus(any(), eq(AppointmentStatus.PENDING)))
                    .thenReturn(1L);
            when(appointmentRepository.countByDoctorIdAndStatus(any(), eq(AppointmentStatus.COMPLETED)))
                    .thenReturn(1L);
            when(appointmentRepository.countByDoctorIdAndStatus(any(), eq(AppointmentStatus.CANCELLED)))
                    .thenReturn(0L);

            when(appointmentRepository.findTodayAppointmentsByDoctorId(any(), any(), any()))
                    .thenReturn(List.of(new Appointment()));

            when(appointmentRepository.findByDoctorIdAndDateRange(any(), any(), any()))
                    .thenReturn(List.of());

            AppointmentDashboardDTO result = dashboardService.getAppointmentDashboard(doctorId);

            assertNotNull(result);
            assertNotNull(result.getStats());
            assertEquals(4, result.getAppointmentsByStatus().size());
        }
    }

    // ---------------------------------------------------------
    // 2. getAppointmentStats()
    // ---------------------------------------------------------
    @Nested
    @DisplayName("getAppointmentStats() Tests")
    class GetAppointmentStatsTest {

        @Test
        @DisplayName("Should calculate stats correctly")
        void testGetAppointmentStats() {
            when(appointmentRepository.findByDoctorId(doctorId))
                    .thenReturn(List.of(new Appointment(), new Appointment()));

            when(appointmentRepository.countByDoctorIdAndStatus(doctorId, AppointmentStatus.PENDING))
                    .thenReturn(1L);
            when(appointmentRepository.countByDoctorIdAndStatus(doctorId, AppointmentStatus.COMPLETED))
                    .thenReturn(1L);
            when(appointmentRepository.countByDoctorIdAndStatus(doctorId, AppointmentStatus.CANCELLED))
                    .thenReturn(0L);

            when(appointmentRepository.findTodayAppointmentsByDoctorId(any(), any(), any()))
                    .thenReturn(List.of(new Appointment()));

            AppointmentStatsDTO stats = dashboardService
                    .getAppointmentDashboard(doctorId)
                    .getStats();

            assertEquals(2L, stats.getTotalAppointments());
            assertEquals(1L, stats.getTodayAppointments());
            assertEquals(1L, stats.getPendingAppointments());
        }
    }

    // ---------------------------------------------------------
    // 3. getAppointmentsByStatus()
    // ---------------------------------------------------------
    @Nested
    @DisplayName("getAppointmentsByStatus() Tests")
    class GetAppointmentsByStatusTest {

        @Test
        @DisplayName("Should return status counts for all enums")
        void testGetAppointmentsByStatus() {
            for (AppointmentStatus status : AppointmentStatus.values()) {
                when(appointmentRepository.countByDoctorIdAndStatus(doctorId, status))
                        .thenReturn(2L);
            }

            Map<String, Long> result =
                    dashboardService.getAppointmentDashboard(doctorId).getAppointmentsByStatus();

            assertEquals(AppointmentStatus.values().length, result.size());
            assertEquals(2L, result.get("PENDING"));
        }
    }

    // ---------------------------------------------------------
    // 4. getWeeklyAppointments()
    // ---------------------------------------------------------
    @Nested
    @DisplayName("getWeeklyAppointments() Tests")
    class GetWeeklyAppointmentsTest {

        @Test
        @DisplayName("Should return 7 entries for the last 7 days")
        void testWeeklyAppointments() {
            when(appointmentRepository.findByDoctorIdAndDateRange(any(), any(), any()))
                    .thenReturn(List.of(new Appointment()));

            Map<String, Long> weekly =
                    dashboardService.getAppointmentDashboard(doctorId).getWeeklyAppointments();

            assertEquals(7, weekly.size());
        }
    }

    // ---------------------------------------------------------
    // 5. getTodayAppointments()
    // ---------------------------------------------------------
    @Nested
    @DisplayName("getTodayAppointments() Tests")
    class GetTodayAppointmentsTest {

        @Test
        @DisplayName("Should return today's appointments")
        void testTodayAppointments() {
            when(appointmentRepository.findTodayAppointmentsByDoctorId(any(), any(), any()))
                    .thenReturn(List.of(new Appointment(), new Appointment()));

            List<Appointment> list = dashboardService.getTodayAppointments(doctorId);

            assertEquals(2, list.size());
        }
    }

    // ---------------------------------------------------------
    // 6. getUpcomingAppointments()
    // ---------------------------------------------------------
    @Nested
    @DisplayName("getUpcomingAppointments() Tests")
    class GetUpcomingAppointmentsTest {

        @Test
        @DisplayName("Should return upcoming appointments in range")
        void testUpcomingAppointments() {
            when(appointmentRepository.findByDoctorIdAndDateRange(any(), any(), any()))
                    .thenReturn(List.of(new Appointment()));

            List<Appointment> list = dashboardService.getUpcomingAppointments(doctorId, 3);

            assertEquals(1, list.size());
        }
    }

    // ---------------------------------------------------------
    // 7. getAppointmentAnalytics()
    // ---------------------------------------------------------
    @Nested
    @DisplayName("getAppointmentAnalytics() Tests")
    class GetAppointmentAnalyticsTest {

        @Test
        @DisplayName("Should calculate analytics correctly")
        void testAnalytics() {
            Appointment completed = new Appointment();
            completed.setStatus(AppointmentStatus.COMPLETED);

            Appointment pending = new Appointment();
            pending.setStatus(AppointmentStatus.PENDING);

            when(appointmentRepository.findByDoctorId(doctorId))
                    .thenReturn(List.of(completed, pending));

            Map<String, Object> analytics =
                    dashboardService.getAppointmentAnalytics(doctorId);

            assertEquals(2L, analytics.get("totalAppointments"));
            assertEquals(0.5, analytics.get("averageAppointmentsPerWeek"));
            assertEquals(50.0, analytics.get("completionRate"));
        }
    }
}
