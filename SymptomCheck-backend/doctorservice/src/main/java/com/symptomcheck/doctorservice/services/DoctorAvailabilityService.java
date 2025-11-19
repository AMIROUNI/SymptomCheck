package com.symptomcheck.doctorservice.services;

import com.symptomcheck.doctorservice.dto.AvailabilityHealthDto;
import com.symptomcheck.doctorservice.models.DoctorAvailability;
import com.symptomcheck.doctorservice.models.HealthcareService;
import com.symptomcheck.doctorservice.repositories.DoctorAvailabilityRepository;
import com.symptomcheck.doctorservice.repositories.HealthcareServiceRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
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
        return availabilityRepository.findIfAvailable(doctorId, day, time).isPresent();
    }

    public boolean existsByDoctorId(UUID doctorId) {
        return availabilityRepository.existsByDoctorId(doctorId);
    }


    public void createAvailabilityHealth(AvailabilityHealthDto availabilityHealthDto) {
        DoctorAvailability da = new DoctorAvailability();
        da.setDoctorId(availabilityHealthDto.getDoctorId());
        da.setStartTime(availabilityHealthDto.getStartTime());
        da.setEndTime(availabilityHealthDto.getEndTime());
        da.setDayOfWeek(availabilityHealthDto.getDayOfWeek());
        availabilityRepository.save(da);

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



}
