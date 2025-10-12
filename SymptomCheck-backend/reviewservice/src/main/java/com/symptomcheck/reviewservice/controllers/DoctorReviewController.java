package com.symptomcheck.reviewservice.controllers;

import com.symptomcheck.reviewservice.repositories.DoctorReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/review")
@RequiredArgsConstructor
public class DoctorReviewController {
    private final DoctorReviewRepository doctorReviewRepository;
}
