package com.symptomcheck.appointmentservice.unit.services;

import com.symptomcheck.appointmentservice.dtos.adminDashboardDto.AdminAppointmentDto;
import com.symptomcheck.appointmentservice.dtos.adminDashboardDto.AppointmentStatsDto;
import com.symptomcheck.appointmentservice.enums.AppointmentStatus;
import com.symptomcheck.appointmentservice.models.Appointment;
import com.symptomcheck.appointmentservice.repositories.AppointmentRepository;
import com.symptomcheck.appointmentservice.services.AdminDashboardService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminDashboardServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @InjectMocks
    private AdminDashboardService adminDashboardService;

    private Appointment appointment1;
    private Appointment appointment2;
    private Appointment appointment3;

    @BeforeEach
    void setUp() {
        appointment1 = new Appointment();
        appointment1.setId(1L);
        appointment1.setDoctorId(UUID.randomUUID());
        appointment1.setPatientId(UUID.randomUUID());
        appointment1.setStatus(AppointmentStatus.PENDING);
        appointment1.setDateTime(LocalDateTime.now());
        appointment1.setCreatedAt(Instant.now());

        appointment2 = new Appointment();
        appointment2.setId(2L);
        appointment2.setDoctorId(appointment1.getDoctorId());
        appointment2.setPatientId(UUID.randomUUID());
        appointment2.setStatus(AppointmentStatus.CONFIRMED);
        appointment2.setDateTime(LocalDateTime.now());
        appointment2.setCreatedAt(Instant.now());

        appointment3 = new Appointment();
        appointment3.setId(3L);
        appointment3.setDoctorId(appointment1.getDoctorId());
        appointment3.setPatientId(UUID.randomUUID());
        appointment3.setStatus(AppointmentStatus.COMPLETED);
        appointment3.setDateTime(LocalDateTime.now());
        appointment3.setCreatedAt(Instant.now());
    }

    @Nested
    @DisplayName("Statistics Tests")
    class StatisticsTests {

        @Test
        @DisplayName("Should return correct appointment statistics")
        void testGetAppointmentStatistics() {
            when(appointmentRepository.count()).thenReturn(3L);
            when(appointmentRepository.countByStatus(AppointmentStatus.PENDING)).thenReturn(1L);
            when(appointmentRepository.countByStatus(AppointmentStatus.CONFIRMED)).thenReturn(1L);
            when(appointmentRepository.countByStatus(AppointmentStatus.COMPLETED)).thenReturn(1L);
            when(appointmentRepository.countByStatus(AppointmentStatus.CANCELLED)).thenReturn(0L);
            when(appointmentRepository.countTodayAppointmentsAlternative()).thenReturn(2L);
            when(appointmentRepository.countByDateTimeBetween(any(LocalDateTime.class), any(LocalDateTime.class)))
                    .thenReturn(3L);

            AppointmentStatsDto stats = adminDashboardService.getAppointmentStatistics();

            assertEquals(3L, stats.getTotalAppointments());
            assertEquals(1L, stats.getPendingAppointments());
            assertEquals(1L, stats.getConfirmedAppointments());
            assertEquals(1L, stats.getCompletedAppointments());
            assertEquals(0L, stats.getCancelledAppointments());
            assertEquals(2L, stats.getTodayAppointments());
            assertEquals(3L, stats.getWeeklyAppointments());
            assertNotNull(stats.getStatusDistribution());
            assertEquals(4, stats.getStatusDistribution().size());
        }
    }

    @Nested
    @DisplayName("Get All Appointments Tests")
    class GetAllTests {

        @Test
        @DisplayName("Should return all appointments")
        void testGetAllAppointments() {
            when(appointmentRepository.findAll()).thenReturn(List.of(appointment1, appointment2, appointment3));

            List<AdminAppointmentDto> result = adminDashboardService.getAllAppointments();

            assertEquals(3, result.size());
            assertTrue(result.stream().anyMatch(a -> a.getId().equals(1L)));
        }
    }

    @Nested
    @DisplayName("Get Appointments By Status Tests")
    class GetByStatusTests {

        @Test
        @DisplayName("Should return appointments by status")
        void testGetAppointmentsByStatus() {
            when(appointmentRepository.findByStatus(AppointmentStatus.PENDING))
                    .thenReturn(List.of(appointment1));

            List<AdminAppointmentDto> result = adminDashboardService.getAppointmentsByStatus("PENDING");

            assertEquals(1, result.size());
            assertEquals("PENDING", result.get(0).getStatus());
        }
    }

    @Nested
    @DisplayName("Get Appointments By Date Range Tests")
    class GetByDateRangeTests {

        @Test
        @DisplayName("Should return appointments within date range")
        void testGetAppointmentsByDateRange() {
            LocalDateTime start = LocalDateTime.now().minusDays(1);
            LocalDateTime end = LocalDateTime.now().plusDays(1);

            when(appointmentRepository.findByDateTimeBetween(start, end))
                    .thenReturn(List.of(appointment1, appointment2));

            List<AdminAppointmentDto> result = adminDashboardService.getAppointmentsByDateRange(start, end);

            assertEquals(2, result.size());
        }
    }

    @Nested
    @DisplayName("Get By Doctor and Update Status Tests")
    class GetByDoctorAndUpdateStatusTests {

        @Test
        @DisplayName("Should return appointments for a doctor")
        void testGetAppointmentsByDoctor() {
            UUID doctorId = appointment1.getDoctorId();
            when(appointmentRepository.findByDoctorId(doctorId))
                    .thenReturn(List.of(appointment1, appointment2, appointment3));

            List<AdminAppointmentDto> result = adminDashboardService.getAppointmentsByDoctor(doctorId);

            assertEquals(3, result.size());
        }

        @Test
        @DisplayName("Should update appointment status")
        void testUpdateAppointmentStatus() {
            appointment1.setStatus(AppointmentStatus.PENDING);
            when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment1));
            when(appointmentRepository.save(any(Appointment.class))).thenAnswer(invocation -> invocation.getArgument(0));

            AdminAppointmentDto result = adminDashboardService.updateAppointmentStatus(1L, "COMPLETED");

            assertEquals("COMPLETED", result.getStatus());
            verify(appointmentRepository).save(appointment1);
        }
    }
}
