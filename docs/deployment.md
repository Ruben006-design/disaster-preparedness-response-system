# Deployment & Operations Guide
## Disaster Preparedness and Response Education System

### 1. Overview
The platform consists of:
1. **Frontend:** React 18 + Vite + MUI (Nginx reverse proxy container, port `3000:80`)
2. **Backend:** Java 21 / Spring Boot 3.3.4 (REST API container, port `8080:8080`)
3. **Database:** MySQL 8.0 (Relational storage container, port `3307:3306`)

---

### 2. Docker Compose Deployment (Recommended)
The root directory includes a turnkey `docker-compose.yml` configured with healthchecks, environment parameters, and persistent disk volumes.

#### Launching the Cluster
```bash
# In the project root:
docker-compose up -d --build
```

#### Verifying Services
```bash
# Check container status
docker-compose ps

# Tail backend service logs
docker-compose logs -f backend
```

Access Points:
- **Web Application Portal:** `http://localhost:3000`
- **Backend REST API:** `http://localhost:8080/api`
- **MySQL Container:** `localhost:3307` (`disaster_prep_db` / `disaster_admin` / `DisasterPrep@2026`)

---

### 3. Native Local Host Deployment

#### 3.1 Database Initialization
1. Ensure MySQL 8.0 is running on your host machine.
2. Create database and load schema:
   ```bash
   mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS disaster_prep_db CHARACTER SET utf8mb4;"
   mysql -u root -p disaster_prep_db < database/schema.sql
   ```

#### 3.2 Backend Service Launch
1. Ensure Java 17+ or Java 21/25 is installed.
2. Build and run Spring Boot:
   ```bash
   cd backend
   mvn clean spring-boot:run
   ```
   *(DataInitializer automatically seeds initial Admin, Officer, Student accounts, modules, quizzes, and simulations on first boot)*

#### 3.3 Frontend Service Launch
1. Ensure Node.js 18+ and npm are installed.
2. Install dependencies and start Vite dev server:
   ```bash
   cd frontend
   npm install
   npm run dev
   ```
   *(Vite automatically proxies `/api` requests to `http://localhost:8080`)*

---

### 4. Default Seeded User Credentials

| Role | Email | Password | Access Capabilities |
| :--- | :--- | :--- | :--- |
| **System Administrator** | `admin@disasterprep.gov` | `AdminPassword@123` | Full governance, users directory, agency fleets |
| **Government Officer 1** | `officer.sharma@disasterprep.gov` | `OfficerPass@123` | Triage queue, report verification, department dispatch |
| **Government Officer 2** | `officer.chen@disasterprep.gov` | `OfficerPass@123` | Regional audit & containment coordination |
| **Citizen / Student 1** | `student.alex@university.edu` | `StudentPass@123` | Educational modules, drills, GPS disaster reporting |
| **Citizen / Student 2** | `student.priya@university.edu` | `StudentPass@123` | Drills, quizzes, emergency hotline directory |

#### Secret Registration Clearance Codes
- **Government Officer Access:** `OFFICER_GOV_AUTHORIZATION_2026`
- **System Administrator Access:** `ADMIN_DISASTER_SECURE_2026`

---

### 5. Production Environment Variables Reference

| Variable | Description | Default / Example |
| :--- | :--- | :--- |
| `DB_HOST` | MySQL hostname | `localhost` / `mysqldb` |
| `DB_PORT` | MySQL connection port | `3306` |
| `DB_NAME` | Database schema name | `disaster_prep_db` |
| `DB_USERNAME` | Database username | `disaster_admin` |
| `DB_PASSWORD` | Database password | `DisasterPrep@2026` |
| `JWT_SECRET` | Secret key for HMAC-SHA512 signing | Base64-encoded 512-bit key |
| `JWT_EXPIRATION_MS` | Access token lifespan | `86400000` (24 Hours) |
| `FILE_UPLOAD_DIR` | Physical evidence disk directory | `./uploads/` |
| `MAX_FILE_SIZE` | Maximum upload size per file | `20MB` |
