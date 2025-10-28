package com.symptomcheck.appointmentservice.repositories;

import com.symptomcheck.appointmentservice.models.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
}
