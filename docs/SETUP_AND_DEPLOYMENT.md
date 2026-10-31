# UdyogaSethu Admin — Setup, Local Run & Deployment Guide

## 1. Prerequisites
- **JDK**: Java 21 LTS (OpenJDK / Eclipse Temurin 21+)
- **Maven**: 3.9.x (Included via project `mvnw.cmd` / `mvnw`)
- **Database**: PostgreSQL 15+ (Supabase connection pooler for production) or built-in H2 for local mode.

---

## 2. Local Development Run

1. Clone or navigate to the repository:
   ```bash
   cd "d:/UdyogaSethu Admin"
   ```
2. Verify Java and Maven wrapper:
   ```powershell
   .\mvnw.cmd -version
   ```
3. Run test suite:
   ```powershell
   .\mvnw.cmd test
   ```
4. Start the application locally:
   ```powershell
   .\mvnw.cmd spring-boot:run
   ```
5. Open your browser and navigate to:
   ```
   http://localhost:8080
   ```
6. Sign in with the default bootstrapped administrator:
   - **Admin Email**: `admin@udyogasethu.org`
   - **Password**: `admin123` (or any non-empty password in local profile)

---

## 3. Production Build & Packaging

Build an optimized production artifact with compiled and minified frontend assets:
```bash
./mvnw clean package -Pproduction
```
The resulting executable JAR will be located at `target/udyogasethu-admin-1.0.0-SNAPSHOT.jar`.

---

## 4. Production Deployment & Environment Variables

When running in production, set the active Spring profile and provide Supabase connection details:

```bash
export SPRING_PROFILES_ACTIVE=prod
export PORT=8080

# Supabase PostgreSQL Connection Pooler
export SUPABASE_DB_HOST="aws-0-ap-south-1.pooler.supabase.com"
export SUPABASE_DB_PORT=6543
export SUPABASE_DB_NAME="postgres"
export SUPABASE_DB_USERNAME="postgres.mmzbpeqkfomnywuzoqii"
export SUPABASE_DB_PASSWORD="your_database_password"

# Initial Super Admin Bootstrapping
export SUPERADMIN_EMAIL="admin@udyogasethu.org"
export SUPERADMIN_NAME="Platform Administrator"

java -jar target/udyogasethu-admin-1.0.0-SNAPSHOT.jar
```
