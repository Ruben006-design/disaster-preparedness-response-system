import { api } from './api';
import { DisasterReport, Evidence, ReportStatus, SeverityLevel } from '../types';

export const reportService = {
  async submitReport(data: {
    disasterTypeId: number;
    severity: SeverityLevel;
    title: string;
    description: string;
    address: string;
    latitude: number;
    longitude: number;
    aftermath?: string;
    contactPhone?: string;
  }): Promise<DisasterReport> {
    const res = await api.post('/reports', data);
    return res.data.data;
  },

  async uploadEvidence(reportId: number, file: File): Promise<Evidence> {
    const formData = new FormData();
    formData.append('file', file);
    const res = await api.post(`/reports/${reportId}/evidence`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
    return res.data.data;
  },

  async getMyReports(page = 0, size = 10): Promise<{ content: DisasterReport[]; totalElements: number; totalPages: number }> {
    const res = await api.get(`/reports/my?page=${page}&size=${size}`);
    return res.data.data;
  },

  async getReportById(id: number): Promise<DisasterReport> {
    const res = await api.get(`/reports/${id}`);
    return res.data.data;
  },

  async filterReports(params: {
    disasterTypeId?: number;
    severity?: SeverityLevel;
    status?: ReportStatus;
    search?: string;
    page?: number;
    size?: number;
  }): Promise<{ content: DisasterReport[]; totalElements: number; totalPages: number }> {
    const query = new URLSearchParams();
    if (params.disasterTypeId) query.append('disasterTypeId', params.disasterTypeId.toString());
    if (params.severity) query.append('severity', params.severity);
    if (params.status) query.append('status', params.status);
    if (params.search) query.append('search', params.search);
    if (params.page !== undefined) query.append('page', params.page.toString());
    if (params.size !== undefined) query.append('size', params.size.toString());

    const res = await api.get(`/reports?${query.toString()}`);
    return res.data.data;
  },

  async getMapIncidents(disasterTypeId?: number, severity?: SeverityLevel): Promise<DisasterReport[]> {
    const query = new URLSearchParams();
    if (disasterTypeId) query.append('disasterTypeId', disasterTypeId.toString());
    if (severity) query.append('severity', severity);
    const res = await api.get(`/map/incidents?${query.toString()}`);
    return res.data.data;
  },

  // Officer Operations
  async getOfficerReports(status?: ReportStatus, page = 0, size = 10): Promise<{ content: DisasterReport[]; totalElements: number; totalPages: number }> {
    const query = new URLSearchParams();
    if (status) query.append('status', status);
    query.append('page', page.toString());
    query.append('size', size.toString());
    const res = await api.get(`/officer/reports?${query.toString()}`);
    return res.data.data;
  },

  async verifyReport(reportId: number, verificationNotes?: string): Promise<DisasterReport> {
    const res = await api.post(`/officer/reports/${reportId}/verify`, { verificationNotes });
    return res.data.data;
  },

  async rejectReport(reportId: number, rejectionReason: string): Promise<DisasterReport> {
    const res = await api.post(`/officer/reports/${reportId}/reject`, { rejectionReason });
    return res.data.data;
  },

  async assignDepartment(reportId: number, data: {
    departmentId: number;
    responseTeamId?: number;
    priority: SeverityLevel;
    instructions?: string;
  }): Promise<DisasterReport> {
    const res = await api.post(`/officer/reports/${reportId}/assign`, data);
    return res.data.data;
  },

  async updateIncidentStatus(reportId: number, status: ReportStatus, remarks?: string): Promise<DisasterReport> {
    const res = await api.patch(`/officer/reports/${reportId}/status`, { status, remarks });
    return res.data.data;
  },
};
