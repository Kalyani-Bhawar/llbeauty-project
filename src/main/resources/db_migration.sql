-- Alter users table
ALTER TABLE users ADD COLUMN active BOOLEAN DEFAULT TRUE;

-- Alter orders table
ALTER TABLE orders ADD COLUMN order_status VARCHAR(50) DEFAULT 'Pending';
ALTER TABLE orders ADD COLUMN payment_status VARCHAR(50) DEFAULT 'Pending';

-- Alter store applications table
ALTER TABLE store_applications ADD COLUMN deleted BOOLEAN DEFAULT FALSE;

-- Alter memberships table
ALTER TABLE memberships ADD COLUMN duration_months INT;
ALTER TABLE memberships ADD COLUMN active BOOLEAN DEFAULT TRUE;

-- Alter salon services table
ALTER TABLE salon_services ADD COLUMN category VARCHAR(100);
ALTER TABLE salon_services ADD COLUMN active BOOLEAN DEFAULT TRUE;

-- Create audit logs table
CREATE TABLE IF NOT EXISTS audit_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    action VARCHAR(100) NOT NULL,
    details TEXT,
    timestamp DATETIME NOT NULL,
    performed_by VARCHAR(255)
);

-- Alter orders table to add tracking and shipping information
ALTER TABLE orders ADD COLUMN shipping_address VARCHAR(500) DEFAULT NULL;
ALTER TABLE orders ADD COLUMN billing_name VARCHAR(255) DEFAULT NULL;
ALTER TABLE orders ADD COLUMN billing_mobile VARCHAR(20) DEFAULT NULL;
ALTER TABLE orders ADD COLUMN tracking_number VARCHAR(100) DEFAULT NULL;
ALTER TABLE orders ADD COLUMN courier_name VARCHAR(100) DEFAULT NULL;
ALTER TABLE orders ADD COLUMN expected_delivery_date DATETIME DEFAULT NULL;
ALTER TABLE orders ADD COLUMN last_status_updated_at DATETIME DEFAULT NULL;

-- Create order status history table
CREATE TABLE IF NOT EXISTS order_status_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL,
    remarks TEXT,
    updated_by VARCHAR(255) NOT NULL,
    updated_at DATETIME NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
);
