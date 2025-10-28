package com.symptomcheck.doctorservice.repositories;

import com.symptomcheck.doctorservice.models.HealthcareService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HealthcareServiceRepository  extends JpaRepository<HealthcareService, Integer> {
  public  boolean   existsByDoctorIdAndName(Long doctorId, String name);
}
