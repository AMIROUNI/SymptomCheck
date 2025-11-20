package com.symptomcheck.appointmentservice.services;

import com.symptomcheck.appointmentservice.dtos.dashboardDto.AppointmentDashboardDTO;
import com.symptomcheck.appointmentservice.dtos.dashboardDto.AppointmentStatsDTO;
import com.symptomcheck.appointmentservice.enums.AppointmentStatus;
import com.symptomcheck.appointmentservice.models.Appointment;
import com.symptomcheck.appointmentservice.repositories.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppointmentDashboardService {

    private final AppointmentRepository appointmentRepository;

    public AppointmentDashboardDTO getAppointmentDashboard(UUID doctorId) {
        log.info("Building appointment dashboard for doctor: {}", doctorId);

        AppointmentStatsDTO stats = getAppointmentStats(doctorId);
        Map<String, Long> appointmentsByStatus = getAppointmentsByStatus(doctorId);
        Map<String, Long> weeklyAppointments = getWeeklyAppointments(doctorId);

        return new AppointmentDashboardDTO(stats, appointmentsByStatus, weeklyAppointments);
    }

    private AppointmentStatsDTO getAppointmentStats(UUID doctorId) {
        Long totalAppointments = (long) appointmentRepository.findByDoctorId(doctorId).size();
        Long todayAppointments = getTodayAppointmentsCount(doctorId);
        Long pendingAppointments = appointmentRepository.countByDoctorIdAndStatus(doctorId, AppointmentStatus.PENDING);
        Long completedAppointments = appointmentRepository.countByDoctorIdAndStatus(doctorId, AppointmentStatus.COMPLETED);
        Long cancelledAppointments = appointmentRepository.countByDoctorIdAndStatus(doctorId, AppointmentStatus.CANCELLED);

        return new AppointmentStatsDTO(
                totalAppointments,
                todayAppointments,
                pendingAppointments,
                completedAppointments,
                cancelledAppointments
        );
    }

    private Map<String, Long> getAppointmentsByStatus(UUID doctorId) {
        return Arrays.stream(AppointmentStatus.values())
                .collect(Collectors.toMap(
                        Enum::name,
                        status -> appointmentRepository.countByDoctorIdAndStatus(doctorId, status)
                ));
    }

    private Map<String, Long> getWeeklyAppointments(UUID doctorId) {
        Map<String, Long> weeklyStats = new LinkedHashMap<>();
        LocalDate today = LocalDate.now();

        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            Long count = getAppointmentsCountForDate(doctorId, date);
            weeklyStats.put(date.getDayOfWeek().toString(), count);
        }

        return weeklyStats;
    }

    private Long getTodayAppointmentsCount(UUID doctorId) {
        LocalDateTime startOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime endOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);

        List<Appointment> todayAppointments = appointmentRepository
                .findTodayAppointmentsByDoctorId(doctorId, startOfDay, endOfDay);

        return (long) todayAppointments.size();
    }

    private Long getAppointmentsCountForDate(UUID doctorId, LocalDate date) {
        LocalDateTime startOfDay = LocalDateTime.of(date, LocalTime.MIN);
        LocalDateTime endOfDay = LocalDateTime.of(date, LocalTime.MAX);

        List<Appointment> dayAppointments = appointmentRepository
                .findByDoctorIdAndDateRange(doctorId, startOfDay, endOfDay);

        return (long) dayAppointments.size();
    }

    // Méthodes supplémentaires pour le dashboard
    public List<Appointment> getTodayAppointments(UUID doctorId) {
        LocalDateTime startOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime endOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);

        return appointmentRepository.findTodayAppointmentsByDoctorId(doctorId, startOfDay, endOfDay);
    }

    public List<Appointment> getUpcomingAppointments(UUID doctorId, int days) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endDate = now.plusDays(days);

        return appointmentRepository.findByDoctorIdAndDateRange(doctorId, now, endDate);
    }

    public Map<String, Object> getAppointmentAnalytics(UUID doctorId) {
        Map<String, Object> analytics = new HashMap<>();

        List<Appointment> allAppointments = appointmentRepository.findByDoctorId(doctorId);

        // Taux de completion
        long total = allAppointments.size();
        long completed = allAppointments.stream()
                .filter(a -> a.getStatus() == AppointmentStatus.COMPLETED)
                .count();

        double completionRate = total > 0 ? (completed * 100.0) / total : 0.0;

        analytics.put("totalAppointments", total);
        analytics.put("completionRate", Math.round(completionRate * 100.0) / 100.0);
        analytics.put("averageAppointmentsPerWeek", calculateWeeklyAverage(doctorId));

        return analytics;
    }

    private double calculateWeeklyAverage(UUID doctorId) {
        List<Appointment> appointments = appointmentRepository.findByDoctorId(doctorId);
        if (appointments.isEmpty()) return 0.0;

        // Calcul simplifié de la moyenne hebdomadaire
        return appointments.size() / 4.0; // Sur 4 semaines
    }
}