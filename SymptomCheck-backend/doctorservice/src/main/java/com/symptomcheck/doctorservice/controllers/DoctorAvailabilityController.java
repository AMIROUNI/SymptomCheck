package com.symptomcheck.doctorservice.controllers;

import com.symptomcheck.doctorservice.models.DoctorAvailability;
import com.symptomcheck.doctorservice.services.DoctorAvailabilityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/doctor/availability")
@RequiredArgsConstructor
public class DoctorAvailabilityController {

    private final DoctorAvailabilityService doctorAvailabilityService;

    // Endpoint simple de test
    @GetMapping("/{doctorId}")
    public ResponseEntity<List<DoctorAvailability>> getDoctorAvailabilityByDoctorId(
            @PathVariable UUID doctorId
    ) {
        List<DoctorAvailability> availabilities =
                doctorAvailabilityService.getAvailabilityByDoctorId(doctorId);

        return ResponseEntity.ok(availabilities);
    }

    // Endpoint réel pour vérifier la disponibilité
    @GetMapping("/isAvailable/{id}/{dateTime}")
    public ResponseEntity<?> isAvailable(
            @PathVariable("id") UUID id,
            @PathVariable("dateTime")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTime) {

        try {
            log.info("isAvailable id: {}, dateTime: {}", id, dateTime);
            boolean available = doctorAvailabilityService.isDoctorAvailable(id, dateTime);

            log.info("isAvailable : {},",available );
            return ResponseEntity.ok(available);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }



    @GetMapping("/daily")
    public ResponseEntity<List<String>> getDailyAvailability(
            @RequestParam UUID doctorId,
            @RequestParam LocalDate date) {

        List<String> slots = doctorAvailabilityService.getAvailableSlotsForDate(doctorId, date);
        return ResponseEntity.ok(slots);
    }

}
