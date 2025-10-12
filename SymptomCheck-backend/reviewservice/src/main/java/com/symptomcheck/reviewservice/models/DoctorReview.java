package com.symptomcheck.reviewservice.models;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.Instant;

@Entity
@Table(name = "doctor_reviews")
public class DoctorReview {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Long patientId;

    @NotNull
    private Long doctorId;

    @NotNull @Min(1) @Max(5)
    private Integer stars;

    @Column(length = 2000)
    private String comment;

    private Instant datePosted = Instant.now();

    // getters & setters...
}
