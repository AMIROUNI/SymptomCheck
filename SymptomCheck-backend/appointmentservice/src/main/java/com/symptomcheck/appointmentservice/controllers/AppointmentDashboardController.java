    package com.symptomcheck.appointmentservice.controllers;


    import com.symptomcheck.appointmentservice.dtos.dashboardDto.AppointmentDashboardDTO;
    import com.symptomcheck.appointmentservice.models.Appointment;
    import com.symptomcheck.appointmentservice.services.AppointmentDashboardService;
    import io.swagger.v3.oas.annotations.Operation;
    import io.swagger.v3.oas.annotations.tags.Tag;
    import lombok.RequiredArgsConstructor;
    import lombok.extern.slf4j.Slf4j;
    import org.springframework.http.ResponseEntity;
    import org.springframework.security.access.prepost.PreAuthorize;
    import org.springframework.web.bind.annotation.*;

    import java.util.List;
    import java.util.Map;
    import java.util.UUID;

    @Slf4j
    @RestController
    @RequestMapping("/api/v1/appointments/dashboard")
    @RequiredArgsConstructor
    @Tag(name = "Appointment Dashboard", description = "Appointment dashboard APIs for doctors")
    public class AppointmentDashboardController {

        private final AppointmentDashboardService dashboardService;

        @Operation(summary = "Get appointment dashboard for doctor")
        @GetMapping("/doctor/{doctorId}")
        @PreAuthorize("hasRole('DOCTOR')")
        public ResponseEntity<AppointmentDashboardDTO> getAppointmentDashboard(@PathVariable UUID doctorId) {
            try {
                log.info("Fetching appointment dashboard for doctor: {}", doctorId);
                AppointmentDashboardDTO dashboard = dashboardService.getAppointmentDashboard(doctorId);
                return ResponseEntity.ok(dashboard);
            } catch (Exception e) {
                log.error("Error fetching appointment dashboard for doctor {}: {}", doctorId, e.getMessage());
                return ResponseEntity.internalServerError().build();
            }
        }

        @Operation(summary = "Get today's appointments")
        @GetMapping("/doctor/{doctorId}/today")
        @PreAuthorize("hasRole('DOCTOR')")
        public ResponseEntity<List<Appointment>> getTodayAppointments(@PathVariable UUID doctorId) {
            try {
                List<Appointment> appointments = dashboardService.getTodayAppointments(doctorId);
                return ResponseEntity.ok(appointments);
            } catch (Exception e) {
                log.error("Error fetching today's appointments for doctor {}: {}", doctorId, e.getMessage());
                return ResponseEntity.internalServerError().build();
            }
        }

        @Operation(summary = "Get upcoming appointments")
        @GetMapping("/doctor/{doctorId}/upcoming")
        @PreAuthorize("hasRole('DOCTOR')")
        public ResponseEntity<List<Appointment>> getUpcomingAppointments(
                @PathVariable UUID doctorId,
                @RequestParam(defaultValue = "7") int days) {
            try {
                List<Appointment> appointments = dashboardService.getUpcomingAppointments(doctorId, days);
                return ResponseEntity.ok(appointments);
            } catch (Exception e) {
                log.error("Error fetching upcoming appointments for doctor {}: {}", doctorId, e.getMessage());
                return ResponseEntity.internalServerError().build();
            }
        }

        @Operation(summary = "Get appointment analytics")
        @GetMapping("/doctor/{doctorId}/analytics")
        @PreAuthorize("hasRole('DOCTOR')")
        public ResponseEntity<Map<String, Object>> getAppointmentAnalytics(@PathVariable UUID doctorId) {
            try {
                Map<String, Object> analytics = dashboardService.getAppointmentAnalytics(doctorId);
                return ResponseEntity.ok(analytics);
            } catch (Exception e) {
                log.error("Error fetching appointment analytics for doctor {}: {}", doctorId, e.getMessage());
                return ResponseEntity.internalServerError().build();
            }
        }
    }