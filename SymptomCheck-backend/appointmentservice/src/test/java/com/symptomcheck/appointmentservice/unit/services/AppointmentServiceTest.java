package com.symptomcheck.appointmentservice.unit.services;

import com.symptomcheck.appointmentservice.dtos.AppointmentDto;
import com.symptomcheck.appointmentservice.enums.AppointmentStatus;
import com.symptomcheck.appointmentservice.models.Appointment;
import com.symptomcheck.appointmentservice.repositories.AppointmentRepository;
import com.symptomcheck.appointmentservice.services.AppointmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private AppointmentService appointmentService;

    @Nested
    class UpdateAppointmentStatus {

        private final Long VALID_ID = 1L;
        private final Long INVALID_ID = 999L;

        @Test
        void shouldUpdateStatusSuccessfully() {
            // Arrange
            int statusNumber = 1; // Assuming this corresponds to a valid status
            when(appointmentRepository.updateAppointmentStatus(VALID_ID, AppointmentStatus.values()[statusNumber]))
                    .thenReturn(1);

            // Act & Assert
            assertDoesNotThrow(() -> appointmentService.updateAppointmentStatus(VALID_ID, statusNumber));
            verify(appointmentRepository).updateAppointmentStatus(VALID_ID, AppointmentStatus.values()[statusNumber]);
        }

        @Test
        void shouldThrowIllegalArgumentExceptionWhenInvalidStatusNumber() {
            // Arrange
            int invalidStatusNumber = 999;

            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> appointmentService.updateAppointmentStatus(VALID_ID, invalidStatusNumber));

            assertEquals("Invalid status number: " + invalidStatusNumber, exception.getMessage());
            verify(appointmentRepository, never()).updateAppointmentStatus(any(), any());
        }

        @Test
        void shouldThrowIllegalArgumentExceptionWhenAppointmentNotFound() {
            // Arrange
            int statusNumber = 1;
            when(appointmentRepository.updateAppointmentStatus(INVALID_ID, AppointmentStatus.values()[statusNumber]))
                    .thenReturn(0);

            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> appointmentService.updateAppointmentStatus(INVALID_ID, statusNumber));

            assertEquals("Appointment with ID " + INVALID_ID + " not found", exception.getMessage());
            verify(appointmentRepository).updateAppointmentStatus(INVALID_ID, AppointmentStatus.values()[statusNumber]);
        }

        @Test
        void shouldHandleNegativeStatusNumber() {
            // Arrange
            int negativeStatusNumber = -1;

            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> appointmentService.updateAppointmentStatus(VALID_ID, negativeStatusNumber));

            assertEquals("Invalid status number: " + negativeStatusNumber, exception.getMessage());
        }
    }

    @Nested
    class MakeAppointment {

        private final UUID VALID_DOCTOR_ID = UUID.randomUUID();
        private final UUID VALID_PATIENT_ID = UUID.randomUUID();
        private final String VALID_TOKEN = "valid-token";

        @Test
        void shouldCreateAppointmentSuccessfully() {
            // Arrange
            AppointmentDto dto = new AppointmentDto();
            dto.setDateTime(LocalDateTime.now());
            dto.setDescription("Test appointment");
            dto.setDoctorId(VALID_DOCTOR_ID);
            dto.setPatientId(VALID_PATIENT_ID);

            Appointment savedAppointment = new Appointment();
            savedAppointment.setId(1L);
            when(appointmentRepository.save(any(Appointment.class))).thenReturn(savedAppointment);

            // Act
            Appointment result = appointmentService.makeAppointment(dto, VALID_TOKEN);

            // Assert
            assertNotNull(result);
            verify(appointmentRepository).save(any(Appointment.class));
        }
    }

    @Nested
    class GetByDoctor {

        private final UUID VALID_DOCTOR_ID = UUID.randomUUID();

        @Test
        void shouldReturnAppointmentsForDoctor() {
            // Arrange
            List<Appointment> expectedAppointments = List.of(new Appointment(), new Appointment());
            when(appointmentRepository.findByDoctorId(VALID_DOCTOR_ID)).thenReturn(expectedAppointments);

            // Act
            List<Appointment> result = appointmentService.getByDoctor(VALID_DOCTOR_ID);

            // Assert
            assertEquals(expectedAppointments, result);
            verify(appointmentRepository).findByDoctorId(VALID_DOCTOR_ID);
        }

        @Test
        void shouldReturnEmptyListWhenNoAppointmentsFound() {
            // Arrange
            when(appointmentRepository.findByDoctorId(VALID_DOCTOR_ID)).thenReturn(List.of());

            // Act
            List<Appointment> result = appointmentService.getByDoctor(VALID_DOCTOR_ID);

            // Assert
            assertTrue(result.isEmpty());
            verify(appointmentRepository).findByDoctorId(VALID_DOCTOR_ID);
        }
    }

    @Nested
    class GetByDoctorFromDoctorService {

        private final UUID VALID_DOCTOR_ID = UUID.randomUUID();
        private final String VALID_TOKEN = "valid-token";


        @Nested
        class IsDoctorAvailable {

            private final UUID VALID_DOCTOR_ID = UUID.randomUUID();
            private final LocalDateTime VALID_DATE_TIME = LocalDateTime.now();

            @Test
            void shouldReturnTrueWhenDoctorIsAvailable() {
                // Arrange
                when(appointmentRepository.existsByDoctorIdAndDateTime(VALID_DOCTOR_ID, VALID_DATE_TIME))
                        .thenReturn(false);

                // Act
                boolean result = appointmentService.isDoctorAvailable(VALID_DOCTOR_ID, VALID_DATE_TIME);

                // Assert
                assertTrue(result);
                verify(appointmentRepository).existsByDoctorIdAndDateTime(VALID_DOCTOR_ID, VALID_DATE_TIME);
            }

            @Test
            void shouldReturnFalseWhenDoctorIsNotAvailable() {
                // Arrange
                when(appointmentRepository.existsByDoctorIdAndDateTime(VALID_DOCTOR_ID, VALID_DATE_TIME))
                        .thenReturn(true);

                // Act
                boolean result = appointmentService.isDoctorAvailable(VALID_DOCTOR_ID, VALID_DATE_TIME);

                // Assert
                assertFalse(result);
                verify(appointmentRepository).existsByDoctorIdAndDateTime(VALID_DOCTOR_ID, VALID_DATE_TIME);
            }
        }

        @Nested
        class GetAvailableDate {

            private final UUID VALID_DOCTOR_ID = UUID.randomUUID();

            @Test
            void shouldReturnAvailableDates() {
                // Arrange
                List<LocalDateTime> expectedDates = List.of(LocalDateTime.now(), LocalDateTime.now().plusDays(1));
                when(appointmentRepository.getDateTimeByDoctorId(VALID_DOCTOR_ID)).thenReturn(expectedDates);

                // Act
                List<LocalDateTime> result = appointmentService.getAvailableDate(VALID_DOCTOR_ID);

                // Assert
                assertEquals(expectedDates, result);
                verify(appointmentRepository).getDateTimeByDoctorId(VALID_DOCTOR_ID);
            }
        }

        @Nested
        class GetTakenAppointments {

            private final UUID VALID_DOCTOR_ID = UUID.randomUUID();
            private final LocalDate VALID_DATE = LocalDate.now();

            @Test
            void shouldReturnTakenAppointmentTimes() {
                // Arrange
                LocalDateTime startOfDay = VALID_DATE.atStartOfDay();
                LocalDateTime endOfDay = VALID_DATE.atTime(LocalTime.MAX);

                Appointment appointment1 = new Appointment();
                appointment1.setDateTime(startOfDay.plusHours(9)); // 9:00 AM
                Appointment appointment2 = new Appointment();
                appointment2.setDateTime(startOfDay.plusHours(14)); // 2:00 PM

                List<Appointment> appointments = List.of(appointment1, appointment2);
                when(appointmentRepository.findByDoctorIdAndDateTimeBetween(VALID_DOCTOR_ID, startOfDay, endOfDay))
                        .thenReturn(appointments);

                // Act
                List<String> result = appointmentService.getTakenAppointments(VALID_DOCTOR_ID, VALID_DATE);

                // Assert
                assertEquals(2, result.size());
                assertEquals("09:00", result.get(0));
                assertEquals("14:00", result.get(1));
                verify(appointmentRepository).findByDoctorIdAndDateTimeBetween(VALID_DOCTOR_ID, startOfDay, endOfDay);
            }

            @Test
            void shouldReturnEmptyListWhenNoAppointmentsForDate() {
                // Arrange
                LocalDateTime startOfDay = VALID_DATE.atStartOfDay();
                LocalDateTime endOfDay = VALID_DATE.atTime(LocalTime.MAX);

                when(appointmentRepository.findByDoctorIdAndDateTimeBetween(VALID_DOCTOR_ID, startOfDay, endOfDay))
                        .thenReturn(List.of());

                // Act
                List<String> result = appointmentService.getTakenAppointments(VALID_DOCTOR_ID, VALID_DATE);

                // Assert
                assertTrue(result.isEmpty());
                verify(appointmentRepository).findByDoctorIdAndDateTimeBetween(VALID_DOCTOR_ID, startOfDay, endOfDay);
            }
        }

        @Nested
        class GetByPatientId {

            private final UUID VALID_PATIENT_ID = UUID.randomUUID();

            @Test
            void shouldReturnAppointmentsForPatient() {
                // Arrange
                List<Appointment> expectedAppointments = List.of(new Appointment(), new Appointment());
                when(appointmentRepository.findByPatientId(VALID_PATIENT_ID)).thenReturn(expectedAppointments);

                // Act
                List<Appointment> result = appointmentService.getByPatientId(VALID_PATIENT_ID);

                // Assert
                assertEquals(expectedAppointments, result);
                verify(appointmentRepository).findByPatientId(VALID_PATIENT_ID);
            }
        }
    }}