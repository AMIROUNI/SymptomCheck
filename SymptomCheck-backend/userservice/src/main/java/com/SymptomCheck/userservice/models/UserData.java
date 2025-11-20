package com.SymptomCheck.userservice.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
@Data
@NoArgsConstructor
@Entity
@Table(name = "user_data")
public class UserData {

    @Id
    @Column(name = "id", length = 36, nullable = false)
    private String id;

    private String phoneNumber;
    private String profilePhotoUrl;

    @Column(name = "is_profile_complete", nullable = false)
    private Boolean profileComplete = false;

    private Long clinicId;

    @Column(name = "created_at")
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at")
    private Instant updatedAt;

    private String speciality;
    private String description;
    private String diploma;
}
