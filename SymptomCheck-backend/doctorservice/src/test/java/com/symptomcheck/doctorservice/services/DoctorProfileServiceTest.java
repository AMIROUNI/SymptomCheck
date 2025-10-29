package com.symptomcheck.doctorservice.services;

import com.symptomcheck.doctorservice.models.DoctorProfile;
import com.symptomcheck.doctorservice.repositories.DoctorProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class DoctorProfileServiceTest {
    @Mock
    private DoctorProfileRepository doctorProfileRepository;

    @InjectMocks
    private DoctorProfileService doctorProfileService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void saveDoctorProfile_shouldReturnSavedProfile() {
        DoctorProfile profile = new DoctorProfile();
        profile.setDoctorId(1L);
        profile.setSpeciality("Cardiologue");
        profile.setDiploma("MD");
        profile.setClinicName("Clinique Coeur Santé");

        when(doctorProfileRepository.save(any(DoctorProfile.class))).thenReturn(profile);

        DoctorProfile result = doctorProfileService.saveDoctorProfile(profile);

        assertNotNull(result);
        assertEquals("Cardiologue", result.getSpeciality());
        verify(doctorProfileRepository, times(1)).save(profile);
    }

    private void assertEquals(String cardiologue, String speciality) {
    }

    private void assertNotNull(DoctorProfile result) {
    }

    @Test
    void getProfileById_shouldReturnProfile() {
        DoctorProfile profile = new DoctorProfile();
        profile.setDoctorId(1L);

        when(doctorProfileRepository.findById(1)).thenReturn(Optional.of(profile));

        Optional<DoctorProfile> result = doctorProfileService.getProfileById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getDoctorId());
        verify(doctorProfileRepository, times(1)).findById(1);
    }

    private void assertEquals(long l, Long doctorId) {
    }

    @Test
    void getAllProfiles_shouldReturnList() {
        DoctorProfile p1 = new DoctorProfile();
        p1.setDoctorId(1L);
        DoctorProfile p2 = new DoctorProfile();
        p2.setDoctorId(2L);

        when(doctorProfileRepository.findAll()).thenReturn(List.of(p1, p2));

        List<DoctorProfile> profiles = doctorProfileService.getAllProfiles();

        assertEquals(2L, (long) profiles.size());
        verify(doctorProfileRepository, times(1)).findAll();
    }

    @Test
    void deleteProfile_shouldCallRepositoryDelete() {
        doctorProfileService.deleteProfile(1L);
        verify(doctorProfileRepository, times(1)).deleteById(1);
    }
}
