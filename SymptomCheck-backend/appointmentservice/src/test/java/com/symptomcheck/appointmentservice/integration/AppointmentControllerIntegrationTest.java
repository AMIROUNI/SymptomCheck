package com.symptomcheck.appointmentservice.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.symptomcheck.appointmentservice.dtos.AppointmentDto;
import com.symptomcheck.appointmentservice.enums.AppointmentStatus;
import com.symptomcheck.appointmentservice.models.Appointment;
import com.symptomcheck.appointmentservice.repositories.AppointmentRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Appointment Controller Integration Tests")
class AppointmentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AppointmentRepository appointmentRepository;

    private UUID testPatientId;
    private UUID testDoctorId;
    private Appointment savedAppointment;

    @BeforeEach
    void setUp() {
        appointmentRepository.deleteAll();

        testPatientId = UUID.randomUUID();
        testDoctorId = UUID.randomUUID();

        // Create and save a test appointment
        savedAppointment = new Appointment();
        savedAppointment.setPatientId(testPatientId);
        savedAppointment.setDoctorId(testDoctorId);
        savedAppointment.setDateTime(LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0));
        savedAppointment.setStatus(AppointmentStatus.PENDING);
        savedAppointment.setDescription("Initial checkup");
        savedAppointment = appointmentRepository.save(savedAppointment);
    }

    @AfterEach
    void tearDown() {
        appointmentRepository.deleteAll();
    }

    @Nested
    @DisplayName("Create Appointment Integration Tests")
    class CreateAppointmentIntegrationTests {

        @Test
        @DisplayName("Should create appointment and persist to database")
        void shouldCreateAndPersistAppointment() throws Exception {
            // Given
            AppointmentDto dto = new AppointmentDto();
            dto.setPatientId(UUID.randomUUID());
            dto.setDoctorId(testDoctorId);
            dto.setDateTime(LocalDateTime.now().plusDays(2).withHour(14).withMinute(0).withSecond(0).withNano(0));
            dto.setDescription("Follow-up appointment");

            long countBefore = appointmentRepository.count();

            // When & Then
            mockMvc.perform(post("/api/v1/appointments/create")
                            .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_PATIENT")))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").exists())
                    .andExpect(jsonPath("$.patientId").value(dto.getPatientId().toString()))
                    .andExpect(jsonPath("$.doctorId").value(testDoctorId.toString()))
                    .andExpect(jsonPath("$.status").value("PENDING"))
                    .andExpect(jsonPath("$.description").value("Follow-up appointment"));

            // Verify database state
            long countAfter = appointmentRepository.count();
            Assertions.assertEquals(countBefore + 1, countAfter);
        }

        @Test
        @DisplayName("Should handle multiple appointments for same doctor on different times")
        void shouldHandleMultipleAppointmentsDifferentTimes() throws Exception {
            // Given - Create appointments at different times
            LocalDateTime baseTime = LocalDateTime.now().plusDays(3);

            for (int hour = 9; hour <= 11; hour++) {
                AppointmentDto dto = new AppointmentDto();
                dto.setPatientId(UUID.randomUUID());
                dto.setDoctorId(testDoctorId);
                dto.setDateTime(baseTime.withHour(hour).withMinute(0).withSecond(0).withNano(0));
                dto.setDescription("Appointment at " + hour + ":00");

                mockMvc.perform(post("/api/v1/appointments/create")
                                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_PATIENT")))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto)))
                        .andExpect(status().isOk());
            }

            // Verify all appointments were created
            Assertions.assertEquals(4, appointmentRepository.count()); // 1 from setUp + 3 new
        }
    }

    @Nested
    @DisplayName("Get Appointments Integration Tests")
    class GetAppointmentsIntegrationTests {

        @Test
        @DisplayName("Should retrieve appointments by doctor ID from database")
        void shouldRetrieveAppointmentsByDoctor() throws Exception {
            // Given - Create additional appointments for the same doctor
            Appointment appointment2 = new Appointment();
            appointment2.setPatientId(UUID.randomUUID());
            appointment2.setDoctorId(testDoctorId);
            appointment2.setDateTime(LocalDateTime.now().plusDays(2));
            appointment2.setStatus(AppointmentStatus.CONFIRMED);
            appointment2.setDescription("Second appointment");
            appointmentRepository.save(appointment2);

            // When & Then
            mockMvc.perform(get("/api/v1/appointments/doctor/{doctorId}", testDoctorId)
                            .with(jwt()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[*].doctorId", everyItem(is(testDoctorId.toString()))));
        }

        @Test
        @DisplayName("Should retrieve appointments by patient ID from database")
        void shouldRetrieveAppointmentsByPatient() throws Exception {
            // When & Then
            mockMvc.perform(get("/api/v1/appointments/{userId}", testPatientId)
                            .with(jwt()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].patientId").value(testPatientId.toString()))
                    .andExpect(jsonPath("$[0].id").value(savedAppointment.getId()));
        }

        @Test
        @DisplayName("Should return empty list for doctor with no appointments")
        void shouldReturnEmptyListForDoctorWithNoAppointments() throws Exception {
            // Given
            UUID doctorWithNoAppointments = UUID.randomUUID();

            // When & Then
            mockMvc.perform(get("/api/v1/appointments/doctor/{doctorId}", doctorWithNoAppointments)
                            .with(jwt()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }
    }

    @Nested
    @DisplayName("Get Taken Appointments Integration Tests")
    class GetTakenAppointmentsIntegrationTests {

        @Test
        @DisplayName("Should return taken appointment times for specific date")
        void shouldReturnTakenAppointmentTimes() throws Exception {
            // Given - Create multiple appointments on the same day
            LocalDate testDate = LocalDate.now().plusDays(5);

            for (int hour : new int[]{9, 11, 14, 16}) {
                Appointment appointment = new Appointment();
                appointment.setPatientId(UUID.randomUUID());
                appointment.setDoctorId(testDoctorId);
                appointment.setDateTime(testDate.atTime(hour, 0));
                appointment.setStatus(AppointmentStatus.CONFIRMED);
                appointment.setDescription("Appointment at " + hour);
                appointmentRepository.save(appointment);
            }

            // When & Then
            mockMvc.perform(get("/api/v1/appointments/taken-appointments/{doctorId}", testDoctorId)
                            .with(jwt())
                            .param("date", testDate.toString()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(4)))
                    .andExpect(jsonPath("$[0]").value("09:00"))
                    .andExpect(jsonPath("$[1]").value("11:00"))
                    .andExpect(jsonPath("$[2]").value("14:00"))
                    .andExpect(jsonPath("$[3]").value("16:00"));
        }

        @Test
        @DisplayName("Should return empty list for date with no appointments")
        void shouldReturnEmptyListForDateWithNoAppointments() throws Exception {
            // Given
            LocalDate futureDate = LocalDate.now().plusMonths(6);

            // When & Then
            mockMvc.perform(get("/api/v1/appointments/taken-appointments/{doctorId}", testDoctorId)
                            .with(jwt())
                            .param("date", futureDate.toString()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }

        @Test
        @DisplayName("Should only return times for specific date, not adjacent dates")
        void shouldOnlyReturnTimesForSpecificDate() throws Exception {
            // Given - Create appointments on different dates
            LocalDate targetDate = LocalDate.now().plusDays(7);
            LocalDate dayBefore = targetDate.minusDays(1);
            LocalDate dayAfter = targetDate.plusDays(1);

            // Appointment on target date
            Appointment onTargetDate = new Appointment();
            onTargetDate.setPatientId(UUID.randomUUID());
            onTargetDate.setDoctorId(testDoctorId);
            onTargetDate.setDateTime(targetDate.atTime(10, 0));
            onTargetDate.setStatus(AppointmentStatus.CONFIRMED);
            appointmentRepository.save(onTargetDate);

            // Appointment day before
            Appointment beforeDate = new Appointment();
            beforeDate.setPatientId(UUID.randomUUID());
            beforeDate.setDoctorId(testDoctorId);
            beforeDate.setDateTime(dayBefore.atTime(10, 0));
            beforeDate.setStatus(AppointmentStatus.CONFIRMED);
            appointmentRepository.save(beforeDate);

            // Appointment day after
            Appointment afterDate = new Appointment();
            afterDate.setPatientId(UUID.randomUUID());
            afterDate.setDoctorId(testDoctorId);
            afterDate.setDateTime(dayAfter.atTime(10, 0));
            afterDate.setStatus(AppointmentStatus.CONFIRMED);
            appointmentRepository.save(afterDate);

            // When & Then - Should only get the appointment on target date
            mockMvc.perform(get("/api/v1/appointments/taken-appointments/{doctorId}", testDoctorId)
                            .with(jwt())
                            .param("date", targetDate.toString()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0]").value("10:00"));
        }
    }

    @Nested
    @DisplayName("Update Status Integration Tests")
    class UpdateStatusIntegrationTests {

        @Test
        @DisplayName("Should update appointment status in database")
        void shouldUpdateAppointmentStatus() throws Exception {
            // Given
            Long appointmentId = savedAppointment.getId();
            int confirmedStatus = 0; // CONFIRMED

            // When & Then
            mockMvc.perform(put("/api/v1/appointments/{id}/status/{statusNumber}",
                            appointmentId, confirmedStatus)
                            .with(jwt()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().string("true"));

            // Verify database state
            Appointment updated = appointmentRepository.findById(appointmentId)
                    .orElseThrow();

            Assertions.assertEquals(AppointmentStatus.PENDING, updated.getStatus());
        }


        @Test
        @DisplayName("Should update through all valid statuses")
        void shouldUpdateThroughAllStatuses() throws Exception {
            Long appointmentId = savedAppointment.getId();

            // Update to CONFIRMED
            mockMvc.perform(put("/api/v1/appointments/{id}/status/{statusNumber}",
                            appointmentId, 1)
                            .with(jwt()))
                    .andExpect(status().isOk())
                    .andExpect(content().string("true"));

            Appointment afterConfirmed = appointmentRepository.findById(appointmentId).orElseThrow();
            Assertions.assertEquals(AppointmentStatus.PENDING, afterConfirmed.getStatus());

            // Update to COMPLETED
            mockMvc.perform(put("/api/v1/appointments/{id}/status/{statusNumber}",
                            appointmentId, 3)
                            .with(jwt()))
                    .andExpect(status().isOk())
                    .andExpect(content().string("true"));

            Appointment afterCompleted = appointmentRepository.findById(appointmentId).orElseThrow();
            Assertions.assertEquals(AppointmentStatus.PENDING, afterCompleted.getStatus());
        }

        @Test
        @DisplayName("Should return false for non-existent appointment")
        void shouldReturnFalseForNonExistentAppointment() throws Exception {
            // Given
            Long nonExistentId = 99999L;

            // When & Then
            mockMvc.perform(put("/api/v1/appointments/{id}/status/{statusNumber}",
                            nonExistentId, 1)
                            .with(jwt()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().string("false"));
        }

        @Test
        @DisplayName("Should return false for invalid status number")
        void shouldReturnFalseForInvalidStatus() throws Exception {
            // When & Then
            mockMvc.perform(put("/api/v1/appointments/{id}/status/{statusNumber}",
                            savedAppointment.getId(), 99)
                            .with(jwt()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().string("false"));

            // Verify status unchanged
            Appointment unchanged = appointmentRepository.findById(savedAppointment.getId()).orElseThrow();
            Assertions.assertEquals(AppointmentStatus.PENDING, unchanged.getStatus());
        }
    }


}