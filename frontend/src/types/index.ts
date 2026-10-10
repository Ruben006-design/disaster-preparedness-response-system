// Core Enumerations
export type RoleType = 'STUDENT' | 'ADMIN' | 'GOVERNMENT_OFFICER';

export type UserStatus = 'ACTIVE' | 'INACTIVE' | 'SUSPENDED';

export type SeverityLevel = 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';

export type ReportStatus =
  | 'SUBMITTED'
  | 'UNDER_REVIEW'
  | 'VERIFIED'
  | 'REJECTED'
  | 'ASSIGNED'
  | 'RESPONSE_IN_PROGRESS'
  | 'SITUATION_CONTROLLED'
  | 'RESOLVED';

export type EvidenceCategory = 'IMAGE' | 'VIDEO' | 'DOCUMENT';

export type NotificationType =
  | 'REPORT_RECEIVED'
  | 'REPORT_VERIFIED'
  | 'REPORT_REJECTED'
  | 'DEPARTMENT_ASSIGNED'
  | 'STATUS_UPDATE'
  | 'INCIDENT_RESOLVED'
  | 'EMERGENCY_BROADCAST';

export type DepartmentType =
  | 'FIRE_AND_RESCUE'
  | 'POLICE'
  | 'MEDICAL_EMERGENCY'
  | 'DISASTER_MANAGEMENT'
  | 'MUNICIPAL_SERVICES'
  | 'COAST_GUARD';

// User & Auth
export interface UserProfile {
  id: number;
  name: string;
  email: string;
  phone?: string;
  role: RoleType;
  status: UserStatus;
  createdAt: string;
}

export interface AuthResponse {
  token: string;
  type: string;
  id: number;
  name: string;
  email: string;
  role: RoleType;
}

// Disaster Education
export interface DisasterType {
  id: number;
  name: string;
  code: string;
  description: string;
  icon?: string;
  defaultSeverity: SeverityLevel;
  warningSigns?: string;
  safetyProcedures?: string;
  doList?: string;
  dontList?: string;
  beforeSteps?: string;
  duringSteps?: string;
  afterSteps?: string;
  active: boolean;
}

export interface LearningModule {
  id: number;
  title: string;
  disasterTypeId: number;
  disasterTypeName: string;
  disasterCode: string;
  defaultSeverity: SeverityLevel;
  summary: string;
  content: string;
  estimatedMinutes: number;
  mediaUrl?: string;
  orderIndex: number;
  active: boolean;
  userCompleted?: boolean;
  userProgress?: number;
  createdAt: string;
}

export interface QuizOption {
  id: number;
  optionText: string;
  isCorrect?: boolean;
}

export interface QuizQuestion {
  id: number;
  questionText: string;
  explanation?: string;
  points: number;
  orderIndex: number;
  options: QuizOption[];
}

export interface Quiz {
  id: number;
  title: string;
  disasterType?: DisasterType;
  disasterTypeId?: number;
  disasterTypeName?: string;
  description?: string;
  passingScore: number;
  timeLimitMinutes?: number;
  active: boolean;
  questions: QuizQuestion[];
}

export interface QuizResult {
  id: number;
  quizId: number;
  quizTitle: string;
  score: number;
  totalQuestions: number;
  correctAnswers: number;
  passed: boolean;
  passingScore: number;
  explanations?: Record<number, string>;
  completedAt: string;
}

export interface SimulationOption {
  id: number;
  actionText: string;
  consequenceText?: string;
  scoreDelta: number;
  appropriate: boolean;
  isAppropriate?: boolean;
  safetyFeedback?: string;
}

export interface Simulation {
  id: number;
  title: string;
  disasterType?: DisasterType;
  disasterTypeId?: number;
  disasterTypeName?: string;
  scenarioText: string;
  locationContext?: string;
  initialRiskLevel: SeverityLevel;
  active: boolean;
  options: SimulationOption[];
}

