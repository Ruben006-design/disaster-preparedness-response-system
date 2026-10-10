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
  MenuItem,
  Avatar,
  Grid,
} from '@mui/material';
import PersonAddOutlinedIcon from '@mui/icons-material/PersonAddOutlined';
import { authService } from '../../services/authService';
import { useAuth } from '../../context/AuthContext';
import { RoleType } from '../../types';

export const RegisterPage: React.FC = () => {
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [phone, setPhone] = useState('');
  const [role, setRole] = useState<RoleType>('STUDENT');
  const [officerRegistrationCode, setOfficerRegistrationCode] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const { login } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setLoading(true);

    try {
      const response = await authService.register({
        name,
        email,
        password,
        phone,
        role,
        officerRegistrationCode: role !== 'STUDENT' ? officerRegistrationCode : undefined,
      });

      login(response);

      if (response.role === 'ADMIN') {
        navigate('/admin');
      } else if (response.role === 'GOVERNMENT_OFFICER') {
        navigate('/officer');
      } else {
        navigate('/student');
      }
    } catch (err: any) {
      setError(err.response?.data?.message || 'Registration failed. Please check form values.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Box sx={{ minHeight: '100vh', display: 'flex', alignItems: 'center', bgcolor: '#f8fafc', py: 6 }}>
      <Container maxWidth="sm">
        <Paper sx={{ p: { xs: 3, sm: 5 }, borderRadius: 3, border: '1px solid #e2e8f0', boxShadow: '0 4px 20px rgba(0,0,0,0.04)' }}>
          <Box sx={{ textAlign: 'center', mb: 3 }}>
            <Avatar sx={{ bgcolor: '#0284c7', mx: 'auto', mb: 1.5, width: 48, height: 48 }}>
              <PersonAddOutlinedIcon />
            </Avatar>
            <Typography variant="h5" sx={{ fontWeight: 800 }}>
              Create Account
            </Typography>
            <Typography variant="body2" color="text.secondary">
              Join the National Disaster Preparedness & Education Network
            </Typography>
          </Box>

          {error && <Alert severity="error" sx={{ mb: 3 }}>{error}</Alert>}

          <Box component="form" onSubmit={handleSubmit}>
            <TextField
              label="Full Name"
              fullWidth
              required
              value={name}
              onChange={(e) => setName(e.target.value)}
              sx={{ mb: 2 }}
            />

            <TextField
              label="Email Address"
              type="email"
              fullWidth
              required
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              sx={{ mb: 2 }}
            />

            <Grid container spacing={2} sx={{ mb: 2 }}>
              <Grid item xs={12} sm={6}>
                <TextField
                  label="Password"
                  type="password"
                  fullWidth
                  required
                  helperText="Minimum 6 characters"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField
                  label="Phone Number"
                  fullWidth
                  value={phone}
                  onChange={(e) => setPhone(e.target.value)}
                />
              </Grid>
            </Grid>

            <TextField
              select
              label="Account Role"
              fullWidth
              value={role}
              onChange={(e) => setRole(e.target.value as RoleType)}
              sx={{ mb: 2 }}
            >
              <MenuItem value="STUDENT">Student / Citizen</MenuItem>
              <MenuItem value="GOVERNMENT_OFFICER">Government Officer (Authorized Agency)</MenuItem>
              <MenuItem value="ADMIN">System Administrator</MenuItem>
            </TextField>

            {role !== 'STUDENT' && (
              <TextField
                label={role === 'ADMIN' ? 'Admin Provisioning Key' : 'Officer Authorization Token'}
                type="password"
                fullWidth
                required
                helperText={
                  role === 'ADMIN'
                    ? 'Security Key: ADMIN_DISASTER_SECURE_2026'
                    : 'Security Token: OFFICER_GOV_AUTHORIZATION_2026'
                }
                value={officerRegistrationCode}
                onChange={(e) => setOfficerRegistrationCode(e.target.value)}
                sx={{ mb: 2 }}
              />
            )}

            <Button
              type="submit"
              variant="contained"
              color="primary"
              fullWidth
              size="large"
              disabled={loading}
              sx={{ py: 1.2, fontWeight: 700, mt: 1 }}
            >
              {loading ? 'Creating Account...' : 'Complete Registration'}
            </Button>
          </Box>

          <Box sx={{ mt: 3, textAlign: 'center' }}>
            <Typography variant="body2" color="text.secondary">
              Already registered?{' '}
              <Link to="/login" style={{ color: '#0284c7', fontWeight: 600, textDecoration: 'none' }}>
                Sign In
              </Link>
            </Typography>
          </Box>
        </Paper>
      </Container>
    </Box>
  );
};
