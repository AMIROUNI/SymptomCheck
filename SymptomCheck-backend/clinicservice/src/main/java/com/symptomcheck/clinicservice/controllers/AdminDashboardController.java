package com.symptomcheck.clinicservice.controllers;

import com.symptomcheck.clinicservice.dtos.admindashboarddto.AdminClinicDto;
import com.symptomcheck.clinicservice.dtos.admindashboarddto.ClinicStatsDto;
import com.symptomcheck.clinicservice.services.AdminDashboardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    @GetMapping("/dashboard/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClinicStatsDto> getDashboardStats() {
        try {
            return ResponseEntity.ok(adminDashboardService.getClinicStatistics());
        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/clinics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AdminClinicDto>> getAllClinics() {
        return ResponseEntity.ok(adminDashboardService.getAllClinics());
    }

    @GetMapping("/clinics/city/{city}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AdminClinicDto>> getClinicsByCity(@PathVariable String city) {
        return ResponseEntity.ok(adminDashboardService.getClinicsByCity(city));
    }

    @PostMapping("/clinics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminClinicDto> createClinic( @Valid  @RequestBody AdminClinicDto clinicDto) {
        try {
                if(clinicDto.getName().isBlank())
                {
                    return ResponseEntity.badRequest().build();
                }
            return ResponseEntity.ok(adminDashboardService.createClinic(clinicDto));
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/clinics/{clinicId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminClinicDto> updateClinic(
            @PathVariable Long clinicId,
            @RequestBody AdminClinicDto clinicDto) {
        try {
            return ResponseEntity.ok(adminDashboardService.updateClinic(clinicId, clinicDto));
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/clinics/{clinicId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteClinic(@PathVariable Long clinicId) {
        adminDashboardService.deleteClinic(clinicId);
        return ResponseEntity.ok().build();
    }
}