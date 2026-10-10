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
  Alert,
} from '@mui/material';
import SportsEsportsIcon from '@mui/icons-material/SportsEsports';
import ArrowForwardIcon from '@mui/icons-material/ArrowForward';
import PlaceIcon from '@mui/icons-material/Place';
import { educationService } from '../../services/educationService';
import { Simulation } from '../../types';
import { LoadingScreen } from '../../components/common/LoadingScreen';
import { SeverityBadge } from '../../components/common/Badges';

export const SimulationsPage: React.FC = () => {
  const [simulations, setSimulations] = useState<Simulation[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchSimulations = async () => {
      try {
        const res = await educationService.getSimulations();
        setSimulations(res);
      } catch (err: any) {
        setError(err.response?.data?.message || 'Failed to load disaster simulations.');
      } finally {
        setLoading(false);
      }
    };
    fetchSimulations();
  }, []);

  if (loading) return <LoadingScreen message="Loading interactive simulations..." />;
  if (error) return <Alert severity="error">{error}</Alert>;

  return (
    <Box>
      <Box sx={{ mb: 4 }}>
        <Typography variant="overline" sx={{ color: 'warning.main', fontWeight: 800 }}>
          TACTICAL DECISION DRILLS
        </Typography>
        <Typography variant="h4" sx={{ fontWeight: 800 }}>
          Interactive Disaster Simulations
        </Typography>
        <Typography variant="body1" color="text.secondary">
          Step into realistic emergency scenarios. Your decisions are graded based on official civil defense survival protocols.
        </Typography>
      </Box>

      <Grid container spacing={3}>
        {simulations.map((sim) => (
          <Grid item xs={12} md={6} key={sim.id}>
            <Card sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
              <CardContent sx={{ flex: 1, p: 3 }}>
                <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
                  <SeverityBadge severity={sim.initialRiskLevel} />
                  <Chip
                    label={sim.disasterType?.name || 'Simulation'}
                    size="small"
                    sx={{ bgcolor: '#f1f5f9', fontWeight: 600 }}
                  />
                </Box>

                <Typography variant="h5" sx={{ fontWeight: 800, mb: 1.5 }}>
                  {sim.title}
                </Typography>

                {sim.locationContext && (
                  <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5, color: 'text.secondary', mb: 2 }}>
                    <PlaceIcon sx={{ fontSize: 18, color: 'secondary.main' }} />
                    <Typography variant="body2" sx={{ fontWeight: 500 }}>
                      {sim.locationContext}
                    </Typography>
                  </Box>
                )}

                <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>
                  {sim.scenarioText}
                </Typography>
              </CardContent>

              <CardActions sx={{ p: 3, pt: 0 }}>
                <Button
                  variant="contained"
                  color="warning"
                  fullWidth
                  size="large"
                  startIcon={<SportsEsportsIcon />}
                  endIcon={<ArrowForwardIcon />}
                  onClick={() => navigate(`/student/simulations/${sim.id}`)}
                  sx={{ fontWeight: 700 }}
                >
                  Launch Scenario Drill
                </Button>
              </CardActions>
            </Card>
          </Grid>
        ))}
      </Grid>
    </Box>
  );
};
