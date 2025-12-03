package com.symptomcheck.clinicservice;

import com.symptomcheck.clinicservice.dtos.admindashboarddto.AdminClinicDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
@Import(SecurityTestConfig.class)
class ClinicserviceApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test")
            .withReuse(true);

    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.jpa.properties.hibernate.dialect", () -> "org.hibernate.dialect.PostgreSQLDialect");
        registry.add("spring.sql.init.mode", () -> "always");
    }

    @Test
    void contextLoads() {
        // This will verify that the application context loads successfully
    }

    private AdminClinicDto adminClinicDto;

    @BeforeEach
    void setUp() {
        adminClinicDto = new AdminClinicDto();
        adminClinicDto.setId(1L);
        adminClinicDto.setName("City Medical Center");
        adminClinicDto.setAddress("123 Main Street");
        adminClinicDto.setPhone("+1-555-0123");
        adminClinicDto.setWebsiteUrl("https://citymedical.example.com");
        adminClinicDto.setCity("New York");
        adminClinicDto.setCountry("USA");
        adminClinicDto.setDoctorCount(15L);
        adminClinicDto.setAppointmentCount(45L);
    }

    @Nested
    class SecurityTests {

        @Test
        @WithMockUser(username = "user", roles = {"USER"})
        void whenUserRoleAccessAdminEndpoint_shouldReturnForbidden() throws Exception {
            mockMvc.perform(get("/api/admin/dashboard/stats")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(username = "user", roles = {"USER"})
        void whenUserRoleTriesToCreateClinic_shouldReturnForbidden() throws Exception {
            mockMvc.perform(post("/api/admin/clinics")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(adminClinicDto)))
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }

        @Test
        void whenUnauthenticatedAccess_shouldReturnUnauthorized() throws Exception {
            mockMvc.perform(get("/api/admin/dashboard/stats")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockUser(username = "user", roles = {"USER"})
        void whenUserRoleTriesToUpdateClinic_shouldReturnForbidden() throws Exception {
            mockMvc.perform(put("/api/admin/clinics/{clinicId}", 1L)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(adminClinicDto)))
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(username = "user", roles = {"USER"})
        void whenUserRoleTriesToDeleteClinic_shouldReturnForbidden() throws Exception {
            mockMvc.perform(delete("/api/admin/clinics/{clinicId}", 1L)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }
    }
}