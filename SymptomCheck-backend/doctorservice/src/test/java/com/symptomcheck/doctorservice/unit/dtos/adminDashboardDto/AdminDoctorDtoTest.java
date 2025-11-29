package com.symptomcheck.doctorservice.unit.dtos.adminDashboardDto;

import com.symptomcheck.doctorservice.dtos.adminDashboardDto.AdminDoctorDto;
import com.symptomcheck.doctorservice.dtos.adminDashboardDto.AvailabilityDto;
import com.symptomcheck.doctorservice.dtos.adminDashboardDto.ServiceDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AdminDoctorDtoTest {

    private AdminDoctorDto adminDoctorDto;
    private ServiceDto serviceDto;
    private AvailabilityDto availabilityDto;
    private UUID doctorId;

    @BeforeEach
    void setUp() {
        doctorId = UUID.randomUUID();

        serviceDto = new ServiceDto();
        serviceDto.setId(1L);
        serviceDto.setName("General Checkup");
        serviceDto.setDescription("Basic health check");
        serviceDto.setCategory("General");
        serviceDto.setPrice(50.0);
        serviceDto.setDurationMinutes(30);

        availabilityDto = new AvailabilityDto();
        availabilityDto.setId(1L);
        availabilityDto.setDayOfWeek("MONDAY");
        availabilityDto.setStartTime("09:00");
        availabilityDto.setEndTime("17:00");

        adminDoctorDto = new AdminDoctorDto();
        adminDoctorDto.setDoctorId(doctorId);
        adminDoctorDto.setSpeciality("Cardiology");
        adminDoctorDto.setDescription("Heart specialist");
        adminDoctorDto.setStatus("APPROVED");
        adminDoctorDto.setRating(4.8);
        adminDoctorDto.setTotalReviews(120);
        adminDoctorDto.setServices(List.of(serviceDto));
        adminDoctorDto.setAvailabilities(List.of(availabilityDto));
    }

    @Test
    void testGettersAndSetters() {
        assertEquals(doctorId, adminDoctorDto.getDoctorId());
        assertEquals("Cardiology", adminDoctorDto.getSpeciality());
        assertEquals("Heart specialist", adminDoctorDto.getDescription());
        assertEquals("APPROVED", adminDoctorDto.getStatus());
        assertEquals(4.8, adminDoctorDto.getRating());
        assertEquals(120, adminDoctorDto.getTotalReviews());

        assertNotNull(adminDoctorDto.getServices());
        assertEquals(1, adminDoctorDto.getServices().size());
        assertEquals("General Checkup", adminDoctorDto.getServices().get(0).getName());

        assertNotNull(adminDoctorDto.getAvailabilities());
        assertEquals(1, adminDoctorDto.getAvailabilities().size());
        assertEquals("MONDAY", adminDoctorDto.getAvailabilities().get(0).getDayOfWeek());
    }

    @Test
    void testToString() {
        String str = adminDoctorDto.toString();
        assertNotNull(str);
        assertTrue(str.contains("speciality=Cardiology"));
        assertTrue(str.contains("status=APPROVED"));
        assertTrue(str.contains("services=[ServiceDto"));
        assertTrue(str.contains("availabilities=[AvailabilityDto"));
    }

    @Test
    void testEqualsAndHashCode() {
        AdminDoctorDto another = new AdminDoctorDto();
        another.setDoctorId(doctorId);
        another.setSpeciality("Cardiology");
        another.setDescription("Heart specialist");
        another.setStatus("APPROVED");
        another.setRating(4.8);
        another.setTotalReviews(120);
        another.setServices(List.of(serviceDto));
        another.setAvailabilities(List.of(availabilityDto));

        assertEquals(adminDoctorDto, another);
        assertEquals(adminDoctorDto.hashCode(), another.hashCode());
    }
}
