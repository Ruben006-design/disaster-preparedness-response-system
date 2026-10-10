import { api } from './api';
import {
  AdminDashboardData,
  DepartmentType,
  DisasterType,
  EmergencyContact,
  GovernmentDepartment,
  LearningModule,
  OfficerDashboardData,
  ResponseTeam,
  StudentDashboardData,
  UserProfile,
} from '../types';

export const adminService = {
  async getUsers(page = 0, size = 15): Promise<{ content: UserProfile[]; totalElements: number; totalPages: number }> {
    const res = await api.get(`/admin/users?page=${page}&size=${size}`);
    return res.data.data;
  },

  async toggleUserStatus(userId: number): Promise<void> {
    await api.patch(`/admin/users/${userId}/toggle-status`);
  },

  async createDisaster(data: Partial<DisasterType>): Promise<DisasterType> {
    const res = await api.post('/admin/disasters', data);
    return res.data.data;
  },

  async updateDisaster(id: number, data: Partial<DisasterType>): Promise<DisasterType> {
    const res = await api.put(`/admin/disasters/${id}`, data);
    return res.data.data;
  },

  async deleteDisaster(id: number): Promise<void> {
    await api.delete(`/admin/disasters/${id}`);
  },

  async createModule(data: Partial<LearningModule>, disasterTypeId: number): Promise<LearningModule> {
    const res = await api.post(`/admin/modules?disasterTypeId=${disasterTypeId}`, data);
    return res.data.data;
  },

  async updateModule(id: number, data: Partial<LearningModule>, disasterTypeId?: number): Promise<LearningModule> {
    const url = disasterTypeId ? `/admin/modules/${id}?disasterTypeId=${disasterTypeId}` : `/admin/modules/${id}`;
    const res = await api.put(url, data);
    return res.data.data;
  },

  async deleteModule(id: number): Promise<void> {
    await api.delete(`/admin/modules/${id}`);
  },

  async createQuiz(data: any): Promise<any> {
    const res = await api.post('/admin/quizzes', data);
    return res.data.data;
  },

  async updateQuiz(id: number, data: any): Promise<any> {
    const res = await api.put(`/admin/quizzes/${id}`, data);
    return res.data.data;
  },

  async deleteQuiz(id: number): Promise<void> {
    await api.delete(`/admin/quizzes/${id}`);
  },

  async createSimulation(data: any): Promise<any> {
    const res = await api.post('/admin/simulations', data);
    return res.data.data;
  },

  async updateSimulation(id: number, data: any): Promise<any> {
    const res = await api.put(`/admin/simulations/${id}`, data);
    return res.data.data;
  },

  async deleteSimulation(id: number): Promise<void> {
    await api.delete(`/admin/simulations/${id}`);
  },

  async getDepartments(): Promise<GovernmentDepartment[]> {
    const res = await api.get('/admin/departments');
    return res.data.data;
  },

  async createDepartment(data: {
    name: string;
    code: string;
    departmentType: DepartmentType;
    description?: string;
    contactEmail?: string;
    contactPhone?: string;
    headName?: string;
  }): Promise<GovernmentDepartment> {
    const res = await api.post('/admin/departments', data);
    return res.data.data;
  },

  async createTeam(data: {
    departmentId: number;
    teamName: string;
    teamLead?: string;
    contactPhone?: string;
  }): Promise<ResponseTeam> {
    const res = await api.post('/admin/teams', data);
    return res.data.data;
  },

  async createContact(data: Partial<EmergencyContact>): Promise<EmergencyContact> {
    const res = await api.post('/admin/contacts', data);
    return res.data.data;
  },

  async deleteContact(id: number): Promise<void> {
    await api.delete(`/admin/contacts/${id}`);
  },
};

export const dashboardService = {
  async getStudentDashboard(): Promise<StudentDashboardData> {
    const res = await api.get('/dashboard/student');
    return res.data.data;
  },

  async getOfficerDashboard(): Promise<OfficerDashboardData> {
    const res = await api.get('/dashboard/officer');
    return res.data.data;
  },

  async getAdminDashboard(): Promise<AdminDashboardData> {
    const res = await api.get('/dashboard/admin');
    return res.data.data;
  },
};

export const emergencyContactService = {
  async getContacts(): Promise<EmergencyContact[]> {
    const res = await api.get('/contacts');
    return res.data.data;
  },
};

export const notificationService = {
  async getRecent(): Promise<any[]> {
    const res = await api.get('/notifications/recent');
    return res.data.data;
  },

  async getUnreadCount(): Promise<number> {
    const res = await api.get('/notifications/unread-count');
    return res.data.data;
  },

  async markAsRead(id: number): Promise<void> {
    await api.patch(`/notifications/${id}/read`);
  },

  async markAllAsRead(): Promise<void> {
    await api.post('/notifications/mark-all-read');
  },
};
