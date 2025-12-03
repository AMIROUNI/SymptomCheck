package com.symptomcheck.appointmentservice.dtos.admindashboarddto;


import lombok.Data;
import java.time.LocalDateTime;
import java.time.Instant;

@Data
public class AdminAppointmentDto {
    private Long id;
    private LocalDateTime dateTime;
    private String patientId;
    private String doctorId;
    private String status;
    private String description;
    private Long paymentTransactionId;
    private Instant createdAt;
    private Instant updatedAt;
}