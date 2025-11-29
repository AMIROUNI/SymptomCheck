-- ===========================
-- Table: doctor_availabilities
-- ===========================

CREATE TABLE doctor_availabilities (
                                       id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                       doctor_id UUID NOT NULL,
                                       start_time TIME NOT NULL,
                                       end_time TIME NOT NULL
);

-- Index on doctorId because you will query by it
CREATE INDEX idx_doctor_availabilities_doctor_id
    ON doctor_availabilities (doctor_id);



-- ===========================
-- Table: availability_days
-- ===========================

CREATE TABLE availability_days (
                                   availability_id BIGINT NOT NULL,
                                   day_of_week VARCHAR(20) NOT NULL,

                                   CONSTRAINT fk_availability_days
                                       FOREIGN KEY (availability_id)
                                           REFERENCES doctor_availabilities (id)
                                           ON DELETE CASCADE
);

-- Index to speed up day-of-week queries
CREATE INDEX idx_availability_days_day
    ON availability_days (day_of_week);



-- ===========================
-- Table: healthcare_services
-- ===========================

CREATE TABLE healthcare_services (
                                     id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                     doctor_id UUID NOT NULL,
                                     name VARCHAR(255),
                                     description VARCHAR(1000),
                                     category VARCHAR(255),
                                     image_url VARCHAR(255),
                                     duration_minutes INT,
                                     price DOUBLE PRECISION
);

-- Index on doctorId
CREATE INDEX idx_healthcare_services_doctor_id
    ON healthcare_services (doctor_id);

-- Index on category (useful for filtering)
CREATE INDEX idx_healthcare_services_category
    ON healthcare_services (category);
