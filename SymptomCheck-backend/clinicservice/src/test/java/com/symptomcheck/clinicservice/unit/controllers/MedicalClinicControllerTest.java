package com.symptomcheck.clinicservice.unit.controllers;

import com.symptomcheck.clinicservice.controllers.MedicalClinicController;
import com.symptomcheck.clinicservice.dtos.MedicalClinicDto;
import com.symptomcheck.clinicservice.models.MedicalClinic;
import com.symptomcheck.clinicservice.services.MedicalClinicService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class MedicalClinicControllerTest {

    @InjectMocks
    private MedicalClinicController controller;

    @Mock
    private MedicalClinicService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateClinic_Success() {
        MedicalClinicDto dto = new MedicalClinicDto();
        dto.setName("Clinic A");

        MedicalClinic savedClinic = new MedicalClinic();
        savedClinic.setName("Clinic A");

        when(service.createClinic(dto)).thenReturn(savedClinic);

        ResponseEntity<?> response = controller.createClinic(dto);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Clinic A", ((MedicalClinic) response.getBody()).getName());
    }

    @Test
    void testCreateClinic_Exception() {
        MedicalClinicDto dto = new MedicalClinicDto();
        dto.setName("Clinic B");

        when(service.createClinic(dto)).thenThrow(new RuntimeException("DB error"));

        ResponseEntity<?> response = controller.createClinic(dto);
        assertEquals(500, response.getStatusCodeValue());
    }

    @Test
    void testGetAllClinics() {
        MedicalClinic c1 = new MedicalClinic();
        c1.setName("Clinic 1");
        MedicalClinic c2 = new MedicalClinic();
        c2.setName("Clinic 2");

        when(service.getAllClinics()).thenReturn(List.of(c1, c2));

        List<MedicalClinic> clinics = controller.getAllClinics();
        assertEquals(2, clinics.size());
        assertEquals("Clinic 1", clinics.get(0).getName());
    }

    @Test
    void testGetClinicById() {
        MedicalClinic clinic = new MedicalClinic();
        clinic.setName("Clinic X");

        when(service.getClinicById(1L)).thenReturn(clinic);

        MedicalClinic result = controller.getClinic(1L);
        assertEquals("Clinic X", result.getName());
    }

    @Test
    void testUpdateClinic() {
        MedicalClinic clinic = new MedicalClinic();
        clinic.setName("Updated Clinic");

        when(service.updateClinic(1L, clinic)).thenReturn(clinic);

        MedicalClinic result = controller.updateClinic(1L, clinic);
        assertEquals("Updated Clinic", result.getName());
    }

    @Test
    void testDeleteClinic() {
        doNothing().when(service).deleteClinic(1L);

        controller.deleteClinic(1L);

        verify(service, times(1)).deleteClinic(1L);
    }
}
