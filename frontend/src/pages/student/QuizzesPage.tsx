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
import QuizIcon from '@mui/icons-material/Quiz';
import TimerOutlinedIcon from '@mui/icons-material/TimerOutlined';
import HelpOutlineIcon from '@mui/icons-material/HelpOutline';
import ArrowForwardIcon from '@mui/icons-material/ArrowForward';
import { educationService } from '../../services/educationService';
import { Quiz } from '../../types';
import { LoadingScreen } from '../../components/common/LoadingScreen';

export const QuizzesPage: React.FC = () => {
  const [quizzes, setQuizzes] = useState<Quiz[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchQuizzes = async () => {
      try {
        const res = await educationService.getQuizzes();
        setQuizzes(res);
      } catch (err: any) {
        setError(err.response?.data?.message || 'Failed to load quizzes.');
      } finally {
        setLoading(false);
      }
    };
    fetchQuizzes();
  }, []);

  if (loading) return <LoadingScreen message="Loading readiness evaluations..." />;
  if (error) return <Alert severity="error">{error}</Alert>;

  return (
    <Box>
      <Box sx={{ mb: 4 }}>
        <Typography variant="overline" sx={{ color: 'secondary.main', fontWeight: 800 }}>
          READINESS EVALUATION
        </Typography>
        <Typography variant="h4" sx={{ fontWeight: 800 }}>
          Preparedness Assessments & Quizzes
        </Typography>
        <Typography variant="body1" color="text.secondary">
          Validate your procedural comprehension. Scores directly contribute to your transparent personal readiness rating.
        </Typography>
      </Box>

      <Grid container spacing={3}>
        {quizzes.map((quiz) => (
          <Grid item xs={12} md={6} key={quiz.id}>
            <Card sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
              <CardContent sx={{ flex: 1, p: 3 }}>
                <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 1.5 }}>
                  <Chip
                    label={quiz.disasterType?.name || 'General Emergency'}
                    size="small"
                    color="secondary"
                    sx={{ fontWeight: 700 }}
                  />
                  <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5, color: 'text.secondary', fontSize: '0.85rem' }}>
                    <TimerOutlinedIcon sx={{ fontSize: 18 }} />
                    {quiz.timeLimitMinutes || 10} mins
                  </Box>
                </Box>

                <Typography variant="h5" sx={{ fontWeight: 800, mb: 1 }}>
                  {quiz.title}
                </Typography>

                <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
                  {quiz.description || 'Comprehensive evaluation covering tactical hazard procedures and warning cues.'}
                </Typography>

                <Box sx={{ display: 'flex', gap: 2, mt: 'auto', pt: 2, borderTop: '1px solid #e2e8f0' }}>
                  <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5 }}>
                    <HelpOutlineIcon fontSize="small" sx={{ color: 'text.secondary' }} />
                    <Typography variant="caption" sx={{ fontWeight: 600 }}>
                      {quiz.questions?.length || 4} Questions
                    </Typography>
                  </Box>
                  <Box>
                    <Typography variant="caption" sx={{ fontWeight: 700, color: 'secondary.main' }}>
                      Passing Grade: {quiz.passingScore}%
                    </Typography>
                  </Box>
                </Box>
              </CardContent>

              <CardActions sx={{ p: 3, pt: 0 }}>
                <Button
                  variant="contained"
                  color="secondary"
                  fullWidth
                  size="large"
                  startIcon={<QuizIcon />}
                  endIcon={<ArrowForwardIcon />}
                  onClick={() => navigate(`/student/quizzes/${quiz.id}`)}
                  sx={{ fontWeight: 700 }}
                >
                  Take Assessment
                </Button>
              </CardActions>
            </Card>
          </Grid>
        ))}
      </Grid>
    </Box>
  );
};
