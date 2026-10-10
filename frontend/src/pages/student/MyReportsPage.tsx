import React, { useState, useEffect } from 'react';
import { useNavigate, Link } from 'react-router-dom';
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
  Button,
  Chip,
  TablePagination,
  Alert,
} from '@mui/material';
import AddAlertIcon from '@mui/icons-material/AddAlert';
import VisibilityIcon from '@mui/icons-material/Visibility';
import { reportService } from '../../services/reportService';
import { DisasterReport } from '../../types';
import { LoadingScreen } from '../../components/common/LoadingScreen';
import { EmptyState } from '../../components/common/EmptyState';
import { StatusBadge, SeverityBadge } from '../../components/common/Badges';

export const MyReportsPage: React.FC = () => {
  const [reports, setReports] = useState<DisasterReport[]>([]);
  const [totalElements, setTotalElements] = useState(0);
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(10);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const navigate = useNavigate();

  const fetchReports = async () => {
    setLoading(true);
    try {
      const res = await reportService.getMyReports(page, rowsPerPage);
      setReports(res.content);
      setTotalElements(res.totalElements);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to load your disaster reports.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchReports();
  }, [page, rowsPerPage]);

  return (
    <Box>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3, flexWrap: 'wrap', gap: 2 }}>
        <Box>
          <Typography variant="overline" sx={{ color: 'secondary.main', fontWeight: 800 }}>
            FIELD DISPATCH LOG
          </Typography>
          <Typography variant="h4" sx={{ fontWeight: 800 }}>
            My Submitted Disaster Reports
          </Typography>
          <Typography variant="body2" color="text.secondary">
            Track government verification progress and emergency department assignments for your reported incidents.
          </Typography>
        </Box>
        <Button
          component={Link}
          to="/report-disaster"
          variant="contained"
          color="error"
          startIcon={<AddAlertIcon />}
          sx={{ fontWeight: 700 }}
        >
          Submit New Report
        </Button>
      </Box>

      {error && <Alert severity="error" sx={{ mb: 3 }}>{error}</Alert>}

      {loading ? (
        <LoadingScreen message="Loading submitted reports..." />
      ) : reports.length === 0 ? (
        <EmptyState
          title="No disaster reports submitted yet"
          description="If you witness an active local hazard, flash flood, seismic damage, or fire, submit a report to alert responders."
          actionText="Submit Disaster Report"
          onAction={() => navigate('/report-disaster')}
        />
      ) : (
        <Card sx={{ borderRadius: 3 }}>
          <TableContainer>
            <Table>
              <TableHead sx={{ bgcolor: '#f8fafc' }}>
                <TableRow>
                  <TableCell sx={{ fontWeight: 700 }}>REPORT ID</TableCell>
                  <TableCell sx={{ fontWeight: 700 }}>DISASTER TYPE</TableCell>
                  <TableCell sx={{ fontWeight: 700 }}>HEADLINE / ADDRESS</TableCell>
                  <TableCell sx={{ fontWeight: 700 }}>SEVERITY</TableCell>
                  <TableCell sx={{ fontWeight: 700 }}>DATE</TableCell>
                  <TableCell sx={{ fontWeight: 700 }}>STATUS</TableCell>
                  <TableCell align="right" sx={{ fontWeight: 700 }}>ACTIONS</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {reports.map((row) => (
                  <TableRow key={row.id} hover>
                    <TableCell>
                      <Chip label={row.reportCode} size="small" sx={{ fontWeight: 800 }} />
                    </TableCell>
                    <TableCell sx={{ fontWeight: 600 }}>{row.disasterTypeName}</TableCell>
                    <TableCell>
                      <Typography variant="body2" sx={{ fontWeight: 700 }}>
                        {row.title}
                      </Typography>
                      <Typography variant="caption" color="text.secondary">
                        {row.address}
                      </Typography>
                    </TableCell>
                    <TableCell>
                      <SeverityBadge severity={row.severity} size="small" />
                    </TableCell>
                    <TableCell sx={{ color: 'text.secondary', fontSize: '0.85rem' }}>
                      {new Date(row.createdAt).toLocaleDateString()}
                    </TableCell>
                    <TableCell>
                      <StatusBadge status={row.status} />
                    </TableCell>
                    <TableCell align="right">
                      <Button
                        component={Link}
                        to={`/reports/${row.id}`}
                        variant="outlined"
                        size="small"
                        startIcon={<VisibilityIcon />}
                        sx={{ fontWeight: 600 }}
                      >
                        Track
                      </Button>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>
          <TablePagination
            rowsPerPageOptions={[5, 10, 25]}
            component="div"
            count={totalElements}
            rowsPerPage={rowsPerPage}
            page={page}
            onPageChange={(_, newPage) => setPage(newPage)}
            onRowsPerPageChange={(e) => {
              setRowsPerPage(parseInt(e.target.value, 10));
              setPage(0);
            }}
          />
        </Card>
      )}
    </Box>
  );
};
