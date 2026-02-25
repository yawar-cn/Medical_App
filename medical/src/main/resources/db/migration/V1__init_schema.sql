CREATE TABLE users (
    id CHAR(36) PRIMARY KEY,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    deleted_at DATETIME(6),
    phone VARCHAR(20) NOT NULL UNIQUE,
    email VARCHAR(255) UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    role VARCHAR(32) NOT NULL,
    active BOOLEAN NOT NULL
);

CREATE TABLE user_addresses (
    id CHAR(36) PRIMARY KEY,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    deleted_at DATETIME(6),
    user_id CHAR(36) NOT NULL REFERENCES users(id),
    label VARCHAR(255) NOT NULL,
    line1 VARCHAR(255) NOT NULL,
    line2 VARCHAR(255),
    city VARCHAR(255) NOT NULL,
    state VARCHAR(255) NOT NULL,
    pincode VARCHAR(10) NOT NULL,
    latitude NUMERIC(10,7) NOT NULL,
    longitude NUMERIC(10,7) NOT NULL,
    is_default_address BOOLEAN NOT NULL
);

CREATE TABLE otp_challenges (
    id CHAR(36) PRIMARY KEY,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    otp_hash VARCHAR(128) NOT NULL,
    expires_at DATETIME(6) NOT NULL,
    consumed BOOLEAN NOT NULL,
    attempts INTEGER NOT NULL
);

CREATE TABLE revoked_access_tokens (
    id CHAR(36) PRIMARY KEY,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    jti VARCHAR(128) NOT NULL UNIQUE,
    expires_at DATETIME(6) NOT NULL
);

CREATE TABLE refresh_tokens (
    id CHAR(36) PRIMARY KEY,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    user_id CHAR(36) NOT NULL REFERENCES users(id),
    token_hash VARCHAR(128) NOT NULL UNIQUE,
    expires_at DATETIME(6) NOT NULL,
    revoked BOOLEAN NOT NULL,
    revoked_at DATETIME(6),
    device_info VARCHAR(255)
);

CREATE TABLE pharmacies (
    id CHAR(36) PRIMARY KEY,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    deleted_at DATETIME(6),
    owner_user_id CHAR(36) NOT NULL REFERENCES users(id),
    store_name VARCHAR(255) NOT NULL,
    license_number VARCHAR(255) NOT NULL UNIQUE,
    kyc_document_path VARCHAR(255) NOT NULL,
    address VARCHAR(255) NOT NULL,
    latitude NUMERIC(10,7) NOT NULL,
    longitude NUMERIC(10,7) NOT NULL,
    opens_at TIME NOT NULL,
    closes_at TIME NOT NULL,
    status VARCHAR(20) NOT NULL,
    rejection_reason VARCHAR(255)
);

CREATE TABLE medicines (
    id CHAR(36) PRIMARY KEY,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    deleted_at DATETIME(6),
    name VARCHAR(255) NOT NULL,
    generic_name VARCHAR(255) NOT NULL,
    manufacturer VARCHAR(255) NOT NULL,
    category VARCHAR(255) NOT NULL,
    gst_percentage NUMERIC(5,2) NOT NULL,
    prescription_required BOOLEAN NOT NULL,
    mrp NUMERIC(12,2) NOT NULL,
    active BOOLEAN NOT NULL
);

CREATE TABLE prescriptions (
    id CHAR(36) PRIMARY KEY,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    user_id CHAR(36) NOT NULL REFERENCES users(id),
    pharmacy_id CHAR(36) REFERENCES pharmacies(id),
    file_path VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL,
    reviewer_user_id CHAR(36),
    reviewed_at DATETIME(6),
    review_notes VARCHAR(255)
);

CREATE TABLE inventory_items (
    id CHAR(36) PRIMARY KEY,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    pharmacy_id CHAR(36) NOT NULL REFERENCES pharmacies(id),
    medicine_id CHAR(36) NOT NULL REFERENCES medicines(id),
    batch_number VARCHAR(255) NOT NULL,
    expiry_date DATE NOT NULL,
    quantity_available INTEGER NOT NULL,
    quantity_reserved INTEGER NOT NULL,
    selling_price NUMERIC(12,2) NOT NULL,
    version BIGINT,
    CONSTRAINT uk_inventory_pharmacy_medicine_batch UNIQUE (pharmacy_id, medicine_id, batch_number)
);

