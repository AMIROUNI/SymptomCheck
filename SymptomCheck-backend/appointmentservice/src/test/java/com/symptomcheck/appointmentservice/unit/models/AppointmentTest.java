package com.symptomcheck.appointmentservice.unit.models;

import com.symptomcheck.appointmentservice.enums.AppointmentStatus;
import com.symptomcheck.appointmentservice.models.Appointment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AppointmentTest {

    private Appointment appointment;
    private Long id;
    private LocalDateTime date;
    private UUID patientId;
    private UUID doctorId;
    private String description;
    private Long paymentId;
    private Instant created;
    private Instant updated;

    @BeforeEach
    void setUp() {
        appointment = new Appointment();

        id = 1L;
        date = LocalDateTime.now().plusDays(1);
        patientId = UUID.randomUUID();
        doctorId = UUID.randomUUID();
        description = "Check-up";
        paymentId = 123L;
        created = Instant.now();
        updated = Instant.now();
    }

    @Nested
    class GetterSetterTests {

        @Test
        void shouldSetAndGetFieldsCorrectly() {
            appointment.setId(id);
            appointment.setDateTime(date);
            appointment.setPatientId(patientId);
            appointment.setDoctorId(doctorId);
            appointment.setStatus(AppointmentStatus.CONFIRMED);
            appointment.setDescription(description);
            appointment.setPaymentTransactionId(paymentId);
            appointment.setCreatedAt(created);
            appointment.setUpdatedAt(updated);

            assertEquals(id, appointment.getId());
            assertEquals(date, appointment.getDateTime());
            assertEquals(patientId, appointment.getPatientId());
            assertEquals(doctorId, appointment.getDoctorId());
            assertEquals(AppointmentStatus.CONFIRMED, appointment.getStatus());
            assertEquals(description, appointment.getDescription());
            assertEquals(paymentId, appointment.getPaymentTransactionId());
            assertEquals(created, appointment.getCreatedAt());
            assertEquals(updated, appointment.getUpdatedAt());
        }
    }

    @Nested
    class DefaultValueTests {

        @Test
        void shouldHaveDefaultValues() {
            assertEquals(AppointmentStatus.PENDING, appointment.getStatus());
            assertNotNull(appointment.getCreatedAt(), "createdAt should be automatically set");
            assertNull(appointment.getUpdatedAt(), "updatedAt should be null by default");
        }
    }

    @Nested
    class StatusChangeTests {

        @Test
        void shouldChangeEnumStatus() {
            appointment.setStatus(AppointmentStatus.CONFIRMED);
            assertEquals(AppointmentStatus.CONFIRMED, appointment.getStatus());

            appointment.setStatus(AppointmentStatus.COMPLETED);
            assertEquals(AppointmentStatus.COMPLETED, appointment.getStatus());
        }
    }

    @Nested
    class EqualityTests {

        @Test
        void shouldBeEqualWhenFieldsMatch() {
            Appointment a1 = new Appointment();
            Appointment a2 = new Appointment();
            Instant now = Instant.now();

            a1.setCreatedAt(now);
            a2.setCreatedAt(now);

            a1.setId(1L);
            a2.setId(1L);

            a1.setPatientId(patientId);
            a2.setPatientId(patientId);

            a1.setDoctorId(doctorId);
            a2.setDoctorId(doctorId);


            assertEquals(a1, a2);
            assertEquals(a1.hashCode(), a2.hashCode());
        }
    }

    @Nested
    class ToStringTests {

        @Test
        void shouldGenerateNonNullToString() {
            appointment.setId(50L);
            appointment.setDescription("Test appointment");

            assertNotNull(appointment.toString());
            assertTrue(appointment.toString().contains("Test appointment"));
        }
    }
}
