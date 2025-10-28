package com.symptomcheck.doctorservice.repositories;

import com.symptomcheck.doctorservice.models.DoctorProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DoctorProfileRepository extends JpaRepository<DoctorProfile, Long> {
}
