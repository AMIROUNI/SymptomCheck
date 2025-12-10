package com.symptomcheck.clinicservice.config;

import com.symptomcheck.clinicservice.models.MedicalClinic;
import com.symptomcheck.clinicservice.repositories.MedicalClinicRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {

    private final MedicalClinicRepository medicalClinicRepository;

    @Override
    public void run(String... args) {

        try {
            long count = medicalClinicRepository.count();

            if (count == 0) {
                log.info("No medical clinics found, seeding initial data...");

                List<MedicalClinic> clinics = List.of(
                        createClinic("City Care Clinic", "123 Main St", "123-456-7890", "https://citycare.com", "Tunis", "Tunisia"),
                        createClinic("Health Plus Center", "45 Avenue Habib Bourguiba", "987-654-3210", "https://healthplus.com", "Sousse", "Tunisia"),
                        createClinic("MediPro Clinic", "Rue de Marseille", "55-66-77-88", "https://medipro.com", "Sfax", "Tunisia"),
                        createClinic("Clinique du Lac", "Lac 1", "71-000-000", "https://lacclinic.com", "Tunis", "Tunisia")
                );

                List<MedicalClinic> saved = medicalClinicRepository.saveAll(clinics);

                if (saved.isEmpty()) {
                    log.warn("Database seeder executed but no medical clinics were saved.");
                } else {
                    log.info("Database seeder inserted {} medical clinics.", saved.size());
                }
            } else {
                log.info("Skipping clinic seeding: {} clinics already present.", count);
            }

        } catch (Exception e) {
            // IMPORTANT: don't let seeding kill the whole application
            log.error("Database seeding for MedicalClinic failed. " +
                    "Application will continue to start without seed data.", e);
        }
    }

    private MedicalClinic createClinic(String name, String address, String phone,
                                       String websiteUrl, String city, String country) {
        MedicalClinic clinic = new MedicalClinic();
        clinic.setName(name);
        clinic.setAddress(address);
        clinic.setPhone(phone);
        clinic.setWebsiteUrl(websiteUrl);
        clinic.setCity(city);
        clinic.setCountry(country);
        return clinic;
    }
}
