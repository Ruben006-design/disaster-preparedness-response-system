import { api } from './api';
import { AuthResponse, UserProfile } from '../types';

export const authService = {
  async register(data: any): Promise<AuthResponse> {
    const res = await api.post('/auth/register', data);
    return res.data.data;
  },

  async login(credentials: { email: string; password: string }): Promise<AuthResponse> {
    const res = await api.post('/auth/login', credentials);
    return res.data.data;
  },

  async getCurrentProfile(): Promise<UserProfile> {
    const res = await api.get('/users/me');
    return res.data.data;
  },

  async updateProfile(data: { name: string; phone?: string }): Promise<UserProfile> {
    const res = await api.put('/users/me', data);
    return res.data.data;
  },

  async changePassword(data: { currentPassword: string; newPassword: string }): Promise<void> {
    await api.put('/users/me/change-password', data);
  },
};
