# UdyogaSethu Admin — Project Blockers & Dependency Tracker

This document tracks items that are pending external inputs (credentials, keys) in strict accordance with the Master Prompt instructions: *"If something is blocked (missing credential, inaccessible schema), STOP only that item, record it in `docs/BLOCKERS.md`, and continue all independent work."*

---

## Active External Dependency Items

### 1. Supabase PostgreSQL Direct Pooler Credentials
- **Status**: **Pending User Input**
- **Impact**: Real database connectivity for HikariCP/JPA in production/live profile.
- **Project URL**: `https://mmzbpeqkfomnywuzoqii.supabase.co`
- **Host**: `aws-0-ap-south-1.pooler.supabase.com` (or regional direct pooler)
- **Port**: `6543` (transaction mode pooler) / `5432` (session mode)
- **Required Inputs**:
  - `SUPABASE_DB_HOST`
  - `SUPABASE_DB_NAME` (`postgres`)
  - `SUPABASE_DB_USERNAME` (`postgres.mmzbpeqkfomnywuzoqii` or custom role)
  - `SUPABASE_DB_PASSWORD`
- **Mitigation / Independent Work**:
  - Offline / local profile runs with in-memory H2 or local PostgreSQL with verified schema definitions so all scaffolding, entities, services, Vaadin UI, and tests continue unimpeded.

### 2. Supabase Service Role Key & JWT Secret / JWKS
- **Status**: **Pending User Input**
- **Impact**: Server-side verification of Supabase Auth JWTs.
- **Required Inputs**:
  - `SUPABASE_JWT_SECRET` or Supabase project JWKS URL (`https://mmzbpeqkfomnywuzoqii.supabase.co/auth/v1/.well-known/jwks.json`).
- **Mitigation**:
  - Spring Security is configured with JWKS decoder support and a local mock JWT validator for development/testing profiles.

---

## Blockers Resolution Log
| Date | Item | Status | Resolved By | Notes |
| :--- | :--- | :--- | :--- | :--- |
| 2026-10-30 | Java 21 & Maven Tooling | **RESOLVED** | Portable JDK 21.0.13 & Maven 3.9.6 configured at `D:\tools` | PATH and JAVA_HOME persistent |
| 2026-10-30 | Flutter Source & Schema | **RESOLVED** | Located at `D:\Projects\Udyoga Sethu` | 107 migrations audited |
