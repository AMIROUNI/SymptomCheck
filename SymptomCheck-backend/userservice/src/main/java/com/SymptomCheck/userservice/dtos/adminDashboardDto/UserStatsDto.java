package com.SymptomCheck.userservice.dtos.adminDashboardDto;

import lombok.Data;
import java.time.LocalDateTime;
@Data
public class UserStatsDto {
    private Long totalUsers;
    private Long totalDoctors;
    private Long totalPatients;
    private Long completedProfiles;
    private Long incompleteProfiles;
    private Long newUsersThisWeek;
    private LocalDateTime lastUpdated;
}
