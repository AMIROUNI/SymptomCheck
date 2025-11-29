package com.symptomcheck.doctorservice.unit.models;

import com.symptomcheck.doctorservice.models.HealthcareService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class HealthcareServiceTest {

    private HealthcareService service;

    @BeforeEach
    void setUp() {
        service = new HealthcareService();
        service.setId(1L);
        service.setDoctorId(UUID.randomUUID());
        service.setName("General Checkup");
        service.setDescription("A thorough health checkup for adults.");
        service.setCategory("General");
        service.setImageUrl("https://example.com/image.png");
        service.setDurationMinutes(60);
        service.setPrice(99.99);
    }

    @Test
    void testGetters() {
        assertEquals(1L, service.getId());
        assertNotNull(service.getDoctorId());
        assertEquals("General Checkup", service.getName());
        assertEquals("A thorough health checkup for adults.", service.getDescription());
        assertEquals("General", service.getCategory());
        assertEquals("https://example.com/image.png", service.getImageUrl());
        assertEquals(60, service.getDurationMinutes());
        assertEquals(99.99, service.getPrice());
    }

    @Test
    void testSetters() {
        UUID newDoctorId = UUID.randomUUID();
        service.setId(2L);
        service.setDoctorId(newDoctorId);
        service.setName("Specialist Consultation");
        service.setDescription("Consultation with a specialist.");
        service.setCategory("Specialist");
        service.setImageUrl("https://example.com/specialist.png");
        service.setDurationMinutes(45);
        service.setPrice(149.99);

        assertEquals(2L, service.getId());
        assertEquals(newDoctorId, service.getDoctorId());
        assertEquals("Specialist Consultation", service.getName());
        assertEquals("Consultation with a specialist.", service.getDescription());
        assertEquals("Specialist", service.getCategory());
        assertEquals("https://example.com/specialist.png", service.getImageUrl());
        assertEquals(45, service.getDurationMinutes());
        assertEquals(149.99, service.getPrice());
    }
}
