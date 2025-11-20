package com.symptomcheck.doctorservice.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "doctor_availabilities")
@Data
public class DoctorAvailability {
    @Id
    @GeneratedValue
    private UUID id;

    private UUID doctorId;
    @NotNull
    private  DayOfWeek dayOfWeek;
    @NotNull
    private LocalTime startTime;
    @NotNull
    private LocalTime endTime;

}