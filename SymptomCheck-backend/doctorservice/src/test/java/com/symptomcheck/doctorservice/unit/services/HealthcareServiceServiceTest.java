package com.symptomcheck.doctorservice.unit.services;

import com.symptomcheck.doctorservice.models.HealthcareService;
import com.symptomcheck.doctorservice.repositories.HealthcareServiceRepository;
import com.symptomcheck.doctorservice.services.HealthcareServiceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@DisplayName("health care service test ")
@ExtendWith(MockitoExtension.class)
class HealthcareServiceServiceTest {

    @Mock
    private HealthcareServiceRepository healthcareServiceRepository;

    @InjectMocks
    private HealthcareServiceService healthcareServiceService;

    // Shared test objects
    private HealthcareService service1;
    private HealthcareService service2;
    private UUID doctorId;

    @BeforeEach
    void setUp() {
        doctorId = UUID.randomUUID();

        service1 = new HealthcareService();
        service1.setId(1L);
        service1.setName("Cardiology");
        service1.setDoctorId(doctorId);

        service2 = new HealthcareService();
        service2.setId(2L);
        service2.setName("Dermatology");
        service2.setDoctorId(doctorId);
    }

    @DisplayName("healthcare service existsByDoctorId ")
    @Nested
    class ExistsByDoctorIdTests {
        @DisplayName("healthcare service existsByDoctorId successfully")
        @Test

        void shouldReturnTrue_whenDoctorIdIsValid_andExists() {
            String doctorIdStr = doctorId.toString();
            when(healthcareServiceRepository.existsByDoctorId(doctorId))
                    .thenReturn(true);

            boolean result = healthcareServiceService.existsByDoctorId(doctorIdStr);

            assertTrue(result);
            verify(healthcareServiceRepository).existsByDoctorId(doctorId);
        }
        @DisplayName("healthcare service existsByDoctorId throw exeption")
        @Test
        void shouldReturnFalse_whenDoctorIdIsValid_butNotExists() {
            String doctorIdStr = doctorId.toString();
            when(healthcareServiceRepository.existsByDoctorId(doctorId))
                    .thenReturn(false);

            boolean result = healthcareServiceService.existsByDoctorId(doctorIdStr);

            assertFalse(result);
            verify(healthcareServiceRepository).existsByDoctorId(doctorId);
        }
        @DisplayName("healthcare service existsByDoctorId throw exeption")
        @Test
        void shouldReturnFalse_whenDoctorIdIsInvalidUUID() {
            String invalid = "INVALID_UUID";

            boolean result = healthcareServiceService.existsByDoctorId(invalid);

            assertFalse(result);
            verify(healthcareServiceRepository, never()).existsByDoctorId(any());
        }
    }
    @DisplayName("get all healthcare services ")
    @Nested
    class GetAllTests {
        @DisplayName("get all healthcare services success")
        @Test
        void shouldReturnAllHealthcareServices() {
            when(healthcareServiceRepository.findAll())
                    .thenReturn(List.of(service1, service2));

            List<HealthcareService> result = healthcareServiceService.getAll();

            assertEquals(2, result.size());
            assertEquals("Cardiology", result.get(0).getName());
            assertEquals("Dermatology", result.get(1).getName());
            verify(healthcareServiceRepository).findAll();
        }
        @DisplayName("get all healthcare services throw exeption")
        @Test
        void shouldReturnEmptyList_whenNoneExist() {
            when(healthcareServiceRepository.findAll())
                    .thenReturn(List.of());

            List<HealthcareService> result = healthcareServiceService.getAll();

            assertTrue(result.isEmpty());
            verify(healthcareServiceRepository).findAll();
        }
    }
    @DisplayName("TESTS FOR getHealthcareServiceByDoctorId ")
    @Nested
    class GetByDoctorIdTests {
        @DisplayName("TESTS FOR getHealthcareServiceByDoctorId success")
        @Test
        void shouldReturnServices_whenDoctorIdExists() {
            when(healthcareServiceRepository.findByDoctorId(doctorId))
                    .thenReturn(List.of(service1, service2));

            List<HealthcareService> result =
                    healthcareServiceService.getHealthcareServiceByDoctorId(doctorId);

            assertEquals(2, result.size());
            assertEquals("Cardiology", result.get(0).getName());
            verify(healthcareServiceRepository).findByDoctorId(doctorId);
        }
        @DisplayName("TESTS FOR getHealthcareServiceByDoctorId throw exeption")
        @Test
        void shouldReturnEmptyList_whenDoctorHasNoServices() {
            when(healthcareServiceRepository.findByDoctorId(doctorId))
                    .thenReturn(List.of());
            List<HealthcareService> result =
                    healthcareServiceService.getHealthcareServiceByDoctorId(doctorId);
            assertTrue(result.isEmpty());
            verify(healthcareServiceRepository).findByDoctorId(doctorId);
        }
    }
}
