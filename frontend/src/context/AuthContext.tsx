import React, { createContext, useContext, useState, useEffect } from 'react';
import { AuthResponse, RoleType } from '../types';

interface AuthContextType {
  user: AuthResponse | null;
  token: string | null;
  role: RoleType | null;
  isAuthenticated: boolean;
  login: (data: AuthResponse) => void;
  logout: () => void;
  updateUser: (name: string) => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<AuthResponse | null>(() => {
    const saved = localStorage.getItem('disaster_user');
    return saved ? JSON.parse(saved) : null;
  });

  const [token, setToken] = useState<string | null>(() => {
    return localStorage.getItem('disaster_token');
  });

  const login = (data: AuthResponse) => {
    setUser(data);
    setToken(data.token);
    localStorage.setItem('disaster_token', data.token);
    localStorage.setItem('disaster_user', JSON.stringify(data));
  };

  const logout = () => {
    setUser(null);
    setToken(null);
    localStorage.removeItem('disaster_token');
    localStorage.removeItem('disaster_user');
    window.location.href = '/login';
  };

  const updateUser = (name: string) => {
    if (user) {
      const updated = { ...user, name };
      setUser(updated);
      localStorage.setItem('disaster_user', JSON.stringify(updated));
    }
  };

  return (
    <AuthContext.Provider
      value={{
        user,
        token,
        role: user?.role || null,
        isAuthenticated: !!token && !!user,
        login,
        logout,
        updateUser,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
