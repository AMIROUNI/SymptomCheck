package com.symptomcheck.clinicservice.services;

import com.symptomcheck.clinicservice.dtos.adminDashboardDto.AdminClinicDto;
import com.symptomcheck.clinicservice.dtos.adminDashboardDto.ClinicStatsDto;
import com.symptomcheck.clinicservice.exception.ClinicNotFoundException;
import com.symptomcheck.clinicservice.exception.ClinicValidationException;
import com.symptomcheck.clinicservice.models.MedicalClinic;
import com.symptomcheck.clinicservice.repositories.MedicalClinicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminDashboardService {

    private final MedicalClinicRepository clinicRepository;

    public ClinicStatsDto getClinicStatistics() {
        ClinicStatsDto stats = new ClinicStatsDto();

        Long totalClinics = clinicRepository.count();
        List<Object[]> clinicsByCity = clinicRepository.countClinicsByCity();

        stats.setTotalClinics(totalClinics);
        stats.setClinicsWithDoctors(totalClinics); // Assuming all clinics have doctors
        stats.setClinicsInEachCity((long) clinicsByCity.size());
        stats.setLastUpdated(LocalDateTime.now());

        return stats;
    }

    public List<AdminClinicDto> getAllClinics() {
        return clinicRepository.findAll().stream()
                .map(this::convertToAdminClinicDto)
                .collect(Collectors.toList());
    }

    public List<AdminClinicDto> getClinicsByCity(String city) {
        return clinicRepository.findByCity(city).stream()
                .map(this::convertToAdminClinicDto)
                .collect(Collectors.toList());
    }

    public AdminClinicDto createClinic(AdminClinicDto clinicDto) {

        if (clinicDto.getName().isBlank()) {
            throw new ClinicValidationException("Validation error");
        }
        MedicalClinic clinic = new MedicalClinic();
        clinic.setName(clinicDto.getName());
        clinic.setAddress(clinicDto.getAddress());
        clinic.setPhone(clinicDto.getPhone());
        clinic.setWebsiteUrl(clinicDto.getWebsiteUrl());
        clinic.setCity(clinicDto.getCity());
        clinic.setCountry(clinicDto.getCountry());

        MedicalClinic savedClinic = clinicRepository.save(clinic);
        return convertToAdminClinicDto(savedClinic);
    }

    public AdminClinicDto updateClinic(Long clinicId, AdminClinicDto clinicDto) {
        MedicalClinic clinic = clinicRepository.findById(clinicId)
                .orElseThrow(() -> {return   new ClinicNotFoundException("Clinic not found");});

        clinic.setName(clinicDto.getName());
        clinic.setAddress(clinicDto.getAddress());
        clinic.setPhone(clinicDto.getPhone());
        clinic.setWebsiteUrl(clinicDto.getWebsiteUrl());
        clinic.setCity(clinicDto.getCity());
        clinic.setCountry(clinicDto.getCountry());

        MedicalClinic savedClinic = clinicRepository.save(clinic);
        return convertToAdminClinicDto(savedClinic);
    }

    public void deleteClinic(Long clinicId) {
        clinicRepository.deleteById(clinicId);
    }

    private AdminClinicDto convertToAdminClinicDto(MedicalClinic clinic) {
        AdminClinicDto dto = new AdminClinicDto();
        dto.setId(clinic.getId());
        dto.setName(clinic.getName());
        dto.setAddress(clinic.getAddress());
        dto.setPhone(clinic.getPhone());
        dto.setWebsiteUrl(clinic.getWebsiteUrl());
        dto.setCity(clinic.getCity());
        dto.setCountry(clinic.getCountry());
        dto.setDoctorCount(0L); // You might want to calculate this
        dto.setAppointmentCount(0L); // You might want to calculate this
        return dto;
    }
}