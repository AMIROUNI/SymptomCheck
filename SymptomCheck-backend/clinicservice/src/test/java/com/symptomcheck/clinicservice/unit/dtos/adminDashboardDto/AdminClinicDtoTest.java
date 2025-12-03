package com.symptomcheck.clinicservice.unit.dtos.adminDashboardDto;

import com.symptomcheck.clinicservice.dtos.admindashboarddto.AdminClinicDto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AdminClinicDtoTest {

    @Test
    void testGettersAndSetters() {
        AdminClinicDto dto = new AdminClinicDto();

        dto.setId(1L);
        dto.setName("Clinic A");
        dto.setAddress("123 Main St");
        dto.setPhone("123-456-7890");
        dto.setWebsiteUrl("https://clinic-a.com");
        dto.setCity("CityX");
        dto.setCountry("CountryY");
        dto.setDoctorCount(5L);
        dto.setAppointmentCount(20L);

        assertEquals(1L, dto.getId());
        assertEquals("Clinic A", dto.getName());
        assertEquals("123 Main St", dto.getAddress());
        assertEquals("123-456-7890", dto.getPhone());
        assertEquals("https://clinic-a.com", dto.getWebsiteUrl());
        assertEquals("CityX", dto.getCity());
        assertEquals("CountryY", dto.getCountry());
        assertEquals(5L, dto.getDoctorCount());
        assertEquals(20L, dto.getAppointmentCount());
    }
}
