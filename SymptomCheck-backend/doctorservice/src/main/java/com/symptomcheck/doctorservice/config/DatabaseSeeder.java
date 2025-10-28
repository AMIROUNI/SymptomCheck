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

@Component
@RequiredArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {

    private final DoctorAvailabilityRepository availabilityRepository;
    private final DoctorProfileRepository doctorProfileRepository;
    private final HealthcareServiceRepository serviceRepository;

    @Override
    public void run(String... args) throws Exception {

        // --- CHECK AND CREATE DOCTOR PROFILE ---
        Optional<DoctorProfile> existingDoctor = doctorProfileRepository.findById(1L);
        DoctorProfile doctor;
        if (existingDoctor.isPresent()) {
            doctor = existingDoctor.get();
            System.out.println("Doctor profile already exists: " + doctor.getDoctorId());
        } else {
            doctor = new DoctorProfile();
            doctor.setDoctorId(1L);
            doctor.setSpeciality("General Practitioner");
            doctor.setDiploma("MD");
            doctor.setDescription("Experienced doctor");
            doctor.setClinicName("Healthy Clinic");
            doctorProfileRepository.save(doctor);
            System.out.println("Doctor profile created.");
        }

        // --- CHECK AND CREATE DOCTOR AVAILABILITY ---
        boolean availabilityExists = availabilityRepository
                .existsByDoctorIdAndDayOfWeek(1L, DayOfWeek.MONDAY);
        if (!availabilityExists) {
            DoctorAvailability availability = new DoctorAvailability();
            availability.setDoctorId(1L);
            availability.setDayOfWeek(DayOfWeek.MONDAY);
            availability.setStartTime(LocalTime.of(9, 0));
            availability.setEndTime(LocalTime.of(17, 0));
            availabilityRepository.save(availability);
            System.out.println("Doctor availability created.");
        } else {
            System.out.println("Doctor availability already exists.");
        }

        // --- CHECK AND CREATE HEALTHCARE SERVICE ---
        boolean serviceExists = serviceRepository
                .existsByDoctorIdAndName(1L, "General Checkup");
        if (!serviceExists) {
            HealthcareService service = new HealthcareService();
            service.setDoctorId(1L);
            service.setName("General Checkup");
            service.setDescription("Routine general checkup");
            service.setCategory("Checkup");
            service.setDurationMinutes(30);
            service.setPrice(50.0);
            service.setDoctorProfile(doctor);
            serviceRepository.save(service);
            System.out.println("Healthcare service created.");
        } else {
            System.out.println("Healthcare service already exists.");
        }
    }
}
