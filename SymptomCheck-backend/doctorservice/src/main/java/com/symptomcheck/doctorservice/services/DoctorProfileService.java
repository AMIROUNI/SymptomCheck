package com.symptomcheck.doctorservice.services;

import com.symptomcheck.doctorservice.models.DoctorProfile;
import com.symptomcheck.doctorservice.repositories.DoctorProfileRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@AllArgsConstructor
public class DoctorProfileService {
    private final DoctorProfileRepository doctorProfileRepository;

    // 🔹 Créer ou mettre à jour un profil médecin
    public DoctorProfile saveDoctorProfile(DoctorProfile profile) {
        return doctorProfileRepository.save(profile);
    }

    // 🔹 Récupérer tous les profils médecins
    public List<DoctorProfile> getAllProfiles() {
        return doctorProfileRepository.findAll();
    }

    // 🔹 Récupérer un profil par ID
    public Optional<DoctorProfile> getProfileById(Long doctorId) {
        return doctorProfileRepository.findById(Math.toIntExact(doctorId));
    }

    // 🔹 Supprimer un profil médecin
    public void deleteProfile(Long doctorId) {
        doctorProfileRepository.deleteById(Math.toIntExact(doctorId));
    }

    // 🔹 Chercher par spécialité (optionnel)
    public DoctorProfile getProfileBySpeciality(String speciality) {
        return doctorProfileRepository.findBySpeciality(speciality);
    }
}
