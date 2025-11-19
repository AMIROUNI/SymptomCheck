package com.symptomcheck.doctorservice.controllers;

import com.symptomcheck.doctorservice.dto.dashboardDto.DoctorDashboardDTO;
import com.symptomcheck.doctorservice.services.DoctorDashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/doctor/dashboard")
@RequiredArgsConstructor
@Tag(name = "Doctor Dashboard", description = "Dashboard APIs for doctors - Doctor Service")
public class DoctorDashboardController {

    private final DoctorDashboardService dashboardService;

    @Operation(summary = "Get complete doctor dashboard")
    @GetMapping("/{doctorId}")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<DoctorDashboardDTO> getDoctorDashboard(@PathVariable UUID doctorId) {
        try {
            log.info("Fetching dashboard for doctor: {}", doctorId);
            DoctorDashboardDTO dashboard = dashboardService.getDoctorDashboard(doctorId);
            return ResponseEntity.ok(dashboard);
        } catch (Exception e) {
            log.error("Error fetching dashboard for doctor {}: {}", doctorId, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "Get doctor service categories")
    @GetMapping("/{doctorId}/service-categories")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<List<String>> getServiceCategories(@PathVariable UUID doctorId) {
        try {
            List<String> categories = dashboardService.getServiceCategories(doctorId);
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            log.error("Error fetching service categories for doctor {}: {}", doctorId, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "Check if doctor profile is complete")
    @GetMapping("/{doctorId}/profile-status")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<Boolean> isProfileComplete(@PathVariable UUID doctorId) {
        try {
            Boolean isComplete = dashboardService.isProfileComplete(doctorId);
            return ResponseEntity.ok(isComplete);
        } catch (Exception e) {
            log.error("Error checking profile status for doctor {}: {}", doctorId, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "Get total services count")
    @GetMapping("/{doctorId}/services-count")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<Long> getServicesCount(@PathVariable UUID doctorId) {
        try {
            Long count = dashboardService.getTotalServicesCount(doctorId);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            log.error("Error fetching services count for doctor {}: {}", doctorId, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "Get total availability slots")
    @GetMapping("/{doctorId}/availability-slots")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<Long> getAvailabilitySlots(@PathVariable UUID doctorId) {
        try {
            Long slots = dashboardService.getTotalAvailabilitySlots(doctorId);
            return ResponseEntity.ok(slots);
        } catch (Exception e) {
            log.error("Error fetching availability slots for doctor {}: {}", doctorId, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}