package com.symptomcheck.paymentservice.models;

import com.symptomcheck.paymentservice.enums.PaymentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.Instant;



@Entity
@Table(name = "payment_transactions")
@Data
public class PaymentTransaction {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Long appointmentId; // référence Appointment Service

    @NotNull
    private Long userId; // payeur (patientId)

    @NotNull
    private Double amount;

    @NotNull
    private Instant paymentDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    private String providerTransactionId; // id externe (Stripe, etc.)


}
