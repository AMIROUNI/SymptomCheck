package com.SymptomCheck.userservice.controllers;

import com.SymptomCheck.userservice.dtos.adminDashboardDto.AdminUserDto;
import com.SymptomCheck.userservice.dtos.adminDashboardDto.UserStatsDto;
import com.SymptomCheck.userservice.services.AdminDashboardService;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<UserStatsDto> getDashboardStats() {
        return ResponseEntity.ok(adminDashboardService.getUserStatistics());
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")

    public ResponseEntity<List<AdminUserDto>> getAllUsers() {
        return ResponseEntity.ok(adminDashboardService.getAllUsers());
    }

    @GetMapping("/users/role/{role}")
    @PreAuthorize("hasRole('ADMIN')")

    public ResponseEntity<List<AdminUserDto>> getUsersByRole(@PathVariable String role) {
        return ResponseEntity.ok(adminDashboardService.getUsersByRole(role));
    }

    @PutMapping("/users/{userId}/status")
    @PreAuthorize("hasRole('ADMIN')")

    public ResponseEntity<AdminUserDto> updateUserProfileStatus(
            @PathVariable String userId,
            @RequestParam boolean profileComplete) {
        return ResponseEntity.ok(adminDashboardService.updateUserProfileStatus(userId, profileComplete));
    }
}