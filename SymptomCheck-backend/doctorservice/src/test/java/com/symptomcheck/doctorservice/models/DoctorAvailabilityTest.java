package com.symptomcheck.doctorservice.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DoctorAvailabilityTest {

    private DoctorAvailability availability;

    @BeforeEach
    void setUp() {
        availability = new DoctorAvailability();
        availability.setDoctorId(UUID.randomUUID());
        availability.setDaysOfWeek(Arrays.asList(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY));
        availability.setStartTime(LocalTime.of(9, 0));
        availability.setEndTime(LocalTime.of(17, 0));
    }

    @Test
    void testGetters() {
        assertNotNull(availability.getDoctorId());
        List<DayOfWeek> days = availability.getDaysOfWeek();
        assertEquals(2, days.size());
        assertEquals(DayOfWeek.MONDAY, days.get(0));
        assertEquals(DayOfWeek.WEDNESDAY, days.get(1));
        assertEquals(LocalTime.of(9, 0), availability.getStartTime());
        assertEquals(LocalTime.of(17, 0), availability.getEndTime());
    }

    @Test
    void testSetters() {
        UUID newDoctorId = UUID.randomUUID();
        availability.setDoctorId(newDoctorId);
        availability.setDaysOfWeek(List.of(DayOfWeek.FRIDAY));
        availability.setStartTime(LocalTime.of(10, 0));
        availability.setEndTime(LocalTime.of(18, 0));

        assertEquals(newDoctorId, availability.getDoctorId());
        assertEquals(1, availability.getDaysOfWeek().size());
        assertEquals(DayOfWeek.FRIDAY, availability.getDaysOfWeek().get(0));
        assertEquals(LocalTime.of(10, 0), availability.getStartTime());
        assertEquals(LocalTime.of(18, 0), availability.getEndTime());
    }

    @Test
    void testIdSetterAndGetter() {
        availability.setId(100L);
        assertEquals(100L, availability.getId());
    }
}
