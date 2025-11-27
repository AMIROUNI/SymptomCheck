package com.symptomcheck.appointmentservice.repositories;

import com.symptomcheck.appointmentservice.enums.AppointmentStatus;
import com.symptomcheck.appointmentservice.models.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {


    boolean existsByDoctorIdAndDateTime(UUID doctorId, LocalDateTime dateTime);

    @Query("SELECT a.dateTime FROM Appointment a WHERE a.doctorId = :doctorId")
    List<LocalDateTime> getDateTimeByDoctorId(UUID doctorId);

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

    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.doctorId = :doctorId")
    Long countByDoctorId(@Param("doctorId") UUID doctorId);

    @Query("SELECT a FROM Appointment a WHERE a.doctorId = :doctorId AND a.dateTime >= :startOfDay AND a.dateTime < :endOfDay")
    List<Appointment> findTodayAppointmentsByDoctorId(@Param("doctorId") UUID doctorId,
                                                      @Param("startOfDay") LocalDateTime startOfDay,
                                                      @Param("endOfDay") LocalDateTime endOfDay);

    List<Appointment> findByStatus(AppointmentStatus status);

    List<Appointment> findByDateTimeBetween(LocalDateTime start, LocalDateTime end);

    // FIX: Change parameter type to UUID to match your entity
    List<Appointment> findByDoctorId(UUID doctorId);

    List<Appointment> findByPatientId(UUID patientId);

    Long countByStatus(AppointmentStatus status);

    // FIXED: Today's appointments count
    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.dateTime >= :startOfDay AND a.dateTime < :endOfDay")
    Long countTodayAppointments(@Param("startOfDay") LocalDateTime startOfDay,
                                @Param("endOfDay") LocalDateTime endOfDay);

    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.dateTime BETWEEN :start AND :end")
    Long countByDateTimeBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COUNT(a) FROM Appointment a ")
    Long countByDateTimeBetween();

    List<Appointment> findByCreatedAtBetween(Instant start, Instant end);

    @Query("SELECT a.status, COUNT(a) FROM Appointment a GROUP BY a.status")
    List<Object[]> countAppointmentsByStatus();

    // Alternative method using CAST (if you prefer this approach)
    @Query("SELECT COUNT(a) FROM Appointment a WHERE CAST(a.dateTime AS date) = CURRENT_DATE")
    Long countTodayAppointmentsAlternative();

    List<Appointment> findByDoctorIdAndDateTimeBetween(UUID doctorId,
                                                       LocalDateTime startOfDay,
                                                       LocalDateTime endOfDay);

    @Modifying
    @Query("UPDATE Appointment a SET a.status = :status, a.updatedAt = CURRENT_TIMESTAMP WHERE a.id = :id")
    int updateAppointmentStatus(@Param("id") Long id,
                                @Param("status") AppointmentStatus status);



}