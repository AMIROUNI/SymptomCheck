package com.symptomcheck.appointmentservice.controllers;

import com.symptomcheck.appointmentservice.models.Appointment;
import com.symptomcheck.appointmentservice.repositories.AppointmentRepository;
import com.symptomcheck.appointmentservice.services.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/appointments")
@RequiredArgsConstructor
public class AppointmentController {
    private final AppointmentService appointmentService;


    @PostMapping("/create")
    public ResponseEntity<?> createAppointment(@RequestBody Appointment appointment) {
        try {
            Appointment savedAppointment = appointmentService.makeAppointment(appointment);
            return ResponseEntity.ok(savedAppointment);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(500).body("Internal server error: " + ex.getMessage());
        }
    }

    // version locale
    @GetMapping("/doctor/{doctorId}")
    @PreAuthorize("hasRole('Doctor')")
    public ResponseEntity<List<Appointment>> getByDoctor(@PathVariable String doctorId) {
        List<Appointment> appointments = appointmentService.getByDoctor(doctorId);
        return ResponseEntity.ok(appointments);
    }

    // version WebClient (appelle un autre microservice)
    @GetMapping("/doctor/{doctorId}/remote")
    @PreAuthorize("hasRole('Doctor')")
    public ResponseEntity<List<Appointment>> getByDoctorRemote(
            @PathVariable int doctorId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        String token = jwt.getTokenValue();
        List<Appointment> appointments = appointmentService.getByDoctorFromDoctorService(doctorId, token);
        return ResponseEntity.ok(appointments);
    }
}
