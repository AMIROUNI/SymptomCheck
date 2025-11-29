package com.symptomcheck.doctorservice.dtos;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AvailabilityHealthDtoTest {

    private AvailabilityHealthDto dto;

    @BeforeEach
    void setUp() {
        dto = new AvailabilityHealthDto();
        dto.setDoctorId(UUID.randomUUID());
        dto.setDaysOfWeek(List.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY));
        dto.setStartTime(LocalTime.of(9, 0));
        dto.setEndTime(LocalTime.of(17, 0));
        dto.setName("Consultation");
        dto.setDescription("General health consultation");
        dto.setCategory("General");
        dto.setImageUrl("http://example.com/image.jpg");
        dto.setDurationMinutes(30);
        dto.setPrice(50.0);
    }

    @Test
    void testGetters() {
        assertNotNull(dto.getDoctorId());
        assertEquals(List.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY), dto.getDaysOfWeek());
        assertEquals(LocalTime.of(9, 0), dto.getStartTime());
        assertEquals(LocalTime.of(17, 0), dto.getEndTime());
        assertEquals("Consultation", dto.getName());
        assertEquals("General health consultation", dto.getDescription());
        assertEquals("General", dto.getCategory());
        assertEquals("http://example.com/image.jpg", dto.getImageUrl());
        assertEquals(30, dto.getDurationMinutes());
        assertEquals(50.0, dto.getPrice());
    }

    @Test
    void testSetters() {
        dto.setDoctorId(UUID.randomUUID());
        dto.setDaysOfWeek(List.of(DayOfWeek.FRIDAY));
        dto.setStartTime(LocalTime.of(8, 0));
        dto.setEndTime(LocalTime.of(12, 0));
        dto.setName("Follow-up");
        dto.setDescription("Follow-up session");
        dto.setCategory("Specialized");
        dto.setImageUrl("http://example.com/newimage.jpg");
        dto.setDurationMinutes(45);
        dto.setPrice(80.0);

        assertEquals(List.of(DayOfWeek.FRIDAY), dto.getDaysOfWeek());
        assertEquals(LocalTime.of(8, 0), dto.getStartTime());
        assertEquals(LocalTime.of(12, 0), dto.getEndTime());
        assertEquals("Follow-up", dto.getName());
        assertEquals("Follow-up session", dto.getDescription());
        assertEquals("Specialized", dto.getCategory());
        assertEquals("http://example.com/newimage.jpg", dto.getImageUrl());
        assertEquals(45, dto.getDurationMinutes());
        assertEquals(80.0, dto.getPrice());
    }
}
