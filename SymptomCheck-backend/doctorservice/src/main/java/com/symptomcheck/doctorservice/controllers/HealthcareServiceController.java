package com.symptomcheck.doctorservice.controllers;

import com.symptomcheck.doctorservice.models.HealthcareService;
import com.symptomcheck.doctorservice.services.HealthcareServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
