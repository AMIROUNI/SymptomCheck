package com.symptomcheck.appointmentservice.services;

import com.symptomcheck.appointmentservice.models.Appointment;
import com.symptomcheck.appointmentservice.repositories.AppointmentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.format.DateTimeFormatter;
import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final WebClient webClient;

    public Appointment makeAppointment(Appointment appointment) {
        // Format dateTime pour URL
        String formattedDate = appointment.getDateTime().
                format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        Boolean isAvailable = webClient.get()
                .uri("http://localhost:8082/api/v1/doctor/availability/isAvailable/{id}/{dateTime}",
                        appointment.getDoctorId(), formattedDate)
                .retrieve()
                .bodyToMono(Boolean.class)
                .onErrorResume(ex -> {
                    System.err.println("Error calling Doctor Service: " + ex.getMessage());
                    return Mono.just(false);
                })
                .block();

        if (Boolean.TRUE.equals(isAvailable)) {
            return appointmentRepository.save(appointment);
        } else {
            throw new IllegalArgumentException(" The doctor is not available at the selected date.");
        }
    }


    // méthode locale (si tu veux récupérer directement depuis ta base)
    public List<Appointment> getByDoctor(String doctorId) {
        return appointmentRepository.findByDoctorId(doctorId);
    }

    // méthode distante (si tu veux appeler un autre microservice)
    public List<Appointment> getByDoctorFromDoctorService(int doctorId, String token) {
        String doctorServiceUrl = "http://doctorservice:8082/api/doctors/" + doctorId + "/appointments";

        return webClient.get()
                .uri(doctorServiceUrl)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToFlux(Appointment.class)
                .collectList()
                .block(); // pour simplifier en synchrone
    }
}
