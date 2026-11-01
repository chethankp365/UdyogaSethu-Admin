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

## 4. Production Deployment on Render & Environment Variables

This application runs as a Java 21 web service on Render. Supabase remains the
shared PostgreSQL backend used by this admin application and the companion
mobile application; Render does not replace or copy the database.

### Deploy with the Render Blueprint

1. Push this repository to GitHub.
2. In Render, choose **New → Blueprint** and select the repository.
3. Render detects `render.yaml` and builds the Docker image using `Dockerfile`.
4. Enter the values for every environment variable marked `sync: false`.
5. Use the Supabase **session pooler** credentials for the deployment. Keep the
   database password and bootstrap password in Render's environment settings,
   never in the repository.

Render supplies `PORT` automatically. The application listens on that port and
uses `/actuator/health` for the Render health check.

Configure the Supabase values from the shared project's **Connect → Session
pooler** panel:

```text
SUPABASE_DB_HOST=your-project-ref.pooler.supabase.com
SUPABASE_DB_PORT=6543
SUPABASE_DB_NAME=postgres
SUPABASE_DB_USERNAME=postgres.your-project-ref
SUPABASE_DB_PASSWORD=your_database_password
SUPERADMIN_EMAIL=admin@udyogasethu.org
SUPERADMIN_NAME=Platform Administrator
SUPERADMIN_PASSWORD=your_initial_secure_superadmin_password
```

Do not use the placeholder values above. The exact pooler hostname and
username must come from the shared Supabase project.
