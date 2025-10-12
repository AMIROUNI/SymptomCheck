package com.symptomcheck.doctorservice.services;

import com.symptomcheck.doctorservice.repositories.DoctorProfileRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@AllArgsConstructor
public class DoctorProfileService {
    private final DoctorProfileRepository doctorProfileRepository;
}
