package com.symptomcheck.medicalrecordservice.models;
import java.time.Instant;
import java.util.Map;

public class MedicalRecordEntry {
    private Instant date;
    private Long doctorId;
    private String note;
    private Map<String, Object> attachments; // ex: urls, meta
}
