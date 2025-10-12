package com.symptomcheck.clinicservice.services;

import com.symptomcheck.clinicservice.repositories.MedicalClinicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MedicalClinicService {
    private   final MedicalClinicRepository medicalClinicRepository;
}
