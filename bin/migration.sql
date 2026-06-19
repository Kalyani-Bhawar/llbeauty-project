-- Add active column to users table if not exists
ALTER TABLE users ADD COLUMN IF NOT EXISTS active BOOLEAN DEFAULT TRUE;

-- Add order_status to orders table
ALTER TABLE orders ADD COLUMN IF NOT EXISTS order_status VARCHAR(255) DEFAULT 'Pending';

-- Add duration_months and active to memberships
ALTER TABLE memberships ADD COLUMN IF NOT EXISTS duration_months INT DEFAULT 1;
ALTER TABLE memberships ADD COLUMN IF NOT EXISTS active BOOLEAN DEFAULT TRUE;

-- Add deleted to salon_info
ALTER TABLE salon_info ADD COLUMN IF NOT EXISTS deleted BOOLEAN DEFAULT FALSE;

-- Add category and active to salon_services
ALTER TABLE salon_services ADD COLUMN IF NOT EXISTS category VARCHAR(255);
ALTER TABLE salon_services ADD COLUMN IF NOT EXISTS active BOOLEAN DEFAULT TRUE;

-- Create audit_logs table
CREATE TABLE IF NOT EXISTS audit_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    action VARCHAR(255),
    details VARCHAR(255),
    performed_by VARCHAR(255),
    timestamp DATETIME
);