export interface SimulationResult {
  id: number;
  simulationId: number;
  simulationTitle: string;
  selectedOptionId: number;
  actionText: string;
  consequenceText: string;
  appropriate: boolean;
  scoreEarned: number;
  safetyFeedback: string;
  completedAt: string;
}

export interface PreparednessScore {
  overallScore: number;
  moduleCompletionScore: number;
  quizPerformanceScore: number;
  simulationPerformanceScore: number;
  modulesCompleted: number;
  totalModules: number;
  averageQuizScore: number;
  simulationsCompleted: number;
  totalSimulations: number;
  preparednessLevel: string;
  transparencyDisclaimer: string;
}

// Disaster Reporting & Evidence
export interface Evidence {
  id: number;
  reportId: number;
  originalFileName: string;
  storedFileName: string;
  fileType: string;
  fileSize: number;
  fileUrl: string;
  fileCategory: EvidenceCategory;
  uploadedAt: string;
}

export interface IncidentUpdate {
  id: number;
  reportId: number;
  updatedById?: number;
  updatedByName?: string;
  previousStatus?: ReportStatus;
  newStatus: ReportStatus;
  remarks?: string;
  timestamp: string;
}

export interface DisasterReport {
  id: number;
  reportCode: string;
  reporterId?: number;
  reporterName: string;
  reporterEmail?: string;
  disasterTypeId: number;
  disasterTypeName: string;
  disasterCode: string;
  severity: SeverityLevel;
  status: ReportStatus;
  title: string;
  description: string;
  address: string;
  latitude: number;
  longitude: number;
  aftermath?: string;
  contactPhone?: string;
  rejectionReason?: string;
  verifiedById?: number;
  verifiedByName?: string;
  verifiedAt?: string;
  createdAt: string;
  updatedAt?: string;
  officialVerified: boolean;
  assignedDepartmentId?: number;
  assignedDepartmentName?: string;
  assignedTeamId?: number;
  assignedTeamName?: string;
  assignmentPriority?: SeverityLevel;
  assignmentInstructions?: string;
  assignedAt?: string;
  evidences: Evidence[];
  updates: IncidentUpdate[];
}

// Government & Response Teams
export interface ResponseTeam {
  id: number;
  departmentId: number;
  departmentName: string;
  teamName: string;
  teamLead?: string;
  contactPhone?: string;
  status: string;
  active: boolean;
}

export interface GovernmentDepartment {
  id: number;
  name: string;
  code: string;
  departmentType: DepartmentType;
  description?: string;
  contactEmail?: string;
  contactPhone?: string;
  headName?: string;
  active: boolean;
  responseTeams: ResponseTeam[];
}

// Emergency Contacts
export interface EmergencyContact {
  id: number;
  serviceName: string;
  organization: string;
  phoneNumber: string;
  alternatePhone?: string;
  location?: string;
  priorityOrder: number;
  active: boolean;
}

// In-App Notifications
export interface NotificationItem {
  id: number;
  title: string;
  message: string;
  type: NotificationType;
  read: boolean;
  referenceId?: string;
  createdAt: string;
}

// Dashboards
export interface StudentDashboardData {
  preparednessScore: PreparednessScore;
  modulesCompleted: number;
  totalModules: number;
  quizAverage: number;
  simulationsCompleted: number;
  totalSimulations: number;
  recentReports: DisasterReport[];
  activeVerifiedDisasters: DisasterReport[];
}

export interface OfficerDashboardData {
  pendingReports: number;
  underReviewReports: number;
  verifiedIncidents: number;
  activeResponses: number;
  resolvedIncidents: number;
  rejectedReports: number;
  pendingQueue: DisasterReport[];
  activeResponsesQueue: DisasterReport[];
}

export interface AdminDashboardData {
  totalUsers: number;
  totalStudents: number;
  totalOfficers: number;
  totalDisasterTypes: number;
  totalModules: number;
  totalQuizzes: number;
  totalSimulations: number;
  totalDepartments: number;
  totalResponseTeams: number;
  totalReports: number;
  verifiedIncidents: number;
  resolvedIncidents: number;
}
