import React, { useState } from 'react';
import {
  Box,
  Typography,
  Card,
  CardContent,
  Grid,
  TextField,
  Button,
  Alert,
  Divider,
  Chip,
  Paper
} from '@mui/material';
import {
  Person,
  Lock,
  Save
} from '@mui/icons-material';
import { useAuth } from '../../context/AuthContext';
import { authService } from '../../services/authService';

export const ProfilePage: React.FC = () => {
  const { user, updateUser } = useAuth();

  // Profile Update State
  const [name, setName] = useState(user?.name || '');
  const [phone, setPhone] = useState('');
  const [profileSuccess, setProfileSuccess] = useState<string | null>(null);
  const [profileError, setProfileError] = useState<string | null>(null);
  const [profileLoading, setProfileLoading] = useState(false);

  // Change Password State
  const [currentPassword, setCurrentPassword] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [passwordSuccess, setPasswordSuccess] = useState<string | null>(null);
  const [passwordError, setPasswordError] = useState<string | null>(null);
  const [passwordLoading, setPasswordLoading] = useState(false);

  const handleUpdateProfile = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      setProfileLoading(true);
      setProfileError(null);
      setProfileSuccess(null);
      await authService.updateProfile({ name, phone });
      updateUser(name);
      setProfileSuccess('Profile information updated successfully.');
    } catch (err: any) {
      setProfileError(err?.response?.data?.message || 'Failed to update profile.');
    } finally {
      setProfileLoading(false);
    }
  };

  const handleChangePassword = async (e: React.FormEvent) => {
    e.preventDefault();
    if (newPassword !== confirmPassword) {
      setPasswordError('New password and confirmation do not match.');
      return;
    }
    if (newPassword.length < 8) {
      setPasswordError('New password must be at least 8 characters.');
      return;
    }

    try {
      setPasswordLoading(true);
      setPasswordError(null);
      setPasswordSuccess(null);
      await authService.changePassword({ currentPassword, newPassword });
      setPasswordSuccess('Password updated successfully. Please use your new password next time you log in.');
      setCurrentPassword('');
      setNewPassword('');
      setConfirmPassword('');
    } catch (err: any) {
      setPasswordError(err?.response?.data?.message || 'Failed to change password. Verify your current password.');
    } finally {
      setPasswordLoading(false);
    }
  };

  const isOfficer = user?.role === 'GOVERNMENT_OFFICER';
  const isAdmin = user?.role === 'ADMIN';

  return (
    <Box sx={{ display: 'flex', flexDirection: 'column', gap: 3 }}>
      {/* Header */}
      <Box>
        <Typography variant="h4" fontWeight={700}>
          User Profile & Security
        </Typography>
        <Typography variant="body2" color="text.secondary">
          Manage your personal details, credentials, and authentication preferences
        </Typography>
      </Box>

      <Grid container spacing={3}>
        {/* Left Column: Account Details Card */}
        <Grid item xs={12} md={6}>
          <Card>
            <CardContent sx={{ display: 'flex', flexDirection: 'column', gap: 2.5 }}>
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 1.5 }}>
                <Person color="primary" />
                <Typography variant="h6" fontWeight={700}>
                  Account Identity & Affiliation
                </Typography>
              </Box>
              <Divider />

              {/* Status and Role Badges */}
              <Box sx={{ display: 'flex', gap: 1, alignItems: 'center' }}>
                <Chip
                  label={user?.role.replace(/_/g, ' ')}
                  color={isAdmin ? 'error' : isOfficer ? 'warning' : 'primary'}
                  sx={{ fontWeight: 700 }}
                />
              </Box>

              {/* Role specific identifiers */}
              <Paper variant="outlined" sx={{ p: 2, bgcolor: 'background.default' }}>
                <Typography variant="caption" color="text.secondary">
                  PRIMARY EMAIL
                </Typography>
                <Typography variant="body1" fontWeight={600} sx={{ mb: 1.5 }}>
                  {user?.email}
                </Typography>

                <Typography variant="caption" color="text.secondary">
                  USER ID
                </Typography>
                <Typography variant="body1" fontWeight={600}>
                  #{user?.id}
                </Typography>
              </Paper>

              {profileSuccess && <Alert severity="success">{profileSuccess}</Alert>}
              {profileError && <Alert severity="error">{profileError}</Alert>}

              {/* Edit Details Form */}
              <Box component="form" onSubmit={handleUpdateProfile} sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
                <TextField
                  label="Full Name"
                  fullWidth
                  required
                  value={name}
                  onChange={(e) => setName(e.target.value)}
                />

                <TextField
                  label="Contact Phone Number"
                  fullWidth
                  value={phone}
                  onChange={(e) => setPhone(e.target.value)}
                  placeholder="+1 (555) 000-0000"
                />

                <Button
                  type="submit"
                  variant="contained"
                  color="primary"
                  startIcon={<Save />}
                  disabled={profileLoading}
                >
                  {profileLoading ? 'Saving...' : 'Save Profile Changes'}
                </Button>
              </Box>
            </CardContent>
          </Card>
        </Grid>

        {/* Right Column: Security & Password Change */}
        <Grid item xs={12} md={6}>
          <Card>
            <CardContent sx={{ display: 'flex', flexDirection: 'column', gap: 2.5 }}>
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 1.5 }}>
                <Lock color="primary" />
                <Typography variant="h6" fontWeight={700}>
                  Security & Authentication
                </Typography>
              </Box>
              <Divider />

              <Typography variant="body2" color="text.secondary">
                Ensure your account is protected with a secure password containing at least 8 characters, letters, and numbers.
              </Typography>

              {passwordSuccess && <Alert severity="success">{passwordSuccess}</Alert>}
              {passwordError && <Alert severity="error">{passwordError}</Alert>}

              <Box component="form" onSubmit={handleChangePassword} sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
                <TextField
                  label="Current Password"
                  type="password"
                  required
                  fullWidth
                  value={currentPassword}
                  onChange={(e) => setCurrentPassword(e.target.value)}
                />

                <TextField
                  label="New Password"
                  type="password"
                  required
                  fullWidth
                  value={newPassword}
                  onChange={(e) => setNewPassword(e.target.value)}
                  helperText="Minimum 8 characters"
                />

                <TextField
                  label="Confirm New Password"
                  type="password"
                  required
                  fullWidth
                  value={confirmPassword}
                  onChange={(e) => setConfirmPassword(e.target.value)}
                />

                <Button
                  type="submit"
                  variant="contained"
                  color="secondary"
                  disabled={passwordLoading}
                >
                  {passwordLoading ? 'Updating Password...' : 'Update Password'}
                </Button>
              </Box>
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    </Box>
  );
};
export default ProfilePage;
