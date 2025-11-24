package com.symptomcheck.doctorservice.config;

import com.symptomcheck.doctorservice.models.DoctorAvailability;
import com.symptomcheck.doctorservice.models.HealthcareService;
import com.symptomcheck.doctorservice.repositories.DoctorAvailabilityRepository;
import com.symptomcheck.doctorservice.repositories.HealthcareServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {

    private final DoctorAvailabilityRepository availabilityRepository;
    private final HealthcareServiceRepository serviceRepository;

    @Override
    public void run(String... args) throws Exception {
        UUID doctorUuid = UUID.fromString("fc6274ff-730a-44f0-9245-17ada9054fe8");

        // --- CHECK AND CREATE DOCTOR AVAILABILITY WITH MULTIPLE DAYS ---
        boolean availabilityExists = availabilityRepository.existsByDoctorId(doctorUuid);
        if (!availabilityExists) {
            DoctorAvailability availability = new DoctorAvailability();
            availability.setDoctorId(doctorUuid);

            // Liste des jours de travail
            List<DayOfWeek> workingDays = Arrays.asList(
                    DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
                    DayOfWeek.THURSDAY, DayOfWeek.FRIDAY
            );
            availability.setDaysOfWeek(workingDays);

            availability.setStartTime(LocalTime.of(9, 0));
            availability.setEndTime(LocalTime.of(17, 0));
            availabilityRepository.save(availability);
            System.out.println("Doctor availability with multiple days created.");
        } else {
            System.out.println("Doctor availability already exists.");
        }

        // --- CHECK AND CREATE HEALTHCARE SERVICE ---
        boolean serviceExists = serviceRepository.existsByDoctorIdAndName(doctorUuid, "General Checkup");
        if (!serviceExists) {
            HealthcareService service = new HealthcareService();
            service.setDoctorId(doctorUuid);
            service.setName("General Checkup");
            service.setDescription("Routine general checkup");
            service.setCategory("Checkup");
            service.setDurationMinutes(30);
            service.setPrice(50.0);
            serviceRepository.save(service);
            System.out.println("Healthcare service created.");
        } else {
            System.out.println("Healthcare service already exists.");
        }
    }
}