-- ==============================================================================
-- Flyway Migration V1: Initialize Isolated Admin Schema & Audit Architecture
-- Target: Supabase PostgreSQL (Schema: admin)
-- Mobile App Impact: ZERO (isolated in admin schema, public tables untouched)
-- ==============================================================================
-- 1. Admin Roles & Authorities
CREATE TABLE IF NOT EXISTS admin.roles (
    name VARCHAR(50) PRIMARY KEY,
    description TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

INSERT INTO admin.roles (name, description) VALUES
    ('SUPER_ADMIN', 'Full system control, admin management, and financial reconciliations'),
    ('ADMIN', 'General administrative access across users, jobs, marketplace, and disputes'),
    ('MODERATOR', 'Content moderation, dispute review, and verification queue processing'),
    ('SUPPORT', 'Read-only access and dispute inspection for user support')
ON CONFLICT (name) DO NOTHING;

-- 2. Admin Users Table
CREATE TABLE IF NOT EXISTS admin.admin_users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) UNIQUE NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL REFERENCES admin.roles(name),
    is_active BOOLEAN NOT NULL DEFAULT true,
    last_login_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- 3. Append-Only Audit Logs Table
CREATE TABLE IF NOT EXISTS admin.audit_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    admin_id UUID REFERENCES admin.admin_users(id),
    admin_email VARCHAR(255) NOT NULL,
    action VARCHAR(100) NOT NULL,
    target_type VARCHAR(100) NOT NULL,
    target_id VARCHAR(255) NOT NULL,
    reason TEXT,
    diff_json JSONB,
    ip_address VARCHAR(100),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_audit_logs_target ON admin.audit_logs(target_type, target_id);
CREATE INDEX IF NOT EXISTS idx_audit_logs_created_at ON admin.audit_logs(created_at DESC);
CREATE INDEX IF NOT EXISTS idx_audit_logs_admin_email ON admin.audit_logs(admin_email);

-- Enforce Append-Only Property (Trigger raises exception on UPDATE or DELETE)
CREATE OR REPLACE FUNCTION admin.prevent_audit_logs_mutation()
RETURNS TRIGGER AS $$
BEGIN
    RAISE EXCEPTION 'admin.audit_logs is an immutable, append-only security log. Modifications and deletions are strictly forbidden.';
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_prevent_audit_mutation ON admin.audit_logs;
CREATE TRIGGER trg_prevent_audit_mutation
BEFORE UPDATE OR DELETE ON admin.audit_logs
FOR EACH ROW EXECUTE FUNCTION admin.prevent_audit_logs_mutation();

-- 4. Isolated Demo Transactions (Zero impact on real financial ledgers)
CREATE TABLE IF NOT EXISTS admin.demo_transactions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    transaction_code VARCHAR(100) UNIQUE NOT NULL,
    scenario_name VARCHAR(100) NOT NULL,
    amount_paise BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'completed',
    created_by_admin UUID REFERENCES admin.admin_users(id),
    metadata JSONB,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- 5. Broadcast & Notification Campaigns
CREATE TABLE IF NOT EXISTS admin.notification_campaigns (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title VARCHAR(255) NOT NULL,
    body TEXT NOT NULL,
    target_audience VARCHAR(50) NOT NULL, -- ALL, WORKERS, FARMERS, PROVIDERS
    recipient_count INT NOT NULL DEFAULT 0,
    status VARCHAR(50) NOT NULL DEFAULT 'draft', -- draft, sent, failed
    created_by_admin UUID REFERENCES admin.admin_users(id),
    sent_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
