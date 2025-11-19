package com.symptomcheck.appointmentservice.repositories;

import com.symptomcheck.appointmentservice.enums.AppointmentStatus;
import com.symptomcheck.appointmentservice.models.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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







    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.doctorId = :doctorId AND a.status = :status")
    Long countByDoctorIdAndStatus(@Param("doctorId") UUID doctorId,
                                  @Param("status") AppointmentStatus status);

    @Query("SELECT a FROM Appointment a WHERE a.doctorId = :doctorId AND a.status = :status")
    List<Appointment> findByDoctorIdAndStatus(@Param("doctorId") UUID doctorId,
                                              @Param("status") AppointmentStatus status);

    @Query("SELECT a FROM Appointment a WHERE a.doctorId = :doctorId AND a.dateTime BETWEEN :startDate AND :endDate")
    List<Appointment> findByDoctorIdAndDateRange(@Param("doctorId") UUID doctorId,
                                                 @Param("startDate") LocalDateTime startDate,
                                                 @Param("endDate") LocalDateTime endDate);

    // CORRECTION : Méthode pour compter tous les rendez-vous d'un médecin
    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.doctorId = :doctorId")
    Long countByDoctorId(@Param("doctorId") UUID doctorId);

    @Query("SELECT a FROM Appointment a WHERE a.doctorId = :doctorId AND a.dateTime >= :startOfDay AND a.dateTime < :endOfDay")
    List<Appointment> findTodayAppointmentsByDoctorId(@Param("doctorId") UUID doctorId,
                                                      @Param("startOfDay") LocalDateTime startOfDay,
                                                      @Param("endOfDay") LocalDateTime endOfDay);
}
