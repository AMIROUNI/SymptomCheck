package com.symptomcheck.doctorservice.repositories;

import com.symptomcheck.doctorservice.models.HealthcareService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface HealthcareServiceRepository  extends JpaRepository<HealthcareService, UUID> {
  public  boolean   existsByDoctorIdAndName(UUID  doctorId, String name);
  boolean existsByDoctorId(UUID  doctorId);

    List<HealthcareService> findByDoctorId(UUID doctorId);




    @Query("SELECT COUNT(hs) FROM HealthcareService hs WHERE hs.doctorId = :doctorId")
    Long countByDoctorId(@Param("doctorId") UUID doctorId);

    @Query("SELECT DISTINCT hs.category FROM HealthcareService hs WHERE hs.doctorId = :doctorId")
    List<String> findCategoriesByDoctorId(@Param("doctorId") UUID doctorId);



    @Query("SELECT COUNT(DISTINCT hs.doctorId) FROM HealthcareService hs")
    Long countDoctorsWithServices();

    List<HealthcareService> findByCategory(String category);
}
