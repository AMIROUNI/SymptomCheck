package com.symptomcheck.medicalrecordservice.repositories;

import com.symptomcheck.medicalrecordservice.models.PatientMedicalRecord;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientMedicalRecordRepository extends MongoRepository<PatientMedicalRecord, String> {
    // You can define custom query methods here if needed
}
