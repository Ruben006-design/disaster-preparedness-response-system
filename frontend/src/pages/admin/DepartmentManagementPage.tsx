import React, { useEffect, useState } from 'react';
import {
  Box,
  Typography,
  Card,
  CardContent,
  Grid,
  Paper,
  Button,
  Chip,
  CircularProgress,
  Alert,
  IconButton,
  TextField,
  MenuItem,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Divider,
  Stack,
  Accordion,
  AccordionSummary,
  AccordionDetails
} from '@mui/material';
import {
  ArrowBack,
  Refresh,
  Add,
  Business,
  Groups,
  ExpandMore,
  Phone,
  Email
} from '@mui/icons-material';
import { useNavigate } from 'react-router-dom';
import { EmptyState } from '../../components/common/EmptyState';
import { adminService } from '../../services/adminService';
import { GovernmentDepartment, DepartmentType } from '../../types';

export const DepartmentManagementPage: React.FC = () => {
  const navigate = useNavigate();

  const [departments, setDepartments] = useState<GovernmentDepartment[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [actionLoading, setActionLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);
  const [successMsg, setSuccessMsg] = useState<string | null>(null);

  // Create Department Dialog
  const [openDeptModal, setOpenDeptModal] = useState<boolean>(false);
  const [deptForm, setDeptForm] = useState({
    name: '',
    code: '',
    departmentType: 'DISASTER_MANAGEMENT' as DepartmentType,
    description: '',
    headName: '',
    contactPhone: '',
    contactEmail: ''
  });

  // Create Team Dialog
  const [openTeamModal, setOpenTeamModal] = useState<boolean>(false);
  const [targetDeptId, setTargetDeptId] = useState<number | null>(null);
  const [teamForm, setTeamForm] = useState({
    teamName: '',
    teamLead: '',
    contactPhone: ''
  });

  const fetchDepartments = async () => {
    try {
      setLoading(true);
      setError(null);
      const res = await adminService.getDepartments();
      setDepartments(res || []);
    } catch (err: any) {
      setError(err?.response?.data?.message || 'Failed to load emergency departments');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchDepartments();
  }, []);

  const handleCreateDept = async () => {
    if (!deptForm.name || !deptForm.code || !deptForm.contactPhone || !deptForm.contactEmail) {
      setError('Please fill in department name, code, phone, and email.');
      return;
    }
    try {
      setActionLoading(true);
      setError(null);
      await adminService.createDepartment(deptForm);
      setSuccessMsg('Government department registered successfully!');
      setOpenDeptModal(false);
      setDeptForm({
        name: '',
        code: '',
        departmentType: 'DISASTER_MANAGEMENT',
        description: '',
        headName: '',
        contactPhone: '',
        contactEmail: ''
      });
      await fetchDepartments();
    } catch (err: any) {
      setError(err?.response?.data?.message || 'Failed to create department');
    } finally {
      setActionLoading(false);
    }
  };

  const handleCreateTeam = async () => {
    if (!targetDeptId || !teamForm.teamName || !teamForm.teamLead || !teamForm.contactPhone) {
      setError('Please fill in all team fields.');
      return;
    }
    try {
      setActionLoading(true);
      setError(null);
      await adminService.createTeam({
        departmentId: targetDeptId,
        teamName: teamForm.teamName,
        teamLead: teamForm.teamLead,
        contactPhone: teamForm.contactPhone
      });
      setSuccessMsg(`Response team "${teamForm.teamName}" added successfully!`);
      setOpenTeamModal(false);
      setTeamForm({
        teamName: '',
        teamLead: '',
        contactPhone: ''
      });
      await fetchDepartments();
    } catch (err: any) {
      setError(err?.response?.data?.message || 'Failed to register response team');
    } finally {
      setActionLoading(false);
    }
  };

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
              Government Agencies & Response Fleet
            </Typography>
            <Typography variant="body2" color="text.secondary">
              Configure public safety departments, rapid deployment units, and tactical specialties
            </Typography>
          </Box>
        </Box>
        <Box sx={{ display: 'flex', gap: 1.5 }}>
          <Button variant="outlined" startIcon={<Refresh />} onClick={fetchDepartments}>
            Refresh
          </Button>
          <Button
            variant="contained"
            color="primary"
            startIcon={<Add />}
            onClick={() => setOpenDeptModal(true)}
          >
            Add Department
          </Button>
        </Box>
      </Box>

      {error && <Alert severity="error">{error}</Alert>}
      {successMsg && <Alert severity="success">{successMsg}</Alert>}

      {loading ? (
        <Box sx={{ display: 'flex', justifyContent: 'center', py: 8 }}>
          <CircularProgress />
        </Box>
      ) : departments.length === 0 ? (
        <EmptyState
          title="No Agencies Registered"
          description="Register government departments and emergency responder teams to begin response dispatch."
        />
      ) : (
        <Stack spacing={2.5}>
          {departments.map((dept) => (
            <Accordion key={dept.id} defaultExpanded sx={{ border: '1px solid', borderColor: 'divider', borderRadius: '8px !important' }}>
              <AccordionSummary expandIcon={<ExpandMore />}>
                <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', width: '100%', pr: 2, flexWrap: 'wrap', gap: 1 }}>
                  <Box sx={{ display: 'flex', alignItems: 'center', gap: 1.5 }}>
                    <Business color="primary" />
                    <Box>
                      <Typography variant="h6" fontWeight={700}>
                        {dept.name} ({dept.code})
                      </Typography>
                      <Typography variant="caption" color="text.secondary">
                        Lead: {dept.headName || 'Agency Lead'}
                      </Typography>
                    </Box>
                  </Box>
                  <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                    <Chip label={dept.departmentType} size="small" color="primary" variant="outlined" />
                    <Chip
                      label={`${dept.responseTeams?.length || 0} Teams`}
                      size="small"
                      color="secondary"
                      variant="filled"
                    />
                  </Box>
                </Box>
              </AccordionSummary>
              <AccordionDetails sx={{ pt: 0 }}>
                <Divider sx={{ mb: 2 }} />
                
                {/* Department Info Bar */}
                <Grid container spacing={2} sx={{ mb: 2 }}>
                  <Grid item xs={12} sm={6}>
                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                      <Phone fontSize="small" color="action" />
                      <Typography variant="body2">{dept.contactPhone || 'N/A'}</Typography>
                    </Box>
                  </Grid>
                  <Grid item xs={12} sm={6}>
                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                      <Email fontSize="small" color="action" />
                      <Typography variant="body2">{dept.contactEmail || 'N/A'}</Typography>
                    </Box>
                  </Grid>
                </Grid>

                {/* Response Teams Section */}
                <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 1.5 }}>
                  <Typography variant="subtitle2" fontWeight={700} color="text.secondary">
                    OPERATIONAL RESPONSE TEAMS
                  </Typography>
                  <Button
                    size="small"
                    variant="text"
                    startIcon={<Add />}
                    onClick={() => {
                      setTargetDeptId(dept.id);
                      setOpenTeamModal(true);
                    }}
                  >
                    Add Team to {dept.name}
                  </Button>
                </Box>

                {(!dept.responseTeams || dept.responseTeams.length === 0) ? (
                  <Paper variant="outlined" sx={{ p: 2, textAlign: 'center', bgcolor: 'action.hover' }}>
                    <Typography variant="body2" color="text.secondary">
                      No response units registered yet under this department.
                    </Typography>
                  </Paper>
                ) : (
                  <Grid container spacing={2}>
                    {dept.responseTeams.map((team) => (
                      <Grid item xs={12} sm={6} md={4} key={team.id}>
                        <Paper variant="outlined" sx={{ p: 2, display: 'flex', flexDirection: 'column', gap: 1 }}>
                          <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
                            <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                              <Groups color="action" />
                              <Typography variant="subtitle1" fontWeight={700}>
                                {team.teamName}
                              </Typography>
                            </Box>
                            <Chip
                              label={team.status}
                              size="small"
                              color={team.status === 'AVAILABLE' ? 'success' : 'warning'}
                              variant="filled"
                            />
                          </Box>
                          <Typography variant="caption" color="text.secondary">
                            Team Lead: <strong>{team.teamLead || 'Commander'}</strong>
                          </Typography>
                          <Typography variant="caption" color="text.secondary">
                            Contact Phone: <strong>{team.contactPhone || 'N/A'}</strong>
                          </Typography>
                        </Paper>
                      </Grid>
                    ))}
                  </Grid>
                )}
              </AccordionDetails>
            </Accordion>
          ))}
        </Stack>
      )}

      {/* --- ADD DEPARTMENT MODAL --- */}
      <Dialog open={openDeptModal} onClose={() => setOpenDeptModal(false)} maxWidth="sm" fullWidth>
        <DialogTitle sx={{ fontWeight: 700 }}>
          Register Government Department
        </DialogTitle>
        <DialogContent dividers sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
          <TextField
            label="Department Name"
            required
            fullWidth
            value={deptForm.name}
            onChange={(e) => setDeptForm({ ...deptForm, name: e.target.value })}
            placeholder="e.g., National Disaster Response Force"
          />

          <TextField
            label="Agency Code"
            required
            fullWidth
            value={deptForm.code}
            onChange={(e) => setDeptForm({ ...deptForm, code: e.target.value })}
            placeholder="e.g., NDRF-NAT"
          />

          <TextField
            select
            label="Department Type"
            required
            fullWidth
            value={deptForm.departmentType}
            onChange={(e) => setDeptForm({ ...deptForm, departmentType: e.target.value as DepartmentType })}
          >
            <MenuItem value="FIRE_AND_RESCUE">FIRE AND RESCUE</MenuItem>
            <MenuItem value="POLICE">POLICE</MenuItem>
            <MenuItem value="MEDICAL_EMERGENCY">MEDICAL EMERGENCY</MenuItem>
            <MenuItem value="DISASTER_MANAGEMENT">DISASTER MANAGEMENT</MenuItem>
            <MenuItem value="COAST_GUARD">COAST GUARD</MenuItem>
            <MenuItem value="MUNICIPAL_SERVICES">MUNICIPAL SERVICES</MenuItem>
          </TextField>

          <TextField
            label="Head Officer / Director Name"
            fullWidth
            value={deptForm.headName}
            onChange={(e) => setDeptForm({ ...deptForm, headName: e.target.value })}
          />

          <TextField
            label="Emergency Contact Phone"
            required
            fullWidth
            value={deptForm.contactPhone}
            onChange={(e) => setDeptForm({ ...deptForm, contactPhone: e.target.value })}
          />

          <TextField
            label="Official Dispatch Email"
            required
            type="email"
            fullWidth
            value={deptForm.contactEmail}
            onChange={(e) => setDeptForm({ ...deptForm, contactEmail: e.target.value })}
          />

          <TextField
            label="Operational Description"
            multiline
            rows={2}
            fullWidth
            value={deptForm.description}
            onChange={(e) => setDeptForm({ ...deptForm, description: e.target.value })}
          />
        </DialogContent>
        <DialogActions sx={{ p: 2 }}>
          <Button onClick={() => setOpenDeptModal(false)} disabled={actionLoading}>
            Cancel
          </Button>
          <Button
            variant="contained"
            color="primary"
            onClick={handleCreateDept}
            disabled={actionLoading}
          >
            {actionLoading ? 'Saving...' : 'Register Department'}
          </Button>
        </DialogActions>
      </Dialog>

      {/* --- ADD TEAM MODAL --- */}
      <Dialog open={openTeamModal} onClose={() => setOpenTeamModal(false)} maxWidth="xs" fullWidth>
        <DialogTitle sx={{ fontWeight: 700 }}>
          Add Response Unit
        </DialogTitle>
        <DialogContent dividers sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
          <TextField
            label="Team / Unit Name"
            required
            fullWidth
            value={teamForm.teamName}
            onChange={(e) => setTeamForm({ ...teamForm, teamName: e.target.value })}
            placeholder="e.g., Quick Response Unit Alpha"
          />

          <TextField
            label="Commander / Leader Name"
            required
            fullWidth
            value={teamForm.teamLead}
            onChange={(e) => setTeamForm({ ...teamForm, teamLead: e.target.value })}
          />

          <TextField
            label="Field Dispatch Hotline"
            required
            fullWidth
            value={teamForm.contactPhone}
            onChange={(e) => setTeamForm({ ...teamForm, contactPhone: e.target.value })}
          />
        </DialogContent>
        <DialogActions sx={{ p: 2 }}>
          <Button onClick={() => setOpenTeamModal(false)} disabled={actionLoading}>
            Cancel
          </Button>
          <Button
            variant="contained"
            color="primary"
            onClick={handleCreateTeam}
            disabled={actionLoading}
          >
            {actionLoading ? 'Saving...' : 'Add Response Team'}
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};
export default DepartmentManagementPage;
