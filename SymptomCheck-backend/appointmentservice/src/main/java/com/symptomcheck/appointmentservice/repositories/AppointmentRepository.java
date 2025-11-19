package com.symptomcheck.appointmentservice.repositories;

import com.symptomcheck.appointmentservice.models.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByDoctorId(UUID doctorId);

    boolean existsByDoctorIdAndDateTime(UUID doctorId, LocalDateTime dateTime);

    @Query("SELECT a.dateTime FROM Appointment a WHERE a.doctorId = :doctorId")
    List<LocalDateTime> getDateTimeByDoctorId(UUID doctorId);;
}
