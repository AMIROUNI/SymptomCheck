package com.symptomcheck.reviewservice.repositories;

import com.symptomcheck.reviewservice.models.DoctorReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DoctorReviewRepository extends JpaRepository<DoctorReview, Integer> {
}
