package com.SymptomCheck.userservice.services;

import com.SymptomCheck.userservice.dtos.adminDashboardDto.AdminUserDto;
import com.SymptomCheck.userservice.dtos.adminDashboardDto.UserStatsDto;
import com.SymptomCheck.userservice.models.UserData;
import com.SymptomCheck.userservice.repositories.UserDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminDashboardService {

    private final UserDataRepository userDataRepository;

    public UserStatsDto getUserStatistics() {
        UserStatsDto stats = new UserStatsDto();

        List<UserData> allUsers = userDataRepository.findAll();
        List<UserData> doctors = userDataRepository.findByClinicIdIsNotNull();
        List<UserData> patients = userDataRepository.findByClinicIdIsNullAndSpecialityIsNull();
        List<UserData> completedProfiles = userDataRepository.findByProfileCompleteTrue();

        // Calculate new users this week
        Instant oneWeekAgo = Instant.now().minus(7, ChronoUnit.DAYS);
        Long newUsersThisWeek = userDataRepository.countByCreatedAtAfter(oneWeekAgo);

        stats.setTotalUsers((long) allUsers.size());
        stats.setTotalDoctors((long) doctors.size());
        stats.setTotalPatients((long) patients.size());
        stats.setCompletedProfiles((long) completedProfiles.size());
        stats.setIncompleteProfiles((long) allUsers.size() - completedProfiles.size());
        stats.setNewUsersThisWeek(newUsersThisWeek);
        stats.setLastUpdated(LocalDateTime.now());

        return stats;
    }

    public List<AdminUserDto> getAllUsers() {
        return userDataRepository.findAll().stream()
                .map(this::convertToAdminUserDto)
                .collect(Collectors.toList());
    }

    public List<AdminUserDto> getUsersByRole(String role) {
        List<UserData> users;

        switch (role.toUpperCase()) {
            case "DOCTOR":
                users = userDataRepository.findByClinicIdIsNotNull();
                break;
            case "PATIENT":
                users = userDataRepository.findByClinicIdIsNullAndSpecialityIsNull();
                break;
            default:
                users = userDataRepository.findAll();
        }

        return users.stream()
                .map(this::convertToAdminUserDto)
                .collect(Collectors.toList());
    }

    public AdminUserDto updateUserProfileStatus(String userId, boolean profileComplete) {
        UserData user = userDataRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setProfileComplete(profileComplete);
        user.setUpdatedAt(Instant.now());
        UserData savedUser = userDataRepository.save(user);

        return convertToAdminUserDto(savedUser);
    }

    private AdminUserDto convertToAdminUserDto(UserData user) {
        AdminUserDto dto = new AdminUserDto();
        dto.setId(user.getId());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setProfilePhotoUrl(user.getProfilePhotoUrl());
        dto.setProfileComplete(user.getProfileComplete() != null ? user.getProfileComplete() : false);
        dto.setClinicId(user.getClinicId());
        dto.setSpeciality(user.getSpeciality());
        dto.setDescription(user.getDescription());
        dto.setDiploma(user.getDiploma());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());

        // Determine role
        if (user.getClinicId() != null) {
            dto.setRole("DOCTOR");
        } else {
            dto.setRole("PATIENT");
        }

        return dto;
    }
}