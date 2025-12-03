package com.symptomcheck.doctorservice.unit.dtos.adminDashboardDto;

import com.symptomcheck.doctorservice.dtos.admindashboarddto.AvailabilityDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AvailabilityDtoTest {

    private AvailabilityDto availabilityDto;

    @BeforeEach
    void setUp() {
        availabilityDto = new AvailabilityDto();
        availabilityDto.setId(1L);
        availabilityDto.setDayOfWeek("MONDAY");
        availabilityDto.setStartTime("09:00");
        availabilityDto.setEndTime("17:00");
    }

    @Test
    void testGettersAndSetters() {
        assertEquals(1L, availabilityDto.getId());
        assertEquals("MONDAY", availabilityDto.getDayOfWeek());
        assertEquals("09:00", availabilityDto.getStartTime());
        assertEquals("17:00", availabilityDto.getEndTime());
    }

    @Test
    void testToString() {
        String str = availabilityDto.toString();
        assertNotNull(str);
        assertTrue(str.contains("dayOfWeek=MONDAY"));
        assertTrue(str.contains("startTime=09:00"));
        assertTrue(str.contains("endTime=17:00"));
    }

    @Test
    void testEqualsAndHashCode() {
        AvailabilityDto another = new AvailabilityDto();
        another.setId(1L);
        another.setDayOfWeek("MONDAY");
        another.setStartTime("09:00");
        another.setEndTime("17:00");

        assertEquals(availabilityDto, another);
        assertEquals(availabilityDto.hashCode(), another.hashCode());
    }
}
