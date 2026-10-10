-- ===================================================================
-- DISASTER PREPAREDNESS AND RESPONSE EDUCATION SYSTEM
-- Database Seed Data (Demo dataset for MySQL Workbench & college viva)
-- ===================================================================

USE disaster_response_db;

-- -------------------------------------------------------------------
-- 1. USERS
-- Default password for all seed accounts: Password@123
-- BCrypt hash: $2a$10$vI8aWBnW3fID.ZQ4/zo1G.q1lRps.9cGLcZEiGDMVr5yUP1KUOYTa
-- -------------------------------------------------------------------
INSERT INTO users (id, name, email, password, phone, role, status, created_at) VALUES
(1, 'System Administrator', 'admin@disasterprep.gov', '$2a$10$vI8aWBnW3fID.ZQ4/zo1G.q1lRps.9cGLcZEiGDMVr5yUP1KUOYTa', '+91 98765 43210', 'ADMIN', 'ACTIVE', NOW()),
(2, 'Officer Rajesh Kumar', 'officer.rajesh@disasterprep.gov', '$2a$10$vI8aWBnW3fID.ZQ4/zo1G.q1lRps.9cGLcZEiGDMVr5yUP1KUOYTa', '+91 98765 43211', 'GOVERNMENT_OFFICER', 'ACTIVE', NOW()),
(3, 'Officer Priya Sharma', 'officer.priya@disasterprep.gov', '$2a$10$vI8aWBnW3fID.ZQ4/zo1G.q1lRps.9cGLcZEiGDMVr5yUP1KUOYTa', '+91 98765 43212', 'GOVERNMENT_OFFICER', 'ACTIVE', NOW()),
(4, 'Aarav Patel', 'aarav.student@college.edu', '$2a$10$vI8aWBnW3fID.ZQ4/zo1G.q1lRps.9cGLcZEiGDMVr5yUP1KUOYTa', '+91 98765 43213', 'STUDENT', 'ACTIVE', NOW()),
(5, 'Ananya Roy', 'ananya.student@college.edu', '$2a$10$vI8aWBnW3fID.ZQ4/zo1G.q1lRps.9cGLcZEiGDMVr5yUP1KUOYTa', '+91 98765 43214', 'STUDENT', 'ACTIVE', NOW()),
(6, 'Rohan Verma', 'rohan.student@college.edu', '$2a$10$vI8aWBnW3fID.ZQ4/zo1G.q1lRps.9cGLcZEiGDMVr5yUP1KUOYTa', '+91 98765 43215', 'STUDENT', 'ACTIVE', NOW())
ON DUPLICATE KEY UPDATE name=VALUES(name);

-- -------------------------------------------------------------------
-- 2. DISASTER TYPES
-- -------------------------------------------------------------------
INSERT INTO disaster_types (id, name, code, description, icon, default_severity, warning_signs, safety_procedures, do_list, dont_list, before_steps, during_steps, after_steps, is_active) VALUES
(1, 'Earthquake', 'EARTHQUAKE', 'Sudden rapid shaking of the earth caused by the shifting of subterranean rock along fault lines.', 'EarthquakeIcon', 'HIGH',
 'Sudden ground vibration, rattling windows, anomalous animal behavior, creaking wall joints.',
 'Execute Drop, Cover, and Hold On immediately under sturdy furniture until shaking stops.',
 'Drop to hands and knees; Cover head and neck; Hold on to sturdy shelter; Move away from glass and outer walls.',
 'Do not run outside during shaking; Do not use elevators; Do not stand in doorways without door frame support.',
 'Fasten heavy furniture to walls; Keep emergency go-bag prepared; Identify safe drop zones indoors.',
 'Drop, Cover, and Hold On. Protect your head. Wait for ground motion to cease completely.',
 'Check for injuries and gas leaks; Evacuate cautiously via stairs; Stay alert for strong aftershocks.', TRUE),

(2, 'Flood', 'FLOOD', 'Overflow of water submerging land that is normally dry, triggered by heavy torrential rainfall or storm surge.', 'WaterIcon', 'HIGH',
 'Rising water levels in canals and drains, heavy continuous rainfall alerts, waterlogged streets.',
 'Move immediately to higher ground; Avoid walking or driving through moving flood water.',
 'Move valuable items and documents upstairs; Turn off electrical mains if safe; Drink boiled or bottled water only.',
 'Never drive through flooded roadways; Do not touch submerged electrical appliances or wiring.',
 'Know community evacuation routes; Store 3-day supply of drinking water and canned food.',
 'Evacuate to elevated shelters immediately when ordered. Avoid swollen streams and stormwater channels.',
 'Do not return home until officials declare it safe; Disinfect flooded living spaces thoroughly.', TRUE),

