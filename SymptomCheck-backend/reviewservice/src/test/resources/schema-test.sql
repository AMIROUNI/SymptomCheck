DROP TABLE IF EXISTS doctor_reviews;

CREATE TABLE doctor_reviews (
                                id BIGINT PRIMARY KEY AUTO_INCREMENT,

                                patient_id VARCHAR(255) NOT NULL,
                                doctor_id VARCHAR(255) NOT NULL,

                                rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),

                                comment VARCHAR(2000) NOT NULL,

                                date_posted TIMESTAMP NOT NULL,
                                last_updated TIMESTAMP NULL
);
