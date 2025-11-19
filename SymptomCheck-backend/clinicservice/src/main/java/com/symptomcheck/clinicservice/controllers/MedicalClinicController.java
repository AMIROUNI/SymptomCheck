package com.symptomcheck.clinicservice.controllers;

import com.symptomcheck.clinicservice.dtos.MedicalClinicDto;
import com.symptomcheck.clinicservice.models.MedicalClinic;
import com.symptomcheck.clinicservice.services.MedicalClinicService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Slf4j
@RestController()
@RequestMapping("/api/v1/medical/clinic")
@RequiredArgsConstructor
public class MedicalClinicController {
    private final MedicalClinicService clinicService;


    // CREATE
    @PostMapping
    public ResponseEntity<?> createClinic(@RequestBody MedicalClinicDto clinic) {
        try {
            log.info("createClinic called");
            log.info(clinic.getName());
            return ResponseEntity.ok(clinicService.createClinic(clinic));
        }
        catch (Exception e) {
            log.info("Exception: ", e);
            return   ResponseEntity.internalServerError().build();

        }


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
