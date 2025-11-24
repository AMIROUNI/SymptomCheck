package com.symptomcheck.appointmentservice.services;

import com.symptomcheck.appointmentservice.dtos.AppointmentDto;
import com.symptomcheck.appointmentservice.models.Appointment;
import com.symptomcheck.appointmentservice.repositories.AppointmentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final WebClient webClient;

    public Appointment makeAppointment(AppointmentDto dto,String token) {

        log.info("start make appointment");
        // Format dateTime pour URL
        log.info("appointment dto: {}", dto.getDateTime());
        String formattedDate = dto.getDateTime()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
        log.info("formatted date: " + formattedDate);
        Appointment appointment = new Appointment();
        appointment.setDescription(dto.getDescription());
        appointment.setDoctorId(dto.getDoctorId());
        appointment.setPatientId(dto.getPatientId());
        appointment.setDateTime(dto.getDateTime());




            return appointmentRepository.save(appointment);



    }


    // méthode locale (si tu veux récupérer directement depuis ta base)
    public List<Appointment> getByDoctor(UUID doctorId) {

        return appointmentRepository.findByDoctorId(doctorId);
    }

    // méthode distante (si tu veux appeler un autre microservice)
    public List<Appointment> getByDoctorFromDoctorService(UUID doctorId, String token) {
        String doctorServiceUrl = "http://doctorservice:8082/api/doctors/" + doctorId + "/appointments";

        return webClient.get()
                .uri(doctorServiceUrl)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToFlux(Appointment.class)
                .collectList()
                .block(); // pour simplifier en synchrone
    }



    public  boolean isDoctorAvailable(UUID doctorId, LocalDateTime dateTime) {
        boolean exists= appointmentRepository.existsByDoctorIdAndDateTime(doctorId,dateTime);
        return  !exists;
    }


    public List<LocalDateTime> getAvailableDate(UUID doctorId) {

        return  appointmentRepository.getDateTimeByDoctorId(doctorId);

    }


    public List<String> getTakenAppointments(UUID doctorId, LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        List<Appointment> takenAppointments = appointmentRepository
                .findByDoctorIdAndDateTimeBetween(doctorId, startOfDay, endOfDay);

        return takenAppointments.stream()
                .map(a -> a.getDateTime().toLocalTime().toString()) // extract time only
                .sorted()
                .collect(Collectors.toList());
    }
}
