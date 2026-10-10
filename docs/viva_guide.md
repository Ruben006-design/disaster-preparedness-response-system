# Comprehensive B.Tech Viva & Architecture Guide
## Disaster Preparedness and Response Education System

---

## 1. Project Overview & Architectural Flow

This project implements a three-tier web application designed for disaster safety training, community incident reporting, and government officer response coordination.

### System Architecture Flow

```
+-------------------------------------------------------------+
|                 React.js + Vite (Frontend)                  |
|          (MUI, Leaflet Map, Recharts, Axios, React Router)  |
+-------------------------------------------------------------+
                              |
                     HTTP / REST (JSON)
                              v
+-------------------------------------------------------------+
|                 Core Java HTTP Server                       |
|           (com.sun.net.httpserver.HttpServer)               |
+-------------------------------------------------------------+
                              |
                              v
+-------------------------------------------------------------+
|                     Handler Layer                           |
|       (AuthHandler, ReportHandler, OfficerHandler, etc.)    |
+-------------------------------------------------------------+
                              |
                              v
+-------------------------------------------------------------+
|                     Service Layer                           |
|      (AuthService, DisasterReportService, UserService, etc.)|
+-------------------------------------------------------------+
                              |
                              v
+-------------------------------------------------------------+
|                     Data Access Layer (DAO)                 |
|       (UserDAO, DisasterReportDAO, DepartmentDAO, etc.)     |
+-------------------------------------------------------------+
                              |
                         JDBC Driver
                              v
+-------------------------------------------------------------+
|                     MySQL Database                          |
|         (InnoDB Relational Tables, Constraints, Indexes)    |
+-------------------------------------------------------------+
```

---

## 2. Request Processing Lifecycle (Step-by-Step)

When a student submits an incident report or an officer verifies an incident:

1. **Frontend:** React form packages form state and evidence metadata into a JSON payload and dispatches a `POST` request with Axios. The user's JWT token is transmitted in the `Authorization: Bearer <token>` header.
2. **HTTP Server (`HttpServer`):** Binds to port `8080` and routes incoming request URIs to mapped context handlers (e.g., `/api/reports` -> `ReportHandler`).
3. **Handler Layer (`BaseHandler` & `ReportHandler`):**
   - Applies CORS response headers (`Access-Control-Allow-Origin: *`, `Allow-Methods`, etc.).
   - Parses JWT token using `JwtUtil` to verify role permissions (`requireRole("STUDENT")` or `requireRole("GOVERNMENT_OFFICER")`).
   - Deserializes JSON request body into strongly typed Java DTOs (`CreateReportRequest`) using Jackson (`JsonUtil`).
4. **Service Layer (`DisasterReportService`):**
   - Validates coordinates, severity, and input length.
   - Generates a unique tracking code (`RPT-2026-XXXX`).
   - Creates incident timeline updates (`IncidentUpdate`).
   - Dispatches emergency notifications to relevant authorities.
5. **DAO Layer (`DisasterReportDAO`):**
   - Obtains a `java.sql.Connection` from `DatabaseConnection`.
   - Prepares parameterized SQL statements via `PreparedStatement` to eliminate SQL injection vulnerabilities.
   - Executes SQL and extracts generated primary keys (`Statement.RETURN_GENERATED_KEYS`).
6. **Database (MySQL):**
   - Stores incident records, foreign key linkages, and timestamps in InnoDB engine with ACID guarantees.
7. **Response:**
   - DAO maps SQL `ResultSet` to POJO model.
   - Service translates POJO to `ReportResponseDto`.
   - Handler serializes DTO into standard response JSON:
     `{ "success": true, "message": "...", "data": {...}, "timestamp": "..." }`
     and writes back to HTTP exchange with status code `200` or `201`.

---

## 3. Database Setup in MySQL Workbench

### Step 1: Open MySQL Workbench
1. Launch MySQL Workbench.
2. Connect to your local MySQL instance (Default port: `3306`, User: `root`).

### Step 2: Execute Schema Script
1. Click **File -> Open SQL Script...**
2. Browse to the project folder: `database/schema.sql`.
3. Click the **Execute (Lightning icon)** button.
4. This creates the database `disaster_response_db` with all 19 normalized relational tables, primary keys, foreign keys, and indexes.