(3, 'Cyclone / Hurricane', 'CYCLONE', 'Intense tropical weather system characterized by strong swirling winds and severe torrential rains.', 'CycloneIcon', 'CRITICAL',
 'Rapid drop in atmospheric pressure, darkening skies, gale-force winds, high sea waves and swell.',
 'Secure premises, board or tape glass windows, charge emergency communication devices, evacuate coastal belts.',
 'Secure loose outdoor rooftop items; Keep battery-operated radio tuned to emergency broadcasts.',
 'Do not step outdoors during the calm eye of the cyclone; Do not venture into coastal or sea zones.',
 'Trim weak tree branches near buildings; Reinforce roofs and storm shutters; Pack survival kits.',
 'Remain indoors in a windowless interior room on ground level; Disconnect power mains.',
 'Watch for downed electrical power lines; Avoid stagnant water bodies; Await all-clear siren.', TRUE),

(4, 'Fire & Wildfire', 'FIRE', 'Rapid oxidation of combustible material releasing heat, light, and toxic smoke in structures or forests.', 'FireIcon', 'HIGH',
 'Smell of pungent smoke, crackling sounds, fire alarms ringing, sudden heat sensation.',
 'Activate fire alarm; Evacuate along designated escape routes; Crawl low under smoke to exit.',
 'Crawl low under smoke; Feel doors with back of hand before opening; Call emergency hotline 101/112.',
 'Never take elevators; Do not re-enter burning premises for personal belongings; Do not panic.',
 'Install smoke detectors and test batteries monthly; Maintain ABC dry-powder fire extinguishers on each floor.',
 'Sound alarm, crawl low to avoid asphyxiation, exit building immediately via fire stairs.',
 'Assemble at predetermined muster point; Report missing individuals to fire rescue crew.', TRUE),

(5, 'Landslide', 'LANDSLIDE', 'Downslope movement of a mass of rock, debris, or earth under the direct influence of gravity on slopes.', 'LandscapeIcon', 'MEDIUM',
 'Cracks widening in ground, tilting trees and utility poles, faint rumbling sound that increases in volume.',
 'Evacuate slope zones immediately; Move perpendicular to the direction of debris flow.',
 'Stay alert during heavy rainfall in hilly terrains; Listen for unusual sounds like trees cracking.',
 'Do not stay in low-lying direct paths of debris; Do not sleep in hillside-facing bedrooms during severe storms.',
 'Plant deep-rooted vegetation on slopes; Install proper slope drainage; Monitor land fissures.',
 'Run immediately perpendicular to path of debris flow; Seek shelter behind sturdy structures if trapped.',
 'Stay away from slide area as secondary slides may occur; Check for trapped neighbors safely.', TRUE)
ON DUPLICATE KEY UPDATE name=VALUES(name);

-- -------------------------------------------------------------------
-- 3. LEARNING MODULES
-- -------------------------------------------------------------------
INSERT INTO learning_modules (id, title, disaster_type_id, summary, content, estimated_minutes, media_url, order_index, is_active, created_at) VALUES
(1, 'Earthquake Preparedness & Drop-Cover-Hold', 1, 'Master life-saving actions during tectonic tremors and building evacuation.',
 '# Earthquake Preparedness Guide\n\nEarthquakes strike without warning. Preparedness is your single greatest asset.\n\n### 1. The Core Action: Drop, Cover, Hold On\n- **DROP** to your hands and knees. This position protects you from being knocked down.\n- **COVER** your head and neck under a sturdy table or desk.\n- **HOLD ON** to your shelter until the shaking stops.\n\n### 2. Evacuation Protocol\nOnce shaking stops, leave the building via staircases. Never use elevators. Watch for falling glass and masonry.', 12, 'https://images.unsplash.com/photo-1590283603385-17ffb3a7f29f?auto=format&fit=crop&w=1200&q=80', 1, TRUE, NOW()),

(2, 'Urban Flood Survival & Water Safety', 2, 'Learn flood warning levels, waterborne disease prevention, and safe evacuation.',
 '# Urban Flood Safety Protocols\n\nFlash floods can submerge streets in minutes.\n\n### 1. Six Inches Can Knock You Down\nJust 6 inches of rapidly moving water can knock an adult off their feet. 12 inches of water can float small vehicles.\n\n### 2. Hygiene and Contamination\nFlood waters frequently carry raw sewage, sharp debris, and hazardous industrial chemicals. Boil water for at least 1 minute before drinking.', 15, 'https://images.unsplash.com/photo-1547683905-f686c993aae5?auto=format&fit=crop&w=1200&q=80', 1, TRUE, NOW()),

