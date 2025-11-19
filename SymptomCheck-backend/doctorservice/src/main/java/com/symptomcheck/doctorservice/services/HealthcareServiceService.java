package com.symptomcheck.doctorservice.services;

import com.symptomcheck.doctorservice.models.HealthcareService;
import com.symptomcheck.doctorservice.repositories.HealthcareServiceRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class HealthcareServiceService {
    private final HealthcareServiceRepository healthcareServiceRepository  ;
    public boolean existsByDoctorId(String doctorId) {
        return healthcareServiceRepository.existsByDoctorId(UUID.fromString(doctorId));
    }

    public List<HealthcareService> getAll() {
        return   healthcareServiceRepository.findAll();
    }

    public List<HealthcareService> getHealthcareServiceByDoctorId(UUID doctorId) {
        return  healthcareServiceRepository.findByDoctorId(doctorId);
    }
}
