-- ===================================================================
-- DISASTER PREPAREDNESS AND RESPONSE EDUCATION SYSTEM
-- Database Schema Definition (MySQL 8.0+)
-- ===================================================================

CREATE DATABASE IF NOT EXISTS disaster_response_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE disaster_response_db;

-- 1. USERS
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(120) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    role VARCHAR(30) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_email (email),
    INDEX idx_user_role (role)
) ENGINE=InnoDB;

-- 2. DISASTER TYPES
CREATE TABLE IF NOT EXISTS disaster_types (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    code VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    icon VARCHAR(50),
    default_severity VARCHAR(20) NOT NULL DEFAULT 'MEDIUM',
    warning_signs TEXT,
    safety_procedures TEXT,
    do_list TEXT,
    dont_list TEXT,
    before_steps TEXT,
    during_steps TEXT,
    after_steps TEXT,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    INDEX idx_disaster_code (code)
) ENGINE=InnoDB;

-- 3. LEARNING MODULES
CREATE TABLE IF NOT EXISTS learning_modules (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    disaster_type_id BIGINT NOT NULL,
    summary TEXT,
    content LONGTEXT NOT NULL,
    estimated_minutes INT DEFAULT 15,
    media_url VARCHAR(255),
    order_index INT DEFAULT 0,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (disaster_type_id) REFERENCES disaster_types(id) ON DELETE CASCADE,
    INDEX idx_module_disaster (disaster_type_id)
) ENGINE=InnoDB;

-- 4. MODULE PROGRESS
CREATE TABLE IF NOT EXISTS module_progress (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    module_id BIGINT NOT NULL,
    is_completed BOOLEAN NOT NULL DEFAULT FALSE,
    completion_percentage INT NOT NULL DEFAULT 0,
    last_accessed_at DATETIME,
    completed_at DATETIME,
    UNIQUE KEY uk_user_module (user_id, module_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (module_id) REFERENCES learning_modules(id) ON DELETE CASCADE,
    INDEX idx_progress_user (user_id),
    INDEX idx_progress_module (module_id)
) ENGINE=InnoDB;

-- 5. QUIZZES
CREATE TABLE IF NOT EXISTS quizzes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    disaster_type_id BIGINT NOT NULL,
    description TEXT,
    passing_score INT NOT NULL DEFAULT 70,
    time_limit_minutes INT DEFAULT 10,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (disaster_type_id) REFERENCES disaster_types(id) ON DELETE CASCADE,
    INDEX idx_quiz_disaster (disaster_type_id)
) ENGINE=InnoDB;

-- 6. QUIZ QUESTIONS
CREATE TABLE IF NOT EXISTS quiz_questions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    quiz_id BIGINT NOT NULL,
    question_text TEXT NOT NULL,
    explanation TEXT,
    points INT NOT NULL DEFAULT 10,
    order_index INT DEFAULT 0,
    FOREIGN KEY (quiz_id) REFERENCES quizzes(id) ON DELETE CASCADE,
    INDEX idx_question_quiz (quiz_id)
) ENGINE=InnoDB;

-- 7. QUIZ OPTIONS
CREATE TABLE IF NOT EXISTS quiz_options (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    question_id BIGINT NOT NULL,
    option_text TEXT NOT NULL,
    is_correct BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (question_id) REFERENCES quiz_questions(id) ON DELETE CASCADE,
    INDEX idx_option_question (question_id)
) ENGINE=InnoDB;

-- 8. QUIZ RESULTS
CREATE TABLE IF NOT EXISTS quiz_results (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    quiz_id BIGINT NOT NULL,
    score INT NOT NULL,
    total_questions INT NOT NULL,
    correct_answers INT NOT NULL,
    is_passed BOOLEAN NOT NULL DEFAULT FALSE,
    completed_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (quiz_id) REFERENCES quizzes(id) ON DELETE CASCADE,
    INDEX idx_qresult_user (user_id),
    INDEX idx_qresult_quiz (quiz_id)
) ENGINE=InnoDB;

-- 9. SIMULATIONS
CREATE TABLE IF NOT EXISTS simulations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    disaster_type_id BIGINT NOT NULL,
    scenario_text TEXT NOT NULL,
    location_context VARCHAR(150),
    initial_risk_level VARCHAR(20) NOT NULL DEFAULT 'HIGH',
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (disaster_type_id) REFERENCES disaster_types(id) ON DELETE CASCADE,
    INDEX idx_sim_disaster (disaster_type_id)
) ENGINE=InnoDB;

-- 10. SIMULATION OPTIONS
CREATE TABLE IF NOT EXISTS simulation_options (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    simulation_id BIGINT NOT NULL,
    action_text TEXT NOT NULL,
    consequence_text TEXT,
    score_delta INT NOT NULL DEFAULT 10,
    is_appropriate BOOLEAN NOT NULL DEFAULT FALSE,
    safety_feedback TEXT,
    FOREIGN KEY (simulation_id) REFERENCES simulations(id) ON DELETE CASCADE,
    INDEX idx_simopt_sim (simulation_id)
) ENGINE=InnoDB;

-- 11. SIMULATION RESULTS
CREATE TABLE IF NOT EXISTS simulation_results (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    simulation_id BIGINT NOT NULL,
    selected_option_id BIGINT NOT NULL,
    score_earned INT NOT NULL,
    completed_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (simulation_id) REFERENCES simulations(id) ON DELETE CASCADE,
    FOREIGN KEY (selected_option_id) REFERENCES simulation_options(id) ON DELETE CASCADE,
    INDEX idx_simres_user (user_id),
    INDEX idx_simres_sim (simulation_id)
) ENGINE=InnoDB;

