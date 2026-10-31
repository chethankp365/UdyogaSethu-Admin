# UdyogaSethu Admin — Rollback & Recovery Procedures

Every administrative migration and configuration includes safe rollback scripts to ensure zero downtime and zero impact on the Flutter mobile app.

---

## 1. Rolling Back the Admin Schema Migration (`V1__init_admin_schema.sql`)

If the admin schema ever needs to be removed from the Supabase PostgreSQL database:

```sql
-- 1. Drop trigger on audit logs
DROP TRIGGER IF EXISTS trg_prevent_audit_mutation ON admin.audit_logs;
DROP FUNCTION IF EXISTS admin.prevent_audit_logs_mutation();

-- 2. Drop admin schema tables
DROP TABLE IF EXISTS admin.notification_campaigns;
DROP TABLE IF EXISTS admin.demo_transactions;
DROP TABLE IF EXISTS admin.audit_logs;
DROP TABLE IF EXISTS admin.admin_users;
DROP TABLE IF EXISTS admin.roles;

-- 3. Drop dedicated admin schema
DROP SCHEMA IF EXISTS admin CASCADE;
```

---

## 2. Revoking the Admin Application Database Role (`scripts/db_admin_role.sql`)

```sql
-- Revoke all permissions granted to udyogasethu_admin_app
REVOKE ALL PRIVILEGES ON ALL TABLES IN SCHEMA admin FROM udyogasethu_admin_app;
REVOKE ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA admin FROM udyogasethu_admin_app;
REVOKE ALL PRIVILEGES ON ALL TABLES IN SCHEMA public FROM udyogasethu_admin_app;
REVOKE USAGE ON SCHEMA admin FROM udyogasethu_admin_app;
REVOKE USAGE ON SCHEMA public FROM udyogasethu_admin_app;

-- Drop role
DROP ROLE IF EXISTS udyogasethu_admin_app;
```

---

## 3. Restoring User Suspensions & Job Statuses

If any operational admin update needs to be reversed:
1. Query `admin.audit_logs` for the action entry and examine `diff_json`.
2. Execute the inverse operation via the Admin Portal or SQL:
   ```sql
   -- Restore user
   UPDATE public.profiles SET is_suspended = false WHERE id = 'TARGET_USER_UUID';

   -- Re-open job
   UPDATE public.jobs SET status = 'open' WHERE id = 'TARGET_JOB_UUID';
   ```