CREATE TABLE stock_reservations (
    id CHAR(36) PRIMARY KEY,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    order_id CHAR(36) NOT NULL,
    pharmacy_id CHAR(36) NOT NULL,
    medicine_id CHAR(36) NOT NULL,
    quantity INTEGER NOT NULL,
    status VARCHAR(20) NOT NULL,
    expires_at DATETIME(6) NOT NULL
);

CREATE TABLE carts (
    id CHAR(36) PRIMARY KEY,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    user_id CHAR(36) NOT NULL REFERENCES users(id),
    status VARCHAR(20) NOT NULL
);

CREATE TABLE cart_items (
    id CHAR(36) PRIMARY KEY,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    cart_id CHAR(36) NOT NULL REFERENCES carts(id),
    pharmacy_id CHAR(36) NOT NULL REFERENCES pharmacies(id),
    medicine_id CHAR(36) NOT NULL REFERENCES medicines(id),
    prescription_id CHAR(36) REFERENCES prescriptions(id),
    quantity INTEGER NOT NULL,
    unit_price NUMERIC(12,2) NOT NULL
);

CREATE TABLE orders (
    id CHAR(36) PRIMARY KEY,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    user_id CHAR(36) NOT NULL REFERENCES users(id),
    pharmacy_id CHAR(36) NOT NULL REFERENCES pharmacies(id),
    prescription_id CHAR(36) REFERENCES prescriptions(id),
    status VARCHAR(40) NOT NULL,
    total_amount NUMERIC(12,2) NOT NULL,
    delivery_address_line1 VARCHAR(255) NOT NULL,
    delivery_address_line2 VARCHAR(255),
    delivery_city VARCHAR(255) NOT NULL,
    delivery_state VARCHAR(255) NOT NULL,
    delivery_pincode VARCHAR(255) NOT NULL,
    delivery_latitude DOUBLE NOT NULL,
    delivery_longitude DOUBLE NOT NULL,
    rider_id CHAR(36),
    delivery_otp_hash VARCHAR(255)
);

CREATE TABLE order_items (
    id CHAR(36) PRIMARY KEY,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    order_id CHAR(36) NOT NULL REFERENCES orders(id),
    medicine_id CHAR(36) NOT NULL REFERENCES medicines(id),
    quantity INTEGER NOT NULL,
    unit_price NUMERIC(12,2) NOT NULL,
    gst_percentage NUMERIC(5,2) NOT NULL
);

CREATE TABLE order_events (
    id CHAR(36) PRIMARY KEY,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    order_id CHAR(36) NOT NULL REFERENCES orders(id),
    from_status VARCHAR(40) NOT NULL,
    to_status VARCHAR(40) NOT NULL,
    actor_user_id CHAR(36) NOT NULL,
    source VARCHAR(255) NOT NULL,
    event_time DATETIME(6) NOT NULL,
    remarks VARCHAR(255) NOT NULL
);

CREATE TABLE payment_transactions (
    id CHAR(36) PRIMARY KEY,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    order_id CHAR(36) NOT NULL REFERENCES orders(id),
    provider_order_id VARCHAR(255) NOT NULL,
    provider_payment_id VARCHAR(255),
    amount NUMERIC(12,2) NOT NULL,
    status VARCHAR(20) NOT NULL,
    raw_gateway_payload TEXT,
    completed_at DATETIME(6),
    refunded_at DATETIME(6)
);

CREATE TABLE riders (
    id CHAR(36) PRIMARY KEY,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    user_id CHAR(36) NOT NULL REFERENCES users(id),
    full_name VARCHAR(255) NOT NULL,
    phone VARCHAR(20) NOT NULL UNIQUE,
    available BOOLEAN NOT NULL,
    latitude DOUBLE NOT NULL,
    longitude DOUBLE NOT NULL,
    total_earnings NUMERIC(12,2) NOT NULL
);

CREATE TABLE delivery_assignments (
    id CHAR(36) PRIMARY KEY,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    order_id CHAR(36) NOT NULL REFERENCES orders(id),
    rider_id CHAR(36) NOT NULL REFERENCES riders(id),
    status VARCHAR(30) NOT NULL,
    earning_amount NUMERIC(12,2) NOT NULL,
    assigned_at DATETIME(6) NOT NULL,
    delivered_at DATETIME(6)
);