(3, 'Cyclone Early Warning & Storm Shelter', 3, 'Understand meteorological warnings, coastal storm surges, and cyclone bunker safety.',
 '# Cyclone Preparedness and Tracking\n\nTropical cyclones bring sustained winds in excess of 120 km/h.\n\n### 1. Color-Coded Warning Stages\n- **Yellow (Watch)**: 48 hours before storm onset.\n- **Orange (Alert)**: 24 hours before storm onset.\n- **Red (Warning)**: 12 hours before storm onset - Immediate evacuation required.\n\n### 2. Surviving the Eye\nIf the wind drops suddenly, do not go outside. The eye of the cyclone has passed, and fierce winds from the opposite direction will resume abruptly.', 14, 'https://images.unsplash.com/photo-1527482797697-8795b05a13fe?auto=format&fit=crop&w=1200&q=80', 1, TRUE, NOW()),

(4, 'Structural Fire Response & Extinguisher Operation', 4, 'Learn the PASS technique for fire extinguishers and smoke-inhalation survival.',
 '# Structural Fire Safety & Evacuation\n\nSmoke causes significantly more casualties in building fires than direct heat.\n\n### 1. Extinguisher PASS Method\n- **P**: Pull the safety pin.\n- **A**: Aim low at the base of the flames.\n- **S**: Squeeze the lever handle slowly.\n- **S**: Sweep nozzle from side to side.\n\n### 2. Crawl Low in Smoke\nClean air remains 1 to 2 feet above floor level. Always crawl with head lowered when escaping smoke-filled hallways.', 10, 'https://images.unsplash.com/photo-1599818816933-28f0015555d7?auto=format&fit=crop&w=1200&q=80', 1, TRUE, NOW())
ON DUPLICATE KEY UPDATE title=VALUES(title);

-- -------------------------------------------------------------------
-- 4. MODULE PROGRESS
-- -------------------------------------------------------------------
INSERT INTO module_progress (id, user_id, module_id, is_completed, completion_percentage, last_accessed_at, completed_at) VALUES
(1, 4, 1, TRUE, 100, NOW(), NOW()),
(2, 4, 2, FALSE, 60, NOW(), NULL),
(3, 5, 1, TRUE, 100, NOW(), NOW()),
(4, 5, 4, TRUE, 100, NOW(), NOW())
ON DUPLICATE KEY UPDATE completion_percentage=VALUES(completion_percentage);

-- -------------------------------------------------------------------
-- 5. QUIZZES
-- -------------------------------------------------------------------
INSERT INTO quizzes (id, title, disaster_type_id, description, passing_score, time_limit_minutes, is_active, created_at) VALUES
(1, 'Earthquake Preparedness Knowledge Assessment', 1, 'Evaluate your readiness on tectonic hazard response, drop-cover-hold, and evacuation protocol.', 70, 10, TRUE, NOW()),
(2, 'Flood Safety & Water Rescue Assessment', 2, 'Test your knowledge on flash flood navigation, safe depths, and sanitation procedures.', 70, 10, TRUE, NOW()),
(3, 'Fire Hazard Response & Extinguisher Mastery', 4, 'Test your grasp on structural fire evacuation, the PASS technique, and smoke evasion.', 75, 10, TRUE, NOW())
ON DUPLICATE KEY UPDATE title=VALUES(title);

-- -------------------------------------------------------------------
-- 6. QUIZ QUESTIONS & OPTIONS
-- -------------------------------------------------------------------
INSERT INTO quiz_questions (id, quiz_id, question_text, explanation, points, order_index) VALUES
(1, 1, 'What is the universally recommended immediate response when indoor earthquake tremors begin?', 'Drop, Cover, and Hold On protects against the primary cause of earthquake injury: falling overhead objects.', 10, 1),
(2, 1, 'Why is running outside during intense shaking strongly discouraged?', 'Falling building facades, glass, brickwork, and utility wires near doorways cause the majority of casualties.', 10, 2),
(3, 2, 'How many inches of rapidly moving flood water is typically sufficient to sweep an adult off their feet?', 'Just 6 inches of swiftly flowing water creates tremendous hydrodynamic force capable of knocking down an adult.', 10, 1),
(4, 3, 'In the PASS technique for operating a fire extinguisher, what does the A stand for?', 'Aim the nozzle low at the base of the flames to suppress fuel burning, not at high flames.', 10, 1)
ON DUPLICATE KEY UPDATE question_text=VALUES(question_text);

