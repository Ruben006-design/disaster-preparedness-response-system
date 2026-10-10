package com.disasterresponse.database;

import com.disasterresponse.dao.*;
import com.disasterresponse.enums.*;
import com.disasterresponse.model.*;
import com.disasterresponse.util.BCrypt;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Initializes database tables and seed data on server startup.
 * Automatically creates all tables if missing and seeds standard academic data.
 */
public class DatabaseInitializer {

    public static void initialize() {
        System.out.println("[DatabaseInitializer] Checking database state...");
        try (Connection conn = DatabaseConnection.getConnection()) {
            boolean tablesExist = checkTableExists(conn, "users");
            if (!tablesExist) {
                System.out.println("[DatabaseInitializer] Database tables not found. Creating schema...");
                createSchema(conn);
                System.out.println("[DatabaseInitializer] Schema created successfully.");
            }

            UserDAO userDAO = new UserDAO();
            if (userDAO.countAll() == 0) {
                System.out.println("[DatabaseInitializer] Empty database detected. Seeding initial data...");
                seedData();
                System.out.println("[DatabaseInitializer] Seed data populated successfully.");
            } else {
                System.out.println("[DatabaseInitializer] Database already contains records. Checking curriculum...");
            }
            seedAdditionalCurriculumIfMissing();
        } catch (Exception e) {
            System.err.println("[DatabaseInitializer] Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static boolean checkTableExists(Connection conn, String tableName) {
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT 1 FROM " + tableName + " LIMIT 1")) {
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static void createSchema(Connection conn) throws Exception {
        String[] ddlStatements = new String[]{
                "CREATE TABLE IF NOT EXISTS users (" +
                        "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                        "name VARCHAR(100) NOT NULL, " +
                        "email VARCHAR(120) NOT NULL UNIQUE, " +
                        "password VARCHAR(255) NOT NULL, " +
                        "phone VARCHAR(20), " +
                        "role VARCHAR(30) NOT NULL, " +
                        "status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE', " +
                        "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                        "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)",

                "CREATE TABLE IF NOT EXISTS disaster_types (" +
                        "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                        "name VARCHAR(100) NOT NULL, " +
                        "code VARCHAR(50) NOT NULL UNIQUE, " +
                        "description TEXT, " +
                        "icon VARCHAR(50), " +
                        "default_severity VARCHAR(20) NOT NULL DEFAULT 'MEDIUM', " +
                        "warning_signs TEXT, " +
                        "safety_procedures TEXT, " +
                        "do_list TEXT, " +
                        "dont_list TEXT, " +
                        "before_steps TEXT, " +
                        "during_steps TEXT, " +
                        "after_steps TEXT, " +
                        "is_active BOOLEAN NOT NULL DEFAULT TRUE)",

                "CREATE TABLE IF NOT EXISTS learning_modules (" +
                        "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                        "title VARCHAR(150) NOT NULL, " +
                        "disaster_type_id BIGINT NOT NULL, " +
                        "summary TEXT, " +
                        "content TEXT NOT NULL, " +
                        "estimated_minutes INT DEFAULT 15, " +
                        "media_url VARCHAR(255), " +
                        "order_index INT DEFAULT 0, " +
                        "is_active BOOLEAN NOT NULL DEFAULT TRUE, " +
                        "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                        "FOREIGN KEY (disaster_type_id) REFERENCES disaster_types(id) ON DELETE CASCADE)",

                "CREATE TABLE IF NOT EXISTS module_progress (" +
                        "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                        "user_id BIGINT NOT NULL, " +
                        "module_id BIGINT NOT NULL, " +
                        "is_completed BOOLEAN NOT NULL DEFAULT FALSE, " +
                        "completion_percentage INT NOT NULL DEFAULT 0, " +
                        "last_accessed_at TIMESTAMP, " +
                        "completed_at TIMESTAMP, " +
                        "UNIQUE (user_id, module_id), " +
                        "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE, " +
                        "FOREIGN KEY (module_id) REFERENCES learning_modules(id) ON DELETE CASCADE)",

                "CREATE TABLE IF NOT EXISTS quizzes (" +
                        "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                        "title VARCHAR(150) NOT NULL, " +
                        "disaster_type_id BIGINT NOT NULL, " +
                        "description TEXT, " +
                        "passing_score INT NOT NULL DEFAULT 70, " +
                        "time_limit_minutes INT DEFAULT 10, " +
                        "is_active BOOLEAN NOT NULL DEFAULT TRUE, " +
                        "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                        "FOREIGN KEY (disaster_type_id) REFERENCES disaster_types(id) ON DELETE CASCADE)",

                "CREATE TABLE IF NOT EXISTS quiz_questions (" +
                        "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                        "quiz_id BIGINT NOT NULL, " +
                        "question_text TEXT NOT NULL, " +
                        "explanation TEXT, " +
                        "points INT NOT NULL DEFAULT 10, " +
                        "order_index INT DEFAULT 0, " +
                        "FOREIGN KEY (quiz_id) REFERENCES quizzes(id) ON DELETE CASCADE)",

                "CREATE TABLE IF NOT EXISTS quiz_options (" +
                        "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                        "question_id BIGINT NOT NULL, " +
                        "option_text TEXT NOT NULL, " +
                        "is_correct BOOLEAN NOT NULL DEFAULT FALSE, " +
                        "FOREIGN KEY (question_id) REFERENCES quiz_questions(id) ON DELETE CASCADE)",

                "CREATE TABLE IF NOT EXISTS quiz_results (" +
                        "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                        "user_id BIGINT NOT NULL, " +
                        "quiz_id BIGINT NOT NULL, " +
                        "score INT NOT NULL, " +
                        "total_questions INT NOT NULL, " +
                        "correct_answers INT NOT NULL, " +
                        "is_passed BOOLEAN NOT NULL DEFAULT FALSE, " +
                        "completed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                        "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE, " +
                        "FOREIGN KEY (quiz_id) REFERENCES quizzes(id) ON DELETE CASCADE)",

                "CREATE TABLE IF NOT EXISTS simulations (" +
                        "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                        "title VARCHAR(150) NOT NULL, " +
                        "disaster_type_id BIGINT NOT NULL, " +
                        "scenario_text TEXT NOT NULL, " +
                        "location_context VARCHAR(150), " +
                        "initial_risk_level VARCHAR(20) NOT NULL DEFAULT 'HIGH', " +
                        "is_active BOOLEAN NOT NULL DEFAULT TRUE, " +
                        "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                        "FOREIGN KEY (disaster_type_id) REFERENCES disaster_types(id) ON DELETE CASCADE)",

                "CREATE TABLE IF NOT EXISTS simulation_options (" +
                        "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                        "simulation_id BIGINT NOT NULL, " +
                        "action_text TEXT NOT NULL, " +
                        "consequence_text TEXT, " +
                        "score_delta INT NOT NULL DEFAULT 10, " +
                        "is_appropriate BOOLEAN NOT NULL DEFAULT FALSE, " +
                        "safety_feedback TEXT, " +
                        "FOREIGN KEY (simulation_id) REFERENCES simulations(id) ON DELETE CASCADE)",

                "CREATE TABLE IF NOT EXISTS simulation_results (" +
                        "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                        "user_id BIGINT NOT NULL, " +
                        "simulation_id BIGINT NOT NULL, " +
                        "selected_option_id BIGINT NOT NULL, " +
                        "score_earned INT NOT NULL, " +
                        "completed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                        "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE, " +
                        "FOREIGN KEY (simulation_id) REFERENCES simulations(id) ON DELETE CASCADE, " +
                        "FOREIGN KEY (selected_option_id) REFERENCES simulation_options(id) ON DELETE CASCADE)",

                "CREATE TABLE IF NOT EXISTS government_departments (" +
                        "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                        "name VARCHAR(120) NOT NULL, " +
                        "code VARCHAR(50) NOT NULL UNIQUE, " +
                        "department_type VARCHAR(40) NOT NULL, " +
                        "description TEXT, " +
                        "contact_email VARCHAR(120), " +
                        "contact_phone VARCHAR(30), " +
                        "head_name VARCHAR(100), " +
                        "is_active BOOLEAN NOT NULL DEFAULT TRUE)",

                "CREATE TABLE IF NOT EXISTS response_teams (" +
                        "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                        "department_id BIGINT NOT NULL, " +
                        "team_name VARCHAR(120) NOT NULL, " +
                        "team_lead VARCHAR(100), " +
                        "contact_phone VARCHAR(30), " +
                        "status VARCHAR(50) NOT NULL DEFAULT 'AVAILABLE', " +
                        "is_active BOOLEAN NOT NULL DEFAULT TRUE, " +
                        "FOREIGN KEY (department_id) REFERENCES government_departments(id) ON DELETE CASCADE)",

                "CREATE TABLE IF NOT EXISTS disaster_reports (" +
                        "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                        "report_code VARCHAR(30) NOT NULL UNIQUE, " +
                        "reporter_id BIGINT NOT NULL, " +
                        "disaster_type_id BIGINT NOT NULL, " +
                        "severity VARCHAR(20) NOT NULL, " +
                        "status VARCHAR(30) NOT NULL DEFAULT 'SUBMITTED', " +
                        "title VARCHAR(150) NOT NULL, " +
                        "description TEXT NOT NULL, " +
                        "address VARCHAR(255) NOT NULL, " +
                        "latitude DOUBLE NOT NULL, " +
                        "longitude DOUBLE NOT NULL, " +
                        "aftermath TEXT, " +
                        "contact_phone VARCHAR(30), " +
                        "rejection_reason TEXT, " +
                        "verified_by_id BIGINT, " +
                        "verified_at TIMESTAMP, " +
                        "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                        "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                        "FOREIGN KEY (reporter_id) REFERENCES users(id) ON DELETE CASCADE, " +
                        "FOREIGN KEY (disaster_type_id) REFERENCES disaster_types(id), " +
                        "FOREIGN KEY (verified_by_id) REFERENCES users(id) ON DELETE SET NULL)",

                "CREATE TABLE IF NOT EXISTS evidences (" +
                        "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                        "report_id BIGINT NOT NULL, " +
                        "original_file_name VARCHAR(255) NOT NULL, " +
                        "stored_file_name VARCHAR(255) NOT NULL, " +
                        "file_type VARCHAR(100) NOT NULL, " +
                        "file_size BIGINT NOT NULL, " +
                        "file_url VARCHAR(500) NOT NULL, " +
                        "file_category VARCHAR(20) NOT NULL DEFAULT 'IMAGE', " +
                        "uploaded_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                        "FOREIGN KEY (report_id) REFERENCES disaster_reports(id) ON DELETE CASCADE)",

