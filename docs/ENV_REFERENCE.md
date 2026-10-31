# Environment Variable Reference Guide

This document provides a comprehensive reference for all environment variables supported by the **UdyogaSethu Admin** application.

---

## 1. Core Server Configuration

| Variable | Description | Required in Prod? | Default (Local) | Default (Prod) |
|---|---|---|---|---|
| `PORT` | HTTP port on which the Spring Boot application listens. | Optional | `8080` | `8080` |
| `SPRING_PROFILES_ACTIVE` | Active Spring profile (`local` for H2 sandbox, `prod` for live Supabase). | **Required** | `local` | `prod` |

---

## 2. Supabase Database Connection Pooler

All live database connections connect to Supabase PostgreSQL through the Supabase Transaction Pooler (or Session Pooler).

> [!NOTE]
> Direct database connections to `db.<project-ref>.supabase.co` are IPv6-only. The pooler host `aws-0-ap-southeast-1.pooler.supabase.com` provides reliable IPv4 connectivity.

| Variable | Description | Required in Prod? | Default / Format |
|---|---|---|---|
| `SUPABASE_DB_HOST` | Hostname of the Supabase Pooler. | **Required** | `aws-0-ap-southeast-1.pooler.supabase.com` |
| `SUPABASE_DB_PORT` | Port for the connection pooler. | Optional | `5432` (or `6543`) |
| `SUPABASE_DB_NAME` | Target database name. | Optional | `postgres` |
| `SUPABASE_DB_USERNAME` | Pooler username formatted as `<role>.<project-ref>`. | **Required** | `udyogasethu_admin_app.mmzbpeqkfomnywuzoqii` |
| `SUPABASE_DB_PASSWORD` | Strong password assigned to the dedicated admin role. | **Required** | *(Never hardcoded)* |

---

## 3. Supabase API & Auth Configuration

| Variable | Description | Required in Prod? | Notes |
|---|---|---|---|
| `SUPABASE_URL` | Base HTTPS endpoint of the Supabase project. | Optional | `https://mmzbpeqkfomnywuzoqii.supabase.co` |
| `SUPABASE_JWT_SECRET` | Supabase JWT Secret or JWKS endpoint URL for token verification. | Optional | Used for external token verification if needed. |

---

## 4. Super Administrator Bootstrap Credentials

When the application boots, `BootstrapAdminRunner` checks `admin.admin_users`. If the initial super admin account does not exist or has an uninitialized password hash, it provisions the account using BCrypt strength 12 hashing.

| Variable | Description | Required in Prod? | Default |
|---|---|---|---|
| `SUPERADMIN_EMAIL` | Corporate email for the primary platform super administrator. | Optional | `admin@udyogasethu.org` |
| `SUPERADMIN_NAME` | Full legal name of the primary super administrator. | Optional | `Platform Administrator` |
| `SUPERADMIN_PASSWORD` | Initial password hashed into BCrypt upon bootstrap. | Recommended | `Admin@Setu2026!` (Must be changed immediately) |

---

## 5. Security & Deployment Best Practices

1. **Never commit `.env` files**: All local secrets belong strictly in `.env`, which is ignored by `.gitignore`.
2. **Dedicated Admin Database Role**: The admin portal uses `udyogasethu_admin_app`, isolated to the `admin` schema and granted `BYPASSRLS` for server-side platform queries without modifying public mobile RLS policies.
3. **Password Management**: Passwords are never stored in plain text. Any passwords created in the UI or during bootstrap are salted and hashed via BCrypt (12 rounds).
