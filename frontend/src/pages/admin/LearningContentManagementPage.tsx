import React, { useState, useEffect } from 'react';
import {
  Box,
  Typography,
  Tabs,
  Tab,
  Card,
  CardContent,
  Button,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Chip,
  IconButton,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  MenuItem,
  Grid,
  Alert,
  Snackbar,
  CircularProgress,
  FormControlLabel,
  Checkbox,
  Radio,
  RadioGroup,
  Divider,
} from '@mui/material';
import AddIcon from '@mui/icons-material/Add';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';
import SchoolIcon from '@mui/icons-material/School';
import QuizIcon from '@mui/icons-material/Quiz';
import SportsEsportsIcon from '@mui/icons-material/SportsEsports';
import DeleteOutlineIcon from '@mui/icons-material/DeleteOutline';

import { educationService } from '../../services/educationService';
import { adminService } from '../../services/adminService';
import { DisasterType, LearningModule, Quiz, Simulation } from '../../types';
import { SeverityBadge } from '../../components/common/Badges';

export const LearningContentManagementPage: React.FC = () => {
  const [tabIndex, setTabIndex] = useState(0);
  const [disasters, setDisasters] = useState<DisasterType[]>([]);
  const [modules, setModules] = useState<LearningModule[]>([]);
  const [quizzes, setQuizzes] = useState<Quiz[]>([]);
  const [simulations, setSimulations] = useState<Simulation[]>([]);
  const [loading, setLoading] = useState(true);
  const [message, setMessage] = useState<{ text: string; severity: 'success' | 'error' } | null>(null);

  // Dialog States
  const [moduleDialogOpen, setModuleDialogOpen] = useState(false);
  const [quizDialogOpen, setQuizDialogOpen] = useState(false);
  const [simDialogOpen, setSimDialogOpen] = useState(false);
  const [editId, setEditId] = useState<number | null>(null);

  // Module Form
  const [moduleForm, setModuleForm] = useState({
    title: '',
    disasterTypeId: '',
    summary: '',
    content: '',
    estimatedMinutes: 15,
  });

  // Quiz Form
  const [quizForm, setQuizForm] = useState({
    title: '',
    disasterTypeId: '',
    description: '',
    passingScore: 70,
    timeLimitMinutes: 10,
    questions: [
      {
        questionText: '',
        explanation: '',
        points: 10,
        options: [
          { optionText: '', isCorrect: true },
          { optionText: '', isCorrect: false },
          { optionText: '', isCorrect: false },
          { optionText: '', isCorrect: false },
        ],
      },
    ],
  });

  // Simulation Form
  const [simForm, setSimForm] = useState({
    title: '',
    disasterTypeId: '',
    scenarioText: '',
    locationContext: '',
    initialRiskLevel: 'HIGH',
    options: [
      {
        actionText: '',
        consequenceText: '',
        scoreDelta: 30,
        isAppropriate: true,
        safetyFeedback: '',
      },
      {
        actionText: '',
        consequenceText: '',
        scoreDelta: -20,
        isAppropriate: false,
        safetyFeedback: '',
      },
    ],
  });

  const fetchData = async () => {
    setLoading(true);
    try {
      const [dList, mList, qList, sList] = await Promise.all([
        educationService.getAllDisasters(),
        educationService.getModules(),
        educationService.getQuizzes(),
        educationService.getSimulations(),
      ]);
      setDisasters(dList);
      setModules(mList);
      setQuizzes(qList);
      setSimulations(sList);
    } catch {
      setMessage({ text: 'Failed to load content datasets.', severity: 'error' });
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  // -------------------------------------------------------------
  // MODULE HANDLERS
  // -------------------------------------------------------------
  const handleOpenModuleDialog = (m?: LearningModule) => {
    if (m) {
      setEditId(m.id);
      setModuleForm({
        title: m.title,
        disasterTypeId: String(m.disasterTypeId),
        summary: m.summary || '',
        content: m.content || '',
        estimatedMinutes: m.estimatedMinutes || 15,
      });
    } else {
      setEditId(null);
      setModuleForm({
        title: '',
        disasterTypeId: disasters[0] ? String(disasters[0].id) : '',
        summary: '',
        content: '',
        estimatedMinutes: 15,
      });
    }
    setModuleDialogOpen(true);
  };

  const handleSaveModule = async () => {
    try {
      if (!moduleForm.title || !moduleForm.disasterTypeId) {
        setMessage({ text: 'Please fill in all required fields.', severity: 'error' });
        return;
      }
      const payload = {
        title: moduleForm.title,
        disasterTypeId: Number(moduleForm.disasterTypeId),
        summary: moduleForm.summary,
        content: moduleForm.content,
        estimatedMinutes: Number(moduleForm.estimatedMinutes),
        active: true,
      };

      if (editId) {
        await adminService.updateModule(editId, payload, Number(moduleForm.disasterTypeId));
        setMessage({ text: 'Module updated successfully!', severity: 'success' });
      } else {
        await adminService.createModule(payload, Number(moduleForm.disasterTypeId));
        setMessage({ text: 'New learning module published!', severity: 'success' });
      }
      setModuleDialogOpen(false);
      fetchData();
    } catch (err: any) {
      setMessage({ text: err.response?.data?.message || 'Error saving module.', severity: 'error' });
    }
  };

  const handleDeleteModule = async (id: number) => {
    if (!window.confirm('Are you sure you want to delete this module?')) return;
    try {
      await adminService.deleteModule(id);
      setMessage({ text: 'Module deleted.', severity: 'success' });
      fetchData();
    } catch {
      setMessage({ text: 'Failed to delete module.', severity: 'error' });
    }
  };

  // -------------------------------------------------------------
  // QUIZ HANDLERS
  // -------------------------------------------------------------
  const handleOpenQuizDialog = (q?: Quiz) => {
    if (q) {
      setEditId(q.id);
      setQuizForm({
        title: q.title,
        disasterTypeId: String(q.disasterTypeId || q.disasterType?.id || ''),
        description: q.description || '',
        passingScore: q.passingScore || 70,
        timeLimitMinutes: q.timeLimitMinutes || 10,
        questions: q.questions && q.questions.length > 0 ? q.questions.map(qu => ({
          questionText: qu.questionText,
          explanation: qu.explanation || '',
          points: qu.points || 10,
          options: qu.options ? qu.options.map(o => ({ optionText: o.optionText, isCorrect: o.isCorrect })) : [],
        })) : [
          {
            questionText: '',
            explanation: '',
            points: 10,
            options: [
              { optionText: '', isCorrect: true },
              { optionText: '', isCorrect: false },
              { optionText: '', isCorrect: false },
              { optionText: '', isCorrect: false },
            ],
          },
        ],
      });
    } else {
      setEditId(null);
      setQuizForm({
        title: '',
        disasterTypeId: disasters[0] ? String(disasters[0].id) : '',
        description: '',
        passingScore: 70,
        timeLimitMinutes: 10,
        questions: [
          {
            questionText: '',
            explanation: '',
            points: 10,
            options: [
              { optionText: '', isCorrect: true },
              { optionText: '', isCorrect: false },
              { optionText: '', isCorrect: false },
              { optionText: '', isCorrect: false },
            ],
          },
        ],
      });
    }
    setQuizDialogOpen(true);
  };

  const handleAddQuestion = () => {
    setQuizForm({
      ...quizForm,
      questions: [
        ...quizForm.questions,
        {
          questionText: '',
          explanation: '',
          points: 10,
          options: [
            { optionText: '', isCorrect: true },
            { optionText: '', isCorrect: false },
            { optionText: '', isCorrect: false },
            { optionText: '', isCorrect: false },
          ],
        },
      ],
    });
  };

  const handleRemoveQuestion = (idx: number) => {
    const updated = [...quizForm.questions];
    updated.splice(idx, 1);
    setQuizForm({ ...quizForm, questions: updated });
  };

  const handleSaveQuiz = async () => {
    try {
      if (!quizForm.title || !quizForm.disasterTypeId) {
        setMessage({ text: 'Please fill in quiz title and disaster type.', severity: 'error' });
        return;
      }
      const payload = {
        title: quizForm.title,
        disasterTypeId: Number(quizForm.disasterTypeId),
        description: quizForm.description,
        passingScore: Number(quizForm.passingScore),
        timeLimitMinutes: Number(quizForm.timeLimitMinutes),
        active: true,
        questions: quizForm.questions,
      };

      if (editId) {
        await adminService.updateQuiz(editId, payload);
        setMessage({ text: 'Quiz updated successfully!', severity: 'success' });
      } else {
        await adminService.createQuiz(payload);
        setMessage({ text: 'Assessment quiz published!', severity: 'success' });
      }
      setQuizDialogOpen(false);
      fetchData();
    } catch (err: any) {
      setMessage({ text: err.response?.data?.message || 'Error saving quiz.', severity: 'error' });
    }
  };

  const handleDeleteQuiz = async (id: number) => {
    if (!window.confirm('Are you sure you want to delete this quiz?')) return;
    try {
      await adminService.deleteQuiz(id);
      setMessage({ text: 'Quiz deleted.', severity: 'success' });
      fetchData();
    } catch {
      setMessage({ text: 'Failed to delete quiz.', severity: 'error' });
    }
  };

  // -------------------------------------------------------------
  // SIMULATION HANDLERS
  // -------------------------------------------------------------
  const handleOpenSimDialog = (s?: Simulation) => {
    if (s) {
      setEditId(s.id);
      setSimForm({
        title: s.title,
        disasterTypeId: String(s.disasterTypeId || s.disasterType?.id || ''),
        scenarioText: s.scenarioText,
        locationContext: s.locationContext || '',
        initialRiskLevel: s.initialRiskLevel || 'HIGH',
        options: s.options && s.options.length > 0 ? s.options.map(o => ({
          actionText: o.actionText,
          consequenceText: o.consequenceText || '',
          scoreDelta: o.scoreDelta,
          isAppropriate: o.isAppropriate ?? o.appropriate ?? false,
          safetyFeedback: o.safetyFeedback || '',
        })) : [
          { actionText: '', consequenceText: '', scoreDelta: 30, isAppropriate: true, safetyFeedback: '' },
          { actionText: '', consequenceText: '', scoreDelta: -20, isAppropriate: false, safetyFeedback: '' },
        ],
      });
    } else {
      setEditId(null);
      setSimForm({
        title: '',
        disasterTypeId: disasters[0] ? String(disasters[0].id) : '',
        scenarioText: '',
        locationContext: '',
        initialRiskLevel: 'HIGH',
        options: [
          { actionText: '', consequenceText: '', scoreDelta: 30, isAppropriate: true, safetyFeedback: '' },
          { actionText: '', consequenceText: '', scoreDelta: -20, isAppropriate: false, safetyFeedback: '' },
        ],
      });
    }
    setSimDialogOpen(true);
  };

  const handleAddSimOption = () => {
    setSimForm({
      ...simForm,
      options: [
        ...simForm.options,
        { actionText: '', consequenceText: '', scoreDelta: 10, isAppropriate: true, safetyFeedback: '' },
      ],
    });
  };

  const handleRemoveSimOption = (idx: number) => {
    const updated = [...simForm.options];
    updated.splice(idx, 1);
    setSimForm({ ...simForm, options: updated });
  };

  const handleSaveSimulation = async () => {
    try {
      if (!simForm.title || !simForm.disasterTypeId || !simForm.scenarioText) {
        setMessage({ text: 'Please fill in title, disaster type, and scenario text.', severity: 'error' });
        return;
      }
      const payload = {
        title: simForm.title,
        disasterTypeId: Number(simForm.disasterTypeId),
        scenarioText: simForm.scenarioText,
        locationContext: simForm.locationContext,
        initialRiskLevel: simForm.initialRiskLevel,
        active: true,
        options: simForm.options,
      };

      if (editId) {
        await adminService.updateSimulation(editId, payload);
        setMessage({ text: 'Simulation updated successfully!', severity: 'success' });
      } else {
        await adminService.createSimulation(payload);
        setMessage({ text: 'Crisis simulation published!', severity: 'success' });
      }
      setSimDialogOpen(false);
      fetchData();
    } catch (err: any) {
      setMessage({ text: err.response?.data?.message || 'Error saving simulation.', severity: 'error' });
    }
  };

  const handleDeleteSimulation = async (id: number) => {
    if (!window.confirm('Are you sure you want to delete this simulation?')) return;
    try {
      await adminService.deleteSimulation(id);
      setMessage({ text: 'Simulation deleted.', severity: 'success' });
      fetchData();
    } catch {
      setMessage({ text: 'Failed to delete simulation.', severity: 'error' });
    }
  };

  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', py: 8 }}>
        <CircularProgress />
      </Box>
    );
  }

  return (
    <Box>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3, flexWrap: 'wrap', gap: 2 }}>
        <Box>
          <Typography variant="h4" sx={{ fontWeight: 800 }}>
            Learning Content & Curriculum Command
          </Typography>
          <Typography variant="body2" color="text.secondary">
            Publish, edit, and govern disaster education modules, assessment quizzes, and interactive simulations.
          </Typography>
        </Box>
        <Box>
          {tabIndex === 0 && (
            <Button variant="contained" color="primary" startIcon={<AddIcon />} onClick={() => handleOpenModuleDialog()}>
              Add Learning Module
            </Button>
          )}
          {tabIndex === 1 && (
            <Button variant="contained" color="secondary" startIcon={<AddIcon />} onClick={() => handleOpenQuizDialog()}>
              Add Assessment Quiz
            </Button>
          )}
          {tabIndex === 2 && (
            <Button variant="contained" color="warning" startIcon={<AddIcon />} onClick={() => handleOpenSimDialog()}>
              Add Crisis Simulation
            </Button>
          )}
        </Box>
      </Box>

      {message && (
        <Alert severity={message.severity} onClose={() => setMessage(null)} sx={{ mb: 3 }}>
          {message.text}
        </Alert>
      )}

      <Paper sx={{ mb: 3, borderRadius: 2 }}>
        <Tabs value={tabIndex} onChange={(_, val) => setTabIndex(val)} indicatorColor="primary" textColor="primary">
          <Tab icon={<SchoolIcon />} iconPosition="start" label={`Learning Modules (${modules.length})`} />
          <Tab icon={<QuizIcon />} iconPosition="start" label={`Assessment Quizzes (${quizzes.length})`} />
          <Tab icon={<SportsEsportsIcon />} iconPosition="start" label={`Crisis Simulations (${simulations.length})`} />
        </Tabs>
      </Paper>

      {/* ------------------------------------------------------------- */}
      {/* TAB 1: MODULES TABLE */}
      {/* ------------------------------------------------------------- */}
      {tabIndex === 0 && (
        <TableContainer component={Paper} sx={{ borderRadius: 2 }}>
          <Table>
            <TableHead sx={{ bgcolor: '#f8fafc' }}>
              <TableRow>
                <TableCell sx={{ fontWeight: 700 }}>ID</TableCell>
                <TableCell sx={{ fontWeight: 700 }}>Module Title</TableCell>
                <TableCell sx={{ fontWeight: 700 }}>Disaster Type</TableCell>
                <TableCell sx={{ fontWeight: 700 }}>Duration</TableCell>
                <TableCell sx={{ fontWeight: 700 }}>Summary</TableCell>
                <TableCell align="right" sx={{ fontWeight: 700 }}>Actions</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {modules.map((m) => (
                <TableRow key={m.id} hover>
                  <TableCell>{m.id}</TableCell>
                  <TableCell sx={{ fontWeight: 600 }}>{m.title}</TableCell>
                  <TableCell>
                    <Chip label={m.disasterTypeName} size="small" />
                  </TableCell>
                  <TableCell>{m.estimatedMinutes} mins</TableCell>
                  <TableCell sx={{ maxWidth: 350, whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' }}>
                    {m.summary}
                  </TableCell>
                  <TableCell align="right">
                    <IconButton color="primary" onClick={() => handleOpenModuleDialog(m)} size="small">
                      <EditIcon fontSize="small" />
                    </IconButton>
                    <IconButton color="error" onClick={() => handleDeleteModule(m.id)} size="small">
                      <DeleteIcon fontSize="small" />
                    </IconButton>
                  </TableCell>
                </TableRow>
              ))}
              {modules.length === 0 && (
                <TableRow>
                  <TableCell colSpan={6} align="center" sx={{ py: 4, color: 'text.secondary' }}>
                    No learning modules found. Click "Add Learning Module" to create one.
                  </TableCell>
                </TableRow>
              )}
            </TableBody>
          </Table>
        </TableContainer>
      )}

      {/* ------------------------------------------------------------- */}
      {/* TAB 2: QUIZZES TABLE */}
      {/* ------------------------------------------------------------- */}
      {tabIndex === 1 && (
        <TableContainer component={Paper} sx={{ borderRadius: 2 }}>
          <Table>
            <TableHead sx={{ bgcolor: '#f8fafc' }}>
              <TableRow>
                <TableCell sx={{ fontWeight: 700 }}>ID</TableCell>
                <TableCell sx={{ fontWeight: 700 }}>Quiz Assessment</TableCell>
                <TableCell sx={{ fontWeight: 700 }}>Disaster Category</TableCell>
                <TableCell sx={{ fontWeight: 700 }}>Passing Score</TableCell>
                <TableCell sx={{ fontWeight: 700 }}>Time Limit</TableCell>
                <TableCell sx={{ fontWeight: 700 }}>Questions</TableCell>
                <TableCell align="right" sx={{ fontWeight: 700 }}>Actions</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {quizzes.map((q) => (
                <TableRow key={q.id} hover>
                  <TableCell>{q.id}</TableCell>
                  <TableCell sx={{ fontWeight: 600 }}>{q.title}</TableCell>
                  <TableCell>
                    <Chip label={q.disasterTypeName || q.disasterType?.name || `Type #${q.disasterTypeId || q.disasterType?.id || ''}`} size="small" />
                  </TableCell>
                  <TableCell>{q.passingScore}%</TableCell>
                  <TableCell>{q.timeLimitMinutes} mins</TableCell>
                  <TableCell>{q.questions ? q.questions.length : 'N/A'}</TableCell>
                  <TableCell align="right">
                    <IconButton color="secondary" onClick={() => handleOpenQuizDialog(q)} size="small">
                      <EditIcon fontSize="small" />
                    </IconButton>
                    <IconButton color="error" onClick={() => handleDeleteQuiz(q.id)} size="small">
                      <DeleteIcon fontSize="small" />
                    </IconButton>
                  </TableCell>
                </TableRow>
              ))}
              {quizzes.length === 0 && (
                <TableRow>
                  <TableCell colSpan={7} align="center" sx={{ py: 4, color: 'text.secondary' }}>
                    No assessment quizzes found. Click "Add Assessment Quiz" to publish one.
                  </TableCell>
                </TableRow>
              )}
            </TableBody>
          </Table>
        </TableContainer>
      )}

      {/* ------------------------------------------------------------- */}
      {/* TAB 3: SIMULATIONS TABLE */}
      {/* ------------------------------------------------------------- */}
      {tabIndex === 2 && (
        <TableContainer component={Paper} sx={{ borderRadius: 2 }}>
          <Table>
            <TableHead sx={{ bgcolor: '#f8fafc' }}>
              <TableRow>
                <TableCell sx={{ fontWeight: 700 }}>ID</TableCell>
                <TableCell sx={{ fontWeight: 700 }}>Simulation Scenario</TableCell>
                <TableCell sx={{ fontWeight: 700 }}>Disaster Type</TableCell>
                <TableCell sx={{ fontWeight: 700 }}>Initial Risk</TableCell>
                <TableCell sx={{ fontWeight: 700 }}>Location Context</TableCell>
                <TableCell sx={{ fontWeight: 700 }}>Choices</TableCell>
                <TableCell align="right" sx={{ fontWeight: 700 }}>Actions</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {simulations.map((s) => (
                <TableRow key={s.id} hover>
                  <TableCell>{s.id}</TableCell>
                  <TableCell sx={{ fontWeight: 600 }}>{s.title}</TableCell>
                  <TableCell>
                    <Chip label={s.disasterTypeName || s.disasterType?.name || `Type #${s.disasterTypeId || s.disasterType?.id || ''}`} size="small" />
                  </TableCell>
                  <TableCell>
                    <SeverityBadge severity={s.initialRiskLevel} />
                  </TableCell>
                  <TableCell>{s.locationContext || 'Campus Facility'}</TableCell>
                  <TableCell>{s.options ? s.options.length : 0}</TableCell>
                  <TableCell align="right">
                    <IconButton color="warning" onClick={() => handleOpenSimDialog(s)} size="small">
                      <EditIcon fontSize="small" />
                    </IconButton>
                    <IconButton color="error" onClick={() => handleDeleteSimulation(s.id)} size="small">
                      <DeleteIcon fontSize="small" />
                    </IconButton>
                  </TableCell>
                </TableRow>
              ))}
              {simulations.length === 0 && (
                <TableRow>
                  <TableCell colSpan={7} align="center" sx={{ py: 4, color: 'text.secondary' }}>
                    No crisis simulations found. Click "Add Crisis Simulation" to publish one.
                  </TableCell>
                </TableRow>
              )}
            </TableBody>
          </Table>
        </TableContainer>
      )}

      {/* ------------------------------------------------------------- */}
      {/* DIALOG 1: MODULE FORM */}
      {/* ------------------------------------------------------------- */}
      <Dialog open={moduleDialogOpen} onClose={() => setModuleDialogOpen(false)} maxWidth="md" fullWidth>
        <DialogTitle sx={{ fontWeight: 700 }}>
          {editId ? 'Edit Learning Module' : 'Create New Learning Module'}
        </DialogTitle>
        <DialogContent dividers>
          <Grid container spacing={2.5}>
            <Grid item xs={12} sm={8}>
              <TextField
                label="Module Title"
                fullWidth
                required
                value={moduleForm.title}
                onChange={(e) => setModuleForm({ ...moduleForm, title: e.target.value })}
              />
            </Grid>
            <Grid item xs={12} sm={4}>
              <TextField
                select
                label="Disaster Type"
                fullWidth
                required
                value={moduleForm.disasterTypeId}
                onChange={(e) => setModuleForm({ ...moduleForm, disasterTypeId: e.target.value })}
              >
                {disasters.map((d) => (
                  <MenuItem key={d.id} value={d.id}>
                    {d.name} ({d.code})
                  </MenuItem>
                ))}
              </TextField>
            </Grid>
            <Grid item xs={12} sm={4}>
              <TextField
                label="Estimated Reading Minutes"
                type="number"
                fullWidth
                value={moduleForm.estimatedMinutes}
                onChange={(e) => setModuleForm({ ...moduleForm, estimatedMinutes: Number(e.target.value) })}
              />
            </Grid>
            <Grid item xs={12} sm={8}>
              <TextField
                label="Executive Summary"
                fullWidth
                multiline
                rows={2}
                value={moduleForm.summary}
                onChange={(e) => setModuleForm({ ...moduleForm, summary: e.target.value })}
              />
            </Grid>
            <Grid item xs={12}>
              <TextField
                label="Detailed Module Content (Markdown & Procedures)"
                fullWidth
                multiline
                rows={8}
                value={moduleForm.content}
                onChange={(e) => setModuleForm({ ...moduleForm, content: e.target.value })}
                placeholder="### 1. Pre-Incident Preparation&#10;- Keep go-bags ready&#10;### 2. Tactical Response&#10;..."
              />
            </Grid>
          </Grid>
        </DialogContent>
        <DialogActions sx={{ p: 2.5 }}>
          <Button onClick={() => setModuleDialogOpen(false)}>Cancel</Button>
          <Button variant="contained" color="primary" onClick={handleSaveModule}>
            {editId ? 'Update Module' : 'Publish Module'}
          </Button>
        </DialogActions>
      </Dialog>

      {/* ------------------------------------------------------------- */}
      {/* DIALOG 2: QUIZ FORM */}
      {/* ------------------------------------------------------------- */}
      <Dialog open={quizDialogOpen} onClose={() => setQuizDialogOpen(false)} maxWidth="md" fullWidth>
        <DialogTitle sx={{ fontWeight: 700 }}>
          {editId ? 'Edit Assessment Quiz' : 'Create New Assessment Quiz'}
        </DialogTitle>
        <DialogContent dividers>
          <Grid container spacing={2.5}>
            <Grid item xs={12} sm={8}>
              <TextField
                label="Quiz Title"
                fullWidth
                required
                value={quizForm.title}
                onChange={(e) => setQuizForm({ ...quizForm, title: e.target.value })}
              />
            </Grid>
            <Grid item xs={12} sm={4}>
              <TextField
                select
                label="Disaster Type"
                fullWidth
                required
                value={quizForm.disasterTypeId}
                onChange={(e) => setQuizForm({ ...quizForm, disasterTypeId: e.target.value })}
              >
                {disasters.map((d) => (
                  <MenuItem key={d.id} value={d.id}>
                    {d.name}
                  </MenuItem>
                ))}
              </TextField>
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                label="Passing Score (%)"
                type="number"
                fullWidth
                value={quizForm.passingScore}
                onChange={(e) => setQuizForm({ ...quizForm, passingScore: Number(e.target.value) })}
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                label="Time Limit (Minutes)"
                type="number"
                fullWidth
                value={quizForm.timeLimitMinutes}
                onChange={(e) => setQuizForm({ ...quizForm, timeLimitMinutes: Number(e.target.value) })}
              />
            </Grid>
            <Grid item xs={12}>
              <TextField
                label="Quiz Description"
                fullWidth
                multiline
                rows={2}
                value={quizForm.description}
                onChange={(e) => setQuizForm({ ...quizForm, description: e.target.value })}
              />
            </Grid>
          </Grid>

          <Divider sx={{ my: 3 }} />

          {/* Dynamic Questions Builder */}
          <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
            <Typography variant="h6" sx={{ fontWeight: 700 }}>
              Assessment Questions ({quizForm.questions.length})
            </Typography>
            <Button size="small" startIcon={<AddIcon />} variant="outlined" onClick={handleAddQuestion}>
              Add Question
            </Button>
          </Box>

          {quizForm.questions.map((q, qIdx) => (
            <Paper key={qIdx} variant="outlined" sx={{ p: 2.5, mb: 2.5, bgcolor: '#fbfcfd' }}>
              <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 1.5 }}>
                <Typography variant="subtitle2" sx={{ fontWeight: 700 }}>
                  Question #{qIdx + 1}
                </Typography>
                {quizForm.questions.length > 1 && (
                  <IconButton size="small" color="error" onClick={() => handleRemoveQuestion(qIdx)}>
                    <DeleteOutlineIcon fontSize="small" />
                  </IconButton>
                )}
              </Box>

              <TextField
                label="Question Prompt"
                fullWidth
                required
                sx={{ mb: 2 }}
                value={q.questionText}
                onChange={(e) => {
                  const updated = [...quizForm.questions];
                  updated[qIdx].questionText = e.target.value;
                  setQuizForm({ ...quizForm, questions: updated });
                }}
              />

              <TextField
                label="Answer Explanation"
                fullWidth
                size="small"
                sx={{ mb: 2 }}
                value={q.explanation}
                onChange={(e) => {
                  const updated = [...quizForm.questions];
                  updated[qIdx].explanation = e.target.value;
                  setQuizForm({ ...quizForm, questions: updated });
                }}
              />

              <Typography variant="caption" sx={{ fontWeight: 700, display: 'block', mb: 1 }}>
                Select the single correct answer radio button:
              </Typography>

              {q.options.map((opt, optIdx) => (
                <Box key={optIdx} sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 1 }}>
                  <Radio
                    checked={opt.isCorrect}
                    onChange={() => {
                      const updated = [...quizForm.questions];
                      updated[qIdx].options = updated[qIdx].options.map((o, idx) => ({
                        ...o,
                        isCorrect: idx === optIdx,
                      }));
                      setQuizForm({ ...quizForm, questions: updated });
                    }}
                  />
                  <TextField
                    size="small"
                    placeholder={`Option ${optIdx + 1}`}
                    fullWidth
                    value={opt.optionText}
                    onChange={(e) => {
                      const updated = [...quizForm.questions];
                      updated[qIdx].options[optIdx].optionText = e.target.value;
                      setQuizForm({ ...quizForm, questions: updated });
                    }}
                  />
                </Box>
              ))}
            </Paper>
          ))}
        </DialogContent>
        <DialogActions sx={{ p: 2.5 }}>
          <Button onClick={() => setQuizDialogOpen(false)}>Cancel</Button>
          <Button variant="contained" color="secondary" onClick={handleSaveQuiz}>
            {editId ? 'Update Quiz' : 'Publish Quiz'}
          </Button>
        </DialogActions>
      </Dialog>

      {/* ------------------------------------------------------------- */}
      {/* DIALOG 3: SIMULATION FORM */}
      {/* ------------------------------------------------------------- */}
      <Dialog open={simDialogOpen} onClose={() => setSimDialogOpen(false)} maxWidth="md" fullWidth>
        <DialogTitle sx={{ fontWeight: 700 }}>
          {editId ? 'Edit Crisis Simulation' : 'Create New Crisis Simulation'}
        </DialogTitle>
        <DialogContent dividers>
          <Grid container spacing={2.5}>
            <Grid item xs={12} sm={8}>
              <TextField
                label="Simulation Title"
                fullWidth
                required
                value={simForm.title}
                onChange={(e) => setSimForm({ ...simForm, title: e.target.value })}
              />
            </Grid>
            <Grid item xs={12} sm={4}>
              <TextField
                select
                label="Disaster Type"
                fullWidth
                required
                value={simForm.disasterTypeId}
                onChange={(e) => setSimForm({ ...simForm, disasterTypeId: e.target.value })}
              >
                {disasters.map((d) => (
                  <MenuItem key={d.id} value={d.id}>
                    {d.name}
                  </MenuItem>
                ))}
              </TextField>
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                label="Location Context"
                fullWidth
                placeholder="e.g. Science Complex, 3rd Floor"
                value={simForm.locationContext}
                onChange={(e) => setSimForm({ ...simForm, locationContext: e.target.value })}
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                select
                label="Initial Risk Level"
                fullWidth
                value={simForm.initialRiskLevel}
                onChange={(e) => setSimForm({ ...simForm, initialRiskLevel: e.target.value })}
              >
                <MenuItem value="LOW">LOW</MenuItem>
                <MenuItem value="MEDIUM">MEDIUM</MenuItem>
                <MenuItem value="HIGH">HIGH</MenuItem>
                <MenuItem value="CRITICAL">CRITICAL</MenuItem>
              </TextField>
            </Grid>
            <Grid item xs={12}>
              <TextField
                label="Scenario Crisis Narrative"
                fullWidth
                required
                multiline
                rows={3}
                value={simForm.scenarioText}
                onChange={(e) => setSimForm({ ...simForm, scenarioText: e.target.value })}
              />
            </Grid>
          </Grid>

          <Divider sx={{ my: 3 }} />

          {/* Dynamic Choices Builder */}
          <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
            <Typography variant="h6" sx={{ fontWeight: 700 }}>
              Tactical Response Choices ({simForm.options.length})
            </Typography>
            <Button size="small" startIcon={<AddIcon />} variant="outlined" onClick={handleAddSimOption}>
              Add Choice
            </Button>
          </Box>

          {simForm.options.map((opt, oIdx) => (
            <Paper key={oIdx} variant="outlined" sx={{ p: 2.5, mb: 2.5, bgcolor: '#fbfcfd' }}>
              <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 1.5 }}>
                <Typography variant="subtitle2" sx={{ fontWeight: 700 }}>
                  Tactical Action #{oIdx + 1}
                </Typography>
                {simForm.options.length > 2 && (
                  <IconButton size="small" color="error" onClick={() => handleRemoveSimOption(oIdx)}>
                    <DeleteOutlineIcon fontSize="small" />
                  </IconButton>
                )}
              </Box>

              <TextField
                label="Decision Action Text"
                fullWidth
                required
                sx={{ mb: 1.5 }}
                value={opt.actionText}
                onChange={(e) => {
                  const updated = [...simForm.options];
                  updated[oIdx].actionText = e.target.value;
                  setSimForm({ ...simForm, options: updated });
                }}
              />

              <Grid container spacing={2} sx={{ mb: 1.5 }}>
                <Grid item xs={12} sm={8}>
                  <TextField
                    label="Immediate Outcome / Consequence"
                    fullWidth
                    size="small"
                    value={opt.consequenceText}
                    onChange={(e) => {
                      const updated = [...simForm.options];
                      updated[oIdx].consequenceText = e.target.value;
                      setSimForm({ ...simForm, options: updated });
                    }}
                  />
                </Grid>
                <Grid item xs={12} sm={4}>
                  <TextField
                    label="Score Delta (e.g. +30, -20)"
                    type="number"
                    fullWidth
                    size="small"
                    value={opt.scoreDelta}
                    onChange={(e) => {
                      const updated = [...simForm.options];
                      updated[oIdx].scoreDelta = Number(e.target.value);
                      setSimForm({ ...simForm, options: updated });
                    }}
                  />
                </Grid>
              </Grid>

              <Grid container spacing={2} alignItems="center">
                <Grid item xs={12} sm={8}>
                  <TextField
                    label="Safety Debrief & Feedback"
                    fullWidth
                    size="small"
                    value={opt.safetyFeedback}
                    onChange={(e) => {
                      const updated = [...simForm.options];
                      updated[oIdx].safetyFeedback = e.target.value;
                      setSimForm({ ...simForm, options: updated });
                    }}
                  />
                </Grid>
                <Grid item xs={12} sm={4}>
                  <FormControlLabel
                    control={
                      <Checkbox
                        checked={opt.isAppropriate}
                        onChange={(e) => {
                          const updated = [...simForm.options];
                          updated[oIdx].isAppropriate = e.target.checked;
                          setSimForm({ ...simForm, options: updated });
                        }}
                      />
                    }
                    label="Safe / Correct Choice"
                  />
                </Grid>
              </Grid>
            </Paper>
          ))}
        </DialogContent>
        <DialogActions sx={{ p: 2.5 }}>
          <Button onClick={() => setSimDialogOpen(false)}>Cancel</Button>
          <Button variant="contained" color="warning" onClick={handleSaveSimulation}>
            {editId ? 'Update Simulation' : 'Publish Simulation'}
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};
