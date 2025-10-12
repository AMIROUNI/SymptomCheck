package com.symptomcheck.reviewservice.services;

import com.symptomcheck.reviewservice.repositories.DoctorReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DoctorReviewService {

    private final DoctorReviewRepository doctorReviewRepository;
}
