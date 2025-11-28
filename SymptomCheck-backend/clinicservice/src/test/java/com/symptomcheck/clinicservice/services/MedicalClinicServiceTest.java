package com.symptomcheck.clinicservice.services;

import com.symptomcheck.clinicservice.dtos.MedicalClinicDto;
import com.symptomcheck.clinicservice.models.MedicalClinic;
import com.symptomcheck.clinicservice.repositories.MedicalClinicRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Medical clinic Unit Tests")
@ExtendWith(MockitoExtension.class)
class MedicalClinicServiceTest {

    private MedicalClinicDto medicalClinicDto;
    private MedicalClinic savedClinic;

    @Mock
    private MedicalClinicRepository repository;

    @InjectMocks
    private MedicalClinicService medicalClinicServiceMock;

    @BeforeEach
    void setUp() {
        medicalClinicDto = new MedicalClinicDto();
        medicalClinicDto.setName("City Clinic");
        medicalClinicDto.setAddress("123 Main Street");
        medicalClinicDto.setPhone("555-1234");
        medicalClinicDto.setWebsiteUrl("www.cityclinic.com");
        medicalClinicDto.setCity("Tunis");
        medicalClinicDto.setCountry("Tunisia");

        savedClinic = new MedicalClinic();
        savedClinic.setId(1L);
        savedClinic.setName("City Clinic");
        savedClinic.setAddress("123 Main Street");
        savedClinic.setPhone("555-1234");
        savedClinic.setWebsiteUrl("www.cityclinic.com");
        savedClinic.setCity("Tunis");
        savedClinic.setCountry("Tunisia");
    }

    @Nested
    @DisplayName("Create medical clinic tests")
    class CreateMedicalClinicTests {

        @Test
        @DisplayName("should create a medical clinic successfully")
        void shouldCreateSuccessfully() {
            when(repository.save(any(MedicalClinic.class)))
                    .thenReturn(savedClinic);

            MedicalClinic result = medicalClinicServiceMock.createClinic(medicalClinicDto);

            assertNotNull(result);
            assertEquals(1L, result.getId());
            assertEquals("City Clinic", result.getName());
            assertEquals("Tunis", result.getCity());

            verify(repository).save(any(MedicalClinic.class));
        }

        @Test
        @DisplayName("should throw an exception when name is missing")
        void shouldThrowExceptionWhenNameIsMissing() {
            medicalClinicDto.setName(null);
            assertThrows(IllegalArgumentException.class, () ->
                    medicalClinicServiceMock.createClinic(medicalClinicDto)
            );
            verify(repository, never()).save(any(MedicalClinic.class));
        }

        @Test
        @DisplayName("should throw exception when name is blank")
        void shouldThrowExceptionWhenNameIsBlank() {
            medicalClinicDto.setName("   ");
            assertThrows(IllegalArgumentException.class, () ->
                    medicalClinicServiceMock.createClinic(medicalClinicDto)
            );
            verify(repository, never()).save(any(MedicalClinic.class));
        }
    }

    @Nested
    @DisplayName("Find by ID medical clinic tests")
    class FindByIdMedicalClinicTests {

        @Test
        @DisplayName("should return a clinic when ID exists")
        void shouldReturnClinicWhenIdExists() {
            when(repository.findById(1L)).thenReturn(Optional.of(savedClinic));

            MedicalClinic result = medicalClinicServiceMock.getClinicById(1L);

            assertNotNull(result);
            assertEquals(savedClinic.getId(), result.getId());
            assertEquals(savedClinic.getName(), result.getName());

            verify(repository).findById(1L);
        }

        @Test
        @DisplayName("should throw exception when ID does not exist")
        void shouldThrowExceptionWhenIdNotExists() {
            when(repository.findById(2L)).thenReturn(Optional.empty());

            RuntimeException exception = assertThrows(RuntimeException.class, () ->
                    medicalClinicServiceMock.getClinicById(2L)
            );

            assertEquals("Clinic not found with id: 2", exception.getMessage());
            verify(repository).findById(2L);
        }


    }

    @Nested
    @DisplayName("Update clinic tests")
    class UpdateClinicTests {

