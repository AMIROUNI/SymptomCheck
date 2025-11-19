package com.symptomcheck.doctorservice.services;

import com.symptomcheck.doctorservice.dto.HealthcareServiceDto;
import com.symptomcheck.doctorservice.models.HealthcareService;
import com.symptomcheck.doctorservice.repositories.HealthcareServiceRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class HealthcareServiceService {
    private final HealthcareServiceRepository healthcareServiceRepository  ;
    private final LocalFileStorageService localFileStorageService;
    public boolean existsByDoctorId(String doctorId) {
        return healthcareServiceRepository.existsByDoctorId(UUID.fromString(doctorId));
    }

    public List<HealthcareService> getAll() {
        return   healthcareServiceRepository.findAll();
    }

    public List<HealthcareService> getHealthcareServiceByDoctorId(UUID doctorId) {
        return  healthcareServiceRepository.findByDoctorId(doctorId);
    }


        public  HealthcareService createHealthcareService(HealthcareServiceDto dto, MultipartFile image) throws IOException {

        HealthcareService healthcareService =  new HealthcareService();
        healthcareService.setDoctorId(dto.getDoctorId());
        healthcareService.setName(dto.getName());
        healthcareService.setCategory(dto.getCategory());
        healthcareService.setDescription(dto.getDescription());
        healthcareService.setPrice(dto.getPrice());
        healthcareService.setDurationMinutes(dto.getDurationMinutes());
        healthcareService.setImageUrl(localFileStorageService.store(image));
        return  healthcareServiceRepository.save(healthcareService);

    }
}
