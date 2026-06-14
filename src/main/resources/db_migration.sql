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
