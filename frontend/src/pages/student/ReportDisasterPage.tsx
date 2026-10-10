import React, { useState, useEffect } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import {
  Box,
  Typography,
  Card,
  CardContent,
  Grid,
  TextField,
  MenuItem,
  Button,
  Paper,
  Alert,
  Divider,
  Chip,
  IconButton,
  List,
  ListItem,
  ListItemText,
  ListItemIcon,
} from '@mui/material';
import MyLocationIcon from '@mui/icons-material/MyLocation';
import CloudUploadIcon from '@mui/icons-material/CloudUpload';
import DeleteOutlineIcon from '@mui/icons-material/DeleteOutline';
import CheckCircleOutlineIcon from '@mui/icons-material/CheckCircleOutline';
import InsertDriveFileIcon from '@mui/icons-material/InsertDriveFile';
import AddAlertIcon from '@mui/icons-material/AddAlert';
import { educationService } from '../../services/educationService';
import { reportService } from '../../services/reportService';
import { DisasterType, DisasterReport, SeverityLevel } from '../../types';

export const ReportDisasterPage: React.FC = () => {
  const navigate = useNavigate();

  const [disasterTypes, setDisasterTypes] = useState<DisasterType[]>([]);
  const [disasterTypeId, setDisasterTypeId] = useState<number | ''>('');
  const [severity, setSeverity] = useState<SeverityLevel>('HIGH');
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [address, setAddress] = useState('');
  const [latitude, setLatitude] = useState<number>(37.7749);
  const [longitude, setLongitude] = useState<number>(-122.4194);
  const [aftermath, setAftermath] = useState('');
  const [contactPhone, setContactPhone] = useState('');

  const [files, setFiles] = useState<File[]>([]);
  const [locating, setLocating] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [successReport, setSuccessReport] = useState<DisasterReport | null>(null);

  useEffect(() => {
    const fetchTypes = async () => {
      try {
        const types = await educationService.getAllDisasters();
        setDisasterTypes(types);
        if (types.length > 0) {
          setDisasterTypeId(types[0].id);
        }
      } catch (err) {
        console.error(err);
      }
    };
    fetchTypes();
  }, []);

  const handleGetCurrentLocation = () => {
    if (!navigator.geolocation) {
      setError('Geolocation is not supported by your current browser.');
      return;
    }
    setLocating(true);
    navigator.geolocation.getCurrentPosition(
      (position) => {
        setLatitude(Number(position.coords.latitude.toFixed(6)));
        setLongitude(Number(position.coords.longitude.toFixed(6)));
        setLocating(false);
      },
      () => {
        setError('Unable to retrieve your current location. Please verify device permissions or enter manually.');
        setLocating(false);
      }
    );
  };

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files) {
      const selected = Array.from(e.target.files);
      setFiles((prev) => [...prev, ...selected]);
    }
  };

  const handleRemoveFile = (index: number) => {
    setFiles((prev) => prev.filter((_, i) => i !== index));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!disasterTypeId) return;
    setError(null);
    setSubmitting(true);

    try {
      // 1. Submit Report
      const report = await reportService.submitReport({
        disasterTypeId: Number(disasterTypeId),
        severity,
        title,
        description,
        address,
        latitude,
        longitude,
        aftermath,
        contactPhone,
      });

      // 2. Upload any selected evidence files
      for (const file of files) {
        try {
          await reportService.uploadEvidence(report.id, file);
        } catch (uploadErr) {
          console.warn('Evidence upload failed for file', file.name, uploadErr);
        }
      }

      setSuccessReport(report);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to submit disaster report.');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <Box sx={{ maxWidth: 900, mx: 'auto' }}>
      {!successReport ? (
        <>
          <Box sx={{ mb: 4 }}>
            <Typography variant="overline" sx={{ color: 'error.main', fontWeight: 800 }}>
              EMERGENCY FIELD DISPATCH
            </Typography>
            <Typography variant="h4" sx={{ fontWeight: 800 }}>
              Submit Citizen Disaster Report
            </Typography>
            <Typography variant="body1" color="text.secondary">
              Report live community hazards directly into the national emergency response coordination network. Every submission receives a tracking ID and undergoes government officer verification.
            </Typography>
          </Box>

          {error && <Alert severity="error" sx={{ mb: 3 }}>{error}</Alert>}

          <Paper component="form" onSubmit={handleSubmit} sx={{ p: { xs: 3, md: 5 }, borderRadius: 3, border: '1px solid #e2e8f0' }}>
            {/* SECTION 1: INCIDENT DETAILS */}
            <Typography variant="h6" sx={{ fontWeight: 700, mb: 1, color: '#0f172a' }}>
              1. Incident Details
            </Typography>
            <Divider sx={{ mb: 3 }} />

            <Grid container spacing={2.5} sx={{ mb: 3 }}>
              <Grid item xs={12} sm={6}>
                <TextField
                  select
                  label="Disaster Type"
                  fullWidth
                  required
                  value={disasterTypeId}
                  onChange={(e) => setDisasterTypeId(Number(e.target.value))}
                >
                  {disasterTypes.map((type) => (
                    <MenuItem key={type.id} value={type.id}>
                      {type.name} ({type.code})
                    </MenuItem>
                  ))}
                </TextField>
              </Grid>

              <Grid item xs={12} sm={6}>
                <TextField
                  select
                  label="Observed Severity Level"
                  fullWidth
                  required
                  value={severity}
                  onChange={(e) => setSeverity(e.target.value as SeverityLevel)}
                >
                  <MenuItem value="LOW">Low (Minor local inconvenience)</MenuItem>
                  <MenuItem value="MEDIUM">Medium (Moderate road/property damage)</MenuItem>
                  <MenuItem value="HIGH">High (Active danger / partial infrastructure collapse)</MenuItem>
                  <MenuItem value="CRITICAL">Critical (Immediate danger to life / widespread devastation)</MenuItem>
                </TextField>
              </Grid>

              <Grid item xs={12}>
                <TextField
                  label="Incident Headline / Summary"
                  fullWidth
                  required
                  placeholder="e.g. Submerged Underpass with Stalled Vehicles on 5th Avenue"
                  value={title}
                  onChange={(e) => setTitle(e.target.value)}
                />
              </Grid>

              <Grid item xs={12}>
                <TextField
                  label="Comprehensive Incident Description"
                  fullWidth
                  required
                  multiline
                  rows={4}
                  placeholder="Provide precise details: current conditions, visible casualties, fire or chemical presence, electrical sparks, building damage..."
                  value={description}
                  onChange={(e) => setDescription(e.target.value)}
                />
              </Grid>

              <Grid item xs={12}>
                <TextField
                  label="Immediate Aftermath / Urgent Assistance Needed"
                  fullWidth
                  multiline
                  rows={2}
                  placeholder="e.g. 15 people cut off on second floor; clean drinking water and medical trauma kit needed urgently."
                  value={aftermath}
                  onChange={(e) => setAftermath(e.target.value)}
                />
              </Grid>
            </Grid>

            {/* SECTION 2: LOCATION */}
            <Typography variant="h6" sx={{ fontWeight: 700, mb: 1, color: '#0f172a' }}>
              2. Geographic Location
            </Typography>
            <Divider sx={{ mb: 3 }} />

            <Grid container spacing={2.5} sx={{ mb: 3 }}>
              <Grid item xs={12}>
                <TextField
                  label="Street Address / Prominent Landmark"
                  fullWidth
                  required
                  placeholder="e.g. Corner of Elm Street & University Boulevard, opposite City Hospital"
                  value={address}
                  onChange={(e) => setAddress(e.target.value)}
                />
              </Grid>

              <Grid item xs={12} sm={5}>
                <TextField
                  label="Latitude"
                  type="number"
                  fullWidth
                  required
                  value={latitude}
                  onChange={(e) => setLatitude(Number(e.target.value))}
                />
              </Grid>

              <Grid item xs={12} sm={5}>
                <TextField
                  label="Longitude"
                  type="number"
                  fullWidth
                  required
                  value={longitude}
                  onChange={(e) => setLongitude(Number(e.target.value))}
                />
              </Grid>

              <Grid item xs={12} sm={2} sx={{ display: 'flex', alignItems: 'center' }}>
                <Button
                  variant="outlined"
                  fullWidth
                  onClick={handleGetCurrentLocation}
                  disabled={locating}
                  startIcon={<MyLocationIcon />}
                  sx={{ height: 54 }}
                >
                  {locating ? 'GPS...' : 'Locate'}
                </Button>
              </Grid>

              <Grid item xs={12} sm={6}>
                <TextField
                  label="Contact Phone Number (Optional for Responders)"
                  fullWidth
                  value={contactPhone}
                  onChange={(e) => setContactPhone(e.target.value)}
                  placeholder="Emergency teams may contact you for directions"
                />
              </Grid>
            </Grid>

            {/* SECTION 3: EVIDENCE ATTACHMENT */}
            <Typography variant="h6" sx={{ fontWeight: 700, mb: 1, color: '#0f172a' }}>
              3. Visual Evidence Upload
            </Typography>
            <Divider sx={{ mb: 3 }} />

            <Paper
              variant="outlined"
              sx={{
                p: 3,
                textAlign: 'center',
                border: '2px dashed #cbd5e1',
                bgcolor: '#f8fafc',
                borderRadius: 2,
                mb: 3,
              }}
            >
              <CloudUploadIcon sx={{ fontSize: 44, color: 'secondary.main', mb: 1 }} />
              <Typography variant="subtitle1" sx={{ fontWeight: 700 }}>
                Attach Images or Videos of Incident
              </Typography>
              <Typography variant="caption" color="text.secondary" sx={{ display: 'block', mb: 2 }}>
                Accepted formats: JPG, PNG, WEBP, MP4 (Max 25MB). Files are securely archived.
              </Typography>
              <Button variant="contained" component="label" color="secondary" size="small">
                Browse Files
                <input type="file" multiple hidden accept="image/*,video/*,.pdf" onChange={handleFileChange} />
              </Button>
            </Paper>

            {files.length > 0 && (
              <List disablePadding sx={{ mb: 3 }}>
                {files.map((file, idx) => (
                  <ListItem
                    key={idx}
                    secondaryAction={
                      <IconButton edge="end" onClick={() => handleRemoveFile(idx)}>
                        <DeleteOutlineIcon color="error" />
                      </IconButton>
                    }
                    sx={{ bgcolor: '#f8fafc', borderRadius: 2, mb: 1 }}
                  >
                    <ListItemIcon>
                      <InsertDriveFileIcon color="primary" />
                    </ListItemIcon>
                    <ListItemText
                      primary={file.name}
                      secondary={`${(file.size / 1024 / 1024).toFixed(2)} MB`}
                    />
                  </ListItem>
                ))}
              </List>
            )}

            <Button
              type="submit"
              variant="contained"
              color="error"
              size="large"
              fullWidth
              disabled={submitting}
              startIcon={<AddAlertIcon />}
              sx={{ py: 1.5, fontWeight: 700, fontSize: '1rem' }}
            >
              {submitting ? 'Transmitting Incident Report...' : 'Transmit Disaster Report'}
            </Button>
          </Paper>
        </>
      ) : (
        /* Successful Submission Receipt */
        <Card sx={{ p: { xs: 3, sm: 5 }, borderRadius: 3, textAlign: 'center', border: '1px solid #bbf7d0' }}>
          <CheckCircleOutlineIcon sx={{ fontSize: 72, color: 'success.main', mb: 2 }} />
          <Typography variant="h4" sx={{ fontWeight: 800, mb: 1 }}>
            Report Submitted Successfully
          </Typography>
          <Typography variant="body1" color="text.secondary" sx={{ mb: 3 }}>
            Your emergency report has been logged into the national public-safety incident registry.
          </Typography>

          <Paper variant="outlined" sx={{ p: 3, maxWidth: 500, mx: 'auto', mb: 4, bgcolor: '#f8fafc' }}>
            <Typography variant="caption" color="text.secondary" sx={{ fontWeight: 700 }}>
              OFFICIAL INCIDENT TRACKING IDENTIFIER
            </Typography>
            <Typography variant="h3" sx={{ fontWeight: 800, color: 'primary.main', my: 1, letterSpacing: '0.04em' }}>
              {successReport.reportCode}
            </Typography>
            <Chip label="STATUS: SUBMITTED" size="small" color="default" sx={{ fontWeight: 700 }} />

            <Divider sx={{ my: 2 }} />
            <Typography variant="body2" color="text.secondary" sx={{ fontStyle: 'italic' }}>
              "Your report will be reviewed by an authorized Government Officer for operational verification and response team assignment."
            </Typography>
          </Paper>

          <Box sx={{ display: 'flex', justifyContent: 'center', gap: 2 }}>
            <Button
              component={Link}
              to={`/reports/${successReport.id}`}
              variant="contained"
              color="primary"
              sx={{ fontWeight: 700 }}
            >
              Track Report Timeline
            </Button>
            <Button
              component={Link}
              to="/student/dashboard"
              variant="outlined"
            >
              Return to Dashboard
            </Button>
          </Box>
        </Card>
      )}
    </Box>
  );
};
