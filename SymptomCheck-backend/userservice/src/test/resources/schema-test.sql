

DROP TABLE IF EXISTS user_data CASCADE;

CREATE TABLE user_data (
                           id VARCHAR(36) PRIMARY KEY,
                           phone_number VARCHAR(20),
                           profile_photo_url TEXT,

                           is_profile_complete BOOLEAN NOT NULL DEFAULT FALSE,

                           clinic_id BIGINT,

                           created_at TIMESTAMP,
                           updated_at TIMESTAMP,

                           speciality VARCHAR(255),
                           description TEXT,
                           diploma VARCHAR(255)
);

-- INDEXES FOR BETTER PERFORMANCE
CREATE INDEX idx_user_phone ON user_data(phone_number);
CREATE INDEX idx_user_clinic_id ON user_data(clinic_id);
CREATE INDEX idx_user_speciality ON user_data(speciality);
CREATE INDEX idx_user_profile_complete ON user_data(is_profile_complete);
