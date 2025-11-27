package com.symptomcheck.clinicservice.unit.models;

import com.symptomcheck.clinicservice.models.MedicalClinic;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MedicalClinicTest {

    @Test
    void testGettersAndSetters() {
        MedicalClinic clinic = new MedicalClinic();

        clinic.setId(1L);
        clinic.setName("Sunrise Clinic");
        clinic.setAddress("123 Main St");
        clinic.setPhone("+123456789");
        clinic.setWebsiteUrl("https://sunriseclinic.com");
        clinic.setCity("New York");
        clinic.setCountry("USA");

        assertEquals(1L, clinic.getId());
        assertEquals("Sunrise Clinic", clinic.getName());
        assertEquals("123 Main St", clinic.getAddress());
        assertEquals("+123456789", clinic.getPhone());
        assertEquals("https://sunriseclinic.com", clinic.getWebsiteUrl());
        assertEquals("New York", clinic.getCity());
        assertEquals("USA", clinic.getCountry());
    }

    @Test
    void testEqualsAndHashCode() {
        MedicalClinic clinic1 = new MedicalClinic();
        clinic1.setId(1L);
        MedicalClinic clinic2 = new MedicalClinic();
        clinic2.setId(1L);
        MedicalClinic clinic3 = new MedicalClinic();
        clinic3.setId(2L);

        assertEquals(clinic1, clinic2);
        assertNotEquals(clinic1, clinic3);
        assertEquals(clinic1.hashCode(), clinic2.hashCode());
        assertNotEquals(clinic1.hashCode(), clinic3.hashCode());
    }

    @Test
    void testToString() {
        MedicalClinic clinic = new MedicalClinic();
        clinic.setName("Sunrise Clinic");

        String str = clinic.toString();
        assertTrue(str.contains("Sunrise Clinic"));
    }
}
