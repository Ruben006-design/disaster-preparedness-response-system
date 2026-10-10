import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import { ProtectedRoute } from '../components/common/ProtectedRoute';
import { DashboardLayout } from '../layouts/DashboardLayout';

// Public & Common Pages
import { LandingPage } from '../pages/common/LandingPage';
import { LoginPage } from '../pages/common/LoginPage';
import { RegisterPage } from '../pages/common/RegisterPage';
import { DisasterMapPage } from '../pages/common/DisasterMapPage';
import { EmergencyContactsPage } from '../pages/common/EmergencyContactsPage';
import { ProfilePage } from '../pages/common/ProfilePage';
import { ReportDetailPage } from '../pages/common/ReportDetailPage';

// Student Pages
import { StudentDashboard } from '../pages/student/StudentDashboard';
import { ModulesPage } from '../pages/student/ModulesPage';
import { ModuleDetailPage } from '../pages/student/ModuleDetailPage';
import { SimulationsPage } from '../pages/student/SimulationsPage';
import { SimulationPlayPage } from '../pages/student/SimulationPlayPage';
import { QuizzesPage } from '../pages/student/QuizzesPage';
import { QuizTakePage } from '../pages/student/QuizTakePage';
import { ReportDisasterPage } from '../pages/student/ReportDisasterPage';
import { MyReportsPage } from '../pages/student/MyReportsPage';

// Officer Pages
import { OfficerDashboard } from '../pages/officer/OfficerDashboard';
import { OfficerQueuePage } from '../pages/officer/OfficerQueuePage';
import { OfficerReviewPage } from '../pages/officer/OfficerReviewPage';

// Admin Pages
import { AdminDashboard } from '../pages/admin/AdminDashboard';
import { UserManagementPage } from '../pages/admin/UserManagementPage';
import { DepartmentManagementPage } from '../pages/admin/DepartmentManagementPage';
import { LearningContentManagementPage } from '../pages/admin/LearningContentManagementPage';

export const AppRoutes: React.FC = () => {
  return (
    <Routes>
      {/* Public Pages */}
      <Route path="/" element={<LandingPage />} />
      <Route path="/login" element={<LoginPage />} />
      <Route path="/register" element={<RegisterPage />} />
      <Route path="/map" element={<DashboardLayout><DisasterMapPage /></DashboardLayout>} />
      <Route path="/emergency-contacts" element={<DashboardLayout><EmergencyContactsPage /></DashboardLayout>} />

      {/* Authenticated Common Routes */}
      <Route element={<ProtectedRoute />}>
        <Route path="/profile" element={<DashboardLayout><ProfilePage /></DashboardLayout>} />
        <Route path="/reports/:id" element={<DashboardLayout><ReportDetailPage /></DashboardLayout>} />
        <Route path="/report-disaster" element={<DashboardLayout><ReportDisasterPage /></DashboardLayout>} />
      </Route>

      {/* Education & Disaster Readiness Routes (Available to all authenticated actors) */}
      <Route element={<ProtectedRoute allowedRoles={['STUDENT', 'ADMIN', 'GOVERNMENT_OFFICER']} />}>
        <Route path="/student/modules" element={<DashboardLayout><ModulesPage /></DashboardLayout>} />
        <Route path="/student/modules/:id" element={<DashboardLayout><ModuleDetailPage /></DashboardLayout>} />
        <Route path="/modules/:id" element={<DashboardLayout><ModuleDetailPage /></DashboardLayout>} />
        <Route path="/student/quizzes" element={<DashboardLayout><QuizzesPage /></DashboardLayout>} />
        <Route path="/student/quizzes/:id" element={<DashboardLayout><QuizTakePage /></DashboardLayout>} />
        <Route path="/quizzes/:id" element={<DashboardLayout><QuizTakePage /></DashboardLayout>} />
        <Route path="/student/simulations" element={<DashboardLayout><SimulationsPage /></DashboardLayout>} />
        <Route path="/student/simulations/:id" element={<DashboardLayout><SimulationPlayPage /></DashboardLayout>} />
        <Route path="/simulations/:id" element={<DashboardLayout><SimulationPlayPage /></DashboardLayout>} />
      </Route>

      {/* Student Exclusive Protected Routes */}
      <Route element={<ProtectedRoute allowedRoles={['STUDENT']} />}>
        <Route path="/student" element={<DashboardLayout><StudentDashboard /></DashboardLayout>} />
        <Route path="/student/reports" element={<DashboardLayout><MyReportsPage /></DashboardLayout>} />
      </Route>

      {/* Officer Protected Routes */}
      <Route element={<ProtectedRoute allowedRoles={['GOVERNMENT_OFFICER']} />}>
        <Route path="/officer" element={<DashboardLayout><OfficerDashboard /></DashboardLayout>} />
        <Route path="/officer/queue" element={<DashboardLayout><OfficerQueuePage /></DashboardLayout>} />
        <Route path="/officer/review/:id" element={<DashboardLayout><OfficerReviewPage /></DashboardLayout>} />
      </Route>

      {/* Admin Protected Routes */}
      <Route element={<ProtectedRoute allowedRoles={['ADMIN']} />}>
        <Route path="/admin" element={<DashboardLayout><AdminDashboard /></DashboardLayout>} />
        <Route path="/admin/users" element={<DashboardLayout><UserManagementPage /></DashboardLayout>} />
        <Route path="/admin/departments" element={<DashboardLayout><DepartmentManagementPage /></DashboardLayout>} />
        <Route path="/admin/content" element={<DashboardLayout><LearningContentManagementPage /></DashboardLayout>} />
      </Route>

      {/* Direct aliases & convenience redirects */}
      <Route path="/contacts" element={<Navigate to="/emergency-contacts" replace />} />
      <Route path="/modules" element={<Navigate to="/student/modules" replace />} />
      <Route path="/quizzes" element={<Navigate to="/student/quizzes" replace />} />
      <Route path="/simulations" element={<Navigate to="/student/simulations" replace />} />
      <Route path="/my-reports" element={<Navigate to="/student/reports" replace />} />
      <Route path="/reports" element={<Navigate to="/officer/queue" replace />} />
      <Route path="/student/dashboard" element={<Navigate to="/student" replace />} />
      <Route path="/officer/dashboard" element={<Navigate to="/officer" replace />} />
      <Route path="/admin/dashboard" element={<Navigate to="/admin" replace />} />

      {/* Fallback */}
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
};
export default AppRoutes;
