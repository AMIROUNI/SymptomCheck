package com.symptomcheck.doctorservice.controllers;

import com.symptomcheck.doctorservice.models.DoctorProfile;
import com.symptomcheck.doctorservice.services.DoctorProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController()
@RequestMapping("/api/v1/doctor/profile")
@RequiredArgsConstructor
public class DoctorProfileController {
    private  final DoctorProfileService doctorProfileService;

    // ➕ Créer un profil médecin
    @PostMapping
    public ResponseEntity<DoctorProfile> createProfile(@RequestBody DoctorProfile profile) {
        return ResponseEntity.ok(doctorProfileService.saveDoctorProfile(profile));
    }

    // 📋 Lister tous les profils
    @GetMapping
    public ResponseEntity<List<DoctorProfile>> getAllProfiles() {
        return ResponseEntity.ok(doctorProfileService.getAllProfiles());
    }

    // 🔍 Récupérer un profil par ID
    @GetMapping("/{doctorId}")
    public ResponseEntity<DoctorProfile> getProfileById(@PathVariable Long doctorId) {
        return doctorProfileService.getProfileById(doctorId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ✏️ Mettre à jour un profil
    @PutMapping("/{doctorId}")
    public ResponseEntity<DoctorProfile> updateProfile(@PathVariable Long doctorId, @RequestBody DoctorProfile updatedProfile) {
        return doctorProfileService.getProfileById(doctorId)
                .map(existing -> {
                    updatedProfile.setDoctorId(doctorId);
                    return ResponseEntity.ok(doctorProfileService.saveDoctorProfile(updatedProfile));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // ❌ Supprimer un profil
    @DeleteMapping("/{doctorId}")
    public ResponseEntity<Void> deleteProfile(@PathVariable Long doctorId) {
        doctorProfileService.deleteProfile(doctorId);
        return ResponseEntity.noContent().build();
    }

}
