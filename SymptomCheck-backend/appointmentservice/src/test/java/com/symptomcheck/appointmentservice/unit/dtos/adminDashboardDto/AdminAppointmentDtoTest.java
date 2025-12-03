package com.symptomcheck.appointmentservice.unit.dtos.adminDashboardDto;

import com.symptomcheck.appointmentservice.dtos.admindashboarddto.AdminAppointmentDto;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class AdminAppointmentDtoTest {

    @Test
    void testGettersAndSetters() {
        AdminAppointmentDto dto = new AdminAppointmentDto();

        Long id = 10L;
        LocalDateTime dateTime = LocalDateTime.now();
        String patientId = "patient-123";
        String doctorId = "doctor-456";
        String status = "PENDING";
        String description = "Routine check";
        Long paymentId = 555L;
        Instant createdAt = Instant.now();
        Instant updatedAt = Instant.now();

        dto.setId(id);
        dto.setDateTime(dateTime);
        dto.setPatientId(patientId);
        dto.setDoctorId(doctorId);
        dto.setStatus(status);
        dto.setDescription(description);
        dto.setPaymentTransactionId(paymentId);
        dto.setCreatedAt(createdAt);
        dto.setUpdatedAt(updatedAt);

        assertEquals(id, dto.getId());
        assertEquals(dateTime, dto.getDateTime());
        assertEquals(patientId, dto.getPatientId());
        assertEquals(doctorId, dto.getDoctorId());
        assertEquals(status, dto.getStatus());
        assertEquals(description, dto.getDescription());
        assertEquals(paymentId, dto.getPaymentTransactionId());
        assertEquals(createdAt, dto.getCreatedAt());
        assertEquals(updatedAt, dto.getUpdatedAt());
    }

    @Test
    void testEqualsAndHashCode() {
        LocalDateTime now = LocalDateTime.now();
        Instant created = Instant.now();

        AdminAppointmentDto dto1 = new AdminAppointmentDto();
        dto1.setId(1L);
        dto1.setDateTime(now);
        dto1.setPatientId("patientA");
        dto1.setDoctorId("doctorA");
        dto1.setStatus("CONFIRMED");
        dto1.setDescription("Test");
        dto1.setPaymentTransactionId(100L);
        dto1.setCreatedAt(created);
        dto1.setUpdatedAt(created);

        AdminAppointmentDto dto2 = new AdminAppointmentDto();
        dto2.setId(1L);
        dto2.setDateTime(now);
        dto2.setPatientId("patientA");
        dto2.setDoctorId("doctorA");
        dto2.setStatus("CONFIRMED");
        dto2.setDescription("Test");
        dto2.setPaymentTransactionId(100L);
        dto2.setCreatedAt(created);
        dto2.setUpdatedAt(created);

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void testToString() {
        AdminAppointmentDto dto = new AdminAppointmentDto();
        dto.setId(99L);
        dto.setDescription("Admin appointment test");

        String text = dto.toString();

        assertNotNull(text);
        assertTrue(text.contains("AdminAppointmentDto"));
        assertTrue(text.contains("Admin appointment test"));
    }
}
