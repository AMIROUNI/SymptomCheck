package com.symptomcheck.appointmentservice.services;

import com.symptomcheck.appointmentservice.dtos.adminDashboardDto.AdminAppointmentDto;
import com.symptomcheck.appointmentservice.dtos.adminDashboardDto.AppointmentStatsDto;
import com.symptomcheck.appointmentservice.models.Appointment;
import com.symptomcheck.appointmentservice.enums.AppointmentStatus;
import com.symptomcheck.appointmentservice.repositories.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminDashboardService {

    private final AppointmentRepository appointmentRepository;

    public AppointmentStatsDto getAppointmentStatistics() {
        AppointmentStatsDto stats = new AppointmentStatsDto();

        // Basic counts
        stats.setTotalAppointments(appointmentRepository.count());
        stats.setPendingAppointments(appointmentRepository.countByStatus(AppointmentStatus.PENDING));
        stats.setConfirmedAppointments(appointmentRepository.countByStatus(AppointmentStatus.CONFIRMED));
        stats.setCompletedAppointments(appointmentRepository.countByStatus(AppointmentStatus.COMPLETED));
        stats.setCancelledAppointments(appointmentRepository.countByStatus(AppointmentStatus.CANCELLED));

        // Today's appointments
        stats.setTodayAppointments(appointmentRepository.countTodayAppointmentsAlternative());

        // Weekly appointments
        LocalDateTime startOfWeek = LocalDate.now().atStartOfDay().minusDays(7);
        LocalDateTime endOfWeek = LocalDate.now().atTime(LocalTime.MAX);
        stats.setWeeklyAppointments(appointmentRepository.countByDateTimeBetween(startOfWeek, endOfWeek));

        // Status distribution
        Map<String, Long> statusDistribution = new HashMap<>();
        for (AppointmentStatus status : AppointmentStatus.values()) {
            statusDistribution.put(status.name(), appointmentRepository.countByStatus(status));
        }
        stats.setStatusDistribution(statusDistribution);

        stats.setLastUpdated(LocalDateTime.now());

        return stats;
    }

    public List<AdminAppointmentDto> getAllAppointments() {
        return appointmentRepository.findAll().stream()
                .map(this::convertToAdminAppointmentDto)
                .collect(Collectors.toList());
    }

    public List<AdminAppointmentDto> getAppointmentsByStatus(String status) {
        AppointmentStatus appointmentStatus = AppointmentStatus.valueOf(status.toUpperCase());
        return appointmentRepository.findByStatus(appointmentStatus).stream()
                .map(this::convertToAdminAppointmentDto)
                .collect(Collectors.toList());
    }

    public List<AdminAppointmentDto> getAppointmentsByDateRange(LocalDateTime start, LocalDateTime end) {
        return appointmentRepository.findByDateTimeBetween(start, end).stream()
                .map(this::convertToAdminAppointmentDto)
                .collect(Collectors.toList());
    }

    public List<AdminAppointmentDto> getAppointmentsByDoctor(UUID doctorId) {
        return appointmentRepository.findByDoctorId(doctorId).stream()
                .map(this::convertToAdminAppointmentDto)
                .collect(Collectors.toList());
    }

    public AdminAppointmentDto updateAppointmentStatus(Long appointmentId, String status) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        AppointmentStatus newStatus = AppointmentStatus.valueOf(status.toUpperCase());
        appointment.setStatus(newStatus);
        appointment.setUpdatedAt(java.time.Instant.now());

        Appointment savedAppointment = appointmentRepository.save(appointment);
        return convertToAdminAppointmentDto(savedAppointment);
    }

    private AdminAppointmentDto convertToAdminAppointmentDto(Appointment appointment) {
        AdminAppointmentDto dto = new AdminAppointmentDto();
        dto.setId(appointment.getId());
        dto.setDateTime(appointment.getDateTime());
        dto.setPatientId(appointment.getPatientId().toString());
        dto.setDoctorId(appointment.getDoctorId().toString());
        dto.setStatus(appointment.getStatus().name());
        dto.setDescription(appointment.getDescription());
        dto.setPaymentTransactionId(appointment.getPaymentTransactionId());
        dto.setCreatedAt(appointment.getCreatedAt());
        dto.setUpdatedAt(appointment.getUpdatedAt());
        return dto;
    }
}