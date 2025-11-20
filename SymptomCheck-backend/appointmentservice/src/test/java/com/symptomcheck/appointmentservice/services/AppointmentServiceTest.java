package com.symptomcheck.appointmentservice.services;

import com.symptomcheck.appointmentservice.dtos.AppointmentDto;
import com.symptomcheck.appointmentservice.models.Appointment;
import com.symptomcheck.appointmentservice.repositories.AppointmentRepository;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AppointmentServiceTest {

    @Mock
    AppointmentRepository appointmentRepository;

    @Mock
    WebClient webClient;

    @InjectMocks
    AppointmentService appointmentService;

    AutoCloseable closeable;

    @BeforeEach
    void setup() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @DisplayName("make appointment ")
    @Nested
    class MakeAppointmentTest {

        @Test
        void shouldMakeAppointment() {
            // Arrange
            UUID doctorId = UUID.randomUUID();
            AppointmentDto dto = new AppointmentDto();
            dto.setDoctorId(doctorId);
            dto.setPatientId(UUID.randomUUID());
            dto.setDescription("Test desc");
            dto.setDateTime(LocalDateTime.now().plusDays(1));

            // Mock WebClient
            WebClient.RequestHeadersUriSpec request = mock(WebClient.RequestHeadersUriSpec.class);
            WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);

            when(webClient.get()).thenReturn(request);
            when(request.uri(anyString(), any(), anyString())).thenReturn(headersSpec);
            when(headersSpec.header(anyString(), anyString())).thenReturn(headersSpec);
            when(headersSpec.retrieve()).thenReturn(mock(WebClient.ResponseSpec.class));
            when(headersSpec.retrieve().bodyToMono(Boolean.class)).thenReturn(Mono.just(true));

            when(appointmentRepository.existsByDoctorIdAndDateTime(any(), any()))
                    .thenReturn(false);

            when(appointmentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            // Act
            Appointment appt = appointmentService.makeAppointment(dto, "token123");

            // Assert
            Assertions.assertNotNull(appt);
            Assertions.assertEquals(doctorId, appt.getDoctorId());
        }
    }

    @DisplayName("get by doctor")
    @Nested
    class GetByDoctorTest {

        @Test
        void shouldReturnAppointmentsByDoctor() {
            UUID doctorId = UUID.randomUUID();

            when(appointmentRepository.findByDoctorId(doctorId))
                    .thenReturn(List.of(new Appointment()));

            List<Appointment> list = appointmentService.getByDoctor(doctorId);

            Assertions.assertEquals(1, list.size());
        }
    }

    // -----------------------------------------------------------------------
    // INNER CLASS 3 : getByDoctorFromDoctorService()
    // -----------------------------------------------------------------------
    @Nested
    class GetByDoctorFromDoctorServiceTest {

        @Test
        void shouldReturnAppointmentsFromRemoteService() {
            UUID doctorId = UUID.randomUUID();

            WebClient.RequestHeadersUriSpec request = mock(WebClient.RequestHeadersUriSpec.class);
            WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);

            when(webClient.get()).thenReturn(request);
            when(request.uri(anyString())).thenReturn(headersSpec);
            when(headersSpec.header(anyString(), anyString())).thenReturn(headersSpec);
            when(headersSpec.retrieve()).thenReturn(mock(WebClient.ResponseSpec.class));
            when(headersSpec.retrieve().bodyToFlux(Appointment.class))
                    .thenReturn(Mono.just(new Appointment()).flux());

            List<Appointment> result =
                    appointmentService.getByDoctorFromDoctorService(doctorId, "token123");

            Assertions.assertEquals(1, result.size());
        }
    }

    // -----------------------------------------------------------------------
    // INNER CLASS 4 : isDoctorAvailable()
    // -----------------------------------------------------------------------
    @Nested
    class IsDoctorAvailableTest {

        @Test
        void shouldReturnTrueWhenDoctorIsAvailable() {
            UUID doctorId = UUID.randomUUID();
            LocalDateTime date = LocalDateTime.now();

            when(appointmentRepository.existsByDoctorIdAndDateTime(any(), any()))
                    .thenReturn(false);

            boolean available = appointmentService.isDoctorAvailable(doctorId, date);

            Assertions.assertTrue(available);
        }
    }

    // -----------------------------------------------------------------------
    // INNER CLASS 5 : getAvailableDate()
    // -----------------------------------------------------------------------
    @Nested
    class GetAvailableDateTest {

        @Test
        void shouldReturnAvailableDates() {
            UUID doctorId = UUID.randomUUID();
            LocalDateTime dt = LocalDateTime.now();

            when(appointmentRepository.getDateTimeByDoctorId(doctorId))
                    .thenReturn(List.of(dt));

            List<LocalDateTime> result = appointmentService.getAvailableDate(doctorId);

            Assertions.assertEquals(1, result.size());
        }
    }
}
