import React, { useState } from 'react';
import { Outlet, useNavigate, useLocation, Link } from 'react-router-dom';
import {
  Box,
  Drawer,
  AppBar,
  Toolbar,
  List,
  Typography,
  Divider,
  IconButton,
  ListItem,
  ListItemButton,
  ListItemIcon,
  ListItemText,
  Avatar,
  Menu,
  MenuItem,
  Chip,
  useTheme,
  useMediaQuery,
} from '@mui/material';
import MenuIcon from '@mui/icons-material/Menu';
import DashboardOutlinedIcon from '@mui/icons-material/DashboardOutlined';
import SchoolOutlinedIcon from '@mui/icons-material/SchoolOutlined';
import QuizOutlinedIcon from '@mui/icons-material/QuizOutlined';
import SportsEsportsOutlinedIcon from '@mui/icons-material/SportsEsportsOutlined';
import MapOutlinedIcon from '@mui/icons-material/MapOutlined';
import AddAlertOutlinedIcon from '@mui/icons-material/AddAlertOutlined';
import AssignmentOutlinedIcon from '@mui/icons-material/AssignmentOutlined';
import ContactPhoneOutlinedIcon from '@mui/icons-material/ContactPhoneOutlined';
import AdminPanelSettingsOutlinedIcon from '@mui/icons-material/AdminPanelSettingsOutlined';
import PeopleAltOutlinedIcon from '@mui/icons-material/PeopleAltOutlined';
import CorporateFareOutlinedIcon from '@mui/icons-material/CorporateFareOutlined';
import VerifiedUserOutlinedIcon from '@mui/icons-material/VerifiedUserOutlined';
import SecurityIcon from '@mui/icons-material/Security';
import LogoutOutlinedIcon from '@mui/icons-material/LogoutOutlined';
import PersonOutlineOutlinedIcon from '@mui/icons-material/PersonOutlineOutlined';

import { useAuth } from '../context/AuthContext';
import { NotificationDropdown } from '../components/common/NotificationDropdown';

const DRAWER_WIDTH = 260;

