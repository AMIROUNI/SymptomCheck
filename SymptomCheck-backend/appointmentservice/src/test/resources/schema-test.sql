-- src/test/resources/schema-test.sql
-- Complete test database schema for Appointment entity
-- Executed automatically in tests via spring.sql.init.mode=always

-- ========================================
-- 1. ENUM TYPE for AppointmentStatus
-- ========================================
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'appointment_status') THEN
CREATE TYPE appointment_status AS ENUM (
            'PENDING',
            'CONFIRMED',
            'COMPLETED',
            'CANCELLED',
            'NO_SHOW'
        );
END IF;
END $$;

-- ========================================
-- 2. APPOINTMENTS TABLE
-- ========================================
DROP TABLE IF EXISTS appointments CASCADE;

CREATE TABLE appointments (
                              id BIGSERIAL PRIMARY KEY,

                              date_time TIMESTAMP WITH TIME ZONE NOT NULL,

                              patient_id UUID NOT NULL,
                              doctor_id UUID NOT NULL,

                              status appointment_status NOT NULL DEFAULT 'PENDING',

                              description TEXT,

                              payment_transaction_id BIGINT,

                              created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
                              updated_at TIMESTAMP WITH TIME ZONE,

    -- Realistic constraints
                              CONSTRAINT chk_future_or_today CHECK (date_time >= CURRENT_DATE - INTERVAL '1 year'), -- no ancient appointments
    CONSTRAINT chk_valid_payment_ref CHECK (
        payment_transaction_id IS NULL
        OR (status IN ('CONFIRMED', 'COMPLETED') AND payment_transaction_id IS NOT NULL)
    )
);

-- ========================================
-- 3. INDEXES (Critical for performance in real queries)
-- ========================================

-- Single column indexes
CREATE INDEX idx_appointments_patient_id ON appointments(patient_id);
CREATE INDEX idx_appointments_doctor_id ON appointments(doctor_id);
CREATE INDEX idx_appointments_status ON appointments(status);
CREATE INDEX idx_appointments_date_time ON appointments(date_time);
CREATE INDEX idx_appointments_created_at ON appointments(created_at);

-- Composite indexes (most common query patterns)
CREATE INDEX idx_appointments_doctor_date_status
    ON appointments(doctor_id, date_time, status);

CREATE INDEX idx_appointments_patient_date
    ON appointments(patient_id, date_time DESC);

CREATE INDEX idx_appointments_upcoming
    ON appointments(date_time, status)
    WHERE status IN ('PENDING', 'CONFIRMED');

CREATE INDEX idx_appointments_today_doctor
    ON appointments(doctor_id)
    WHERE date_time::date = CURRENT_DATE;

-- For dashboard queries: "next 7 days for doctor"
CREATE INDEX idx_appointments_next_week
    ON appointments(doctor_id, date_time)
    WHERE date_time BETWEEN CURRENT_DATE AND CURRENT_DATE + INTERVAL '7 days'
      AND status NOT IN ('CANCELLED', 'NO_SHOW');

-- ========================================
-- 4. COMMENTS (helpful for devs & DB introspection)
-- ========================================
COMMENT ON TABLE appointments IS 'Medical appointments between patients and doctors';
COMMENT ON COLUMN appointments.patient_id IS 'Reference to User Service (patient UUID)';
COMMENT ON COLUMN appointments.doctor_id IS 'Reference to User/Doctor Service (doctor UUID)';
COMMENT ON COLUMN appointments.payment_transaction_id IS 'Reference to Payment Service transaction (nullable)';
COMMENT ON COLUMN appointments.status IS 'Current state of the appointment';

-- Optional: Enable row-level security (if you want to go full paranoid in tests too)
-- ALTER TABLE appointments ENABLE ROW LEVEL SECURITY;