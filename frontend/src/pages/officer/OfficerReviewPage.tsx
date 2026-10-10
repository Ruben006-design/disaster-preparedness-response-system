import React, { useEffect, useState } from 'react';
import {
  Box,
  Grid,
  Typography,
  Card,
  CardContent,
  Chip,
  Button,
  CircularProgress,
  Alert,
  Divider,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  MenuItem,
  Stack,
  IconButton,
  Paper
} from '@mui/material';
import {
  ArrowBack,
  CheckCircle,
  Cancel,
  AssignmentTurnedIn,
  Update,
  Place,
  Person,
  Security
} from '@mui/icons-material';
import { useParams, useNavigate } from 'react-router-dom';
import { StatusBadge, SeverityBadge } from '../../components/common/Badges';
import { reportService } from '../../services/reportService';
import { adminService } from '../../services/adminService';
import {
  DisasterReport,
  GovernmentDepartment,
  ResponseTeam,
  ReportStatus,
  SeverityLevel
} from '../../types';

export const OfficerReviewPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();

  const [report, setReport] = useState<DisasterReport | null>(null);
  const [departments, setDepartments] = useState<GovernmentDepartment[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [actionLoading, setActionLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);
  const [successMsg, setSuccessMsg] = useState<string | null>(null);

  // Dialog States
  const [verifyDialogOpen, setVerifyDialogOpen] = useState<boolean>(false);
  const [verifyNotes, setVerifyNotes] = useState<string>('');

  const [rejectDialogOpen, setRejectDialogOpen] = useState<boolean>(false);
  const [rejectReason, setRejectReason] = useState<string>('');
  const [rejectError, setRejectError] = useState<string | null>(null);

  const [assignDialogOpen, setAssignDialogOpen] = useState<boolean>(false);
  const [selectedDeptId, setSelectedDeptId] = useState<number | ''>('');
  const [selectedTeamId, setSelectedTeamId] = useState<number | ''>('');
  const [assignPriority, setAssignPriority] = useState<SeverityLevel>('HIGH');
  const [assignInstructions, setAssignInstructions] = useState<string>('');

  const [statusDialogOpen, setStatusDialogOpen] = useState<boolean>(false);
  const [nextStatus, setNextStatus] = useState<string>('');
  const [statusNotes, setStatusNotes] = useState<string>('');

  const fetchDetails = async () => {
    if (!id) return;
    try {
      setLoading(true);
      setError(null);
      const rep = await reportService.getReportById(parseInt(id, 10));
      setReport(rep);

      // Fetch departments for assignment dialog
      const depts = await adminService.getDepartments();
      setDepartments(depts);
    } catch (err: any) {
      setError(err?.response?.data?.message || 'Failed to load report details');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchDetails();
  }, [id]);

  // Handle Verify Action
  const handleVerify = async () => {
    if (!report) return;
    try {
      setActionLoading(true);
      setError(null);
      await reportService.verifyReport(report.id, verifyNotes);
      setVerifyDialogOpen(false);
      setSuccessMsg('Report verified successfully! It is now visible on public maps and ready for team assignment.');
      await fetchDetails();
    } catch (err: any) {
      setError(err?.response?.data?.message || 'Verification failed');
    } finally {
      setActionLoading(false);
    }
  };

  // Handle Reject Action (Mandatory Reason)
  const handleReject = async () => {
    if (!report) return;
    if (!rejectReason.trim()) {
      setRejectError('A formal rejection reason is strictly required.');
      return;
    }
    try {
      setActionLoading(true);
      setRejectError(null);
      await reportService.rejectReport(report.id, rejectReason);
      setRejectDialogOpen(false);
      setSuccessMsg('Incident report has been rejected.');
      await fetchDetails();
    } catch (err: any) {
      setRejectError(err?.response?.data?.message || 'Rejection failed');
    } finally {
      setActionLoading(false);
    }
  };

  // Handle Assignment Action (Restricted to VERIFIED)
  const handleAssign = async () => {
    if (!report || !selectedDeptId) return;
    try {
      setActionLoading(true);
      setError(null);
      await reportService.assignDepartment(report.id, {
        departmentId: Number(selectedDeptId),
        responseTeamId: selectedTeamId ? Number(selectedTeamId) : undefined,
        priority: assignPriority,
        instructions: assignInstructions
      });
      setAssignDialogOpen(false);
      setSuccessMsg('Agency and response team assigned successfully! Emergency notification dispatched.');
      await fetchDetails();
    } catch (err: any) {
      setError(err?.response?.data?.message || 'Assignment failed');
    } finally {
      setActionLoading(false);
    }
  };

  // Handle Status Update
  const handleUpdateStatus = async () => {
    if (!report || !nextStatus) return;
    try {
      setActionLoading(true);
      setError(null);
      await reportService.updateIncidentStatus(report.id, nextStatus as ReportStatus, statusNotes);
      setStatusDialogOpen(false);
      setSuccessMsg(`Incident state updated to ${nextStatus.replace(/_/g, ' ')} successfully.`);
      await fetchDetails();
    } catch (err: any) {
      setError(err?.response?.data?.message || 'Status transition failed');
    } finally {
      setActionLoading(false);
    }
  };

  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '60vh' }}>
        <CircularProgress size={48} />
      </Box>
    );
  }

  if (!report) {
    return (
      <Box sx={{ p: 4 }}>
        <Alert severity="error">Incident record not found.</Alert>
        <Button sx={{ mt: 2 }} variant="outlined" onClick={() => navigate('/officer')}>
          Back to Dashboard
        </Button>
      </Box>
    );
  }

  const selectedDepartment = departments.find((d) => d.id === selectedDeptId);
  const availableTeams: ResponseTeam[] = selectedDepartment?.responseTeams || [];

  return (
    <Box sx={{ display: 'flex', flexDirection: 'column', gap: 3 }}>
      {/* Top Header & Navigation */}
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: 2 }}>
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1.5 }}>
          <IconButton onClick={() => navigate('/officer')}>
            <ArrowBack />
          </IconButton>
          <Box>
            <Box sx={{ display: 'flex', alignItems: 'center', gap: 1.5 }}>
              <Typography variant="h4" fontWeight={700}>
                Review Incident
              </Typography>
              <Chip
                label={report.reportCode}
                color="primary"
                sx={{ fontFamily: 'monospace', fontWeight: 700 }}
              />
            </Box>
            <Typography variant="body2" color="text.secondary">
              Submitted on {new Date(report.createdAt).toLocaleString()}
            </Typography>
          </Box>
        </Box>

        {/* Action Buttons Header */}
        <Box sx={{ display: 'flex', gap: 1.5, flexWrap: 'wrap' }}>
          {(report.status === 'SUBMITTED' || report.status === 'UNDER_REVIEW') && (
            <>
              <Button
                variant="contained"
                color="success"
                startIcon={<CheckCircle />}
                onClick={() => setVerifyDialogOpen(true)}
              >
                Verify Incident
              </Button>
              <Button
                variant="outlined"
                color="error"
                startIcon={<Cancel />}
                onClick={() => setRejectDialogOpen(true)}
              >
                Reject Report
              </Button>
            </>
          )}

          {report.status === 'VERIFIED' && (
            <Button
              variant="contained"
              color="primary"
              startIcon={<AssignmentTurnedIn />}
              onClick={() => setAssignDialogOpen(true)}
            >
              Assign Response Unit
            </Button>
          )}

          {['ASSIGNED', 'RESPONSE_IN_PROGRESS', 'SITUATION_CONTROLLED'].includes(report.status) && (
            <Button
              variant="contained"
              color="info"
              startIcon={<Update />}
              onClick={() => {
                setNextStatus(
                  report.status === 'ASSIGNED'
                    ? 'RESPONSE_IN_PROGRESS'
                    : report.status === 'RESPONSE_IN_PROGRESS'
                    ? 'SITUATION_CONTROLLED'
                    : 'RESOLVED'
                );
                setStatusDialogOpen(true);
              }}
            >
              Update Incident State
            </Button>
          )}
        </Box>
      </Box>

      {/* Alerts */}
      {error && <Alert severity="error">{error}</Alert>}
      {successMsg && <Alert severity="success">{successMsg}</Alert>}

      <Grid container spacing={3}>
        {/* Left Column: Core Report Details & Reporter Info */}
        <Grid item xs={12} md={7}>
          <Stack spacing={3}>
            {/* Summary Card */}
            <Card>
              <CardContent sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
                <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
                  <Box>
                    <Typography variant="h5" fontWeight={700} gutterBottom>
                      {report.title}
                    </Typography>
                    <Box sx={{ display: 'flex', gap: 1, flexWrap: 'wrap', alignItems: 'center' }}>
                      <Chip label={report.disasterTypeName} color="primary" variant="outlined" />
                      <SeverityBadge severity={report.severity} />
                      <StatusBadge status={report.status} />
                    </Box>
                  </Box>
                </Box>

                <Divider />

                <Box>
                  <Typography variant="subtitle2" color="text.secondary" gutterBottom>
                    INCIDENT DESCRIPTION
                  </Typography>
                  <Typography variant="body1" sx={{ whiteSpace: 'pre-wrap', lineHeight: 1.6 }}>
                    {report.description || 'No detailed narrative provided by reporter.'}
                  </Typography>
                </Box>

                {report.aftermath && (
                  <Box>
                    <Typography variant="subtitle2" color="text.secondary" gutterBottom>
                      IMPACT / AFTERMATH DETAILS
                    </Typography>
                    <Typography variant="body2" sx={{ lineHeight: 1.6 }}>
                      {report.aftermath}
                    </Typography>
                  </Box>
                )}

                {report.rejectionReason && (
                  <Alert severity="error" sx={{ mt: 1 }}>
                    <Typography variant="subtitle2" fontWeight={700}>Rejection Reason:</Typography>
                    {report.rejectionReason}
                  </Alert>
                )}
              </CardContent>
            </Card>
          </Stack>
        </Grid>

        {/* Right Column: Location, Reporter Info, Response Team assignment */}
        <Grid item xs={12} md={5}>
          <Stack spacing={3}>
            {/* Geo & Location Details */}
            <Card>
              <CardContent sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
                <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                  <Place color="primary" />
                  <Typography variant="h6" fontWeight={700}>
                    Geographic Coordinates
                  </Typography>
                </Box>
                <Divider />

                <Box>
                  <Typography variant="caption" color="text.secondary">GEO ADDRESS</Typography>
                  <Typography variant="body1" fontWeight={600}>
                    {report.address || 'Address lookup unavailable'}
                  </Typography>
                </Box>

                <Box sx={{ display: 'flex', gap: 3 }}>
                  <Box>
                    <Typography variant="caption" color="text.secondary">LATITUDE</Typography>
                    <Typography variant="body1" sx={{ fontFamily: 'monospace' }}>
                      {report.latitude.toFixed(6)}
                    </Typography>
                  </Box>
                  <Box>
                    <Typography variant="caption" color="text.secondary">LONGITUDE</Typography>
                    <Typography variant="body1" sx={{ fontFamily: 'monospace' }}>
                      {report.longitude.toFixed(6)}
                    </Typography>
                  </Box>
                </Box>

                <Button
                  variant="outlined"
                  size="small"
                  startIcon={<Place />}
                  href={`https://www.openstreetmap.org/?mlat=${report.latitude}&mlon=${report.longitude}#map=16/${report.latitude}/${report.longitude}`}
                  target="_blank"
                  rel="noopener noreferrer"
                >
                  Inspect on OpenStreetMap
                </Button>
              </CardContent>
            </Card>

            {/* Reporter Contact Info */}
            <Card>
              <CardContent sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
                <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                  <Person color="primary" />
                  <Typography variant="h6" fontWeight={700}>
                    Reporter Information
                  </Typography>
                </Box>
                <Divider />

                <Box>
                  <Typography variant="caption" color="text.secondary">FULL NAME</Typography>
                  <Typography variant="body1" fontWeight={600}>
                    {report.reporterName || 'Anonymous Submitter'}
                  </Typography>
                </Box>

                <Box>
                  <Typography variant="caption" color="text.secondary">PHONE NUMBER</Typography>
                  <Typography variant="body1">
                    {report.contactPhone || 'Not provided'}
                  </Typography>
                </Box>
              </CardContent>
            </Card>

            {/* Department Assignment Info */}
            <Card>
              <CardContent sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
                <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                  <Security color="primary" />
                  <Typography variant="h6" fontWeight={700}>
                    Assigned Response Unit
                  </Typography>
                </Box>
                <Divider />

                {report.assignedDepartmentName ? (
                  <Box sx={{ display: 'flex', flexDirection: 'column', gap: 1.5 }}>
                    <Box>
                      <Typography variant="caption" color="text.secondary">LEAD DEPARTMENT</Typography>
                      <Typography variant="body1" fontWeight={700} color="primary.main">
                        {report.assignedDepartmentName}
                      </Typography>
                    </Box>

                    {report.assignedTeamId && (
                      <Box>
                        <Typography variant="caption" color="text.secondary">DISPATCHED TEAM ID</Typography>
                        <Typography variant="body1" fontWeight={600}>
                          Team #{report.assignedTeamId}
                        </Typography>
                      </Box>
                    )}
                  </Box>
                ) : (
                  <Box sx={{ py: 2, textAlign: 'center' }}>
                    <Typography variant="body2" color="text.secondary" gutterBottom>
                      No agency or tactical response team has been assigned yet.
                    </Typography>
                    {report.status === 'VERIFIED' ? (
                      <Button
                        variant="contained"
                        size="small"
                        sx={{ mt: 1 }}
                        onClick={() => setAssignDialogOpen(true)}
                      >
                        Assign Unit Now
                      </Button>
                    ) : (
                      <Chip
                        label="Verification Required Before Assignment"
                        size="small"
                        color="warning"
                        variant="outlined"
                      />
                    )}
                  </Box>
                )}
              </CardContent>
            </Card>
          </Stack>
        </Grid>
      </Grid>

      {/* --- VERIFY DIALOG --- */}
      <Dialog open={verifyDialogOpen} onClose={() => setVerifyDialogOpen(false)} maxWidth="sm" fullWidth>
        <DialogTitle sx={{ fontWeight: 700 }}>
          Verify Emergency Incident Report
        </DialogTitle>
        <DialogContent dividers sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
          <Typography variant="body2">
            Verifying this incident confirms that it is authentic, makes it visible to public monitoring maps, and authorizes response dispatch.
          </Typography>
          <TextField
            label="Verification Notes & Dispatch Rationale (Optional)"
            multiline
            rows={3}
            fullWidth
            value={verifyNotes}
            onChange={(e) => setVerifyNotes(e.target.value)}
            placeholder="e.g., Confirmed with municipal fire control room. Dispatching immediate flood containment unit."
          />
        </DialogContent>
        <DialogActions sx={{ p: 2 }}>
          <Button onClick={() => setVerifyDialogOpen(false)} disabled={actionLoading}>
            Cancel
          </Button>
          <Button
            variant="contained"
            color="success"
            onClick={handleVerify}
            disabled={actionLoading}
          >
            {actionLoading ? 'Verifying...' : 'Confirm Verification'}
          </Button>
        </DialogActions>
      </Dialog>

      {/* --- REJECT DIALOG --- */}
      <Dialog open={rejectDialogOpen} onClose={() => setRejectDialogOpen(false)} maxWidth="sm" fullWidth>
        <DialogTitle sx={{ fontWeight: 700, color: 'error.main' }}>
          Reject Incident Report
        </DialogTitle>
        <DialogContent dividers sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
          <Alert severity="warning">
            A formal rejection reason is <strong>mandatory</strong> and will be logged in the public safety audit trail.
          </Alert>
          {rejectError && <Alert severity="error">{rejectError}</Alert>}
          <TextField
            label="Mandatory Rejection Reason"
            required
            multiline
            rows={3}
            fullWidth
            value={rejectReason}
            onChange={(e) => {
              setRejectReason(e.target.value);
              setRejectError(null);
            }}
            placeholder="e.g., Inaccurate GPS location, duplicate incident entry, or test submission."
          />
        </DialogContent>
        <DialogActions sx={{ p: 2 }}>
          <Button onClick={() => setRejectDialogOpen(false)} disabled={actionLoading}>
            Cancel
          </Button>
          <Button
            variant="contained"
            color="error"
            onClick={handleReject}
            disabled={actionLoading}
          >
            {actionLoading ? 'Rejecting...' : 'Reject Incident'}
          </Button>
        </DialogActions>
      </Dialog>

      {/* --- ASSIGN DEPARTMENT DIALOG --- */}
      <Dialog open={assignDialogOpen} onClose={() => setAssignDialogOpen(false)} maxWidth="sm" fullWidth>
        <DialogTitle sx={{ fontWeight: 700 }}>
          Assign Emergency Response Department
        </DialogTitle>
        <DialogContent dividers sx={{ display: 'flex', flexDirection: 'column', gap: 2.5 }}>
          <Typography variant="body2" color="text.secondary">
            Dispatch an official government agency and operational response team to resolve this incident.
          </Typography>

          <TextField
            select
            label="Government Department"
            required
            fullWidth
            value={selectedDeptId}
            onChange={(e) => {
              setSelectedDeptId(Number(e.target.value));
              setSelectedTeamId('');
            }}
          >
            {departments.map((dept) => (
              <MenuItem key={dept.id} value={dept.id}>
                {dept.name} ({dept.departmentType})
              </MenuItem>
            ))}
          </TextField>

          <TextField
            select
            label="Response Team (Optional)"
            fullWidth
            value={selectedTeamId}
            onChange={(e) => setSelectedTeamId(Number(e.target.value))}
            disabled={!selectedDeptId || availableTeams.length === 0}
            helperText={
              !selectedDeptId
                ? 'Select a department first'
                : availableTeams.length === 0
                ? 'No teams registered under this department'
                : ''
            }
          >
            <MenuItem value="">Unassigned (Direct to Dept Commander)</MenuItem>
            {availableTeams.map((team) => (
              <MenuItem key={team.id} value={team.id}>
                {team.teamName} ({team.status}) - {team.teamLead || 'Commander'}
              </MenuItem>
            ))}
          </TextField>

          <TextField
            select
            label="Dispatch Priority"
            required
            fullWidth
            value={assignPriority}
            onChange={(e) => setAssignPriority(e.target.value as SeverityLevel)}
          >
            <MenuItem value="LOW">LOW</MenuItem>
            <MenuItem value="MEDIUM">MEDIUM</MenuItem>
            <MenuItem value="HIGH">HIGH</MenuItem>
            <MenuItem value="CRITICAL">CRITICAL (Immediate Alarm)</MenuItem>
          </TextField>

          <TextField
            label="Special Operational Instructions"
            multiline
            rows={3}
            fullWidth
            value={assignInstructions}
            onChange={(e) => setAssignInstructions(e.target.value)}
            placeholder="e.g., Deploy heavy rescue watercraft; coordinate directly with local community elders on site."
          />
        </DialogContent>
        <DialogActions sx={{ p: 2 }}>
          <Button onClick={() => setAssignDialogOpen(false)} disabled={actionLoading}>
            Cancel
          </Button>
          <Button
            variant="contained"
            color="primary"
            onClick={handleAssign}
            disabled={actionLoading || !selectedDeptId}
          >
            {actionLoading ? 'Assigning...' : 'Dispatch Response Unit'}
          </Button>
        </DialogActions>
      </Dialog>

      {/* --- STATUS UPDATE DIALOG --- */}
      <Dialog open={statusDialogOpen} onClose={() => setStatusDialogOpen(false)} maxWidth="sm" fullWidth>
        <DialogTitle sx={{ fontWeight: 700 }}>
          Advance Incident Operational Status
        </DialogTitle>
        <DialogContent dividers sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
          <TextField
            select
            label="Target Status"
            fullWidth
            value={nextStatus}
            onChange={(e) => setNextStatus(e.target.value)}
          >
            <MenuItem value="RESPONSE_IN_PROGRESS">RESPONSE IN PROGRESS</MenuItem>
            <MenuItem value="SITUATION_CONTROLLED">SITUATION CONTROLLED</MenuItem>
            <MenuItem value="RESOLVED">RESOLVED</MenuItem>
          </TextField>

          <TextField
            label="Operational Update Notes"
            multiline
            rows={3}
            fullWidth
            value={statusNotes}
            onChange={(e) => setStatusNotes(e.target.value)}
            placeholder="Details on containment, civilian evacuations, or medical care provided..."
          />
        </DialogContent>
        <DialogActions sx={{ p: 2 }}>
          <Button onClick={() => setStatusDialogOpen(false)} disabled={actionLoading}>
            Cancel
          </Button>
          <Button
            variant="contained"
            color="primary"
            onClick={handleUpdateStatus}
            disabled={actionLoading || !nextStatus}
          >
            {actionLoading ? 'Updating...' : 'Update Status'}
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};
export default OfficerReviewPage;
