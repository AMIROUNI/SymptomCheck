package com.symptomcheck.doctorservice.dtos.adminDashboardDto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ServiceDtoTest {

    private ServiceDto serviceDto;

    @BeforeEach
    void setUp() {
        serviceDto = new ServiceDto();
        serviceDto.setId(1L);
        serviceDto.setName("General Checkup");
        serviceDto.setDescription("Full body general checkup");
        serviceDto.setCategory("Health");
        serviceDto.setPrice(100.0);
        serviceDto.setDurationMinutes(60);
    }

    @Test
    void testGettersAndSetters() {
        assertEquals(1L, serviceDto.getId());
        assertEquals("General Checkup", serviceDto.getName());
        assertEquals("Full body general checkup", serviceDto.getDescription());
        assertEquals("Health", serviceDto.getCategory());
        assertEquals(100.0, serviceDto.getPrice());
        assertEquals(60, serviceDto.getDurationMinutes());
    }

    @Test
    void testAllArgsConstructor() {
        ServiceDto dto = new ServiceDto();
        dto.setId(2L);
        dto.setName("Dental Check");
        dto.setDescription("Dental hygiene and checkup");
        dto.setCategory("Dental");
        dto.setPrice(80.0);
        dto.setDurationMinutes(45);

        assertEquals(2L, dto.getId());
        assertEquals("Dental Check", dto.getName());
        assertEquals("Dental hygiene and checkup", dto.getDescription());
        assertEquals("Dental", dto.getCategory());
        assertEquals(80.0, dto.getPrice());
        assertEquals(45, dto.getDurationMinutes());
    }

    @Test
    void testToString() {
        String str = serviceDto.toString();
        assertNotNull(str);
        assertTrue(str.contains("General Checkup"));
        assertTrue(str.contains("Health"));
    }

    @Test
    void testEqualsAndHashCode() {
        ServiceDto another = new ServiceDto();
        another.setId(1L);
        another.setName("General Checkup");
        another.setDescription("Full body general checkup");
        another.setCategory("Health");
        another.setPrice(100.0);
        another.setDurationMinutes(60);

        assertEquals(serviceDto, another);
        assertEquals(serviceDto.hashCode(), another.hashCode());
    }
}
