package com.symptomcheck.doctorservice.repositories;

import com.symptomcheck.doctorservice.models.DoctorAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DoctorAvailabilityRepository extends JpaRepository<DoctorAvailability, UUID> {
    @Query("""
        SELECT da FROM DoctorAvailability da
        WHERE da.doctorId = :doctorId
          AND da.dayOfWeek = :dayOfWeek
          AND :time BETWEEN da.startTime AND da.endTime
    """)
    Optional<DoctorAvailability> findIfAvailable(
            @Param("doctorId") UUID doctorId,
            @Param("dayOfWeek") DayOfWeek dayOfWeek,
            @Param("time") LocalTime time
    );


   public boolean existsByDoctorIdAndDayOfWeek(UUID doctorId, DayOfWeek dayOfWeek);
    boolean existsByDoctorId(UUID doctorId);



    List<DoctorAvailability> findByDoctorId(UUID doctorId);

    @Query("SELECT COUNT(da) FROM DoctorAvailability da WHERE da.doctorId = :doctorId")
    Long countByDoctorId(@Param("doctorId") UUID doctorId);


    Long countDoctorsWithAvailability();
}
