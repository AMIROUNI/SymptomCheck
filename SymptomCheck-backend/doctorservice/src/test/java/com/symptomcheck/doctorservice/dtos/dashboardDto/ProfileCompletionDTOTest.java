package com.symptomcheck.doctorservice.dtos.dashboardDto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProfileCompletionDTOTest {

    private ProfileCompletionDTO dto;

    @BeforeEach
    void setUp() {
        dto = new ProfileCompletionDTO();
        dto.setHasAvailability(true);
        dto.setHasServices(false);
        dto.setHasBasicInfo(true);
        dto.setCompletionPercentage(75);
    }

    @Test
    void testGetters() {
        assertTrue(dto.getHasAvailability());
        assertFalse(dto.getHasServices());
        assertTrue(dto.getHasBasicInfo());
        assertEquals(75, dto.getCompletionPercentage());
    }

    @Test
    void testSetters() {
        dto.setHasAvailability(false);
        dto.setHasServices(true);
        dto.setHasBasicInfo(false);
        dto.setCompletionPercentage(50);

        assertFalse(dto.getHasAvailability());
        assertTrue(dto.getHasServices());
        assertFalse(dto.getHasBasicInfo());
        assertEquals(50, dto.getCompletionPercentage());
    }

    @Test
    void testAllArgsConstructor() {
        ProfileCompletionDTO dto2 = new ProfileCompletionDTO(true, true, false, 100);

        assertTrue(dto2.getHasAvailability());
        assertTrue(dto2.getHasServices());
        assertFalse(dto2.getHasBasicInfo());
        assertEquals(100, dto2.getCompletionPercentage());
    }
}
