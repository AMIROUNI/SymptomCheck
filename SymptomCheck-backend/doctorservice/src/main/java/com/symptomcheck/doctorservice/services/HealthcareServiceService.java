package com.symptomcheck.doctorservice.services;

import com.symptomcheck.doctorservice.models.HealthcareService;
import com.symptomcheck.doctorservice.repositories.HealthcareServiceRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class HealthcareServiceService {
    private final HealthcareServiceRepository healthcareServiceRepository  ;
    public boolean existsByDoctorId(String doctorId) {
        try {
            UUID uuid = UUID.fromString(doctorId);
            return healthcareServiceRepository.existsByDoctorId(uuid);
        } catch (IllegalArgumentException e) {
            log.error("Invalid doctorId UUID: {}", doctorId);
            return false; // test-friendly: no exception explosion
        }
    }

    public List<HealthcareService> getAll() {
        return   healthcareServiceRepository.findAll();
    }

    public List<HealthcareService> getHealthcareServiceByDoctorId(UUID doctorId) {
        return  healthcareServiceRepository.findByDoctorId(doctorId);
    }
}