                "CREATE TABLE IF NOT EXISTS incident_updates (" +
                        "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                        "report_id BIGINT NOT NULL, " +
                        "updated_by_id BIGINT, " +
                        "previous_status VARCHAR(30), " +
                        "new_status VARCHAR(30) NOT NULL, " +
                        "remarks TEXT, " +
                        "timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                        "FOREIGN KEY (report_id) REFERENCES disaster_reports(id) ON DELETE CASCADE, " +
                        "FOREIGN KEY (updated_by_id) REFERENCES users(id) ON DELETE SET NULL)",

                "CREATE TABLE IF NOT EXISTS response_assignments (" +
                        "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                        "report_id BIGINT NOT NULL UNIQUE, " +
                        "department_id BIGINT NOT NULL, " +
                        "response_team_id BIGINT, " +
                        "assigned_by_id BIGINT NOT NULL, " +
                        "priority VARCHAR(20) NOT NULL DEFAULT 'HIGH', " +
                        "instructions TEXT, " +
                        "assigned_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                        "FOREIGN KEY (report_id) REFERENCES disaster_reports(id) ON DELETE CASCADE, " +
                        "FOREIGN KEY (department_id) REFERENCES government_departments(id), " +
                        "FOREIGN KEY (response_team_id) REFERENCES response_teams(id) ON DELETE SET NULL, " +
                        "FOREIGN KEY (assigned_by_id) REFERENCES users(id))",

