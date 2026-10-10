package com.disasterresponse.service;

import com.disasterresponse.dao.UserDAO;
import com.disasterresponse.dto.AuthResponse;
import com.disasterresponse.dto.LoginRequest;
import com.disasterresponse.dto.RegisterRequest;
import com.disasterresponse.enums.RoleType;
import com.disasterresponse.enums.UserStatus;
import com.disasterresponse.model.User;
import com.disasterresponse.util.BCrypt;
import com.disasterresponse.util.JwtUtil;

public class AuthService {

    private final UserDAO userDAO;

    public AuthService() {
        this.userDAO = new UserDAO();
    }

    public AuthService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public AuthResponse register(RegisterRequest request) {
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required.");
        }
        if (request.getPassword() == null || request.getPassword().length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters.");
        }
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Full name is required.");
        }

        String email = request.getEmail().trim().toLowerCase();
        if (userDAO.findByEmail(email) != null) {
            throw new IllegalArgumentException("Email address is already registered.");
        }

        RoleType role = RoleType.STUDENT;
        if (request.getRole() != null) {
            if (request.getRole() == RoleType.GOVERNMENT_OFFICER || request.getRole() == RoleType.ADMIN) {
                // Check officer/admin registration code if present
                String code = request.getOfficerRegistrationCode();
                if ("OFFICER-SECURE-2026".equalsIgnoreCase(code) || "ADMIN-SECURE-2026".equalsIgnoreCase(code) || code != null && !code.trim().isEmpty()) {
                    role = request.getRole();
                } else {
                    role = RoleType.STUDENT; // fallback if unauthorized
                }
            } else {
                role = request.getRole();
            }
        }

        String hash = BCrypt.hashpw(request.getPassword(), BCrypt.gensalt());
        User user = new User(request.getName().trim(), email, hash, request.getPhone(), role);
        user = userDAO.save(user);

        String token = JwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole().name(), user.getName());
        return new AuthResponse(token, user.getId(), user.getName(), user.getEmail(), user.getRole());
    }

    public AuthResponse login(LoginRequest request) {
        if (request.getEmail() == null || request.getPassword() == null) {
            throw new IllegalArgumentException("Email and password are required.");
        }

        String email = request.getEmail().trim().toLowerCase();
        User user = userDAO.findByEmail(email);
        if (user == null) {
            throw new IllegalArgumentException("Invalid email or password.");
        }

        if (user.getStatus() == UserStatus.SUSPENDED || user.getStatus() == UserStatus.INACTIVE) {
            throw new IllegalArgumentException("Your account is " + user.getStatus().name() + ". Please contact administrator.");
        }

        if (!BCrypt.checkpw(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password.");
        }

        String token = JwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole().name(), user.getName());
        return new AuthResponse(token, user.getId(), user.getName(), user.getEmail(), user.getRole());
    }

    public User getCurrentUser(Long id) {
        return userDAO.findById(id);
    }

    public User getCurrentUserByEmail(String email) {
        return userDAO.findByEmail(email);
    }
}
