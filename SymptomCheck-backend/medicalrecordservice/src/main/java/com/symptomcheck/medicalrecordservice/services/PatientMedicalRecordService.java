package com.symptomcheck.medicalrecordservice.services;

import com.symptomcheck.medicalrecordservice.repositories.PatientMedicalRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PatientMedicalRecordService {
    private   final PatientMedicalRecordRepository patientMedicalRecordRepository;
}
