package com.symptomcheck.appointmentservice.unit.dtos.dashboardDto;

import com.symptomcheck.appointmentservice.dtos.dashboarddto.AppointmentSummaryDTO;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class AppointmentSummaryDTOTest {

    @Test
    void testNoArgsConstructorAndSetters() {
        AppointmentSummaryDTO dto = new AppointmentSummaryDTO();

        Long id = 100L;
        LocalDateTime dateTime = LocalDateTime.now();
        String patientName = "John Doe";
        String status = "CONFIRMED";
        String serviceType = "Cardiology";

        dto.setId(id);
        dto.setDateTime(dateTime);
        dto.setPatientName(patientName);
        dto.setStatus(status);
        dto.setServiceType(serviceType);

        assertEquals(id, dto.getId());
        assertEquals(dateTime, dto.getDateTime());
        assertEquals(patientName, dto.getPatientName());
        assertEquals(status, dto.getStatus());
        assertEquals(serviceType, dto.getServiceType());
    }

    @Test
    void testAllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();

        AppointmentSummaryDTO dto = new AppointmentSummaryDTO(
                5L,
                now,
                "Alice",
                "PENDING",
                "Dermatology"
        );

        assertEquals(5L, dto.getId());
        assertEquals(now, dto.getDateTime());
        assertEquals("Alice", dto.getPatientName());
        assertEquals("PENDING", dto.getStatus());
        assertEquals("Dermatology", dto.getServiceType());
    }

    @Test
    void testEqualsAndHashCode() {
        LocalDateTime now = LocalDateTime.now();

        AppointmentSummaryDTO dto1 = new AppointmentSummaryDTO(1L, now, "Sam", "DONE", "General");
        AppointmentSummaryDTO dto2 = new AppointmentSummaryDTO(1L, now, "Sam", "DONE", "General");

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void testToString() {
        AppointmentSummaryDTO dto = new AppointmentSummaryDTO(
                1L,
                LocalDateTime.now(),
                "Patient X",
                "PENDING",
                "Cardiology"
        );

        assertNotNull(dto.toString());
        assertTrue(dto.toString().contains("AppointmentSummaryDTO"));
    }
}
