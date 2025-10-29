package com.symptomcheck.appointmentservice.controllers;

import com.symptomcheck.appointmentservice.models.Appointment;
import com.symptomcheck.appointmentservice.repositories.AppointmentRepository;
import com.symptomcheck.appointmentservice.services.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}