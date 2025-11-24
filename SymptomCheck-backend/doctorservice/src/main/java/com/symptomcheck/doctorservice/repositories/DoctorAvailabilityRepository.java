package com.symptomcheck.doctorservice.repositories;

import com.symptomcheck.doctorservice.models.DoctorAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DoctorAvailabilityRepository extends JpaRepository<DoctorAvailability, UUID> {

    // Cette méthode doit être adaptée pour la liste de jours
    @Query("""
        SELECT da FROM DoctorAvailability da
        WHERE da.doctorId = :doctorId
          AND :dayOfWeek MEMBER OF da.daysOfWeek
          AND :time BETWEEN da.startTime AND da.endTime
    """)
    Optional<DoctorAvailability> findIfAvailable(
            @Param("doctorId") UUID doctorId,
            @Param("dayOfWeek") DayOfWeek dayOfWeek,
            @Param("time") LocalTime time
    );

    // Remplacer l'ancienne méthode qui utilisait un seul jour
    @Query("SELECT COUNT(da) > 0 FROM DoctorAvailability da WHERE da.doctorId = :doctorId AND :dayOfWeek MEMBER OF da.daysOfWeek")
    boolean existsByDoctorIdAndDayOfWeek(@Param("doctorId") UUID doctorId, @Param("dayOfWeek") DayOfWeek dayOfWeek);

    boolean existsByDoctorId(UUID doctorId);
    List<DoctorAvailability> findByDoctorId(UUID doctorId);

    @Query("SELECT COUNT(da) FROM DoctorAvailability da WHERE da.doctorId = :doctorId")
    Long countByDoctorId(@Param("doctorId") UUID doctorId);

    @Query("SELECT COUNT(DISTINCT da.doctorId) FROM DoctorAvailability da")
    Long countDoctorsWithAvailability();

    @Modifying
    @Query("DELETE FROM DoctorAvailability da WHERE da.doctorId = :doctorId")
    void deleteByDoctorId(@Param("doctorId") UUID doctorId);
}