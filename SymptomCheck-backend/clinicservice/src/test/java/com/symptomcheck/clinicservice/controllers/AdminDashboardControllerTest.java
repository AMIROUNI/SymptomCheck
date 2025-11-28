package com.symptomcheck.clinicservice.controllers;

import com.symptomcheck.clinicservice.dtos.adminDashboardDto.AdminClinicDto;
import com.symptomcheck.clinicservice.dtos.adminDashboardDto.ClinicStatsDto;
import com.symptomcheck.clinicservice.services.AdminDashboardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminDashboardControllerTest {

    @InjectMocks
    private AdminDashboardController controller;

    @Mock
    private AdminDashboardService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetDashboardStats() {
        ClinicStatsDto stats = new ClinicStatsDto();
        stats.setTotalClinics(10L);
        stats.setClinicsWithDoctors(5L);
        stats.setClinicsInEachCity(3L);
        stats.setLastUpdated(LocalDateTime.now());

        when(service.getClinicStatistics()).thenReturn(stats);

        ResponseEntity<ClinicStatsDto> response = controller.getDashboardStats();
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(10L, response.getBody().getTotalClinics());
    }

    @Test
    void testGetAllClinics() {
        AdminClinicDto c1 = new AdminClinicDto();
        c1.setName("Clinic A");
        AdminClinicDto c2 = new AdminClinicDto();
        c2.setName("Clinic B");

        when(service.getAllClinics()).thenReturn(List.of(c1, c2));

        ResponseEntity<List<AdminClinicDto>> response = controller.getAllClinics();
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
        assertEquals("Clinic A", response.getBody().get(0).getName());
    }

    @Test
    void testGetClinicsByCity() {
        String city = "CityX";
        AdminClinicDto clinic = new AdminClinicDto();
        clinic.setCity(city);
        clinic.setName("Clinic X");

        when(service.getClinicsByCity(city)).thenReturn(List.of(clinic));

        ResponseEntity<List<AdminClinicDto>> response = controller.getClinicsByCity(city);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        assertEquals(city, response.getBody().get(0).getCity());
    }

    @Test
    void testCreateClinic() {
        AdminClinicDto dto = new AdminClinicDto();
        dto.setName("New Clinic");

        when(service.createClinic(dto)).thenReturn(dto);

        ResponseEntity<AdminClinicDto> response = controller.createClinic(dto);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("New Clinic", response.getBody().getName());
    }

    @Test
    void testUpdateClinic() {
        AdminClinicDto dto = new AdminClinicDto();
        dto.setName("Updated Clinic");

        when(service.updateClinic(1L, dto)).thenReturn(dto);

        ResponseEntity<AdminClinicDto> response = controller.updateClinic(1L, dto);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Updated Clinic", response.getBody().getName());
    }

    @Test
    void testDeleteClinic() {
        doNothing().when(service).deleteClinic(1L);

        ResponseEntity<Void> response = controller.deleteClinic(1L);
        assertEquals(200, response.getStatusCodeValue());

        verify(service, times(1)).deleteClinic(1L);
    }
}
