package com.symptomcheck.doctorservice.controllers;

import com.symptomcheck.doctorservice.dto.AvailabilityHealthDto;
import com.symptomcheck.doctorservice.dto.DoctorProfileStatusDTO;
import com.symptomcheck.doctorservice.services.DoctorAvailabilityService;
import com.symptomcheck.doctorservice.services.HealthcareServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/doctor/profile")
@RequiredArgsConstructor
public class DoctorProfileController {


    private final DoctorAvailabilityService availabilityService;
    private final HealthcareServiceService healthcareServiceService;

    @GetMapping("/{doctorId}/profile-status")
    public ResponseEntity<DoctorProfileStatusDTO> getProfileStatus(@PathVariable String doctorId) {

        // Conversion de la chaîne en UUID
        UUID doctorUuid = UUID.fromString(doctorId);

        // Appel des services avec UUID
        boolean availabilityCompleted = availabilityService.existsByDoctorId(doctorUuid);
        boolean healthcareServiceCompleted = healthcareServiceService.existsByDoctorId(String.valueOf(doctorUuid));

        DoctorProfileStatusDTO dto = new DoctorProfileStatusDTO();
        dto.setAvailabilityCompleted(availabilityCompleted);
        dto.setHealthcareServiceCompleted(healthcareServiceCompleted);
        dto.setProfileCompleted(availabilityCompleted && healthcareServiceCompleted);

        return ResponseEntity.ok(dto);
    }

    @PostMapping("/completeprofile")
    public ResponseEntity<?> complete(@RequestBody AvailabilityHealthDto availabilityHealthDto){
        try {
            availabilityService.createAvailabilityHealth(availabilityHealthDto);
            return ResponseEntity.ok().body(true);

        }
        catch(Exception e){
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

}
