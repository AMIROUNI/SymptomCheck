package com.symptomcheck.doctorservice.dtos;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class HealthcareServiceDtoTest {

    private HealthcareServiceDto dto;

    @BeforeEach
    void setUp() {
        dto = new HealthcareServiceDto();
        dto.setDoctorId(UUID.randomUUID());
        dto.setName("General Checkup");
        dto.setDescription("A thorough health checkup for adults.");
        dto.setCategory("General");
        dto.setDurationMinutes(60);
        dto.setPrice(99.99);
    }

    @Test
    void testGetters() {
        assertNotNull(dto.getDoctorId());
        assertEquals("General Checkup", dto.getName());
        assertEquals("A thorough health checkup for adults.", dto.getDescription());
        assertEquals("General", dto.getCategory());
        assertEquals(60, dto.getDurationMinutes());
        assertEquals(99.99, dto.getPrice());
    }

    @Test
    void testSetters() {
        UUID newDoctorId = UUID.randomUUID();
        dto.setDoctorId(newDoctorId);
        dto.setName("Specialist Consultation");
        dto.setDescription("Consultation with a specialist.");
        dto.setCategory("Specialist");
        dto.setDurationMinutes(45);
        dto.setPrice(149.99);

        assertEquals(newDoctorId, dto.getDoctorId());
        assertEquals("Specialist Consultation", dto.getName());
        assertEquals("Consultation with a specialist.", dto.getDescription());
        assertEquals("Specialist", dto.getCategory());
        assertEquals(45, dto.getDurationMinutes());
        assertEquals(149.99, dto.getPrice());
    }
}
