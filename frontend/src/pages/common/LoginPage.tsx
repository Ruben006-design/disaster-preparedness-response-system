import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import {
  Box,
  Container,
  Paper,
  Typography,
  TextField,
  Button,
  Alert,
  Divider,
  Chip,
  Avatar,
} from '@mui/material';
import LockOutlinedIcon from '@mui/icons-material/LockOutlined';
import SecurityIcon from '@mui/icons-material/Security';
import { authService } from '../../services/authService';
import { useAuth } from '../../context/AuthContext';

export const LoginPage: React.FC = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const { login } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setLoading(true);

    try {
      const response = await authService.login({ email, password });
      login(response);

      if (response.role === 'ADMIN') {
        navigate('/admin');
      } else if (response.role === 'GOVERNMENT_OFFICER') {
        navigate('/officer');
      } else {
        navigate('/student');
      }
    } catch (err: any) {
      setError(err.response?.data?.message || 'Invalid email or password. Please verify credentials.');
    } finally {
      setLoading(false);
    }
  };

  const fillCredentials = (userEmail: string, userPass: string) => {
    setEmail(userEmail);
    setPassword(userPass);
    setError(null);
  };

  return (
    <Box sx={{ minHeight: '100vh', display: 'flex', alignItems: 'center', bgcolor: '#f8fafc', py: 6 }}>
      <Container maxWidth="sm">
        <Paper sx={{ p: { xs: 3, sm: 5 }, borderRadius: 3, border: '1px solid #e2e8f0', boxShadow: '0 4px 20px rgba(0,0,0,0.04)' }}>
          <Box sx={{ textAlign: 'center', mb: 3 }}>
            <Avatar sx={{ bgcolor: '#0f172a', mx: 'auto', mb: 1.5, width: 48, height: 48 }}>
              <SecurityIcon />
            </Avatar>
            <Typography variant="h5" sx={{ fontWeight: 800 }}>
              Sign In to Platform
            </Typography>
            <Typography variant="body2" color="text.secondary">
              Disaster Preparedness & Response Education System
            </Typography>
          </Box>

          {error && <Alert severity="error" sx={{ mb: 3 }}>{error}</Alert>}

          <Box component="form" onSubmit={handleSubmit}>
            <TextField
              label="Email Address"
              type="email"
              fullWidth
              required
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              sx={{ mb: 2.5 }}
            />
            <TextField
              label="Password"
              type="password"
              fullWidth
              required
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              sx={{ mb: 3 }}
            />

            <Button
              type="submit"
              variant="contained"
              color="primary"
              fullWidth
              size="large"
              disabled={loading}
              sx={{ py: 1.2, fontWeight: 700 }}
            >
              {loading ? 'Authenticating...' : 'Sign In'}
            </Button>
          </Box>

          {/* Quick Test Credential Helpers for Academic Review */}
          <Box sx={{ mt: 4, pt: 3, borderTop: '1px solid #e2e8f0' }}>
            <Typography variant="caption" sx={{ fontWeight: 700, color: 'text.secondary', display: 'block', mb: 1.5, textAlign: 'center' }}>
              DEMONSTRATION & REVIEW PRESET CREDENTIALS:
            </Typography>
            <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 1, justifyContent: 'center' }}>
              <Chip
                label="Student Account"
                color="info"
                variant="outlined"
                clickable
                onClick={() => fillCredentials('student.alex@university.edu', 'StudentPass@123')}
              />
              <Chip
                label="Gov Officer Account"
                color="secondary"
                variant="outlined"
                clickable
                onClick={() => fillCredentials('officer.sharma@disasterprep.gov', 'OfficerPass@123')}
              />
              <Chip
                label="Admin Account"
                color="primary"
                variant="outlined"
                clickable
                onClick={() => fillCredentials('admin@disasterprep.gov', 'AdminPassword@123')}
              />
            </Box>
          </Box>

          <Box sx={{ mt: 3, textAlign: 'center' }}>
            <Typography variant="body2" color="text.secondary">
              Don't have an account?{' '}
              <Link to="/register" style={{ color: '#0284c7', fontWeight: 600, textDecoration: 'none' }}>
                Create Student Account
              </Link>
            </Typography>
          </Box>
        </Paper>
      </Container>
    </Box>
  );
};
