package com.symptomcheck.clinicservice.unit.dtos.adminDashboardDto;

import com.symptomcheck.clinicservice.dtos.admindashboarddto.ClinicStatsDto;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ClinicStatsDtoTest {

    @Test
    void testGettersAndSetters() {
        ClinicStatsDto stats = new ClinicStatsDto();

        // Set values
        stats.setTotalClinics(50L);
        stats.setClinicsWithDoctors(30L);
        stats.setClinicsInEachCity(10L);
        LocalDateTime now = LocalDateTime.now();
        stats.setLastUpdated(now);

        // Assert values
        assertEquals(50L, stats.getTotalClinics());
        assertEquals(30L, stats.getClinicsWithDoctors());
        assertEquals(10L, stats.getClinicsInEachCity());
        assertEquals(now, stats.getLastUpdated());
    }
}
