INSERT INTO users (id, created_at, updated_at, deleted_at, phone, email, password_hash, full_name, role, active)
VALUES
('11111111-1111-1111-1111-111111111111', now(), now(), null, '9999990001', 'admin@medapp.com', '$2a$10$Q7QJ.fO2Z7e6M7aDajSKfODu9xf2uYxDCEV4VpWw2X2gYdExgkt1G', 'MedApp Admin', 'ROLE_ADMIN', true),
('22222222-2222-2222-2222-222222222222', now(), now(), null, '9999990002', 'user@medapp.com', '$2a$10$Q7QJ.fO2Z7e6M7aDajSKfODu9xf2uYxDCEV4VpWw2X2gYdExgkt1G', 'Regular User', 'ROLE_USER', true),
('33333333-3333-3333-3333-333333333333', now(), now(), null, '9999990003', 'pharmacy@medapp.com', '$2a$10$Q7QJ.fO2Z7e6M7aDajSKfODu9xf2uYxDCEV4VpWw2X2gYdExgkt1G', 'Pharmacy Owner', 'ROLE_PHARMACY', true),
('44444444-4444-4444-4444-444444444444', now(), now(), null, '9999990004', 'rider@medapp.com', '$2a$10$Q7QJ.fO2Z7e6M7aDajSKfODu9xf2uYxDCEV4VpWw2X2gYdExgkt1G', 'Rider User', 'ROLE_RIDER', true);

INSERT INTO user_addresses (id, created_at, updated_at, deleted_at, user_id, label, line1, line2, city, state, pincode, latitude, longitude, is_default_address)
VALUES
('55555555-5555-5555-5555-555555555551', now(), now(), null, '22222222-2222-2222-2222-222222222222', 'Home', '12 MG Road', 'Near Metro', 'Bengaluru', 'Karnataka', '560001', 12.9716000, 77.5946000, true);

INSERT INTO pharmacies (id, created_at, updated_at, deleted_at, owner_user_id, store_name, license_number, kyc_document_path, address, latitude, longitude, opens_at, closes_at, status, rejection_reason)
VALUES
('66666666-6666-6666-6666-666666666661', now(), now(), null, '33333333-3333-3333-3333-333333333333', 'CityCare Pharmacy', 'DL-ABCD-1234', '/docs/kyc/citycare.pdf', '10 Residency Road, Bengaluru', 12.9702000, 77.5938000, '08:00', '23:00', 'APPROVED', null);

INSERT INTO medicines (id, created_at, updated_at, deleted_at, name, generic_name, manufacturer, category, gst_percentage, prescription_required, mrp, active)
VALUES
('77777777-7777-7777-7777-777777777771', now(), now(), null, 'Paracetamol 650', 'Paracetamol', 'Acme Pharma', 'Pain Relief', 12.00, false, 25.00, true),
('77777777-7777-7777-7777-777777777772', now(), now(), null, 'Amoxicillin 500', 'Amoxicillin', 'Wellness Labs', 'Antibiotic', 12.00, true, 180.00, true),
('77777777-7777-7777-7777-777777777773', now(), now(), null, 'Cough Syrup', 'Dextromethorphan', 'Cure Labs', 'Cold & Cough', 18.00, false, 120.00, true);

INSERT INTO inventory_items (id, created_at, updated_at, pharmacy_id, medicine_id, batch_number, expiry_date, quantity_available, quantity_reserved, selling_price, version)
VALUES
('88888888-8888-8888-8888-888888888881', now(), now(), '66666666-6666-6666-6666-666666666661', '77777777-7777-7777-7777-777777777771', 'BATCH-PARA-001', DATE_ADD(CURRENT_DATE, INTERVAL 365 DAY), 200, 0, 22.00, 0),
('88888888-8888-8888-8888-888888888882', now(), now(), '66666666-6666-6666-6666-666666666661', '77777777-7777-7777-7777-777777777772', 'BATCH-AMOX-001', DATE_ADD(CURRENT_DATE, INTERVAL 240 DAY), 100, 0, 165.00, 0),
('88888888-8888-8888-8888-888888888883', now(), now(), '66666666-6666-6666-6666-666666666661', '77777777-7777-7777-7777-777777777773', 'BATCH-COUGH-001', DATE_ADD(CURRENT_DATE, INTERVAL 300 DAY), 150, 0, 110.00, 0);

INSERT INTO riders (id, created_at, updated_at, user_id, full_name, phone, available, latitude, longitude, total_earnings)
VALUES
('99999999-9999-9999-9999-999999999991', now(), now(), '44444444-4444-4444-4444-444444444444', 'Rider One', '9999990004', true, 12.9695000, 77.5900000, 0.00);
