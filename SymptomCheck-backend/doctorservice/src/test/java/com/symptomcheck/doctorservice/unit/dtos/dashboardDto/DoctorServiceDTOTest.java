package com.symptomcheck.doctorservice.unit.dtos.dashboardDto;

import com.symptomcheck.doctorservice.dtos.dashboardDto.DoctorServiceDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DoctorServiceDTOTest {

    private DoctorServiceDTO dto;

    @BeforeEach
    void setUp() {
        dto = new DoctorServiceDTO();
        dto.setId(1L);
        dto.setName("General Consultation");
        dto.setCategory("Consultation");
        dto.setPrice(50.0);
        dto.setDuration(30);
        dto.setDescription("Basic checkup for general health");
    }

    @Test
    void testGetters() {
        assertEquals(1L, dto.getId());
        assertEquals("General Consultation", dto.getName());
        assertEquals("Consultation", dto.getCategory());
        assertEquals(50.0, dto.getPrice());
        assertEquals(30, dto.getDuration());
        assertEquals("Basic checkup for general health", dto.getDescription());
    }

    @Test
    void testSetters() {
        dto.setId(2L);
        dto.setName("Specialist Consultation");
        dto.setCategory("Specialist");
        dto.setPrice(100.0);
        dto.setDuration(60);
        dto.setDescription("Detailed specialist checkup");

        assertEquals(2L, dto.getId());
        assertEquals("Specialist Consultation", dto.getName());
        assertEquals("Specialist", dto.getCategory());
        assertEquals(100.0, dto.getPrice());
        assertEquals(60, dto.getDuration());
        assertEquals("Detailed specialist checkup", dto.getDescription());
    }

    @Test
    void testAllArgsConstructor() {
        DoctorServiceDTO dto2 = new DoctorServiceDTO(
                3L, "Therapy Session", "Therapy", 75.0, 45, "Session for mental health"
        );

        assertEquals(3L, dto2.getId());
        assertEquals("Therapy Session", dto2.getName());
        assertEquals("Therapy", dto2.getCategory());
        assertEquals(75.0, dto2.getPrice());
        assertEquals(45, dto2.getDuration());
        assertEquals("Session for mental health", dto2.getDescription());
    }
}
