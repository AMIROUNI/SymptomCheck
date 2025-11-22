package com.symptomcheck.doctorservice.controllers;

import com.symptomcheck.doctorservice.dtos.HealthcareServiceDto;
import com.symptomcheck.doctorservice.services.HealthcareServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController()
@RequestMapping("/api/v1/doctor/healthcare/service")
@RequiredArgsConstructor

public class HealthcareServiceController {
    private final HealthcareServiceService healthcareService;


    @GetMapping("")
    public ResponseEntity<?> getHealthcareService() {
        try {

            return  ResponseEntity.ok().body(healthcareService.getAll());
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }


    @GetMapping("doctor/{doctorId}")
    public ResponseEntity<?> getDoctorHealthcareService(@PathVariable UUID doctorId) {
        try{
             return  ResponseEntity.ok().body(healthcareService.getHealthcareServiceByDoctorId(doctorId));
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }


    @PostMapping()
        public ResponseEntity<?> saveHealthcareService(@RequestPart("dto") HealthcareServiceDto dto,
                                                       @RequestPart("file") MultipartFile image) throws IOException {
try {

    return  ResponseEntity.ok(healthcareService.createHealthcareService(dto, image));
}
catch ( Exception e) {
    return ResponseEntity.internalServerError().build();
}


    }
}