INSERT INTO quiz_options (id, question_id, option_text, is_correct) VALUES
(1, 1, 'Run outside as fast as possible', FALSE),
(2, 1, 'Drop, Cover, and Hold On under sturdy furniture', TRUE),
(3, 1, 'Stand directly in the center of an open hallway', FALSE),
(4, 1, 'Immediately enter the elevator to escape to ground floor', FALSE),
(5, 2, 'Because doors lock automatically during tremors', FALSE),
(6, 2, 'Because falling glass, bricks, and facades outside create extreme injury risk', TRUE),
(7, 2, 'Because ground level air is immediately toxic', FALSE),
(8, 3, '2 inches', FALSE),
(9, 3, '6 inches', TRUE),
(10, 3, '24 inches', FALSE),
(11, 4, 'Aim high above the flames', FALSE),
(12, 4, 'Aim low at the base of the fire', TRUE),
(13, 4, 'Air-cool surrounding furniture', FALSE)
ON DUPLICATE KEY UPDATE option_text=VALUES(option_text);

-- -------------------------------------------------------------------
-- 7. SIMULATIONS & OPTIONS
-- -------------------------------------------------------------------
INSERT INTO simulations (id, title, disaster_type_id, scenario_text, location_context, initial_risk_level, is_active, created_at) VALUES
(1, 'Campus Laboratory Fire Hazard Scenario', 4, 'You are in a third-floor chemistry laboratory when an electrical spark ignites ethanol vapors. Thick black smoke begins billowing toward the hallway exit door.', 'College Science Complex, Floor 3', 'CRITICAL', TRUE, NOW()),
(2, 'High-Rise Classroom Earthquake Shaking', 1, 'You are attending a lecture on the 4th floor when building alarm bells blare and the floor begins violent horizontal oscillations. Ceiling tiles crack.', 'University Academic Wing B', 'HIGH', TRUE, NOW())
ON DUPLICATE KEY UPDATE title=VALUES(title);

INSERT INTO simulation_options (id, simulation_id, action_text, consequence_text, score_delta, is_appropriate, safety_feedback) VALUES
(1, 1, 'Pull the fire alarm, crawl low below the smoke, and exit using the exterior fire stairs.', 'Alarms sound, campus security dispatches responders, and you reach the muster point safely.', 30, TRUE, 'Optimal response. Crawling minimizes toxic smoke inhalation while pull station alerts everyone.'),
(2, 1, 'Run straight through the hallway toward the nearest elevator to reach ground quickly.', 'Elevator power fails between floors due to building safety breakers; you become trapped in smoke.', -20, FALSE, 'Never take elevators during fire emergencies! Power loss and chimney smoke suction make elevators deadly.'),
(3, 2, 'Immediately drop under your student desk, clasp the desk legs firmly, and protect your neck.', 'The heavy desk shields you from falling lighting fixtures and plaster. Shaking stops safely.', 30, TRUE, 'Excellent adherence to Drop-Cover-Hold protocols.'),
(4, 2, 'Rush immediately to the balcony to shout for help.', 'Exterior railings loosen and balance is lost during intense secondary shock wave.', -25, FALSE, 'Stay away from balconies, exterior windows, and building perimeters during active shaking.')
ON DUPLICATE KEY UPDATE action_text=VALUES(action_text);

-- -------------------------------------------------------------------
-- 8. GOVERNMENT DEPARTMENTS
-- -------------------------------------------------------------------
INSERT INTO government_departments (id, name, code, department_type, description, contact_email, contact_phone, head_name, is_active) VALUES
(1, 'National Disaster Response Force (NDRF)', 'NDRF_DIV_01', 'DISASTER_MANAGEMENT', 'Specialized rapid response force for chemical, biological, radiological, and natural disasters.', 'ndrf.control@gov.in', '011-24363260', 'Director Gen. V. Sharma', TRUE),
(2, 'State Fire & Emergency Rescue Command', 'FIRE_RESCUE_HQ', 'FIRE_DEPARTMENT', 'Urban firefighting, technical rope rescues, hazardous material containment, and extraction.', 'fire.emergency@gov.in', '101', 'Chief Fire Officer M. Das', TRUE),
(3, 'Municipal Disaster Emergency Cell', 'MUNICIPAL_CORP', 'MUNICIPAL_CORPORATION', 'Civic waterlogging dewatering, culvert unclogging, debris clearance, and temporary shelters.', 'disaster.cell@municipal.gov', '+91 22 2269 4725', 'Commissioner S. Kulkarni', TRUE),
(4, 'District Medical & Public Health Bureau', 'HEALTH_SERVICES', 'HEALTH_DEPARTMENT', 'Field triage, medical emergency ambulances, disease surveillance, and immunization.', 'health.emergency@gov.in', '108', 'Dr. Aruna Reddy', TRUE)
ON DUPLICATE KEY UPDATE name=VALUES(name);

