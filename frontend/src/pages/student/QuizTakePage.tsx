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
import SendIcon from '@mui/icons-material/Send';
import { educationService } from '../../services/educationService';
import { Quiz, QuizResult } from '../../types';
import { LoadingScreen } from '../../components/common/LoadingScreen';

export const QuizTakePage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();

  const [quiz, setQuiz] = useState<Quiz | null>(null);
  const [answers, setAnswers] = useState<Record<number, number>>({});
  const [result, setResult] = useState<QuizResult | null>(null);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchQuiz = async () => {
      try {
        if (!id) return;
        const res = await educationService.getQuizById(Number(id));
        setQuiz(res);
      } catch (err: any) {
        setError(err.response?.data?.message || 'Failed to load quiz.');
      } finally {
        setLoading(false);
      }
    };
    fetchQuiz();
  }, [id]);

  const handleSelectOption = (questionId: number, optionId: number) => {
    setAnswers((prev) => ({ ...prev, [questionId]: optionId }));
  };

  const handleSubmit = async () => {
    if (!quiz) return;
    setSubmitting(true);
    try {
      const res = await educationService.submitQuiz(quiz.id, answers);
      setResult(res);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to submit quiz.');
    } finally {
      setSubmitting(false);
    }
  };

  const handleRetake = () => {
    setResult(null);
    setAnswers({});
  };

  if (loading) return <LoadingScreen message="Loading assessment questions..." />;
  if (error || !quiz) return <Alert severity="error">{error || 'Quiz not found.'}</Alert>;

  const allAnswered = quiz.questions.length > 0 && quiz.questions.every((q) => answers[q.id] !== undefined);

  return (
    <Box sx={{ maxWidth: 850, mx: 'auto' }}>
      <Button
        startIcon={<ArrowBackIcon />}
        onClick={() => navigate('/quizzes')}
        sx={{ mb: 2, color: 'text.secondary' }}
      >
        Back to Quizzes
      </Button>

      {/* Quiz Header */}
      <Paper sx={{ p: 4, mb: 4, borderRadius: 3, border: '1px solid #e2e8f0' }}>
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 1.5 }}>
          <Chip label={quiz.disasterType?.name || 'Evaluation'} size="small" color="secondary" sx={{ fontWeight: 700 }} />
          <Typography variant="caption" sx={{ fontWeight: 700, color: 'text.secondary' }}>
            Passing Requirement: {quiz.passingScore}%
          </Typography>
        </Box>
        <Typography variant="h4" sx={{ fontWeight: 800, mb: 1 }}>
          {quiz.title}
        </Typography>
        <Typography variant="body2" color="text.secondary">
          {quiz.description || 'Answer all procedural questions to evaluate your readiness compliance.'}
        </Typography>
      </Paper>

      {/* Quiz Form or Results */}
      {!result ? (
        <Box component="form">
          {quiz.questions.map((q, idx) => (
            <Card key={q.id} sx={{ mb: 3 }}>
              <CardContent sx={{ p: 3.5 }}>
                <Box sx={{ display: 'flex', alignItems: 'center', gap: 1.5, mb: 2 }}>
                  <Chip
                    label={`Q${idx + 1}`}
                    size="small"
                    sx={{ bgcolor: '#0f172a', color: '#fff', fontWeight: 700 }}
                  />
                  <Typography variant="h6" sx={{ fontWeight: 700 }}>
                    {q.questionText}
                  </Typography>
                </Box>

                <FormControl component="fieldset" sx={{ width: '100%', ml: 1 }}>
                  <RadioGroup
                    value={answers[q.id] || ''}
                    onChange={(e) => handleSelectOption(q.id, Number(e.target.value))}
                  >
                    {q.options.map((opt) => (
                      <FormControlLabel
                        key={opt.id}
                        value={opt.id}
                        control={<Radio color="primary" />}
                        label={
                          <Typography variant="body2" sx={{ fontWeight: 500, my: 0.5 }}>
                            {opt.optionText}
                          </Typography>
                        }
                      />
                    ))}
                  </RadioGroup>
                </FormControl>
              </CardContent>
            </Card>
          ))}

          <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', py: 2 }}>
            <Typography variant="body2" color="text.secondary">
              Answered: {Object.keys(answers).length} of {quiz.questions.length} questions
            </Typography>
            <Button
              variant="contained"
              color="primary"
              size="large"
              disabled={!allAnswered || submitting}
              onClick={handleSubmit}
              endIcon={<SendIcon />}
              sx={{ px: 4, py: 1.2, fontWeight: 700 }}
            >
              {submitting ? 'Evaluating Responses...' : 'Submit Assessment'}
            </Button>
          </Box>
        </Box>
      ) : (
        /* Evaluation Results Card */
        <Box>
          <Card sx={{ mb: 4, borderColor: result.passed ? '#bbf7d0' : '#fecaca' }}>
            <CardContent sx={{ p: 4, textAlign: 'center' }}>
              {result.passed ? (
                <CheckCircleIcon sx={{ fontSize: 64, color: 'success.main', mb: 1 }} />
              ) : (
                <CancelIcon sx={{ fontSize: 64, color: 'error.main', mb: 1 }} />
              )}
              <Typography variant="h4" sx={{ fontWeight: 800, mb: 1 }}>
                {result.passed ? 'Assessment Passed!' : 'Assessment Incomplete'}
              </Typography>
              <Typography variant="body1" color="text.secondary" sx={{ mb: 2 }}>
                {result.passed
                  ? 'Congratulations! You have satisfied civil defense educational threshold standards.'
                  : 'You did not achieve the required threshold score. Review the safety modules and retest.'}
              </Typography>

              <Box sx={{ display: 'flex', justifyContent: 'center', gap: 3, my: 3 }}>
                <Paper variant="outlined" sx={{ p: 2, minWidth: 140 }}>
                  <Typography variant="caption" color="text.secondary" sx={{ fontWeight: 700 }}>
                    YOUR SCORE
                  </Typography>
                  <Typography variant="h4" sx={{ fontWeight: 800, color: result.passed ? 'success.main' : 'error.main' }}>
                    {result.score}%
                  </Typography>
                </Paper>
                <Paper variant="outlined" sx={{ p: 2, minWidth: 140 }}>
                  <Typography variant="caption" color="text.secondary" sx={{ fontWeight: 700 }}>
                    CORRECT ANSWERS
                  </Typography>
                  <Typography variant="h4" sx={{ fontWeight: 800 }}>
                    {result.correctAnswers} / {result.totalQuestions}
                  </Typography>
                </Paper>
              </Box>

              <Box sx={{ display: 'flex', justifyContent: 'center', gap: 2 }}>
                <Button variant="outlined" startIcon={<RefreshIcon />} onClick={handleRetake}>
                  Retake Assessment
                </Button>
                <Button variant="contained" color="primary" onClick={() => navigate('/student/dashboard')}>
                  Back to Dashboard
                </Button>
              </Box>
            </CardContent>
          </Card>

          {/* Question Explanations */}
          {result.explanations && (
            <Card sx={{ mb: 4 }}>
              <CardContent sx={{ p: 3 }}>
                <Typography variant="h6" sx={{ fontWeight: 700, mb: 2 }}>
                  Procedural Explanations & Key Insights
                </Typography>
                {quiz.questions.map((q, idx) => (
                  <Box key={q.id} sx={{ mb: 2, p: 2, bgcolor: '#f8fafc', borderRadius: 2 }}>
                    <Typography variant="subtitle2" sx={{ fontWeight: 700, mb: 0.5 }}>
                      Q{idx + 1}: {q.questionText}
                    </Typography>
                    <Typography variant="body2" color="text.secondary">
                      <strong>Civil Defense Rationale:</strong> {result.explanations?.[q.id] || q.explanation}
                    </Typography>
                  </Box>
                ))}
              </CardContent>
            </Card>
          )}
        </Box>
      )}
    </Box>
  );
};
