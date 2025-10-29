package com.symptomcheck.doctorservice.controllers;

import com.symptomcheck.doctorservice.services.DoctorAvailabilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/doctor/availability")
@RequiredArgsConstructor
public class DoctorAvailabilityController {

    private final DoctorAvailabilityService doctorAvailabilityService;

    // Endpoint simple de test
    @GetMapping
    public ResponseEntity<String> getDoctorAvailability() {
        return ResponseEntity.ok("Doctor Availability Service is running ");
    }

    // Endpoint réel pour vérifier la disponibilité
    @GetMapping("/isAvailable/{id}/{dateTime}")
    public ResponseEntity<?> isAvailable(
            @PathVariable("id") Long id,
            @PathVariable("dateTime")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTime) {

        try {
            boolean available = doctorAvailabilityService.isDoctorAvailable(id, dateTime);
            return ResponseEntity.ok(available);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}
