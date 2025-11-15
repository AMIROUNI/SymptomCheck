package com.symptomcheck.clinicservice.controllers;

import com.symptomcheck.clinicservice.models.MedicalClinic;
import com.symptomcheck.clinicservice.services.MedicalClinicService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController()
@RequestMapping("/api/v1/medical/clinic")
@RequiredArgsConstructor
public class MedicalClinicController {
    private final MedicalClinicService clinicService;


    // CREATE
    @PostMapping
    public MedicalClinic createClinic(@RequestBody MedicalClinic clinic) {
        return clinicService.createClinic(clinic);
    }

    // GET ALL
    @GetMapping
    public List<MedicalClinic> getAllClinics() {
        return clinicService.getAllClinics();
    }

    // GET BY ID
    @GetMapping("/{id}")
    public MedicalClinic getClinic(@PathVariable Long id) {
        return clinicService.getClinicById(id);
    }

    // UPDATE
    @PutMapping("/{id}")
    public MedicalClinic updateClinic(
            @PathVariable Long id,
            @RequestBody MedicalClinic clinic
    ) {
        return clinicService.updateClinic(id, clinic);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public void deleteClinic(@PathVariable Long id) {
        clinicService.deleteClinic(id);
    }


}
