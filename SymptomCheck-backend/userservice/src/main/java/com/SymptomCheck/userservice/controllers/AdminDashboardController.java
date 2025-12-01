package com.SymptomCheck.userservice.controllers;

import com.SymptomCheck.userservice.dtos.adminDashboardDto.AdminUserDto;
import com.SymptomCheck.userservice.dtos.adminDashboardDto.UserStatsDto;
import com.SymptomCheck.userservice.exceptions.UserNotFountException;
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
        try {
            return ResponseEntity.ok(adminDashboardService.getAllUsers());
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/users/role/{role}")
    @PreAuthorize("hasRole('ADMIN')")

    public ResponseEntity<List<AdminUserDto>> getUsersByRole(@PathVariable String role) {
        try {
            return ResponseEntity.ok(adminDashboardService.getUsersByRole(role));
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }

    }

    @PutMapping("/users/{userId}/status")
    @PreAuthorize("hasRole('ADMIN')")

    public ResponseEntity<AdminUserDto> updateUserProfileStatus(
            @PathVariable String userId,
            @RequestParam boolean profileComplete) {
        try {
            return ResponseEntity.ok(adminDashboardService.updateUserProfileStatus(userId, profileComplete));


        }
        catch (UserNotFountException e) {
            return ResponseEntity.notFound().build();
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}