import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Box,
  Typography,
  Grid,
  Card,
  CardContent,
  CardActions,
  Button,
  Chip,
  LinearProgress,
  Alert,
} from '@mui/material';
import SchoolIcon from '@mui/icons-material/School';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import AccessTimeIcon from '@mui/icons-material/AccessTime';
import ArrowForwardIcon from '@mui/icons-material/ArrowForward';
import { educationService } from '../../services/educationService';
import { LearningModule } from '../../types';
import { LoadingScreen } from '../../components/common/LoadingScreen';
import { SeverityBadge } from '../../components/common/Badges';

export const ModulesPage: React.FC = () => {
  const [modules, setModules] = useState<LearningModule[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchModules = async () => {
      try {
        const res = await educationService.getModules();
        setModules(res);
      } catch (err: any) {
        setError(err.response?.data?.message || 'Failed to load educational learning modules.');
      } finally {
        setLoading(false);
      }
    };
    fetchModules();
  }, []);

  if (loading) return <LoadingScreen message="Loading safety education modules..." />;
  if (error) return <Alert severity="error">{error}</Alert>;

  return (
    <Box>
      <Box sx={{ mb: 4 }}>
        <Typography variant="overline" sx={{ color: 'secondary.main', fontWeight: 800 }}>
          CURRICULUM & KNOWLEDGE BASE
        </Typography>
        <Typography variant="h4" sx={{ fontWeight: 800 }}>
          Disaster Preparedness Modules
        </Typography>
        <Typography variant="body1" color="text.secondary">
          Certified safety manuals covering pre-disaster hardening, tactical in-event responses, and post-calamity recovery procedures.
        </Typography>
      </Box>

      <Grid container spacing={3}>
        {modules.map((m) => (
          <Grid item xs={12} sm={6} md={4} key={m.id}>
            <Card sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
              <CardContent sx={{ flex: 1, p: 3 }}>
                <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', mb: 1.5 }}>
                  <SeverityBadge severity={m.defaultSeverity} />
                  <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5, color: 'text.secondary', fontSize: '0.8rem' }}>
                    <AccessTimeIcon sx={{ fontSize: 16 }} />
                    {m.estimatedMinutes} mins
                  </Box>
                </Box>

                <Typography variant="h6" sx={{ fontWeight: 700, mb: 1, lineHeight: 1.3 }}>
                  {m.title}
                </Typography>

                <Chip
                  label={m.disasterTypeName}
                  size="small"
                  sx={{ bgcolor: '#f1f5f9', fontWeight: 600, mb: 2, fontSize: '0.72rem' }}
                />

                <Typography variant="body2" color="text.secondary" sx={{ mb: 2, minHeight: 44 }}>
                  {m.summary}
                </Typography>

                {/* Progress Indicator */}
                <Box sx={{ mt: 'auto' }}>
                  <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 0.5 }}>
                    <Typography variant="caption" sx={{ fontWeight: 600 }}>
                      Progress
                    </Typography>
                    <Typography variant="caption" sx={{ fontWeight: 700 }}>
                      {m.userCompleted ? '100% Completed' : `${m.userProgress || 0}%`}
                    </Typography>
                  </Box>
                  <LinearProgress
                    variant="determinate"
                    value={m.userCompleted ? 100 : m.userProgress || 0}
                    color={m.userCompleted ? 'success' : 'secondary'}
                    sx={{ height: 6, borderRadius: 3 }}
                  />
                </Box>
              </CardContent>

              <CardActions sx={{ p: 2, pt: 0 }}>
                <Button
                  variant={m.userCompleted ? 'outlined' : 'contained'}
                  color={m.userCompleted ? 'success' : 'primary'}
                  fullWidth
                  startIcon={m.userCompleted ? <CheckCircleIcon /> : <SchoolIcon />}
                  endIcon={<ArrowForwardIcon />}
                  onClick={() => navigate(`/student/modules/${m.id}`)}
                >
                  {m.userCompleted ? 'Review Module' : 'Start Learning'}
                </Button>
              </CardActions>
            </Card>
          </Grid>
        ))}
      </Grid>
    </Box>
  );
};
