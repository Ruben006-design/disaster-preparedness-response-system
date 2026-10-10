import React from 'react';
import { Box, CircularProgress, Typography } from '@mui/material';

export const LoadingScreen: React.FC<{ message?: string }> = ({ message = 'Loading emergency coordination platform...' }) => {
  return (
    <Box
      sx={{
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        justifyContent: 'center',
        minHeight: '50vh',
        gap: 2,
      }}
    >
      <CircularProgress size={40} thickness={4} color="secondary" />
      <Typography variant="body2" sx={{ color: 'text.secondary', fontWeight: 500 }}>
        {message}
      </Typography>
    </Box>
  );
};
