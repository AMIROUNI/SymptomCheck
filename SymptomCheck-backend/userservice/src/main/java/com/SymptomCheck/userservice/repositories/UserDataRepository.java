package com.SymptomCheck.userservice.repositories;

import com.SymptomCheck.userservice.models.UserData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface UserDataRepository extends JpaRepository<UserData,String> {

    // Find doctors (users with clinicId)
    List<UserData> findByClinicIdIsNotNull();

    // Find patients (users without clinicId and speciality)
    List<UserData> findByClinicIdIsNullAndSpecialityIsNull();

    // Find users with complete profiles
    List<UserData> findByProfileCompleteTrue();

    // Find users with incomplete profiles
    List<UserData> findByProfileCompleteFalse();

    // Count users created after a certain date
    Long countByCreatedAtAfter(Instant date);

    // Find users by speciality
    List<UserData> findBySpeciality(String speciality);

    // Find users created in date range
    List<UserData> findByCreatedAtBetween(Instant start, Instant end);
}
