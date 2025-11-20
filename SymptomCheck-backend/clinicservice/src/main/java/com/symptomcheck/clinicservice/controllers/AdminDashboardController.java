package com.symptomcheck.clinicservice.controllers;

import com.symptomcheck.clinicservice.dtos.adminDashboardDto.AdminClinicDto;
import com.symptomcheck.clinicservice.dtos.adminDashboardDto.ClinicStatsDto;
import com.symptomcheck.clinicservice.services.AdminDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    @GetMapping("/dashboard/stats")
    public ResponseEntity<ClinicStatsDto> getDashboardStats() {
        return ResponseEntity.ok(adminDashboardService.getClinicStatistics());
    }

    @GetMapping("/clinics")
    public ResponseEntity<List<AdminClinicDto>> getAllClinics() {
        return ResponseEntity.ok(adminDashboardService.getAllClinics());
    }

    @GetMapping("/clinics/city/{city}")
    public ResponseEntity<List<AdminClinicDto>> getClinicsByCity(@PathVariable String city) {
        return ResponseEntity.ok(adminDashboardService.getClinicsByCity(city));
    }

    @PostMapping("/clinics")
    public ResponseEntity<AdminClinicDto> createClinic(@RequestBody AdminClinicDto clinicDto) {
        return ResponseEntity.ok(adminDashboardService.createClinic(clinicDto));
    }

    @PutMapping("/clinics/{clinicId}")
    public ResponseEntity<AdminClinicDto> updateClinic(
            @PathVariable Long clinicId,
            @RequestBody AdminClinicDto clinicDto) {
        return ResponseEntity.ok(adminDashboardService.updateClinic(clinicId, clinicDto));
    }

    @DeleteMapping("/clinics/{clinicId}")
    public ResponseEntity<Void> deleteClinic(@PathVariable Long clinicId) {
        adminDashboardService.deleteClinic(clinicId);
        return ResponseEntity.ok().build();
    }
}