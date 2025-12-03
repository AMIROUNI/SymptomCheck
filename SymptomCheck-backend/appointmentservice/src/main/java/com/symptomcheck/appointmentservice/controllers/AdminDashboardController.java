package com.symptomcheck.appointmentservice.controllers;

import com.symptomcheck.appointmentservice.dtos.adminDashboardDto.AdminAppointmentDto;
import com.symptomcheck.appointmentservice.dtos.adminDashboardDto.AppointmentStatsDto;
import com.symptomcheck.appointmentservice.services.AdminDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    @GetMapping("/dashboard/stats")
    public ResponseEntity<AppointmentStatsDto> getDashboardStats() {
        return ResponseEntity.ok(adminDashboardService.getAppointmentStatistics());
    }

    @GetMapping("/appointments")
    public ResponseEntity<List<AdminAppointmentDto>> getAllAppointments() {
        return ResponseEntity.ok(adminDashboardService.getAllAppointments());
    }

    @GetMapping("/appointments/status/{status}")

    public ResponseEntity<List<AdminAppointmentDto>> getAppointmentsByStatus(@PathVariable String status) {
        return ResponseEntity.ok(adminDashboardService.getAppointmentsByStatus(status));
    }

    @GetMapping("/appointments/date-range")
    public ResponseEntity<List<AdminAppointmentDto>> getAppointmentsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(adminDashboardService.getAppointmentsByDateRange(start, end));
    }

    @GetMapping("/appointments/doctor/{doctorId}")
    public ResponseEntity<List<AdminAppointmentDto>> getAppointmentsByDoctor(@PathVariable UUID doctorId) {
        return ResponseEntity.ok(adminDashboardService.getAppointmentsByDoctor(doctorId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/appointments/{appointmentId}/status")
    public ResponseEntity<AdminAppointmentDto> updateAppointmentStatus(
            @PathVariable Long appointmentId,
            @RequestParam String status) {
        return ResponseEntity.ok(adminDashboardService.updateAppointmentStatus(appointmentId, status));
    }
}