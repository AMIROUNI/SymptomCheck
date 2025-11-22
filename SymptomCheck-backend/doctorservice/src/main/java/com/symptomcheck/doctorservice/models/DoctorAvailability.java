package com.symptomcheck.doctorservice.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "doctor_availabilities")
@Data
public class DoctorAvailability {
    @Id
    @GeneratedValue
    private Long id;

    private UUID doctorId;
    @NotNull
    @ElementCollection
    @CollectionTable(name = "availability_days", joinColumns = @JoinColumn(name = "availability_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week")
    private List<DayOfWeek> daysOfWeek; // Liste de jours
    @NotNull
    private LocalTime startTime;
    @NotNull
    private LocalTime endTime;

}