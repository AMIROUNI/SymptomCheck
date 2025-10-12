package com.symptomcheck.doctorservice.services;

import com.symptomcheck.doctorservice.repositories.DoctorProfileRepository;
import com.symptomcheck.doctorservice.repositories.HealthcareServiceRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class HealthcareServiceService {
    private final HealthcareServiceRepository healthcareServiceRepository  ;
}
