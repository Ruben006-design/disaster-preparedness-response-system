# REST API Specification
## Disaster Preparedness and Response Education System

Base URL: `http://localhost:8080/api`

All authenticated endpoints require an Authorization Header:
```
Authorization: Bearer <JWT_ACCESS_TOKEN>
```

---

### 1. Authentication & Identity (`/auth`)

#### `POST /auth/register`
Creates a new system account.
- **Request Body:**
  ```json
  {
    "name": "Jane Doe",
    "email": "jane.doe@university.edu",
    "password": "Password@123",
    "phone": "+1 555-0199",
    "role": "STUDENT",
    "officerRegistrationCode": ""
  }
  ```
  *(Note: `role="GOVERNMENT_OFFICER"` requires `officerRegistrationCode="OFFICER_GOV_AUTHORIZATION_2026"`; `role="ADMIN"` requires `"ADMIN_DISASTER_SECURE_2026"`)*
- **Response (200 OK):**
  ```json
  {
    "success": true,
    "message": "User registered successfully",
    "data": {
      "token": "eyJhbGciOiJIUzUxMiJ9...",
      "type": "Bearer",
      "id": 6,
      "name": "Jane Doe",
      "email": "jane.doe@university.edu",
      "role": "STUDENT"
    }
  }
  ```

#### `POST /auth/login`
Authenticates existing credentials.
- **Request Body:**
  ```json
  {
    "email": "admin@disasterprep.gov",
    "password": "AdminPassword@123"
  }
  ```

---

### 2. Disaster Reporting Workflow (`/reports`)

#### `POST /reports` (Student, Officer, Admin)
Submits a citizen disaster report. Generates a unique incident code (e.g. `DR-2026-ABC123`).
- **Request Body:**
  ```json
  {
    "disasterTypeId": 1,
    "severity": "HIGH",
    "title": "Severe Flash Flood Inundation near Sector 4 Bridge",
    "description": "Rapidly rising water levels submerged residential ground floors. 4 families trapped.",
    "address": "4th Cross Road, Riverview Suburb",
    "latitude": 37.7749,
    "longitude": -122.4194,
    "aftermath": "Vehicular access completely blocked",
    "contactPhone": "+1 555-0182"
  }
  ```

#### `POST /reports/{reportId}/evidence`
Uploads visual evidence (JPG, PNG, MP4, PDF) associated with the report.
- **Request (Multipart):** `file=@flood_scene.jpg`

#### `GET /reports/{id}`
Returns incident details, status timeline history, uploaded evidence links, and dispatched response agency info.

---

### 3. Government Officer Triage & Response (`/officer`)

#### `POST /officer/reports/{id}/verify` (Officers & Admins only)
Officially confirms an authentic disaster. Marks `officialVerified=true` and `status=VERIFIED`, publishing the hazard to the public map.
- **Request Body:**
  ```json
  {
    "verificationNotes": "Confirmed with municipal police control room. Immediate rescue team required."
  }
  ```

#### `POST /officer/reports/{id}/reject` (Officers & Admins only)
Rejects an invalid or duplicate report. **Mandatory reason required.**
- **Request Body:**
  ```json
  {
    "rejectionReason": "Duplicate report of incident DR-2026-FLOOD-01. Coordinates verified already."
  }
  ```

#### `POST /officer/reports/{id}/assign` (Officers & Admins only)
Dispatches an official department and response team to a `VERIFIED` incident.
- **Request Body:**
  ```json
  {
    "departmentId": 1,
    "responseTeamId": 2,
    "priority": "HIGH",
    "instructions": "Deploy flood inflatable rafts; coordinate with local community leaders on site."
  }
  ```

#### `PATCH /officer/reports/{id}/status`
Advances the incident lifecycle (`RESPONSE_IN_PROGRESS` $\rightarrow$ `SITUATION_CONTROLLED` $\rightarrow$ `RESOLVED`).

---

### 4. Interactive Education & Preparedness (`/education`)

#### `GET /education/disasters`
List of disaster categories (Earthquakes, Floods, Wildfires, Cyclones).

#### `GET /education/modules`
Curriculum modules with sections, safety procedures, and preparedness guidelines.

#### `POST /education/modules/{id}/complete`
Records module completion and updates the user's Preparedness Assessment score.

#### `GET /education/quizzes/{id}`
Retrieves quiz questions and options.

#### `POST /education/quizzes/{id}/submit`
Submits student answers, calculates score, returns pass/fail, and adjusts overall preparedness.

#### `GET /education/simulations/{id}`
Retrieves emergency drill scenario with branching choice actions.

#### `POST /education/simulations/{id}/submit`
Submits student drill decision, evaluates consequence, safety feedback, and score delta.

#### `GET /education/preparedness-score`
Returns the transparent breakdown of the student's preparedness assessment (40% Modules, 40% Quizzes, 20% Simulations).

---

### 5. Public Emergency Directory & Map (`/contacts`, `/map`)

#### `GET /map/incidents`
Public endpoint returning verified active emergency incident markers with geo-coordinates.

#### `GET /contacts`
Public list of national 24/7 disaster hotlines, police, ambulance, and fire services.
