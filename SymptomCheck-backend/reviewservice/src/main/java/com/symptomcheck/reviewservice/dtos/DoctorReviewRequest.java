package com.symptomcheck.reviewservice.dtos;

import jakarta.validation.constraints.*;
import lombok.*;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorReviewRequest {
    @NotBlank(message = "Doctor ID is required")
    private String doctorId; // Changed to String

    @NotNull(message = "Rating is required")
    @Min(1)
    @Max(5)
    private Integer rating;

    @NotBlank(message = "Comment cannot be empty")
    @Size(max = 2000)
    private String comment;
}