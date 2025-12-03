package com.symptomcheck.doctorservice.integration;

import com.symptomcheck.doctorservice.config.SecurityConfig;
import com.symptomcheck.doctorservice.controllers.DoctorProfileController;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.*;
@WebMvcTest(DoctorProfileController.class)
@Import(SecurityConfig.class)

class DoctorProfileControllerIntegrationTest {

}