-- -------------------------------------------------------------------
-- 9. RESPONSE TEAMS
-- -------------------------------------------------------------------
INSERT INTO response_teams (id, department_id, team_name, team_lead, contact_phone, status, is_active) VALUES
(1, 1, 'NDRF Urban Search & Rescue Bravo', 'Inspector Arvind Rao', '+91 94123 45678', 'AVAILABLE', TRUE),
(2, 2, 'Fire Station 04 Rapid Extrication Unit', 'Captain D. Sengupta', '+91 94234 56789', 'DEPLOYED', TRUE),
(3, 3, 'Monsoon Inundation Task Team East', 'Eng. K. Balaji', '+91 94345 67890', 'AVAILABLE', TRUE),
(4, 4, 'Mobile Medical Triage Unit 2', 'Dr. S. Nair', '+91 94456 78901', 'AVAILABLE', TRUE)
ON DUPLICATE KEY UPDATE team_name=VALUES(team_name);

-- -------------------------------------------------------------------
-- 10. DISASTER REPORTS
-- -------------------------------------------------------------------
INSERT INTO disaster_reports (id, report_code, reporter_id, disaster_type_id, severity, status, title, description, address, latitude, longitude, aftermath, contact_phone, rejection_reason, verified_by_id, verified_at, created_at) VALUES
(1, 'RPT-2026-001', 4, 2, 'HIGH', 'RESPONSE_IN_PROGRESS',
 'Severe flash flooding near University South Gate',
 'Heavy cloudburst water has reached waist height near the university south avenue. Approximately 30 students are stranded inside the bus terminal building.',
 'South Avenue Crossing, University Campus Road, Sector 4', 19.0760, 72.8777,
 'Road blocked, public buses stalled in water, power line tripped safely by electricity board.',
 '+91 98765 43213', NULL, 2, NOW(), DATE_SUB(NOW(), INTERVAL 3 HOUR)),

(2, 'RPT-2026-002', 5, 4, 'CRITICAL', 'VERIFIED',
 'Electrical Transformer Fire near College Dormitory',
 'Overhead distribution transformer exploded with high-intensity fire spraying sparks across the dry dormitory lawn. Building evacuated.',
 'Student Hostel Block B, North Campus, Green Park Lane', 19.0825, 72.8820,
 'Smoke entering second floor corridors. Dorm warden initiated fire siren. Fire extinguishers exhausted.',
 '+91 98765 43214', NULL, 3, NOW(), DATE_SUB(NOW(), INTERVAL 1 HOUR)),

(3, 'RPT-2026-003', 6, 1, 'MEDIUM', 'SUBMITTED',
 'Deep wall fissures following mild tremor',
 'Noticeable structural shear cracks opened along the laboratory building external facade following morning tremor.',
 'Civil Engineering Wing, Main Polytechnic Campus', 19.0680, 72.8650,
 'No injuries reported. Students advised not to enter lecture room 102 until inspection.',
 '+91 98765 43215', NULL, NULL, NULL, NOW()),

(4, 'RPT-2026-004', 4, 5, 'LOW', 'RESOLVED',
 'Roadside mudslide cleared on campus bypass',
 'Loose earth slid down hillside road after torrential overnight shower.',
 'Hill View Bypass Road, Campus West Boundary', 19.0910, 72.8710,
 'Municipal excavator deployed; debris cleared; traffic reopened.',
 '+91 98765 43213', NULL, 2, DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY))
ON DUPLICATE KEY UPDATE title=VALUES(title);

