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
  List,
  ListItem,
  ListItemText,
} from '@mui/material';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import PlaceIcon from '@mui/icons-material/Place';
import CalendarMonthIcon from '@mui/icons-material/CalendarMonth';
import CorporateFareIcon from '@mui/icons-material/CorporateFare';
import GroupIcon from '@mui/icons-material/Group';
import ErrorOutlineIcon from '@mui/icons-material/ErrorOutline';
import ImageOutlinedIcon from '@mui/icons-material/ImageOutlined';
import VideocamOutlinedIcon from '@mui/icons-material/VideocamOutlined';
import CheckCircleOutlineIcon from '@mui/icons-material/CheckCircleOutline';
import { reportService } from '../../services/reportService';
import { DisasterReport } from '../../types';
import { LoadingScreen } from '../../components/common/LoadingScreen';
import { StatusBadge, SeverityBadge } from '../../components/common/Badges';

export const ReportDetailPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();

  const [report, setReport] = useState<DisasterReport | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchReport = async () => {
      try {
        if (!id) return;
        const res = await reportService.getReportById(Number(id));
        setReport(res);
      } catch (err: any) {
        setError(err.response?.data?.message || 'Failed to retrieve disaster report details.');
      } finally {
        setLoading(false);
      }
    };
    fetchReport();
  }, [id]);

  if (loading) return <LoadingScreen message="Loading comprehensive incident record..." />;
  if (error || !report) return <Alert severity="error">{error || 'Report not found.'}</Alert>;

  return (
    <Box sx={{ maxWidth: 1050, mx: 'auto' }}>
      <Button
        startIcon={<ArrowBackIcon />}
        onClick={() => navigate(-1)}
        sx={{ mb: 2, color: 'text.secondary' }}
      >
        Back
      </Button>

      {/* Header Banner */}
      <Paper sx={{ p: 4, mb: 4, borderRadius: 3, border: '1px solid #e2e8f0' }}>
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', flexWrap: 'wrap', gap: 2 }}>
          <Box>
            <Box sx={{ display: 'flex', alignItems: 'center', gap: 1.5, mb: 1.5 }}>
              <Chip label={report.reportCode} size="medium" sx={{ fontWeight: 800, bgcolor: '#0f172a', color: '#fff' }} />
              <SeverityBadge severity={report.severity} />
              <StatusBadge status={report.status} />
            </Box>
            <Typography variant="h4" sx={{ fontWeight: 800, mb: 1 }}>
              {report.title}
            </Typography>
            <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 3, color: 'text.secondary', fontSize: '0.875rem' }}>
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5 }}>
                <PlaceIcon fontSize="small" sx={{ color: 'secondary.main' }} />
                {report.address} (GPS: {report.latitude}, {report.longitude})
              </Box>
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5 }}>
                <CalendarMonthIcon fontSize="small" />
                Submitted: {new Date(report.createdAt).toLocaleString()}
              </Box>
            </Box>
          </Box>

          {report.officialVerified && (
            <Chip
              label="OFFICIALLY VERIFIED DISASTER"
              color="secondary"
              icon={<CheckCircleOutlineIcon />}
              sx={{ fontWeight: 700 }}
            />
          )}
        </Box>
      </Paper>

      {/* REJECTION REASON BANNER (If rejected) */}
      {report.status === 'REJECTED' && (
        <Alert severity="error" icon={<ErrorOutlineIcon fontSize="large" />} sx={{ mb: 4, borderRadius: 2 }}>
          <Typography variant="subtitle1" sx={{ fontWeight: 800 }}>
            Incident Report Rejected by Government Officer
          </Typography>
          <Typography variant="body2" sx={{ mt: 0.5 }}>
            <strong>Official Rejection Rationale:</strong> {report.rejectionReason}
          </Typography>
        </Alert>
      )}

      {/* Main Grid: Details on Left, Timeline & Department on Right */}
      <Grid container spacing={3}>
        <Grid item xs={12} md={7}>
          {/* Detailed Narrative */}
          <Card sx={{ mb: 3 }}>
            <CardContent sx={{ p: 3 }}>
              <Typography variant="h6" sx={{ fontWeight: 700, mb: 2 }}>
                Incident Description & Field Observations
              </Typography>
              <Typography variant="body1" sx={{ whiteSpace: 'pre-line', lineHeight: 1.8, color: '#334155', mb: 3 }}>
                {report.description}
              </Typography>

              {report.aftermath && (
                <>
                  <Typography variant="subtitle2" sx={{ fontWeight: 700, color: 'text.secondary', mb: 1 }}>
                    AFTERMATH & IMMEDIATE NEEDS:
                  </Typography>
                  <Paper variant="outlined" sx={{ p: 2, bgcolor: '#f8fafc', borderRadius: 2 }}>
                    <Typography variant="body2" sx={{ color: 'text.primary' }}>
                      {report.aftermath}
                    </Typography>
                  </Paper>
                </>
              )}
            </CardContent>
          </Card>

          {/* Visual Evidence Section */}
          <Card sx={{ mb: 3 }}>
            <CardContent sx={{ p: 3 }}>
              <Typography variant="h6" sx={{ fontWeight: 700, mb: 2 }}>
                Visual Evidence Repository ({report.evidences?.length || 0})
              </Typography>

              {report.evidences && report.evidences.length > 0 ? (
                <Grid container spacing={2}>
                  {report.evidences.map((ev) => (
                    <Grid item xs={12} sm={6} key={ev.id}>
                      <Paper variant="outlined" sx={{ p: 2, borderRadius: 2 }}>
                        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 1 }}>
                          {ev.fileCategory === 'VIDEO' ? (
                            <VideocamOutlinedIcon color="error" />
                          ) : (
                            <ImageOutlinedIcon color="primary" />
                          )}
                          <Typography variant="caption" noWrap sx={{ fontWeight: 700, flex: 1 }}>
                            {ev.originalFileName}
                          </Typography>
                        </Box>
                        {ev.fileCategory === 'IMAGE' ? (
                          <Box
                            component="img"
                            src={ev.fileUrl}
                            alt={ev.originalFileName}
                            sx={{
                              width: '100%',
                              height: 160,
                              objectFit: 'cover',
                              borderRadius: 1,
                              border: '1px solid #e2e8f0',
                            }}
                          />
                        ) : (
                          <Box sx={{ p: 4, textAlign: 'center', bgcolor: '#f1f5f9', borderRadius: 1 }}>
                            <Button variant="outlined" href={ev.fileUrl} target="_blank" size="small">
                              Stream Media
                            </Button>
                          </Box>
                        )}
                        <Typography variant="caption" color="text.secondary" sx={{ display: 'block', mt: 1 }}>
                          Uploaded: {new Date(ev.uploadedAt).toLocaleDateString()}
                        </Typography>
                      </Paper>
                    </Grid>
                  ))}
                </Grid>
              ) : (
                <Typography variant="body2" color="text.secondary">
                  No images or video recordings were attached to this report.
                </Typography>
              )}
            </CardContent>
          </Card>
        </Grid>

        {/* Right Column: Assigned Response & Status Timeline */}
        <Grid item xs={12} md={5}>
          {/* Assignment Card */}
          {report.assignedDepartmentName && (
            <Card sx={{ mb: 3, border: '1px solid #0284c7', bgcolor: '#f0f9ff' }}>
              <CardContent sx={{ p: 3 }}>
                <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 1.5 }}>
                  <CorporateFareIcon color="secondary" />
                  <Typography variant="h6" sx={{ fontWeight: 700, color: '#0369a1' }}>
                    Assigned Response Unit
                  </Typography>
                </Box>
                <Typography variant="subtitle1" sx={{ fontWeight: 800 }}>
                  {report.assignedDepartmentName}
                </Typography>

                {report.assignedTeamName && (
                  <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5, mt: 0.5, mb: 1.5 }}>
                    <GroupIcon fontSize="small" sx={{ color: 'text.secondary' }} />
                    <Typography variant="body2" sx={{ fontWeight: 600 }}>
                      Squad: {report.assignedTeamName}
                    </Typography>
                  </Box>
                )}

                <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, my: 1 }}>
                  <Typography variant="caption" sx={{ fontWeight: 700 }}>
                    DISPATCH PRIORITY:
                  </Typography>
                  <SeverityBadge severity={report.assignmentPriority || 'HIGH'} size="small" />
                </Box>

                {report.assignmentInstructions && (
                  <Typography variant="body2" color="text.secondary" sx={{ mt: 1.5, p: 1.5, bgcolor: '#ffffff', borderRadius: 1.5, border: '1px solid #e0f2fe' }}>
                    <strong>Dispatch Directives:</strong> {report.assignmentInstructions}
                  </Typography>
                )}
              </CardContent>
            </Card>
          )}

          {/* Audit & Response Timeline */}
          <Card>
            <CardContent sx={{ p: 3 }}>
              <Typography variant="h6" sx={{ fontWeight: 700, mb: 2 }}>
                Response Status Timeline
              </Typography>

              {report.updates && report.updates.length > 0 ? (
                <List disablePadding>
                  {report.updates.map((update, idx) => (
                    <ListItem
                      key={update.id}
                      sx={{
                        px: 0,
                        py: 1.5,
                        alignItems: 'flex-start',
                        borderLeft: '2px solid #0284c7',
                        pl: 2,
                        ml: 1,
                        position: 'relative',
                        mb: 1,
                        '&::before': {
                          content: '""',
                          position: 'absolute',
                          left: -5,
                          top: 14,
                          width: 8,
                          height: 8,
                          borderRadius: '50%',
                          bgcolor: '#0284c7',
                        },
                      }}
                    >
                      <ListItemText
                        primary={
                          <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 0.5 }}>
                            <StatusBadge status={update.newStatus} size="small" />
                            <Typography variant="caption" color="text.secondary">
                              {new Date(update.timestamp).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit', month: 'short', day: 'numeric' })}
                            </Typography>
                          </Box>
                        }
                        secondary={
                          <Box sx={{ mt: 0.5 }}>
                            <Typography variant="body2" sx={{ color: 'text.primary', fontWeight: 500 }}>
                              {update.remarks || 'Status updated.'}
                            </Typography>
                            {update.updatedByName && (
                              <Typography variant="caption" sx={{ color: 'secondary.main', fontWeight: 600, display: 'block', mt: 0.5 }}>
                                Handled by: {update.updatedByName}
                              </Typography>
                            )}
                          </Box>
                        }
                      />
                    </ListItem>
                  ))}
                </List>
              ) : (
                <Typography variant="body2" color="text.secondary">
                  No response timeline updates recorded yet.
                </Typography>
              )}
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    </Box>
  );
};
