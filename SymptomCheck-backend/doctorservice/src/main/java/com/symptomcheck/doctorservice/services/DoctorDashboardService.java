package com.symptomcheck.doctorservice.services;

import com.symptomcheck.doctorservice.dtos.dashboarddto.*;
import com.symptomcheck.doctorservice.models.DoctorAvailability;
import com.symptomcheck.doctorservice.models.HealthcareService;
import com.symptomcheck.doctorservice.repositories.DoctorAvailabilityRepository;
import com.symptomcheck.doctorservice.repositories.HealthcareServiceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DoctorDashboardService {

    private final DoctorAvailabilityRepository availabilityRepository;
    private final HealthcareServiceRepository healthcareServiceRepository;

    public DoctorDashboardDTO getDoctorDashboard(UUID doctorId) {
        log.info("Building doctor dashboard for: {}", doctorId);

        // Récupérer les données du médecin
        DoctorStatsDTO stats = getDoctorStats(doctorId);
        List<DoctorServiceDTO> services = getDoctorServices(doctorId);
        List<DoctorAvailabilityDTO> availability = getDoctorAvailability(doctorId);
        ProfileCompletionDTO profileCompletion = getProfileCompletion(doctorId);

        return new DoctorDashboardDTO(stats, services, availability, profileCompletion);
    }

    private DoctorStatsDTO getDoctorStats(UUID doctorId) {
        Long totalServices = healthcareServiceRepository.countByDoctorId(doctorId);
        Long totalAvailabilitySlots = availabilityRepository.countByDoctorId(doctorId);

        boolean hasAvailability = availabilityRepository.existsByDoctorId(doctorId);
        boolean hasServices = healthcareServiceRepository.existsByDoctorId(doctorId);
        Boolean isProfileComplete = hasAvailability && hasServices;

        int completionPercentage = calculateCompletionPercentage(hasAvailability, hasServices);

        return new DoctorStatsDTO(
                totalServices,
                totalAvailabilitySlots,
                isProfileComplete,
                completionPercentage
        );
    }

    public List<DoctorServiceDTO> getDoctorServices(UUID doctorId) {
        return healthcareServiceRepository.findByDoctorId(doctorId)
                .stream()
                .map(this::convertToServiceDTO)
                .collect(Collectors.toList());
    }

    public List<DoctorAvailabilityDTO> getDoctorAvailability(UUID doctorId) {
        return availabilityRepository.findByDoctorId(doctorId)
                .stream()
                .map(this::convertToAvailabilityDTO)
                .collect(Collectors.toList());
    }

        public ProfileCompletionDTO getProfileCompletion(UUID doctorId) {
        boolean hasAvailability = availabilityRepository.existsByDoctorId(doctorId);
        boolean hasServices = healthcareServiceRepository.existsByDoctorId(doctorId);
        boolean hasBasicInfo = hasAvailability && hasServices; // Simplifié pour l'exemple

        int completionPercentage = calculateCompletionPercentage(hasAvailability, hasServices);

        return new ProfileCompletionDTO(hasAvailability, hasServices, hasBasicInfo, completionPercentage);
    }

    private DoctorServiceDTO convertToServiceDTO(HealthcareService service) {
        return new DoctorServiceDTO(
                service.getId(),
                service.getName(),
                service.getCategory(),
                service.getPrice(),
                service.getDurationMinutes(),
                service.getDescription()
        );
    }

    private DoctorAvailabilityDTO convertToAvailabilityDTO(DoctorAvailability availability) {
        // Convertir la liste des jours en String
        String daysString = availability.getDaysOfWeek().stream()
                .map(DayOfWeek::name)
                .collect(Collectors.joining(", "));

        return new DoctorAvailabilityDTO(
                availability.getId().toString(),
                daysString, // Utiliser la chaîne de jours
                availability.getStartTime().toString(),
                availability.getEndTime().toString(),
                true
        );
    }

    private int calculateCompletionPercentage(boolean hasAvailability, boolean hasServices) {
        int totalItems = 2;
        int completedItems = 0;

        if (hasAvailability) completedItems++;
        if (hasServices) completedItems++;

        return (completedItems * 100) / totalItems;
    }
    // Méthodes supplémentaires pour le dashboard
    public List<String> getServiceCategories(UUID doctorId) {
        return healthcareServiceRepository.findCategoriesByDoctorId(doctorId);
    }

    public Long getTotalServicesCount(UUID doctorId) {
        return healthcareServiceRepository.countByDoctorId(doctorId);
    }

    public Long getTotalAvailabilitySlots(UUID doctorId) {
        return availabilityRepository.countByDoctorId(doctorId);
    }

    public Boolean isProfileComplete(UUID doctorId) {
        boolean hasAvailability = availabilityRepository.existsByDoctorId(doctorId);
        boolean hasServices = healthcareServiceRepository.existsByDoctorId(doctorId);
        return hasAvailability && hasServices;
    }
}