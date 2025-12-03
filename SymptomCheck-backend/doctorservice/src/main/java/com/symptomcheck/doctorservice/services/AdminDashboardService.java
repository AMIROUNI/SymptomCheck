package com.symptomcheck.doctorservice.services;



import com.symptomcheck.doctorservice.dtos.admindashboarddto.AdminDoctorDto;
import com.symptomcheck.doctorservice.dtos.admindashboarddto.AvailabilityDto;
import com.symptomcheck.doctorservice.dtos.admindashboarddto.DoctorStatsDto;
import com.symptomcheck.doctorservice.dtos.admindashboarddto.ServiceDto;
import com.symptomcheck.doctorservice.models.DoctorAvailability;
import com.symptomcheck.doctorservice.models.HealthcareService;
import com.symptomcheck.doctorservice.repositories.DoctorAvailabilityRepository;
import com.symptomcheck.doctorservice.repositories.HealthcareServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminDashboardService {

    private final HealthcareServiceRepository healthcareServiceRepository;
    private final DoctorAvailabilityRepository availabilityRepository;

    public DoctorStatsDto getDoctorStatistics() {
        DoctorStatsDto stats = new DoctorStatsDto();

        // Get all unique doctors from services
        List<HealthcareService> allServices = healthcareServiceRepository.findAll();
        Set<UUID> uniqueDoctors = allServices.stream()
                .map(HealthcareService::getDoctorId)
                .collect(Collectors.toSet());

        // Get doctors with availability
        Long doctorsWithAvailability = availabilityRepository.countDoctorsWithAvailability();

        // Calculate statistics (you might want to add status field to your models)
        stats.setTotalDoctors((long) uniqueDoctors.size());
        stats.setPendingDoctors(0L); // You'll need to add status field
        stats.setApprovedDoctors((long) uniqueDoctors.size());
        stats.setRejectedDoctors(0L);
        stats.setTotalServices(healthcareServiceRepository.count());
        stats.setDoctorsWithAvailability(doctorsWithAvailability);
        stats.setLastUpdated(LocalDateTime.now());

        return stats;
    }

    public List<AdminDoctorDto> getAllDoctors() {
        // Get all unique doctors from services
        List<HealthcareService> allServices = healthcareServiceRepository.findAll();
        Map<UUID, List<HealthcareService>> servicesByDoctor = allServices.stream()
                .collect(Collectors.groupingBy(HealthcareService::getDoctorId));

        return servicesByDoctor.entrySet().stream()
                .map(entry -> convertToAdminDoctorDto(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    public List<AdminDoctorDto> getDoctorsBySpeciality(String speciality) {
        List<HealthcareService> services = healthcareServiceRepository.findAll();

        return services.stream()
                .filter(service -> speciality.equalsIgnoreCase(service.getCategory()))
                .collect(Collectors.groupingBy(HealthcareService::getDoctorId))
                .entrySet().stream()
                .map(entry -> convertToAdminDoctorDto(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    public AdminDoctorDto updateDoctorStatus(UUID doctorId, String status) {
        // Implementation to update doctor status
        // You'll need to add status field to your doctor model

        List<HealthcareService> doctorServices = healthcareServiceRepository.findByDoctorId(doctorId);
        return convertToAdminDoctorDto(doctorId, doctorServices);
    }

    private AdminDoctorDto convertToAdminDoctorDto(UUID doctorId, List<HealthcareService> services) {
        AdminDoctorDto dto = new AdminDoctorDto();
        dto.setDoctorId(doctorId);

        if (!services.isEmpty()) {
            HealthcareService firstService = services.get(0);
            dto.setSpeciality(firstService.getCategory());
            dto.setDescription(firstService.getDescription());
        }

        dto.setStatus("APPROVED"); // Default status
        dto.setRating(4.5); // Default rating
        dto.setTotalReviews(10); // Default reviews

        // Convert services
        List<ServiceDto> serviceDtos = services.stream()
                .map(this::convertToServiceDto)
                .collect(Collectors.toList());
        dto.setServices(serviceDtos);

        // Get and convert availabilities
        List<DoctorAvailability> availabilities = availabilityRepository.findByDoctorId(doctorId);
        List<AvailabilityDto> availabilityDtos = availabilities.stream()
                .map(this::convertToAvailabilityDto)
                .collect(Collectors.toList());
        dto.setAvailabilities(availabilityDtos);

        return dto;
    }

    private ServiceDto convertToServiceDto(HealthcareService service) {
   ServiceDto dto = new ServiceDto();
        dto.setId(service.getId());
        dto.setName(service.getName());
        dto.setDescription(service.getDescription());
        dto.setCategory(service.getCategory());
        dto.setPrice(service.getPrice());
        dto.setDurationMinutes(service.getDurationMinutes());
        return dto;
    }

    private AvailabilityDto convertToAvailabilityDto(DoctorAvailability availability) {
        AvailabilityDto dto = new AvailabilityDto();
        dto.setId(availability.getId());

        // Convertir la liste des jours en String
        String daysString = availability.getDaysOfWeek().stream()
                .map(DayOfWeek::name)
                .collect(Collectors.joining(", "));
        dto.setDayOfWeek(daysString);

        dto.setStartTime(availability.getStartTime().toString());
        dto.setEndTime(availability.getEndTime().toString());
        return dto;
    }
}