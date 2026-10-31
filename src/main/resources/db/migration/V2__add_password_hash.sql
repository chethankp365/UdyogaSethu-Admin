-- ==============================================================================
-- Flyway Migration V2: Add Password Hash column for Administrator Authentication
-- Target: Supabase PostgreSQL (Schema: admin)
-- Mobile App Impact: ZERO (isolated in admin schema)
-- ==============================================================================
ALTER TABLE admin.admin_users ADD COLUMN IF NOT EXISTS password_hash VARCHAR(255);
