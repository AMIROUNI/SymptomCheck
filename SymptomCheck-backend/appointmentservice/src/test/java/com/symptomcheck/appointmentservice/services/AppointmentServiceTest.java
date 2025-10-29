package com.symptomcheck.appointmentservice.services;

import com.symptomcheck.appointmentservice.models.Appointment;
import com.symptomcheck.appointmentservice.repositories.AppointmentRepository;
import com.symptomcheck.appointmentservice.services.AppointmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AppointmentServiceTest {
    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private WebClient webClient; // WebClient mocké avec RETURNS_DEEP_STUBS

    @InjectMocks
    private AppointmentService appointmentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Permet de mocker tous les sous-objets (get().uri().retrieve().bodyToMono())
        webClient = mock(WebClient.class, RETURNS_DEEP_STUBS);
        appointmentService = new AppointmentService(appointmentRepository, webClient);
    }

    @Test
    void makeAppointment_doctorAvailable_shouldSaveAppointment() {
        Appointment appointment = new Appointment();
        appointment.setDoctorId(1L);
        appointment.setPatientId(10L);
        appointment.setDateTime(LocalDateTime.now().plusDays(1));

        // Mock simple grâce à RETURNS_DEEP_STUBS
        when(webClient.get()
                .uri(anyString(), any(), any())
                .retrieve()
                .bodyToMono(Boolean.class))
                .thenReturn(Mono.just(true));

        when(appointmentRepository.save(appointment)).thenReturn(appointment);

        Appointment result = appointmentService.makeAppointment(appointment);

        assertNotNull(result);
        verify(appointmentRepository, times(1)).save(appointment);
    }

    @Test
    void makeAppointment_doctorNotAvailable_shouldThrowException() {
        Appointment appointment = new Appointment();
        appointment.setDoctorId(1L);
        appointment.setPatientId(10L);
        appointment.setDateTime(LocalDateTime.now().plusDays(1));

        // Mock WebClient pour renvoyer false
        when(webClient.get()
                .uri(anyString(), any(), any())
                .retrieve()
                .bodyToMono(Boolean.class))
                .thenReturn(Mono.just(false));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> appointmentService.makeAppointment(appointment));

        assertEquals(" The doctor is not available at the selected date.", ex.getMessage());
        verify(appointmentRepository, never()).save(any());
    }
}
