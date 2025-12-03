package com.symptomcheck.appointmentservice.models;

import com.symptomcheck.appointmentservice.enums.AppointmentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;


import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Table(name = "appointments")
@Data
public class Appointment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private LocalDateTime dateTime; // moment du rendez-vous

    @NotNull
    private UUID patientId;   // référence User Service

    @NotNull
    private UUID doctorId;    // référence Doctor Service / User Service

    @NotNull
    @Enumerated(EnumType.STRING)
    private AppointmentStatus status = AppointmentStatus.PENDING;

    @Column(length = 2000)
    private String description;

    private Long paymentTransactionId; // référence Payment Service

    private Instant createdAt = Instant.now();
    private Instant updatedAt;

}
