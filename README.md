# UdyogaSethu Admin Portal 🌾💼

[![Java 21](https://img.shields.io/badge/Java-21-orange.svg)](https://adoptium.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Vaadin](https://img.shields.io/badge/Vaadin-24.4.12-blue.svg)](https://vaadin.com/)
[![Supabase](https://img.shields.io/badge/Supabase-PostgreSQL%2017-3ECF8E.svg)](https://supabase.com/)
[![Security Audited](https://img.shields.io/badge/Security-20%2F20%20Verified-success.svg)](#security--governance)

An enterprise administrative dashboard engineered with **Java 21**, **Spring Boot 3.3**, and **Vaadin Flow 24**, providing mission-critical management for the **UdyogaSethu** mobile ecosystem (informal workers, agricultural farmers, employers, produce commerce, escrow transactions, and KYC identity verification).

---

## 🌟 Key Architecture & Highlights

- **Shared Supabase PostgreSQL Backend**: Seamlessly connected to the Flutter mobile application's live Supabase database (`mmzbpeqkfomnywuzoqii`) via high-performance connection poolers without modifying client-side RLS policies.
- **Isolated Admin Schema**: Dedicated `admin` schema containing administrator accounts, RBAC roles, immutable audit trails, sandbox demo transactions, and notification campaigns.
- **Append-Only Immutable Audit Trail**: Enforced at the database engine level via PostgreSQL PL/pgSQL triggers preventing modification or deletion of compliance logs.
- **BCrypt Strength 12 Authentication**: Hardened administrator credentials with brute-force rate-limiting and temporary account lockouts.
- **Zero Raw HTML Injection**: Fully sanitized UI using Vaadin's type-safe component model and Content Security Policy headers.

---

## 🏛️ Administration Modules (18 Modules)

| Category | Module | Functionality |
|---|---|---|
| **Overview** | **Dashboard** | Live platform KPIs, active workers, marketplace volume, and recent activities. |
| **User Directory** | **Users Management** | Search, filter, inspect profiles, and manage suspensions with mandatory audit reasons. |
| **Workforce** | **Worker Management** | Informal seeker profiles, trade categories, hourly/daily wages, and ratings. |
| **Workforce** | **Service Providers** | Employer profiles, crew capacity, and operational areas. |
| **Agriculture** | **Farmer Management** | Farm clusters, acreages, delivery radii, and organic certifications. |
| **Employment** | **Job Management** | Job postings, wage structures, worker slot quotas, and geofencing. |
| **Commerce** | **Produce Marketplace** | Agricultural listings, crop batches, pricing, and harvest dates. |
| **Commerce** | **Orders Management** | Produce delivery tracking, platform fees, and handover PIN verification. |
| **Finance** | **Payments & Escrow** | Payment transactions, Razorpay references, and financial ledger inspection. |
| **Compliance** | **Identity Verification** | KYC review queue with document image inspection and approval workflows. |
| **Community** | **Reviews & Ratings** | Star ratings, feedback moderation, and inappropriate review hiding. |
| **Resolution** | **Disputes & Complaints**| Case arbitration, resolution recording, and refund adjustments. |
| **AI Telemetry** | **Voice Assistant** | Voice session telemetry and intent monitoring. |
| **Engagement** | **Notifications** | Broadcast notification campaigns and push message logs. |
| **Intelligence**| **Reports & Analytics** | Platform metrics, GMV growth, and dispute trend analytics. |
| **Governance** | **Admin Management** | RBAC administrator provisioning, password resets, and deactivation safeguards. |
| **Security** | **Audit Logs** | Immutable, searchable operational log of all administrative actions. |
| **System** | **Settings** | Platform parameters and security policies. |

---

## 🔒 Security & Governance

The platform has passed a strict **20-point security audit**:

1. **Secrets Management**: Zero hardcoded API keys or credentials; all environment-driven.
2. **Rate Limiting**: Thread-safe login rate limiter with max 5 failed attempts per account/IP with a 15-minute sliding lockout window.
3. **Defensive Input Sanitization**: All form inputs validated with regex and sanitized against script tags.
4. **Strict RBAC**: Gated routes with `@RolesAllowed("SUPER_ADMIN")` and `@RolesAllowed({"SUPER_ADMIN", "ADMIN"})`.
5. **Security Headers**: HSTS, Referrer-Policy, nosniff, and Clickjacking frame protection.
6. **Isolated Sandbox Testing**: Demo financial simulations isolated from real marketplace GMV calculations.

For complete audit findings, see [Security Audit Report](docs/SECURITY_AUDIT.md).

---

## 🚀 Quickstart & Setup

### Prerequisites
- **JDK 21** or later
- **Maven 3.9+** (or use the included `./mvnw.cmd` / `./mvnw`)
- Access to Supabase PostgreSQL or local H2 database

### 1. Clone & Configure Environment
```bash
cp .env.example .env
```
Edit `.env` and specify your database pooler credentials:
```properties
SPRING_PROFILES_ACTIVE=prod
SUPABASE_DB_HOST=aws-0-ap-southeast-1.pooler.supabase.com
SUPABASE_DB_PORT=5432
SUPABASE_DB_NAME=postgres
SUPABASE_DB_USERNAME=udyogasethu_admin_app.mmzbpeqkfomnywuzoqii
SUPABASE_DB_PASSWORD=your_password_here
SUPERADMIN_EMAIL=admin@udyogasethu.org
SUPERADMIN_PASSWORD=your_initial_password_here
```

### 2. Run Locally (Local H2 Mode)
```bash
./mvnw.cmd spring-boot:run
```
Access the portal at `http://localhost:8080`.

### 3. Run with Live Supabase Connection
```bash
./mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=prod
```

### 4. Run Test Suite
```bash
./mvnw.cmd test
```

---

## 📚 Documentation Directory

- [`docs/FINAL_VERIFICATION_REPORT.md`](docs/FINAL_VERIFICATION_REPORT.md) — Comprehensive final independent verification report, test matrix, and release verdict.
- [`docs/GITHUB_PUBLICATION_CHECKLIST.md`](docs/GITHUB_PUBLICATION_CHECKLIST.md) — Pre-publication security checklist, secret scans, and Git publication commands.
- [`docs/SUPABASE_INTEGRATION_AUDIT.md`](docs/SUPABASE_INTEGRATION_AUDIT.md) — Live Supabase connection, RLS architecture, and mobile workflow verification.
- [`docs/SECURITY_AUDIT.md`](docs/SECURITY_AUDIT.md) — Full 20-point application security audit.
- [`docs/FINAL_PRE_GITHUB_REPORT.md`](docs/FINAL_PRE_GITHUB_REPORT.md) — Module status report and database integration details.
- [`docs/ENV_REFERENCE.md`](docs/ENV_REFERENCE.md) — Reference guide for all configuration variables.
