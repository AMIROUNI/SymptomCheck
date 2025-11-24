package com.symptomcheck.clinicservice.dtos;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MedicalClinicDtoTest {

    @Test
    void testGettersAndSetters() {
        MedicalClinicDto dto = new MedicalClinicDto();

        // Set values
        dto.setName("Healthy Clinic");
        dto.setAddress("123 Main St");
        dto.setPhone("+1234567890");
        dto.setWebsiteUrl("https://healthyclinic.com");
        dto.setCity("New York");
        dto.setCountry("USA");

        // Assert values
        assertEquals("Healthy Clinic", dto.getName());
        assertEquals("123 Main St", dto.getAddress());
        assertEquals("+1234567890", dto.getPhone());
        assertEquals("https://healthyclinic.com", dto.getWebsiteUrl());
        assertEquals("New York", dto.getCity());
        assertEquals("USA", dto.getCountry());
    }
}
