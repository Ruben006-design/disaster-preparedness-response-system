import React from 'react';
import { useNavigate, Link } from 'react-router-dom';
import {
  Box,
  Container,
  Typography,
  Button,
  Grid,
  Card,
  CardContent,
  Avatar,
  Chip,
  Paper,
  Divider,
} from '@mui/material';
import SecurityIcon from '@mui/icons-material/Security';
import SchoolIcon from '@mui/icons-material/School';
import QuizIcon from '@mui/icons-material/Quiz';
import SportsEsportsIcon from '@mui/icons-material/SportsEsports';
import MapIcon from '@mui/icons-material/Map';
import AddAlertIcon from '@mui/icons-material/AddAlert';
import VerifiedUserIcon from '@mui/icons-material/VerifiedUser';
import PhoneForwardedIcon from '@mui/icons-material/PhoneForwarded';
import ArrowForwardIcon from '@mui/icons-material/ArrowForward';
import { useAuth } from '../../context/AuthContext';

export const LandingPage: React.FC = () => {
  const navigate = useNavigate();
  const { isAuthenticated, role } = useAuth();

  const handlePortalEntry = () => {
    if (!isAuthenticated) {
      navigate('/login');
    } else if (role === 'ADMIN') {
      navigate('/admin');
    } else if (role === 'GOVERNMENT_OFFICER') {
      navigate('/officer');
    } else {
      navigate('/student');
    }
  };

  const workflowSteps = [
    { label: 'LEARN', desc: 'Disaster safety modules' },
    { label: 'PREPARE', desc: 'Interactive simulations' },
    { label: 'MONITOR', desc: 'Live spatial incident map' },
    { label: 'REPORT', desc: 'Citizen evidence dispatch' },
    { label: 'VERIFY', desc: 'Government officer audit' },
    { label: 'ASSIGN', desc: 'Fire, police & triage teams' },
    { label: 'RESPOND', desc: 'Real-time coordinated action' },
    { label: 'RESOLVE', desc: 'Verified status closure' },
  ];

  return (
    <Box sx={{ bgcolor: '#f8fafc', minHeight: '100vh' }}>
      {/* Header Bar */}
      <Box sx={{ bgcolor: '#0f172a', py: 2, px: { xs: 2, md: 4 }, borderBottom: '1px solid rgba(255,255,255,0.08)' }}>
        <Container maxWidth="lg" sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 1.5 }}>
            <Avatar sx={{ bgcolor: '#0284c7', width: 40, height: 40 }}>
              <SecurityIcon />
            </Avatar>
            <Box>
              <Typography variant="h6" sx={{ color: '#fff', fontWeight: 800, letterSpacing: '-0.01em', lineHeight: 1.1 }}>
                DISASTER PREPAREDNESS
              </Typography>
              <Typography variant="caption" sx={{ color: '#38bdf8', fontWeight: 600, letterSpacing: '0.05em' }}>
                & EMERGENCY COORDINATION PLATFORM
              </Typography>
            </Box>
          </Box>

          <Box sx={{ display: 'flex', gap: 1.5 }}>
            <Button component={Link} to="/emergency-contacts" sx={{ color: '#94a3b8', '&:hover': { color: '#fff' } }}>
              Hotlines
            </Button>
            <Button component={Link} to="/map" sx={{ color: '#94a3b8', '&:hover': { color: '#fff' } }}>
              Public Map
            </Button>
            {isAuthenticated ? (
              <Button variant="contained" color="secondary" onClick={handlePortalEntry}>
                Go to Portal
              </Button>
            ) : (
              <>
                <Button component={Link} to="/login" variant="outlined" sx={{ color: '#fff', borderColor: 'rgba(255,255,255,0.2)' }}>
                  Sign In
                </Button>
                <Button component={Link} to="/register" variant="contained" color="secondary">
                  Register
                </Button>
              </>
            )}
          </Box>
        </Container>
      </Box>

      {/* Hero Section */}
      <Box sx={{ bgcolor: '#0f172a', color: '#fff', pt: { xs: 6, md: 10 }, pb: { xs: 8, md: 12 } }}>
        <Container maxWidth="lg">
          <Grid container spacing={4} alignItems="center">
            <Grid item xs={12} md={7}>
              <Chip
                label="GOVERNMENT & CIVIC SAFETY NETWORK"
                size="small"
                sx={{ bgcolor: 'rgba(14, 165, 233, 0.15)', color: '#38bdf8', fontWeight: 700, mb: 2 }}
              />
              <Typography variant="h2" sx={{ fontWeight: 800, mb: 2, lineHeight: 1.15 }}>
                Learn. Prepare. Respond. <br />
                <span style={{ color: '#38bdf8' }}>Stay Safe.</span>
              </Typography>
              <Typography variant="body1" sx={{ color: '#94a3b8', fontSize: '1.1rem', mb: 4, maxWidth: 560 }}>
                A unified platform combining academic disaster education, simulation training, citizen incident reporting, and authorized Government Officer verification and response team dispatch.
              </Typography>

              <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 2 }}>
                <Button
                  variant="contained"
                  color="secondary"
                  size="large"
                  onClick={handlePortalEntry}
                  endIcon={<ArrowForwardIcon />}
                  sx={{ px: 3, py: 1.2, fontWeight: 700 }}
                >
                  Enter Safety Platform
                </Button>
                <Button
                  component={Link}
                  to="/map"
                  variant="outlined"
                  size="large"
                  sx={{ color: '#fff', borderColor: 'rgba(255,255,255,0.3)', px: 3, py: 1.2 }}
                  startIcon={<MapIcon />}
                >
                  Live Incident Map
                </Button>
              </Box>
            </Grid>

            <Grid item xs={12} md={5}>
              <Paper sx={{ p: 3, bgcolor: '#1e293b', border: '1px solid rgba(255,255,255,0.1)', borderRadius: 3 }}>
                <Typography variant="subtitle2" sx={{ color: '#38bdf8', fontWeight: 700, mb: 1 }}>
                  INTEGRATED EMERGENCY CYCLE
                </Typography>
                <Typography variant="h6" sx={{ color: '#fff', fontWeight: 700, mb: 2 }}>
                  From Education to Coordinated Field Relief
                </Typography>
                <Divider sx={{ borderColor: 'rgba(255,255,255,0.1)', mb: 2 }} />

                <Box sx={{ display: 'flex', flexDirection: 'column', gap: 1.5 }}>
                  <Box sx={{ display: 'flex', alignItems: 'center', gap: 1.5 }}>
                    <Avatar sx={{ bgcolor: 'rgba(16, 185, 129, 0.2)', color: '#10b981', width: 34, height: 34 }}>
                      <SchoolIcon fontSize="small" />
                    </Avatar>
                    <Box>
                      <Typography variant="body2" sx={{ color: '#fff', fontWeight: 600 }}>
                        Disaster Readiness Academy
                      </Typography>
                      <Typography variant="caption" sx={{ color: '#94a3b8' }}>
                        Earthquake, cyclone, flood, & wildfire protocols
                      </Typography>
                    </Box>
                  </Box>

                  <Box sx={{ display: 'flex', alignItems: 'center', gap: 1.5 }}>
                    <Avatar sx={{ bgcolor: 'rgba(245, 158, 11, 0.2)', color: '#f59e0b', width: 34, height: 34 }}>
                      <AddAlertIcon fontSize="small" />
                    </Avatar>
                    <Box>
                      <Typography variant="body2" sx={{ color: '#fff', fontWeight: 600 }}>
                        Citizen Emergency Reporting
                      </Typography>
                      <Typography variant="caption" sx={{ color: '#94a3b8' }}>
                        Geo-tagged incidents with photo/video evidence
                      </Typography>
                    </Box>
                  </Box>

                  <Box sx={{ display: 'flex', alignItems: 'center', gap: 1.5 }}>
                    <Avatar sx={{ bgcolor: 'rgba(2, 132, 199, 0.2)', color: '#38bdf8', width: 34, height: 34 }}>
                      <VerifiedUserIcon fontSize="small" />
                    </Avatar>
                    <Box>
                      <Typography variant="body2" sx={{ color: '#fff', fontWeight: 600 }}>
                        Official Officer Verification
                      </Typography>
                      <Typography variant="caption" sx={{ color: '#94a3b8' }}>
                        Multi-agency dispatch to Fire, Police & Trauma units
                      </Typography>
                    </Box>
                  </Box>
                </Box>
              </Paper>
            </Grid>
          </Grid>
        </Container>
      </Box>

      {/* Workflow Cycle Ribbon */}
      <Box sx={{ bgcolor: '#0284c7', color: '#fff', py: 3 }}>
        <Container maxWidth="lg">
          <Grid container spacing={2} justifyContent="center">
            {workflowSteps.map((step, idx) => (
              <Grid item xs={6} sm={3} md={1.5} key={step.label} sx={{ textAlign: 'center' }}>
                <Typography variant="subtitle2" sx={{ fontWeight: 800, fontSize: '0.85rem' }}>
                  {idx + 1}. {step.label}
                </Typography>
                <Typography variant="caption" sx={{ opacity: 0.9, fontSize: '0.72rem', display: 'block' }}>
                  {step.desc}
                </Typography>
              </Grid>
            ))}
          </Grid>
        </Container>
      </Box>

      {/* Platform Pillars */}
      <Container maxWidth="lg" sx={{ py: 8 }}>
        <Box sx={{ textAlign: 'center', mb: 6 }}>
          <Typography variant="overline" sx={{ color: 'secondary.main', fontWeight: 800, letterSpacing: '0.1em' }}>
            CORE CAPABILITIES
          </Typography>
          <Typography variant="h4" sx={{ fontWeight: 800, color: 'text.primary', mt: 0.5 }}>
            Designed for Citizens, Academic Institutions, and Emergency Responders
          </Typography>
        </Box>

        <Grid container spacing={3}>
          <Grid item xs={12} sm={6} md={3}>
            <Card sx={{ height: '100%', p: 1 }}>
              <CardContent>
                <Avatar sx={{ bgcolor: '#e0f2fe', color: '#0284c7', mb: 2 }}>
                  <SchoolIcon />
                </Avatar>
                <Typography variant="h6" sx={{ fontWeight: 700, mb: 1 }}>
                  Curated Modules
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  Actionable Before, During, and After safety guidelines, warning signs, and official do's & don'ts.
                </Typography>
              </CardContent>
            </Card>
          </Grid>

          <Grid item xs={12} sm={6} md={3}>
            <Card sx={{ height: '100%', p: 1 }}>
              <CardContent>
                <Avatar sx={{ bgcolor: '#fef3c7', color: '#d97706', mb: 2 }}>
                  <SportsEsportsIcon />
                </Avatar>
                <Typography variant="h6" sx={{ fontWeight: 700, mb: 1 }}>
                  Live Simulations
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  Real-time scenario branching. Test tactical decisions inside classrooms, subways, and homes.
                </Typography>
              </CardContent>
            </Card>
          </Grid>

          <Grid item xs={12} sm={6} md={3}>
            <Card sx={{ height: '100%', p: 1 }}>
              <CardContent>
                <Avatar sx={{ bgcolor: '#fee2e2', color: '#dc2626', mb: 2 }}>
                  <AddAlertIcon />
                </Avatar>
                <Typography variant="h6" sx={{ fontWeight: 700, mb: 1 }}>
                  Evidence Reporting
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  Fast citizen reporting with GPS coordinate capture and photo/video evidence attachments.
                </Typography>
              </CardContent>
            </Card>
          </Grid>

          <Grid item xs={12} sm={6} md={3}>
            <Card sx={{ height: '100%', p: 1 }}>
              <CardContent>
                <Avatar sx={{ bgcolor: '#dcfce7', color: '#16a34a', mb: 2 }}>
                  <VerifiedUserIcon />
                </Avatar>
                <Typography variant="h6" sx={{ fontWeight: 700, mb: 1 }}>
                  Agency Assignment
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  Authorized Government Officers audit reports, assign response departments, and track incident resolution.
                </Typography>
              </CardContent>
            </Card>
          </Grid>
        </Grid>
      </Container>
    </Box>
  );
};
