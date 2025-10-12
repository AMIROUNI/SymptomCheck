package com.symptomcheck.medicalrecordservice.models;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@Document(collection = "medical_records")
@Data
@Builder
public class PatientMedicalRecord {
    @Id
    private String id;

    private Long patientId; // référence User Service

    private Instant createdAt = Instant.now();

    // Contenu du dossier : structure flexible (liste d'entrées)
    private List<MedicalRecordEntry> entries;

}