                "CREATE TABLE IF NOT EXISTS emergency_contacts (" +
                        "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                        "service_name VARCHAR(120) NOT NULL, " +
                        "organization VARCHAR(150) NOT NULL, " +
                        "phone_number VARCHAR(30) NOT NULL, " +
                        "alternate_phone VARCHAR(30), " +
                        "location VARCHAR(100), " +
                        "priority_order INT DEFAULT 0, " +
                        "is_active BOOLEAN NOT NULL DEFAULT TRUE)",

                "CREATE TABLE IF NOT EXISTS notifications (" +
                        "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                        "recipient_id BIGINT NOT NULL, " +
                        "title VARCHAR(150) NOT NULL, " +
                        "message TEXT NOT NULL, " +
                        "type VARCHAR(40) NOT NULL, " +
                        "is_read BOOLEAN NOT NULL DEFAULT FALSE, " +
                        "reference_id VARCHAR(50), " +
                        "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                        "FOREIGN KEY (recipient_id) REFERENCES users(id) ON DELETE CASCADE)"
        };

        try (Statement stmt = conn.createStatement()) {
            for (String sql : ddlStatements) {
                stmt.execute(sql);
            }
        }
    }

    private static void seedData() {
        UserDAO userDAO = new UserDAO();
        DisasterDAO disasterDAO = new DisasterDAO();
        LearningModuleDAO moduleDAO = new LearningModuleDAO();
        QuizDAO quizDAO = new QuizDAO();
        SimulationDAO simDAO = new SimulationDAO();
        DepartmentDAO deptDAO = new DepartmentDAO();
        ResponseTeamDAO teamDAO = new ResponseTeamDAO();
        EmergencyContactDAO contactDAO = new EmergencyContactDAO();
        DisasterReportDAO reportDAO = new DisasterReportDAO();
        IncidentUpdateDAO updateDAO = new IncidentUpdateDAO();
        ResponseAssignmentDAO assignDAO = new ResponseAssignmentDAO();

        // 1. Seed Users
        User admin = userDAO.save(new User("System Administrator", "admin@disasterprep.gov", BCrypt.hashpw("AdminPassword@123", BCrypt.gensalt()), "+1-800-555-0100", RoleType.ADMIN));
        User officer1 = userDAO.save(new User("Officer Rajesh Sharma", "officer.sharma@disasterprep.gov", BCrypt.hashpw("OfficerPass@123", BCrypt.gensalt()), "+1-800-555-0111", RoleType.GOVERNMENT_OFFICER));
        User officer2 = userDAO.save(new User("Officer Maya Chen", "officer.chen@disasterprep.gov", BCrypt.hashpw("OfficerPass@123", BCrypt.gensalt()), "+1-800-555-0112", RoleType.GOVERNMENT_OFFICER));
        User student1 = userDAO.save(new User("Alex Rivera", "student.alex@university.edu", BCrypt.hashpw("StudentPass@123", BCrypt.gensalt()), "+1-800-555-0121", RoleType.STUDENT));
        User student2 = userDAO.save(new User("Priya Patel", "student.priya@university.edu", BCrypt.hashpw("StudentPass@123", BCrypt.gensalt()), "+1-800-555-0122", RoleType.STUDENT));

        // 2. Seed Disaster Types
        DisasterType eq = new DisasterType("Earthquake", "EQ-01", "Sudden violent shaking of the ground resulting from subterranean movements along geological faults or volcanic activity.", "LandslideOutlined", SeverityLevel.CRITICAL);
        eq.setWarningSigns("Sudden rumbling sound, animals behaving erratically, slight tremors preceding major shaking.");
        eq.setSafetyProcedures("Drop, Cover, and Hold on. Stay away from glass, exterior walls, and heavy unstable furniture.");
        eq.setDoList("Drop to your hands and knees.\nCover your head and neck under a sturdy table or desk.\nHold on until shaking completely ceases.\nCheck utilities for gas leaks before turning on switches.");
        eq.setDontList("Do not run outside while shaking is occurring.\nDo not use elevators under any circumstances.\nDo not stand in doorways.\nDo not light matches or flick electrical switches if gas is suspected.");
        eq.setBeforeSteps("Fasten heavy shelves securely to walls. Store breakable items in low, closed cabinets. Practice family earthquake drills.");
        eq.setDuringSteps("Drop, Cover, and Hold on immediately. If outdoors, move away from power cables, brick chimneys, and high-rise facades.");
        eq.setAfterSteps("Expect aftershocks. Listen to battery-powered radio for official updates. Evacuate if building structure appears compromised.");
        eq = disasterDAO.save(eq);

        DisasterType flood = new DisasterType("Flash Flood", "FL-01", "Rapid overflow of water submerging land that is usually dry, frequently triggered by intense rainfall or dam breaches.", "WaterOutlined", SeverityLevel.HIGH);
        flood.setWarningSigns("Rapidly rising water levels in drainage channels, muddy swirling water, persistent torrential downpours.");
        flood.setSafetyProcedures("Move to higher elevation immediately. Never walk, swim, or drive through moving flood waters.");
        flood.setDoList("Turn off main electricity breakers if safe to do so.\nMove critical valuables and medical kits to upper floors.\nFollow official evacuation orders without delay.");
        flood.setDontList("Never attempt to drive through water-covered bridges or roadways.\nDo not touch electrical cords while standing in water.\nNever drink tap water during flood events before boiling.");
        flood.setBeforeSteps("Learn your community flood plain elevation. Keep sandbags and waterproof containers accessible.");
        flood.setDuringSteps("Evacuate immediately to higher ground. Do not wait for water to enter your home.");
        flood.setAfterSteps("Avoid disaster zones. Throw away food that has come into contact with floodwater.");
        flood = disasterDAO.save(flood);

        DisasterType cyclone = new DisasterType("Cyclone / Hurricane", "CY-01", "Massive tropical storm system featuring sustained high-velocity winds, torrential rain, and devastating coastal storm surges.", "AirOutlined", SeverityLevel.CRITICAL);
        cyclone.setWarningSigns("Sudden drop in barometric pressure, intense coastal swells, red sky, official meteorological storm advisories.");
        cyclone.setSafetyProcedures("Board windows, secure outdoor objects, stay in an interior windowless room, and monitor emergency broadcasts.");
        cyclone.setDoList("Stock at least 72 hours of potable water and non-perishable foods.\nKeep phones and emergency battery banks fully charged.\nAnchor outdoor fixtures and vehicles.");
        cyclone.setDontList("Do not venture outside during the eye of the storm.\nDo not shelter in caravans or mobile structures.");
        cyclone.setBeforeSteps("Reinforce roofing sheets and trim tree branches near electrical wires.");
        cyclone.setDuringSteps("Remain indoors away from windows until all official warnings are lifted.");
        cyclone.setAfterSteps("Watch out for fallen power lines, broken gas mains, and weakened bridges.");
        cyclone = disasterDAO.save(cyclone);

        DisasterType fire = new DisasterType("Wildfire", "WF-01", "Uncontrolled inferno consuming combustible vegetation in rural and forested lands, capable of spreading at high speeds.", "LocalFireDepartmentOutlined", SeverityLevel.HIGH);
        fire.setWarningSigns("Dense smoke plumes on horizon, soot raining down, distinct smell of burning timber, high ambient temperature with dry winds.");
        fire.setSafetyProcedures("Create defensible space around structures, follow evacuation routes early, protect airways with N95 respirators.");
        fire.setDoList("Clear gutters of dry leaves and pine needles.\nKeep garden hoses connected and ladders readily positioned.\nEvacuate early when warnings are issued.");
        fire.setDontList("Do not delay evacuation to protect non-essential physical property.\nDo not drive through zero-visibility smoke corridors.");
        fire.setBeforeSteps("Designate two separate evacuation escape paths from your neighborhood.");
        fire.setDuringSteps("Close all doors and windows to prevent draft and ember intrusion.");
        fire.setAfterSteps("Inspect roofs and attics for hidden lingering embers.");
        fire = disasterDAO.save(fire);

        // 3. Seed Learning Modules
        LearningModule mod1 = new LearningModule("Mastering Earthquake Survival: The Drop, Cover & Hold Protocol", eq.getId(),
                "Comprehensive practical guide for safeguarding life during structural ground vibrations in schools and homes.",
                "<h3>Module Overview</h3><p>Earthquakes strike without predictable warning. Structural integrity can be compromised in fractions of a second...</p><h4>Key Action Points:</h4><ul><li><b>DROP</b> down to hands and knees</li><li><b>COVER</b> head and neck under durable furniture</li><li><b>HOLD ON</b> until vibrations stop</li></ul>",
                20);
        mod1.setOrderIndex(1);
        moduleDAO.save(mod1);

        LearningModule mod2 = new LearningModule("Flood Preparedness & 'Turn Around Don't Drown' Guidelines", flood.getId(),
                "Crucial flood water behavior, electrical hazard identification, and life-saving evacuation strategies.",
                "<h3>Flash Flooding Dynamics</h3><p>Just 6 inches of fast-moving water can knock an adult off their feet; 12 inches can carry away a small sedan...</p>",
                15);
        mod2.setOrderIndex(2);
        moduleDAO.save(mod2);

        // 4. Seed Quizzes
        Quiz q1 = new Quiz("Earthquake Preparedness Certification Quiz", eq.getId(), "Assess your knowledge of immediate response actions during high-intensity seismic shocks.", 70, 10);
        q1 = quizDAO.save(q1);

        QuizQuestion qq1 = quizDAO.saveQuestion(new QuizQuestion(q1.getId(), "When shaking starts while you are indoors, what is the safest immediate action?", "Drop, Cover, and Hold On minimizes trauma from falling debris.", 10, 1));
        quizDAO.saveOption(new QuizOption(qq1.getId(), "Sprint outdoors immediately", false));
        quizDAO.saveOption(new QuizOption(qq1.getId(), "Drop, Cover, and Hold On beneath sturdy furniture", true));
        quizDAO.saveOption(new QuizOption(qq1.getId(), "Take the elevator to ground level", false));
        quizDAO.saveOption(new QuizOption(qq1.getId(), "Stand on top of a table to avoid falling floor pieces", false));

        QuizQuestion qq2 = quizDAO.saveQuestion(new QuizQuestion(q1.getId(), "Why should you never use matches or flick light switches after an earthquake?", "Sparks or open flames can ignite gas leaks caused by ruptured pipes.", 10, 2));
        quizDAO.saveOption(new QuizOption(qq2.getId(), "Electrical current will reverse and shock you", false));
        quizDAO.saveOption(new QuizOption(qq2.getId(), "Broken gas pipelines may trigger catastrophic explosions", true));
        quizDAO.saveOption(new QuizOption(qq2.getId(), "It drains emergency grid battery power", false));
        quizDAO.saveOption(new QuizOption(qq2.getId(), "It creates excessive ambient heat", false));

        // 5. Seed Simulations
        Simulation sim1 = new Simulation("Earthquake Scenario: High-Rise School Classroom Under Seismic Tremor", eq.getId(),
                "You are on the 3rd floor of a campus building during lecture hours when severe tremors begin. Heavy ceiling light fixtures swing violently.",
                "3rd Floor Campus Academic Block", SeverityLevel.CRITICAL);
        sim1 = simDAO.save(sim1);

        simDAO.saveOption(new SimulationOption(sim1.getId(), "Sprint toward the stairwell immediately",
                "Violent shaking throws you against concrete walls; stairwells are prone to structural detachment during tremors.", -20, false, "Do NOT run while ground is actively vibrating. Falling debris is the #1 cause of casualty."));
        simDAO.saveOption(new SimulationOption(sim1.getId(), "Drop to the floor, take cover beneath a heavy wooden study desk, and hold firmly",
                "Desk protects you against falling fluorescent bulbs and plaster. You sustain zero head injuries.", 30, true, "Optimal execution of Drop, Cover, and Hold On protocol."));
        simDAO.saveOption(new SimulationOption(sim1.getId(), "Rush toward the external window to signal for help",
                "Shattering window glass causes severe lacerations and hazard exposure.", -30, false, "Keep clear of glass, exterior walls, and overhead lighting fixtures."));

        // 6. Seed Government Departments & Teams
        GovernmentDepartment fireDept = deptDAO.save(new GovernmentDepartment("National Fire & Disaster Rescue Service", "NFDRS-01", DepartmentType.FIRE_AND_RESCUE, "contact@fire-rescue.gov", "+1-800-FIRE-001"));
        teamDAO.save(new ResponseTeam(fireDept.getId(), "Alpha Heavy Rescue Unit", "Capt. Marcus Vance", "+1-800-555-4011"));
        teamDAO.save(new ResponseTeam(fireDept.getId(), "Bravo Urban Flood Battalion", "Lt. Sarah Jenkins", "+1-800-555-4012"));

        GovernmentDepartment dmDept = deptDAO.save(new GovernmentDepartment("State Disaster Management Authority (SDMA)", "SDMA-01", DepartmentType.DISASTER_MANAGEMENT, "hq@sdma.gov", "+1-800-SDMA-100"));
        teamDAO.save(new ResponseTeam(dmDept.getId(), "SDMA Rapid Assessment Strike Team", "Commander David Cole", "+1-800-555-4021"));

        GovernmentDepartment medDept = deptDAO.save(new GovernmentDepartment("Emergency Medical Response Command", "EMRC-01", DepartmentType.MEDICAL_EMERGENCY, "dispatch@emrc-med.gov", "+1-800-MED-9911"));
        teamDAO.save(new ResponseTeam(medDept.getId(), "Mobile Triage Field Unit 1", "Dr. Anita Roy", "+1-800-555-4031"));

        // 7. Seed Emergency Contacts
        contactDAO.save(new EmergencyContact("Central Emergency Dispatch (Police / Fire / EMS)", "National Public Safety", "911", "112", "National Coverage", 1));
        contactDAO.save(new EmergencyContact("Disaster Management Rapid Response Helpline", "SDMA Command", "1070", "1077", "State Emergency Operation Center", 2));
        contactDAO.save(new EmergencyContact("National Flood & Water Rescue Hotline", "Civil Defense & Coast Guard", "1078", "+1-800-111-9999", "Coast & Inland Riverways", 3));
        contactDAO.save(new EmergencyContact("Ambulance & Critical Care Transport", "National Health Service", "108", "102", "Nationwide Medical Dispatch", 4));
        contactDAO.save(new EmergencyContact("Gas Leakage & Industrial Hazmat Hotline", "Public Utilities Commission", "1906", "+1-800-999-4276", "Municipal Gas Grids", 5));

        // 8. Seed Sample Disaster Reports (representing full workflow)
        DisasterReport r1 = new DisasterReport("RPT-2026-0001", student1.getId(), eq.getId(), SeverityLevel.CRITICAL,
                "Structural Wall Cracks in North Campus Dormitory Building",
                "Noticeable diagonal shearing fractures across outer brick masonry following recent tremors. Visible mortar crumbling near 2nd floor balcony.",
                "North Campus Dormitory Quad, Gate 3, Sector 12", 37.7749, -122.4194);
        r1.setContactPhone("+1-800-555-0121");
        r1.setStatus(ReportStatus.ASSIGNED);
        r1 = reportDAO.save(r1);
        reportDAO.verifyReport(r1.getId(), officer1.getId());
        reportDAO.updateStatus(r1.getId(), ReportStatus.ASSIGNED);
        assignDAO.save(new ResponseAssignment(r1.getId(), fireDept.getId(), 1L, officer1.getId(), SeverityLevel.CRITICAL, "Dispatch structural evaluation squad immediately. Cordon dorm entrance."));
        updateDAO.save(new IncidentUpdate(r1.getId(), officer1.getId(), "SUBMITTED", "VERIFIED", "Officer inspection confirmed hazardous masonry fracture."));
        updateDAO.save(new IncidentUpdate(r1.getId(), officer1.getId(), "VERIFIED", "ASSIGNED", "Assigned to National Fire & Rescue Alpha Unit for perimeter stabilization."));

        DisasterReport r2 = new DisasterReport("RPT-2026-0002", student2.getId(), flood.getId(), SeverityLevel.HIGH,
                "Flash Flood Inundation at Riverside Elementary Road Crossing",
                "Storm drainage overflowed. Rapid current water depth reached ~2.5 feet across the main vehicle bridge. Multiple student buses stranded on approach.",
                "Riverside Boulevard & 5th Ave Crossing", 37.7833, -122.4167);
        r2.setContactPhone("+1-800-555-0122");
        r2.setStatus(ReportStatus.RESPONSE_IN_PROGRESS);
        r2 = reportDAO.save(r2);
        reportDAO.verifyReport(r2.getId(), officer2.getId());
        reportDAO.updateStatus(r2.getId(), ReportStatus.RESPONSE_IN_PROGRESS);
        assignDAO.save(new ResponseAssignment(r2.getId(), fireDept.getId(), 2L, officer2.getId(), SeverityLevel.HIGH, "Deploy inflatable rescue boats to extract bus passengers."));
        updateDAO.save(new IncidentUpdate(r2.getId(), officer2.getId(), "SUBMITTED", "VERIFIED", "Officer Chen verified live drone camera feed of submerged roadway."));
        updateDAO.save(new IncidentUpdate(r2.getId(), officer2.getId(), "ASSIGNED", "RESPONSE_IN_PROGRESS", "Bravo Urban Flood Battalion on scene with watercraft."));

        DisasterReport r3 = new DisasterReport("RPT-2026-0003", student1.getId(), cyclone.getId(), SeverityLevel.MEDIUM,
                "High-Velocity Tree Branch Damage Over School Power Feeder",
                "Heavy branches severed overhead primary power cables near gymnasium, sparking on wet tarmac.",
                "University Sports Complex, Arena Road", 37.7690, -122.4467);
        r3.setContactPhone("+1-800-555-0121");
        r3.setStatus(ReportStatus.SUBMITTED);
        reportDAO.save(r3);
    }

    private static void seedAdditionalCurriculumIfMissing() {
        try {
            LearningModuleDAO moduleDAO = new LearningModuleDAO();
            QuizDAO quizDAO = new QuizDAO();
            SimulationDAO simDAO = new SimulationDAO();
            DisasterDAO disasterDAO = new DisasterDAO();

            List<DisasterType> disasters = disasterDAO.findAll();
            if (disasters.isEmpty()) return;

            Long eqId = null, floodId = null, cycloneId = null, fireId = null;
            for (DisasterType dt : disasters) {
                String code = dt.getCode() != null ? dt.getCode().toUpperCase() : "";
                String name = dt.getName() != null ? dt.getName().toUpperCase() : "";
                if (code.contains("EQ") || name.contains("EARTHQUAKE")) eqId = dt.getId();
                else if (code.contains("FL") || name.contains("FLOOD")) floodId = dt.getId();
                else if (code.contains("CY") || name.contains("CYCLONE") || name.contains("HURRICANE")) cycloneId = dt.getId();
                else if (code.contains("FIRE") || code.contains("WF") || name.contains("FIRE") || name.contains("WILDFIRE")) fireId = dt.getId();
            }
            if (eqId == null) eqId = disasters.get(0).getId();
            if (floodId == null) floodId = disasters.size() > 1 ? disasters.get(1).getId() : eqId;
            if (cycloneId == null) cycloneId = disasters.size() > 2 ? disasters.get(2).getId() : floodId;
            if (fireId == null) fireId = disasters.size() > 3 ? disasters.get(3).getId() : cycloneId;

            // 1. Learning Modules
            List<LearningModule> existingModules = moduleDAO.findAllIncludingInactive();
            boolean hasCycloneModule = existingModules.stream().anyMatch(m -> m.getTitle().toLowerCase().contains("cyclone"));
            boolean hasWildfireModule = existingModules.stream().anyMatch(m -> m.getTitle().toLowerCase().contains("wildfire") || m.getTitle().toLowerCase().contains("fire"));

            if (!hasCycloneModule && cycloneId != null) {
                LearningModule mod3 = new LearningModule("Cyclone & Severe Storm Survival Guide: Coastal Hazard Mitigation", cycloneId,
                        "Essential storm surge precautions, structural windproofing, and 72-hour survival kit staging for coastal institutions.",
                        "<h3>Cyclone Dynamics & Early Warning Signs</h3><p>Cyclones and hurricanes generate destructive storm surges, sustained winds surpassing 120 km/h, and torrential rainfall. Timely physical preparedness saves lives.</p><h4>Crucial Preparedness Protocols:</h4><ul><li><b>Window Protection:</b> Install storm shutters or board windows securely. Tape on glass provides zero protection against violent debris.</li><li><b>Outdoor Clearance:</b> Secure loose rooftop solar heaters, satellite dishes, antenna masts, and outdoor patio furniture that can become lethal missiles.</li><li><b>Shelter Selection:</b> Stay in an interior windowless room on the lowest safe floor (above projected storm surge levels).</li><li><b>The 'Eye' Illusion:</b> Do NOT leave shelter during the sudden calm when the eye passes; violent reverse winds resume abruptly!</li></ul>",
                        25);
                mod3.setOrderIndex(3);
                moduleDAO.save(mod3);
                System.out.println("[DatabaseInitializer] Added Cyclone learning module.");
            }

            if (!hasWildfireModule && fireId != null) {
                LearningModule mod4 = new LearningModule("Wildfire Evacuation & Smoke Mitigation Protocol", fireId,
                        "Wildfire behavior, creating defensible space, respiratory defense, and vehicle evacuation through low-visibility corridors.",
                        "<h3>Wildfire Behavior and Ember Cast</h3><p>Wildfires travel exceptionally fast uphill and throw burning embers kilometers ahead of the main firefront, igniting roofs and brush unpredictably.</p><h4>Key Action Directives:</h4><ul><li><b>Defensible Space:</b> Clear 30-100 feet around buildings of dead foliage, firewood stacks, and low overhanging pine limbs.</li><li><b>Respiratory Protection:</b> Standard surgical masks or bandanas do NOT stop toxic smoke particles. Always utilize certified N95 or P100 particulate respirators.</li><li><b>Immediate Evacuation Route:</b> Never wait for flames to be visible from your doorstep. If evacuation is ordered, leave immediately with headlights on low beam and hazard lights flashing.</li><li><b>Vehicle Staging:</b> Back your vehicle into the driveway with doors unlocked and emergency go-bags loaded for instant departure.</li></ul>",
                        20);
                mod4.setOrderIndex(4);
                moduleDAO.save(mod4);
                System.out.println("[DatabaseInitializer] Added Wildfire learning module.");
            }

            // 2. Quizzes
            List<Quiz> existingQuizzes = quizDAO.findAll();
            boolean hasFloodQuiz = existingQuizzes.stream().anyMatch(q -> q.getTitle().toLowerCase().contains("flood"));
            boolean hasCycloneQuiz = existingQuizzes.stream().anyMatch(q -> q.getTitle().toLowerCase().contains("cyclone"));
            boolean hasFireQuiz = existingQuizzes.stream().anyMatch(q -> q.getTitle().toLowerCase().contains("fire") || q.getTitle().toLowerCase().contains("wildfire"));

            if (!hasFloodQuiz && floodId != null) {
                Quiz q2 = new Quiz("Flash Flood Dynamics & Water Safety Assessment", floodId,
                        "Test your understanding of rapidly rising flood waters, hydrodynamic forces on vehicles, and electrical hazard isolation.", 70, 10);
                q2 = quizDAO.save(q2);
                QuizQuestion qq1 = quizDAO.saveQuestion(new QuizQuestion(q2.getId(), "What is the primary danger when attempting to drive a vehicle through flowing flood water?", "Just 12 to 24 inches of moving water can float most vehicles, including SUVs, and sweep them downstream into deep currents.", 10, 1));
                quizDAO.saveOption(new QuizOption(qq1.getId(), "Vehicle engine oil will contaminate the rainwater", false));
                quizDAO.saveOption(new QuizOption(qq1.getId(), "Moving water easily creates buoyancy that sweeps vehicles away into fatal currents", true));
                quizDAO.saveOption(new QuizOption(qq1.getId(), "Brake pads will wear out faster than normal", false));
                quizDAO.saveOption(new QuizOption(qq1.getId(), "Vehicle headlights cause electrical arcing in water", false));

                QuizQuestion qq2 = quizDAO.saveQuestion(new QuizQuestion(q2.getId(), "What is the recommended personal safety response when a flash flood alert is broadcast for your low-lying neighborhood?", "Flash floods escalate in minutes; moving immediately to higher ground is the single most effective survival strategy.", 10, 2));
                quizDAO.saveOption(new QuizOption(qq2.getId(), "Move immediately to higher ground or upper building stories", true));
                quizDAO.saveOption(new QuizOption(qq2.getId(), "Attempt to build makeshift sandbag barriers in the front yard", false));
                quizDAO.saveOption(new QuizOption(qq2.getId(), "Wade across the nearest drainage ditch to inspect water depth", false));
                quizDAO.saveOption(new QuizOption(qq2.getId(), "Wait until flood water enters your room before taking action", false));
                System.out.println("[DatabaseInitializer] Added Flood quiz.");
            }

            if (!hasCycloneQuiz && cycloneId != null) {
                Quiz q3 = new Quiz("Cyclone Preparedness & High-Wind Safety Evaluation", cycloneId,
                        "Assess your preparedness for severe tropical cyclonic storms, hurricane eyewall dynamics, and shelter selection.", 70, 10);
                q3 = quizDAO.save(q3);
                QuizQuestion qq1 = quizDAO.saveQuestion(new QuizQuestion(q3.getId(), "What should you do if the weather suddenly turns calm and sunny in the middle of a severe cyclone?", "The eye of the storm brings a temporary lull, followed rapidly by peak destructive winds blowing from the opposite direction.", 10, 1));
                quizDAO.saveOption(new QuizOption(qq1.getId(), "Go outside to survey neighborhood damage and clear debris", false));
                quizDAO.saveOption(new QuizOption(qq1.getId(), "Drive to the beach to observe ocean swells", false));
                quizDAO.saveOption(new QuizOption(qq1.getId(), "Remain in shelter because the destructive back half of the storm is imminent", true));
                quizDAO.saveOption(new QuizOption(qq1.getId(), "Open all windows to equalize indoor atmospheric air pressure", false));

                QuizQuestion qq2 = quizDAO.saveQuestion(new QuizQuestion(q3.getId(), "Where is the safest place to seek shelter inside a building during a category-4 cyclone?", "Interior rooms without exterior glass minimize vulnerability to high-velocity airborne debris and shattered glass.", 10, 2));
                quizDAO.saveOption(new QuizOption(qq2.getId(), "Near wide glass windows to observe falling tree branches", false));
                quizDAO.saveOption(new QuizOption(qq2.getId(), "In an interior windowless room, hallway, or sturdy bathroom on a safe floor", true));
                quizDAO.saveOption(new QuizOption(qq2.getId(), "Under a sheet metal roof awning or temporary sunshade", false));
                quizDAO.saveOption(new QuizOption(qq2.getId(), "On the top floor balcony overlooking the wind direction", false));
                System.out.println("[DatabaseInitializer] Added Cyclone quiz.");
            }

            if (!hasFireQuiz && fireId != null) {
                Quiz q4 = new Quiz("Wildfire Defense & Firebreak Tactics Assessment", fireId,
                        "Evaluate critical survival knowledge concerning rapid wildland fire spread, defensible barriers, and respiratory safety.", 70, 10);
                q4 = quizDAO.save(q4);
                QuizQuestion qq1 = quizDAO.saveQuestion(new QuizQuestion(q4.getId(), "Which mask type provides verified protection against microscopic toxic smoke particulates in wildfire zones?", "N95, N99, or P100 respirators filter fine 0.3-micron PM2.5 particulates, whereas cloth/cloth bandanas offer minimal protection.", 10, 1));
                quizDAO.saveOption(new QuizOption(qq1.getId(), "A damp cotton bandana or surgical paper mask", false));
                quizDAO.saveOption(new QuizOption(qq1.getId(), "A certified N95 or P100 particulate respirator", true));
                quizDAO.saveOption(new QuizOption(qq1.getId(), "A nylon face scarf folded into double layers", false));
                quizDAO.saveOption(new QuizOption(qq1.getId(), "A loose dust mask with side ventilation gaps", false));

                QuizQuestion qq2 = quizDAO.saveQuestion(new QuizQuestion(q4.getId(), "What is the primary objective of creating a 'defensible space' around school and residential structures?", "Removing combustible biomass reduces radiant heat exposure and prevents embers from catching roof eaves and exterior walls.", 10, 2));
                quizDAO.saveOption(new QuizOption(qq2.getId(), "To provide a landing zone for emergency helicopters", false));
                quizDAO.saveOption(new QuizOption(qq2.getId(), "To slow or stop wildfire spread and prevent embers from igniting the building", true));
                quizDAO.saveOption(new QuizOption(qq2.getId(), "To capture rainwater runoff for irrigation purposes", false));
                quizDAO.saveOption(new QuizOption(qq2.getId(), "To increase airflow so smoke disperses faster", false));
                System.out.println("[DatabaseInitializer] Added Wildfire quiz.");
            }

            // 3. Simulations
            List<Simulation> existingSims = simDAO.findAll();
            boolean hasFloodSim = existingSims.stream().anyMatch(s -> s.getTitle().toLowerCase().contains("flood"));
            boolean hasCycloneSim = existingSims.stream().anyMatch(s -> s.getTitle().toLowerCase().contains("cyclone"));
            boolean hasFireSim = existingSims.stream().anyMatch(s -> s.getTitle().toLowerCase().contains("wildfire") || s.getTitle().toLowerCase().contains("fire"));

            if (!hasFloodSim && floodId != null) {
                Simulation sim2 = new Simulation("Flash Flood Emergency: Submerged Campus Parking Underpass", floodId,
                        "Torrential downpours have flooded the lower campus. You are walking toward your vehicle parked in the basement underpass and notice water level is rapidly rising past wheel hubs (~18 inches) with strong current.",
                        "Campus Lower Parking Lot & Storm Underpass", SeverityLevel.HIGH);
                sim2 = simDAO.save(sim2);
                simDAO.saveOption(new SimulationOption(sim2.getId(), "Wade through the current quickly to get inside your car and drive out",
                        "The rising current sweeps you off your feet against submerged metal rails, trapping you in fast-rising water.", -25, false, "Never enter moving floodwaters. Just 6 inches can knock you down, and cars stall and float easily in 18 inches."));
                simDAO.saveOption(new SimulationOption(sim2.getId(), "Immediately turn around, abandon vehicle retrieval, and ascend exterior stairs to upper campus building",
                        "You safely reach the elevated 2nd floor terrace. Minutes later, the underpass is completely submerged.", 30, true, "Outstanding decision! Material property can be replaced; human lives cannot. Always seek immediate high ground."));
                simDAO.saveOption(new SimulationOption(sim2.getId(), "Climb onto the roof of a parked car and shout for passersby to throw a rope",
                        "As flood levels rise, water buoyancy begins shifting vehicles into one another, creating crush hazards.", -15, false, "Parked cars become unstable floating hazards during flash floods. Ascend permanent structural stairs immediately."));
                System.out.println("[DatabaseInitializer] Added Flood simulation.");
            }

            if (!hasCycloneSim && cycloneId != null) {
                Simulation sim3 = new Simulation("Coastal Cyclone: High Winds & Flying Debris at Campus Dormitory", cycloneId,
                        "A Category 3 Cyclone has made landfall. 140 km/h wind gusts are rattling glass windows in your dormitory room on the 4th floor. Suddenly, a tree branch smashes the outer pane of your window, showering glass and allowing torrential rain and wind inside.",
                        "Coastal Campus East Residence Hall", SeverityLevel.CRITICAL);
                sim3 = simDAO.save(sim3);
                simDAO.saveOption(new SimulationOption(sim3.getId(), "Run towards the broken window with a blanket and try to tape or hold it in place against the wind",
                        "Extreme gale winds push inward, shattering the remaining glass violently and causing severe flying-shard injuries.", -30, false, "Never approach breached windows during extreme cyclone winds. Atmospheric pressure and airborne debris are hazardous."));
                simDAO.saveOption(new SimulationOption(sim3.getId(), "Grab your emergency go-bag, close the dorm room door behind you, and relocate down to the central interior corridor on the 2nd floor",
                        "You reach the reinforced concrete interior corridor away from all windows. You remain protected and dry.", 35, true, "Perfect choice! Interior rooms and corridors provide maximum structural shielding from high-wind debris."));
                simDAO.saveOption(new SimulationOption(sim3.getId(), "Take the dormitory elevator down to the basement parking lot",
                        "A lightning strike knocks out the local power transformer; the elevator stalls between floors in the dark.", -25, false, "Never use elevators during storms or cyclones due to high probability of grid failure and mechanical entrapment."));
                System.out.println("[DatabaseInitializer] Added Cyclone simulation.");
            }

            if (!hasFireSim && fireId != null) {
                Simulation sim4 = new Simulation("Wildfire Approach: Smoke Inundation at Campus Sports Pavilion", fireId,
                        "A nearby forest wildfire has shifted direction due to unexpected winds. Dense ash and smoke are enveloping the athletic pavilion. You and 4 other students are inside. Visibility outside has dropped to under 30 meters, and phone sirens sound an immediate Level 3 Evacuation Order.",
                        "Campus Perimeter Athletic Pavilion & Forest Edge", SeverityLevel.HIGH);
                sim4 = simDAO.save(sim4);
                simDAO.saveOption(new SimulationOption(sim4.getId(), "Lock yourselves inside the wooden pavilion locker room and wait for fire engines to arrive",
                        "Smoke quickly permeates through ventilation gaps and timber walls, resulting in dangerous carbon monoxide buildup.", -30, false, "Wooden standalone structures without defensible space are ignition hazards. Evacuate immediately when ordered."));
                simDAO.saveOption(new SimulationOption(sim4.getId(), "Equip N95 masks, turn on vehicle hazard lights, and follow the pre-designated asphalt southern evacuation route away from the smoke column",
                        "By moving promptly along the paved southern route, you clear the fire perimeter well before embers reach the road.", 35, true, "Exemplary protocol! Immediate evacuation with respiratory protection along paved egress routes ensures safety."));
                simDAO.saveOption(new SimulationOption(sim4.getId(), "Run into the adjacent woods to use the foot-trail shortcut to your dorm",
                        "Forest trails are filled with dry tinder and blinding smoke, leaving you disoriented in the path of flying embers.", -35, false, "Never enter forested trails during a wildfire. Always stay on wide paved roads with visibility and clearance."));
                System.out.println("[DatabaseInitializer] Added Wildfire simulation.");
            }
        } catch (Exception e) {
            System.err.println("[DatabaseInitializer] Notice: Additional curriculum seeding: " + e.getMessage());
        }
    }
}
