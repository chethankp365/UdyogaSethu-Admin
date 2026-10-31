# UdyogaSethu Admin — System Architecture & Layered Design

## 1. Architectural Philosophy

**UdyogaSethu Admin** is an enterprise administration web platform engineered with **Java 21**, **Spring Boot 3.3.x**, and **Vaadin Flow 24.4+**. It is designed to administer the live UdyogaSethu mobile ecosystem (workers, service providers, farmers, produce commerce, financial vault, and KYC verification) while strictly sharing the existing Supabase PostgreSQL database (`mmzbpeqkfomnywuzoqii`).

### Key Invariants:
1. **Zero Data Faking / Ground Truth**: Every metric, status, grid column, and drawer traces to verified columns from the 107 database migrations (`0000_ping.sql` through `0811_chat_rpc_and_storage.sql`). If a module has no backing data (e.g. raw audio storage), an explicit "Not available in current data model" state is presented.
2. **Double-Entry Financial & Mobile Flow Integrity**: The mobile application's `apply_to_job` flow, geofenced QR attendance, order state machine, and ledger trigger (`trg_prevent_ledger_mutation`) must never be broken or bypassed. Admin mutations are transactional, permission-gated, and audited.
3. **Dedicated Admin Isolation**: Admin users, RBAC permissions, audit log trails, and notification campaigns reside in a dedicated `admin` PostgreSQL schema, leaving `public` schemas intact and unmodified.

---

## 2. Layered Architecture

```
┌─────────────────────────────────────────────────────────────┐
│               PRESENTATION LAYER (Vaadin Flow)              │
│  - MainLayout (Branded Sidebar, Live Badges, Top Bar)       │
│  - 18 Domain Views (Dashboard, Users, Jobs, Payments, ...)  │
│  - Shared Components: DataGridPage, KpiCard, StatusChip,    │
│    ConfirmReasonDialog, DetailDrawer, EChartComponent       │
└──────────────────────────────┬──────────────────────────────┘
                               │
┌──────────────────────────────▼──────────────────────────────┐
│          CONTROLLER & SECURITY LAYER (Spring Security)       │
│  - Vaadin Route Guards (BeforeEnterObserver)                │
│  - Method Security (@PreAuthorize("hasRole(...)"))          │
│  - AuditLogAspect / Transactional Audit Recording           │
└──────────────────────────────┬──────────────────────────────┘
                               │
┌──────────────────────────────▼──────────────────────────────┐
│                    SERVICE LAYER (Spring)                   │
│  - DashboardService, UserService, JobService,               │
│    MarketplaceService, FinancialService, VerificationService│
│    DisputeService, NotificationService, AdminService        │
│  - All business logic, validation, and audit events         │
└──────────────────────────────┬──────────────────────────────┘
                               │
┌──────────────────────────────▼──────────────────────────────┐
│                 DATA ACCESS LAYER (Spring Data)             │
│  - 20 JPA Repositories (ProfileRepository, JobRepository,   │
│    PaymentRepository, LedgerEntryRepository, ...)           │
│  - Server-Side Paging, Filtering, Sorting (Pageable)        │
└──────────────────────────────┬──────────────────────────────┘
                               │
┌──────────────────────────────▼──────────────────────────────┐
│              INFRASTRUCTURE & PERSISTENCE (Supabase)        │
│  - HikariCP Connection Pooler (SSL required)                │
│  - Schema `public` (Existing Mobile Tables, Read/Write)     │
│  - Schema `admin` (Isolated Admin Users, Audit, Demo)       │
│  - PostGIS Spatial Proximity & Append-Only Triggers         │
└─────────────────────────────────────────────────────────────┘
```

---

## 3. Technology Stack

| Layer | Component | Version / Specification | Rationale |
| :--- | :--- | :--- | :--- |
| **Language** | Java 21 LTS | OpenJDK 21.0.13 | Virtual threads, record classes, modern pattern matching |
| **Framework** | Spring Boot | 3.3.5 | Production-ready DI, Actuator health, Spring Data JPA |
| **Frontend** | Vaadin Flow | 24.4.12 | Pure Java server-side state, push websockets, zero JS boilerplate |
| **Charts** | Apache ECharts | 5.5.0 | Dynamic responsive charts, dark theme integration, zero commercial license fees |
| **Security** | Spring Security | 6.3.x | Method security, CSRF protection, secure cookie sessions |
| **Database** | PostgreSQL | 15.6 on Supabase | Connection pooler, PostGIS spatial extensions, RLS policies |
| **Migrations** | Flyway | 10.x | Dedicated `admin` schema versioned migrations |
