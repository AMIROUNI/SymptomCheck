package com.symptomcheck.doctorservice.controllers;


import com.symptomcheck.doctorservice.dtos.adminDashboardDto.AdminDoctorDto;
import com.symptomcheck.doctorservice.dtos.adminDashboardDto.DoctorStatsDto;
import com.symptomcheck.doctorservice.services.AdminDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    @GetMapping("/dashboard/stats")
    public ResponseEntity<DoctorStatsDto> getDashboardStats() {
        return ResponseEntity.ok(adminDashboardService.getDoctorStatistics());
    }

    @GetMapping("/doctors")
    public ResponseEntity<List<AdminDoctorDto>> getAllDoctors() {
        return ResponseEntity.ok(adminDashboardService.getAllDoctors());
    }

    @GetMapping("/doctors/speciality/{speciality}")
    public ResponseEntity<List<AdminDoctorDto>> getDoctorsBySpeciality(@PathVariable String speciality) {
        return ResponseEntity.ok(adminDashboardService.getDoctorsBySpeciality(speciality));
    }

    @PutMapping("/doctors/{doctorId}/status")
    public ResponseEntity<AdminDoctorDto> updateDoctorStatus(
            @PathVariable UUID doctorId,
            @RequestParam String status) {
        return ResponseEntity.ok(adminDashboardService.updateDoctorStatus(doctorId, status));
    }
}