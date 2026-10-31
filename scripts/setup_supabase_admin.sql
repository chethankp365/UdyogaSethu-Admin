-- ==============================================================================
-- UdyogaSethu Admin: Supabase Database Role & Compatibility Script
-- Target: Supabase PostgreSQL (Schema: public & admin)
-- Security: Least-privilege access, no bypassrls, explicit column grants
-- ==============================================================================

-- 1. Create dedicated least-privilege role for the admin application
DO $$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = 'udyogasethu_admin_app') THEN
        CREATE ROLE udyogasethu_admin_app WITH LOGIN PASSWORD 'CHANGE_THIS_TO_STRONG_PASSWORD';
    END IF;
END $$;

-- 2. Schema usages
CREATE SCHEMA IF NOT EXISTS admin;
GRANT USAGE ON SCHEMA public TO udyogasethu_admin_app;
GRANT USAGE, CREATE ON SCHEMA admin TO udyogasethu_admin_app;

-- 3. Read permissions on public tables
GRANT SELECT ON ALL TABLES IN SCHEMA public TO udyogasethu_admin_app;

-- 4. Controlled operational writes (least-privilege)
GRANT UPDATE (is_suspended) ON public.profiles TO udyogasethu_admin_app;
GRANT UPDATE (status) ON public.jobs TO udyogasethu_admin_app;
GRANT UPDATE (status) ON public.produce_listings TO udyogasethu_admin_app;
GRANT UPDATE (status, rejection_reason) ON public.kyc_records TO udyogasethu_admin_app;
GRANT UPDATE (status, resolution_notes, refund_amount_paise) ON public.disputes TO udyogasethu_admin_app;

-- 5. Full permissions inside admin schema
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA admin TO udyogasethu_admin_app;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA admin TO udyogasethu_admin_app;

-- 6. Implicit string-to-enum casts for JPA repository integration
CREATE OR REPLACE FUNCTION public.cast_varchar_to_app_role(val varchar) RETURNS app_role AS $$
    SELECT val::app_role;
$$ LANGUAGE sql IMMUTABLE;

CREATE OR REPLACE FUNCTION public.cast_varchar_to_job_status(val varchar) RETURNS job_status AS $$
    SELECT val::job_status;
$$ LANGUAGE sql IMMUTABLE;

CREATE OR REPLACE FUNCTION public.cast_varchar_to_produce_status(val varchar) RETURNS produce_status AS $$
    SELECT val::produce_status;
$$ LANGUAGE sql IMMUTABLE;

CREATE OR REPLACE FUNCTION public.cast_varchar_to_verification_status(val varchar) RETURNS verification_status AS $$
    SELECT val::verification_status;
$$ LANGUAGE sql IMMUTABLE;

CREATE OR REPLACE FUNCTION public.cast_varchar_to_dispute_status(val varchar) RETURNS dispute_status AS $$
    SELECT val::dispute_status;
$$ LANGUAGE sql IMMUTABLE;

DROP CAST IF EXISTS (varchar AS app_role);
CREATE CAST (varchar AS app_role) WITH FUNCTION public.cast_varchar_to_app_role(varchar) AS IMPLICIT;

DROP CAST IF EXISTS (varchar AS job_status);
CREATE CAST (varchar AS job_status) WITH FUNCTION public.cast_varchar_to_job_status(varchar) AS IMPLICIT;

DROP CAST IF EXISTS (varchar AS produce_status);
CREATE CAST (varchar AS produce_status) WITH FUNCTION public.cast_varchar_to_produce_status(varchar) AS IMPLICIT;

DROP CAST IF EXISTS (varchar AS verification_status);
CREATE CAST (varchar AS verification_status) WITH FUNCTION public.cast_varchar_to_verification_status(varchar) AS IMPLICIT;

DROP CAST IF EXISTS (varchar AS dispute_status);
CREATE CAST (varchar AS dispute_status) WITH FUNCTION public.cast_varchar_to_dispute_status(varchar) AS IMPLICIT;

-- 7. Upper and lower functions for JPA IgnoreCase queries on PostgreSQL enums
CREATE OR REPLACE FUNCTION public.upper(s job_status) RETURNS text AS $$ SELECT upper(s::text); $$ LANGUAGE sql IMMUTABLE;
CREATE OR REPLACE FUNCTION public.lower(s job_status) RETURNS text AS $$ SELECT lower(s::text); $$ LANGUAGE sql IMMUTABLE;

CREATE OR REPLACE FUNCTION public.upper(s produce_status) RETURNS text AS $$ SELECT upper(s::text); $$ LANGUAGE sql IMMUTABLE;
CREATE OR REPLACE FUNCTION public.lower(s produce_status) RETURNS text AS $$ SELECT lower(s::text); $$ LANGUAGE sql IMMUTABLE;

CREATE OR REPLACE FUNCTION public.upper(s order_status) RETURNS text AS $$ SELECT upper(s::text); $$ LANGUAGE sql IMMUTABLE;
CREATE OR REPLACE FUNCTION public.lower(s order_status) RETURNS text AS $$ SELECT lower(s::text); $$ LANGUAGE sql IMMUTABLE;

CREATE OR REPLACE FUNCTION public.upper(s payment_status) RETURNS text AS $$ SELECT upper(s::text); $$ LANGUAGE sql IMMUTABLE;
CREATE OR REPLACE FUNCTION public.lower(s payment_status) RETURNS text AS $$ SELECT lower(s::text); $$ LANGUAGE sql IMMUTABLE;

CREATE OR REPLACE FUNCTION public.upper(s payout_status) RETURNS text AS $$ SELECT upper(s::text); $$ LANGUAGE sql IMMUTABLE;
CREATE OR REPLACE FUNCTION public.lower(s payout_status) RETURNS text AS $$ SELECT lower(s::text); $$ LANGUAGE sql IMMUTABLE;

