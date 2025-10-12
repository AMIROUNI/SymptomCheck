package com.symptomcheck.doctorservice.controllers;

import com.symptomcheck.doctorservice.services.DoctorAvailabilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/api/v1/doctor/availability")
@RequiredArgsConstructor
public class DoctorAvailabilityController {
    private final DoctorAvailabilityService doctorAvailabilityService;


    @GetMapping()
    public ResponseEntity<String> getDoctorAvailability() {
        return  ResponseEntity.ok("hello");

    }

}
