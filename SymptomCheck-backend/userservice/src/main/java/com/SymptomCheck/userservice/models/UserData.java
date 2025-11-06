package com.SymptomCheck.userservice.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.Instant;


@Data
@Entity
public class UserData {
    @Id
    private  String id;
    private String phoneNumber;
    private String profilePhotoUrl;
    private boolean isProfileComplete = false;

    private Long clinicId;

    private Instant createdAt = Instant.now();
    private Instant updatedAt;
}
