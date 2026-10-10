import React, { useEffect, useState } from 'react';
import {
  Box,
  Typography,
  Card,
  CardContent,
  Grid,
  Paper,
  Button,
  Chip,
  CircularProgress,
  Alert,
  Stack
} from '@mui/material';
import {
  Phone,
  Refresh,
  ContactPhone,
  LocalHospital,
  LocalFireDepartment,
  LocalPolice
} from '@mui/icons-material';
import { emergencyContactService } from '../../services/adminService';
import { EmergencyContact } from '../../types';

export const EmergencyContactsPage: React.FC = () => {
  const [contacts, setContacts] = useState<EmergencyContact[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  const fetchContacts = async () => {
    try {
      setLoading(true);
      setError(null);
      const data = await emergencyContactService.getContacts();
      setContacts(data || []);
    } catch (err: any) {
      setError(err?.response?.data?.message || 'Failed to fetch emergency directory');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchContacts();
  }, []);

  return (
    <Box sx={{ display: 'flex', flexDirection: 'column', gap: 3 }}>
      {/* Header */}
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: 2 }}>
        <Box>
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 1.5 }}>
            <ContactPhone color="error" sx={{ fontSize: 36 }} />
            <Typography variant="h4" fontWeight={700}>
              National & Regional Emergency Directory
            </Typography>
          </Box>
          <Typography variant="body2" color="text.secondary">
            Verified 24/7 disaster response hotlines, municipal rescue helplines, and rapid dispatch contacts
          </Typography>
        </Box>
        <Button variant="outlined" startIcon={<Refresh />} onClick={fetchContacts}>
          Refresh Contacts
        </Button>
      </Box>

      {/* Immediate SOS Alert Card */}
      <Card sx={{ bgcolor: 'rgba(239, 68, 68, 0.08)', border: '1px solid rgba(239, 68, 68, 0.3)' }}>
        <CardContent sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: 2 }}>
          <Box>
            <Typography variant="h6" fontWeight={700} color="error.main">
              In Extreme Life-Threatening Peril?
            </Typography>
            <Typography variant="body2" color="text.secondary">
              Dial national primary emergency numbers immediately before completing online reports.
            </Typography>
          </Box>
          <Stack direction="row" spacing={2}>
            <Button
              variant="contained"
              color="error"
              size="large"
              startIcon={<Phone />}
              href="tel:112"
            >
              Dial 112 (National Emergency)
            </Button>
            <Button
              variant="outlined"
              color="error"
              size="large"
              startIcon={<LocalFireDepartment />}
              href="tel:101"
            >
              Dial 101 (Fire Control)
            </Button>
          </Stack>
        </CardContent>
      </Card>

      {error && <Alert severity="error">{error}</Alert>}

      {/* Contact Cards Grid */}
      {loading ? (
        <Box sx={{ display: 'flex', justifyContent: 'center', py: 8 }}>
          <CircularProgress />
        </Box>
      ) : (
        <Grid container spacing={2.5}>
          {contacts.map((contact) => (
            <Grid item xs={12} sm={6} md={4} key={contact.id}>
              <Card sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
                <CardContent sx={{ display: 'flex', flexDirection: 'column', gap: 1.5, flexGrow: 1 }}>
                  <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 1.5 }}>
                      <ContactPhone color="secondary" />
                      <Typography variant="h6" fontWeight={700}>
                        {contact.serviceName}
                      </Typography>
                    </Box>
                    <Chip
                      label={contact.organization}
                      size="small"
                      color="primary"
                      variant="outlined"
                    />
                  </Box>

                  <Paper
                    variant="outlined"
                    sx={{
                      p: 1.5,
                      bgcolor: 'background.default',
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'space-between'
                    }}
                  >
                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                      <Phone color="error" />
                      <Typography variant="h6" fontWeight={800} color="error.main">
                        {contact.phoneNumber}
                      </Typography>
                    </Box>
                    <Button
                      variant="contained"
                      size="small"
                      color="error"
                      href={`tel:${contact.phoneNumber}`}
                    >
                      Call
                    </Button>
                  </Paper>

                  <Box sx={{ mt: 'auto', display: 'flex', flexDirection: 'column', gap: 0.5 }}>
                    {contact.alternatePhone && (
                      <Typography variant="caption" color="text.secondary">
                        Alt Number: <strong>{contact.alternatePhone}</strong>
                      </Typography>
                    )}
                    <Typography variant="caption" color="text.secondary">
                      Region / Area: <strong>{contact.location || 'Nationwide Coverage'}</strong>
                    </Typography>
                  </Box>
                </CardContent>
              </Card>
            </Grid>
          ))}
        </Grid>
      )}
    </Box>
  );
};
export default EmergencyContactsPage;