export const DashboardLayout: React.FC<{ children?: React.ReactNode }> = ({ children }) => {
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('md'));
  const [mobileOpen, setMobileOpen] = useState(false);
  const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null);

  const { user, role, logout } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const handleDrawerToggle = () => {
    setMobileOpen(!mobileOpen);
  };

  const handleProfileMenuOpen = (event: React.MouseEvent<HTMLElement>) => {
    setAnchorEl(event.currentTarget);
  };

  const handleProfileMenuClose = () => {
    setAnchorEl(null);
  };

  const getNavItems = () => {
    if (role === 'ADMIN') {
      return [
        { label: 'Admin Command', path: '/admin', icon: <AdminPanelSettingsOutlinedIcon /> },
        { label: 'Curriculum Manager', path: '/admin/content', icon: <SchoolOutlinedIcon /> },
        { label: 'User Directory', path: '/admin/users', icon: <PeopleAltOutlinedIcon /> },
        { label: 'Departments & Teams', path: '/admin/departments', icon: <CorporateFareOutlinedIcon /> },
        { label: 'Disaster Map', path: '/map', icon: <MapOutlinedIcon /> },
        { label: 'Emergency Contacts', path: '/emergency-contacts', icon: <ContactPhoneOutlinedIcon /> },
      ];
    } else if (role === 'GOVERNMENT_OFFICER') {
      return [
        { label: 'Officer Dashboard', path: '/officer', icon: <VerifiedUserOutlinedIcon /> },
        { label: 'All Incidents', path: '/officer/queue', icon: <AssignmentOutlinedIcon /> },
        { label: 'Disaster Map', path: '/map', icon: <MapOutlinedIcon /> },
        { label: 'Emergency Contacts', path: '/emergency-contacts', icon: <ContactPhoneOutlinedIcon /> },
      ];
    } else {
      // STUDENT / CITIZEN
      return [
        { label: 'Overview', path: '/student', icon: <DashboardOutlinedIcon /> },
        { label: 'Learning Modules', path: '/student/modules', icon: <SchoolOutlinedIcon /> },
        { label: 'Quizzes & Assessment', path: '/student/quizzes', icon: <QuizOutlinedIcon /> },
        { label: 'Simulations', path: '/student/simulations', icon: <SportsEsportsOutlinedIcon /> },
        { label: 'Disaster Map', path: '/map', icon: <MapOutlinedIcon /> },
        { label: 'Report Incident', path: '/report-disaster', icon: <AddAlertOutlinedIcon /> },
        { label: 'My Reports', path: '/student/reports', icon: <AssignmentOutlinedIcon /> },
        { label: 'Emergency Contacts', path: '/emergency-contacts', icon: <ContactPhoneOutlinedIcon /> },
      ];
    }
  };

  const navItems = getNavItems();

  const drawerContent = (
    <Box sx={{ display: 'flex', flexDirection: 'column', height: '100%', bgcolor: '#0f172a', color: '#fff' }}>
      {/* Brand Header */}
      <Box sx={{ p: 2.5, display: 'flex', alignItems: 'center', gap: 1.5 }}>
        <Avatar sx={{ bgcolor: '#0284c7', width: 38, height: 38 }}>
          <SecurityIcon fontSize="small" />
        </Avatar>
        <Box>
          <Typography variant="subtitle1" sx={{ fontWeight: 800, lineHeight: 1.2, color: '#f8fafc' }}>
            DISASTER PREP
          </Typography>
          <Typography variant="caption" sx={{ color: '#94a3b8', fontSize: '0.72rem', letterSpacing: '0.04em' }}>
            & RESPONSE SYSTEM
          </Typography>
        </Box>
      </Box>

      <Divider sx={{ borderColor: 'rgba(255,255,255,0.08)' }} />

      {/* Role Pill */}
      <Box sx={{ px: 2.5, py: 1.5 }}>
        <Chip
          label={role ? role.replace('_', ' ') : 'CITIZEN'}
          size="small"
          sx={{
            bgcolor: role === 'ADMIN' ? '#475569' : role === 'GOVERNMENT_OFFICER' ? '#0369a1' : '#1e293b',
            color: '#38bdf8',
            fontWeight: 700,
            fontSize: '0.7rem',
            width: '100%',
          }}
        />
      </Box>

      {/* Navigation Links */}
      <List sx={{ px: 1.5, flex: 1 }}>
        {navItems.map((item) => {
          const active = location.pathname === item.path || (item.path !== '/' && location.pathname.startsWith(item.path + '/'));
          return (
            <ListItem key={item.path} disablePadding sx={{ mb: 0.5 }}>
              <ListItemButton
                component={Link}
                to={item.path}
                onClick={() => isMobile && setMobileOpen(false)}
                sx={{
                  borderRadius: 2,
                  bgcolor: active ? '#0284c7' : 'transparent',
                  color: active ? '#ffffff' : '#94a3b8',
                  '&:hover': {
                    bgcolor: active ? '#0284c7' : 'rgba(255,255,255,0.06)',
                    color: '#ffffff',
                  },
                }}
              >
                <ListItemIcon sx={{ color: 'inherit', minWidth: 38 }}>{item.icon}</ListItemIcon>
                <ListItemText
                  primary={item.label}
                  primaryTypographyProps={{ fontSize: '0.875rem', fontWeight: active ? 700 : 500 }}
                />
              </ListItemButton>
            </ListItem>
          );
        })}
      </List>

      <Divider sx={{ borderColor: 'rgba(255,255,255,0.08)' }} />

      {/* Quick User Badge */}
      <Box sx={{ p: 2, display: 'flex', alignItems: 'center', gap: 1.5 }}>
        <Avatar sx={{ bgcolor: '#0ea5e9', width: 34, height: 34, fontSize: '0.85rem', fontWeight: 700 }}>
          {user?.name ? user.name.charAt(0).toUpperCase() : 'U'}
        </Avatar>
        <Box sx={{ overflow: 'hidden', flex: 1 }}>
          <Typography variant="body2" noWrap sx={{ fontWeight: 600, color: '#f8fafc' }}>
            {user?.name || 'Authorized User'}
          </Typography>
          <Typography variant="caption" noWrap sx={{ color: '#94a3b8', display: 'block' }}>
            {user?.email || ''}
          </Typography>
        </Box>
      </Box>
    </Box>
  );

  return (
    <Box sx={{ display: 'flex', minHeight: '100vh', bgcolor: '#f8fafc' }}>
      {/* App Bar */}
      <AppBar
        position="fixed"
        sx={{
          width: { md: `calc(100% - ${DRAWER_WIDTH}px)` },
          ml: { md: `${DRAWER_WIDTH}px` },
        }}
      >
        <Toolbar sx={{ justifyContent: 'space-between' }}>
          <Box sx={{ display: 'flex', alignItems: 'center' }}>
            <IconButton
              color="inherit"
              aria-label="open drawer"
              edge="start"
              onClick={handleDrawerToggle}
              sx={{ mr: 2, display: { md: 'none' } }}
            >
              <MenuIcon />
            </IconButton>
            <Typography variant="subtitle1" sx={{ fontWeight: 700, color: 'text.primary' }}>
              National Emergency & Education Coordination Network
            </Typography>
          </Box>

          <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
            <NotificationDropdown />

            <IconButton onClick={handleProfileMenuOpen} sx={{ p: 0.5 }}>
              <Avatar sx={{ bgcolor: '#0f172a', width: 36, height: 36, fontSize: '0.9rem' }}>
                {user?.name ? user.name.charAt(0).toUpperCase() : 'U'}
              </Avatar>
            </IconButton>

            <Menu
              anchorEl={anchorEl}
              open={Boolean(anchorEl)}
              onClose={handleProfileMenuClose}
              PaperProps={{ sx: { width: 200, mt: 1.5, borderRadius: 2 } }}
              transformOrigin={{ horizontal: 'right', vertical: 'top' }}
              anchorOrigin={{ horizontal: 'right', vertical: 'bottom' }}
            >
              <MenuItem
                onClick={() => {
                  handleProfileMenuClose();
                  navigate('/profile');
                }}
              >
                <ListItemIcon>
                  <PersonOutlineOutlinedIcon fontSize="small" />
                </ListItemIcon>
                Profile & Security
              </MenuItem>
              <Divider />
              <MenuItem
                onClick={() => {
                  handleProfileMenuClose();
                  logout();
                }}
                sx={{ color: 'error.main' }}
              >
                <ListItemIcon sx={{ color: 'error.main' }}>
                  <LogoutOutlinedIcon fontSize="small" />
                </ListItemIcon>
                Logout
              </MenuItem>
            </Menu>
          </Box>
        </Toolbar>
      </AppBar>

      {/* Drawer */}
      <Box component="nav" sx={{ width: { md: DRAWER_WIDTH }, flexShrink: { md: 0 } }}>
        {/* Mobile Drawer */}
        <Drawer
          variant="temporary"
          open={mobileOpen}
          onClose={handleDrawerToggle}
          ModalProps={{ keepMounted: true }}
          sx={{
            display: { xs: 'block', md: 'none' },
            '& .MuiDrawer-paper': { boxSizing: 'border-box', width: DRAWER_WIDTH },
          }}
        >
          {drawerContent}
        </Drawer>
        {/* Desktop Permanent Drawer */}
        <Drawer
          variant="permanent"
          sx={{
            display: { xs: 'none', md: 'block' },
            '& .MuiDrawer-paper': { boxSizing: 'border-box', width: DRAWER_WIDTH, borderRight: 'none' },
          }}
          open
        >
          {drawerContent}
        </Drawer>
      </Box>

      {/* Main Content Area */}
      <Box
        component="main"
        sx={{
          flexGrow: 1,
          p: { xs: 2, sm: 3, md: 4 },
          width: { md: `calc(100% - ${DRAWER_WIDTH}px)` },
          mt: '64px',
          minHeight: 'calc(100vh - 64px)',
        }}
      >
        {children || <Outlet />}
      </Box>
    </Box>
  );
};
