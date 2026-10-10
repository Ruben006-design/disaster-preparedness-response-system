import React, { useEffect, useState } from 'react';
import {
  Box,
  Typography,
  Card,
  CardContent,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Button,
  Chip,
  CircularProgress,
  Alert,
  IconButton,
  Tooltip,
  TextField,
  MenuItem,
  Stack
} from '@mui/material';
import {
  Visibility,
  Refresh,
  Clear,
  ArrowBack
} from '@mui/icons-material';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { StatusBadge, SeverityBadge } from '../../components/common/Badges';
import { EmptyState } from '../../components/common/EmptyState';
import { reportService } from '../../services/reportService';
import { DisasterReport, SeverityLevel, ReportStatus } from '../../types';

export const OfficerQueuePage: React.FC = () => {
  const navigate = useNavigate();
  const [searchParams, setSearchParams] = useSearchParams();

  const [reports, setReports] = useState<DisasterReport[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  // Filters
  const [statusFilter, setStatusFilter] = useState<string>(searchParams.get('status') || '');
  const [severityFilter, setSeverityFilter] = useState<string>('');
  const [searchQuery, setSearchQuery] = useState<string>('');

  const fetchReports = async () => {
    try {
      setLoading(true);
      setError(null);
      const res = await reportService.filterReports({
        status: statusFilter ? (statusFilter as ReportStatus) : undefined,
        severity: severityFilter ? (severityFilter as SeverityLevel) : undefined,
        search: searchQuery || undefined,
        page: 0,
        size: 50
      });
      setReports(res.content || []);
    } catch (err: any) {
      setError(err?.response?.data?.message || 'Failed to load disaster reports');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchReports();
  }, [statusFilter, severityFilter]);

  const handleClearFilters = () => {
    setStatusFilter('');
    setSeverityFilter('');
    setSearchQuery('');
    setSearchParams({});
  };

  const filteredReports = reports.filter((r) => {
    if (!searchQuery) return true;
    const q = searchQuery.toLowerCase();
    return (
      (r.reportCode && r.reportCode.toLowerCase().includes(q)) ||
      (r.title && r.title.toLowerCase().includes(q)) ||
      (r.description && r.description.toLowerCase().includes(q)) ||
      (r.address && r.address.toLowerCase().includes(q)) ||
      (r.reporterName && r.reporterName.toLowerCase().includes(q))
    );
  });

  return (
    <Box sx={{ display: 'flex', flexDirection: 'column', gap: 3 }}>
      {/* Header */}
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: 2 }}>
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1.5 }}>
          <IconButton onClick={() => navigate('/officer')}>
            <ArrowBack />
          </IconButton>
          <Box>
            <Typography variant="h4" fontWeight={700}>
              Incident Triage Queue
            </Typography>
            <Typography variant="body2" color="text.secondary">
              Comprehensive list of all community-submitted and officer-managed emergency records
            </Typography>
          </Box>
        </Box>
        <Box sx={{ display: 'flex', gap: 1.5 }}>
          <Button variant="outlined" startIcon={<Refresh />} onClick={fetchReports}>
            Refresh
          </Button>
        </Box>
      </Box>

      {error && <Alert severity="error">{error}</Alert>}

      {/* Filter Toolbar */}
      <Card>
        <CardContent sx={{ py: 2 }}>
          <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2} alignItems="center">
            <TextField
              size="small"
              placeholder="Search by Report ID, title, address, reporter..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              sx={{ flexGrow: 1, minWidth: 260 }}
            />

            <TextField
              select
              size="small"
              label="Status"
              value={statusFilter}
              onChange={(e) => {
                setStatusFilter(e.target.value);
                if (e.target.value) {
                  setSearchParams({ status: e.target.value });
                } else {
                  setSearchParams({});
                }
              }}
              sx={{ minWidth: 160 }}
            >
              <MenuItem value="">All Statuses</MenuItem>
              <MenuItem value="SUBMITTED">SUBMITTED</MenuItem>
              <MenuItem value="UNDER_REVIEW">UNDER REVIEW</MenuItem>
              <MenuItem value="VERIFIED">VERIFIED</MenuItem>
              <MenuItem value="ASSIGNED">ASSIGNED</MenuItem>
              <MenuItem value="RESPONSE_IN_PROGRESS">IN PROGRESS</MenuItem>
              <MenuItem value="SITUATION_CONTROLLED">CONTROLLED</MenuItem>
              <MenuItem value="RESOLVED">RESOLVED</MenuItem>
              <MenuItem value="REJECTED">REJECTED</MenuItem>
            </TextField>

            <TextField
              select
              size="small"
              label="Severity"
              value={severityFilter}
              onChange={(e) => setSeverityFilter(e.target.value)}
              sx={{ minWidth: 150 }}
            >
              <MenuItem value="">All Severities</MenuItem>
              <MenuItem value="LOW">LOW</MenuItem>
              <MenuItem value="MEDIUM">MEDIUM</MenuItem>
              <MenuItem value="HIGH">HIGH</MenuItem>
              <MenuItem value="CRITICAL">CRITICAL</MenuItem>
            </TextField>

            {(statusFilter || severityFilter || searchQuery) && (
              <Button
                variant="text"
                color="secondary"
                startIcon={<Clear />}
                onClick={handleClearFilters}
              >
                Reset
              </Button>
            )}
          </Stack>
        </CardContent>
      </Card>

      {/* Table */}
      <Card>
        <CardContent sx={{ p: 0 }}>
          {loading ? (
            <Box sx={{ display: 'flex', justifyContent: 'center', py: 8 }}>
              <CircularProgress />
            </Box>
          ) : filteredReports.length === 0 ? (
            <Box sx={{ py: 6 }}>
              <EmptyState
                title="No Incidents Match Filters"
                description="Try clearing your search query or selecting a different status/severity filter."
              />
            </Box>
          ) : (
            <TableContainer component={Paper} elevation={0}>
              <Table sx={{ minWidth: 800 }}>
                <TableHead>
                  <TableRow sx={{ bgcolor: 'action.hover' }}>
                    <TableCell sx={{ fontWeight: 700 }}>Report ID</TableCell>
                    <TableCell sx={{ fontWeight: 700 }}>Category</TableCell>
                    <TableCell sx={{ fontWeight: 700 }}>Title & Reporter</TableCell>
                    <TableCell sx={{ fontWeight: 700 }}>Location</TableCell>
                    <TableCell sx={{ fontWeight: 700 }}>Severity</TableCell>
                    <TableCell sx={{ fontWeight: 700 }}>Status</TableCell>
                    <TableCell sx={{ fontWeight: 700 }}>Assigned Agency</TableCell>
                    <TableCell sx={{ fontWeight: 700 }}>Submitted</TableCell>
                    <TableCell align="right" sx={{ fontWeight: 700 }}>Review</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {filteredReports.map((report) => (
                    <TableRow
                      key={report.id}
                      hover
                      sx={{ cursor: 'pointer' }}
                      onClick={() => navigate(`/officer/review/${report.id}`)}
                    >
                      <TableCell sx={{ fontFamily: 'monospace', fontWeight: 700, color: 'primary.main' }}>
                        {report.reportCode}
                      </TableCell>
                      <TableCell>
                        <Chip label={report.disasterTypeName} size="small" variant="outlined" />
                      </TableCell>
                      <TableCell sx={{ maxWidth: 220, overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>
                        <Typography variant="body2" fontWeight={600} noWrap>
                          {report.title}
                        </Typography>
                        <Typography variant="caption" color="text.secondary">
                          {report.reporterName || 'Citizen'}
                        </Typography>
                      </TableCell>
                      <TableCell sx={{ maxWidth: 180, overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>
                        {report.address || `${report.latitude.toFixed(4)}, ${report.longitude.toFixed(4)}`}
                      </TableCell>
                      <TableCell>
                        <SeverityBadge severity={report.severity} />
                      </TableCell>
                      <TableCell>
                        <StatusBadge status={report.status} />
                      </TableCell>
                      <TableCell>
                        {report.assignedDepartmentName ? (
                          <Chip
                            label={report.assignedDepartmentName}
                            size="small"
                            color="info"
                            variant="filled"
                          />
                        ) : (
                          <Typography variant="caption" color="text.secondary">
                            Unassigned
                          </Typography>
                        )}
                      </TableCell>
                      <TableCell sx={{ whiteSpace: 'nowrap', fontSize: '0.8125rem' }}>
                        {new Date(report.createdAt).toLocaleDateString()}
                      </TableCell>
                      <TableCell align="right">
                        <Tooltip title="Examine and verify report">
                          <IconButton
                            color="primary"
                            size="small"
                            onClick={(e) => {
                              e.stopPropagation();
                              navigate(`/officer/review/${report.id}`);
                            }}
                          >
                            <Visibility fontSize="small" />
                          </IconButton>
                        </Tooltip>
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </TableContainer>
          )}
        </CardContent>
      </Card>
    </Box>
  );
};
export default OfficerQueuePage;
