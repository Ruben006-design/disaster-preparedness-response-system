import React, { useState, useEffect } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import {
  Box,
  Typography,
  Grid,
  Card,
  CardContent,
  Button,
  LinearProgress,
  Chip,
  Paper,
  Divider,
  List,
  ListItem,
  ListItemText,
  Alert,
} from '@mui/material';
import SchoolIcon from '@mui/icons-material/School';
import QuizIcon from '@mui/icons-material/Quiz';
import SportsEsportsIcon from '@mui/icons-material/SportsEsports';
import AddAlertIcon from '@mui/icons-material/AddAlert';
import MapIcon from '@mui/icons-material/Map';
import VerifiedIcon from '@mui/icons-material/Verified';
import ArrowForwardIcon from '@mui/icons-material/ArrowForward';
import ShieldOutlinedIcon from '@mui/icons-material/ShieldOutlined';

import { useAuth } from '../../context/AuthContext';
import { dashboardService } from '../../services/adminService';
import { StudentDashboardData } from '../../types';
import { MetricCard } from '../../components/common/MetricCard';
import { LoadingScreen } from '../../components/common/LoadingScreen';
import { StatusBadge, SeverityBadge } from '../../components/common/Badges';

export const StudentDashboard: React.FC = () => {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [data, setData] = useState<StudentDashboardData | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchDashboard = async () => {
      try {
        const res = await dashboardService.getStudentDashboard();
        setData(res);
      } catch (err: any) {
        setError(err.response?.data?.message || 'Unable to load student dashboard data.');
      } finally {
        setLoading(false);
      }
    };

    fetchDashboard();
  }, []);

  if (loading) return <LoadingScreen message="Loading personalized student dashboard..." />;
  if (error || !data) return <Alert severity="error">{error || 'Failed to load dashboard data.'}</Alert>;

  const prep = data.preparednessScore;

  return (
    <Box>
      {/* Hero Welcome Banner */}
      <Paper
        sx={{
          p: { xs: 3, md: 4 },
          mb: 4,
          borderRadius: 3,
          background: 'linear-gradient(135deg, #0f172a 0%, #1e3a8a 100%)',
          color: '#fff',
        }}
      >
        <Grid container spacing={3} alignItems="center">
          <Grid item xs={12} md={8}>
            <Chip
              label="STUDENT READINESS HUB"
              size="small"
              sx={{ bgcolor: 'rgba(56, 189, 248, 0.2)', color: '#38bdf8', fontWeight: 700, mb: 1.5 }}
            />
            <Typography variant="h4" sx={{ fontWeight: 800, mb: 1 }}>
              Welcome back, {user?.name || 'Student'}!
            </Typography>
            <Typography variant="body1" sx={{ color: '#94a3b8', fontSize: '1.05rem', maxWidth: 600 }}>
              "Be Prepared. Stay Safe. Know What To Do." Master critical survival procedures and report active local emergencies directly to state responders.
            </Typography>
          </Grid>

          <Grid item xs={12} md={4}>
            {/* Quick Action Floating Cluster */}
            <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 1.5, justifyContent: { xs: 'flex-start', md: 'flex-end' } }}>
              <Button
                component={Link}
                to="/report-disaster"
                variant="contained"
                color="error"
                startIcon={<AddAlertIcon />}
                sx={{ fontWeight: 700, px: 2.5 }}
              >
                Report Emergency
              </Button>
              <Button
                component={Link}
                to="/map"
                variant="outlined"
                sx={{ color: '#fff', borderColor: 'rgba(255,255,255,0.4)', fontWeight: 600 }}
                startIcon={<MapIcon />}
              >
                Disaster Map
              </Button>
            </Box>
          </Grid>
        </Grid>
      </Paper>

      {/* KPI Overview Metrics */}
      <Grid container spacing={3} sx={{ mb: 4 }}>
        <Grid item xs={12} sm={6} md={3}>
          <MetricCard
            title="Readiness Score"
            value={`${prep.overallScore}%`}
            icon={<ShieldOutlinedIcon />}
            color="#0284c7"
            subtext={`Status: ${prep.preparednessLevel}`}
          />
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <MetricCard
            title="Modules Finished"
            value={`${data.modulesCompleted} / ${data.totalModules}`}
            icon={<SchoolIcon />}
            color="#10b981"
            subtext={`${prep.moduleCompletionScore}% module completion`}
          />
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <MetricCard
            title="Quiz Average"
            value={`${data.quizAverage}%`}
            icon={<QuizIcon />}
            color="#8b5cf6"
            subtext="Assessment evaluations"
          />
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <MetricCard
            title="Simulations Complete"
            value={`${data.simulationsCompleted} / ${data.totalSimulations}`}
            icon={<SportsEsportsIcon />}
            color="#f59e0b"
            subtext="Scenario drills taken"
          />
        </Grid>
      </Grid>

      {/* Preparedness Breakdown Widget */}
      <Card sx={{ mb: 4 }}>
        <CardContent sx={{ p: 3 }}>
          <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', mb: 2, flexWrap: 'wrap' }}>
            <Box>
              <Typography variant="h6" sx={{ fontWeight: 700 }}>
                Transparent Preparedness Assessment Index
              </Typography>
              <Typography variant="caption" color="text.secondary">
                Formula: (30% Module Progress) + (40% Quiz Evaluations) + (30% Scenario Simulations)
              </Typography>
            </Box>
            <Chip
              label={prep.preparednessLevel}
              color={prep.overallScore >= 70 ? 'success' : prep.overallScore >= 40 ? 'warning' : 'default'}
              sx={{ fontWeight: 700 }}
            />
          </Box>

          <Box sx={{ mb: 3 }}>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 0.5 }}>
              <Typography variant="body2" sx={{ fontWeight: 600 }}>
                Overall Readiness
              </Typography>
              <Typography variant="body2" sx={{ fontWeight: 700 }}>
                {prep.overallScore} / 100
              </Typography>
            </Box>
            <LinearProgress
              variant="determinate"
              value={prep.overallScore}
              sx={{ height: 10, borderRadius: 5, bgcolor: '#e2e8f0', '& .MuiLinearProgress-bar': { bgcolor: '#0284c7' } }}
            />
          </Box>

          <Grid container spacing={2}>
            <Grid item xs={12} md={4}>
              <Paper variant="outlined" sx={{ p: 2, bgcolor: '#f8fafc' }}>
                <Typography variant="caption" color="text.secondary" sx={{ fontWeight: 600 }}>
                  MODULE COMPLETION (30% WEIGHT)
                </Typography>
                <Typography variant="h6" sx={{ fontWeight: 800 }}>
                  {prep.moduleCompletionScore}%
                </Typography>
                <LinearProgress variant="determinate" value={prep.moduleCompletionScore} color="primary" sx={{ mt: 1, height: 6, borderRadius: 3 }} />
              </Paper>
            </Grid>
            <Grid item xs={12} md={4}>
              <Paper variant="outlined" sx={{ p: 2, bgcolor: '#f8fafc' }}>
                <Typography variant="caption" color="text.secondary" sx={{ fontWeight: 600 }}>
                  QUIZ PERFORMANCE (40% WEIGHT)
                </Typography>
                <Typography variant="h6" sx={{ fontWeight: 800 }}>
                  {prep.quizPerformanceScore}%
                </Typography>
                <LinearProgress variant="determinate" value={prep.quizPerformanceScore} color="secondary" sx={{ mt: 1, height: 6, borderRadius: 3 }} />
              </Paper>
            </Grid>
            <Grid item xs={12} md={4}>
              <Paper variant="outlined" sx={{ p: 2, bgcolor: '#f8fafc' }}>
                <Typography variant="caption" color="text.secondary" sx={{ fontWeight: 600 }}>
                  SIMULATION DRILLS (30% WEIGHT)
                </Typography>
                <Typography variant="h6" sx={{ fontWeight: 800 }}>
                  {prep.simulationPerformanceScore}%
                </Typography>
                <LinearProgress variant="determinate" value={prep.simulationPerformanceScore} color="warning" sx={{ mt: 1, height: 6, borderRadius: 3 }} />
              </Paper>
            </Grid>
          </Grid>

          <Typography variant="caption" sx={{ color: 'text.secondary', display: 'block', mt: 2, fontStyle: 'italic' }}>
            *{prep.transparencyDisclaimer}
          </Typography>
        </CardContent>
      </Card>

      {/* Main Two Columns: Active Incidents & My Reports */}
      <Grid container spacing={3}>
        {/* Verified Active Disasters Feed */}
        <Grid item xs={12} md={6}>
          <Card sx={{ height: '100%' }}>
            <CardContent sx={{ p: 3 }}>
              <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', mb: 2 }}>
                <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                  <VerifiedIcon color="secondary" />
                  <Typography variant="h6" sx={{ fontWeight: 700 }}>
                    Official Verified Disasters
                  </Typography>
                </Box>
                <Button component={Link} to="/map" size="small" endIcon={<ArrowForwardIcon />}>
                  View Map
                </Button>
              </Box>
              <Typography variant="caption" color="text.secondary" sx={{ display: 'block', mb: 2 }}>
                Confirmed and audited by Government Emergency Management Officers
              </Typography>
              <Divider sx={{ mb: 2 }} />

              {data.activeVerifiedDisasters.length === 0 ? (
                <Typography variant="body2" color="text.secondary" sx={{ py: 3, textAlign: 'center' }}>
                  No active verified emergency alerts currently active.
                </Typography>
              ) : (
                <List disablePadding>
                  {data.activeVerifiedDisasters.slice(0, 4).map((inc) => (
                    <ListItem
                      key={inc.id}
                      component={Link}
                      to={`/reports/${inc.id}`}
                      sx={{
                        px: 1.5,
                        py: 1.5,
                        borderRadius: 2,
                        mb: 1,
                        bgcolor: '#f8fafc',
                        textDecoration: 'none',
                        color: 'inherit',
                        '&:hover': { bgcolor: '#f1f5f9' },
                      }}
                    >
                      <ListItemText
                        primary={
                          <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 0.5 }}>
                            <SeverityBadge severity={inc.severity} size="small" />
                            <Typography variant="subtitle2" noWrap sx={{ fontWeight: 700 }}>
                              {inc.title}
                            </Typography>
                          </Box>
                        }
                        secondary={
                          <Typography variant="caption" color="text.secondary">
                            {inc.disasterTypeName} • {inc.address} • {new Date(inc.createdAt).toLocaleDateString()}
                          </Typography>
                        }
                      />
                      <StatusBadge status={inc.status} />
                    </ListItem>
                  ))}
                </List>
              )}
            </CardContent>
          </Card>
        </Grid>

        {/* My Recent Reports */}
        <Grid item xs={12} md={6}>
          <Card sx={{ height: '100%' }}>
            <CardContent sx={{ p: 3 }}>
              <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', mb: 2 }}>
                <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                  <AddAlertIcon color="primary" />
                  <Typography variant="h6" sx={{ fontWeight: 700 }}>
                    My Submitted Reports
                  </Typography>
                </Box>
                <Button component={Link} to="/student/reports" size="small" endIcon={<ArrowForwardIcon />}>
                  All Reports
                </Button>
              </Box>
              <Typography variant="caption" color="text.secondary" sx={{ display: 'block', mb: 2 }}>
                Real-time review and response status of your field dispatches
              </Typography>
              <Divider sx={{ mb: 2 }} />

              {data.recentReports.length === 0 ? (
                <Box sx={{ textAlign: 'center', py: 3 }}>
                  <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
                    You have not submitted any disaster reports yet.
                  </Typography>
                  <Button
                    component={Link}
                    to="/report-disaster"
                    variant="outlined"
                    color="primary"
                    size="small"
                    startIcon={<AddAlertIcon />}
                  >
                    Report Nearby Incident
                  </Button>
                </Box>
              ) : (
                <List disablePadding>
                  {data.recentReports.map((rep) => (
                    <ListItem
                      key={rep.id}
                      component={Link}
                      to={`/reports/${rep.id}`}
                      sx={{
                        px: 1.5,
                        py: 1.5,
                        borderRadius: 2,
                        mb: 1,
                        bgcolor: '#f8fafc',
                        textDecoration: 'none',
                        color: 'inherit',
                        '&:hover': { bgcolor: '#f1f5f9' },
                      }}
                    >
                      <ListItemText
                        primary={
                          <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 0.5 }}>
                            <Chip label={rep.reportCode} size="small" sx={{ fontWeight: 700, fontSize: '0.72rem' }} />
                            <Typography variant="subtitle2" noWrap sx={{ fontWeight: 700 }}>
                              {rep.title}
                            </Typography>
                          </Box>
                        }
                        secondary={
                          <Typography variant="caption" color="text.secondary">
                            Submitted: {new Date(rep.createdAt).toLocaleDateString()} • {rep.address}
                          </Typography>
                        }
                      />
                      <StatusBadge status={rep.status} />
                    </ListItem>
                  ))}
                </List>
              )}
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    </Box>
  );
};
