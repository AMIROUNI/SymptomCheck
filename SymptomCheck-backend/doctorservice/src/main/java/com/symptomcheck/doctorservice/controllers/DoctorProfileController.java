package com.symptomcheck.doctorservice.controllers;

import com.symptomcheck.doctorservice.dtos.AvailabilityHealthDto;
import com.symptomcheck.doctorservice.services.DoctorAvailabilityService;
import com.symptomcheck.doctorservice.services.HealthcareServiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
@Slf4j
@RestController
@RequestMapping("/api/v1/doctor/profile")
@RequiredArgsConstructor
public class DoctorProfileController {


    private final DoctorAvailabilityService availabilityService;
    private final HealthcareServiceService healthcareServiceService;

    @GetMapping("/{doctorId}/profile-status")
    public ResponseEntity<Boolean> getProfileStatus(@PathVariable String doctorId) {

        // Conversion de la chaîne en UUID
        UUID doctorUuid = UUID.fromString(doctorId);

        // Appel des services avec UUID
        boolean availabilityCompleted = availabilityService.existsByDoctorId(doctorUuid);
        boolean healthcareServiceCompleted = healthcareServiceService.existsByDoctorId(String.valueOf(doctorUuid));

        return ResponseEntity.ok(availabilityCompleted && healthcareServiceCompleted);
    }

    @PostMapping("/completeprofile")
    public ResponseEntity<?> complete(@Valid @RequestBody AvailabilityHealthDto availabilityHealthDto){
        try {
            log.info("Recelived request to compete profile");

                log.info(availabilityHealthDto.getDaysOfWeek().get(0).toString()+"//////////////////");

            availabilityService.createAvailabilityHealth(availabilityHealthDto);
            return ResponseEntity.ok().body(true);

        }
        catch(Exception e){
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

}
