package com.symptomcheck.doctorservice.controllers;

import com.symptomcheck.doctorservice.services.DoctorProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/api/v1/doctor/profile")
@RequiredArgsConstructor
public class DoctorProfileController {
    private  final DoctorProfileService doctorProfileService;

}
