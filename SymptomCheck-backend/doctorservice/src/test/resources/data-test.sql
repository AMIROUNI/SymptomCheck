-- ===========================
-- doctor_availabilities
-- ===========================

INSERT INTO doctor_availabilities (id, doctor_id, start_time, end_time)
VALUES
    (1, '11111111-1111-1111-1111-111111111111', '09:00', '12:00'),
    (2, '11111111-1111-1111-1111-111111111111', '14:00', '18:00'),

    (3, '22222222-2222-2222-2222-222222222222', '08:30', '11:30'),
    (4, '22222222-2222-2222-2222-222222222222', '13:00', '17:00');



-- ===========================
-- availability_days  (linked to above)
-- ===========================

-- Doctor 1 availability slots
INSERT INTO availability_days (availability_id, day_of_week)
VALUES
    (1, 'MONDAY'),
    (1, 'WEDNESDAY'),
    (2, 'FRIDAY'),

-- Doctor 2 availability slots
    (3, 'TUESDAY'),
    (3, 'THURSDAY'),
    (4, 'SATURDAY');



-- ===========================
-- healthcare_services
-- ===========================

INSERT INTO healthcare_services (id, doctor_id, name, description, category, image_url, duration_minutes, price)
VALUES
    (1, '11111111-1111-1111-1111-111111111111', 'General Consultation',
     'A standard medical consultation', 'General', 'https://img.com/c1.jpg', 30, 50.0),

    (2, '11111111-1111-1111-1111-111111111111', 'Dermatology Check',
     'Skin health examination', 'Dermatology', 'https://img.com/c2.jpg', 45, 80.0),

    (3, '22222222-2222-2222-2222-222222222222', 'Cardiology Consultation',
     'Heart health consultation', 'Cardiology', 'https://img.com/c3.jpg', 60, 120.0);
