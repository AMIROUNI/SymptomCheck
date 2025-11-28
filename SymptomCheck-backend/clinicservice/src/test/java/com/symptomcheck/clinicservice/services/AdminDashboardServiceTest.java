package com.symptomcheck.clinicservice.services;

import com.symptomcheck.clinicservice.dtos.adminDashboardDto.AdminClinicDto;
import com.symptomcheck.clinicservice.dtos.adminDashboardDto.ClinicStatsDto;
import com.symptomcheck.clinicservice.models.MedicalClinic;
import com.symptomcheck.clinicservice.repositories.MedicalClinicRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminDashboardServiceTest {

    @Mock
    private MedicalClinicRepository clinicRepository;

    @InjectMocks
    private AdminDashboardService adminService;

    private MedicalClinic clinic;
    private AdminClinicDto clinicDto;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        clinic = new MedicalClinic();
        clinic.setId(1L);
        clinic.setName("Healthy Life Clinic");
        clinic.setAddress("123 Main St");
        clinic.setPhone("+123456789");
        clinic.setWebsiteUrl("https://healthylife.com");
        clinic.setCity("New York");
        clinic.setCountry("USA");

        clinicDto = new AdminClinicDto();
        clinicDto.setName("Healthy Life Clinic");
        clinicDto.setAddress("123 Main St");
        clinicDto.setPhone("+123456789");
        clinicDto.setWebsiteUrl("https://healthylife.com");
        clinicDto.setCity("New York");
        clinicDto.setCountry("USA");




    }

    @Nested
    @DisplayName("getClinicStatistics method tests")
    class GetClinicStatisticsTests {

        @Test
        @DisplayName("should return clinic statistics")
        void testGetClinicStatistics() {

            // Mock count()
            when(clinicRepository.count()).thenReturn(3L);

            // Mock countClinicsByCity() correctly
            when(clinicRepository.countClinicsByCity())
                    .thenReturn(List.of(
                            new Object[]{"New York", 1L},
                            new Object[]{"Paris", 2L}
                    ));

            ClinicStatsDto stats = adminService.getClinicStatistics();

            assertEquals(3L, stats.getTotalClinics());
            assertEquals(2L, stats.getClinicsInEachCity()); // 2 distinct cities
        }

    }

    @Nested
    @DisplayName("getAllClinics method tests")
    class GetAllClinicsTests {

        @Test
        @DisplayName("should return all clinics")
        void testGetAllClinics() {
            when(clinicRepository.findAll()).thenReturn(List.of(clinic));

            List<AdminClinicDto> clinics = adminService.getAllClinics();

            assertEquals(1, clinics.size());
            AdminClinicDto dto = clinics.get(0);
            assertEquals(clinic.getName(), dto.getName());
            assertEquals(clinic.getCity(), dto.getCity());
        }
    }

    @Nested
    @DisplayName("getClinicsByCity method tests")
    class GetClinicsByCityTests {

        @Test
        @DisplayName("should filter clinics by city")
        void testGetClinicsByCity() {
            when(clinicRepository.findByCity("New York")).thenReturn(List.of(clinic));

            List<AdminClinicDto> clinics = adminService.getClinicsByCity("New York");

            assertEquals(1, clinics.size());
            assertEquals("New York", clinics.get(0).getCity());
        }
    }

    @Nested
    @DisplayName("createClinic method tests")
    class CreateClinicTests {

        @Test
        @DisplayName("should create a new clinic")
        void testCreateClinic() {
            when(clinicRepository.save(any(MedicalClinic.class))).thenReturn(clinic);

            AdminClinicDto savedClinic = adminService.createClinic(clinicDto);

            assertEquals(clinic.getName(), savedClinic.getName());
            assertEquals(clinic.getCity(), savedClinic.getCity());
        }
    }

    @Nested
    @DisplayName("updateClinic method tests")
    class UpdateClinicTests {

        @Test
        @DisplayName("should update an existing clinic")
        void testUpdateClinic() {
            when(clinicRepository.findById(1L)).thenReturn(Optional.of(clinic));
            when(clinicRepository.save(any(MedicalClinic.class))).thenReturn(clinic);

            AdminClinicDto updatedClinic = adminService.updateClinic(1L, clinicDto);

            assertEquals(clinic.getName(), updatedClinic.getName());
            assertEquals(clinic.getCity(), updatedClinic.getCity());
        }
    }

    @Nested
    @DisplayName("deleteClinic method tests")
    class DeleteClinicTests {

        @Test
        @DisplayName("should delete clinic by id")
        void testDeleteClinic() {
            doNothing().when(clinicRepository).deleteById(1L);

            adminService.deleteClinic(1L);

            verify(clinicRepository, times(1)).deleteById(1L);
        }
    }
}
