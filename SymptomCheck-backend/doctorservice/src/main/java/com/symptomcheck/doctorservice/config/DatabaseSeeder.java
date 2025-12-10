package com.symptomcheck.doctorservice.config;

import com.symptomcheck.doctorservice.models.DoctorAvailability;
import com.symptomcheck.doctorservice.models.HealthcareService;
import com.symptomcheck.doctorservice.repositories.DoctorAvailabilityRepository;
import com.symptomcheck.doctorservice.repositories.HealthcareServiceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {

    private final DoctorAvailabilityRepository availabilityRepository;
    private final HealthcareServiceRepository serviceRepository;

    @Override
    public void run(String... args) {

        UUID doctorUuid = UUID.fromString("fc6274ff-730a-44f0-9245-17ada9054fe8");

        try {
            seedDoctorAvailability(doctorUuid);
            seedHealthcareService(doctorUuid);
        } catch (Exception e) {
            // DO NOT let this kill the whole application
            log.error("Doctor service database seeding failed. " +
                    "Application will continue to run without seed data.", e);
        }
    }

    private void seedDoctorAvailability(UUID doctorUuid) {
        boolean availabilityExists = availabilityRepository.existsByDoctorId(doctorUuid);

        if (!availabilityExists) {
            DoctorAvailability availability = new DoctorAvailability();
            availability.setDoctorId(doctorUuid);

            // Working days: Monday–Friday
            List<DayOfWeek> workingDays = Arrays.asList(
                    DayOfWeek.MONDAY,
                    DayOfWeek.TUESDAY,
                    DayOfWeek.WEDNESDAY,
                    DayOfWeek.THURSDAY,
                    DayOfWeek.FRIDAY
            );
            availability.setDaysOfWeek(workingDays);
            availability.setStartTime(LocalTime.of(9, 0));
            availability.setEndTime(LocalTime.of(17, 0));

            availabilityRepository.save(availability);
            log.info("Doctor availability with multiple days created for doctor {}.", doctorUuid);
        } else {
            log.info("Doctor availability already exists for doctor {}.", doctorUuid);
        }
    }

    private void seedHealthcareService(UUID doctorUuid) {
        String serviceName = "General Checkup";

        boolean serviceExists = serviceRepository.existsByDoctorIdAndName(doctorUuid, serviceName);

        if (!serviceExists) {
            HealthcareService service = new HealthcareService();
            service.setDoctorId(doctorUuid);
            service.setName(serviceName);
            service.setDescription("Routine general checkup");
            service.setCategory("Checkup");
            service.setDurationMinutes(30);
            service.setPrice(50.0);

            serviceRepository.save(service);
            log.info("Healthcare service '{}' created for doctor {}.", serviceName, doctorUuid);
        } else {
            log.info("Healthcare service '{}' already exists for doctor {}.", serviceName, doctorUuid);
        }
    }
}
