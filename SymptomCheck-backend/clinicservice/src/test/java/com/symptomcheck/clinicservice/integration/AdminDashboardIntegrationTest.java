    package com.symptomcheck.clinicservice.integration;

    import com.fasterxml.jackson.databind.ObjectMapper;
    import com.symptomcheck.clinicservice.config.SecurityConfig;
    import com.symptomcheck.clinicservice.dtos.admindashboarddto.AdminClinicDto;
    import com.symptomcheck.clinicservice.models.MedicalClinic;
    import com.symptomcheck.clinicservice.repositories.MedicalClinicRepository;
    import org.junit.jupiter.api.BeforeEach;
    import org.junit.jupiter.api.Test;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
    import org.springframework.boot.test.context.SpringBootTest;
    import org.springframework.context.annotation.Import;
    import org.springframework.http.MediaType;
    import org.springframework.security.test.context.support.WithMockUser;
    import org.springframework.test.context.DynamicPropertyRegistry;
    import org.springframework.test.context.DynamicPropertySource;
    import org.springframework.test.web.servlet.MockMvc;
    import org.springframework.transaction.annotation.Transactional;
    import org.testcontainers.containers.PostgreSQLContainer;
    import org.testcontainers.junit.jupiter.Container;
    import org.testcontainers.junit.jupiter.Testcontainers;

    import static org.assertj.core.api.Assertions.assertThat;
    import static org.hamcrest.Matchers.hasSize;
    import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
    import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

    @SpringBootTest
    @AutoConfigureMockMvc
    @Testcontainers
    @Transactional
    @Import(SecurityConfig.class)
    class AdminDashboardIntegrationTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @Autowired
        private MedicalClinicRepository medicalClinicRepository;


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

        @BeforeEach
        void setUp() {
            medicalClinicRepository.deleteAll();
        }

        @Test
        @WithMockUser(username = "admin", roles = {"ADMIN"})
        void getDashboardStats_ShouldReturnStatistics() throws Exception {
            // Given - Create some clinics
            MedicalClinic clinic1 = new MedicalClinic();
            clinic1.setName("Clinic 1");
            clinic1.setCity("City A");
            clinic1.setCountry("Country A");
            medicalClinicRepository.save(clinic1);

            MedicalClinic clinic2 = new MedicalClinic();
            clinic2.setName("Clinic 2");
            clinic2.setCity("City B");
            clinic2.setCountry("Country B");
            medicalClinicRepository.save(clinic2);

            // When & Then
            mockMvc.perform(get("/api/admin/dashboard/stats"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalClinics").value(2))
                    .andExpect(jsonPath("$.clinicsWithDoctors").value(2))
                    .andExpect(jsonPath("$.lastUpdated").exists());
        }

        @Test
        @WithMockUser(username = "admin", roles = {"ADMIN"})
        void getAllClinics_ShouldReturnAllClinics() throws Exception {
            // Given
            MedicalClinic clinic1 = new MedicalClinic();
            clinic1.setName("Clinic 1");
            clinic1.setCity("City A");
            medicalClinicRepository.save(clinic1);

            MedicalClinic clinic2 = new MedicalClinic();
            clinic2.setName("Clinic 2");
            clinic2.setCity("City B");
            medicalClinicRepository.save(clinic2);

            // When & Then
            mockMvc.perform(get("/api/admin/clinics"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$").value(hasSize(2)))
                    .andExpect(jsonPath("$[0].name").value("Clinic 1"))
                    .andExpect(jsonPath("$[1].name").value("Clinic 2"));
        }

        @Test
        @WithMockUser(username = "admin", roles = {"ADMIN"})
        void getClinicsByCity_ShouldReturnClinicsInCity() throws Exception {
            // Given
            MedicalClinic clinic1 = new MedicalClinic();
            clinic1.setName("Clinic 1");
            clinic1.setCity("New York");
            medicalClinicRepository.save(clinic1);

            MedicalClinic clinic2 = new MedicalClinic();
            clinic2.setName("Clinic 2");
            clinic2.setCity("Los Angeles");
            medicalClinicRepository.save(clinic2);

            MedicalClinic clinic3 = new MedicalClinic();
            clinic3.setName("Clinic 3");
            clinic3.setCity("New York");
            medicalClinicRepository.save(clinic3);

            // When & Then
            mockMvc.perform(get("/api/admin/clinics/city/New York"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$").value(hasSize(2)))
                    .andExpect(jsonPath("$[0].city").value("New York"))
                    .andExpect(jsonPath("$[1].city").value("New York"));
        }

        @Test
        @WithMockUser(username = "admin", roles = {"ADMIN"})
        void createClinic_ShouldCreateNewClinic() throws Exception {
            // Given
            AdminClinicDto clinicDto = new AdminClinicDto();
            clinicDto.setName("Test Clinic");
            clinicDto.setAddress("123 Test St");
            clinicDto.setCity("Test City");
            clinicDto.setCountry("Test Country");
            clinicDto.setPhone("123-456-7890");
            clinicDto.setWebsiteUrl("https://testclinic.com");

            // When & Then
            mockMvc.perform(post("/api/admin/clinics")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(clinicDto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("Test Clinic"))
                    .andExpect(jsonPath("$.address").value("123 Test St"))
                    .andExpect(jsonPath("$.doctorCount").value(0))
                    .andExpect(jsonPath("$.appointmentCount").value(0));

            // Verify in database
            assertThat(medicalClinicRepository.findAll()).hasSize(1);
        }

        @Test
        @WithMockUser(username = "admin", roles = {"ADMIN"})
        void createClinic_WithBlankName_ShouldReturnBadRequest() throws Exception {
            // Given
            AdminClinicDto clinicDto = new AdminClinicDto();
            clinicDto.setName(""); // Blank name
            clinicDto.setCity("Test City");

            // When & Then
            mockMvc.perform(post("/api/admin/clinics")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(clinicDto)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser(username = "admin", roles = {"ADMIN"})
        void updateClinic_ShouldUpdateClinic() throws Exception {
            // Given
            MedicalClinic clinic = new MedicalClinic();
            clinic.setName("Old Name");
            clinic.setCity("Old City");
            clinic.setAddress("Old Address");
            clinic = medicalClinicRepository.save(clinic);

            AdminClinicDto updateDto = new AdminClinicDto();
            updateDto.setName("Updated Name");
            updateDto.setCity("Updated City");
            updateDto.setAddress("Updated Address");
            updateDto.setPhone("987-654-3210");

            // When & Then
            mockMvc.perform(put("/api/admin/clinics/{clinicId}", clinic.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateDto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("Updated Name"))
                    .andExpect(jsonPath("$.city").value("Updated City"))
                    .andExpect(jsonPath("$.address").value("Updated Address"));

            // Verify in database
            MedicalClinic updated = medicalClinicRepository.findById(clinic.getId()).orElseThrow();
            assertThat(updated.getName()).isEqualTo("Updated Name");
            assertThat(updated.getCity()).isEqualTo("Updated City");
        }

        @Test
        @WithMockUser(username = "admin", roles = {"ADMIN"})
        void updateClinic_WithNonExistentId_ShouldReturnInternalServerError() throws Exception {
            // Given
            AdminClinicDto updateDto = new AdminClinicDto();
            updateDto.setName("Updated Name");
            updateDto.setCity("Updated City");

            Long nonExistentId = 999L;

            // When & Then
            mockMvc.perform(put("/api/admin/clinics/{clinicId}", nonExistentId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateDto)))
                    .andExpect(status().isInternalServerError());
        }

        @Test
        @WithMockUser(username = "admin", roles = {"ADMIN"})
        void deleteClinic_ShouldDeleteClinic() throws Exception {
            // Given
            MedicalClinic clinic = new MedicalClinic();
            clinic.setName("Clinic to Delete");
            clinic.setCity("Test City");
            clinic = medicalClinicRepository.save(clinic);

            // When & Then
            mockMvc.perform(delete("/api/admin/clinics/{clinicId}", clinic.getId()))
                    .andExpect(status().isOk());

            // Verify deleted
            assertThat(medicalClinicRepository.findById(clinic.getId())).isEmpty();
        }

        @Test
        @WithMockUser(username = "admin", roles = {"ADMIN"})
        void deleteClinic_WithNonExistentId_ShouldDeleteWithoutError() throws Exception {
            // Given
            Long nonExistentId = 999L;

            // When & Then - Should return OK even if clinic doesn't exist
            mockMvc.perform(delete("/api/admin/clinics/{clinicId}", nonExistentId))
                    .andExpect(status().isOk());
        }

        @Test
        @WithMockUser(username = "user", roles = {"USER"})
        void getAllClinics_WithoutAdminRole_ShouldReturnForbidden() throws Exception {
            // When & Then - User without ADMIN role should get 403
            mockMvc.perform(get("/api/admin/clinics"))
                    .andExpect(status().isForbidden());
        }

        @Test
        void getAllClinics_WithoutAuthentication_ShouldReturnUnauthorized() throws Exception {
            // When & Then - No authentication should get 401
            mockMvc.perform(get("/api/admin/clinics"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockUser(username = "admin", roles = {"ADMIN"})
        void getClinicsByCity_WithNonExistentCity_ShouldReturnEmptyList() throws Exception {
            // Given
            MedicalClinic clinic = new MedicalClinic();
            clinic.setName("Clinic");
            clinic.setCity("Existing City");
            medicalClinicRepository.save(clinic);

            // When & Then
            mockMvc.perform(get("/api/admin/clinics/city/NonExistentCity"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$").value(hasSize(0)));
        }

        @Test
        @WithMockUser(username = "admin", roles = {"ADMIN"})
        void createClinic_WithAllFields_ShouldCreateClinicWithAllDetails() throws Exception {
            // Given
            AdminClinicDto clinicDto = new AdminClinicDto();
            clinicDto.setName("Complete Clinic");
            clinicDto.setAddress("123 Main St");
            clinicDto.setCity("Metropolis");
            clinicDto.setCountry("USA");
            clinicDto.setPhone("555-123-4567");
            clinicDto.setWebsiteUrl("https://completeclinic.com");

            // When & Then
            mockMvc.perform(post("/api/admin/clinics")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(clinicDto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("Complete Clinic"))
                    .andExpect(jsonPath("$.address").value("123 Main St"))
                    .andExpect(jsonPath("$.city").value("Metropolis"))
                    .andExpect(jsonPath("$.country").value("USA"))
                    .andExpect(jsonPath("$.phone").value("555-123-4567"))
                    .andExpect(jsonPath("$.websiteUrl").value("https://completeclinic.com"));
        }
    }