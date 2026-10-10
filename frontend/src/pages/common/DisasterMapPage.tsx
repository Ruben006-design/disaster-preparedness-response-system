import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import {
  Box,
  Typography,
  Card,
  CardContent,
  Grid,
  MenuItem,
  TextField,
  Chip,
  Button,
  Paper,
  Divider,
} from '@mui/material';
import { MapContainer, TileLayer, Marker, Popup } from 'react-leaflet';
import L from 'leaflet';
import PlaceIcon from '@mui/icons-material/Place';
import VerifiedIcon from '@mui/icons-material/Verified';
import ArrowForwardIcon from '@mui/icons-material/ArrowForward';
import { reportService } from '../../services/reportService';
import { educationService } from '../../services/educationService';
import { DisasterReport, DisasterType, SeverityLevel } from '../../types';
import { LoadingScreen } from '../../components/common/LoadingScreen';
import { StatusBadge, SeverityBadge } from '../../components/common/Badges';

// Fix Leaflet Default Marker icon paths for bundlers
const createCustomMarker = (severity: SeverityLevel) => {
  let color = '#3b82f6';
  if (severity === 'CRITICAL') color = '#ef4444';
  else if (severity === 'HIGH') color = '#f59e0b';
  else if (severity === 'LOW') color = '#10b981';

  return L.divIcon({
    className: 'custom-map-marker',
    html: `<div style="
      background-color: ${color};
      width: 24px;
      height: 24px;
      border-radius: 50%;
      border: 3px solid #ffffff;
      box-shadow: 0 2px 8px rgba(0,0,0,0.35);
    "></div>`,
    iconSize: [24, 24],
    iconAnchor: [12, 12],
    popupAnchor: [0, -12],
  });
};

