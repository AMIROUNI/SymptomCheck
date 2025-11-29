package com.symptomcheck.doctorservice.unit.dtos.dashboardDto;

import com.symptomcheck.doctorservice.dtos.dashboardDto.DoctorStatsDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DoctorStatsDTOTest {

    private DoctorStatsDTO dto;

    @BeforeEach
    void setUp() {
        dto = new DoctorStatsDTO();
        dto.setTotalServices(5L);
        dto.setTotalAvailabilitySlots(10L);
        dto.setIsProfileComplete(true);
        dto.setCompletionPercentage(80);
    }

    @Test
    void testGetters() {
        assertEquals(5L, dto.getTotalServices());
        assertEquals(10L, dto.getTotalAvailabilitySlots());
        assertTrue(dto.getIsProfileComplete());
        assertEquals(80, dto.getCompletionPercentage());
    }

    @Test
    void testSetters() {
        dto.setTotalServices(8L);
        dto.setTotalAvailabilitySlots(12L);
        dto.setIsProfileComplete(false);
        dto.setCompletionPercentage(50);

        assertEquals(8L, dto.getTotalServices());
        assertEquals(12L, dto.getTotalAvailabilitySlots());
        assertFalse(dto.getIsProfileComplete());
        assertEquals(50, dto.getCompletionPercentage());
    }

    @Test
    void testAllArgsConstructor() {
        DoctorStatsDTO dto2 = new DoctorStatsDTO(3L, 6L, true, 100);

        assertEquals(3L, dto2.getTotalServices());
        assertEquals(6L, dto2.getTotalAvailabilitySlots());
        assertTrue(dto2.getIsProfileComplete());
        assertEquals(100, dto2.getCompletionPercentage());
    }
}
