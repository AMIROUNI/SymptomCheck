package com.symptomcheck.reviewservice.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "doctor_reviews")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Patient ID is required")
    @Column(name = "patient_id", nullable = false)
    private String patientId;

    @NotBlank(message = "Doctor ID is required")
    @Column(name = "doctor_id", nullable = false)
    private String doctorId;

    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    @Column(nullable = false)
    private Integer rating;

    @NotBlank(message = "Comment cannot be empty")
    @Size(max = 2000, message = "Comment must not exceed 2000 characters")
    @Column(length = 2000, nullable = false)
    private String comment;

    @Column(name = "date_posted", nullable = false)
    @Builder.Default
    private Instant datePosted = Instant.now();

    @Column(name = "last_updated")
    private Instant lastUpdated;

    @PrePersist
    protected void onCreate() {
        if (datePosted == null) {
            datePosted = Instant.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        lastUpdated = Instant.now();
    }
}