package com.symptomcheck.doctorservice.services;

import com.symptomcheck.doctorservice.repositories.DoctorAvailabilityRepository;
import com.symptomcheck.doctorservice.repositories.DoctorProfileRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class DoctorAvailabilityService {
    private final DoctorAvailabilityRepository doctorAvailabilityRepository;

    public boolean isDoctorAvailable(Long doctorId, LocalDateTime dateTime) {
        if (doctorId == null || dateTime == null) {
            throw new IllegalArgumentException("doctorId et dateTime ne doivent pas être null");
        }

        DayOfWeek day = dateTime.getDayOfWeek();
        var time = dateTime.toLocalTime();

        // Vérifie s’il existe une disponibilité correspondant à ce jour et cette heure
        return doctorAvailabilityRepository.findIfAvailable(doctorId, day, time).isPresent();
    }

}
