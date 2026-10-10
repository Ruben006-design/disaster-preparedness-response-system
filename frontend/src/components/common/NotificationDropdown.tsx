import React, { useState, useEffect } from 'react';
import {
  IconButton,
  Badge,
  Menu,
  MenuItem,
  Typography,
  Box,
  Divider,
  Button,
  ListItemIcon,
} from '@mui/material';
import NotificationsNoneIcon from '@mui/icons-material/NotificationsNone';
import CheckCircleOutlineIcon from '@mui/icons-material/CheckCircleOutline';
import WarningAmberIcon from '@mui/icons-material/WarningAmber';
import ErrorOutlineIcon from '@mui/icons-material/ErrorOutline';
import AssignmentTurnedInIcon from '@mui/icons-material/AssignmentTurnedIn';
import { notificationService } from '../../services/adminService';
import { NotificationItem } from '../../types';

export const NotificationDropdown: React.FC = () => {
  const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null);
  const [notifications, setNotifications] = useState<NotificationItem[]>([]);
  const [unreadCount, setUnreadCount] = useState<number>(0);

  const fetchNotifs = async () => {
    try {
      const count = await notificationService.getUnreadCount();
      const num = typeof count === 'number' ? count : (Number((count as any)?.unreadCount) || 0);
      setUnreadCount(num);
      const items = await notificationService.getRecent();
      setNotifications(Array.isArray(items) ? items : []);
    } catch {
      // Ignored if not logged in
    }
  };

  useEffect(() => {
    fetchNotifs();
    const interval = setInterval(fetchNotifs, 25000);
    return () => clearInterval(interval);
  }, []);

  const handleClick = (event: React.MouseEvent<HTMLElement>) => {
    setAnchorEl(event.currentTarget);
  };

  const handleClose = () => {
    setAnchorEl(null);
  };

  const handleMarkAllRead = async () => {
    try {
      await notificationService.markAllAsRead();
      setUnreadCount(0);
      setNotifications(notifications.map((n) => ({ ...n, read: true })));
    } catch (err) {
      console.error(err);
    }
  };

  const handleItemClick = async (notif: NotificationItem) => {
    try {
      if (!notif.read) {
        await notificationService.markAsRead(notif.id);
        setUnreadCount((prev) => Math.max(0, prev - 1));
        setNotifications(notifications.map((n) => (n.id === notif.id ? { ...n, read: true } : n)));
      }
    } catch (err) {
      console.error(err);
    }
  };

  const getNotifIcon = (type: string) => {
    switch (type) {
      case 'REPORT_VERIFIED':
        return <CheckCircleOutlineIcon color="secondary" fontSize="small" />;
      case 'REPORT_REJECTED':
        return <ErrorOutlineIcon color="error" fontSize="small" />;
      case 'DEPARTMENT_ASSIGNED':
        return <AssignmentTurnedInIcon color="primary" fontSize="small" />;
      default:
        return <WarningAmberIcon color="warning" fontSize="small" />;
    }
  };

  return (
    <>
      <IconButton color="inherit" onClick={handleClick} sx={{ ml: 1 }}>
        <Badge badgeContent={unreadCount} color="error">
          <NotificationsNoneIcon />
        </Badge>
      </IconButton>

      <Menu
        anchorEl={anchorEl}
        open={Boolean(anchorEl)}
        onClose={handleClose}
        PaperProps={{
          sx: { width: 360, maxHeight: 450, mt: 1.5, borderRadius: 2 },
        }}
        transformOrigin={{ horizontal: 'right', vertical: 'top' }}
        anchorOrigin={{ horizontal: 'right', vertical: 'bottom' }}
      >
        <Box sx={{ p: 2, display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
          <Typography variant="subtitle1" sx={{ fontWeight: 700 }}>
            Alerts & Notifications
          </Typography>
          {unreadCount > 0 && (
            <Button size="small" onClick={handleMarkAllRead}>
              Mark all read
            </Button>
          )}
        </Box>
        <Divider />

        {notifications.length === 0 ? (
          <MenuItem disabled sx={{ py: 3, justifyContent: 'center' }}>
            <Typography variant="body2" color="text.secondary">
              No recent notifications
            </Typography>
          </MenuItem>
        ) : (
          notifications.map((notif) => (
            <MenuItem
              key={notif.id}
              onClick={() => handleItemClick(notif)}
              sx={{
                py: 1.5,
                px: 2,
                bgcolor: notif.read ? 'inherit' : 'rgba(2, 132, 199, 0.05)',
                whiteSpace: 'normal',
                alignItems: 'flex-start',
              }}
            >
              <ListItemIcon sx={{ minWidth: 32, mt: 0.5 }}>{getNotifIcon(notif.type)}</ListItemIcon>
              <Box sx={{ flex: 1 }}>
                <Typography variant="body2" sx={{ fontWeight: notif.read ? 500 : 700 }}>
                  {notif.title}
                </Typography>
                <Typography variant="caption" color="text.secondary" sx={{ display: 'block', mt: 0.5 }}>
                  {notif.message}
                </Typography>
                <Typography variant="caption" sx={{ color: 'text.disabled', fontSize: '0.7rem' }}>
                  {new Date(notif.createdAt).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                </Typography>
              </Box>
            </MenuItem>
          ))
        )}
      </Menu>
    </>
  );
};
