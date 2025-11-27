package com.symptomcheck.doctorservice.dtos.dashboardDto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DoctorDashboardDTOTest {

    private DoctorDashboardDTO dashboardDTO;

    @BeforeEach
    void setUp() {
        // Stats
        DoctorStatsDTO stats = new DoctorStatsDTO(5L, 10L, true, 80);

        // Services
        DoctorServiceDTO service1 = new DoctorServiceDTO(1L, "General Checkup", "General", 50.0, 30, "Basic health checkup");
        DoctorServiceDTO service2 = new DoctorServiceDTO(2L, "Dental Cleaning", "Dental", 75.0, 45, "Teeth cleaning session");
        List<DoctorServiceDTO> services = List.of(service1, service2);

        // Availability
        DoctorAvailabilityDTO availability1 = new DoctorAvailabilityDTO();
        availability1.setStartTime("08:00");
        availability1.setEndTime("12:00");

        DoctorAvailabilityDTO availability2 = new DoctorAvailabilityDTO();
        availability2.setStartTime("14:00");
        availability2.setEndTime("18:00");

        List<DoctorAvailabilityDTO> availability = List.of(availability1, availability2);

        // Profile completion
        ProfileCompletionDTO profileCompletion = new ProfileCompletionDTO(true, true, true, 80);

        // Assemble dashboard
        dashboardDTO = new DoctorDashboardDTO(stats, services, availability, profileCompletion);
    }

    @Test
    void testDashboardDTOCreation() {
        assertNotNull(dashboardDTO);

        // Stats
        assertEquals(5L, dashboardDTO.getStats().getTotalServices());
        assertTrue(dashboardDTO.getStats().getIsProfileComplete());

        // Services
        assertEquals(2, dashboardDTO.getServices().size());
        assertEquals("Dental Cleaning", dashboardDTO.getServices().get(1).getName());

        // Availability
        assertEquals(2, dashboardDTO.getAvailability().size());


        // Profile completion
        assertEquals(80, dashboardDTO.getProfileCompletion().getCompletionPercentage());
        assertTrue(dashboardDTO.getProfileCompletion().getHasServices());
    }
}
