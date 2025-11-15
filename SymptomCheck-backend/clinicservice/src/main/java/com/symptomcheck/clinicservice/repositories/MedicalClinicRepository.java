package com.symptomcheck.clinicservice.repositories;

import com.symptomcheck.clinicservice.models.MedicalClinic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicalClinicRepository extends JpaRepository<MedicalClinic, Long> {
}
