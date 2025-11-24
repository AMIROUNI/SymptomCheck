package com.symptomcheck.doctorservice.services;

import com.symptomcheck.doctorservice.dtos.AvailabilityHealthDto;
import com.symptomcheck.doctorservice.models.DoctorAvailability;
import com.symptomcheck.doctorservice.models.HealthcareService;
import com.symptomcheck.doctorservice.repositories.DoctorAvailabilityRepository;
import com.symptomcheck.doctorservice.repositories.HealthcareServiceRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class DoctorAvailabilityService {
    private final DoctorAvailabilityRepository availabilityRepository;
    private final HealthcareServiceRepository healthcareRepo;
    private final WebClient webClient;

    public boolean isDoctorAvailable(UUID doctorId, LocalDateTime dateTime) {
        DayOfWeek day = dateTime.getDayOfWeek();
        var time = dateTime.toLocalTime();

        // Récupérer toutes les disponibilités du docteur
        List<DoctorAvailability> availabilities = availabilityRepository.findByDoctorId(doctorId);

        // Vérifier si le jour et l'heure sont dans une des disponibilités
        return availabilities.stream()
                .anyMatch(availability ->
                        availability.getDaysOfWeek().contains(day) &&
                                !time.isBefore(availability.getStartTime()) &&
                                !time.isAfter(availability.getEndTime())
                );
    }

    public boolean existsByDoctorId(UUID doctorId) {
        return availabilityRepository.existsByDoctorId(doctorId);
    }

    public void createAvailabilityHealth(AvailabilityHealthDto availabilityHealthDto) {
        // Créer une seule entrée de disponibilité avec la liste des jours
        DoctorAvailability da = new DoctorAvailability();
        da.setDoctorId(availabilityHealthDto.getDoctorId());
        da.setStartTime(availabilityHealthDto.getStartTime());
        da.setEndTime(availabilityHealthDto.getEndTime());
        da.setDaysOfWeek(availabilityHealthDto.getDaysOfWeek()); // Liste des jours
        availabilityRepository.save(da);

        // Créer le service de santé
        HealthcareService hc = new HealthcareService();
        hc.setDoctorId(availabilityHealthDto.getDoctorId());
        hc.setCategory(availabilityHealthDto.getCategory());
        hc.setDescription(availabilityHealthDto.getDescription());
        hc.setName(availabilityHealthDto.getName());
        hc.setPrice(availabilityHealthDto.getPrice());
        hc.setImageUrl(availabilityHealthDto.getImageUrl());
        hc.setDurationMinutes(availabilityHealthDto.getDurationMinutes());
        healthcareRepo.save(hc);
    }

    public List<DoctorAvailability> getAvailabilityByDoctorId(UUID doctorId) {
        return availabilityRepository.findByDoctorId(doctorId);
    }




    public List<String> getAvailableSlotsForDate(UUID doctorId, LocalDate date) {
        log.info("entered getAvailableSlotsForDate");

        List<DoctorAvailability> availabilities = availabilityRepository.findByDoctorId(doctorId);
        log.info("availabilities: " + availabilities.size());

        if (availabilities.isEmpty()) {
            return List.of();
        }

        DayOfWeek requestedDay = date.getDayOfWeek();
        log.info("requestedDay: " + requestedDay);

        // 1️⃣ Filter by correct DayOfWeek ENUM
        List<DoctorAvailability> todaysAvailabilities = availabilities.stream()
                .filter(a -> a.getDaysOfWeek().contains(requestedDay))
                .toList();

        log.info("todaysAvailabilities size: " + todaysAvailabilities.size());

        if (todaysAvailabilities.isEmpty()) {
            return List.of();
        }

        List<String> generatedSlots = new ArrayList<>();

        // 2️⃣ Generate 60-minute slots
        for (DoctorAvailability availability : todaysAvailabilities) {
            LocalTime time = availability.getStartTime();

            while (!time.isAfter(availability.getEndTime().minusMinutes(60))) {
                generatedSlots.add(time.toString());
                time = time.plusMinutes(60);
            }
        }

        /*  // 3️⃣ Remove taken appointments
        List<Appointment> takenAppointments =
                appointmentRepository.findByDoctorIdAndDate(doctorId, date);

        List<String> takenSlots = takenAppointments.stream()
                .map(a -> a.getTime().toString())
                .toList();  */

        // 3️⃣ Remove duplicates & sort
        return generatedSlots.stream()
                .distinct()
                .sorted()
                .toList();
    }

}