-- -------------------------------------------------------------------
-- 11. EVIDENCES
-- -------------------------------------------------------------------
INSERT INTO evidences (id, report_id, original_file_name, stored_file_name, file_type, file_size, file_url, file_category, uploaded_at) VALUES
(1, 1, 'flood_water_campus.jpg', 'evidence_1_1727400000.jpg', 'image/jpeg', 245030, 'https://images.unsplash.com/photo-1547683905-f686c993aae5?auto=format&fit=crop&w=800&q=80', 'IMAGE', NOW()),
(2, 2, 'transformer_fire.jpg', 'evidence_2_1727401000.jpg', 'image/jpeg', 312400, 'https://images.unsplash.com/photo-1599818816933-28f0015555d7?auto=format&fit=crop&w=800&q=80', 'IMAGE', NOW())
ON DUPLICATE KEY UPDATE original_file_name=VALUES(original_file_name);

-- -------------------------------------------------------------------
-- 12. INCIDENT UPDATES
-- -------------------------------------------------------------------
INSERT INTO incident_updates (id, report_id, updated_by_id, previous_status, new_status, remarks, timestamp) VALUES
(1, 1, 2, 'SUBMITTED', 'VERIFIED', 'Verified with campus security officer and local municipal control room. Water rescue boat dispatched.', DATE_SUB(NOW(), INTERVAL 2 HOUR)),
(2, 1, 2, 'VERIFIED', 'RESPONSE_IN_PROGRESS', 'Municipal Task Team deployed with dewatering high-volume pumps.', DATE_SUB(NOW(), INTERVAL 1 HOUR)),
(3, 2, 3, 'SUBMITTED', 'VERIFIED', 'Verified priority report. Fire rescue team alerted immediately.', DATE_SUB(NOW(), INTERVAL 45 MINUTE))
ON DUPLICATE KEY UPDATE remarks=VALUES(remarks);

-- -------------------------------------------------------------------
-- 13. RESPONSE ASSIGNMENTS
-- -------------------------------------------------------------------
INSERT INTO response_assignments (id, report_id, department_id, response_team_id, assigned_by_id, priority, instructions, assigned_at) VALUES
(1, 1, 3, 3, 2, 'CRITICAL', 'Dispatch high-output submersible pumps to South Gate. Assist stranded students to high ground.', DATE_SUB(NOW(), INTERVAL 1 HOUR)),
(2, 2, 2, 2, 3, 'CRITICAL', 'Foam tender unit dispatched to extinguish chemical transformer fire and isolate electrical mains.', DATE_SUB(NOW(), INTERVAL 45 MINUTE))
ON DUPLICATE KEY UPDATE instructions=VALUES(instructions);

-- -------------------------------------------------------------------
-- 14. EMERGENCY CONTACTS
-- -------------------------------------------------------------------
INSERT INTO emergency_contacts (id, service_name, organization, phone_number, alternate_phone, location, priority_order, is_active) VALUES
(1, 'National Emergency Helpline', 'Government of India Emergency Response', '112', '100', 'Pan-India Nationwide', 1, TRUE),
(2, 'Fire and Rescue Services', 'State Fire Brigade Headquarters', '101', '011-23412222', 'Metro Central Fire Station', 2, TRUE),
(3, 'Ambulance Emergency Medical Response', 'Emergency Medical Relief Cell', '108', '102', 'City Hospital Command', 3, TRUE),
(4, 'Disaster Management Authority Helpline', 'NDMA Emergency Operations Centre', '1078', '011-26701728', 'National Operations Center', 4, TRUE),
(5, 'Campus Security & Medical Control', 'University Student Safety Office', '+91 98765 00000', 'Ext. 1001', 'Main Administrative Gate', 5, TRUE)
ON DUPLICATE KEY UPDATE service_name=VALUES(service_name);

-- -------------------------------------------------------------------
-- 15. NOTIFICATIONS
-- -------------------------------------------------------------------
INSERT INTO notifications (id, recipient_id, title, message, type, is_read, reference_id, created_at) VALUES
(1, 4, 'Report Verified by Officer', 'Your report RPT-2026-001 has been verified by Officer Rajesh Kumar and response teams are deployed.', 'REPORT_VERIFIED', FALSE, 'RPT-2026-001', NOW()),
(2, 5, 'Report Verified by Officer', 'Your incident report RPT-2026-002 has been verified as CRITICAL and Fire Command is responding.', 'REPORT_VERIFIED', FALSE, 'RPT-2026-002', NOW()),
(3, 1, 'New Incident Report Filed', 'Student Rohan Verma submitted report RPT-2026-003 regarding earthquake wall cracks.', 'STATUS_UPDATE', TRUE, 'RPT-2026-003', NOW())
ON DUPLICATE KEY UPDATE title=VALUES(title);
