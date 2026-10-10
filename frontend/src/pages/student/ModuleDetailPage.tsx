import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import {
  Box,
  Typography,
  Card,
  CardContent,
  Grid,
  Button,
  Chip,
  Paper,
  Divider,
  Alert,
  Snackbar,
} from '@mui/material';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import AccessTimeIcon from '@mui/icons-material/AccessTime';
import WarningAmberIcon from '@mui/icons-material/WarningAmber';
import DoneIcon from '@mui/icons-material/Done';
import CloseIcon from '@mui/icons-material/Close';
import { educationService } from '../../services/educationService';
import { DisasterType, LearningModule } from '../../types';
import { LoadingScreen } from '../../components/common/LoadingScreen';
import { SeverityBadge } from '../../components/common/Badges';

export const ModuleDetailPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();

  const [module, setModule] = useState<LearningModule | null>(null);
  const [disaster, setDisaster] = useState<DisasterType | null>(null);
  const [loading, setLoading] = useState(true);
  const [completing, setCompleting] = useState(false);
  const [successSnackbar, setSuccessSnackbar] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchData = async () => {
      try {
        if (!id) return;
        const mod = await educationService.getModuleById(Number(id));
        setModule(mod);
        const dt = await educationService.getDisasterById(mod.disasterTypeId);
        setDisaster(dt);
      } catch (err: any) {
        setError(err.response?.data?.message || 'Failed to load module details.');
      } finally {
        setLoading(false);
      }
    };
    fetchData();
  }, [id]);

  const handleMarkComplete = async () => {
    if (!module) return;
    setCompleting(true);
    try {
      await educationService.updateModuleProgress(module.id, 100, true);
      setModule({ ...module, userCompleted: true, userProgress: 100 });
      setSuccessSnackbar(true);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to update progress.');
    } finally {
      setCompleting(false);
    }
  };

  if (loading) return <LoadingScreen message="Loading module curriculum..." />;
  if (error || !module) return <Alert severity="error">{error || 'Module not found.'}</Alert>;

  return (
    <Box sx={{ maxWidth: 1000, mx: 'auto' }}>
      <Button
        startIcon={<ArrowBackIcon />}
        onClick={() => navigate('/modules')}
        sx={{ mb: 2, color: 'text.secondary' }}
      >
        Back to Modules
      </Button>

      {/* Module Title Header */}
      <Paper sx={{ p: 4, mb: 4, borderRadius: 3, border: '1px solid #e2e8f0' }}>
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', flexWrap: 'wrap', gap: 2 }}>
          <Box sx={{ flex: 1 }}>
            <Box sx={{ display: 'flex', alignItems: 'center', gap: 1.5, mb: 1.5 }}>
              <SeverityBadge severity={module.defaultSeverity} />
              <Chip label={module.disasterTypeName} size="small" sx={{ fontWeight: 700 }} />
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5, color: 'text.secondary', fontSize: '0.85rem' }}>
                <AccessTimeIcon sx={{ fontSize: 18 }} />
                {module.estimatedMinutes} minutes read
              </Box>
            </Box>
            <Typography variant="h4" sx={{ fontWeight: 800, mb: 1 }}>
              {module.title}
            </Typography>
            <Typography variant="body1" color="text.secondary">
              {module.summary}
            </Typography>
          </Box>

          <Button
            variant={module.userCompleted ? 'outlined' : 'contained'}
            color={module.userCompleted ? 'success' : 'primary'}
            size="large"
            disabled={completing}
            startIcon={<CheckCircleIcon />}
            onClick={handleMarkComplete}
            sx={{ fontWeight: 700, minWidth: 200 }}
          >
            {module.userCompleted ? 'Completed' : completing ? 'Saving...' : 'Mark as Completed'}
          </Button>
        </Box>
      </Paper>

      {/* Warning Signs */}
      {disaster?.warningSigns && (
        <Alert severity="warning" icon={<WarningAmberIcon />} sx={{ mb: 4, borderRadius: 2 }}>
          <Typography variant="subtitle2" sx={{ fontWeight: 700 }}>
            Early Warning Signs & Sensor Triggers
          </Typography>
          <Typography variant="body2">{disaster.warningSigns}</Typography>
        </Alert>
      )}

      {/* Main Educational Text */}
      <Card sx={{ mb: 4 }}>
        <CardContent sx={{ p: 4 }}>
          <Typography variant="h6" sx={{ fontWeight: 700, mb: 2 }}>
            Comprehensive Safety Protocol
          </Typography>
          <Typography
            variant="body1"
            sx={{ whiteSpace: 'pre-line', lineHeight: 1.8, color: '#334155' }}
          >
            {module.content}
          </Typography>
        </CardContent>
      </Card>

      {/* Chronological Action Steps (Before, During, After) */}
      <Typography variant="h5" sx={{ fontWeight: 800, mb: 2 }}>
        Chronological Emergency Action Plan
      </Typography>

      <Grid container spacing={3} sx={{ mb: 4 }}>
        <Grid item xs={12} md={4}>
          <Card sx={{ height: '100%', bgcolor: '#f0fdf4', borderColor: '#bbf7d0' }}>
            <CardContent sx={{ p: 3 }}>
              <Typography variant="subtitle1" sx={{ fontWeight: 800, color: '#166534', mb: 1 }}>
                1. BEFORE THE DISASTER
              </Typography>
              <Typography variant="body2" sx={{ whiteSpace: 'pre-line', color: '#14532d' }}>
                {disaster?.beforeSteps || 'Store emergency kits, memorize evacuation routes, secure heavy household and campus equipment.'}
              </Typography>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} md={4}>
          <Card sx={{ height: '100%', bgcolor: '#fef2f2', borderColor: '#fecaca' }}>
            <CardContent sx={{ p: 3 }}>
              <Typography variant="subtitle1" sx={{ fontWeight: 800, color: '#991b1b', mb: 1 }}>
                2. DURING THE EVENT
              </Typography>
              <Typography variant="body2" sx={{ whiteSpace: 'pre-line', color: '#7f1d1d' }}>
                {disaster?.duringSteps || 'Seek primary structural cover, avoid panic running, keep away from glass walls and exterior balconies.'}
              </Typography>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} md={4}>
          <Card sx={{ height: '100%', bgcolor: '#eff6ff', borderColor: '#bfdbfe' }}>
            <CardContent sx={{ p: 3 }}>
              <Typography variant="subtitle1" sx={{ fontWeight: 800, color: '#1e40af', mb: 1 }}>
                3. AFTER THE EVENT
              </Typography>
              <Typography variant="body2" sx={{ whiteSpace: 'pre-line', color: '#1e3a8a' }}>
                {disaster?.afterSteps || 'Check for utility fractures, listen for civil defense FM broadcasts, assemble at designated open-air muster points.'}
              </Typography>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      {/* Do's and Don'ts */}
      <Grid container spacing={3} sx={{ mb: 4 }}>
        <Grid item xs={12} md={6}>
          <Paper sx={{ p: 3, border: '1px solid #bbf7d0', bgcolor: '#ffffff', borderRadius: 3 }}>
            <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 2 }}>
              <DoneIcon color="success" />
              <Typography variant="h6" sx={{ fontWeight: 700, color: 'success.main' }}>
                MANDATORY DO'S
              </Typography>
            </Box>
            <Typography variant="body2" sx={{ whiteSpace: 'pre-line', lineHeight: 1.8 }}>
              {disaster?.doList || 'Follow emergency coordinator commands.\nKeep emergency radios tuned to disaster broadcasts.'}
            </Typography>
          </Paper>
        </Grid>

        <Grid item xs={12} md={6}>
          <Paper sx={{ p: 3, border: '1px solid #fecaca', bgcolor: '#ffffff', borderRadius: 3 }}>
            <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 2 }}>
              <CloseIcon color="error" />
              <Typography variant="h6" sx={{ fontWeight: 700, color: 'error.main' }}>
                CRITICAL DON'TS
              </Typography>
            </Box>
            <Typography variant="body2" sx={{ whiteSpace: 'pre-line', lineHeight: 1.8 }}>
              {disaster?.dontList || 'Do not use elevators during emergencies.\nDo not spread unverified rumors on social channels.'}
            </Typography>
          </Paper>
        </Grid>
      </Grid>

      {/* Bottom Completion Action */}
      <Box sx={{ textAlign: 'center', py: 4 }}>
        <Button
          variant="contained"
          color="primary"
          size="large"
          startIcon={<CheckCircleIcon />}
          onClick={handleMarkComplete}
          disabled={completing || module.userCompleted}
          sx={{ px: 4, py: 1.5, fontWeight: 700 }}
        >
          {module.userCompleted ? 'Module Already Completed ✓' : 'Mark Module as Completed'}
        </Button>
      </Box>

      <Snackbar
        open={successSnackbar}
        autoHideDuration={4000}
        onClose={() => setSuccessSnackbar(false)}
        message="Module progress saved! Your preparedness score has been updated."
      />
    </Box>
  );
};
