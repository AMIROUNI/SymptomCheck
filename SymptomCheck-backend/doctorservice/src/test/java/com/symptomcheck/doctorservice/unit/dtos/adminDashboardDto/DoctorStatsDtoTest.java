package com.symptomcheck.doctorservice.unit.dtos.adminDashboardDto;

import com.symptomcheck.doctorservice.dtos.admindashboarddto.DoctorStatsDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class DoctorStatsDtoTest {

    private DoctorStatsDto statsDto;

    @BeforeEach
    void setUp() {
        statsDto = new DoctorStatsDto();
        statsDto.setTotalDoctors(50L);
        statsDto.setPendingDoctors(5L);
        statsDto.setApprovedDoctors(40L);
        statsDto.setRejectedDoctors(5L);
        statsDto.setTotalServices(120L);
        statsDto.setDoctorsWithAvailability(45L);
        statsDto.setLastUpdated(LocalDateTime.of(2025, 11, 25, 17, 0));
    }

    @Test
    void testGettersAndSetters() {
        assertEquals(50L, statsDto.getTotalDoctors());
        assertEquals(5L, statsDto.getPendingDoctors());
        assertEquals(40L, statsDto.getApprovedDoctors());
        assertEquals(5L, statsDto.getRejectedDoctors());
        assertEquals(120L, statsDto.getTotalServices());
        assertEquals(45L, statsDto.getDoctorsWithAvailability());
        assertEquals(LocalDateTime.of(2025, 11, 25, 17, 0), statsDto.getLastUpdated());
    }

    @Test
    void testToString() {
        String str = statsDto.toString();
        assertNotNull(str);
        assertTrue(str.contains("totalDoctors=50"));
        assertTrue(str.contains("totalServices=120"));
    }

    @Test
    void testEqualsAndHashCode() {
        DoctorStatsDto another = new DoctorStatsDto();
        another.setTotalDoctors(50L);
        another.setPendingDoctors(5L);
        another.setApprovedDoctors(40L);
        another.setRejectedDoctors(5L);
        another.setTotalServices(120L);
        another.setDoctorsWithAvailability(45L);
        another.setLastUpdated(LocalDateTime.of(2025, 11, 25, 17, 0));

        assertEquals(statsDto, another);
        assertEquals(statsDto.hashCode(), another.hashCode());
    }
}
