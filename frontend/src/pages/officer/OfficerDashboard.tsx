import React, { useEffect, useState } from 'react';
import {
  Box,
  Grid,
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
  Tooltip
} from '@mui/material';
import {
  AssignmentLate,
  CheckCircle,
  PendingActions,
  LocalShipping,
  DoneAll,
  Visibility,
  Refresh,
  FilterList
} from '@mui/icons-material';
import { useNavigate } from 'react-router-dom';
import { MetricCard } from '../../components/common/MetricCard';
import { StatusBadge, SeverityBadge } from '../../components/common/Badges';
import { EmptyState } from '../../components/common/EmptyState';
import { dashboardService } from '../../services/adminService';
import { OfficerDashboardData, DisasterReport } from '../../types';

export const OfficerDashboard: React.FC = () => {
  const navigate = useNavigate();
  const [dashboardData, setDashboardData] = useState<OfficerDashboardData | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [statusFilter, setStatusFilter] = useState<string>('ALL');

  const fetchDashboard = async () => {
    try {
      setLoading(true);
      setError(null);
      const data = await dashboardService.getOfficerDashboard();
      setDashboardData(data);
    } catch (err: any) {
      setError(err?.response?.data?.message || 'Failed to load officer dashboard data');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchDashboard();
  }, []);

  const reports: DisasterReport[] = [
    ...(dashboardData?.pendingQueue || []),
    ...(dashboardData?.activeResponsesQueue || [])
  ];

  const filteredReports = statusFilter === 'ALL'
    ? reports
    : reports.filter(r => r.status === statusFilter);

  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '60vh' }}>
        <CircularProgress size={48} />
      </Box>
    );
  }

  return (
    <Box sx={{ display: 'flex', flexDirection: 'column', gap: 3 }}>
      {/* Header */}
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: 2 }}>
        <Box>
          <Typography variant="h4" fontWeight={700}>
            Officer Operations Dashboard
          </Typography>
          <Typography variant="body2" color="text.secondary">
            Incident triage queue, emergency verification, and inter-agency response dispatch
          </Typography>
        </Box>
        <Box sx={{ display: 'flex', gap: 1.5 }}>
          <Button
            variant="outlined"
            startIcon={<Refresh />}
            onClick={fetchDashboard}
          >
            Refresh Data
          </Button>
          <Button
            variant="contained"
            color="primary"
            onClick={() => navigate('/officer/queue')}
          >
            Open Full Triage Queue
          </Button>
        </Box>
      </Box>

      {error && <Alert severity="error">{error}</Alert>}

      {/* KPI Cards */}
      <Grid container spacing={2.5}>
        <Grid item xs={12} sm={6} md={2.4}>
          <MetricCard
            title="Pending Verification"
            value={dashboardData?.pendingReports ?? 0}
            subtitle="Citizen reports awaiting review"
            icon={<PendingActions fontSize="large" />}
            color="#f59e0b"
          />
        </Grid>
        <Grid item xs={12} sm={6} md={2.4}>
          <MetricCard
            title="Under Review"
            value={dashboardData?.underReviewReports ?? 0}
            subtitle="Currently undergoing audit"
            icon={<AssignmentLate fontSize="large" />}
            color="#3b82f6"
          />
        </Grid>
        <Grid item xs={12} sm={6} md={2.4}>
          <MetricCard
            title="Verified Disasters"
            value={dashboardData?.verifiedIncidents ?? 0}
            subtitle="Awaiting team dispatch"
            icon={<CheckCircle fontSize="large" />}
            color="#10b981"
          />
        </Grid>
        <Grid item xs={12} sm={6} md={2.4}>
          <MetricCard
            title="Active Responses"
            value={dashboardData?.activeResponses ?? 0}
            subtitle="Ground units deployed"
            icon={<LocalShipping fontSize="large" />}
            color="#8b5cf6"
          />
        </Grid>
        <Grid item xs={12} sm={6} md={2.4}>
          <MetricCard
            title="Resolved Incidents"
            value={dashboardData?.resolvedIncidents ?? 0}
            subtitle="Successfully contained"
            icon={<DoneAll fontSize="large" />}
            color="#059669"
          />
        </Grid>
      </Grid>

      {/* Triage Action Banner */}
      {(dashboardData?.pendingReports ?? 0) > 0 && (
        <Card sx={{ bgcolor: 'rgba(245, 158, 11, 0.08)', border: '1px solid rgba(245, 158, 11, 0.3)' }}>
          <CardContent sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', py: 2 }}>
            <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
              <PendingActions sx={{ color: '#f59e0b', fontSize: 32 }} />
              <Box>
                <Typography variant="subtitle1" fontWeight={700} color="#b45309">
                  Attention Required: {dashboardData?.pendingReports} Unverified Incident Reports
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  Prompt verification is required before response units can be officially assigned and dispatched.
                </Typography>
              </Box>
            </Box>
            <Button
              variant="contained"
              sx={{ bgcolor: '#f59e0b', '&:hover': { bgcolor: '#d97706' } }}
              onClick={() => navigate('/officer/queue?status=SUBMITTED')}
            >
              Triage Now
            </Button>
          </CardContent>
        </Card>
      )}

      {/* Incident Review Queue Table */}
      <Card>
        <CardContent sx={{ p: 0 }}>
          <Box sx={{ p: 2.5, display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: 2, borderBottom: '1px solid', borderColor: 'divider' }}>
            <Box>
              <Typography variant="h6" fontWeight={700}>
                Incident Triage & Monitoring Stream
              </Typography>
              <Typography variant="body2" color="text.secondary">
                Select an incident to audit photos/videos, verify coordinates, or dispatch emergency response teams.
              </Typography>
            </Box>
            
            {/* Filter buttons */}
            <Box sx={{ display: 'flex', gap: 1, flexWrap: 'wrap', alignItems: 'center' }}>
              <FilterList fontSize="small" sx={{ color: 'text.secondary' }} />
              {(['ALL', 'SUBMITTED', 'UNDER_REVIEW', 'VERIFIED', 'ASSIGNED', 'RESPONSE_IN_PROGRESS', 'RESOLVED'] as const).map((st) => (
                <Chip
                  key={st}
                  label={st.replace(/_/g, ' ')}
                  size="small"
                  variant={statusFilter === st ? 'filled' : 'outlined'}
                  color={statusFilter === st ? 'primary' : 'default'}
                  onClick={() => setStatusFilter(st)}
                  sx={{ cursor: 'pointer', fontSize: '0.75rem' }}
                />
              ))}
            </Box>
          </Box>

          {filteredReports.length === 0 ? (
            <Box sx={{ py: 6 }}>
              <EmptyState
                title="No Reports Found"
                description={`There are currently no disaster reports matching status '${statusFilter}'.`}
              />
            </Box>
          ) : (
            <TableContainer component={Paper} elevation={0}>
              <Table sx={{ minWidth: 750 }}>
                <TableHead>
                  <TableRow sx={{ bgcolor: 'action.hover' }}>
                    <TableCell sx={{ fontWeight: 700 }}>Report ID</TableCell>
                    <TableCell sx={{ fontWeight: 700 }}>Disaster Type</TableCell>
                    <TableCell sx={{ fontWeight: 700 }}>Title / Headline</TableCell>
                    <TableCell sx={{ fontWeight: 700 }}>Location</TableCell>
                    <TableCell sx={{ fontWeight: 700 }}>Severity</TableCell>
                    <TableCell sx={{ fontWeight: 700 }}>Status</TableCell>
                    <TableCell sx={{ fontWeight: 700 }}>Submitted At</TableCell>
                    <TableCell align="right" sx={{ fontWeight: 700 }}>Action</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {filteredReports.map((report) => (
                    <TableRow
                      key={report.id}
                      hover
                      sx={{ '&:last-child td, &:last-child th': { border: 0 }, cursor: 'pointer' }}
                      onClick={() => navigate(`/officer/review/${report.id}`)}
                    >
                      <TableCell sx={{ fontFamily: 'monospace', fontWeight: 700, color: 'primary.main' }}>
                        {report.reportCode}
                      </TableCell>
                      <TableCell>
                        <Chip label={report.disasterTypeName} size="small" variant="outlined" />
                      </TableCell>
                      <TableCell sx={{ maxWidth: 220, overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>
                        <Typography variant="body2" fontWeight={600} noWrap>
                          {report.title}
                        </Typography>
                        <Typography variant="caption" color="text.secondary">
                          by {report.reporterName || 'Anonymous / Citizen'}
                        </Typography>
                      </TableCell>
                      <TableCell sx={{ maxWidth: 180, overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>
                        {report.address || `${report.latitude.toFixed(4)}, ${report.longitude.toFixed(4)}`}
                      </TableCell>
                      <TableCell>
                        <SeverityBadge severity={report.severity} />
                      </TableCell>
                      <TableCell>
                        <StatusBadge status={report.status} />
                      </TableCell>
                      <TableCell sx={{ whiteSpace: 'nowrap', fontSize: '0.8125rem' }}>
                        {new Date(report.createdAt).toLocaleString()}
                      </TableCell>
                      <TableCell align="right">
                        <Tooltip title="Open Review & Assignment Console">
                          <IconButton
                            color="primary"
                            size="small"
                            onClick={(e) => {
                              e.stopPropagation();
                              navigate(`/officer/review/${report.id}`);
                            }}
                          >
                            <Visibility fontSize="small" />
                          </IconButton>
                        </Tooltip>
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </TableContainer>
          )}
        </CardContent>
      </Card>
    </Box>
  );
};
export default OfficerDashboard;
