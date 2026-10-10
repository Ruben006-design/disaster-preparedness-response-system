import React, { useEffect, useState } from 'react';
import {
  Box,
  Grid,
  Typography,
  Card,
  CardContent,
  Button,
  CircularProgress,
  Alert,
  Paper,
  Divider
} from '@mui/material';
import {
  People,
  Business,
  ReportProblem,
  CheckCircle,
  MenuBook,
  Quiz,
  SportsEsports,
  TrendingUp,
  Refresh,
  ArrowForward
} from '@mui/icons-material';
import { useNavigate } from 'react-router-dom';
import { MetricCard } from '../../components/common/MetricCard';
import { dashboardService } from '../../services/adminService';
import { AdminDashboardData } from '../../types';

export const AdminDashboard: React.FC = () => {
  const navigate = useNavigate();
  const [data, setData] = useState<AdminDashboardData | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  const fetchStats = async () => {
    try {
      setLoading(true);
      setError(null);
      const res = await dashboardService.getAdminDashboard();
      setData(res);
    } catch (err: any) {
      setError(err?.response?.data?.message || 'Failed to load administrator telemetry');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchStats();
  }, []);

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
            System Administration
          </Typography>
          <Typography variant="body2" color="text.secondary">
            Platform governance, institutional user access, curriculum management, and agency telemetry
          </Typography>
        </Box>
        <Box sx={{ display: 'flex', gap: 1.5 }}>
          <Button variant="outlined" startIcon={<Refresh />} onClick={fetchStats}>
            Refresh Telemetry
          </Button>
        </Box>
      </Box>

      {error && <Alert severity="error">{error}</Alert>}

      {/* KPI Cards */}
      <Grid container spacing={2.5}>
        <Grid item xs={12} sm={6} md={3}>
          <MetricCard
            title="Total Registered Users"
            value={data?.totalUsers ?? 0}
            subtitle={`${data?.totalStudents ?? 0} Students, ${data?.totalOfficers ?? 0} Officers`}
            icon={<People fontSize="large" />}
            color="#2563eb"
          />
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <MetricCard
            title="Incident Reports"
            value={data?.totalReports ?? 0}
            subtitle={`${data?.verifiedIncidents ?? 0} Verified across sectors`}
            icon={<ReportProblem fontSize="large" />}
            color="#f97316"
          />
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <MetricCard
            title="Verified Incidents"
            value={data?.verifiedIncidents ?? 0}
            subtitle="Confirmed emergencies"
            icon={<TrendingUp fontSize="large" />}
            color="#e11d48"
          />
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <MetricCard
            title="Resolved Emergencies"
            value={data?.resolvedIncidents ?? 0}
            subtitle="Disaster containment achieved"
            icon={<CheckCircle fontSize="large" />}
            color="#10b981"
          />
        </Grid>
      </Grid>

      {/* Administrative Operations Hub */}
      <Typography variant="h6" fontWeight={700} sx={{ mt: 1 }}>
        Administrative Governance Hub
      </Typography>

      <Grid container spacing={3}>
        {/* User Governance Card */}
        <Grid item xs={12} md={4}>
          <Card sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
            <CardContent sx={{ flexGrow: 1, display: 'flex', flexDirection: 'column', gap: 2 }}>
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 1.5 }}>
                <Paper sx={{ p: 1, bgcolor: 'primary.light', color: 'primary.contrastText', borderRadius: 2 }}>
                  <People />
                </Paper>
                <Box>
                  <Typography variant="h6" fontWeight={700}>
                    User Directory
                  </Typography>
                  <Typography variant="caption" color="text.secondary">
                    Role management & identity audits
                  </Typography>
                </Box>
              </Box>
              <Typography variant="body2" color="text.secondary">
                Audit system accounts, toggle user activity states, review student enrollment, and verify officer agency credentials.
              </Typography>
              <Divider sx={{ my: 'auto' }} />
              <Button
                variant="outlined"
                endIcon={<ArrowForward />}
                onClick={() => navigate('/admin/users')}
              >
                Manage All Users
              </Button>
            </CardContent>
          </Card>
        </Grid>

        {/* Agency & Response Teams */}
        <Grid item xs={12} md={4}>
          <Card sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
            <CardContent sx={{ flexGrow: 1, display: 'flex', flexDirection: 'column', gap: 2 }}>
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 1.5 }}>
                <Paper sx={{ p: 1, bgcolor: '#8b5cf6', color: '#fff', borderRadius: 2 }}>
                  <Business />
                </Paper>
                <Box>
                  <Typography variant="h6" fontWeight={700}>
                    Agency & Fleet Roster
                  </Typography>
                  <Typography variant="caption" color="text.secondary">
                    Departments & rapid response teams
                  </Typography>
                </Box>
              </Box>
              <Typography variant="body2" color="text.secondary">
                Configure fire rescue, national disaster response, medical EMS, and municipal engineering departments and operational units.
              </Typography>
              <Divider sx={{ my: 'auto' }} />
              <Button
                variant="outlined"
                endIcon={<ArrowForward />}
                onClick={() => navigate('/admin/departments')}
              >
                Manage Departments & Teams
              </Button>
            </CardContent>
          </Card>
        </Grid>

        {/* Emergency Hotlines */}
        <Grid item xs={12} md={4}>
          <Card sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
            <CardContent sx={{ flexGrow: 1, display: 'flex', flexDirection: 'column', gap: 2 }}>
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 1.5 }}>
                <Paper sx={{ p: 1, bgcolor: '#f59e0b', color: '#fff', borderRadius: 2 }}>
                  <ReportProblem />
                </Paper>
                <Box>
                  <Typography variant="h6" fontWeight={700}>
                    Public Safety Hotlines
                  </Typography>
                  <Typography variant="caption" color="text.secondary">
                    National & local emergency contacts
                  </Typography>
                </Box>
              </Box>
              <Typography variant="body2" color="text.secondary">
                Review, verify, and publish national emergency contacts (Police, Ambulance, Fire, Disaster Management Helpline).
              </Typography>
              <Divider sx={{ my: 'auto' }} />
              <Button
                variant="outlined"
                endIcon={<ArrowForward />}
                onClick={() => navigate('/emergency-contacts')}
              >
                View Emergency Contacts
              </Button>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      {/* Curriculum & Education Metrics */}
      <Card sx={{ mt: 1 }}>
        <CardContent>
          <Typography variant="h6" fontWeight={700} gutterBottom>
            Educational Curriculum Telemetry
          </Typography>
          <Grid container spacing={2} sx={{ mt: 1 }}>
            <Grid item xs={12} sm={4}>
              <Paper variant="outlined" sx={{ p: 2, display: 'flex', alignItems: 'center', gap: 2 }}>
                <MenuBook color="primary" sx={{ fontSize: 36 }} />
                <Box>
                  <Typography variant="h5" fontWeight={700}>
                    {data?.totalModules ?? 4}
                  </Typography>
                  <Typography variant="body2" color="text.secondary">
                    Active Educational Modules
                  </Typography>
                </Box>
              </Paper>
            </Grid>
            <Grid item xs={12} sm={4}>
              <Paper variant="outlined" sx={{ p: 2, display: 'flex', alignItems: 'center', gap: 2 }}>
                <Quiz color="success" sx={{ fontSize: 36 }} />
                <Box>
                  <Typography variant="h5" fontWeight={700}>
                    {data?.totalQuizzes ?? 4}
                  </Typography>
                  <Typography variant="body2" color="text.secondary">
                    Assessment Examination Batteries
                  </Typography>
                </Box>
              </Paper>
            </Grid>
            <Grid item xs={12} sm={4}>
              <Paper variant="outlined" sx={{ p: 2, display: 'flex', alignItems: 'center', gap: 2 }}>
                <SportsEsports color="secondary" sx={{ fontSize: 36 }} />
                <Box>
                  <Typography variant="h5" fontWeight={700}>
                    {data?.totalSimulations ?? 4}
                  </Typography>
                  <Typography variant="body2" color="text.secondary">
                    Interactive Drill Scenarios
                  </Typography>
                </Box>
              </Paper>
            </Grid>
          </Grid>
        </CardContent>
      </Card>
    </Box>
  );
};
export default AdminDashboard;
