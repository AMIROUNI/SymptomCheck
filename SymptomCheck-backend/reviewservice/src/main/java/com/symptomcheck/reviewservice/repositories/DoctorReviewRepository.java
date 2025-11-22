package com.symptomcheck.reviewservice.repositories;

import com.symptomcheck.reviewservice.models.DoctorReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorReviewRepository extends JpaRepository<DoctorReview, Long> {

    List<DoctorReview> findByDoctorId(String doctorId);
    List<DoctorReview> findByPatientId(String patientId);

    Optional<DoctorReview> findByPatientIdAndDoctorId(String patientId, String doctorId);

    boolean existsByPatientIdAndDoctorId(String patientId, String doctorId);

    @Query("SELECT AVG(dr.rating) FROM DoctorReview dr WHERE dr.doctorId = :doctorId")
    Double findAverageRatingByDoctorId(@Param("doctorId") String doctorId);

    @Query("SELECT COUNT(dr) FROM DoctorReview dr WHERE dr.doctorId = :doctorId")
    Long countByDoctorId(@Param("doctorId") String doctorId);

    @Query("SELECT dr.rating, COUNT(dr) FROM DoctorReview dr WHERE dr.doctorId = :doctorId GROUP BY dr.rating")
    List<Object[]> getRatingDistributionByDoctorId(@Param("doctorId") String doctorId);

    void deleteByDoctorId(String doctorId);
}