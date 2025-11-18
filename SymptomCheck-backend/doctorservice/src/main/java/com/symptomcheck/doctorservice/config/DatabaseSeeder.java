package com.symptomcheck.doctorservice.config;

import com.symptomcheck.doctorservice.models.DoctorAvailability;
import com.symptomcheck.doctorservice.models.DoctorProfile;
import com.symptomcheck.doctorservice.models.HealthcareService;
import com.symptomcheck.doctorservice.repositories.DoctorAvailabilityRepository;
import com.symptomcheck.doctorservice.repositories.DoctorProfileRepository;
import com.symptomcheck.doctorservice.repositories.HealthcareServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {

    private final DoctorAvailabilityRepository availabilityRepository;
    private final DoctorProfileRepository doctorProfileRepository;
    private final HealthcareServiceRepository serviceRepository;

    @Override
    public void run(String... args) throws Exception {

        // --- CHECK AND CREATE DOCTOR PROFILE ---
        UUID doctorUuid = UUID.fromString("fc6274ff-730a-44f0-9245-17ada9054fe8"); // tu peux générer random avec UUID.randomUUID() si tu veux
        Optional<DoctorProfile> existingDoctor = doctorProfileRepository.findById(doctorUuid);
        DoctorProfile doctor;

        if (existingDoctor.isPresent()) {
            doctor = existingDoctor.get();
            System.out.println("Doctor profile already exists: " + doctor.getDoctorId());
        } else {
            doctor = new DoctorProfile();
            doctor.setDoctorId(doctorUuid);
            doctor.setClinicName("Healthy Clinic");
            doctorProfileRepository.save(doctor);
            System.out.println("Doctor profile created.");
        }

        // --- CHECK AND CREATE DOCTOR AVAILABILITY ---
        boolean availabilityExists = availabilityRepository.existsByDoctorIdAndDayOfWeek(doctorUuid, DayOfWeek.MONDAY);
        if (!availabilityExists) {
            DoctorAvailability availability = new DoctorAvailability();
            availability.setDoctorId(doctorUuid);
            availability.setDayOfWeek(DayOfWeek.MONDAY);
            availability.setStartTime(LocalTime.of(9, 0));
            availability.setEndTime(LocalTime.of(17, 0));
            availabilityRepository.save(availability);
            System.out.println("Doctor availability created.");
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
