package com.symptomcheck.doctorservice.unit.dtos.dashboardDto;

import com.symptomcheck.doctorservice.dtos.dashboardDto.DoctorAvailabilityDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DoctorAvailabilityDTOTest {

    private DoctorAvailabilityDTO availabilityDTO;

    @BeforeEach
    void setUp() {
        availabilityDTO = new DoctorAvailabilityDTO();
        availabilityDTO.setId("1");
        availabilityDTO.setDayOfWeek("MONDAY");
        availabilityDTO.setStartTime("08:00");
        availabilityDTO.setEndTime("12:00");
        availabilityDTO.setIsActive(true);
    }

    @Test
    void testDoctorAvailabilityDTOGettersAndSetters() {
        assertEquals("1", availabilityDTO.getId());
        assertEquals("MONDAY", availabilityDTO.getDayOfWeek());
        assertEquals("08:00", availabilityDTO.getStartTime());
        assertEquals("12:00", availabilityDTO.getEndTime());
        assertTrue(availabilityDTO.getIsActive());
    }

    @Test
    void testDoctorAvailabilityDTOAllArgsConstructor() {
        DoctorAvailabilityDTO dto = new DoctorAvailabilityDTO("2", "FRIDAY", "14:00", "18:00", false);

        assertEquals("2", dto.getId());
        assertEquals("FRIDAY", dto.getDayOfWeek());
        assertEquals("14:00", dto.getStartTime());
        assertEquals("18:00", dto.getEndTime());
        assertFalse(dto.getIsActive());
    }

    @Test
    void testDoctorAvailabilityDTOToString() {
        String str = availabilityDTO.toString();
        assertNotNull(str);
        assertTrue(str.contains("MONDAY"));
        assertTrue(str.contains("08:00"));
    }

    @Test
    void testDoctorAvailabilityDTOEqualsAndHashCode() {
        DoctorAvailabilityDTO another = new DoctorAvailabilityDTO("1", "MONDAY", "08:00", "12:00", true);
        assertEquals(availabilityDTO, another);
        assertEquals(availabilityDTO.hashCode(), another.hashCode());
    }
}
