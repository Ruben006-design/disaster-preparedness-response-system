import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import {
  Box,
  Typography,
  Card,
  CardContent,
  Button,
  Paper,
  Alert,
  Divider,
  Chip,
  Radio,
  RadioGroup,
  FormControlLabel,
  FormControl,
} from '@mui/material';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import CancelIcon from '@mui/icons-material/Cancel';
import RefreshIcon from '@mui/icons-material/Refresh';
import SportsEsportsIcon from '@mui/icons-material/SportsEsports';
import { educationService } from '../../services/educationService';
import { Simulation, SimulationResult } from '../../types';
import { LoadingScreen } from '../../components/common/LoadingScreen';
import { SeverityBadge } from '../../components/common/Badges';

export const SimulationPlayPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();

  const [simulation, setSimulation] = useState<Simulation | null>(null);
  const [selectedOptionId, setSelectedOptionId] = useState<number | null>(null);
  const [submitting, setSubmitting] = useState(false);
  const [result, setResult] = useState<SimulationResult | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchSim = async () => {
      try {
        if (!id) return;
        const res = await educationService.getSimulationById(Number(id));
        setSimulation(res);
      } catch (err: any) {
        setError(err.response?.data?.message || 'Failed to load simulation.');
      } finally {
        setLoading(false);
      }
    };
    fetchSim();
  }, [id]);

  const handleSubmit = async () => {
    if (!simulation || selectedOptionId === null) return;
    setSubmitting(true);
    try {
      const res = await educationService.submitSimulation(simulation.id, selectedOptionId);
      setResult(res);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to evaluate action.');
    } finally {
      setSubmitting(false);
    }
  };

  const handleRetry = () => {
    setResult(null);
    setSelectedOptionId(null);
  };

  if (loading) return <LoadingScreen message="Initializing simulation scenario environment..." />;
  if (error || !simulation) return <Alert severity="error">{error || 'Simulation not found.'}</Alert>;

  return (
    <Box sx={{ maxWidth: 850, mx: 'auto' }}>
      <Button
        startIcon={<ArrowBackIcon />}
        onClick={() => navigate('/simulations')}
        sx={{ mb: 2, color: 'text.secondary' }}
      >
        Back to Simulations
      </Button>

      {/* Scenario Stage */}
      <Paper
        sx={{
          p: 4,
          mb: 4,
          borderRadius: 3,
          background: 'linear-gradient(135deg, #1e293b 0%, #0f172a 100%)',
          color: '#fff',
        }}
      >
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
          <SeverityBadge severity={simulation.initialRiskLevel} />
          <Chip label="TACTICAL SIMULATION" size="small" sx={{ bgcolor: 'rgba(255,255,255,0.1)', color: '#fff', fontWeight: 700 }} />
        </Box>

        <Typography variant="h4" sx={{ fontWeight: 800, mb: 1.5 }}>
          {simulation.title}
        </Typography>

        {simulation.locationContext && (
          <Typography variant="subtitle2" sx={{ color: '#38bdf8', fontWeight: 600, mb: 2 }}>
            Environment: {simulation.locationContext}
          </Typography>
        )}

        <Divider sx={{ borderColor: 'rgba(255,255,255,0.15)', mb: 3 }} />

        <Typography variant="h6" sx={{ fontWeight: 600, lineHeight: 1.6, color: '#e2e8f0' }}>
          "{simulation.scenarioText}"
        </Typography>
      </Paper>

      {/* Decision Options / Outcome */}
      {!result ? (
        <Card sx={{ mb: 4 }}>
          <CardContent sx={{ p: 4 }}>
            <Typography variant="h6" sx={{ fontWeight: 700, mb: 2 }}>
              Choose your immediate survival action:
            </Typography>

            <FormControl component="fieldset" sx={{ width: '100%' }}>
              <RadioGroup
                value={selectedOptionId || ''}
                onChange={(e) => setSelectedOptionId(Number(e.target.value))}
              >
                {simulation.options.map((opt) => (
                  <Paper
                    key={opt.id}
                    variant="outlined"
                    sx={{
                      p: 2,
                      mb: 2,
                      borderRadius: 2,
                      border: selectedOptionId === opt.id ? '2px solid #0284c7' : '1px solid #e2e8f0',
                      bgcolor: selectedOptionId === opt.id ? 'rgba(2, 132, 199, 0.04)' : '#fff',
                      cursor: 'pointer',
                      transition: 'all 0.15s ease',
                      '&:hover': { bgcolor: 'rgba(2, 132, 199, 0.02)' },
                    }}
                    onClick={() => setSelectedOptionId(opt.id)}
                  >
                    <FormControlLabel
                      value={opt.id}
                      control={<Radio color="primary" />}
                      label={
                        <Typography variant="body1" sx={{ fontWeight: 600, ml: 1 }}>
                          {opt.actionText}
                        </Typography>
                      }
                      sx={{ width: '100%', m: 0 }}
                    />
                  </Paper>
                ))}
              </RadioGroup>
            </FormControl>

            <Box sx={{ mt: 3, textAlign: 'right' }}>
              <Button
                variant="contained"
                color="primary"
                size="large"
                disabled={selectedOptionId === null || submitting}
                onClick={handleSubmit}
                startIcon={<SportsEsportsIcon />}
                sx={{ px: 4, py: 1.2, fontWeight: 700 }}
              >
                {submitting ? 'Simulating Outcome...' : 'Execute Decision'}
              </Button>
            </Box>
          </CardContent>
        </Card>
      ) : (
        /* Evaluation & Consequence Screen */
        <Card sx={{ mb: 4, borderColor: result.appropriate ? '#bbf7d0' : '#fecaca' }}>
          <CardContent sx={{ p: 4 }}>
            <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, mb: 3 }}>
              {result.appropriate ? (
                <CheckCircleIcon sx={{ fontSize: 44, color: 'success.main' }} />
              ) : (
                <CancelIcon sx={{ fontSize: 44, color: 'error.main' }} />
              )}
              <Box>
                <Typography variant="h5" sx={{ fontWeight: 800 }}>
                  {result.appropriate ? 'Tactically Sound Response!' : 'Dangerous Course of Action'}
                </Typography>
                <Chip
                  label={`${result.scoreEarned > 0 ? '+' : ''}${result.scoreEarned} Preparedness Points`}
                  color={result.appropriate ? 'success' : 'error'}
                  sx={{ fontWeight: 700, mt: 0.5 }}
                />
              </Box>
            </Box>

            <Paper sx={{ p: 3, bgcolor: '#f8fafc', borderRadius: 2, mb: 3 }}>
              <Typography variant="subtitle2" sx={{ fontWeight: 700, color: 'text.secondary', mb: 1 }}>
                CONSEQUENCE & PHYSICAL OUTCOME:
              </Typography>
              <Typography variant="body1" sx={{ fontWeight: 600, color: 'text.primary', mb: 2 }}>
                {result.consequenceText}
              </Typography>

              <Divider sx={{ my: 1.5 }} />

              <Typography variant="subtitle2" sx={{ fontWeight: 700, color: 'text.secondary', mb: 0.5 }}>
                TACTICAL SAFETY ANALYSIS:
              </Typography>
              <Typography variant="body2" sx={{ color: 'text.secondary', lineHeight: 1.6 }}>
                {result.safetyFeedback}
              </Typography>
            </Paper>

            <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <Button
                variant="outlined"
                color="secondary"
                startIcon={<RefreshIcon />}
                onClick={handleRetry}
              >
                Retry Scenario
              </Button>
              <Button
                variant="contained"
                color="primary"
                onClick={() => navigate('/student/dashboard')}
                sx={{ fontWeight: 700 }}
              >
                Return to Dashboard
              </Button>
            </Box>
          </CardContent>
        </Card>
      )}
    </Box>
  );
};
