-- -------------------------------------------------------------
-- Agent Module Database Migrations
-- -------------------------------------------------------------

-- Alter users table if needed (to store agent status)
ALTER TABLE users ADD COLUMN IF NOT EXISTS agent_status VARCHAR(20) DEFAULT 'NOT_APPLIED';

-- Create agents table
CREATE TABLE IF NOT EXISTS agents (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    agent_id VARCHAR(255) NULL,
    referral_code VARCHAR(255) NULL,
    referral_link VARCHAR(255) NULL,
    status VARCHAR(255) NOT NULL DEFAULT 'PENDING',
    created_at DATETIME NOT NULL,
    UNIQUE KEY uq_agents_user (user_id),
    UNIQUE KEY uq_agents_agent_id (agent_id),
    UNIQUE KEY uq_agents_referral_code (referral_code),
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create agent_profiles table
CREATE TABLE IF NOT EXISTS agent_profiles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    agent_id BIGINT NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    mobile_number VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    dob DATE NULL,
    gender VARCHAR(255) NULL,
    address VARCHAR(255) NULL,
    city VARCHAR(255) NULL,
    state VARCHAR(255) NULL,
    pincode VARCHAR(255) NULL,
    occupation VARCHAR(255) NULL,
    experience VARCHAR(255) NULL,
    profile_photo_url VARCHAR(255) NULL,
    identity_proof_url VARCHAR(255) NULL,
    registration_type VARCHAR(255) NOT NULL,
    terms_accepted BOOLEAN DEFAULT FALSE,
    UNIQUE KEY uq_agent_profiles_agent (agent_id),
    FOREIGN KEY (agent_id) REFERENCES agents (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create agent_wallets table
CREATE TABLE IF NOT EXISTS agent_wallets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    agent_id BIGINT NOT NULL,
    current_balance DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    pending_earnings DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    approved_earnings DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    paid_earnings DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    lifetime_earnings DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    total_referrals INT NOT NULL DEFAULT 0,
    UNIQUE KEY uq_agent_wallets_agent (agent_id),
    FOREIGN KEY (agent_id) REFERENCES agents (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create agent_referrals table
CREATE TABLE IF NOT EXISTS agent_referrals (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    referral_code VARCHAR(255) NOT NULL,
    agent_id BIGINT NOT NULL,
    source_module VARCHAR(255) NOT NULL,
    source_record_id VARCHAR(255) NOT NULL,
    source_user_id BIGINT NULL,
    commission_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    commission_status VARCHAR(255) NOT NULL DEFAULT 'Pending',
    activity_status VARCHAR(255) NOT NULL DEFAULT 'Pending',
    created_at DATETIME NOT NULL,
    approved_at DATETIME NULL,
    paid_at DATETIME NULL,
    INDEX idx_referrals_agent (agent_id),
    INDEX idx_referrals_source (source_module, source_record_id),
    FOREIGN KEY (agent_id) REFERENCES agents (id) ON DELETE CASCADE,
    FOREIGN KEY (source_user_id) REFERENCES users (id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create agent_commissions table
CREATE TABLE IF NOT EXISTS agent_commissions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    agent_id BIGINT NOT NULL,
    referral_id BIGINT NULL,
    amount DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    status VARCHAR(255) NOT NULL DEFAULT 'Pending',
    description VARCHAR(255) NOT NULL,
    created_at DATETIME NOT NULL,
    approved_at DATETIME NULL,
    paid_at DATETIME NULL,
    UNIQUE KEY uq_commissions_referral (referral_id),
    INDEX idx_commissions_agent (agent_id),
    FOREIGN KEY (agent_id) REFERENCES agents (id) ON DELETE CASCADE,
    FOREIGN KEY (referral_id) REFERENCES agent_referrals (id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create agent_commission_rules table
CREATE TABLE IF NOT EXISTS agent_commission_rules (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    activity_type VARCHAR(255) NOT NULL,
    value DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    is_percentage BOOLEAN NOT NULL DEFAULT FALSE,
    UNIQUE KEY uq_commission_rules_activity (activity_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create agent_settlements table
CREATE TABLE IF NOT EXISTS agent_settlements (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    settlement_id VARCHAR(255) NOT NULL,
    agent_id BIGINT NOT NULL,
    month VARCHAR(255) NOT NULL,
    total_amount DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    transfer_date DATETIME NULL,
    transaction_reference VARCHAR(255) NULL,
    status VARCHAR(255) NOT NULL DEFAULT 'PENDING',
    UNIQUE KEY uq_settlements_code (settlement_id),
    INDEX idx_settlements_agent (agent_id),
    FOREIGN KEY (agent_id) REFERENCES agents (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create agent_activity_logs table
CREATE TABLE IF NOT EXISTS agent_activity_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    created_by VARCHAR(255) NULL,
    updated_by VARCHAR(255) NULL,
    ip_address VARCHAR(255) NULL,
    device VARCHAR(255) NULL,
    timestamp DATETIME NOT NULL,
    action_type VARCHAR(255) NOT NULL,
    old_value TEXT NULL,
    new_value TEXT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create agent_notifications table
CREATE TABLE IF NOT EXISTS agent_notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    agent_id BIGINT NOT NULL,
    message VARCHAR(255) NOT NULL,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL,
    INDEX idx_notifications_agent (agent_id),
    FOREIGN KEY (agent_id) REFERENCES agents (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
