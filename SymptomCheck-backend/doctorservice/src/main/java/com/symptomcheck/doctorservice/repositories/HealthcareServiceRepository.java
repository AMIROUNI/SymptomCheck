package com.symptomcheck.doctorservice.repositories;

import com.symptomcheck.doctorservice.models.HealthcareService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface HealthcareServiceRepository  extends JpaRepository<HealthcareService, UUID> {
  public  boolean   existsByDoctorIdAndName(UUID  doctorId, String name);
  boolean existsByDoctorId(UUID  doctorId);

}
