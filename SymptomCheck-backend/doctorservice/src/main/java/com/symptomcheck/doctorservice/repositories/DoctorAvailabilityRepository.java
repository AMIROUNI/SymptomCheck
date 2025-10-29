package com.symptomcheck.doctorservice.repositories;

import com.symptomcheck.doctorservice.models.DoctorAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Optional;

@Repository
public interface DoctorAvailabilityRepository extends JpaRepository<DoctorAvailability, Integer> {
    @Query("""
        SELECT da FROM DoctorAvailability da
        WHERE da.doctorId = :doctorId
          AND da.dayOfWeek = :dayOfWeek
          AND :time BETWEEN da.startTime AND da.endTime
    """)
    Optional<DoctorAvailability> findIfAvailable(
            @Param("doctorId") Long doctorId,
            @Param("dayOfWeek") DayOfWeek dayOfWeek,
            @Param("time") LocalTime time
    );


    public boolean existsByDoctorIdAndDayOfWeek(Long  doctorId, DayOfWeek dayOfWeek);
}
