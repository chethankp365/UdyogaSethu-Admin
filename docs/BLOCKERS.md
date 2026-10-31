# UdyogaSethu Admin — Project Blockers & Dependency Tracker

This document tracks items that are pending external inputs (credentials, keys) in strict accordance with the Master Prompt instructions: *"If something is blocked (missing credential, inaccessible schema), STOP only that item, record it in `docs/BLOCKERS.md`, and continue all independent work."*

---

## Active External Dependency Items

*None. All external dependencies and connection pooler integrations are fully resolved, hardened, and active.*

---

## Blockers Resolution Log
| Date | Item | Status | Resolved By | Notes |
| :--- | :--- | :--- | :--- | :--- |
| 2026-10-30 | Java 21 & Maven Tooling | **RESOLVED** | Portable JDK 21.0.13 & Maven 3.9.6 configured at `D:\tools` | PATH and JAVA_HOME persistent |
| 2026-10-30 | Flutter Source & Schema | **RESOLVED** | Located at `D:\Projects\Udyoga Sethu` | 107 migrations audited |
| 2026-10-31 | Supabase DB Connectivity | **RESOLVED** | Pooler host configured with `udyogasethu_admin_app` role | Least-privilege schema access verified |
| 2026-10-31 | Admin Authentication | **RESOLVED** | BCrypt credential hashing & `admin.admin_users` table | Verified with `Admin@Setu2026!` |
| 2026-11-01 | PostgreSQL Enum Casts & Functions | **RESOLVED** | Added `upper()`/`lower()` SQL helper functions in public schema | Fixed JPA IgnoreCase query compatibility |

