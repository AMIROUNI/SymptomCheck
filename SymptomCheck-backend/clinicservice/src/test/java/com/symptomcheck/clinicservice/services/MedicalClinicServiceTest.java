package com.symptomcheck.clinicservice.services;

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

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Medical clinic Unit Tests")
@ExtendWith(MockitoExtension.class)
class MedicalClinicServiceTest {

    @BeforeEach
    void setUp(){
        MedicalClinic clinic = new MedicalClinic();
        clinic.setName("City Clinic");
        clinic.setAddress("123 Main Street");
        clinic.setPhone("555-1234");
        clinic.setWebsiteUrl("www.cityclinic.com");
        clinic.setCity("Tunis");
        clinic.setCountry("Tunisia");
    }


    @Mock
    private MedicalClinicRepository repository;
    @InjectMocks
    private MedicalClinicService medicalClinicServiceMock ;




    @Nested
    @DisplayName("Create medical clinic tests ")

    class CreateMedicalClinicTests {
        @Test
        @DisplayName("should create a medical clinic successfully")
        void shouldCreateSuccessfully(){
            //Given


            //when
            final MedicalClinic result = MedicalClinicServiceTest.this.medicalClinicServiceMock.createClinic()

            //then
        }

    }

}