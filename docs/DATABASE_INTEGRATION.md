# UdyogaSethu Admin — Database Integration & Mobile Invariant Safety

## 1. Connection Pooler Architecture

The UdyogaSethu Admin platform connects to Supabase PostgreSQL via the **Transaction Connection Pooler** (or Session Pooler) over SSL.

- **Host**: `aws-0-ap-south-1.pooler.supabase.com`
- **Port**: `6543` (Transaction Pooler) / `5432` (Direct Session)
- **Database**: `postgres`
- **SSL Mode**: `require`
- **HikariCP Configuration**:
  ```yaml
  hikari:
    maximum-pool-size: 10
    minimum-idle: 2
    idle-timeout: 300000
    max-lifetime: 600000
    connection-timeout: 20000
    pool-name: UdyogaSethuAdminHikariPool
  ```

---

## 2. Invariant Rules: Protecting the Mobile App

1. **`hibernate.ddl-auto=validate`**:
   In production (`application-prod.yml`), Hibernate will NEVER execute `CREATE TABLE`, `ALTER TABLE`, or drop columns on the public schema. Any discrepancy causes the application to fail fast rather than silently alter existing tables.
2. **Dedicated `admin` Schema**:
   All new admin tables (`admin_users`, `roles`, `permissions`, `audit_logs`, `demo_transactions`, `notification_campaigns`) are isolated under `schema: admin`. They can never collide with mobile tables in `public`.
3. **Double-Entry Financial Safety**:
   The table `public.ledger_entries` is protected by trigger `trg_prevent_ledger_mutation` which raises an exception on any attempt to `UPDATE` or `DELETE`. Admin balance operations are strictly auditable credit/debit inserts.
4. **Mobile Apply Flow Safety Proof**:
   - The Flutter mobile app invokes `public.apply_to_job(p_job_id UUID, p_message TEXT)` (migration `0780`).
   - The admin app never updates `jobs.slots_filled` independently without corresponding assignment state changes.
   - The status enum `application_status` (`applied`, `shortlisted`, `rejected`, `withdrawn`, `hired`) is preserved identically.

---

## 3. Dedicated Admin Role Migration Script (`scripts/db_admin_role.sql`)

```sql
-- Create dedicated least-privilege role for the admin application
DO $$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = 'udyogasethu_admin_app') THEN
        CREATE ROLE udyogasethu_admin_app WITH LOGIN PASSWORD 'YOUR_STRONG_PASSWORD_HERE';
    END IF;
END $$;

-- Grant schema usage
GRANT USAGE ON SCHEMA public TO udyogasethu_admin_app;
GRANT USAGE, CREATE ON SCHEMA admin TO udyogasethu_admin_app;

-- Grant SELECT on all public tables to admin role
GRANT SELECT ON ALL TABLES IN SCHEMA public TO udyogasethu_admin_app;

-- Grant controlled UPDATE on specific operational tables only
GRANT UPDATE (is_suspended) ON public.profiles TO udyogasethu_admin_app;
GRANT UPDATE (status) ON public.jobs TO udyogasethu_admin_app;
GRANT UPDATE (status) ON public.produce_listings TO udyogasethu_admin_app;
GRANT UPDATE (status, rejection_reason) ON public.kyc_records TO udyogasethu_admin_app;
GRANT UPDATE (status, resolution_notes, refund_amount_paise) ON public.disputes TO udyogasethu_admin_app;

-- Grant full control over the admin schema
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA admin TO udyogasethu_admin_app;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA admin TO udyogasethu_admin_app;
```
