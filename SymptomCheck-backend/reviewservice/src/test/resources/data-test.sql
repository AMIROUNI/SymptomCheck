-- ==========================
-- Insert Test Data
-- ==========================

INSERT INTO doctor_reviews (id, patient_id, doctor_id, rating, comment, date_posted, last_updated)
VALUES
    (1, 'PAT001', 'DOC001', 5, 'Excellent doctor, very professional.', CURRENT_TIMESTAMP, NULL),
    (2, 'PAT002', 'DOC001', 4, 'Good experience overall.', CURRENT_TIMESTAMP, NULL),
    (3, 'PAT003', 'DOC002', 3, 'Average service, could be better.', CURRENT_TIMESTAMP, NULL),
    (4, 'PAT004', 'DOC003', 1, 'Very bad experience, not recommended.', CURRENT_TIMESTAMP, NULL),
    (5, 'PAT001', 'DOC002', 5, 'Amazing consultation, highly recommended!', CURRENT_TIMESTAMP, NULL);

-- ==========================
-- Indexes
-- ==========================

-- Search reviews by doctor faster
CREATE INDEX idx_doctor_reviews_doctor_id
    ON doctor_reviews (doctor_id);

-- Search reviews by patient faster
CREATE INDEX idx_doctor_reviews_patient_id
    ON doctor_reviews (patient_id);

-- Filter or aggregate reviews by rating
CREATE INDEX idx_doctor_reviews_rating
    ON doctor_reviews (rating);

-- Optional: Index date_posted if you sort by newest
CREATE INDEX idx_doctor_reviews_date_posted
    ON doctor_reviews (date_posted);
