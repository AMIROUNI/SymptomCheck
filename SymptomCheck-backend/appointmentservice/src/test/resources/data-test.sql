-- src/test/resources/data-test.sql
-- Test data + performance indexes for Appointment entity

-- ========================================
-- 1. INDEXES (highly recommended for real queries)
-- ========================================

-- Most common queries in appointment service:
CREATE INDEX IF NOT EXISTS idx_appointments_patient_id ON appointments (patient_id);
CREATE INDEX IF NOT EXISTS idx_appointments_doctor_id ON appointments (doctor_id);
CREATE INDEX IF NOT EXISTS idx_appointments_status ON appointments (status);
CREATE INDEX IF NOT EXISTS idx_appointments_date_time ON appointments (date_time);

-- Composite indexes for frequent combined filters
CREATE INDEX IF NOT EXISTS idx_appointments_doctor_date ON appointments (doctor_id, date_time);
CREATE INDEX IF NOT EXISTS idx_appointments_patient_date ON appointments (patient_id, date_time);
CREATE INDEX IF NOT EXISTS idx_appointments_status_date ON appointments (status, date_time);

-- For finding upcoming appointments quickly
CREATE INDEX IF NOT EXISTS idx_appointments_upcoming ON appointments (date_time, status)
    WHERE status IN ('PENDING', 'CONFIRMED');

-- ========================================
-- 2. TEST DATA
-- ========================================

INSERT INTO appointments (
    id,
    date_time,
    patient_id,
    doctor_id,
    status,
    description,
    payment_transaction_id,
    created_at,
    updated_at
) VALUES
-- 1. Upcoming confirmed appointment (today + 2 days)
(1,
 '2025-12-15 10:30:00',
 'a1b2c3d4-e5f6-7890-abcd-ef1234567890'::uuid,  -- patientId: Alice
 'd4e5f6a1-b2c3-4567-89ab-cdef01234567'::uuid,  -- doctorId: Dr. Martin
 'CONFIRMED',
 'Consultation de suivi - hypertension artérielle',
 1001,
 '2025-11-28T08:00:00Z',
 '2025-11-29T14:22:10Z'),

-- 2. Pending appointment (tomorrow)
(2,
 '2025-12-01 14:00:00',
 'b2c3d4e5-f6a1-7890-bcde-f234567890ab'::uuid,  -- patientId: Bob
 'd4e5f6a1-b2c3-4567-89ab-cdef01234567'::uuid,  -- same doctor
 'PENDING',
 'Premier rendez-vous - douleurs abdominales',
 NULL,
 '2025-11-30T09:15:00Z',
 NULL),

-- 3. Past completed appointment
(3,
 '2025-11-20 09:00:00',
 'a1b2c3d4-e5f6-7890-abcd-ef1234567890'::uuid,  -- Alice again
 'e6f7a8b9-c0d1-2345-6789-abcdef012345'::uuid,  -- Dr. Sophie
 'COMPLETED',
 'Bilan annuel - tout va bien',
 1002,
 '2025-10-01T12:00:00Z',
 '2025-11-20T10:30:00Z'),

-- 4. Cancelled appointment
(4,
 '2025-12-05 16:30:00',
 'c3d4e5f6-a1b2-7890-cdef-34567890abcd'::uuid,  -- patientId: Charlie
 'd4e5f6a1-b2c3-4567-89ab-cdef01234567'::uuid,
 'CANCELLED',
 'Annulé par le patient - empêchement professionnel',
 NULL,
 '2025-11-25T18:44:12Z',
 '2025-11-29T07:12:33Z'),

-- 5. No-show (patient didn’t come)
(5,
 '2025-11-25 11:15:00',
 'f1a2b3c4-d5e6-7890-abcd-ef1234567890'::uuid,  -- patientId: David
 'e6f7a8b9-c0d1-2345-6789-abcdef012345'::uuid,  -- Dr. Sophie
 'NO_SHOW',
 'Patient n''est pas venu - rappel envoyé',
 NULL,
 '2025-11-20T08:30:00Z',
 '2025-11-25T12:00:00Z'),

-- 6. Future appointment far ahead (2026)
(6,
 '2026-02-10 15:45:00',
 'a1b2c3d4-e5f6-7890-abcd-ef1234567890'::uuid,  -- Alice books early
 'f8g9h0i1-j2k3-4567-89ab-cdef01234567'::uuid,  -- Dr. Dubois (new doctor)
 'PENDING',
 'Chirurgie esthétique - consultation pré-opératoire',
 NULL,
 '2025-11-30T10:20:00Z',
 NULL),

-- 7. Today’s appointment in 2 hours
(7,
 CURRENT_DATE + INTERVAL '2 hours',
 'b2c3d4e5-f6a1-7890-bcde-f234567890ab'::uuid,  -- Bob
 'd4e5f6a1-b2c3-4567-89ab-cdef01234567'::uuid,
 'CONFIRMED',
 'Contrôle dermatologique - suivi acné',
 1003,
 NOW() - INTERVAL '1 day',
 NULL),

-- 8. Emergency slot (same day, created today)
(8,
 CURRENT_DATE + INTERVAL '30 minutes',
 'g3h4i5j6-k7l8-9012-3456-7890abcdef12'::uuid,  -- patientId: Emma
 'e6f7a8b9-c0d1-2345-6789-abcdef012345'::uuid,
 'CONFIRMED',
 'URGENT - Forte fièvre + toux depuis 3 jours',
 1004,
 NOW() - INTERVAL '20 minutes',
 NOW() - INTERVAL '15 minutes');

-- Optional: Reset sequence if using IDENTITY
-- ALTER SEQUENCE appointments_id_seq RESTART WITH 100;