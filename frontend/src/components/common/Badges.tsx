import React from 'react';
import { Chip } from '@mui/material';
import { ReportStatus, SeverityLevel } from '../../types';

interface StatusBadgeProps {
  status: ReportStatus;
  size?: 'small' | 'medium';
}

export const StatusBadge: React.FC<StatusBadgeProps> = ({ status, size = 'small' }) => {
  let color: 'default' | 'primary' | 'secondary' | 'error' | 'info' | 'success' | 'warning' = 'default';
  let label = status.replace(/_/g, ' ');

  switch (status) {
    case 'SUBMITTED':
      color = 'default';
      break;
    case 'UNDER_REVIEW':
      color = 'info';
      break;
    case 'VERIFIED':
      color = 'secondary';
      break;
    case 'ASSIGNED':
      color = 'primary';
      break;
    case 'RESPONSE_IN_PROGRESS':
      color = 'warning';
      break;
    case 'SITUATION_CONTROLLED':
      color = 'info';
      break;
    case 'RESOLVED':
      color = 'success';
      break;
    case 'REJECTED':
      color = 'error';
      break;
  }

  return (
    <Chip
      size={size}
      label={label}
      color={color}
      sx={{
        fontWeight: 600,
        letterSpacing: '0.02em',
        fontSize: size === 'small' ? '0.75rem' : '0.85rem',
      }}
    />
  );
};

interface SeverityBadgeProps {
  severity: SeverityLevel;
  size?: 'small' | 'medium';
}

export const SeverityBadge: React.FC<SeverityBadgeProps> = ({ severity, size = 'small' }) => {
  let color: 'default' | 'info' | 'warning' | 'error' = 'default';

  switch (severity) {
    case 'LOW':
      color = 'info';
      break;
    case 'MEDIUM':
      color = 'default';
      break;
    case 'HIGH':
      color = 'warning';
      break;
    case 'CRITICAL':
      color = 'error';
      break;
  }

  return (
    <Chip
      size={size}
      label={severity}
      color={color}
      variant={severity === 'CRITICAL' ? 'filled' : 'outlined'}
      sx={{
        fontWeight: 700,
        letterSpacing: '0.04em',
        fontSize: size === 'small' ? '0.7rem' : '0.8rem',
      }}
    />
  );
};
