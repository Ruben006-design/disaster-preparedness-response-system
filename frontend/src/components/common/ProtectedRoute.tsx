import React from 'react';
import { Navigate, Outlet } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { RoleType } from '../../types';
import { Box, Typography, Button, Container, Paper } from '@mui/material';
import LockOutlinedIcon from '@mui/icons-material/LockOutlined';

interface ProtectedRouteProps {
  allowedRoles?: RoleType[];
}

export const ProtectedRoute: React.FC<ProtectedRouteProps> = ({ allowedRoles }) => {
  const { isAuthenticated, role } = useAuth();

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  if (allowedRoles && role && !allowedRoles.includes(role)) {
    return (
      <Container maxWidth="sm" sx={{ py: 10 }}>
        <Paper sx={{ p: 5, textAlign: 'center', borderRadius: 3, border: '1px solid #fee2e2' }}>
          <LockOutlinedIcon sx={{ fontSize: 60, color: 'error.main', mb: 2 }} />
          <Typography variant="h5" sx={{ fontWeight: 800, mb: 1, color: 'text.primary' }}>
            Access Restricted
          </Typography>
          <Typography variant="body1" sx={{ color: 'text.secondary', mb: 3 }}>
            You do not possess the required government or administrative clearance to access this portal section.
          </Typography>
          <Button variant="contained" color="primary" onClick={() => window.history.back()}>
            Return to Dashboard
          </Button>
        </Paper>
      </Container>
    );
  }

  return <Outlet />;
};