        @Test
        @DisplayName("should update a clinic successfully when ID exists")
        void shouldUpdateClinicSuccessfully() {
            when(repository.findById(1L)).thenReturn(Optional.of(savedClinic));

            MedicalClinic updatedData = new MedicalClinic();
            updatedData.setName("Updated Clinic");
            updatedData.setAddress("456 New Street");
            updatedData.setPhone("555-9999");
            updatedData.setWebsiteUrl("www.updatedclinic.com");
            updatedData.setCity("Sfax");
            updatedData.setCountry("Tunisia");

            MedicalClinic updatedClinic = new MedicalClinic();
            updatedClinic.setId(1L);
            updatedClinic.setName("Updated Clinic");
            updatedClinic.setAddress("456 New Street");
            updatedClinic.setPhone("555-9999");
            updatedClinic.setWebsiteUrl("www.updatedclinic.com");
            updatedClinic.setCity("Sfax");
            updatedClinic.setCountry("Tunisia");

            when(repository.save(any(MedicalClinic.class))).thenReturn(updatedClinic);

            MedicalClinic result = medicalClinicServiceMock.updateClinic(1L, updatedData);

            assertNotNull(result);
            assertEquals(1L, result.getId());
            assertEquals("Updated Clinic", result.getName());
            assertEquals("456 New Street", result.getAddress());
            assertEquals("555-9999", result.getPhone());
            assertEquals("Sfax", result.getCity());
            assertEquals("Tunisia", result.getCountry());

            verify(repository).findById(1L);
            verify(repository).save(any(MedicalClinic.class));
        }

        @Test
        @DisplayName("should throw exception when updating a non-existing clinic")
        void shouldThrowExceptionWhenIdNotExists() {
            when(repository.findById(2L)).thenReturn(Optional.empty());

            MedicalClinic updatedData = new MedicalClinic();
            updatedData.setName("Updated Clinic");

            RuntimeException exception = assertThrows(RuntimeException.class, () ->
                    medicalClinicServiceMock.updateClinic(2L, updatedData)
            );

            assertEquals("Clinic not found with id: 2", exception.getMessage());

            verify(repository).findById(2L);
            verify(repository, never()).save(any(MedicalClinic.class));
        }


    }

    @Nested
    @DisplayName("Get all medical clinic tests")
    class GetAllMedicalClinicTests {
        @Test
        @DisplayName("should return all clinics when list is not empty")
        void shouldReturnAllClinics() {
            when(repository.findAll()).thenReturn(List.of(savedClinic));

            List<MedicalClinic> result = medicalClinicServiceMock.getAllClinics();

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(savedClinic.getName(), result.get(0).getName());
        }

        @Test
        @DisplayName("should throw exception when no clinics are found")
        void shouldThrowExceptionWhenNoClinics() {
            when(repository.findAll()).thenReturn(List.of());

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> medicalClinicServiceMock.getAllClinics());

            assertEquals("No clinics found in the database.", exception.getMessage());
        }

        @Test
        @DisplayName("should return multiple clinics")
        void shouldReturnMultipleClinics() {
            MedicalClinic c2 = new MedicalClinic();
            c2.setId(2L);
            c2.setName("Clinic Two");

            when(repository.findAll()).thenReturn(List.of(savedClinic, c2));

            List<MedicalClinic> result = medicalClinicServiceMock.getAllClinics();
            assertEquals(2, result.size());
        }
    }

    @Nested
    @DisplayName("Delete clinic tests")
    class DeleteClinicTests {

        @Test
        @DisplayName("should delete a clinic successfully when ID exists")
        void shouldDeleteClinicSuccessfully() {
            when(repository.findById(1L)).thenReturn(Optional.of(savedClinic));

            medicalClinicServiceMock.deleteClinic(1L);

            verify(repository).findById(1L);
            verify(repository).deleteById(1L);
        }

        @Test
        @DisplayName("should throw exception when deleting non-existing clinic")
        void shouldThrowExceptionWhenIdNotExists() {
            when(repository.findById(2L)).thenReturn(Optional.empty());

            RuntimeException exception = assertThrows(RuntimeException.class, () ->
                    medicalClinicServiceMock.deleteClinic(2L)
            );

            assertEquals("Clinic not found with id: 2", exception.getMessage());

            verify(repository).findById(2L);
            verify(repository, never()).deleteById(anyLong());
        }

        @Test
        @DisplayName("should throw exception if deleteById fails")
        void shouldThrowExceptionWhenDeleteFails() {
            when(repository.findById(1L)).thenReturn(Optional.of(savedClinic));
            doThrow(new RuntimeException("DB error")).when(repository).deleteById(1L);

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> medicalClinicServiceMock.deleteClinic(1L));

            assertEquals("DB error", ex.getMessage());
        }
    }
}