export const DisasterMapPage: React.FC = () => {
  const [incidents, setIncidents] = useState<DisasterReport[]>([]);
  const [disasterTypes, setDisasterTypes] = useState<DisasterType[]>([]);
  const [selectedType, setSelectedType] = useState<string>('');
  const [selectedSeverity, setSelectedSeverity] = useState<string>('');
  const [loading, setLoading] = useState(true);

  const fetchIncidents = async () => {
    try {
      const typeId = selectedType ? Number(selectedType) : undefined;
      const sev = selectedSeverity ? (selectedSeverity as SeverityLevel) : undefined;
      const data = await reportService.getMapIncidents(typeId, sev);
      setIncidents(data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    const init = async () => {
      try {
        const types = await educationService.getAllDisasters();
        setDisasterTypes(types);
      } catch (err) {
        console.error(err);
      }
    };
    init();
  }, []);

  useEffect(() => {
    fetchIncidents();
  }, [selectedType, selectedSeverity]);

  // Center coordinate: average of incidents or fallback
  const centerLat = incidents.length > 0 ? incidents[0].latitude : 37.7749;
  const centerLng = incidents.length > 0 ? incidents[0].longitude : -122.4194;

  if (loading) return <LoadingScreen message="Loading interactive spatial disaster incident map..." />;

  return (
    <Box>
      <Box sx={{ mb: 3 }}>
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 0.5 }}>
          <VerifiedIcon color="secondary" />
          <Typography variant="overline" sx={{ color: 'secondary.main', fontWeight: 800 }}>
            LIVE SPATIAL MONITORING
          </Typography>
        </Box>
        <Typography variant="h4" sx={{ fontWeight: 800 }}>
          Interactive Verified Disaster Map
        </Typography>
        <Typography variant="body1" color="text.secondary">
          Displaying verified disaster situations and emergency response operations. Citizen reports are vetted by Government Officers prior to map publication.
        </Typography>
      </Box>

      {/* Filter Toolbar */}
      <Paper sx={{ p: 2.5, mb: 3, borderRadius: 2, border: '1px solid #e2e8f0' }}>
        <Grid container spacing={2} alignItems="center">
          <Grid item xs={12} sm={4}>
            <TextField
              select
              size="small"
              label="Filter by Disaster Type"
              fullWidth
              value={selectedType}
              onChange={(e) => setSelectedType(e.target.value)}
            >
              <MenuItem value="">All Disaster Categories</MenuItem>
              {disasterTypes.map((t) => (
                <MenuItem key={t.id} value={t.id}>
                  {t.name}
                </MenuItem>
              ))}
            </TextField>
          </Grid>

          <Grid item xs={12} sm={4}>
            <TextField
              select
              size="small"
              label="Filter by Severity"
              fullWidth
              value={selectedSeverity}
              onChange={(e) => setSelectedSeverity(e.target.value)}
            >
              <MenuItem value="">All Severities</MenuItem>
              <MenuItem value="LOW">Low</MenuItem>
              <MenuItem value="MEDIUM">Medium</MenuItem>
              <MenuItem value="HIGH">High</MenuItem>
              <MenuItem value="CRITICAL">Critical</MenuItem>
            </TextField>
          </Grid>

          <Grid item xs={12} sm={4}>
            <Box sx={{ display: 'flex', alignItems: 'center', gap: 1.5, justifyContent: { xs: 'flex-start', sm: 'flex-end' } }}>
              <Typography variant="body2" sx={{ fontWeight: 700 }}>
                {incidents.length} Verified Incidents
              </Typography>
              <Chip label="LIVE" size="small" color="error" sx={{ fontWeight: 700, fontSize: '0.7rem' }} />
            </Box>
          </Grid>
        </Grid>
      </Paper>

      {/* Map Container */}
      <Card sx={{ borderRadius: 3, overflow: 'hidden', mb: 3 }}>
        <Box sx={{ height: '580px', width: '100%', position: 'relative' }}>
          <MapContainer
            center={[centerLat, centerLng]}
            zoom={12}
            scrollWheelZoom={true}
            style={{ height: '100%', width: '100%' }}
          >
            <TileLayer
              attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
              url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
            />

            {incidents.map((inc) => (
              <Marker
                key={inc.id}
                position={[inc.latitude, inc.longitude]}
                icon={createCustomMarker(inc.severity)}
              >
                <Popup>
                  <Box sx={{ p: 1, minWidth: 220 }}>
                    <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 1 }}>
                      <SeverityBadge severity={inc.severity} size="small" />
                      <Chip label={inc.reportCode} size="small" sx={{ fontWeight: 700, fontSize: '0.7rem' }} />
                    </Box>

                    <Typography variant="subtitle2" sx={{ fontWeight: 800, mb: 0.5 }}>
                      {inc.title}
                    </Typography>

                    <Typography variant="caption" color="text.secondary" sx={{ display: 'block', mb: 1 }}>
                      {inc.disasterTypeName} • {inc.address}
                    </Typography>

                    <Typography variant="body2" sx={{ fontSize: '0.8rem', mb: 1.5 }}>
                      {inc.description}
                    </Typography>

                    <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', pt: 1, borderTop: '1px solid #e2e8f0' }}>
                      <StatusBadge status={inc.status} />
                      <Button
                        component={Link}
                        to={`/reports/${inc.id}`}
                        size="small"
                        endIcon={<ArrowForwardIcon />}
                        sx={{ fontSize: '0.75rem', fontWeight: 700 }}
                      >
                        Details
                      </Button>
                    </Box>
                  </Box>
                </Popup>
              </Marker>
            ))}
          </MapContainer>
        </Box>
      </Card>

      {/* Severity Color Legend */}
      <Paper sx={{ p: 2, borderRadius: 2, display: 'flex', flexWrap: 'wrap', gap: 3, alignItems: 'center', justifyContent: 'center' }}>
        <Typography variant="caption" sx={{ fontWeight: 700, color: 'text.secondary' }}>
          MAP SEVERITY INDICATORS:
        </Typography>
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
          <Box sx={{ width: 14, height: 14, borderRadius: '50%', bgcolor: '#ef4444' }} />
          <Typography variant="caption" sx={{ fontWeight: 600 }}>Critical</Typography>
        </Box>
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
          <Box sx={{ width: 14, height: 14, borderRadius: '50%', bgcolor: '#f59e0b' }} />
          <Typography variant="caption" sx={{ fontWeight: 600 }}>High</Typography>
        </Box>
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
          <Box sx={{ width: 14, height: 14, borderRadius: '50%', bgcolor: '#3b82f6' }} />
          <Typography variant="caption" sx={{ fontWeight: 600 }}>Medium</Typography>
        </Box>
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
          <Box sx={{ width: 14, height: 14, borderRadius: '50%', bgcolor: '#10b981' }} />
          <Typography variant="caption" sx={{ fontWeight: 600 }}>Low</Typography>
        </Box>
      </Paper>
    </Box>
  );
};
