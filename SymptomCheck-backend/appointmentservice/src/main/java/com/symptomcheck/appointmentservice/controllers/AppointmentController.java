package com.symptomcheck.appointmentservice.controllers;

import com.symptomcheck.appointmentservice.dtos.AppointmentDto;
import com.symptomcheck.appointmentservice.models.Appointment;
import com.symptomcheck.appointmentservice.repositories.AppointmentRepository;
import com.symptomcheck.appointmentservice.services.AppointmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/appointments")
@RequiredArgsConstructor
public class AppointmentController {
    private final AppointmentService appointmentService;


    @PostMapping("/create")
    public ResponseEntity<?> createAppointment(@RequestBody AppointmentDto appointment,
                                               @AuthenticationPrincipal Jwt jwt) {
        try {
            log.info("**************************************************************");
            log.info("*                             createAppointment                                            *");
            log.info("**************************************************************");
            String token = jwt.getTokenValue();
            Appointment savedAppointment = appointmentService.makeAppointment(appointment, token);
            return ResponseEntity.ok(savedAppointment);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(500).body("Internal server error: " + ex.getMessage());
        }
    }

    // version locale
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<Appointment>> getByDoctor(@PathVariable UUID doctorId) {
        try {
            log.info("**************************************************************");
            log.info(doctorId.toString());
            List<Appointment> appointments = appointmentService.getByDoctor(doctorId);
            return ResponseEntity.ok(appointments);
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body(List.of());
        }

    }

    // version WebClient (appelle un autre microservice)





    @GetMapping("/taken-appointments/{doctorId}")
    public List<String> getTakenAppointments(
            @PathVariable UUID doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return appointmentService.getTakenAppointments(doctorId, date);
    }

    @GetMapping("{userId}")
    public ResponseEntity<?> getByPatient(
            @PathVariable UUID userId
    ) {
        try {
            return ResponseEntity.ok().body(appointmentService.getByPatientId(userId));
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body(List.of());
        }
    }

    @PutMapping("/{id}/status/{statusNumber}")
    public ResponseEntity<Boolean> updateStatus(
            @PathVariable Long id,
            @PathVariable int statusNumber) {

        try {
            appointmentService.updateAppointmentStatus(id, statusNumber);
            return ResponseEntity.ok(true);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(false);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(false);
        }
    }



}