CREATE OR REPLACE FUNCTION public.upper(s verification_status) RETURNS text AS $$ SELECT upper(s::text); $$ LANGUAGE sql IMMUTABLE;
CREATE OR REPLACE FUNCTION public.lower(s verification_status) RETURNS text AS $$ SELECT lower(s::text); $$ LANGUAGE sql IMMUTABLE;

CREATE OR REPLACE FUNCTION public.upper(s dispute_status) RETURNS text AS $$ SELECT upper(s::text); $$ LANGUAGE sql IMMUTABLE;
CREATE OR REPLACE FUNCTION public.lower(s dispute_status) RETURNS text AS $$ SELECT lower(s::text); $$ LANGUAGE sql IMMUTABLE;

CREATE OR REPLACE FUNCTION public.upper(s app_role) RETURNS text AS $$ SELECT upper(s::text); $$ LANGUAGE sql IMMUTABLE;
CREATE OR REPLACE FUNCTION public.lower(s app_role) RETURNS text AS $$ SELECT lower(s::text); $$ LANGUAGE sql IMMUTABLE;

CREATE OR REPLACE FUNCTION public.upper(s seeker_type) RETURNS text AS $$ SELECT upper(s::text); $$ LANGUAGE sql IMMUTABLE;
CREATE OR REPLACE FUNCTION public.lower(s seeker_type) RETURNS text AS $$ SELECT lower(s::text); $$ LANGUAGE sql IMMUTABLE;

CREATE OR REPLACE FUNCTION public.upper(s provider_type) RETURNS text AS $$ SELECT upper(s::text); $$ LANGUAGE sql IMMUTABLE;
CREATE OR REPLACE FUNCTION public.lower(s provider_type) RETURNS text AS $$ SELECT lower(s::text); $$ LANGUAGE sql IMMUTABLE;

CREATE OR REPLACE FUNCTION public.upper(s assignment_status) RETURNS text AS $$ SELECT upper(s::text); $$ LANGUAGE sql IMMUTABLE;
CREATE OR REPLACE FUNCTION public.lower(s assignment_status) RETURNS text AS $$ SELECT lower(s::text); $$ LANGUAGE sql IMMUTABLE;

CREATE OR REPLACE FUNCTION public.upper(s application_status) RETURNS text AS $$ SELECT upper(s::text); $$ LANGUAGE sql IMMUTABLE;
CREATE OR REPLACE FUNCTION public.lower(s application_status) RETURNS text AS $$ SELECT lower(s::text); $$ LANGUAGE sql IMMUTABLE;

CREATE OR REPLACE FUNCTION public.upper(s ledger_direction) RETURNS text AS $$ SELECT upper(s::text); $$ LANGUAGE sql IMMUTABLE;
CREATE OR REPLACE FUNCTION public.lower(s ledger_direction) RETURNS text AS $$ SELECT lower(s::text); $$ LANGUAGE sql IMMUTABLE;

CREATE OR REPLACE FUNCTION public.upper(s ledger_bucket) RETURNS text AS $$ SELECT upper(s::text); $$ LANGUAGE sql IMMUTABLE;
CREATE OR REPLACE FUNCTION public.lower(s ledger_bucket) RETURNS text AS $$ SELECT lower(s::text); $$ LANGUAGE sql IMMUTABLE;

CREATE OR REPLACE FUNCTION public.upper(s lang_code) RETURNS text AS $$ SELECT upper(s::text); $$ LANGUAGE sql IMMUTABLE;
CREATE OR REPLACE FUNCTION public.lower(s lang_code) RETURNS text AS $$ SELECT lower(s::text); $$ LANGUAGE sql IMMUTABLE;

CREATE OR REPLACE FUNCTION public.upper(s availability_status) RETURNS text AS $$ SELECT upper(s::text); $$ LANGUAGE sql IMMUTABLE;
CREATE OR REPLACE FUNCTION public.lower(s availability_status) RETURNS text AS $$ SELECT lower(s::text); $$ LANGUAGE sql IMMUTABLE;

CREATE OR REPLACE FUNCTION public.upper(s wage_type) RETURNS text AS $$ SELECT upper(s::text); $$ LANGUAGE sql IMMUTABLE;
CREATE OR REPLACE FUNCTION public.lower(s wage_type) RETURNS text AS $$ SELECT lower(s::text); $$ LANGUAGE sql IMMUTABLE;

CREATE OR REPLACE FUNCTION public.upper(s payment_mode) RETURNS text AS $$ SELECT upper(s::text); $$ LANGUAGE sql IMMUTABLE;
CREATE OR REPLACE FUNCTION public.lower(s payment_mode) RETURNS text AS $$ SELECT lower(s::text); $$ LANGUAGE sql IMMUTABLE;

CREATE OR REPLACE FUNCTION public.upper(s job_source) RETURNS text AS $$ SELECT upper(s::text); $$ LANGUAGE sql IMMUTABLE;
CREATE OR REPLACE FUNCTION public.lower(s job_source) RETURNS text AS $$ SELECT lower(s::text); $$ LANGUAGE sql IMMUTABLE;

CREATE OR REPLACE FUNCTION public.upper(s produce_category) RETURNS text AS $$ SELECT upper(s::text); $$ LANGUAGE sql IMMUTABLE;
CREATE OR REPLACE FUNCTION public.lower(s produce_category) RETURNS text AS $$ SELECT lower(s::text); $$ LANGUAGE sql IMMUTABLE;

-- 8. Grant execute to all roles
GRANT EXECUTE ON ALL FUNCTIONS IN SCHEMA public TO udyogasethu_admin_app, authenticated, anon, service_role;
