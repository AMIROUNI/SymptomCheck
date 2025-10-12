package com.symptomcheck.aiservice.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Entity
@Table(name = "ai_requests")
@Data
public class AiRequestLog {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long patientId;
    private String inputText; // symptômes bruts
    private String predictedLabel;
    private Double confidence;
    private Instant requestedAt = Instant.now();
    private String modelVersion;
}

