# UdyogaSethu Admin — Schema Verification Checklist & Queries

If the live Supabase database direct connection cannot be accessed from this development workstation (e.g. pending DB password or network egress restrictions), the following exact SQL queries can be executed in the **Supabase Dashboard SQL Editor** (`https://supabase.com/dashboard/project/mmzbpeqkfomnywuzoqii/sql`) to verify live database state against our discovery documents.

---

## Query 1: Verify Table Inventory & Row Counts
```sql
SELECT 
    schemaname,
    relname AS table_name,
    n_live_tup AS estimated_row_count
FROM pg_stat_user_tables
WHERE schemaname = 'public'
ORDER BY n_live_tup DESC;
```

---

## Query 2: Verify Key Enums & Values
```sql
SELECT 
    t.typname AS enum_name,
    e.enumlabel AS enum_value
FROM pg_type t 
JOIN pg_enum e ON t.oid = e.enumtypid  
JOIN pg_catalog.pg_namespace n ON n.oid = t.typnamespace
WHERE n.nspname = 'public'
ORDER BY t.typname, e.enumsortorder;
```

---

## Query 3: Verify Foreign Key Constraints on Core Tables
```sql
SELECT
    tc.table_schema, 
    tc.table_name, 
    kcu.column_name, 
    ccu.table_name AS foreign_table_name,
    ccu.column_name AS foreign_column_name 
FROM information_schema.table_constraints AS tc 
JOIN information_schema.key_column_usage AS kcu
  ON tc.constraint_name = kcu.constraint_name
  AND tc.table_schema = kcu.table_schema
JOIN information_schema.constraint_column_usage AS ccu
  ON ccu.constraint_name = tc.constraint_name
  AND ccu.table_schema = tc.table_schema
WHERE tc.constraint_type = 'FOREIGN KEY' 
  AND tc.table_schema = 'public'
ORDER BY tc.table_name;
```

---

## Query 4: Verify Active RLS Policies
```sql
SELECT 
    schemaname, 
    tablename, 
    policyname, 
    permissive, 
    roles, 
    cmd, 
    qual 
FROM pg_policies 
WHERE schemaname = 'public'
ORDER BY tablename, policyname;
```

---

## Query 5: Create Dedicated Admin Role (`admin.admin_users` migration)
This script can be executed once database credentials are provided:
```sql
CREATE SCHEMA IF NOT EXISTS admin;

CREATE TABLE IF NOT EXISTS admin.admin_users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email TEXT UNIQUE NOT NULL,
    full_name TEXT NOT NULL,
    role TEXT NOT NULL DEFAULT 'ADMIN', -- SUPER_ADMIN, ADMIN, MODERATOR, SUPPORT
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS admin.audit_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    admin_id UUID REFERENCES admin.admin_users(id),
    admin_email TEXT NOT NULL,
    action TEXT NOT NULL,
    target_type TEXT NOT NULL,
    target_id TEXT NOT NULL,
    reason TEXT,
    diff_json JSONB,
    ip_address TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- Trigger to make audit logs append-only (prevent UPDATE and DELETE)
CREATE OR REPLACE FUNCTION admin.prevent_audit_mutation()
RETURNS TRIGGER AS $$
BEGIN
    RAISE EXCEPTION 'admin.audit_logs is an append-only audit trail. UPDATE and DELETE are prohibited.';
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_prevent_audit_mutation ON admin.audit_logs;
CREATE TRIGGER trg_prevent_audit_mutation
BEFORE UPDATE OR DELETE ON admin.audit_logs
FOR EACH ROW EXECUTE FUNCTION admin.prevent_audit_mutation();
```
