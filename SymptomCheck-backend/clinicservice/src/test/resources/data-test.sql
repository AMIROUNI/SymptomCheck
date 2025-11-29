-- Insert test clinics data
INSERT INTO clinics (name, address, phone, website_url, city, country) VALUES
                                                                           ('City Medical Center', '123 Main Street', '+1-555-0123', 'https://citymedical.example.com', 'New York', 'USA'),
                                                                           ('Downtown Health Clinic', '456 Oak Avenue', '+1-555-0124', 'https://downtownclinic.example.com', 'Los Angeles', 'USA'),
                                                                           ('Metropolitan Hospital', '789 Park Road', '+1-555-0125', 'https://metropolitan.example.com', 'Chicago', 'USA'),
                                                                           ('Central Medical', '321 Central Blvd', '+1-555-0126', 'https://centralmedical.example.com', 'New York', 'USA'),
                                                                           ('Westside Clinic', '654 West Street', '+1-555-0127', 'https://westside.example.com', 'Los Angeles', 'USA'),
                                                                           ('North Memorial', '987 North Ave', '+1-555-0128', 'https://northmemorial.example.com', 'Chicago', 'USA'),
                                                                           ('Southview Medical', '147 South Lane', '+1-555-0129', 'https://southview.example.com', 'Houston', 'USA'),
                                                                           ('Eastside Health', '258 East Drive', '+1-555-0130', 'https://eastside.example.com', 'Phoenix', 'USA');

-- Update the sequence to avoid conflicts
SELECT setval('clinics_id_seq', (SELECT MAX(id) FROM clinics));