package com.symptomcheck.clinicservice.services;
import com.symptomcheck.clinicservice.dtos.MedicalClinicDto;
import com.symptomcheck.clinicservice.models.MedicalClinic;
import com.symptomcheck.clinicservice.repositories.MedicalClinicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicalClinicService {
    private   final MedicalClinicRepository repository;
    // CREATE


    public MedicalClinic createClinic(MedicalClinicDto dto) {

        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new IllegalArgumentException("Clinic name cannot be empty.");
        }

        MedicalClinic clinic = new MedicalClinic();
        clinic.setName(dto.getName());
        clinic.setAddress(dto.getAddress());
        clinic.setPhone(dto.getPhone());
        clinic.setCity(dto.getCity());
        clinic.setCountry(dto.getCountry());
        clinic.setWebsiteUrl(dto.getWebsiteUrl());

        return repository.save(clinic);
    }


    // READ BY ID
    public MedicalClinic getClinicById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Clinic not found with id: " + id));
    }

    // READ ALL
    public List<MedicalClinic> getAllClinics() {
        List<MedicalClinic> clinics = repository.findAll();

        if (clinics.isEmpty()) {
            throw new RuntimeException("No clinics found in the database.");
        }

        return clinics;
    }

    // UPDATE
    public MedicalClinic updateClinic(Long id, MedicalClinic clinic) {
        MedicalClinic existing = getClinicById(id);

        existing.setName(clinic.getName());
        existing.setAddress(clinic.getAddress());
        existing.setCity(clinic.getCity());
        existing.setCountry(clinic.getCountry());
        existing.setPhone(clinic.getPhone());
        existing.setWebsiteUrl(clinic.getWebsiteUrl());

        return repository.save(existing);
    }

    // DELETE
    public void deleteClinic(Long id) {

        MedicalClinic existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Clinic not found with id: " + id));

        repository.deleteById(existing.getId());
    }

}