-- 12. GOVERNMENT DEPARTMENTS
CREATE TABLE IF NOT EXISTS government_departments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    code VARCHAR(50) NOT NULL UNIQUE,
    department_type VARCHAR(40) NOT NULL,
    description TEXT,
    contact_email VARCHAR(120),
    contact_phone VARCHAR(30),
    head_name VARCHAR(100),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    INDEX idx_dept_code (code)
) ENGINE=InnoDB;

-- 13. RESPONSE TEAMS
CREATE TABLE IF NOT EXISTS response_teams (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    department_id BIGINT NOT NULL,
    team_name VARCHAR(120) NOT NULL,
    team_lead VARCHAR(100),
    contact_phone VARCHAR(30),
    status VARCHAR(50) NOT NULL DEFAULT 'AVAILABLE',
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    FOREIGN KEY (department_id) REFERENCES government_departments(id) ON DELETE CASCADE,
    INDEX idx_team_dept (department_id)
) ENGINE=InnoDB;

-- 14. DISASTER REPORTS
CREATE TABLE IF NOT EXISTS disaster_reports (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    report_code VARCHAR(30) NOT NULL UNIQUE,
    reporter_id BIGINT NOT NULL,
    disaster_type_id BIGINT NOT NULL,
    severity VARCHAR(20) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'SUBMITTED',
    title VARCHAR(150) NOT NULL,
    description TEXT NOT NULL,
    address VARCHAR(255) NOT NULL,
    latitude DOUBLE NOT NULL,
    longitude DOUBLE NOT NULL,
    aftermath TEXT,
    contact_phone VARCHAR(30),
    rejection_reason TEXT,
    verified_by_id BIGINT,
    verified_at DATETIME,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (reporter_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (disaster_type_id) REFERENCES disaster_types(id) ON DELETE RESTRICT,
    FOREIGN KEY (verified_by_id) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_report_code (report_code),
    INDEX idx_report_status (status),
    INDEX idx_report_severity (severity),
    INDEX idx_report_reporter (reporter_id),
    INDEX idx_report_created (created_at)
) ENGINE=InnoDB;

-- 15. EVIDENCES
CREATE TABLE IF NOT EXISTS evidences (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    report_id BIGINT NOT NULL,
    original_file_name VARCHAR(255) NOT NULL,
    stored_file_name VARCHAR(255) NOT NULL,
    file_type VARCHAR(100) NOT NULL,
    file_size BIGINT NOT NULL,
    file_url VARCHAR(500) NOT NULL,
    file_category VARCHAR(20) NOT NULL DEFAULT 'IMAGE',
    uploaded_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (report_id) REFERENCES disaster_reports(id) ON DELETE CASCADE,
    INDEX idx_evidence_report (report_id)
) ENGINE=InnoDB;

-- 16. INCIDENT UPDATES
CREATE TABLE IF NOT EXISTS incident_updates (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    report_id BIGINT NOT NULL,
    updated_by_id BIGINT,
    previous_status VARCHAR(30),
    new_status VARCHAR(30) NOT NULL,
    remarks TEXT,
    timestamp DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (report_id) REFERENCES disaster_reports(id) ON DELETE CASCADE,
    FOREIGN KEY (updated_by_id) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_update_report (report_id)
) ENGINE=InnoDB;

-- 17. RESPONSE ASSIGNMENTS
CREATE TABLE IF NOT EXISTS response_assignments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    report_id BIGINT NOT NULL UNIQUE,
    department_id BIGINT NOT NULL,
    response_team_id BIGINT,
    assigned_by_id BIGINT NOT NULL,
    priority VARCHAR(20) NOT NULL DEFAULT 'HIGH',
    instructions TEXT,
    assigned_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (report_id) REFERENCES disaster_reports(id) ON DELETE CASCADE,
    FOREIGN KEY (department_id) REFERENCES government_departments(id) ON DELETE RESTRICT,
    FOREIGN KEY (response_team_id) REFERENCES response_teams(id) ON DELETE SET NULL,
    FOREIGN KEY (assigned_by_id) REFERENCES users(id) ON DELETE RESTRICT,
    INDEX idx_assign_report (report_id),
    INDEX idx_assign_dept (department_id),
    INDEX idx_assign_team (response_team_id)
) ENGINE=InnoDB;

-- 18. EMERGENCY CONTACTS
CREATE TABLE IF NOT EXISTS emergency_contacts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    service_name VARCHAR(120) NOT NULL,
    organization VARCHAR(150) NOT NULL,
    phone_number VARCHAR(30) NOT NULL,
    alternate_phone VARCHAR(30),
    location VARCHAR(100),
    priority_order INT DEFAULT 0,
    is_active BOOLEAN NOT NULL DEFAULT TRUE
) ENGINE=InnoDB;

-- 19. NOTIFICATIONS
CREATE TABLE IF NOT EXISTS notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    recipient_id BIGINT NOT NULL,
    title VARCHAR(150) NOT NULL,
    message TEXT NOT NULL,
    type VARCHAR(40) NOT NULL,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    reference_id VARCHAR(50),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (recipient_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_notif_recipient (recipient_id),
    INDEX idx_notif_created (created_at)
) ENGINE=InnoDB;