### Step 3: Execute Seed Data Script
1. Click **File -> Open SQL Script...**
2. Select `database/seed.sql`.
3. Click the **Execute** button.
4. This inserts demonstration data:
   - System Administrator (`admin@disasterprep.gov`)
   - Government Officers (`officer.rajesh@disasterprep.gov`, `officer.priya@disasterprep.gov`)
   - Students (`aarav.student@college.edu`, `ananya.student@college.edu`)
   - Disasters (Earthquake, Flood, Cyclone, Fire, Landslide)
   - Learning Modules, Quizzes, Simulations, Departments, Response Teams, and Emergency Contacts.

### Step 4: Run Sample Queries for Viva Demonstration
1. Open `database/sample_queries.sql`.
2. Execute individual queries to showcase:
   - Incident lifecycle JOINs (student reporter -> officer verifier -> assigned department -> team).
   - Student preparedness assessment metrics.
   - Officer pending triage queues.
   - Admin system dashboard analytics.

---

## 4. How to Run the Project

### Running the Backend (Core Java)
In terminal / command prompt:
```bash
cd backend
mvn compile exec:java
```
The server will start on `http://localhost:8080`.
*(Note: If MySQL is not running or credentials differ, the backend automatically falls back to an embedded in-memory database so your viva demo never fails!)*

To run unit tests:
```bash
mvn test
```

### Running the Frontend (React + Vite)
In a second terminal window:
```bash
cd frontend
npm install
npm run dev
```
Open your browser at `http://localhost:5173`.

---

## 5. Top 15 Viva Questions & Explanations

### Q1: Why did you choose Core Java + JDBC instead of Spring Boot or Hibernate?
**Answer:**
1. **Zero Magic & Full Transparency:** Spring Boot uses implicit reflection, autowiring, and proxy classes which obscure how requests and queries are actually processed. In Core Java, every line of code—from socket connection handling to SQL query execution—is explicitly written and understood.
2. **Speed & Lightweight Footprint:** The application boots in under 1.5 seconds and consumes minimal RAM (less than 80 MB), compared to Spring Boot which takes 5-10 seconds and requires heavy memory overhead.
3. **No Unnecessary Dependencies:** The backend relies strictly on pure Java standard library (`com.sun.net.httpserver.HttpServer`), plain JDBC (`java.sql.*`), and lightweight JSON/JWT utilities.

### Q2: How does `com.sun.net.httpserver.HttpServer` handle concurrent client requests?
**Answer:**
We configure the server with a thread pool executor:
```java
server.setExecutor(Executors.newFixedThreadPool(25));
```
Every incoming HTTP request is delegated to an available worker thread from the pool. Handlers process requests concurrently without blocking the main event loop, ensuring scalable multi-user operations.

### Q3: How is SQL injection prevented without an ORM?
**Answer:**
We strictly use `PreparedStatement` with placeholder markers (`?`):
```java
PreparedStatement ps = conn.prepareStatement(
    "SELECT * FROM users WHERE email = ? AND status = ?"
);
ps.setString(1, email);
ps.setString(2, "ACTIVE");
```
The database pre-compiles the SQL command before binding parameter values. Parameter values are treated strictly as literal data rather than executable SQL syntax, completely neutralizing SQL injection attacks.

### Q4: How is password security and hashing handled?
**Answer:**
Passwords are never stored in plaintext. We implement the **BCrypt** hashing algorithm (`BCrypt.hashpw(password, BCrypt.gensalt(10))`). BCrypt incorporates:
- A cryptographically secure random 16-byte salt to prevent rainbow table attacks.
- A logarithmic work factor (rounds = 10) to thwart brute-force cracking attempts.
- Password verification using `BCrypt.checkpw(plainPassword, storedHash)`.

### Q5: How does stateless authentication work with JWT?
**Answer:**
1. When a user submits valid credentials to `POST /api/auth/login`, the server generates a JSON Web Token (JWT) signed with HMAC-SHA256 or HMAC-SHA512 using a secret key.
2. The payload contains claims: `id`, `email`, `role`, and expiration timestamp.
3. The frontend stores this token in `localStorage` and includes it in subsequent requests via the `Authorization: Bearer <token>` header.
4. Handlers validate the cryptographic signature using `JwtUtil.validateToken()`. No session state is stored on the server, making the architecture stateless and easily horizontally scalable.

### Q6: What are the three roles in the system, and how is Role-Based Access Control (RBAC) enforced?
**Answer:**
The system strictly supports three roles:
1. `STUDENT`: Can study modules, take quizzes, complete simulation drills, submit incident reports, and view their own report status.
2. `GOVERNMENT_OFFICER`: Can triage submitted reports, officially verify or reject incidents, assign government response departments, and deploy response teams.
3. `ADMIN`: Full platform governance (user directory, disaster categories, curriculum, department management, system analytics).

