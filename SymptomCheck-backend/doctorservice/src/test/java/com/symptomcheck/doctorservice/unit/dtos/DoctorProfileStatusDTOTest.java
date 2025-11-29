package com.symptomcheck.doctorservice.dtos;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DoctorProfileStatusDTOTest {

    private DoctorProfileStatusDTO dto;

    @BeforeEach
    void setUp() {
        dto = new DoctorProfileStatusDTO();
        dto.setAvailabilityCompleted(true);
        dto.setHealthcareServiceCompleted(false);
        dto.setProfileCompleted(true);
    }

    @Test
    void testGetters() {
        assertTrue(dto.isAvailabilityCompleted());
        assertFalse(dto.isHealthcareServiceCompleted());
        assertTrue(dto.isProfileCompleted());
    }

    @Test
    void testSetters() {
        dto.setAvailabilityCompleted(false);
        dto.setHealthcareServiceCompleted(true);
        dto.setProfileCompleted(false);

        assertFalse(dto.isAvailabilityCompleted());
        assertTrue(dto.isHealthcareServiceCompleted());
        assertFalse(dto.isProfileCompleted());
    }
}
