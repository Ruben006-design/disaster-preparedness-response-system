import React, { useEffect, useState } from 'react';
import {
  Box,
  Typography,
  Card,
  CardContent,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Button,
  Chip,
  CircularProgress,
  Alert,
  IconButton,
  Tooltip,
  TextField,
  MenuItem,
  Stack,
  Switch,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions
} from '@mui/material';
import {
  ArrowBack,
  Refresh,
  Clear
} from '@mui/icons-material';
import { useNavigate } from 'react-router-dom';
import { EmptyState } from '../../components/common/EmptyState';
import { adminService } from '../../services/adminService';
import { UserProfile, RoleType, UserStatus } from '../../types';

export const UserManagementPage: React.FC = () => {
  const navigate = useNavigate();

  const [users, setUsers] = useState<UserProfile[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [actionLoading, setActionLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);
  const [successMsg, setSuccessMsg] = useState<string | null>(null);

  // Filters
  const [roleFilter, setRoleFilter] = useState<string>('');
  const [statusFilter, setStatusFilter] = useState<string>('');
  const [searchQuery, setSearchQuery] = useState<string>('');

  // Status toggle confirmation
  const [confirmDialog, setConfirmDialog] = useState<{
    open: boolean;
    user: UserProfile | null;
  }>({
    open: false,
    user: null
  });

  const fetchUsers = async () => {
    try {
      setLoading(true);
      setError(null);
      const res = await adminService.getUsers(0, 100);
      setUsers(res.content || []);
    } catch (err: any) {
      setError(err?.response?.data?.message || 'Failed to load user accounts');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchUsers();
  }, []);

  const handleToggleStatus = async () => {
    if (!confirmDialog.user) return;
    try {
      setActionLoading(true);
      setError(null);
      await adminService.toggleUserStatus(confirmDialog.user.id);
      setSuccessMsg(`User status updated successfully.`);
      setConfirmDialog({ open: false, user: null });
      await fetchUsers();
    } catch (err: any) {
      setError(err?.response?.data?.message || 'Failed to update user status');
    } finally {
      setActionLoading(false);
    }
  };

  const filteredUsers = users.filter((u) => {
    if (roleFilter && u.role !== roleFilter) return false;
    if (statusFilter && u.status !== statusFilter) return false;
    if (!searchQuery) return true;
    const q = searchQuery.toLowerCase();
    return (
      (u.name && u.name.toLowerCase().includes(q)) ||
      (u.email && u.email.toLowerCase().includes(q)) ||
      (u.phone && u.phone.toLowerCase().includes(q))
    );
  });

  return (
    <Box sx={{ display: 'flex', flexDirection: 'column', gap: 3 }}>
      {/* Header */}
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: 2 }}>
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1.5 }}>
          <IconButton onClick={() => navigate('/admin')}>
            <ArrowBack />
          </IconButton>
          <Box>
            <Typography variant="h4" fontWeight={700}>
              User Account Directory
            </Typography>
            <Typography variant="body2" color="text.secondary">
              Manage system permissions, verify credentials, and govern access across all 3 roles
            </Typography>
          </Box>
        </Box>
        <Box sx={{ display: 'flex', gap: 1.5 }}>
          <Button variant="outlined" startIcon={<Refresh />} onClick={fetchUsers}>
            Refresh
          </Button>
        </Box>
      </Box>

      {error && <Alert severity="error">{error}</Alert>}
      {successMsg && <Alert severity="success">{successMsg}</Alert>}

      {/* Filter Toolbar */}
      <Card>
        <CardContent sx={{ py: 2 }}>
          <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2} alignItems="center">
            <TextField
              size="small"
              placeholder="Search by name, email, phone..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              sx={{ flexGrow: 1, minWidth: 260 }}
            />

            <TextField
              select
              size="small"
              label="Role"
              value={roleFilter}
              onChange={(e) => setRoleFilter(e.target.value)}
              sx={{ minWidth: 160 }}
            >
              <MenuItem value="">All Roles</MenuItem>
              <MenuItem value="STUDENT">STUDENT</MenuItem>
              <MenuItem value="GOVERNMENT_OFFICER">GOVERNMENT OFFICER</MenuItem>
              <MenuItem value="ADMIN">ADMIN</MenuItem>
            </TextField>

            <TextField
              select
              size="small"
              label="Status"
              value={statusFilter}
              onChange={(e) => setStatusFilter(e.target.value)}
              sx={{ minWidth: 150 }}
            >
              <MenuItem value="">All Statuses</MenuItem>
              <MenuItem value="ACTIVE">ACTIVE</MenuItem>
              <MenuItem value="INACTIVE">INACTIVE</MenuItem>
              <MenuItem value="SUSPENDED">SUSPENDED</MenuItem>
            </TextField>

            {(roleFilter || statusFilter || searchQuery) && (
              <Button
                variant="text"
                color="secondary"
                startIcon={<Clear />}
                onClick={() => {
                  setRoleFilter('');
                  setStatusFilter('');
                  setSearchQuery('');
                }}
              >
                Reset
              </Button>
            )}
          </Stack>
        </CardContent>
      </Card>

      {/* Table */}
      <Card>
        <CardContent sx={{ p: 0 }}>
          {loading ? (
            <Box sx={{ display: 'flex', justifyContent: 'center', py: 8 }}>
              <CircularProgress />
            </Box>
          ) : filteredUsers.length === 0 ? (
            <Box sx={{ py: 6 }}>
              <EmptyState
                title="No Users Found"
                description="No user records match your selected search and filtering criteria."
              />
            </Box>
          ) : (
            <TableContainer component={Paper} elevation={0}>
              <Table sx={{ minWidth: 800 }}>
                <TableHead>
                  <TableRow sx={{ bgcolor: 'action.hover' }}>
                    <TableCell sx={{ fontWeight: 700 }}>User ID</TableCell>
                    <TableCell sx={{ fontWeight: 700 }}>Full Name & Email</TableCell>
                    <TableCell sx={{ fontWeight: 700 }}>System Role</TableCell>
                    <TableCell sx={{ fontWeight: 700 }}>Phone</TableCell>
                    <TableCell sx={{ fontWeight: 700 }}>Status</TableCell>
                    <TableCell sx={{ fontWeight: 700 }}>Registered On</TableCell>
                    <TableCell align="right" sx={{ fontWeight: 700 }}>Account Access</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {filteredUsers.map((u) => {
                    const isOfficer = u.role === 'GOVERNMENT_OFFICER';
                    const isAdmin = u.role === 'ADMIN';

                    return (
                      <TableRow key={u.id} hover>
                        <TableCell sx={{ fontFamily: 'monospace', fontWeight: 600 }}>
                          #{u.id}
                        </TableCell>
                        <TableCell>
                          <Typography variant="body2" fontWeight={600}>
                            {u.name}
                          </Typography>
                          <Typography variant="caption" color="text.secondary">
                            {u.email}
                          </Typography>
                        </TableCell>
                        <TableCell>
                          <Chip
                            label={u.role.replace(/_/g, ' ')}
                            size="small"
                            color={isAdmin ? 'error' : isOfficer ? 'warning' : 'primary'}
                            variant="filled"
                          />
                        </TableCell>
                        <TableCell sx={{ fontSize: '0.8125rem' }}>
                          {u.phone || '—'}
                        </TableCell>
                        <TableCell>
                          <Chip
                            label={u.status}
                            size="small"
                            color={u.status === 'ACTIVE' ? 'success' : 'default'}
                            variant={u.status === 'ACTIVE' ? 'filled' : 'outlined'}
                          />
                        </TableCell>
                        <TableCell sx={{ whiteSpace: 'nowrap', fontSize: '0.8125rem' }}>
                          {new Date(u.createdAt).toLocaleDateString()}
                        </TableCell>
                        <TableCell align="right">
                          <Tooltip title={u.status === 'ACTIVE' ? 'Deactivate Account' : 'Activate Account'}>
                            <Switch
                              checked={u.status === 'ACTIVE'}
                              onChange={() =>
                                setConfirmDialog({
                                  open: true,
                                  user: u
                                })
                              }
                              disabled={u.role === 'ADMIN'}
                              color="success"
                            />
                          </Tooltip>
                        </TableCell>
                      </TableRow>
                    );
                  })}
                </TableBody>
              </Table>
            </TableContainer>
          )}
        </CardContent>
      </Card>

      {/* Confirmation Dialog */}
      <Dialog
        open={confirmDialog.open}
        onClose={() => setConfirmDialog({ open: false, user: null })}
      >
        <DialogTitle sx={{ fontWeight: 700 }}>
          Confirm Account Status Toggle
        </DialogTitle>
        <DialogContent dividers>
          <Typography variant="body1">
            Are you sure you want to toggle status for <strong>{confirmDialog.user?.name}</strong> ({confirmDialog.user?.email})?
          </Typography>
          {confirmDialog.user?.status === 'ACTIVE' && (
            <Alert severity="warning" sx={{ mt: 2 }}>
              The user will be immediately blocked from logging in until reactivated.
            </Alert>
          )}
        </DialogContent>
        <DialogActions sx={{ p: 2 }}>
          <Button
            onClick={() => setConfirmDialog({ open: false, user: null })}
            disabled={actionLoading}
          >
            Cancel
          </Button>
          <Button
            variant="contained"
            color="primary"
            onClick={handleToggleStatus}
            disabled={actionLoading}
          >
            {actionLoading ? 'Updating...' : `Confirm Change`}
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};
export default UserManagementPage;