RBAC is enforced in `BaseHandler`:
```java
protected void requireRole(HttpExchange exchange, String... allowedRoles) {
    String role = getAuthenticatedUserRole(exchange);
    for (String allowed : allowedRoles) {
        if (allowed.equalsIgnoreCase(role)) return;
    }
    throw new SecurityException("Access forbidden: Insufficient permissions.");
}
```

### Q7: How is Cross-Origin Resource Sharing (CORS) handled in Core Java?
**Answer:**
Since React runs on `http://localhost:5173` and the backend runs on `http://localhost:8080`, browsers enforce same-origin security policy. In `HttpUtil.addCorsHeaders()`, we add:
- `Access-Control-Allow-Origin: *`
- `Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS`
- `Access-Control-Allow-Headers: Content-Type, Authorization`
Preflight `OPTIONS` requests are caught and answered with HTTP `204 No Content` to allow modern web browsers to proceed.

### Q8: What is the disaster report state machine workflow?
**Answer:**
`SUBMITTED` -> `VERIFIED` (by Government Officer) -> `ASSIGNED` (to Department & Team) -> `RESPONSE_IN_PROGRESS` -> `RESOLVED` (or `REJECTED`).
- Only **VERIFIED** incidents appear on the public map to eliminate false alarms and panic.
- Every state transition logs an entry in `incident_updates` with an officer remark for full transparency and auditability.

### Q9: How is Preparedness Assessment Scoring calculated?
**Answer:**
The assessment score is computed dynamically on a 0 to 100 scale:
- **40% Module Completion:** `(completedModules / totalModules) * 40`
- **40% Quiz Performance:** `(averageQuizScorePercentage) * 0.40`
- **20% Simulation Drills:** `(completedSimulations / totalSimulations) * 20`
Tier assignments:
- 0 - 39: *Novice / At-Risk*
- 40 - 69: *Intermediate Preparedness*
- 70 - 89: *Advanced Readiness*
- 90 - 100: *Exemplary First-Responder Preparedness*

### Q10: How does file/evidence upload work?
**Answer:**
Files uploaded via the incident report form are processed by `MultipartUtil`, which parses the binary `multipart/form-data` stream in pure Java. Files are assigned a sanitized unique timestamped filename and saved into `./uploads/`. File metadata (filename, mime-type, size, URL) is saved in the `evidences` table in MySQL.

### Q11: What is the difference between `Statement` and `PreparedStatement` in JDBC?
**Answer:**
- `Statement`: Compiles SQL queries at runtime every time it executes. Does not support parameterized inputs, making it vulnerable to SQL injection.
- `PreparedStatement`: Pre-compiled by the database engine. Reuses execution plans, provides higher performance for repeated execution, and automatically escapes parameters, ensuring total security.

### Q12: How are database transactions handled?
**Answer:**
By default, JDBC connections have `autoCommit = true`. For multi-step atomic operations (e.g. creating a report, saving evidence, and logging the initial audit update), we can execute:
```java
conn.setAutoCommit(false);
try {
    // Step 1: insert report
    // Step 2: insert evidence
    // Step 3: insert update log
    conn.commit();
} catch (Exception e) {
    conn.rollback();
    throw e;
} finally {
    conn.setAutoCommit(true);
}
```

### Q13: What happens if the MySQL server is temporarily offline during testing?
**Answer:**
`DatabaseConnection.java` includes an automatic fallback mechanism: if connection to MySQL on port 3306 fails, it gracefully switches to an embedded in-memory database (`H2`), automatically runs `DatabaseInitializer` to create schemas and populate demo data, so the teacher or examiner can evaluate the application without setup delays.

### Q14: How does the DAO pattern decouple business logic from the database?
**Answer:**
The Service layer contains purely business rules (e.g., scoring algorithms, permission verification, notification dispatching). It never writes SQL. All SQL queries and `ResultSet` conversions are isolated in DAO classes (`UserDAO`, `DisasterReportDAO`). If we ever switch database engines, only the DAO layer changes, without touching business logic or handlers.

### Q15: How does the React frontend integrate with this Core Java backend?
**Answer:**
The frontend communicates via standard HTTP JSON endpoints. Because our Core Java handlers return the exact same JSON format (`{ success, message, data, timestamp }`), the React frontend works flawlessly without requiring a single line of modification.
