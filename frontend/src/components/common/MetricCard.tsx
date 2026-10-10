import React from 'react';
import { Card, CardContent, Typography, Box, Avatar } from '@mui/material';

interface MetricCardProps {
  title: string;
  value: string | number;
  icon: React.ReactNode;
  color?: string;
  subtext?: string;
  subtitle?: string;
}

export const MetricCard: React.FC<MetricCardProps> = ({
  title,
  value,
  icon,
  color = '#0284c7',
  subtext,
  subtitle,
}) => {
  const displaySubtitle = subtext || subtitle;
  return (
    <Card sx={{ height: '100%', position: 'relative', overflow: 'hidden' }}>
      <CardContent sx={{ p: 3 }}>
        <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', mb: 2 }}>
          <Typography variant="body2" sx={{ fontWeight: 600, color: 'text.secondary', textTransform: 'uppercase', letterSpacing: '0.05em' }}>
            {title}
          </Typography>
          <Avatar
            sx={{
              bgcolor: `${color}18`,
              color: color,
              width: 44,
              height: 44,
            }}
          >
            {icon}
          </Avatar>
        </Box>
        <Typography variant="h4" sx={{ fontWeight: 800, color: 'text.primary', mb: 0.5 }}>
          {value}
        </Typography>
        {displaySubtitle && (
          <Typography variant="caption" sx={{ color: 'text.secondary', display: 'block' }}>
            {displaySubtitle}
          </Typography>
        )}
      </CardContent>
    </Card>
  );
};
