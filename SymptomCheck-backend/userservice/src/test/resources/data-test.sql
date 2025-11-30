-- ===============================
-- TEST DATA FOR INTEGRATION TESTS
-- ===============================

INSERT INTO user_data (
    id, phone_number, profile_photo_url,
    is_profile_complete, clinic_id,
    created_at, updated_at,
    speciality, description, diploma
)
VALUES
    (
        '11111111-1111-1111-1111-111111111111',
        '+21650000001',
        'https://cdn.example.com/photos/user1.jpg',
        TRUE,
        10,
        NOW(),
        NOW(),
        'General Medicine',
        'General practitioner with 5 years experience.',
        'Doctorate in General Medicine'
    ),
    (
        '22222222-2222-2222-2222-222222222222',
        '+21650000002',
        'https://cdn.example.com/photos/user2.jpg',
        TRUE,
        20,
        NOW(),
        NOW(),
        'Cardiology',
        'Expert in cardiovascular diseases.',
        'Specialized Diploma in Cardiology'
    ),
    (
        '33333333-3333-3333-3333-333333333333',
        '+21650000003',
        NULL,
        FALSE,
        NULL,
        NOW(),
        NULL,
        NULL,
        NULL,
        NULL
    );