CREATE TABLE settlements (
    id CHAR(36) PRIMARY KEY,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    order_id CHAR(36) NOT NULL UNIQUE REFERENCES orders(id),
    pharmacy_id CHAR(36) NOT NULL,
    rider_id CHAR(36),
    gross_amount NUMERIC(12,2) NOT NULL,
    commission_percentage NUMERIC(5,2) NOT NULL,
    commission_amount NUMERIC(12,2) NOT NULL,
    pharmacy_payout NUMERIC(12,2) NOT NULL,
    rider_payout NUMERIC(12,2) NOT NULL,
    status VARCHAR(20) NOT NULL,
    settled_at DATETIME(6)
);

CREATE TABLE notifications (
    id CHAR(36) PRIMARY KEY,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    user_id CHAR(36) NOT NULL,
    channel VARCHAR(20) NOT NULL,
    subject VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    status VARCHAR(20) NOT NULL
);

CREATE TABLE audit_logs (
    id CHAR(36) PRIMARY KEY,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    actor_user_id CHAR(36) NOT NULL,
    action_timestamp DATETIME(6) NOT NULL,
    action VARCHAR(100) NOT NULL,
    entity_type VARCHAR(100) NOT NULL,
    entity_id CHAR(36) NOT NULL,
    details TEXT NOT NULL
);

ALTER TABLE stock_reservations
    ADD CONSTRAINT fk_stock_reservation_order FOREIGN KEY (order_id) REFERENCES orders(id);

ALTER TABLE stock_reservations
    ADD CONSTRAINT fk_stock_reservation_pharmacy FOREIGN KEY (pharmacy_id) REFERENCES pharmacies(id);

ALTER TABLE stock_reservations
    ADD CONSTRAINT fk_stock_reservation_medicine FOREIGN KEY (medicine_id) REFERENCES medicines(id);

ALTER TABLE notifications
    ADD CONSTRAINT fk_notification_user FOREIGN KEY (user_id) REFERENCES users(id);

ALTER TABLE audit_logs
    ADD CONSTRAINT fk_audit_actor_user FOREIGN KEY (actor_user_id) REFERENCES users(id);

ALTER TABLE settlements
    ADD CONSTRAINT fk_settlement_pharmacy FOREIGN KEY (pharmacy_id) REFERENCES pharmacies(id);

ALTER TABLE settlements
    ADD CONSTRAINT fk_settlement_rider FOREIGN KEY (rider_id) REFERENCES riders(id);

CREATE INDEX idx_user_addresses_user_id ON user_addresses(user_id);
CREATE INDEX idx_user_addresses_geo ON user_addresses(latitude, longitude);
CREATE INDEX idx_pharmacy_owner ON pharmacies(owner_user_id);
CREATE INDEX idx_pharmacy_status ON pharmacies(status);
CREATE INDEX idx_pharmacy_geo ON pharmacies(latitude, longitude);
CREATE INDEX idx_inventory_pharmacy ON inventory_items(pharmacy_id);
CREATE INDEX idx_inventory_medicine ON inventory_items(medicine_id);
CREATE INDEX idx_stock_reservation_order ON stock_reservations(order_id);
CREATE INDEX idx_stock_reservation_pharmacy ON stock_reservations(pharmacy_id);
CREATE INDEX idx_stock_reservation_medicine ON stock_reservations(medicine_id);
CREATE INDEX idx_prescription_user ON prescriptions(user_id);
CREATE INDEX idx_cart_user ON carts(user_id);
CREATE INDEX idx_cart_items_cart ON cart_items(cart_id);
CREATE INDEX idx_orders_user_id ON orders(user_id);
CREATE INDEX idx_orders_pharmacy_id ON orders(pharmacy_id);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_orders_delivery_geo ON orders(delivery_latitude, delivery_longitude);
CREATE INDEX idx_order_items_order_id ON order_items(order_id);
CREATE INDEX idx_order_events_order_id ON order_events(order_id);
CREATE INDEX idx_payment_order_id ON payment_transactions(order_id);
CREATE INDEX idx_riders_user_id ON riders(user_id);
CREATE INDEX idx_riders_geo ON riders(latitude, longitude);
CREATE INDEX idx_delivery_assignment_order ON delivery_assignments(order_id);
CREATE INDEX idx_settlement_pharmacy ON settlements(pharmacy_id);
CREATE INDEX idx_settlement_rider ON settlements(rider_id);
CREATE INDEX idx_notifications_user ON notifications(user_id);
CREATE INDEX idx_audit_actor ON audit_logs(actor_user_id);
CREATE INDEX idx_audit_entity ON audit_logs(entity_type, entity_id);
