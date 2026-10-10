-- ===================================================================
-- DISASTER PREPAREDNESS AND RESPONSE EDUCATION SYSTEM
-- Sample SQL Queries for College Viva, Demonstration & Lab Exam
-- ===================================================================

USE disaster_response_db;

-- -------------------------------------------------------------------
-- QUERY 1: Disaster Report Lifecycle & Response Workflow
-- Shows the entire pipeline: Reporter -> Disaster -> Verification -> Assigned Department & Team
-- -------------------------------------------------------------------
SELECT 
    r.report_code AS 'Report Code',
    r.title AS 'Incident Title',
    dt.name AS 'Disaster Type',
    r.severity AS 'Severity',
    r.status AS 'Current Status',
    reporter.name AS 'Reported By (Student)',
    reporter.email AS 'Student Email',
    verifier.name AS 'Verified By (Officer)',
    dept.name AS 'Assigned Department',
    team.team_name AS 'Response Team',
    r.created_at AS 'Reported At'
FROM disaster_reports r
JOIN disaster_types dt ON r.disaster_type_id = dt.id
JOIN users reporter ON r.reporter_id = reporter.id
LEFT JOIN users verifier ON r.verified_by_id = verifier.id
LEFT JOIN response_assignments ra ON r.id = ra.report_id
LEFT JOIN government_departments dept ON ra.department_id = dept.id
LEFT JOIN response_teams team ON ra.response_team_id = team.id
ORDER BY r.created_at DESC;

-- -------------------------------------------------------------------
-- QUERY 2: Student Educational Preparedness Progress
-- Computes module completion percentage and quiz performance per student
-- -------------------------------------------------------------------
SELECT 
    u.id AS 'Student ID',
    u.name AS 'Student Name',
    u.email AS 'Email',
    COUNT(DISTINCT mp.module_id) AS 'Modules Started',
    SUM(CASE WHEN mp.is_completed = TRUE THEN 1 ELSE 0 END) AS 'Modules Completed',
    AVG(COALESCE(qr.score, 0)) AS 'Average Quiz Score (%)',
    SUM(CASE WHEN qr.is_passed = TRUE THEN 1 ELSE 0 END) AS 'Quizzes Passed'
FROM users u
LEFT JOIN module_progress mp ON u.id = mp.user_id
LEFT JOIN quiz_results qr ON u.id = qr.user_id
WHERE u.role = 'STUDENT'
GROUP BY u.id, u.name, u.email;

-- -------------------------------------------------------------------
-- QUERY 3: Government Officer Incident Queue (Awaiting Verification)
-- Shows all incoming student submissions that require officer action
-- -------------------------------------------------------------------
SELECT 
    r.id AS 'Report ID',
    r.report_code AS 'Report Code',
    r.title AS 'Title',
    dt.name AS 'Disaster Type',
    r.severity AS 'Severity',
    r.address AS 'Location',
    r.latitude,
    r.longitude,
    r.created_at AS 'Submission Timestamp',
    TIMESTAMPDIFF(MINUTE, r.created_at, NOW()) AS 'Minutes Elapsed'
FROM disaster_reports r
JOIN disaster_types dt ON r.disaster_type_id = dt.id
WHERE r.status = 'SUBMITTED'
ORDER BY 
    CASE r.severity
        WHEN 'CRITICAL' THEN 1
        WHEN 'HIGH' THEN 2
        WHEN 'MEDIUM' THEN 3
        WHEN 'LOW' THEN 4
        ELSE 5
    END,
    r.created_at ASC;

-- -------------------------------------------------------------------
-- QUERY 4: Government Department Workload & Team Availability
-- Shows how many emergency incidents are assigned to each department
-- -------------------------------------------------------------------
SELECT 
    d.name AS 'Department',
    d.department_type AS 'Type',
    COUNT(DISTINCT t.id) AS 'Total Response Teams',
    SUM(CASE WHEN t.status = 'AVAILABLE' THEN 1 ELSE 0 END) AS 'Available Teams',
    SUM(CASE WHEN t.status = 'DEPLOYED' THEN 1 ELSE 0 END) AS 'Deployed Teams',
    COUNT(DISTINCT ra.id) AS 'Active Assigned Incidents'
FROM government_departments d
LEFT JOIN response_teams t ON d.id = t.department_id AND t.is_active = TRUE
LEFT JOIN response_assignments ra ON d.id = ra.department_id
WHERE d.is_active = TRUE
GROUP BY d.id, d.name, d.department_type;

-- -------------------------------------------------------------------
-- QUERY 5: Incident Audit Trail & Status Timeline
-- Demonstrates the state-machine transitions of a specific report
-- -------------------------------------------------------------------
SELECT 
    r.report_code AS 'Report Code',
    iu.previous_status AS 'From Status',
    iu.new_status AS 'To Status',
    iu.remarks AS 'Officer Remarks',
    u.name AS 'Action Taken By',
    iu.timestamp AS 'Transition Time'
FROM incident_updates iu
JOIN disaster_reports r ON iu.report_id = r.id
LEFT JOIN users u ON iu.updated_by_id = u.id
ORDER BY iu.report_id, iu.timestamp ASC;

-- -------------------------------------------------------------------
-- QUERY 6: Executive Analytics Dashboard (Admin Overview)
-- Aggregates key system metrics into a single summary report
-- -------------------------------------------------------------------
SELECT 
    (SELECT COUNT(*) FROM users WHERE role = 'STUDENT') AS 'Total Students Registered',
    (SELECT COUNT(*) FROM users WHERE role = 'GOVERNMENT_OFFICER') AS 'Active Officers',
    (SELECT COUNT(*) FROM disaster_reports) AS 'Total Reports Filed',
    (SELECT COUNT(*) FROM disaster_reports WHERE status = 'SUBMITTED') AS 'Pending Verification',
    (SELECT COUNT(*) FROM disaster_reports WHERE status = 'RESPONSE_IN_PROGRESS') AS 'In Active Response',
    (SELECT COUNT(*) FROM disaster_reports WHERE status = 'RESOLVED') AS 'Resolved Incidents',
    (SELECT COUNT(*) FROM emergency_contacts WHERE is_active = TRUE) AS 'Active Hotlines';
