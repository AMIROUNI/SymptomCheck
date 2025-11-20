package com.symptomcheck.clinicservice.repositories;

import com.symptomcheck.clinicservice.models.MedicalClinic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicalClinicRepository extends JpaRepository<MedicalClinic, Long> {
    List<MedicalClinic> findByCity(String city);

    List<MedicalClinic> findByCountry(String country);

    @Query("SELECT mc.city, COUNT(mc) FROM MedicalClinic mc GROUP BY mc.city")
    List<Object[]> countClinicsByCity();

    Long countByCity(String city);
}
